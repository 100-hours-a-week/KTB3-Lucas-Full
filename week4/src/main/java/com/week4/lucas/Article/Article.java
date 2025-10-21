package com.week4.lucas.Article;

import lombok.*;
import java.time.Instant;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class Article {
    private Long id;
    private Integer userId;          // 작성자 id
    private String title;
    private String content;
    private String fileName;
    private int likes;
    private int views;
    private Instant createdAt;
    private String authorUserName;
    private String authorProfileImage;
}
