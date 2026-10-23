import React, { useState, useCallback } from 'react';
import MoneyInput from '../../shared/ui/MoneyInput';
import { MOVIMENTACOES_TEXTS } from '../constants/texts';
import { useBanking } from '../../shared/context/BankingContext';

export default function SaqueTab({
  contaSelecionada: propConta,
  onConfirmar: propConfirmar,
  loading: propLoading
} = {}) {
  const banking = useBanking();
  const contaSelecionada = propConta !== undefined ? propConta : banking?.contaSelecionada;
  const onConfirmar = propConfirmar || banking?.handleSaque;
  const loading = propLoading !== undefined ? propLoading : banking?.loadingMovimentacao;

  const [valor, setValor] = useState(0);

  const handleSubmit = useCallback((e) => {
    e.preventDefault();
    if (onConfirmar) {
      onConfirmar(valor, () => setValor(0));
    }
  }, [valor, onConfirmar]);

  if (!contaSelecionada) {
    return (
      <div className="bg-white border border-gray-200 rounded p-8 text-center text-sm text-gray-500 shadow-sm">
        {MOVIMENTACOES_TEXTS.semContaSelecionadaSaque}
      </div>
    );
  }

  return (
    <div className="bg-white border border-gray-200 rounded p-6 shadow-sm max-w-lg mx-auto">
      <h2 className="text-base font-bold text-gray-900 mb-1">{MOVIMENTACOES_TEXTS.tituloSaque}</h2>
      <p className="text-xs text-gray-500 mb-4">{MOVIMENTACOES_TEXTS.subtituloSaque}</p>

      <form onSubmit={handleSubmit} className="space-y-3.5">
        <div>
          <label className="block text-xs font-semibold text-gray-700 mb-1">
            {MOVIMENTACOES_TEXTS.labelContaDebito}
          </label>
          <input
            type="text"
            className="w-full px-3 py-1.5 bg-gray-100 border border-gray-300 rounded text-sm text-gray-600 cursor-not-allowed"
            disabled
            value={`${contaSelecionada.numeroFormatado} - ${contaSelecionada.nomeTitular}`}
          />
        </div>

        <div>
          <label className="block text-xs font-semibold text-gray-700 mb-1">
            {MOVIMENTACOES_TEXTS.labelValor}
          </label>
          <MoneyInput
            value={valor}
            onChange={setValor}
            disabled={loading}
            required
          />
        </div>

        <button
          type="submit"
          className="w-full py-2 px-4 bg-red-600 hover:bg-red-700 text-white text-sm font-semibold rounded shadow-sm transition-colors disabled:opacity-60 disabled:cursor-not-allowed"
          disabled={loading}
        >
          {loading ? MOVIMENTACOES_TEXTS.btnProcessando : MOVIMENTACOES_TEXTS.btnConfirmarSaque}
        </button>
      </form>
    </div>
  );
}
