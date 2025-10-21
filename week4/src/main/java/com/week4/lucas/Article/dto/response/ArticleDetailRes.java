package com.week4.lucas.Article.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "게시글 상세 응답")
public record ArticleDetailRes(
        Long postId,
        Integer userId,
        String title,
        String content,
        String fileName,
        int likes,
        int views,
        Instant createdAt,
        String authorUserName,
        String authorProfileImage
) {}