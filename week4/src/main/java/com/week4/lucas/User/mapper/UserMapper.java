package com.week4.lucas.User.mapper;

import com.week4.lucas.User.dto.request.UserReq;
import com.week4.lucas.User.entity.User;

import static com.week4.lucas.User.service.UserServiceImpl.sha256;

public class UserMapper {
    public static User toEntity(UserReq dto) {
        return User.builder()
                .email(dto.email())
                .passwordHash(sha256(dto.password()))
                .name(dto.name())
                .profileImage(dto.profileImage())   // null 가능
                .build();
    }
}
