import React from 'react';
import { APP_TEXTS } from '../constants/texts';

export default function Header({ onNovaConta }) {
  return (
    <header className="bg-white border-b border-gray-200 px-6 py-3 flex justify-between items-center">
      <div>
        <h1 className="text-lg font-bold text-gray-900">{APP_TEXTS.headerTitle}</h1>
        <p className="text-xs text-gray-500">{APP_TEXTS.headerSubtitle}</p>
      </div>
      <button 
        type="button" 
        onClick={onNovaConta}
        className="px-3 py-1.5 text-xs font-semibold text-blue-600 border border-blue-600 rounded hover:bg-blue-600 hover:text-white transition-colors"
      >
        {APP_TEXTS.btnNovaConta}
      </button>
    </header>
  );
}
