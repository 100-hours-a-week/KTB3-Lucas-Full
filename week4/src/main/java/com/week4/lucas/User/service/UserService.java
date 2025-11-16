package com.week4.lucas.User.service;

import com.week4.lucas.User.dto.response.AccountUpdateRes;
import com.week4.lucas.User.dto.response.LoginUser;
import com.week4.lucas.User.entity.User;
import com.week4.lucas.User.dto.request.UserReq;
import com.week4.lucas.User.dto.request.AccountUpdateReq;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public interface UserService {
    Long signup(UserReq dto);

    LoginUser get(Long id);

    LoginUser login(String email, String password,String token); // 로그인 실패시 UnauthorizedException

    void registerToken(String token, Long userId);

    void logout(String token);

    Long resolveUserIdByToken(String token);

    boolean deleteAccount(Long userId);

    AccountUpdateRes updateAccount(Long userId, AccountUpdateReq req);

    void changePassword(Long userId,String currentPassword,String newPassword);

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    class UnauthorizedException extends RuntimeException {
        public UnauthorizedException() {
            super("unauthorized");

        }
    }
}
