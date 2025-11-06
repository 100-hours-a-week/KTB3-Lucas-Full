package com.week4.lucas.Article.service;

import com.week4.lucas.Article.dto.request.ArticleReq;
import com.week4.lucas.Article.entity.Article;

import java.util.List;

public interface ArticleService {
    List<Article> list(int page, int size);
    Article create(ArticleReq.CreateArticleReq req);
    Article detail(Long articleId, boolean increaseViews);
    Article edit(Long articleId, ArticleReq.EditArticleReq req) throws ForbiddenException;
    boolean delete(Long articleId);

    //  좋아요/취소
    boolean like(Long articleId,Long userId);
    boolean unlike(Long articleId,Long userId);

    class ForbiddenException extends RuntimeException {}
}