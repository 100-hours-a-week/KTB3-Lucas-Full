package com.week4.lucas.Exception;

import com.week4.lucas.User.dto.response.ApiResponse;
import com.week4.lucas.User.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 예외 매핑 (검증 400 / 기타 500)
@RestControllerAdvice
public class ApiErrorHandler {

    @ExceptionHandler({ MethodArgumentNotValidException.class })
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("invalid_request"));
    }

    @ExceptionHandler(UserService.UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(UserService.UnauthorizedException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("unauthorized"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleServerError(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("internal_server_error"));
    }

    @ExceptionHandler(UserService.InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidCredentials(UserService.InvalidCredentialsException ex) {
        // 로그인 실패 → 401 Unauthorized
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ex.getMessage())); // "invalid_credentials"
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        // currentPassword_unauthorized, password_duplicated, 그 외 등등 전부 400
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }
}

