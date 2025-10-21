package com.week5.lucas.User;

import com.week5.lucas.User.dto.request.UserDto;

public interface UserService {
    Long signup(UserDto dto);
    User get(Long id);
    User login(String email, String password);
    void registerToken(String token, Long userId);
    void logout(String token);

    // 401 예외
    class UnauthorizedException extends RuntimeException {
        public UnauthorizedException() { super(); }
        public UnauthorizedException(String msg) { super(msg); }
    }
}
