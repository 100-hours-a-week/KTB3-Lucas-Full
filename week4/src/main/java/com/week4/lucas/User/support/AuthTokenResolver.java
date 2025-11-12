package com.week4.lucas.User.support;

import com.week4.lucas.User.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class AuthTokenResolver {

    private static final String BEARER_PREFIX = "Bearer ";
    private final UserService userService;

    public Long requireUserId(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new UserService.UnauthorizedException();
        }
        String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();
        if (!StringUtils.hasText(token)) {
            throw new UserService.UnauthorizedException();
        }
        return userService.resolveUserIdByToken(token);
    }
}
