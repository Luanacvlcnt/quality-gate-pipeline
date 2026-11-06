// Teste de carga leve (k6) - Gate 8
// Uso: k6 run -e BASE_URL=http://localhost:8080 tests/load/smoke.js
import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export const options = {
  vus: 10,
  duration: '30s',
  // Os thresholds são o quality gate: se forem violados, o k6 sai com erro
  thresholds: {
    http_req_duration: ['p(95)<300'], // 95% das requisições abaixo de 300 ms
    http_req_failed: ['rate<0.01'],   // menos de 1% de erros
  },
};

export default function () {
  const res = http.get(`${BASE_URL}/health`);
  check(res, { 'status é 200': (r) => r.status === 200 });
  sleep(1);
}
