package com.week4.lucas.Article.controller;

import com.week4.lucas.Article.mapper.ArticleMapper;
import com.week4.lucas.Article.service.ArticleService;
import com.week4.lucas.Article.dto.request.ArticleReq;
import com.week4.lucas.Article.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Tag(name = "Article", description = "Article API")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService service;



    // 목록
    @Operation(summary = "특정 페이시 게시글 목록 불러오기")
    @GetMapping("/articles")
    public ResponseEntity<Object> list(@RequestParam(defaultValue = "1") int page) {
        if (page < 1)  return ResponseEntity.status(BAD_REQUEST).body(ApiResponse.error("invalid_request"));
        int size = 10;
        var articles = service.list(page, size);
        var posts = articles.stream().map(ArticleMapper::toSummary).toList();
        return ResponseEntity.ok(ApiResponse.ok("post_list_success",posts));
    }

    // 작성 (201)
    @Operation(summary = "글 작성")
    @PostMapping("/articles")
    public ResponseEntity<Object> create(@Valid @RequestBody ArticleReq.CreateArticleReq req) {
        var a = service.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("post_created_success", a));
    }

    // 상세
    @Operation(summary = "게시글 조회")
    @GetMapping("/articles/{article_id}")
    public ResponseEntity<Object> detail(@PathVariable("article_id") Long id) {
        var a = service.detail(id, true);
        if (a == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("not_found"));

        return ResponseEntity.ok(ApiResponse.ok("post_detail_success",ArticleMapper.toArticleDetail(a)));
    }

    // 수정
    @Operation(summary = "게시글 수정")
    @PatchMapping("/articles/{article_id}")
    public ResponseEntity<Object> edit(@PathVariable("article_id") Long id,
                                       @Valid @RequestBody ArticleReq.EditArticleReq req) {
        try {
            var a = service.edit(id, req);
            if (a == null)  return ResponseEntity.status(NOT_FOUND).body(ApiResponse.error("Edited_failed"));

            var data = ArticleMapper.toArticleDetail(a);
            return ResponseEntity.ok(ApiResponse.ok("Edited_success",data));
        } catch (ArticleService.ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("FORBIDDEN"));
        }
    }

    // 삭제
    @Operation(summary = "게시글 삭제")
    @DeleteMapping("/articles/{article_id}")
    public ResponseEntity<Object> delete(@PathVariable("article_id") Long id) {
        boolean ok = service.delete(id);
        if (!ok) return ResponseEntity.status(BAD_REQUEST).body(ApiResponse.error("Delete_failed"));
        return ResponseEntity.ok(ApiResponse.ok("Delete_Success"));
    }



    // 좋아요
    @Operation(summary = "좋아요")
    @PostMapping("/articles/{article_id}/{user_id}/likes")
    public ResponseEntity<Object> like(@PathVariable("article_id") Long article_id,
                                       @PathVariable("user_id") Long user_id)
    {
        boolean a = service.like(article_id,user_id);
        if (!a) return ResponseEntity.status(NOT_FOUND).body(ApiResponse.error("like_failed"));
        return ResponseEntity.ok(ApiResponse.ok("like_success"));
    }

    //좋아요 취소

    @Operation(summary = "좋아요 취소")
    @DeleteMapping("/articles/{article_id}/{user_id}/likes")
    public ResponseEntity<Object> unlike(@PathVariable("article_id") Long article_id,
                                         @PathVariable("user_id") Long user_id) {
        boolean a = service.unlike(article_id,user_id);
        if (!a) return ResponseEntity.status(NOT_FOUND).body(ApiResponse.error("unlike_failed"));
        return ResponseEntity.ok(ApiResponse.ok("unlike_success"));
    }

}