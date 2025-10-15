package com.week4.lucas.User;

import jakarta.validation.constraints.*;

public record UserDto(
        @Email @NotBlank String email,
        @NotBlank @Size(min = 6, max = 100) String password,
        @NotBlank String nickname,
        String profile_image // 선택가능
) {}