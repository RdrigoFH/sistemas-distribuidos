const express = require('express');
const cors = require('cors');
const app = express();
const PORT = 3001;

app.use(cors());
app.use(express.json());

// Base de datos en memoria (simulada)
let stock = {
  'P-001': { nombre: 'Pollo congelado', cantidad: 50 },
  'P-002': { nombre: 'Lasaña', cantidad: 30 },
  'P-003': { nombre: 'Pescado congelado', cantidad: 15 },
  'P-004': { nombre: 'Verduras mixtas', cantidad: 20 },
  'P-005': { nombre: 'Helado', cantidad: 10 },
  'P-006': { nombre: 'Chocolate', cantidad: 25 },
  'P-007': { nombre: 'Queso', cantidad: 18 }
};

// Endpoint: Verificar stock (para Pedidos)
app.post('/verificar', (req, res) => {
  const { items } = req.body;
  let disponible = true;
  let mensajes = [];

  for (let item of items) {
    const producto = stock[item.productoId];
    if (!producto) {
      disponible = false;
      mensajes.push(`Producto ${item.productoId} no existe`);
    } else if (producto.cantidad < item.cantidad) {
      disponible = false;
      mensajes.push(`Stock insuficiente para ${producto.nombre}. Disponible: ${producto.cantidad}`);
    }
  }

  if (disponible) {
    // Simular latencia de red (100ms) para hacer las pruebas de rendimiento realistas
    setTimeout(() => res.json({ disponible: true, mensaje: 'Stock suficiente' }), 100);
  } else {
    res.status(409).json({ disponible: false, errores: mensajes });
  }
});

// Endpoint: Reservar/Descontar stock (para Pedidos)
app.post('/reservar', (req, res) => {
  const { items } = req.body;
  let errores = [];

  for (let item of items) {
    const producto = stock[item.productoId];
    if (!producto || producto.cantidad < item.cantidad) {
      errores.push(`Fallo al reservar ${item.productoId}`);
    } else {
      producto.cantidad -= item.cantidad;
    }
  }

  if (errores.length === 0) {
    setTimeout(() => res.json({ exito: true, mensaje: 'Stock reservado' }), 100);
  } else {
    res.status(500).json({ exito: false, errores });
  }
});

// Endpoint: Liberar stock (para cancelaciones)
app.post('/liberar', (req, res) => {
  const { items } = req.body;
  for (let item of items) {
    const producto = stock[item.productoId];
    if (producto) {
      producto.cantidad += item.cantidad;
    }
  }
  setTimeout(() => res.json({ exito: true, mensaje: 'Stock liberado' }), 100);
});

// Endpoint: Obtener stock (para consultas)
app.get('/stock', (req, res) => {
  res.json(stock);
});

app.listen(PORT, () => {
  console.log(`📦 Inventario corriendo en puerto ${PORT}`);
});