const express = require('express');
const cors = require('cors');
const app = express();
const PORT = 3002;

app.use(cors());
app.use(express.json());

let facturaCounter = 1000;

// Endpoint: Generar factura
app.post('/facturar', (req, res) => {
  const { pedidoId, clienteId, montoTotal, items } = req.body;
  
  // Simular validación con SUNAT (latencia de 200-400ms)
  const delay = Math.floor(Math.random() * 300) + 200; // 200ms a 500ms
  setTimeout(() => {
    const numeroFactura = `F001-${++facturaCounter}`;
    res.json({
      exito: true,
      factura: {
        numero: numeroFactura,
        pedidoId,
        clienteId,
        montoTotal,
        fecha: new Date().toISOString(),
        items
      }
    });
  }, delay);
});

app.listen(PORT, () => {
  console.log(`🧾 Facturación corriendo en puerto ${PORT}`);
});