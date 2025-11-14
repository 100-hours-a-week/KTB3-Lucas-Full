package com.week4.lucas.Article.dto.response;


import com.week4.lucas.base.dto.response.AuthorRes;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "게시글 목록 응답")
public record ArticleSummaryRes(
        Long postId,
        String title,
        Integer likeCount,
        Integer viewCount,
        Integer commentCount,
        LocalDateTime createdAt,
        LocalDateTime editedAt,
        AuthorRes author
) {}
