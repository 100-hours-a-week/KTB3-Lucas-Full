import { getState } from '../state/store.js';

export function initHomeView(container) {
  const greeting = container.querySelector('[data-role="home-greeting"]');
  const statPosts = container.querySelector('[data-role="stat-posts"]');
  const statReviews = container.querySelector('[data-role="stat-reviews"]');
  const statMembers = container.querySelector('[data-role="stat-members"]');
  const { user } = getState();

  if (greeting) {
    greeting.textContent = user
      ? `${user.userName}님,\nTrueDev에 오신 것을 환영합니다!`
      : 'AI 검증 기반 커뮤니티\n TrueDev에 오신 것을 환영합니다!';
    greeting.style.whiteSpace = 'pre-line';
  }

  if (statPosts) statPosts.textContent = (Math.floor(Math.random() * 50) + 120).toString();
  if (statReviews) statReviews.textContent = (Math.floor(Math.random() * 10) + 5).toString();
  if (statMembers) statMembers.textContent = (Math.floor(Math.random() * 30) + 340).toString();
}
