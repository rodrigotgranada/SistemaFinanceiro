import { APP_TEXTS } from '../constants/texts';

export function formatarMoeda(val) {
  if (val === undefined || val === null) return 'R$ 0,00';
  return Number(val).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

export function formatarData(isoStr) {
  if (!isoStr) return '-';
  const d = new Date(isoStr);
  return d.toLocaleString('pt-BR');
}

export const TIPO_MOVIMENTACAO_LABELS = APP_TEXTS.operacoes;
