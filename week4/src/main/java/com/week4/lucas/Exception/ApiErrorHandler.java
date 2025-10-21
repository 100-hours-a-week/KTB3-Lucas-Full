package com.week4.lucas.Exception;

import com.week4.lucas.User.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 예외 매핑 (검증 400 / 기타 500)
@RestControllerAdvice
public class ApiErrorHandler {

    @ExceptionHandler({ MethodArgumentNotValidException.class, IllegalArgumentException.class })
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("invalid_request"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleServerError(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("internal_server_error"));
    }
}