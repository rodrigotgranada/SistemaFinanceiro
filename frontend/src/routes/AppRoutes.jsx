import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import DashboardLayout from '../shared/ui/DashboardLayout';
import NovaContaTab from '../contas/components/NovaContaTab';

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<DashboardLayout />}>
        <Route index element={<Navigate to="/nova-conta" replace />} />
        <Route path="nova-conta" element={<NovaContaTab />} />
        <Route path="*" element={<Navigate to="/nova-conta" replace />} />
      </Route>
    </Routes>
  );
}
