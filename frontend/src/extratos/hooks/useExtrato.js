import { useState, useCallback } from 'react';
import * as extratoService from '../services/extratoService';

export function useExtrato() {
  const [extrato, setExtrato] = useState(null);
  const [loadingExtrato, setLoadingExtrato] = useState(false);
  const [erroExtrato, setErroExtrato] = useState(null);

  const carregarExtrato = useCallback(async (numeroConta, dataInicio = '', dataFim = '') => {
    if (!numeroConta) return null;
    setLoadingExtrato(true);
    setErroExtrato(null);
    try {
      const data = await extratoService.consultarExtrato(numeroConta, dataInicio, dataFim);
      setExtrato(data);
      return data;
    } catch (err) {
      setErroExtrato(err.message);
      throw err;
    } finally {
      setLoadingExtrato(false);
    }
  }, []);

  const limparExtrato = useCallback(() => {
    setExtrato(null);
    setErroExtrato(null);
  }, []);

  return {
    extrato,
    loadingExtrato,
    erroExtrato,
    carregarExtrato,
    limparExtrato,
  };
}
