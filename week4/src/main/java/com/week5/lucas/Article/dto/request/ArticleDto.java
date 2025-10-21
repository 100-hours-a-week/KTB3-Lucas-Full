package com.week5.lucas.Article.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ArticleDto {
    @Schema(name = "글 생성 요청")
    public record CreateArticleReq(
            @NotNull Integer userId,
            @NotBlank String title,
            @NotBlank String content,
            @NotBlank String fileName
    ) {}
    @Schema(name = "글 수정 요청")
    public record EditArticleReq(
            @NotNull Integer userId,
            String title,
            String content,
            String fileName
    ) {}
    @Schema(name = "댓글 생성 요청")
    public record CreateCommentReq(
            @NotNull Integer userId,
            @NotBlank String content
    ) {}
    @Schema(name = "댓글 수정 요청")
    public record EditCommentReq(
            @NotBlank String content
    ) {}
}
