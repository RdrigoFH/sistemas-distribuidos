import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '1m', target: 100 },  
    { duration: '3m', target: 100 }, 
    { duration: '1m', target: 0 },    
  ],
  thresholds: {
    http_req_duration: ['p(95)<1500'],
    http_req_failed: ['rate<0.02'],
  },
};

const payload = JSON.stringify({
  clienteId: 'C-TEST',
  direccion: 'Calle Rendimiento 123',
  items: [
    { productoId: 'P-001', cantidad: 1, precio: 25 },
    { productoId: 'P-005', cantidad: 2, precio: 15 }
  ],
  codigoPromocion: 'FRESCO-10'
});

const params = {
  headers: { 'Content-Type': 'application/json' },
};

export default function () {
  const res = http.post('http://localhost:3000/pedidos', payload, params);
  
  check(res, {
    'Estado es 201': (r) => r.status === 201,
    'Respuesta rápida (< 2 segundos)': (r) => r.timings.duration < 2000,
  });
  
  sleep(1);
}