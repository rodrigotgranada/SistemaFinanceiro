import { useState, useCallback, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { APP_TEXTS } from '../constants/texts';
import { useContas } from '../../contas/hooks/useContas';

export function useBankingDashboard() {
  const navigate = useNavigate();
  const [feedback, setFeedback] = useState({ tipo: null, mensagem: null });

  const { 
    contas, 
    contaSelecionada, 
    selecionarConta, 
    carregarDetalhesConta,
    abrirConta 
  } = useContas();

  const mostrarAlerta = useCallback((tipo, mensagem) => {
    setFeedback({ tipo, mensagem });
    setTimeout(() => setFeedback({ tipo: null, mensagem: null }), 6000);
  }, []);

  const fecharAlerta = useCallback(() => {
    setFeedback({ tipo: null, mensagem: null });
  }, []);

  const handleNovaConta = useCallback(async (formData, onSuccess) => {
    try {
      const novaConta = await abrirConta(formData);
      mostrarAlerta('success', APP_TEXTS.messages.contaAbertaSucesso(novaConta.numeroFormatado, novaConta.nomeTitular));
      if (onSuccess) onSuccess();
      await carregarDetalhesConta(novaConta.numeroConta);
      navigate('/nova-conta');
    } catch (err) {
      mostrarAlerta('error', err.message);
    }
  }, [abrirConta, carregarDetalhesConta, mostrarAlerta, navigate]);

  const rotasNavegacao = useMemo(() => [
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
    rotasNavegacao,
    handleNovaConta
  };
}
