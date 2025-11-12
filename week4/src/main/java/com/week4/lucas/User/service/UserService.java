package com.week4.lucas.User.service;

import com.week4.lucas.User.entity.User;
import com.week4.lucas.User.dto.request.UserReq;

public interface UserService {
    Long signup(UserReq dto);

    User get(Long id);

    User login(String email, String password); // 로그인 실패시 UnauthorizedException

    void registerToken(String token, Long userId);

    void logout(String token);

    Long resolveUserIdByToken(String token);

    class UnauthorizedException extends RuntimeException { }
}
