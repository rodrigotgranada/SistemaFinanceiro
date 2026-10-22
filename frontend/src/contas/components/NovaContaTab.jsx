import React, { useState, useCallback } from 'react';
import { CONTAS_TEXTS } from '../constants/texts';
import { useBanking } from '../../shared/context/BankingContext';

export default function NovaContaTab({ 
  onConfirmar: propConfirmar, 
  loading: propLoading 
} = {}) {
  const banking = useBanking();
  const onConfirmar = propConfirmar || banking?.handleNovaConta;
  const loading = propLoading !== undefined ? propLoading : false;

  const [form, setForm] = useState({ cpfCnpj: '', nomeTitular: '', numeroConta: '' });

  const handleChange = useCallback((e) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  }, []);

  const handleSubmit = useCallback((e) => {
    e.preventDefault();
    if (onConfirmar) {
      onConfirmar(form, () => setForm({ cpfCnpj: '', nomeTitular: '', numeroConta: '' }));
    }
  }, [form, onConfirmar]);

  return (
    <div className="bg-white border border-gray-200 rounded p-6 shadow-sm max-w-lg mx-auto">
      <h2 className="text-base font-bold text-gray-900 mb-1">{CONTAS_TEXTS.tituloNovaConta}</h2>
      <p className="text-xs text-gray-500 mb-4">{CONTAS_TEXTS.subtituloNovaConta}</p>

      <form onSubmit={handleSubmit} className="space-y-3.5">
        <div>
          <label className="block text-xs font-semibold text-gray-700 mb-1">
            {CONTAS_TEXTS.labelCpfCnpj}
          </label>
          <input 
            type="text" 
            name="cpfCnpj"
            className="w-full px-3 py-1.5 bg-white border border-gray-300 rounded text-sm text-gray-900 focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500" 
            placeholder={CONTAS_TEXTS.placeholderCpfCnpj}
            value={form.cpfCnpj}
            onChange={handleChange}
            required 
          />
        </div>

        <div>
          <label className="block text-xs font-semibold text-gray-700 mb-1">
            {CONTAS_TEXTS.labelNomeTitular}
          </label>
          <input 
            type="text" 
            name="nomeTitular"
            className="w-full px-3 py-1.5 bg-white border border-gray-300 rounded text-sm text-gray-900 focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500" 
            placeholder={CONTAS_TEXTS.placeholderNomeTitular}
            value={form.nomeTitular}
            onChange={handleChange}
            required 
          />
        </div>

        <div>
          <label className="block text-xs font-semibold text-gray-700 mb-1">
            {CONTAS_TEXTS.labelNumeroConta}
          </label>
          <input 
            type="text" 
            name="numeroConta"
            className="w-full px-3 py-1.5 bg-white border border-gray-300 rounded text-sm text-gray-900 focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500" 
            placeholder={CONTAS_TEXTS.placeholderNumeroConta}
            value={form.numeroConta}
            onChange={handleChange}
          />
        </div>

        <button 
          type="submit" 
          className="w-full py-2 px-4 bg-blue-600 hover:bg-blue-700 text-white text-sm font-semibold rounded shadow-sm transition-colors disabled:opacity-60 disabled:cursor-not-allowed" 
          disabled={loading}
        >
          {loading ? CONTAS_TEXTS.btnCadastrando : CONTAS_TEXTS.btnCadastrar}
        </button>
      </form>
    </div>
  );
}
