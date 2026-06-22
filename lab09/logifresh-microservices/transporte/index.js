const express = require('express');
const cors = require('cors');
const app = express();
const PORT = 3003;

app.use(cors());
app.use(express.json());

// Endpoint: Asignar transporte
app.post('/asignar', (req, res) => {
  const { pedidoId, direccion } = req.body;
  
  // Simular búsqueda de ruta y conductor (200-600ms)
  const delay = Math.floor(Math.random() * 400) + 200;
  setTimeout(() => {
    const conductores = ['Carlos G.', 'María L.', 'Juan P.', 'Ana R.'];
    const conductor = conductores[Math.floor(Math.random() * conductores.length)];
    const vehiculo = `V-${Math.floor(Math.random() * 1000)}`;
    
    res.json({
      exito: true,
      asignacion: {
        pedidoId,
        conductor,
        vehiculo,
        estado: 'Asignado',
        tiempoEstimado: `${Math.floor(Math.random() * 30) + 15} minutos`
      }
    });
  }, delay);
});

app.listen(PORT, () => {
  console.log(`🚚 Transporte corriendo en puerto ${PORT}`);
});