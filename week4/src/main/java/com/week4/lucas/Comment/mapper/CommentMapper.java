package com.week4.lucas.Comment.mapper;

import com.week4.lucas.Article.entity.Article;
import com.week4.lucas.base.dto.response.AuthorRes;
import com.week4.lucas.Comment.dto.request.CommentReq;
import com.week4.lucas.Comment.dto.response.CommentRes;
import com.week4.lucas.Comment.entity.Comment;
import com.week4.lucas.User.entity.User;

public class CommentMapper {
    public static Comment toEntity(Article article, User user, CommentReq.CreateCommentReq req) {
        return Comment.builder()
                .article(article)
                .user(user)
                .content(req.content())
                .build();
    }


    public static CommentRes toRes(Comment c){
        String userName = (c.getUser() != null) ? c.getUser().getName() : null;
        String userImg  = (c.getUser() != null) ? c.getUser().getProfileImage() : null;

        return new CommentRes(
                c.getId(),
                c.getArticle() != null ? c.getArticle().getId() : null,
                c.getContent(),
                c.getCommentCreatedAt(),
                c.getCommentEditedAt(),
                new AuthorRes(userName,userImg)
        );
    }
}
