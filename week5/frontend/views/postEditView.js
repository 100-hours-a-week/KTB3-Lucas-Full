import { fetchArticleDetail, updateArticle } from '../api/articles.js';
import { setHelperText, setLoading } from '../utils/dom.js';
import { navigate } from '../core/router.js';

export async function initPostEditView(container) {
  const form = container.querySelector('#postEditForm');
  const helper = container.querySelector('[data-role="post-helper"]');
  if (!form) return;
  if (helper) setHelperText(helper, '* helper text');

  const articleId = getArticleIdFromQuery();
  if (!articleId) {
    setHelperText(helper, '잘못된 접근입니다.');
    form.querySelectorAll('input, textarea, button[type="submit"]').forEach((el) => {
      el.disabled = true;
    });
    return;
  }

  try {
    const response = await fetchArticleDetail(articleId);
    const { data } = response;
    populateForm(form, data);
  } catch (error) {
    setHelperText(helper, error.message || '게시글 정보를 불러오지 못했습니다.');
    form.querySelectorAll('input, textarea, button[type="submit"]').forEach((el) => {
      el.disabled = true;
    });
    return;
  }

  form.addEventListener('submit', (event) => handleSubmit(event, form, helper, articleId));
}

function populateForm(form, article) {
  form.querySelector('#postEditTitle').value = article?.title ?? '';
  form.querySelector('#postEditContent').value = article?.content ?? '';
}

async function handleSubmit(event, form, helper, articleId) {
  event.preventDefault();
  const titleInput = form.querySelector('#postEditTitle');
  const contentInput = form.querySelector('#postEditContent');
  const title = titleInput.value.trim();
  const content = contentInput.value.trim();

  if (!title) {
    setHelperText(helper, '제목을 입력해주세요.');
    titleInput.focus();
    return;
  }
  if (!content) {
    setHelperText(helper, '내용을 입력해주세요.');
    contentInput.focus();
    return;
  }

  const submitButton = form.querySelector('button[type="submit"]');
  setLoading(submitButton, true);
  try {
    await updateArticle(articleId, { title, content });
    setHelperText(helper, '게시글이 수정되었습니다.', 'success');
    setTimeout(() => navigate('post', { replace: true }), 800);
  } catch (error) {
    setHelperText(helper, error.message || '게시글 수정에 실패했습니다.');
  } finally {
    setLoading(submitButton, false);
  }
}

function getArticleIdFromQuery() {
  const params = new URLSearchParams(window.location.search);
  const id = params.get('article');
  return id ? Number(id) : null;
}
