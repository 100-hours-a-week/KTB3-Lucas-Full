package com.week4.lucas.Article;

import com.week4.lucas.Article.dto.request.ArticleDto;

import java.util.List;

public interface ArticleService {
    List<Article> list(int page, int size);
    Article create(ArticleDto.CreateArticleReq req);
    Article detail(Long articleId, boolean increaseViews);
    Article edit(Long articleId, ArticleDto.EditArticleReq req) throws ForbiddenException;
    boolean delete(Long articleId);

    Comment createComment(Long articleId, ArticleDto.CreateCommentReq req);
    Comment editComment(Long articleId, Long commentId, ArticleDto.EditCommentReq req);
    boolean deleteComment(Long articleId, Long commentId);

    //  좋아요/취소
    Article like(Long articleId);
    Article unlike(Long articleId);

    class ForbiddenException extends RuntimeException {}
}