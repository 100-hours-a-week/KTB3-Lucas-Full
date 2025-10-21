package com.week4.lucas.User.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

// 로그인 성공 응답
@Schema(name = "로그인 성공 응답")
public record LoginSuccess(String message, String token, LoginUser user) { }