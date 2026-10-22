import { useState, useCallback, useMemo, useEffect } from 'react';
import * as contaService from '../services/contaService';

export function useContas() {
  const [contas, setContas] = useState([]);
  const [contaSelecionada, setContaSelecionada] = useState(null);
  const [loadingContas, setLoadingContas] = useState(false);
  const [erroContas, setErroContas] = useState(null);

  const carregarDetalhesConta = useCallback(async (numeroConta) => {
    try {
      const conta = await contaService.obterConta(numeroConta);
      setContaSelecionada(conta);
      return conta;
    } catch (err) {
      setErroContas(err.message);
      return null;
    }
  }, []);

  const carregarContas = useCallback(async (selecionarPrimeira = false) => {
    setLoadingContas(true);
    setErroContas(null);
    try {
      const data = await contaService.listarContas();
      setContas(data);
      if (data.length > 0 && (selecionarPrimeira || !contaSelecionada)) {
        await carregarDetalhesConta(data[0].numeroConta);
      }
      return data;
    } catch (err) {
      setErroContas(err.message);
      return [];
    } finally {
      setLoadingContas(false);
    }
  }, [contaSelecionada, carregarDetalhesConta]);

  const selecionarConta = useCallback(async (contaOuNumero) => {
    if (!contaOuNumero) return;
    const numero = typeof contaOuNumero === 'object' ? contaOuNumero.numeroConta : contaOuNumero;
    if (numero) {
      await carregarDetalhesConta(numero);
    }
  }, [carregarDetalhesConta]);

  const atualizarSaldoContaSelecionada = useCallback((novoSaldo) => {
    setContaSelecionada(prev => prev ? { ...prev, saldo: novoSaldo } : prev);
  }, []);

  const abrirConta = useCallback(async (formData) => {
    setLoadingContas(true);
    setErroContas(null);
    try {
      const novaConta = await contaService.criarConta(formData);
      await carregarContas();
      await carregarDetalhesConta(novaConta.numeroConta);
      return novaConta;
    } catch (err) {
      setErroContas(err.message);
      throw err;
    } finally {
      setLoadingContas(false);
    }
  }, [carregarContas, carregarDetalhesConta]);

  useEffect(() => {
    carregarContas(true);
  }, []);

  const contasOrdenadas = useMemo(() => {
    return [...contas].sort((a, b) => a.numeroConta.localeCompare(b.numeroConta));
  }, [contas]);

  return {
    contas: contasOrdenadas,
    contaSelecionada,
    loadingContas,
    erroContas,
    carregarContas,
    carregarDetalhesConta,
    selecionarConta,
    atualizarSaldoContaSelecionada,
    abrirConta,
  };
}
