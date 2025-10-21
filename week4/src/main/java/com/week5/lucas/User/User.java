package com.week5.lucas.User;

import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor

public class User {
    private Long id;
    private String email;
    private String password;
    private String nickname;
    private String profileImage; // nullable
}