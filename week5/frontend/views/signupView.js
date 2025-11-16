import { signup } from '../api/auth.js';
import { setHelperText, setLoading } from '../utils/dom.js';
import { navigate } from '../core/router.js';

export function initSignupView(container) {
  const form = container.querySelector('#signupForm');
  const helper = container.querySelector('[data-role="signup-helper"]');
  if (!form) return;

  setHelperText(helper, ' ');

  form.addEventListener('submit', async (event) => {
    event.preventDefault();
    const email = form.querySelector('#signupEmail').value.trim();
    const password = form.querySelector('#signupPassword').value;
    const confirm = form.querySelector('#signupPasswordConfirm').value;
    const name = form.querySelector('#signupName').value.trim();

    if (!email || !password || !name) {
      setHelperText(helper, '필수 항목을 모두 입력해주세요.');
      return;
    }
    if (!isValidEmail(email)) {
      setHelperText(helper, '올바른 이메일 형식을 입력해주세요.');
      return;
    }
    if (!isValidSignupPassword(password)) {
      setHelperText(helper, '비밀번호는 8~72자 사이여야 합니다.');
      return;
    }
    if (password !== confirm) {
      setHelperText(helper, '비밀번호가 일치하지 않습니다.');
      return;
    }
    if (!isValidName(name)) {
      setHelperText(helper, '닉네임은 2~20글자 사이여야 합니다.');
      return;
    }

    const payload = {
      email,
      password,
      name,
      profileImage: ''
    };

    const submitButton = form.querySelector('[type="submit"]');
    setLoading(submitButton, true);
    try {
      await signup(payload);
      setHelperText(helper, '회원가입이 완료되었습니다. 로그인 해주세요.', 'success');
      form.reset();
      setTimeout(() => navigate('login', { replace: true }), 800);
    } catch (error) {
      setHelperText(helper, error.message || '회원가입에 실패했습니다.');
    } finally {
      setLoading(submitButton, false);
    }
  });
}

function isValidEmail(value) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);
}

function isValidSignupPassword(value) {
  return typeof value === 'string' && value.length >= 8 && value.length <= 72;
}

function isValidName(value) {
  return typeof value === 'string' && value.length >= 2 && value.length <= 20;
}
