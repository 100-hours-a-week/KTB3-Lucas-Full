import { createArticle } from '../api/articles.js';
import { setHelperText, setLoading } from '../utils/dom.js';
import { navigate } from '../core/router.js';
import { mountRichEditor } from '../utils/editor.js';

export function initPostCreateView(container) {
  const form = container.querySelector('#postCreateForm');
  const helper = container.querySelector('[data-role="post-helper"]');
  const editorHost = container.querySelector('#postEditor');
  const hiddenTextarea = container.querySelector('#postContent');
  if (!form) return;
  setHelperText(helper, '* helper text');
  const editor = mountRichEditor(editorHost, {
    placeholder: '코드, 링크, 인사이트를 자유롭게 작성해 주세요.'
  });
  if (!editor && hiddenTextarea) {
    hiddenTextarea.hidden = false;
  }

  form.addEventListener('submit', async (event) => {
    event.preventDefault();
    const title = form.querySelector('#postTitle').value.trim();
    const contentSource = editor ? editor.getMarkdown() : hiddenTextarea?.value;
    const content = (contentSource || '').trim();

    if (!title) {
      setHelperText(helper, '제목을 입력해주세요.');
      return;
    }
    if (!content) {
      setHelperText(helper, '내용을 입력해주세요.');
      return;
    }

    const submitButton = form.querySelector('[type="submit"]');
    setLoading(submitButton, true);
    try {
      await createArticle({ title, content });
      setHelperText(helper, '게시글이 작성되었습니다.', 'success');
      form.reset();
      hiddenTextarea && (hiddenTextarea.value = '');
      if (editor) editor.setMarkdown('');
      setTimeout(() => navigate('board', { replace: true }), 800);
    } catch (error) {
      setHelperText(helper, error.message || '게시글 작성에 실패했습니다.');
    } finally {
      setLoading(submitButton, false);
    }
  });
}
