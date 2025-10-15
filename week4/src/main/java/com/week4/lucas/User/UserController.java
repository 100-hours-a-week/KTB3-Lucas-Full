package com.week4.lucas.User;

import org.apache.logging.log4j.message.SimpleMessage;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import jakarta.validation.Valid;

import java.util.UUID;
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;
    public UserController(UserService service) { this.service = service; }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResult>> signup(@Valid @RequestBody UserDto dto) {
        Long id = service.signup(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("register_success", new SignupResult(id)));
    }

    // 단순 조회 테스트용
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> get(@PathVariable Long id) {
        User u = service.get(id);
        return ResponseEntity.ok(ApiResponse.ok("ok", u));
    }
    @PostMapping("/login")
    public ResponseEntity<LoginSuccess> login(@RequestBody LoginRequest req) {

        if (req == null || !StringUtils.hasText(req.email) || !StringUtils.hasText(req.password)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new LoginSuccess("Bad Request", null, null));
        }

        User u = service.login(req.email, req.password); // 401 발생 시 아래 Advice가 처리
        String token = UUID.randomUUID().toString().replace("-", ""); // 임시 토큰
        service.registerToken(token, u.getId());
        LoginUser user = new LoginUser(u.getId(), u.getNickname()); // user_name = nickname
        return ResponseEntity.ok(new LoginSuccess("login_success", token, user));
    }
    @PostMapping("/logout")
    public ResponseEntity<Object> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        try {

            if (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new SimpleMessage("Bad Request"));
            }
            String token = authorization.substring("Bearer ".length()).trim();
            if (!StringUtils.hasText(token)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new SimpleMessage("Bad Request"));
            }

            // 토큰 검증/삭제 (없으면 401)
            service.logout(token);

            // 성공 200
            return ResponseEntity.ok(new SimpleMessage("Logout_success"));

        } catch (UserService.UnauthorizedException e) {
            // 인증 실패 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new SimpleMessage("Unauthorized"));
        } catch (Exception e) {
            // 기타 오류 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new SimpleMessage("Internal Server Error"));
        }
    }




    // 예외 매핑 (검증 400 / 기타 500)
    @RestControllerAdvice
    static class ApiErrorHandler {

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
    static class LoginRequest {
        public String email;
        public String password;
    }
    // 로그인 성공 응답
    static record LoginSuccess(String message, String token, LoginUser user) { }
    static record LoginUser(Long user_id, String user_name) { }
    // 가입 성공 응답 데이터
    record SignupResult(Long user_id) {}
    // 응답 래퍼
    record ApiResponse<T>(String message, T data) {
        static <T> ApiResponse<T> ok(String msg, T data) { return new ApiResponse<>(msg, data); }
        static <T> ApiResponse<T> error(String msg) { return new ApiResponse<>(msg, null); }
    }

}