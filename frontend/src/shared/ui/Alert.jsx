import React from 'react';

export default function Alert({ tipo, mensagem, onClose }) {
  if (!mensagem) return null;

  const isSuccess = tipo === 'success';

  return (
    <div 
      className={`mb-4 px-4 py-3 rounded border text-sm flex justify-between items-center ${
        isSuccess 
          ? 'bg-green-50 border-green-200 text-green-800' 
          : 'bg-red-50 border-red-200 text-red-800'
      }`}
    >
      <span>{mensagem}</span>
      {onClose && (
        <button 
          type="button" 
          onClick={onClose} 
          className="ml-3 text-lg font-bold leading-none hover:opacity-75 focus:outline-none"
        >
          &times;
        </button>
      )}
    </div>
  );
}
