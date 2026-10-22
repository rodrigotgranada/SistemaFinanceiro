import React from 'react';
import Badge from '../../shared/ui/Badge';
import { formatarMoeda } from '../../shared/utils/formatters';
import { CONTAS_TEXTS } from '../constants/texts';

export default function ContaCard({
  contas,
  contaSelecionada,
  onSelecionarConta
}) {
  return (
    <div className="bg-white border border-gray-200 rounded p-4 shadow-sm">
      <div className="mb-3.5">
        <label className="block text-xs font-semibold text-gray-700 mb-1" htmlFor="select-conta">
          {CONTAS_TEXTS.labelContaAtiva}
        </label>
        <select
          id="select-conta"
          className="w-full px-2.5 py-1.5 bg-white border border-gray-300 rounded text-sm text-gray-900 focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500"
          value={contaSelecionada?.numeroConta || ''}
          onChange={(e) => {
            if (e.target.value) onSelecionarConta(e.target.value);
          }}
        >
          {contas.length === 0 && <option value="">{CONTAS_TEXTS.placeholderSemContas}</option>}
          {contas.map(c => (
            <option key={c.id} value={c.numeroConta}>
              {c.numeroFormatado} - {c.nomeTitular}
            </option>
          ))}
        </select>
      </div>

      {contaSelecionada ? (
        <div className="bg-gray-50 border border-gray-200 rounded p-3">
          <div className="flex justify-between items-center">
            <span className="text-xs font-bold text-blue-600">
              {CONTAS_TEXTS.conta}: {contaSelecionada.numeroFormatado}
            </span>
            <Badge variant="info">{contaSelecionada.status}</Badge>
          </div>

          <div className="my-2.5">
            <div className="text-[11px] uppercase tracking-wider text-gray-500 font-semibold">
              {CONTAS_TEXTS.labelSaldoDisponivel}
            </div>
            <div className="text-2xl font-bold text-gray-900">
              {formatarMoeda(contaSelecionada.saldo)}
            </div>
          </div>

          <div className="border-t border-gray-200 pt-2 text-xs">
            <div className="font-semibold text-gray-800">{contaSelecionada.nomeTitular}</div>
            <div className="text-gray-500">CPF/CNPJ: {contaSelecionada.cpfCnpj}</div>
          </div>
        </div>
      ) : (
        <div className="text-center py-6 text-gray-500 text-xs bg-gray-50 rounded border border-dashed border-gray-300">
          <p>{CONTAS_TEXTS.placeholderNenhumaSelecionada}</p>
        </div>
      )}
    </div>
  );
}
