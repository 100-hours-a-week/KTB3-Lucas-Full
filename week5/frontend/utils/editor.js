function getEditorConstructor() {
  return window?.toastui?.Editor;
}

function getSyntaxPlugin() {
  return window?.toastui?.Editor?.plugin?.codeSyntaxHighlight;
}

export function mountRichEditor(target, { initialValue = '', placeholder = '무엇을 공유하고 싶으신가요?' } = {}) {
  const Editor = getEditorConstructor();
  if (!Editor || !target) {
    console.warn('Toast UI Editor가 로드되지 않았습니다.');
    return null;
  }
  const plugins = [];
  const syntaxPlugin = getSyntaxPlugin();
  if (syntaxPlugin) plugins.push(syntaxPlugin);
  return new Editor({
    el: target,
    height: '520px',
    initialEditType: 'markdown',
    previewStyle: 'vertical',
    placeholder,
    initialValue,
    usageStatistics: false,
    autofocus: false,
    plugins
  });
}

export function renderMarkdown(target, value = '') {
  const Editor = getEditorConstructor();
  if (!target) return;
  if (!Editor || typeof Editor.factory !== 'function') {
    target.textContent = value;
    return;
  }
  target.innerHTML = '';
  const plugins = [];
  const syntaxPlugin = getSyntaxPlugin();
  if (syntaxPlugin) plugins.push(syntaxPlugin);
  Editor.factory({
    el: target,
    viewer: true,
    initialValue: value || '',
    usageStatistics: false,
    plugins
  });
}
