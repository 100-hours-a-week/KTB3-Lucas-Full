package com.week4.lucas.Article.service;

import com.week4.lucas.Article.dto.request.ArticleReq;
import com.week4.lucas.Article.dto.response.ArticleDetailRes;
import com.week4.lucas.Article.dto.response.ArticlePageRes;
import com.week4.lucas.Article.dto.response.ArticleSummaryRes;
import com.week4.lucas.Article.entity.Article;

import java.util.List;

public interface ArticleService {
    ArticlePageRes list(int page, int size);
    ArticleDetailRes create(Long userId, ArticleReq.CreateArticleReq req);
    ArticleDetailRes detail(Long userId, Long articleId, boolean increaseViews);
    ArticleDetailRes edit(Long articleId, Long userId, ArticleReq.EditArticleReq req) throws ForbiddenException;
    boolean delete(Long articleId,Long userId);

    //  좋아요/취소
    boolean like(Long articleId, Long userId);
    boolean unlike(Long articleId, Long userId);

    class ForbiddenException extends RuntimeException {}
}
