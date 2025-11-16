import { getState, clearAuth } from '../state/store.js';
import { setHelperText, setLoading } from '../utils/dom.js';
import { updateAccount, deleteAccount, logout as requestLogout } from '../api/auth.js';
import { navigate } from '../core/router.js';

export function initProfileEditView(container) {
  const form = container.querySelector('#profileEditForm');
  const helper = container.querySelector('[data-role="profile-helper"]');
  const preview = container.querySelector('[data-role="profile-preview"]');
  const withdrawButton = container.querySelector('.profile-edit__withdraw');
  if (!form) return;
  populateForm(form, preview);
  if (helper) setHelperText(helper, '* helper text');

  form.addEventListener('submit', (event) => {
    event.preventDefault();
    handleSubmit(form, helper, preview);
  });

  if (withdrawButton) {
    withdrawButton.addEventListener('click', handleWithdraw);
  }
}

function populateForm(form, preview) {
  const { user } = getState();
  form.querySelector('#profileEmail').value = user?.email ?? '';
  form.querySelector('#profileNickname').value = user?.userName ?? '';
  if (preview) {
    if (user?.profileImage) {
      preview.style.backgroundImage = `url(${user.profileImage})`;
      preview.textContent = '';
    } else {
      preview.style.backgroundImage = 'none';
      preview.textContent = user?.userName?.charAt(0)?.toUpperCase() ?? '?';
    }
  }
}

async function handleSubmit(form, helper, preview) {
  const nickname = form.querySelector('#profileNickname').value.trim();
  if (!nickname) {
    setHelperText(helper, '닉네임을 입력해주세요.');
    return;
  }
  const submitButton = form.querySelector('.primary-button');
  setLoading(submitButton, true);
  try {
    const response = await updateAccount({ name: nickname, profileImage: null });
    const { data } = response;
    const state = getState();
    if (state.user) {
      state.user.userName = data.name;
      state.user.email = data.email;
    }
    setHelperText(helper, '회원 정보가 수정되었습니다.', 'success');
    populateForm(form, preview);
  } catch (error) {
    setHelperText(helper, error.message || '회원 정보 수정에 실패했습니다.');
  } finally {
    setLoading(submitButton, false);
  }
}

async function handleWithdraw() {
  if (!window.confirm('회원 탈퇴를 진행하시겠습니까?')) return;
  try {
    await deleteAccount();
    await requestLogout();
  } catch (error) {
    window.alert(error.message || '회원 탈퇴 처리에 실패했습니다.');
    return;
  }
  clearAuth();
  window.alert('회원 탈퇴가 완료되었습니다.');
  navigate('login', { replace: true });
}
