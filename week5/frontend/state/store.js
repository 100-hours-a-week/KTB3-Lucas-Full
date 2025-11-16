const STORAGE_TOKEN = 'lucas_auth_token';
const STORAGE_USER = 'lucas_auth_user';

const persistedToken = localStorage.getItem(STORAGE_TOKEN) || '';
const persistedUser = (() => {
  try {
    const raw = localStorage.getItem(STORAGE_USER);
    return raw ? JSON.parse(raw) : null;
  } catch (error) {
    console.warn('Failed to parse stored user', error);
    return null;
  }
})();

const state = {
  token: persistedToken,
  user: persistedUser,
  profileImage: ''
};

export function getState() {
  return state;
}

export function hasAuth() {
  return Boolean(state.token && state.user);
}

export function setProfileImage(src) {
  state.profileImage = src;
}

export function clearProfileImage() {
  state.profileImage = '';
}

export function persistAuth(token, user) {
  state.token = token;
  state.user = user;
  localStorage.setItem(STORAGE_TOKEN, token ?? '');
  localStorage.setItem(STORAGE_USER, JSON.stringify(user ?? null));
}

export function clearAuth() {
  state.token = '';
  state.user = null;
  localStorage.removeItem(STORAGE_TOKEN);
  localStorage.removeItem(STORAGE_USER);
}

export function getProfileImage() {
  return state.profileImage;
}
