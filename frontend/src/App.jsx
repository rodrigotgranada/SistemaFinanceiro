import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { BankingProvider } from './shared/context/BankingContext';
import AppRoutes from './routes/AppRoutes';

export default function App() {
  return (
    <BrowserRouter>
      <BankingProvider>
        <AppRoutes />
      </BankingProvider>
    </BrowserRouter>
  );
}
