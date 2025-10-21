package com.week4.lucas.User.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "로그인 요청")
public record LoginRequest(String email, String password) {}