package com.week4.lucas.Comment.dto.response;

import com.week4.lucas.base.dto.response.AuthorRes;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "댓글 응답")
public record CommentRes(
        Long id,
        Long postId,
        String content,
        LocalDateTime createdAt,
        LocalDateTime editedAt,
        AuthorRes author
) {}
