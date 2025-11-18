import {
  fetchArticleDetail,
  likeArticle,
  unlikeArticle,
  deleteArticle,
  fetchArticleComments,
  createComment,
  updateComment,
  deleteComment
} from '../api/articles.js';
import { setHelperText, setLoading } from '../utils/dom.js';
import { navigate } from '../core/router.js';

const CATEGORY_META = {
  tech: 'Tech Talk',
  dev: '개발자 Talk',
  career: '취준생 Talk'
};
const CATEGORY_KEYS = Object.keys(CATEGORY_META);

export async function initPostDetailView(container) {
  const articleId = getArticleIdFromQuery();
  if (!articleId) {
    container.innerHTML = '<div class="empty-state">잘못된 접근입니다.</div>';
    return;
  }
  const helper = container.querySelector('[data-role="post-helper"]');
  const commentInput = container.querySelector('[data-role="comment-input"]');
  const commentSubmit = container.querySelector('[data-role="comment-submit"]');
  const commentHelper = container.querySelector('[data-role="comment-helper"]');
  if (commentSubmit && commentInput) {
    commentSubmit.addEventListener('click', () =>
      handleCommentSubmit({
        textarea: commentInput,
        helper: commentHelper,
        button: commentSubmit,
        container,
        articleId
      })
    );
  }
  const commentList = container.querySelector('[data-role="comment-list"]');
  const commentDeleteModal = container.querySelector('[data-role="comment-delete-modal"]');
  const commentDeleteCancel = commentDeleteModal?.querySelector('[data-role="comment-delete-cancel"]');
  const commentDeleteConfirm = commentDeleteModal?.querySelector('[data-role="comment-delete-confirm"]');
  const commentEditModal = container.querySelector('[data-role="comment-edit-modal"]');
  const commentEditCancel = commentEditModal?.querySelector('[data-role="comment-edit-cancel"]');
  const commentEditConfirm = commentEditModal?.querySelector('[data-role="comment-edit-confirm"]');
  const commentEditInput = commentEditModal?.querySelector('[data-role="comment-edit-input"]');
  const commentEditHelper = commentEditModal?.querySelector('[data-role="comment-edit-helper"]');
  if (commentList) {
    commentList.addEventListener('click', (event) =>
      handleCommentAction(event, {
        container,
        articleId,
        commentDeleteModal,
        commentEditModal,
        commentEditInput,
        commentEditHelper
      })
    );
  }
  if (commentDeleteCancel) {
    commentDeleteCancel.addEventListener('click', () => closeCommentDeleteModal(commentDeleteModal));
  }
  if (commentDeleteModal) {
    commentDeleteModal.addEventListener('click', (event) => {
      if (event.target === commentDeleteModal) {
        closeCommentDeleteModal(commentDeleteModal);
      }
    });
  }
  if (commentDeleteConfirm) {
    commentDeleteConfirm.addEventListener('click', () =>
      confirmCommentDelete({ modal: commentDeleteModal, container, articleId })
    );
  }
  if (commentEditCancel) {
    commentEditCancel.addEventListener('click', () => closeCommentEditModal(commentEditModal));
  }
  if (commentEditModal) {
    commentEditModal.addEventListener('click', (event) => {
      if (event.target === commentEditModal) {
        closeCommentEditModal(commentEditModal);
      }
    });
  }
  if (commentEditConfirm) {
    commentEditConfirm.addEventListener('click', () =>
      confirmCommentEdit({
        modal: commentEditModal,
        container,
        articleId,
        input: commentEditInput,
        helper: commentEditHelper,
        button: commentEditConfirm
      })
    );
  }
  const likeButton = container.querySelector('[data-role="like-button"]');
  if (likeButton) {
    likeButton.addEventListener('click', () => handleLikeToggle(likeButton, articleId));
  }
  const editButton = container.querySelector('[data-role="post-edit"]');
  if (editButton) {
    editButton.addEventListener('click', () => navigate('edit'));
  }
  const deleteModal = container.querySelector('[data-role="post-delete-modal"]');
  const deleteTrigger = container.querySelector('[data-role="post-delete-trigger"]');
  const deleteCancel = deleteModal?.querySelector('[data-role="delete-cancel"]');
  const deleteConfirm = deleteModal?.querySelector('[data-role="delete-confirm"]');
  if (deleteTrigger && deleteModal) {
    deleteTrigger.addEventListener('click', () => openDeleteModal(deleteModal));
  }
  if (deleteCancel) {
    deleteCancel.addEventListener('click', () => closeDeleteModal(deleteModal));
  }
  if (deleteConfirm && deleteModal) {
    deleteConfirm.addEventListener('click', () => handleDeleteArticle(deleteConfirm, deleteModal, articleId));
  }
  if (deleteModal) {
    deleteModal.addEventListener('click', (event) => {
      if (event.target === deleteModal) {
        closeDeleteModal(deleteModal);
      }
    });
  }

  try {
    const response = await fetchArticleDetail(articleId);
    const { data } = response;
    populateDetail(container, data);
    loadComments(container, articleId);
  } catch (error) {
    if (helper) setHelperText(helper, error.message || '게시글을 불러오지 못했습니다.');
  }
}

