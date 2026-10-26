import { APP_TEXTS } from '../constants/texts';

const API_BASE = '/api/v1';

export async function request(endpoint, options = {}) {
  const config = {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
    ...options,
  };

  if (config.body && typeof config.body === 'object') {
    config.body = JSON.stringify(config.body);
  }

  const response = await fetch(`${API_BASE}${endpoint}`, config);
  const isJson = response.headers.get('content-type')?.includes('json');
  const data = isJson ? await response.json() : null;

  if (!response.ok) {
    const errorMsg = data?.detail || data?.message || APP_TEXTS.messages.erroGenerico;
    throw new Error(errorMsg);
  }

  return data;
}
