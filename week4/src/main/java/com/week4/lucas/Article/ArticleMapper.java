package com.week4.lucas.Article;

import com.week4.lucas.Article.dto.request.ArticleDto;
import com.week4.lucas.Article.dto.response.ArticleDetailRes;
import com.week4.lucas.Article.dto.response.ArticleSummaryRes;
import com.week4.lucas.Article.dto.response.AuthorRes;

import java.time.Instant;

public class ArticleMapper {

    public static Article toEntity(ArticleDto.CreateArticleReq req) {
        Article a = new Article();
        a.setUserId(req.userId());
        a.setTitle(req.title());
        a.setContent(req.content());
        a.setFileName(req.fileName());
        a.setLikes(0);
        a.setViews(0);
        a.setCreatedAt(Instant.now());
        a.setAuthorUserName("더미 작성자 " + req.userId());
        a.setAuthorProfileImage(null);
        return a;
    }

    public static Comment toEntity(Long articleId, ArticleDto.CreateCommentReq req) {
        Comment c = new Comment();
        c.setArticleId(articleId);
        c.setUserId(req.userId());
        c.setContent(req.content());
        c.setCreatedAt(Instant.now());
        c.setUserName("댓글 작성자");
        c.setUserProfileImage("");
        return c;
    }

    public static ArticleSummaryRes toSummary(Article a) {
        return new ArticleSummaryRes(
                a.getId(),
                a.getTitle(),
                a.getLikes(),
                a.getViews(),
                a.getCreatedAt(),
                new AuthorRes(a.getAuthorUserName(), a.getAuthorProfileImage() == null ? "" : a.getAuthorProfileImage())
        );
    }

    public static ArticleDetailRes toArticleDetail(Article a) {
        return new ArticleDetailRes(
                a.getId(),
                a.getUserId(),
                a.getTitle(),
                a.getContent(),
                a.getFileName(),
                a.getLikes(),
                a.getViews(),
                a.getCreatedAt(),
                a.getAuthorUserName(),
                a.getAuthorProfileImage()
        );
    }

}