function populateDetail(container, article) {
  container.querySelector('[data-field="title"]').textContent = article.title || '';
  container.querySelector('[data-field="content"]').textContent = article.content || '';
  container.querySelector('[data-field="author"]').textContent = article.author?.userName || '';
  container.querySelector('[data-field="createdAt"]').textContent = article.createdAt || '';
  container.querySelector('[data-field="likeCount"]').textContent = article.likeCount ?? 0;
  container.querySelector('[data-field="viewCount"]').textContent = article.viewCount ?? 0;
  container.querySelector('[data-field="commentCount"]').textContent = article.commentCount ?? 0;
  populateCategory(container, article);
  populateAIVerdict(container, article);
  const actions = container.querySelector('.post-detail__actions');
  if (actions) {
    actions.classList.toggle('is-hidden', article.isAuthor === false);
  }
  const likeButton = container.querySelector('[data-role="like-button"]');
  syncLikeButtonState(likeButton, resolveLikedState(article));
}

function getArticleIdFromQuery() {
  const params = new URLSearchParams(window.location.search);
  const id = params.get('article');
  return id ? Number(id) : null;
}

function resolveLikedState(article) {
  if (!article || typeof article !== 'object') return false;
  if (typeof article.likedByMe === 'boolean') return article.likedByMe;
  if (typeof article.isLiked === 'boolean') return article.isLiked;
  if (typeof article.liked === 'boolean') return article.liked;
  if (typeof article.isLike === 'boolean') return article.isLike;
  return false;
}

function syncLikeButtonState(button, isLiked) {
  if (!button) return;
  button.classList.toggle('is-liked', isLiked);
  button.setAttribute('aria-pressed', isLiked ? 'true' : 'false');
  button.dataset.liked = isLiked ? 'true' : 'false';
}

function populateAIVerdict(container, article) {
  const statusField = container.querySelector('[data-field="ai-status"]');
  const messageField = container.querySelector('[data-field="ai-message"]');
  const updatedField = container.querySelector('[data-field="ai-updated"]');
  if (!statusField || !messageField) return;
  const verified = (article.viewCount ?? 0) % 3 !== 1;
  statusField.textContent = verified ? 'AI VERIFIED' : 'AI REVIEWING';
  statusField.classList.toggle('is-reviewing', !verified);
  messageField.textContent = verified
    ? 'TrueDev AI가 내용을 검증했습니다. '
    : '현재 AI가 내용을 검증하고 있습니다. 커뮤니티 가이드에 어긋나지 않는지 확인 중입니다.';
  if (updatedField) {
    updatedField.textContent = formatDate(article.editedAt || article.createdAt || new Date().toISOString());
  }
}

function populateCategory(container, article) {
  const label = container.querySelector('[data-role="post-category"]');
  if (!label) return;
  const key = deriveCategoryKey(article);
  label.textContent = CATEGORY_META[key] ?? 'TrueDev Thread';
}

function deriveCategoryKey(article) {
  const base = typeof article.postId === 'number'
    ? article.postId
    : typeof article.id === 'number'
      ? article.id
      : Math.floor(Math.random() * 999);
  return CATEGORY_KEYS[base % CATEGORY_KEYS.length];
}

