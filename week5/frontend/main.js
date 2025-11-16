import { setupRouter, navigate } from './core/router.js';
import { initHeaderControls, refreshHeader } from './components/header.js';
import { logout as requestLogout } from './api/auth.js';
import { clearAuth, hasAuth } from './state/store.js';

let forcedLogoutInProgress = false;

function bootstrap() {
  initHeaderControls({ onLogout: handleLogout });
  document.addEventListener('app:unauthorized', handleUnauthorizedSession);
  setupRouter();
}

async function handleLogout({ skipRequest = false } = {}) {
  if (!hasAuth()) return;
  try {
    if (!skipRequest) {
      await requestLogout();
    }
  } catch (error) {
    console.warn('logout_failed', error);
  } finally {
    clearAuth();
    refreshHeader();
    navigate('login', { replace: true });
  }
}

function handleUnauthorizedSession() {
  if (forcedLogoutInProgress) return;
  forcedLogoutInProgress = true;
  window.alert('세션이 만료되었습니다. 다시 로그인해주세요.');
  handleLogout({ skipRequest: true }).finally(() => {
    forcedLogoutInProgress = false;
  });
}

bootstrap();
