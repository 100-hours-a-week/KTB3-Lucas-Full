package com.week4.lucas.Article.dto.response;

import com.week4.lucas.base.dto.response.AuthorRes;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "게시글 상세 응답")
public record ArticleDetailRes(
        Long postId,
        String title,
        String content,
        int likeCount,
        int viewCount,
        int commentCount,
        LocalDateTime createdAt,
        LocalDateTime editedAt,
        AuthorRes author,
        boolean likedByMe
) {}