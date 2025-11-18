import { getState } from '../state/store.js';

const SAMPLE_POSTS = [
  { title: 'React Suspense in Practice', status: 'AI 검증 통과' },
  { title: 'Next.js 15 Preview', status: 'AI 검토 중' }
];

const SAMPLE_COMMENTS = [
  { title: 'Rust ownership 질문', status: 'AI 검증 통과' },
  { title: 'Docker 메모리 튜닝 팁', status: 'AI 검토 중' }
];

export function initMyPageView(container) {
  const { user } = getState();
  const name = container.querySelector('[data-role="mypage-name"]');
  const email = container.querySelector('[data-role="mypage-email"]');
  const avatar = container.querySelector('[data-role="mypage-avatar"]');
  const postList = container.querySelector('[data-role="mypage-post-list"]');
  const commentList = container.querySelector('[data-role="mypage-comment-list"]');

  if (name) name.textContent = user?.userName ?? 'TrueDev Member';
  if (email) email.textContent = user?.email ?? 'user@truedev.com';
  if (avatar) avatar.textContent = user?.userName?.charAt(0)?.toUpperCase() ?? 'T';

  fillList(postList, SAMPLE_POSTS);
  fillList(commentList, SAMPLE_COMMENTS);

  focusSectionFromQuery();
}

function fillList(target, items) {
  if (!target) return;
  target.innerHTML = '';
  if (!items || items.length === 0) {
    target.innerHTML = '<li>기록이 없습니다.</li>';
    return;
  }
  items.forEach((item) => {
    const li = document.createElement('li');
    const title = document.createElement('span');
    title.textContent = item.title;
    const status = document.createElement('em');
    status.textContent = item.status;
    li.append(title, status);
    target.appendChild(li);
  });
}

function focusSectionFromQuery() {
  const params = new URLSearchParams(window.location.search);
  const section = params.get('section');
  if (!section) return;
  const target = document.getElementById(section === 'comments' ? 'mypageComments' : 'mypagePosts');
  target?.scrollIntoView({ behavior: 'smooth', block: 'start' });
}
