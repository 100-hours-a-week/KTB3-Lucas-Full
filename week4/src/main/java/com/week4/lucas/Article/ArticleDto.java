package com.week4.lucas.Article;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ArticleDto {

    public record CreateArticleReq(
            @NotNull Integer user_id,
            @NotBlank String title,
            @NotBlank String content,
            @NotBlank String file_name
    ) {}

    public record EditArticleReq(
            @NotNull Integer user_id,
            String title,
            String content,
            String file_name
    ) {}

    public record CreateCommentReq(
            @NotNull Integer user_id,
            @NotBlank String content
    ) {}

    public record EditCommentReq(
            @NotBlank String content
    ) {}
}
