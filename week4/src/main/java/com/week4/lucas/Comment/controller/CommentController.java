package com.week4.lucas.Comment.controller;

import com.week4.lucas.Article.dto.response.ApiResponse;
import com.week4.lucas.Comment.service.CommentService;
import com.week4.lucas.Comment.dto.request.CommentReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Comment", description = "Comment API")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class CommentController {
    private final CommentService service;

    // 댓글 작성
    @Operation(summary = "댓글 작성")
    @PostMapping("/articles/{article_id}/comments")
    public ResponseEntity<Object> createComment(@PathVariable("article_id") Long articleId,
                                                @Valid @RequestBody CommentReq.CreateCommentReq req) {
        var res = service.createComment(articleId, req);
        if (res == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("comment_created_failed"));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("comment_created_success", res));
    }

    // 댓글 수정
    @Operation(summary = "댓글 수정")
    @PatchMapping("/articles/{article_id}/comments/{comment_id}")
    public ResponseEntity<Object> editComment(@PathVariable("article_id") Long articleId,
                                              @PathVariable("comment_id") Long commentId,
                                              @Valid @RequestBody CommentReq.EditCommentReq req) {
        var res = service.editComment(articleId, commentId, req);
        if (res == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("comment_edited_failed"));
        return ResponseEntity.ok(ApiResponse.ok("comment_edited_success", res));
    }

    // 댓글 삭제
    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/articles/{article_id}/comments/{comment_id}")
    public ResponseEntity<Object> deleteComment(@PathVariable("article_id") Long articleId,
                                                @PathVariable("comment_id") Long commentId) {
        boolean ok = service.deleteComment(articleId, commentId);
        if (!ok) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("delete_failed"));
        return ResponseEntity.ok(ApiResponse.ok("delete_success"));
    }
}
