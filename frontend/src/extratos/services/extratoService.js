import { request } from '../../shared/services/apiClient';

export async function consultarExtrato(numeroConta, dataInicio = '', dataFim = '') {
  const params = new URLSearchParams();
  if (dataInicio) params.append('dataInicio', `${dataInicio}T00:00:00Z`);
  if (dataFim) params.append('dataFim', `${dataFim}T23:59:59Z`);

  const queryString = params.toString();
  const url = `/contas/${numeroConta}/extrato${queryString ? `?${queryString}` : ''}`;

  return request(url);
}
