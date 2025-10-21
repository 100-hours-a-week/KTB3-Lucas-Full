package com.week4.lucas.User;

import com.week4.lucas.User.dto.request.UserDto;

public class UserMapper {
    public static User toEntity(UserDto dto) {
        return User.builder()
                .email(dto.email())
                .password(dto.password())
                .nickname(dto.nickname())
                .profileImage(dto.profileImage())
                .build();
    }
}
