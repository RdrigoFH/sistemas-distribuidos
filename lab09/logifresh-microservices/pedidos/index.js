const express = require('express');
const cors = require('cors');
const axios = require('axios');
const app = express();
const PORT = 3000;

app.use(cors());
app.use(express.json());

// URLs de los servicios (inyectadas por Docker Compose)
const INVENTARIO_URL = process.env.INVENTARIO_URL || 'http://localhost:3001';
const FACTURACION_URL = process.env.FACTURACION_URL || 'http://localhost:3002';
const TRANSPORTE_URL = process.env.TRANSPORTE_URL || 'http://localhost:3003';
const NOTIFICACIONES_URL = process.env.NOTIFICACIONES_URL || 'http://localhost:3004';

let pedidosDB = [];
let orderCounter = 1000;

// Promociones simuladas
const promociones = {
  'FRESCO-10': { tipo: 'porcentaje', valor: 10, valido: true },
  'PROMO-2024': { tipo: 'porcentaje', valor: 15, valido: false }, // Expirada
  'SUPER-20': { tipo: 'porcentaje', valor: 20, valido: true }
};

// --- FUNCIÓN PARA CALCULAR TOTAL CON PROMOCIÓN ---
function calcularTotal(items, codigoPromocion) {
  const subtotal = items.reduce((sum, item) => sum + (item.precio || 10) * item.cantidad, 0);
  
  if (codigoPromocion && promociones[codigoPromocion] && promociones[codigoPromocion].valido) {
    const promo = promociones[codigoPromocion];
    if (promo.tipo === 'porcentaje') {
      return subtotal * (1 - promo.valor / 100);
    }
  }
  return subtotal;
}

// --- ENDPOINT 1: CREAR PEDIDO ---
app.post('/pedidos', async (req, res) => {
  const { clienteId, direccion, items, codigoPromocion } = req.body;

  // 1. Validar campos obligatorios
  if (!clienteId || !items || items.length === 0) {
    return res.status(400).json({ error: 'Cliente y items son obligatorios' });
  }

  try {
    // 2. Verificar stock en Inventario (con timeout de 2 segundos)
    const stockCheck = await axios.post(`${INVENTARIO_URL}/verificar`, { items }, { timeout: 2000 });
    if (!stockCheck.data.disponible) {
      return res.status(409).json({ error: 'Stock insuficiente', detalle: stockCheck.data.errores });
    }

    // 3. Reservar stock
    const reserva = await axios.post(`${INVENTARIO_URL}/reservar`, { items }, { timeout: 2000 });

    // 4. Calcular total
    const montoTotal = calcularTotal(items, codigoPromocion);
    const promocionAplicada = (codigoPromocion && promociones[codigoPromocion]?.valido) ? codigoPromocion : null;

    // 5. Generar pedido (en memoria)
    const pedidoId = `ORD-${++orderCounter}`;
    const nuevoPedido = {
      id: pedidoId,
      clienteId,
      direccion,
      items,
      codigoPromocion: promocionAplicada,
      montoTotal,
      estado: 'Confirmado',
      fecha: new Date().toISOString()
    };
    pedidosDB.push(nuevoPedido);

    // 6. Generar factura (llamada a Facturación)
    const factura = await axios.post(`${FACTURACION_URL}/facturar`, {
      pedidoId,
      clienteId,
      montoTotal,
      items
    }, { timeout: 3000 });

    // 7. Asignar transporte (llamada a Transporte) - OJO: si falla, el pedido ya está creado.
    // Para simplificar, lo hacemos síncrono. Si falla, cambiamos estado a "Pendiente de Transporte".
    let transporte = { exito: false, asignacion: null };
    try {
      const respTransporte = await axios.post(`${TRANSPORTE_URL}/asignar`, {
        pedidoId,
        direccion
      }, { timeout: 3000 });
      transporte = respTransporte.data;
      if (transporte.exito) {
        nuevoPedido.estado = 'En camino';
        nuevoPedido.transporte = transporte.asignacion;
      }
    } catch (error) {
      nuevoPedido.estado = 'Pendiente de transporte';
      console.log(`⚠️ Transporte falló para pedido ${pedidoId}, queda pendiente.`);
    }

    // 8. Enviar notificación (asíncrona, no bloqueamos la respuesta)
    axios.post(`${NOTIFICACIONES_URL}/enviar`, {
      clienteId,
      mensaje: `Su pedido ${pedidoId} ha sido confirmado. Estado: ${nuevoPedido.estado}`,
      tipo: 'email'
    }).catch(err => console.log('Error al enviar notificación:', err.message));

    // 9. Responder al cliente
    res.status(201).json({
      mensaje: 'Pedido creado exitosamente',
      pedido: nuevoPedido,
      factura: factura.data.factura,
      transporte: transporte.asignacion
    });

  } catch (error) {
    console.error('Error en flujo de pedido:', error.message);
    if (error.code === 'ECONNABORTED') {
      return res.status(504).json({ error: 'Timeout en la comunicación entre servicios' });
    }
    res.status(500).json({ error: 'Error interno al procesar el pedido', detalle: error.message });
  }
});

// --- ENDPOINT 2: CANCELAR PEDIDO ---
app.put('/pedidos/:id/cancelar', async (req, res) => {
  const { id } = req.params;
  const pedido = pedidosDB.find(p => p.id === id);
  
  if (!pedido) {
    return res.status(404).json({ error: 'Pedido no encontrado' });
  }

  if (pedido.estado === 'En camino' || pedido.estado === 'Entregado') {
    return res.status(400).json({ error: 'No es posible cancelar el pedido porque ya está en ruta o entregado' });
  }

  // Liberar stock
  try {
    await axios.post(`${INVENTARIO_URL}/liberar`, { items: pedido.items });
    pedido.estado = 'Cancelado';
    
    // Notificar cancelación
    axios.post(`${NOTIFICACIONES_URL}/enviar`, {
      clienteId: pedido.clienteId,
      mensaje: `Su pedido ${id} ha sido cancelado exitosamente.`,
      tipo: 'email'
    }).catch(() => {});

    res.json({ mensaje: 'Pedido cancelado y stock liberado', pedido });
  } catch (error) {
    res.status(500).json({ error: 'Error al liberar stock', detalle: error.message });
  }
});

// --- ENDPOINT 3: OBTENER PEDIDO POR ID ---
app.get('/pedidos/:id', (req, res) => {
  const pedido = pedidosDB.find(p => p.id === req.params.id);
  if (!pedido) return res.status(404).json({ error: 'No encontrado' });
  res.json(pedido);
});

// --- ENDPOINT 4: LISTAR TODOS LOS PEDIDOS ---
app.get('/pedidos', (req, res) => {
  res.json(pedidosDB);
});

app.listen(PORT, () => {
  console.log(`📋 Servicio de Pedidos corriendo en puerto ${PORT}`);
});