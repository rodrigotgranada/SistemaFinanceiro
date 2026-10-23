import React, { useState, useCallback, useMemo } from 'react';
import MoneyInput from '../../shared/ui/MoneyInput';
import { formatarMoeda } from '../../shared/utils/formatters';
import { MOVIMENTACOES_TEXTS } from '../constants/texts';
import { useBanking } from '../../shared/context/BankingContext';

export default function TransferenciaTab({
  contas: propContas,
  contaSelecionada: propConta,
  onConfirmar: propConfirmar,
  loading: propLoading
} = {}) {
  const banking = useBanking();
  const contas = propContas || banking?.contas || [];
  const contaSelecionada = propConta !== undefined ? propConta : banking?.contaSelecionada;
  const onConfirmar = propConfirmar || banking?.handleTransferencia;
  const loading = propLoading !== undefined ? propLoading : banking?.loadingMovimentacao;

  const [form, setForm] = useState({ contaDestino: '', valor: 0, descricao: '' });

  const contasDestinoDisponiveis = useMemo(() => {
    return contas.filter(c => c.numeroConta !== contaSelecionada?.numeroConta);
  }, [contas, contaSelecionada?.numeroConta]);

  const handleChange = useCallback((e) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  }, []);

  const handleValorChange = useCallback((novoValor) => {
    setForm(prev => ({ ...prev, valor: novoValor }));
  }, []);

  const handleSubmit = useCallback((e) => {
    e.preventDefault();
    if (onConfirmar) {
      onConfirmar(form, () => setForm({ contaDestino: '', valor: 0, descricao: '' }));
    }
  }, [form, onConfirmar]);

  if (!contaSelecionada) {
    return (
      <div className="bg-white border border-gray-200 rounded p-8 text-center text-sm text-gray-500 shadow-sm">
        {MOVIMENTACOES_TEXTS.semContaSelecionadaTransferencia}
      </div>
    );
  }

  return (
    <div className="bg-white border border-gray-200 rounded p-6 shadow-sm max-w-lg mx-auto">
      <h2 className="text-base font-bold text-gray-900 mb-1">{MOVIMENTACOES_TEXTS.tituloTransferencia}</h2>
      <p className="text-xs text-gray-500 mb-4">{MOVIMENTACOES_TEXTS.subtituloTransferencia}</p>

      <form onSubmit={handleSubmit} className="space-y-3.5">
        <div>
          <label className="block text-xs font-semibold text-gray-700 mb-1">
            {MOVIMENTACOES_TEXTS.labelContaOrigem}
          </label>
          <input
            type="text"
            className="w-full px-3 py-1.5 bg-gray-100 border border-gray-300 rounded text-sm text-gray-600 cursor-not-allowed"
            disabled
            value={`${contaSelecionada.numeroFormatado} (Saldo: ${formatarMoeda(contaSelecionada.saldo)})`}
          />
        </div>

        <div>
          <label className="block text-xs font-semibold text-gray-700 mb-1">
            {MOVIMENTACOES_TEXTS.labelContaDestino}
          </label>
          <select
            name="contaDestino"
            className="w-full px-3 py-1.5 bg-white border border-gray-300 rounded text-sm text-gray-900 focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500"
            value={form.contaDestino}
            onChange={handleChange}
            required
          >
            <option value="">{MOVIMENTACOES_TEXTS.placeholderSelecioneDestino}</option>
            {contasDestinoDisponiveis.map(c => (
              <option key={c.id} value={c.numeroConta}>
                {c.numeroFormatado} - {c.nomeTitular}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label className="block text-xs font-semibold text-gray-700 mb-1">
            {MOVIMENTACOES_TEXTS.labelValor}
          </label>
          <MoneyInput
            value={form.valor}
            onChange={handleValorChange}
            disabled={loading}
            required
          />
        </div>

        <div>
          <label className="block text-xs font-semibold text-gray-700 mb-1">
            {MOVIMENTACOES_TEXTS.labelDescricao}
          </label>
          <input
            type="text"
            name="descricao"
            className="w-full px-3 py-1.5 bg-white border border-gray-300 rounded text-sm text-gray-900 focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500"
            placeholder={MOVIMENTACOES_TEXTS.placeholderDescricao}
            value={form.descricao}
            onChange={handleChange}
          />
        </div>

        <button
          type="submit"
          className="w-full py-2 px-4 bg-blue-600 hover:bg-blue-700 text-white text-sm font-semibold rounded shadow-sm transition-colors disabled:opacity-60 disabled:cursor-not-allowed"
          disabled={loading}
        >
          {loading ? MOVIMENTACOES_TEXTS.btnProcessando : MOVIMENTACOES_TEXTS.btnConfirmarTransferencia}
        </button>
      </form>
    </div>
  );
}
