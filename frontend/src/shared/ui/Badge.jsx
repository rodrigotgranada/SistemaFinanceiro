import React from 'react';

export default function Badge({ variant = 'default', children }) {
  const variants = {
    default: 'bg-gray-100 text-gray-700 border-gray-200',
    success: 'bg-green-100 text-green-800 border-green-200',
    danger: 'bg-red-100 text-red-800 border-red-200',
    info: 'bg-blue-100 text-blue-800 border-blue-200',
  };

  const currentClass = variants[variant] || variants.default;

  return (
    <span className={`inline-block px-2 py-0.5 text-xs font-semibold rounded border whitespace-nowrap ${currentClass}`}>
      {children}
    </span>
  );
}
