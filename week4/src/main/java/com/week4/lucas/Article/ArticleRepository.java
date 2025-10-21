package com.week4.lucas.Article;

import java.util.List;

public interface ArticleRepository {
    Article saveArticle(Article a);
    Article findArticle(Long id);
    boolean deleteArticle(Long id);
    List<Article> findPage(int page, int size);
    void increaseViews(Long id);
    Article addLike(Long id, int delta);

    Comment saveComment(Comment c);
    Comment findComment(Long id);
    boolean deleteComment(Long id);
    List<Comment> findCommentsByArticle(Long articleId);
    int countCommentsByArticle(Long articleId);
}