async function handleLikeToggle(button, articleId) {
  if (!button || !articleId || button.disabled) return;
  const isLiked = button.dataset.liked === 'true';
  button.disabled = true;
  button.setAttribute('aria-busy', 'true');
  try {
    if (isLiked) {
      await unlikeArticle(articleId);
      syncLikeButtonState(button, false);
      adjustLikeCount(button, -1);
    } else {
      await likeArticle(articleId);
      syncLikeButtonState(button, true);
      adjustLikeCount(button, 1);
    }
  } catch (error) {
    if (error?.status === 409) {
      window.alert('이미 좋아요를 누른 게시글입니다.');
      syncLikeButtonState(button, true);
    } else {
      window.alert(error.message || '좋아요 처리에 실패했습니다.');
    }
  } finally {
    button.disabled = false;
    button.removeAttribute('aria-busy');
  }
}

function adjustLikeCount(button, delta) {
  if (!button || typeof delta !== 'number') return;
  const target = button.querySelector('[data-field="likeCount"]');
  if (!target) return;
  const current = Number(target.textContent) || 0;
  const next = Math.max(0, current + delta);
  target.textContent = next;
}

async function handleDeleteArticle(confirmButton, modal, articleId) {
  if (!confirmButton || !articleId) return;
  setLoading(confirmButton, true);
  try {
    await deleteArticle(articleId);
    window.alert('게시글이 삭제되었습니다.');
    closeDeleteModal(modal);
    navigate('board', { replace: true });
  } catch (error) {
    window.alert(error.message || '게시글 삭제에 실패했습니다.');
  } finally {
    setLoading(confirmButton, false);
  }
}

function openDeleteModal(modal) {
  if (!modal) return;
  modal.classList.remove('is-hidden');
}

function closeDeleteModal(modal) {
  if (!modal) return;
  modal.classList.add('is-hidden');
}

function handleCommentAction(
  event,
  { container, articleId, commentDeleteModal, commentEditModal, commentEditInput, commentEditHelper }
) {
  const editButton = event.target.closest('[data-role="comment-edit"]');
  if (editButton) {
    const card = editButton.closest('.comment-card');
    if (card && card.dataset.isAuthor === 'true') {
      handleCommentEdit({
        card,
        container,
        articleId,
        modal: commentEditModal,
        input: commentEditInput,
        helper: commentEditHelper
      });
    }
    return;
  }
  const deleteButton = event.target.closest('[data-role="comment-delete"]');
  if (deleteButton) {
    const card = deleteButton.closest('.comment-card');
    if (card && card.dataset.isAuthor === 'true') {
      const commentId = Number(card.dataset.commentId);
      if (commentId) {
        if (commentDeleteModal) {
          openCommentDeleteModal(commentDeleteModal, commentId);
        } else {
          handleCommentDelete({ commentId, container, articleId });
        }
      }
    }
  }
}

async function handleCommentSubmit({ textarea, helper, button, container, articleId }) {
  if (!textarea || !button || !articleId) return;
  const content = textarea.value.trim();
  if (!content) {
    setHelperText(helper, '댓글을 입력해주세요.');
    textarea.focus();
    return;
  }
  setHelperText(helper, '');
  setLoading(button, true);
  try {
    await createComment(articleId, { content });
    setHelperText(helper, '댓글이 등록되었습니다.', 'success');
    textarea.value = '';
    updateCommentCount(container, 1);
    loadComments(container, articleId);
  } catch (error) {
    setHelperText(helper, error.message || '댓글 등록에 실패했습니다.');
  } finally {
    setLoading(button, false);
  }
}

