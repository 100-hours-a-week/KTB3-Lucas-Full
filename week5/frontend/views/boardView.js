import { fetchArticles } from '../api/articles.js';
import { renderBoardList, showBoardMessage } from '../components/boardList.js';
import { getState } from '../state/store.js';
import { navigate } from '../core/router.js';

export function initBoardView(container) {
  const list = container.querySelector('#boardList');
  const template = container.querySelector('#board-card-template');
  const greeting = container.querySelector('[data-role="board-greeting"]');
  if (!list || !template) return;

  const { user } = getState();
  if (greeting) {
    greeting.textContent = user
      ? `안녕하세요,\n${user.userName}님! 아무 말 대잔치 게시판 입니다.`
      : '안녕하세요,\n아무 말 대잔치 게시판 입니다.';
  }

  list.addEventListener('click', (event) => {
    const card = event.target.closest('.board-card');
    if (!card) return;
    const articleId = card.dataset.articleId;
    if (!articleId) return;
    const url = new URL(window.location.href);
    url.searchParams.set('article', articleId);
    history.replaceState({}, '', url);
    navigate('post');
  });

  loadArticles(list, template);
}

async function loadArticles(list, template) {
  showBoardMessage(list, '게시글을 불러오는 중입니다...');
  try {
    const response = await fetchArticles();
    const posts = Array.isArray(response.data) ? response.data : [];
    renderBoardList(posts, list, template);
  } catch (error) {
    showBoardMessage(list, error.message || '게시글을 불러오지 못했습니다.');
  }
}
