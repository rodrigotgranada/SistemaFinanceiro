import { useState, useCallback } from 'react';
import * as movimentacaoService from '../services/movimentacaoService';

export function useMovimentacoes() {
  const [loading, setLoading] = useState(false);
  const [erro, setErro] = useState(null);

  const depositar = useCallback(async (numeroConta, valor) => {
    setLoading(true);
    setErro(null);
    try {
      const resultado = await movimentacaoService.depositar(numeroConta, valor);
      return resultado;
    } catch (err) {
      setErro(err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  const sacar = useCallback(async (numeroConta, valor) => {
    setLoading(true);
    setErro(null);
    try {
      const resultado = await movimentacaoService.sacar(numeroConta, valor);
      return resultado;
    } catch (err) {
      setErro(err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  const transferir = useCallback(async (dadosTransferencia) => {
    setLoading(true);
    setErro(null);
    try {
      const resultado = await movimentacaoService.transferir(dadosTransferencia);
      return resultado;
    } catch (err) {
      setErro(err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  return {
    loadingMovimentacao: loading,
    erroMovimentacao: erro,
    depositar,
    sacar,
    transferir,
  };
}
