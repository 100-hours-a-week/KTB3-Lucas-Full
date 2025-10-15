package com.week4.lucas.User;

public class UserMapper {
    public static User toEntity(UserDto dto) {
        return User.builder()
                .email(dto.email())
                .password(dto.password())
                .nickname(dto.nickname())
                .profile_image(dto.profile_image())
                .build();
    }
}
