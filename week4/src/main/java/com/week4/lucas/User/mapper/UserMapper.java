package com.week4.lucas.User.mapper;

import com.week4.lucas.User.dto.request.AccountUpdateReq;
import com.week4.lucas.User.dto.request.UserReq;
import com.week4.lucas.User.dto.response.AccountUpdateRes;
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
    public static AccountUpdateRes toUpdateUser(User user){
        return new AccountUpdateRes(
                user.getName(),
                user.getEmail(),
                user.getProfileImage()
        );
    }

}
