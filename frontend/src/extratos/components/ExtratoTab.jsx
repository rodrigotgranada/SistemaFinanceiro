import React, { useMemo } from 'react';
import Badge from '../../shared/ui/Badge';
import { formatarMoeda, formatarData, TIPO_MOVIMENTACAO_LABELS } from '../../shared/utils/formatters';
import { EXTRATOS_TEXTS } from '../constants/texts';
import { useBanking } from '../../shared/context/BankingContext';

export default function ExtratoTab({ 
  extrato: propExtrato, 
  loading: propLoading, 
  dataInicio: propDataInicio, 
  setDataInicio: propSetDataInicio, 
  dataFim: propDataFim, 
  setDataFim: propSetDataFim, 
  onFiltrar: propOnFiltrar, 
  onLimparFiltros: propOnLimparFiltros 
} = {}) {
  const banking = useBanking();
  const extrato = propExtrato !== undefined ? propExtrato : banking?.extrato;
  const loading = propLoading !== undefined ? propLoading : banking?.loadingExtrato;
  const dataInicio = propDataInicio !== undefined ? propDataInicio : (banking?.dataInicio || '');
  const setDataInicio = propSetDataInicio || banking?.setDataInicio;
  const dataFim = propDataFim !== undefined ? propDataFim : (banking?.dataFim || '');
  const setDataFim = propSetDataFim || banking?.setDataFim;
  const onFiltrar = propOnFiltrar || banking?.handleFiltrarExtrato;
  const onLimparFiltros = propOnLimparFiltros || banking?.handleLimparFiltros;

  const movimentacoes = useMemo(() => extrato?.movimentacoes || [], [extrato]);

  return (
    <div className="bg-white border border-gray-200 rounded p-4 shadow-sm">
      <div className="flex justify-between items-center flex-wrap gap-3 mb-3.5">
        <h2 className="text-base font-bold text-gray-900">{EXTRATOS_TEXTS.titulo}</h2>
        
        <div className="flex items-center gap-1.5 flex-wrap">
          <input 
            type="date" 
            className="px-2 py-1 bg-white border border-gray-300 rounded text-xs text-gray-800 focus:outline-none focus:border-blue-500"
            value={dataInicio}
            onChange={(e) => setDataInicio && setDataInicio(e.target.value)}
          />
          <span className="text-xs text-gray-500">{EXTRATOS_TEXTS.separadorAte}</span>
          <input 
            type="date" 
            className="px-2 py-1 bg-white border border-gray-300 rounded text-xs text-gray-800 focus:outline-none focus:border-blue-500"
            value={dataFim}
            onChange={(e) => setDataFim && setDataFim(e.target.value)}
          />
          <button 
            type="button"
            className="py-1 px-2.5 bg-blue-600 hover:bg-blue-700 text-white text-xs font-semibold rounded transition-colors disabled:opacity-60"
            onClick={onFiltrar}
            disabled={loading}
          >
            {loading ? EXTRATOS_TEXTS.btnFiltrando : EXTRATOS_TEXTS.btnFiltrar}
          </button>
          {(dataInicio || dataFim) && (
            <button 
              type="button"
              className="py-1 px-2.5 bg-gray-100 hover:bg-gray-200 text-gray-700 text-xs font-semibold rounded border border-gray-300 transition-colors"
              onClick={onLimparFiltros}
            >
              {EXTRATOS_TEXTS.btnLimpar}
            </button>
          )}
        </div>
      </div>

      {movimentacoes.length > 0 ? (
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs border-collapse">
            <thead>
              <tr className="bg-gray-50 border-b border-gray-200 text-[11px] font-semibold text-gray-500 uppercase tracking-wider">
                <th className="py-2 px-3">{EXTRATOS_TEXTS.thData}</th>
                <th className="py-2 px-3">{EXTRATOS_TEXTS.thOperacao}</th>
                <th className="py-2 px-3">{EXTRATOS_TEXTS.thDescricao}</th>
                <th className="py-2 px-3 text-right">{EXTRATOS_TEXTS.thValor}</th>
                <th className="py-2 px-3 text-right">{EXTRATOS_TEXTS.thSaldo}</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {movimentacoes.map((mov) => {
                const isCredit = mov.tipo === 'DEPOSITO' || mov.tipo === 'TRANSFERENCIA_RECEBIMENTO' || mov.tipo === 'TRANSFERENCIA_DESTINO';
                const operacoesLabels = EXTRATOS_TEXTS.operacoes || TIPO_MOVIMENTACAO_LABELS;
                const labelOperacao = operacoesLabels[mov.tipo] || mov.tipo;

                return (
                  <tr key={mov.id} className="hover:bg-gray-50 transition-colors">
                    <td className="py-2.5 px-3 text-gray-500 whitespace-nowrap">
                      {formatarData(mov.dataHora)}
                    </td>
                    <td className="py-2.5 px-3">
                      <Badge variant={isCredit ? 'success' : 'danger'}>
                        {labelOperacao}
                      </Badge>
                    </td>
                    <td className="py-2.5 px-3">
                      <div className="text-gray-900">{mov.descricao || '-'}</div>
                      {mov.contaContraparte && (
                        <div className="text-[11px] text-gray-400">
                          {EXTRATOS_TEXTS.labelContaContraparte} {mov.contaContraparte}
                        </div>
                      )}
                    </td>
                    <td className={`py-2.5 px-3 text-right font-bold whitespace-nowrap ${isCredit ? 'text-green-700' : 'text-red-700'}`}>
                      {isCredit ? '+' : '-'} {formatarMoeda(mov.valor)}
                    </td>
                    <td className="py-2.5 px-3 text-right font-medium text-gray-900 whitespace-nowrap">
                      {formatarMoeda(mov.saldoResultante)}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      ) : (
        <div className="text-center py-8 text-gray-500 text-xs bg-gray-50 rounded border border-dashed border-gray-300">
          <p>{EXTRATOS_TEXTS.semMovimentacoes}</p>
        </div>
      )}
    </div>
  );
}
