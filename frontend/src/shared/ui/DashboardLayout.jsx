import React from 'react';
import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import Header from './Header';
import Alert from './Alert';
import ContaCard from '../../contas/components/ContaCard';
import { useBanking } from '../context/BankingContext';

export default function DashboardLayout() {
  const navigate = useNavigate();
  const {
    feedback,
    fecharAlerta,
    contas,
    contaSelecionada,
    selecionarConta,
    rotasNavegacao
  } = useBanking();

  return (
    <div className="min-h-screen bg-slate-50 text-gray-900 font-sans">
      <Header onNovaConta={() => navigate('/nova-conta')} />

      <main className="max-w-5xl mx-auto px-4 py-5">
        <Alert 
          tipo={feedback.tipo} 
          mensagem={feedback.mensagem} 
          onClose={fecharAlerta} 
        />

        <div className="grid grid-cols-1 md:grid-cols-[300px_1fr] gap-5">
          <ContaCard 
            contas={contas}
            contaSelecionada={contaSelecionada}
            onSelecionarConta={selecionarConta}
          />

          <div>
            <nav className="flex space-x-1 border-b border-gray-200 mb-4">
              {rotasNavegacao.map(rota => {
                const desabilitado = rota.precisaConta && !contaSelecionada;

                if (desabilitado) {
                  return (
                    <span
                      key={rota.path}
                      className="py-2 px-4 text-xs font-semibold border-b-2 border-transparent text-gray-400 cursor-not-allowed select-none"
                    >
                      {rota.label}
                    </span>
                  );
                }

                return (
                  <NavLink
                    key={rota.path}
                    to={rota.path}
                    className={({ isActive }) =>
                      `py-2 px-4 text-xs font-semibold border-b-2 transition-colors ${
                        isActive
                          ? 'border-blue-600 text-blue-600 bg-white'
                          : 'border-transparent text-gray-600 hover:text-gray-900 hover:border-gray-300'
                      }`
                    }
                  >
                    {rota.label}
                  </NavLink>
                );
              })}
            </nav>

            <Outlet />
          </div>
        </div>
      </main>
    </div>
  );
}
