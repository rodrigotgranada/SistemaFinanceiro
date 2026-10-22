import { request } from '../../shared/services/apiClient';

export async function listarContas() {
  return request('/contas');
}

export async function obterConta(numeroConta) {
  return request(`/contas/${numeroConta}`);
}

export async function criarConta(dados) {
  return request('/contas', {
    method: 'POST',
    body: dados,
  });
}
