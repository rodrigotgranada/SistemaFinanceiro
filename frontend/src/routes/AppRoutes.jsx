import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import DashboardLayout from '../shared/ui/DashboardLayout';
import ExtratoTab from '../extratos/components/ExtratoTab';
import DepositoTab from '../movimentacoes/components/DepositoTab';
import SaqueTab from '../movimentacoes/components/SaqueTab';
import TransferenciaTab from '../movimentacoes/components/TransferenciaTab';
import NovaContaTab from '../contas/components/NovaContaTab';

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<DashboardLayout />}>
        <Route index element={<Navigate to="/extrato" replace />} />
        <Route path="extrato" element={<ExtratoTab />} />
        <Route path="deposito" element={<DepositoTab />} />
        <Route path="saque" element={<SaqueTab />} />
        <Route path="transferencia" element={<TransferenciaTab />} />
        <Route path="nova-conta" element={<NovaContaTab />} />
        <Route path="*" element={<Navigate to="/extrato" replace />} />
      </Route>
    </Routes>
  );
}
