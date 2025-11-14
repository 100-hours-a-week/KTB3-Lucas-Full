package com.week4.lucas.Comment.service;

import com.week4.lucas.Comment.dto.request.CommentReq;
import com.week4.lucas.Comment.dto.response.CommentRes;
import com.week4.lucas.Comment.entity.Comment;
import java.util.List;

public interface CommentService {

    CommentRes createComment(Long articleId, Long userId, CommentReq.CreateCommentReq req);

    CommentRes editComment(Long articleId, Long commentId, Long userId, CommentReq.EditCommentReq req);

    List<Comment> getCommentList(Long articleId, int page, int size);

    boolean deleteComment(Long articleId, Long commentId, Long userId);
}
