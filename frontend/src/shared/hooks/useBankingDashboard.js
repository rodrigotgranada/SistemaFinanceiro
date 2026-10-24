import { useState, useEffect, useCallback, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { APP_TEXTS } from '../constants/texts';
import { formatarMoeda } from '../utils/formatters';
import { useContas } from '../../contas/hooks/useContas';
import { useMovimentacoes } from '../../movimentacoes/hooks/useMovimentacoes';
import { useExtrato } from '../../extratos/hooks/useExtrato';

export function useBankingDashboard() {
  const navigate = useNavigate();
  const [dataInicio, setDataInicio] = useState('');
  const [dataFim, setDataFim] = useState('');
  const [feedback, setFeedback] = useState({ tipo: null, mensagem: null });

  const { 
    contas, 
    contaSelecionada, 
    selecionarConta, 
    carregarDetalhesConta,
    atualizarSaldoContaSelecionada, 
    abrirConta 
  } = useContas();

  const { 
    extrato, 
    loadingExtrato, 
    carregarExtrato 
  } = useExtrato();

  const { 
    loadingMovimentacao, 
    depositar, 
    sacar, 
    transferir 
  } = useMovimentacoes();

  const mostrarAlerta = useCallback((tipo, mensagem) => {
    setFeedback({ tipo, mensagem });
    setTimeout(() => setFeedback({ tipo: null, mensagem: null }), 6000);
  }, []);

  const fecharAlerta = useCallback(() => {
    setFeedback({ tipo: null, mensagem: null });
  }, []);

  useEffect(() => {
    if (contaSelecionada?.numeroConta) {
      carregarExtrato(contaSelecionada.numeroConta, dataInicio, dataFim)
        .then(data => {
          if (data?.saldoAtual !== undefined) {
            atualizarSaldoContaSelecionada(data.saldoAtual);
          }
        })
        .catch(err => {
          mostrarAlerta('error', err.message);
        });
    }
  }, [contaSelecionada?.numeroConta, carregarExtrato, atualizarSaldoContaSelecionada]);

  const handleDeposito = useCallback(async (valorStr, onSuccess) => {
    const val = parseFloat(valorStr);
    if (!val || val <= 0) {
      mostrarAlerta('error', APP_TEXTS.messages.valorInvalido);
      return;
    }
    try {
      const res = await depositar(contaSelecionada.numeroConta, val);
      mostrarAlerta('success', APP_TEXTS.messages.depositoSucesso(formatarMoeda(val), res.protocolo));
      if (onSuccess) onSuccess();
      await carregarDetalhesConta(contaSelecionada.numeroConta);
      const novoExtrato = await carregarExtrato(contaSelecionada.numeroConta);
      if (novoExtrato?.saldoAtual !== undefined) {
        atualizarSaldoContaSelecionada(novoExtrato.saldoAtual);
      }
      navigate('/extrato');
    } catch (err) {
      mostrarAlerta('error', err.message);
    }
  }, [contaSelecionada?.numeroConta, depositar, carregarDetalhesConta, carregarExtrato, atualizarSaldoContaSelecionada, mostrarAlerta, navigate]);

  const handleSaque = useCallback(async (valorStr, onSuccess) => {
    const val = parseFloat(valorStr);
    if (!val || val <= 0) {
      mostrarAlerta('error', APP_TEXTS.messages.valorInvalido);
      return;
    }
    try {
      const res = await sacar(contaSelecionada.numeroConta, val);
      mostrarAlerta('success', APP_TEXTS.messages.saqueSucesso(formatarMoeda(val), res.protocolo));
      if (onSuccess) onSuccess();
      await carregarDetalhesConta(contaSelecionada.numeroConta);
      const novoExtrato = await carregarExtrato(contaSelecionada.numeroConta);
      if (novoExtrato?.saldoAtual !== undefined) {
        atualizarSaldoContaSelecionada(novoExtrato.saldoAtual);
      }
      navigate('/extrato');
    } catch (err) {
      mostrarAlerta('error', err.message);
    }
  }, [contaSelecionada?.numeroConta, sacar, carregarDetalhesConta, carregarExtrato, atualizarSaldoContaSelecionada, mostrarAlerta, navigate]);

  const handleTransferencia = useCallback(async (formData, onSuccess) => {
    const val = parseFloat(formData.valor);
    if (!formData.contaDestino || !val || val <= 0) {
      mostrarAlerta('error', APP_TEXTS.messages.preenchaDestino);
      return;
    }
    try {
      const res = await transferir({
        contaOrigem: contaSelecionada.numeroConta,
        contaDestino: formData.contaDestino,
        valor: val,
        descricao: formData.descricao
      });
      mostrarAlerta('success', APP_TEXTS.messages.transferenciaSucesso(formatarMoeda(val), res.protocolo));
      if (onSuccess) onSuccess();
      await carregarDetalhesConta(contaSelecionada.numeroConta);
      const novoExtrato = await carregarExtrato(contaSelecionada.numeroConta);
      if (novoExtrato?.saldoAtual !== undefined) {
        atualizarSaldoContaSelecionada(novoExtrato.saldoAtual);
      }
      navigate('/extrato');
    } catch (err) {
      mostrarAlerta('error', err.message);
    }
  }, [contaSelecionada?.numeroConta, transferir, carregarDetalhesConta, carregarExtrato, atualizarSaldoContaSelecionada, mostrarAlerta, navigate]);

  const handleNovaConta = useCallback(async (formData, onSuccess) => {
    try {
      const novaConta = await abrirConta(formData);
      mostrarAlerta('success', APP_TEXTS.messages.contaAbertaSucesso(novaConta.numeroFormatado, novaConta.nomeTitular));
      if (onSuccess) onSuccess();
      navigate('/extrato');
    } catch (err) {
      mostrarAlerta('error', err.message);
    }
  }, [abrirConta, mostrarAlerta, navigate]);

  const handleFiltrarExtrato = useCallback(() => {
    if (contaSelecionada?.numeroConta) {
      carregarExtrato(contaSelecionada.numeroConta, dataInicio, dataFim)
        .catch(err => mostrarAlerta('error', err.message));
    }
  }, [contaSelecionada?.numeroConta, dataInicio, dataFim, carregarExtrato, mostrarAlerta]);

  const handleLimparFiltros = useCallback(() => {
    setDataInicio('');
    setDataFim('');
    if (contaSelecionada?.numeroConta) {
      carregarExtrato(contaSelecionada.numeroConta, '', '')
        .catch(err => mostrarAlerta('error', err.message));
    }
  }, [contaSelecionada?.numeroConta, carregarExtrato, mostrarAlerta]);

  const rotasNavegacao = useMemo(() => [
    { path: '/extrato', label: APP_TEXTS.tabs.extrato, precisaConta: false },
    { path: '/deposito', label: APP_TEXTS.tabs.deposito, precisaConta: true },
    { path: '/saque', label: APP_TEXTS.tabs.saque, precisaConta: true },
    { path: '/transferencia', label: APP_TEXTS.tabs.transferencia, precisaConta: true },
    { path: '/nova-conta', label: APP_TEXTS.tabs.abrirConta, precisaConta: false },
  ], []);

  return {
    navigate,
    dataInicio,
    setDataInicio,
    dataFim,
    setDataFim,
    feedback,
    fecharAlerta,
    mostrarAlerta,
    contas,
    contaSelecionada,
    selecionarConta,
    extrato,
    loadingExtrato,
    loadingMovimentacao,
    rotasNavegacao,
    handleDeposito,
    handleSaque,
    handleTransferencia,
    handleNovaConta,
    handleFiltrarExtrato,
    handleLimparFiltros
  };
}
