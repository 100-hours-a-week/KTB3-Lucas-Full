package com.week4.lucas.Comment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CommentReq {
    @Schema(name = "댓글 생성 요청")
    public record CreateCommentReq(
            @NotNull Long userId,
            @NotBlank String content
    ) {}
    @Schema(name = "댓글 수정 요청")
    public record EditCommentReq(
            @NotNull Long userId,
            @NotBlank String content
    ) {}
}
