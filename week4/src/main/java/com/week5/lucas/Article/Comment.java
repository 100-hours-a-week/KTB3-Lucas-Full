package com.week5.lucas.Article;


import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Comment {
    private Long id;
    private Long articleId;
    private Integer userId;
    private String content;
    private Instant createdAt;
    private String userName;
    private String userProfileImage;
}
