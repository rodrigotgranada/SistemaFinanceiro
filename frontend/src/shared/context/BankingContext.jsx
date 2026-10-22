import React, { createContext, useContext } from 'react';
import { useBankingDashboard } from '../hooks/useBankingDashboard';

const BankingContext = createContext(null);

export function BankingProvider({ children }) {
  const value = useBankingDashboard();
  return (
    <BankingContext.Provider value={value}>
      {children}
    </BankingContext.Provider>
  );
}

export function useBanking() {
  const context = useContext(BankingContext);
  return context;
}
