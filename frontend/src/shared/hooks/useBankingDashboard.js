import { useState, useCallback, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { APP_TEXTS } from '../constants/texts';
import { formatarMoeda } from '../utils/formatters';
import { useContas } from '../../contas/hooks/useContas';
import { useMovimentacoes } from '../../movimentacoes/hooks/useMovimentacoes';

export function useBankingDashboard() {
  const navigate = useNavigate();
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
      if (res?.saldoAtual !== undefined) {
        atualizarSaldoContaSelecionada(res.saldoAtual);
      }
      navigate('/deposito');
    } catch (err) {
      mostrarAlerta('error', err.message);
    }
  }, [contaSelecionada?.numeroConta, depositar, carregarDetalhesConta, atualizarSaldoContaSelecionada, mostrarAlerta, navigate]);

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
      if (res?.saldoAtual !== undefined) {
        atualizarSaldoContaSelecionada(res.saldoAtual);
      }
      navigate('/saque');
    } catch (err) {
      mostrarAlerta('error', err.message);
    }
  }, [contaSelecionada?.numeroConta, sacar, carregarDetalhesConta, atualizarSaldoContaSelecionada, mostrarAlerta, navigate]);

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
      if (res?.saldoAtual !== undefined) {
        atualizarSaldoContaSelecionada(res.saldoAtual);
      }
      navigate('/transferencia');
    } catch (err) {
      mostrarAlerta('error', err.message);
    }
  }, [contaSelecionada?.numeroConta, transferir, carregarDetalhesConta, atualizarSaldoContaSelecionada, mostrarAlerta, navigate]);

  const handleNovaConta = useCallback(async (formData, onSuccess) => {
    try {
      const novaConta = await abrirConta(formData);
      mostrarAlerta('success', APP_TEXTS.messages.contaAbertaSucesso(novaConta.numeroFormatado, novaConta.nomeTitular));
      if (onSuccess) onSuccess();
      await carregarDetalhesConta(novaConta.numeroConta);
      navigate('/transferencia');
    } catch (err) {
      mostrarAlerta('error', err.message);
    }
  }, [abrirConta, carregarDetalhesConta, mostrarAlerta, navigate]);

  const rotasNavegacao = useMemo(() => [
    { path: '/deposito', label: APP_TEXTS.tabs.deposito, precisaConta: true },
    { path: '/saque', label: APP_TEXTS.tabs.saque, precisaConta: true },
    { path: '/transferencia', label: APP_TEXTS.tabs.transferencia, precisaConta: true },
    { path: '/nova-conta', label: APP_TEXTS.tabs.abrirConta, precisaConta: false },
  ], []);

  return {
    navigate,
    feedback,
    fecharAlerta,
    mostrarAlerta,
    contas,
    contaSelecionada,
    selecionarConta,
    loadingMovimentacao,
    rotasNavegacao,
    handleDeposito,
    handleSaque,
    handleTransferencia,
    handleNovaConta
  };
}
