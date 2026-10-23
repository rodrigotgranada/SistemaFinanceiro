import React, { useCallback, useMemo } from 'react';
import { formatarMoeda } from '../utils/formatters';

export default function MoneyInput({ 
  value = 0, 
  onChange, 
  disabled = false,
  required = false,
  id,
  name,
  className = ''
}) {
  const displayValue = useMemo(() => {
    return formatarMoeda(value || 0);
  }, [value]);

  const handleKeyDown = useCallback((e) => {
    if (disabled) return;

    if (e.key >= '0' && e.key <= '9') {
      e.preventDefault();
      const currentCents = Math.round((Number(value) || 0) * 100);
      const newCents = currentCents * 10 + parseInt(e.key, 10);
      if (newCents <= 99999999999) {
        onChange(newCents / 100);
      }
    } else if (e.key === 'Backspace') {
      e.preventDefault();
      const currentCents = Math.round((Number(value) || 0) * 100);
      const newCents = Math.floor(currentCents / 10);
      onChange(newCents / 100);
    } else if (e.key === 'Delete') {
      e.preventDefault();
      onChange(0);
    }
  }, [value, onChange, disabled]);

  const handlePaste = useCallback((e) => {
    if (disabled) return;
    e.preventDefault();
    const text = e.clipboardData.getData('text');
    const digits = text.replace(/\D/g, '');
    if (digits) {
      const cents = parseInt(digits, 10);
      onChange(cents / 100);
    }
  }, [onChange, disabled]);

  return (
    <input
      type="text"
      inputMode="numeric"
      id={id}
      name={name}
      disabled={disabled}
      required={required}
      value={displayValue}
      onKeyDown={handleKeyDown}
      onPaste={handlePaste}
      onChange={() => {}}
      className={`w-full px-3 py-1.5 bg-white border border-gray-300 rounded text-sm text-gray-900 font-mono focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 ${className}`}
    />
  );
}
