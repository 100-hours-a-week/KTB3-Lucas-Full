package com.week4.lucas.Article.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
@Schema(description = "게시글 목록 응답")
public record ArticleSummaryRes(
        Long postId,
        String title,
        Integer likes,
        Integer views,
        Instant createdAt,
        AuthorRes author
) {}
