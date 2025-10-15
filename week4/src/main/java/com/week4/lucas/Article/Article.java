package com.week4.lucas.Article;

import lombok.*;
import java.time.Instant;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class Article {
    private Long id;
    private Integer user_id;          // 작성자 id
    private String title;
    private String content;
    private String file_name;
    private int likes;
    private int views;
    private Instant created_at;
    private String author_user_name;
    private String author_profile_image;
}

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
class Comment {
    private Long id;
    private Long article_id;
    private Integer user_id;
    private String content;
    private Instant created_at;
    private String user_name;
    private String user_profile_image;
}
