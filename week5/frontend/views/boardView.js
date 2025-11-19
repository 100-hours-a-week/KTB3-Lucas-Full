import { fetchArticles } from '../api/articles.js';
import { renderBoardList, showBoardMessage } from '../components/boardList.js';
import { getState } from '../state/store.js';
import { navigate } from '../core/router.js';
import { resolveAIStatus, AI_STATUS } from '../utils/ai.js';

const CATEGORY_META = {
  tech: {
    label: 'Tech Talk',
    description: '최신 기술과 인사이트를 나누는 공간'
  },
  dev: {
    label: '개발자 Talk',
    description: '개발자의 고민과 자유로운 토론을 위한 공간'
  },
  career: {
    label: '취준생 Talk',
    description: '취업 준비생을 위한 커뮤니티'
  }
};
const CATEGORY_KEYS = Object.keys(CATEGORY_META);
let cachedPosts = [];
let activeCategory = 'tech';

export function initBoardView(container) {
  const params = new URLSearchParams(window.location.search);
  const requestedCategory = params.get('category');
  if (requestedCategory && CATEGORY_META[requestedCategory]) {
    activeCategory = requestedCategory;
  } else {
    activeCategory = 'tech';
  }
  const list = container.querySelector('#boardList');
  const template = container.querySelector('#board-card-template');
  const greeting = container.querySelector('[data-role="board-greeting"]');
  const sub = container.querySelector('[data-role="board-sub"]');
  const tabContainer = container.querySelector('[data-role="category-tabs"]');
  const statsNodes = {
    verified: container.querySelector('[data-role="board-stat-verified"]'),
    pending: container.querySelector('[data-role="board-stat-pending"]'),
    failed: container.querySelector('[data-role="board-stat-failed"]'),
    members: container.querySelector('[data-role="board-stat-members"]')
  };
  if (!list || !template) return;

  if (tabContainer) {
    tabContainer.addEventListener('click', (event) => {
      const tab = event.target.closest('.category-tab');
      if (!tab) return;
      const category = tab.dataset.category;
      if (!category || category === activeCategory) return;
      activeCategory = category;
      updateCategoryTabs(tabContainer);
      updateGreeting(greeting, undefined, sub);
      const url = new URL(window.location.href);
      url.searchParams.set('category', activeCategory);
      history.replaceState({}, '', url);
      renderCurrentCategory(list, template);
    });
  }

  const { user } = getState();
  updateGreeting(greeting, user, sub);

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

  loadArticles(list, template, tabContainer, statsNodes);
}

async function loadArticles(list, template, tabContainer, statsNodes) {
  showBoardMessage(list, '게시글을 불러오는 중입니다...');
  try {
    const response = await fetchArticles();
    const posts = Array.isArray(response.data) ? response.data : [];
    cachedPosts = posts.map(enhancePostMetadata);
    if (tabContainer) updateCategoryTabs(tabContainer);
    updateBoardStats(statsNodes);
    renderCurrentCategory(list, template);
  } catch (error) {
    showBoardMessage(list, error.message || '게시글을 불러오지 못했습니다.');
  }
}

function enhancePostMetadata(post) {
  const baseId = typeof post.postId === 'number' ? post.postId : Math.floor(Math.random() * 999);
  const categoryKey = CATEGORY_KEYS[baseId % CATEGORY_KEYS.length];
  const aiStatus = resolveAIStatus(post);
  return {
    ...post,
    categoryKey,
    categoryLabel: CATEGORY_META[categoryKey].label,
    aiStatus
  };
}

function filterPostsByCategory(posts) {
  return posts.filter((post) => post.categoryKey === activeCategory);
}

function updateCategoryTabs(tabContainer) {
  if (!tabContainer) return;
  tabContainer.querySelectorAll('.category-tab').forEach((tab) => {
    tab.classList.toggle('is-active', tab.dataset.category === activeCategory);
  });
}

function updateGreeting(target, user = getState().user, sub) {
  if (!target) return;
  const meta = CATEGORY_META[activeCategory];
  const username = user?.userName ? `${user.userName}님, ` : '';
  target.textContent = user
    ? `${username}${meta.label}에서 깊이 있는 인사이트를 나눠보세요.`
    : 'AI 검증 기반 개발 인사이트를 함께 만들어가요.';
  if (sub) {
    sub.textContent = `${meta.description} · AI가 검증한 정보를 확인해 보세요.`;
  }
}

function renderCurrentCategory(list, template) {
  const filtered = filterPostsByCategory(cachedPosts);
  if (filtered.length === 0) {
    showBoardMessage(list, `${CATEGORY_META[activeCategory].label} 카테고리에 아직 게시글이 없습니다.`);
    return;
  }
  renderBoardList(filtered, list, template);
}

function updateBoardStats(statsNodes = {}) {
  if (!statsNodes) return;
  const totals = cachedPosts.reduce(
    (acc, post) => {
      const status = resolveAIStatus(post);
      if (status === AI_STATUS.VERIFIED) acc.verified += 1;
      else if (status === AI_STATUS.FLAGGED) acc.failed += 1;
      else acc.pending += 1;
      return acc;
    },
    { verified: 0, pending: 0, failed: 0 }
  );
  if (statsNodes.verified) statsNodes.verified.textContent = totals.verified.toString();
  if (statsNodes.pending) statsNodes.pending.textContent = totals.pending.toString();
  if (statsNodes.failed) statsNodes.failed.textContent = totals.failed.toString();
  if (statsNodes.members) {
    const memberCount =
      cachedPosts.length > 0 ? cachedPosts.length * 8 + 300 + Math.floor(Math.random() * 45) : 320;
    statsNodes.members.textContent = memberCount.toString();
  }
}
