package com.week4.lucas.Comment.service;

import com.week4.lucas.Comment.dto.request.CommentReq;
import com.week4.lucas.Comment.dto.response.CommentRes;

public interface CommentService {

    CommentRes createComment(Long articleId, Long userId, CommentReq.CreateCommentReq req);

    CommentRes editComment(Long articleId, Long commentId, Long userId, CommentReq.EditCommentReq req);

    boolean deleteComment(Long articleId, Long commentId, Long userId);
}
