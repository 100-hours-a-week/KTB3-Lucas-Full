package com.week4.lucas.Article;

import java.time.Instant;

public class ArticleMapper {

    public static Article toEntity(ArticleDto.CreateArticleReq req) {
        Article a = new Article();
        a.setUser_id(req.user_id());
        a.setTitle(req.title());
        a.setContent(req.content());
        a.setFile_name(req.file_name());
        a.setLikes(0);
        a.setViews(0);
        a.setCreated_at(Instant.now());
        a.setAuthor_user_name("더미 작성자 " + req.user_id());
        a.setAuthor_profile_image(null);
        return a;
    }

    public static Comment toEntity(Long articleId, ArticleDto.CreateCommentReq req) {
        Comment c = new Comment();
        c.setArticle_id(articleId);
        c.setUser_id(req.user_id());
        c.setContent(req.content());
        c.setCreated_at(Instant.now());
        c.setUser_name("댓글 작성자");
        c.setUser_profile_image("");
        return c;
    }
}