const express = require('express');
const cors = require('cors');
const app = express();
const PORT = 3004;

app.use(cors());
app.use(express.json());

// Endpoint: Enviar notificación
app.post('/enviar', (req, res) => {
  const { clienteId, mensaje, tipo } = req.body;
  
  // Simular envío de email/SMS (50-150ms)
  const delay = Math.floor(Math.random() * 100) + 50;
  setTimeout(() => {
    console.log(`📧 Notificación enviada a Cliente ${clienteId}: ${mensaje}`);
    res.json({
      exito: true,
      mensaje: 'Notificación enviada correctamente',
      canal: tipo === 'email' ? 'email' : 'sms'
    });
  }, delay);
});

app.listen(PORT, () => {
  console.log(`📧 Notificaciones corriendo en puerto ${PORT}`);
});