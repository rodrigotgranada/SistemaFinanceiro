import { request } from '../../shared/services/apiClient';

export async function depositar(numeroConta, valor) {
  return request(`/contas/${numeroConta}/depositos`, {
    method: 'POST',
    body: { valor },
  });
}

export async function sacar(numeroConta, valor) {
  return request(`/contas/${numeroConta}/saques`, {
    method: 'POST',
    body: { valor },
  });
}

export async function transferir(dados) {
  return request('/contas/transferencias', {
    method: 'POST',
    body: dados,
  });
}
