package com.week4.lucas.Article.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ArticleReq {
    @Schema(name = "글 생성 요청")
    public record CreateArticleReq(
            @NotNull Long userId,
            @NotBlank String title,
            @NotBlank String content
    ) {}
    @Schema(name = "글 수정 요청")
    public record EditArticleReq(
            @NotNull Long userId,
            String title,
            String content
    ) {}

}
