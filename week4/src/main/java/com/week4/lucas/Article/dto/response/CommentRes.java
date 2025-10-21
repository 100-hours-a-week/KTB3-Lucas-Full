package com.week4.lucas.Article.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "댓글 응답")
public record CommentRes(
        Long id,
        Long postId,
        Integer userId,
        String content,
        Instant createdAt,
        String userName,
        String userProfileImage
) {}
