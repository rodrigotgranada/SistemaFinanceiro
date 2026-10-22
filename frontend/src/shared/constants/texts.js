export const APP_TEXTS = {
  headerTitle: 'Sistema Bancário',
  headerSubtitle: 'Gestão de Contas Correntes e Operações Financeiras',
  btnNovaConta: '+ Nova Conta',
  tabs: {
    extrato: 'Extrato',
    deposito: 'Depósito',
    saque: 'Saque',
    transferencia: 'Transferência',
    abrirConta: 'Nova Conta',
  },
  messages: {
    depositoSucesso: (val, prot) => `Depósito de ${val} realizado! Protocolo: ${prot}`,
    saqueSucesso: (val, prot) => `Saque de ${val} realizado! Protocolo: ${prot}`,
    transferenciaSucesso: (val, prot) => `Transferência de ${val} realizada! Protocolo: ${prot}`,
    contaAbertaSucesso: (num, titular) => `Conta ${num} aberta com sucesso para ${titular}!`,
    valorInvalido: 'Informe um valor maior que zero.',
    preenchaDestino: 'Preencha a conta de destino e um valor válido.',
    erroGenerico: 'Erro na comunicação com a API.',
  },
  operacoes: {
    DEPOSITO: 'Depósito',
    SAQUE: 'Saque',
    TRANSFERENCIA_ENVIO: 'Transf. Enviada',
    TRANSFERENCIA_RECEBIMENTO: 'Transf. Recebida',
    TRANSFERENCIA_ORIGEM: 'Transf. Enviada',
    TRANSFERENCIA_DESTINO: 'Transf. Recebida',
  },
};
