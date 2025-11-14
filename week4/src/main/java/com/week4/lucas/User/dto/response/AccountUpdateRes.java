package com.week4.lucas.User.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AccountUpdateRes", description = "회원 정보 수정 응답")
public record AccountUpdateRes(
        String name,
        String email,
        String profileImage
) {}
