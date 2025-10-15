package com.week4.lucas.Article;

import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping
public class ArticleController {

    private final ArticleService service;

    public ArticleController(ArticleService service) {
        this.service = service;
    }


    // 목록
    @GetMapping("/articles")
    public ResponseEntity<Object> list(@RequestParam(defaultValue = "1") int page) {
        if (page < 1) return bad("Bad Request");

        var result = service.list(page, 10);
        var posts = result.stream().map(a -> Map.of(
                "post_id", a.getId(),
                "title", a.getTitle(),
                "likes", a.getLikes(),
                "views", a.getViews(),
                "created_at", a.getCreated_at().toString(),
                "author", Map.of(
                        "user_name", a.getAuthor_user_name(),
                        "profile_image", a.getAuthor_profile_image()
                )
        )).collect(Collectors.toList());

        return ok("post_list_success", Map.of("posts", posts));
    }

    // 작성 (201)
    @PostMapping("/articles")
    public ResponseEntity<Object> create(@Valid @RequestBody ArticleDto.CreateArticleReq req) {
        var a = service.create(req);
        var data = new LinkedHashMap<String, Object>();
        data.put("post_id", a.getId());
        data.put("user_id", a.getUser_id());
        data.put("title", a.getTitle());
        data.put("content", a.getContent());
        data.put("file_name", a.getFile_name());
        data.put("created_at", a.getCreated_at().toString());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "post_created_success", "data", data));
    }

    // 상세
    @GetMapping("/articles/{article_id}")
    public ResponseEntity<Object> detail(@PathVariable("article_id") Long id) {
        var a = service.detail(id, true);
        if (a == null) return notFound();

        var data = Map.of(
                "author", Map.of(
                        "user_name", a.getAuthor_user_name(),
                        "profile_image", a.getAuthor_profile_image() == null ? "" : a.getAuthor_profile_image()
                ),
                "post", Map.of(
                        "title", a.getTitle(),
                        "content", a.getContent(),
                        "likes", a.getLikes(),
                        "views", a.getViews()
                ),
                "comments", List.of() // 댓글 목록은 필요 시 Service/Repo에서 받아 채워도 됨
        );
        return ok("post_detail_success", data);
    }

    // 수정
    @PatchMapping("/articles/{article_id}")
    public ResponseEntity<Object> edit(@PathVariable("article_id") Long id,
                                       @Valid @RequestBody ArticleDto.EditArticleReq req) {
        try {
            var a = service.edit(id, req);
            if (a == null) return notFound();

            var data = Map.of(
                    "author", Map.of(
                            "user_name", a.getAuthor_user_name(),
                            "profile_image", a.getAuthor_profile_image() == null ? "" : a.getAuthor_profile_image()
                    ),
                    "post", Map.of(
                            "title", a.getTitle(),
                            "content", a.getContent(),
                            "likes", a.getLikes(),
                            "views", a.getViews()
                    ),
                    "comments", List.of()
            );
            return ok("article_detail_success", data);
        } catch (ArticleService.ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Forbidden"));
        }
    }

    // 삭제
    @DeleteMapping("/articles/{article_id}")
    public ResponseEntity<Object> delete(@PathVariable("article_id") Long id) {
        boolean ok = service.delete(id);
        if (!ok) return notFound();
        return okSimple("delete_success");
    }


    // 댓글 작성
    @PostMapping("/articles/{article_id}/comments")
    public ResponseEntity<Object> createComment(@PathVariable("article_id") Long articleId,
                                                @Valid @RequestBody ArticleDto.CreateCommentReq req) {
        var c = service.createComment(articleId, req);
        if (c == null) return notFound();

        var data = new LinkedHashMap<String, Object>();
        data.put("post_id", c.getArticle_id());
        data.put("user_id", c.getUser_id());
        data.put("content", c.getContent());
        data.put("created_at", c.getCreated_at().toString());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "comment_created_success", "data", data));
    }

    // 댓글 수정
    @PatchMapping("/articles/{article_id}/comments/{comment_id}")
    public ResponseEntity<Object> editComment(@PathVariable("article_id") Long articleId,
                                              @PathVariable("comment_id") Long commentId,
                                              @Valid @RequestBody ArticleDto.EditCommentReq req) {
        var c = service.editComment(articleId, commentId, req);
        if (c == null) return notFound();

        var data = new LinkedHashMap<String, Object>();
        data.put("post_id", c.getArticle_id());
        data.put("user_id", c.getUser_id());
        data.put("content", c.getContent());
        data.put("created_at", c.getCreated_at().toString());

        return ok("comment_edited_success", data);
    }

    // 댓글 삭제
    @DeleteMapping("/articles/{article_id}/comments/{comment_id}")
    public ResponseEntity<Object> deleteComment(@PathVariable("article_id") Long articleId,
                                                @PathVariable("comment_id") Long commentId) {
        boolean ok = service.deleteComment(articleId, commentId);
        if (!ok) return notFound();
        return okSimple("delete_success");
    }

    // 좋아요
    @PostMapping("/articles/{article_id}/likes")
    public ResponseEntity<Object> like(@PathVariable("article_id") Long id) {
        var a = service.like(id);
        if (a == null) return notFound();
        return okSimple("like_success");
    }
    //좋아요 취소
    @DeleteMapping("/articles/{article_id}/likes")
    public ResponseEntity<Object> unlike(@PathVariable("article_id") Long id) {
        var a = service.unlike(id);
        if (a == null) return notFound();
        return okSimple("unlike_success");
    }

    private static ResponseEntity<Object> ok(String message, Object data){
        return ResponseEntity.ok(Map.of("message", message, "data", data));
    }
    private static ResponseEntity<Object> okSimple(String message){
        return ResponseEntity.ok(Map.of("message", message));
    }
    private static ResponseEntity<Object> bad(String message){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", message));
    }
    private static ResponseEntity<Object> notFound(){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Not Found"));
    }
}