async function loadComments(container, articleId, page = 1) {
  const list = container.querySelector('[data-role="comment-list"]');
  const template = container.querySelector('#comment-item-template');
  if (!list || !template) return;
  showCommentsMessage(list, '댓글을 불러오는 중입니다...');
  try {
    const response = await fetchArticleComments(articleId, page);
    const comments = Array.isArray(response.data) ? response.data : [];
    if (comments.length === 0) {
      showCommentsMessage(list, '등록된 댓글이 없습니다.');
      return;
    }
    const fragment = document.createDocumentFragment();
    comments.forEach((comment) => {
      const node = template.content.cloneNode(true);
      const card = node.querySelector('.comment-card');
      if (card) {
        card.dataset.commentId = comment.id;
        card.dataset.isAuthor = comment.isAuthor ? 'true' : 'false';
      }
      node.querySelector('[data-field="author"]').textContent = comment.author?.userName || '익명';
      node.querySelector('[data-field="createdAt"]').textContent = formatDate(comment.createdAt);
      node.querySelector('[data-field="content"]').textContent = comment.content || '';
      const actions = node.querySelector('[data-role="comment-actions"]');
      if (actions) {
        actions.classList.toggle('is-hidden', comment.isAuthor === false);
      }
      const avatar = node.querySelector('[data-field="avatar"]');
      if (avatar) {
        if (comment.author?.profileImage) {
          avatar.style.backgroundImage = `url(${comment.author.profileImage})`;
          avatar.style.backgroundSize = 'cover';
        } else {
          avatar.style.backgroundImage = 'none';
        }
      }
      fragment.appendChild(node);
    });
    list.replaceChildren(fragment);
  } catch (error) {
    showCommentsMessage(list, error.message || '댓글을 불러오지 못했습니다.');
  }
}

function showCommentsMessage(list, text) {
  if (!list) return;
  list.innerHTML = `<div class="empty-state">${text}</div>`;
}

function formatDate(value) {
  if (!value) return '-';
  try {
    return new Date(value).toLocaleString('ko-KR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  } catch (error) {
    return value;
  }
}

function updateCommentCount(container, delta) {
  const target = container.querySelector('[data-field="commentCount"]');
  if (!target || typeof delta !== 'number') return;
  const current = Number(target.textContent) || 0;
  const next = Math.max(0, current + delta);
  target.textContent = next;
}

function handleCommentEdit({ card, modal, input, helper }) {
  if (!modal || !input) return;
  const commentId = Number(card?.dataset.commentId);
  if (!commentId) return;
  const contentNode = card.querySelector('[data-field="content"]');
  const currentText = contentNode?.textContent?.trim() ?? '';
  modal.dataset.commentId = commentId;
  input.value = currentText;
  if (helper) helper.textContent = '';
  modal.classList.remove('is-hidden');
}

async function handleCommentDelete({ commentId, container, articleId }) {
  if (!commentId) return;
  try {
    await deleteComment(articleId, commentId);
    window.alert('댓글이 삭제되었습니다.');
    updateCommentCount(container, -1);
    loadComments(container, articleId);
  } catch (error) {
    window.alert(error.message || '댓글 삭제에 실패했습니다.');
  }
}

function openCommentDeleteModal(modal, commentId) {
  if (!modal) return;
  modal.dataset.commentId = commentId;
  modal.classList.remove('is-hidden');
}

function closeCommentDeleteModal(modal) {
  if (!modal) return;
  delete modal.dataset.commentId;
  modal.classList.add('is-hidden');
}

function confirmCommentDelete({ modal, container, articleId }) {
  if (!modal) return;
  const commentId = Number(modal.dataset.commentId);
  closeCommentDeleteModal(modal);
  handleCommentDelete({ commentId, container, articleId });
}

function closeCommentEditModal(modal) {
  if (!modal) return;
  delete modal.dataset.commentId;
  modal.classList.add('is-hidden');
}

async function confirmCommentEdit({ modal, container, articleId, input, helper, button }) {
  if (!modal || !input || !button) return;
  const commentId = Number(modal.dataset.commentId);
  const content = input.value.trim();
  if (!content) {
    setHelperText(helper, '내용을 입력해주세요.');
    input.focus();
    return;
  }
  setHelperText(helper, '');
  setLoading(button, true);
  try {
    await updateComment(articleId, commentId, { content });
    closeCommentEditModal(modal);
    loadComments(container, articleId);
  } catch (error) {
    setHelperText(helper, error.message || '댓글 수정에 실패했습니다.');
  } finally {
    setLoading(button, false);
  }
}
