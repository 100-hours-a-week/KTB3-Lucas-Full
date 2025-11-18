import { fetchArticleDetail, updateArticle } from '../api/articles.js';
import { setHelperText, setLoading } from '../utils/dom.js';
import { navigate } from '../core/router.js';
import { mountRichEditor } from '../utils/editor.js';

export async function initPostEditView(container) {
  const form = container.querySelector('#postEditForm');
  const helper = container.querySelector('[data-role="post-helper"]');
  const editorHost = container.querySelector('#postEditEditor');
  const hiddenTextarea = container.querySelector('#postEditContent');
  if (!form) return;
  if (helper) setHelperText(helper, '* helper text');
  const editor = mountRichEditor(editorHost, {
    placeholder: '수정할 내용을 입력해주세요.'
  });
  if (!editor && hiddenTextarea) {
    hiddenTextarea.hidden = false;
  }

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
    populateForm(form, data, editor, hiddenTextarea);
  } catch (error) {
    setHelperText(helper, error.message || '게시글 정보를 불러오지 못했습니다.');
    form.querySelectorAll('input, textarea, button[type="submit"]').forEach((el) => {
      el.disabled = true;
    });
    return;
  }

  form.addEventListener('submit', (event) =>
    handleSubmit({ event, form, helper, articleId, editor, hiddenTextarea })
  );
}

function populateForm(form, article, editor, textarea) {
  form.querySelector('#postEditTitle').value = article?.title ?? '';
  if (editor) {
    editor.setMarkdown(article?.content ?? '');
  } else if (textarea) {
    textarea.value = article?.content ?? '';
  }
}

async function handleSubmit({ event, form, helper, articleId, editor, hiddenTextarea }) {
  event.preventDefault();
  const titleInput = form.querySelector('#postEditTitle');
  const title = titleInput.value.trim();
  const contentSource = editor ? editor.getMarkdown() : hiddenTextarea?.value;
  const content = (contentSource || '').trim();

  if (!title) {
    setHelperText(helper, '제목을 입력해주세요.');
    titleInput.focus();
    return;
  }
  if (!content) {
    setHelperText(helper, '내용을 입력해주세요.');
    editor?.focus();
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
