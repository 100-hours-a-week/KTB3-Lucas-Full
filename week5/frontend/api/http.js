import { getState } from '../state/store.js';

const API_BASE_URL = window.API_BASE_URL ?? deriveDefaultBaseUrl();

export async function request(path, options = {}) {
  const headers = options.headers ? { ...options.headers } : {};
  if (options.body && !(options.body instanceof FormData)) {
    headers['Content-Type'] = headers['Content-Type'] ?? 'application/json';
  }
  headers.Accept = headers.Accept ?? 'application/json';

  const { token } = getState();
  if (token && !headers.Authorization) {
    headers.Authorization = `Bearer ${token}`;
  }

  try {
    const response = await fetch(`${API_BASE_URL}${path}`, {
      method: options.method ?? 'GET',
      body: options.body,
      headers,
      mode: 'cors'
    });

    const isJson = response.headers.get('content-type')?.includes('application/json');
    const payload = isJson ? await response.json() : await response.text();
    if (!response.ok) {
      const message = typeof payload === 'object' && payload?.message
        ? payload.message
        : '요청에 실패했습니다.';
      if (response.status === 401 && message !== 'invalid_credentials') {
        document.dispatchEvent(new CustomEvent('app:unauthorized'));
      }
      const error = new Error(message);
      error.status = response.status;
      error.data = payload;
      throw error;
    }
    return payload;
  } catch (error) {
    if (error.name === 'TypeError') {
      throw new Error('서버에 연결하지 못했습니다. 백엔드가 실행 중인지 확인해주세요.');
    }
    throw error;
  }
}

function deriveDefaultBaseUrl() {
  const { protocol, hostname } = window.location;
  const port = 8080;
  return `${protocol}//${hostname}:${port}`;
}
