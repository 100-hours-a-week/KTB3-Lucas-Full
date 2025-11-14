package com.week4.lucas.User.controller;

import com.week4.lucas.User.dto.response.ApiResponse;
import com.week4.lucas.User.dto.request.LoginRequest;
import com.week4.lucas.User.dto.request.UserReq;
import com.week4.lucas.User.dto.request.AccountUpdateReq;
import com.week4.lucas.User.dto.response.LoginSuccess;
import com.week4.lucas.User.dto.response.LoginUser;
import com.week4.lucas.User.dto.response.SignupResult;
import com.week4.lucas.User.dto.response.AccountUpdateRes;
import com.week4.lucas.User.entity.User;
import com.week4.lucas.User.service.UserService;
import com.week4.lucas.User.support.AuthTokenResolver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;

@Tag(name = "User", description = "User API")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final AuthTokenResolver authTokenResolver;

    // 회원가입
    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResult>> signup(@Valid @RequestBody UserReq dto) {
        Long id = service.signup(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("register_success", new SignupResult(id)));
    }

    // 단순 조회 테스트용
    @Operation(summary = "회원 테스트 조회(가입 잘 됐는지 확인용)")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> get(@PathVariable Long id) {
        User u = service.get(id);
        return ResponseEntity.ok(ApiResponse.ok("ok", u));
    }

    // 로그인
    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<LoginSuccess> login(@Valid @RequestBody LoginRequest req) {
        // 실패시 UnauthorizedException
        User u = service.login(req.email(), req.password());

        String token = UUID.randomUUID().toString().replace("-", ""); // 토큰생성
        service.registerToken(token, u.getId());

        LoginUser user = new LoginUser(u.getId(), u.getName()); // nickname 대신 name 사용
        return ResponseEntity.ok(new LoginSuccess("login_success", token, user));
    }


    @Operation(summary = "로그아웃")
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {

            String token = authorization.substring("Bearer ".length()).trim();

            // 토큰 검증/삭제 (없으면 401)
            service.logout(token);

            // 성공 200
            return ResponseEntity.ok(ApiResponse.ok("Logout_success",null));

    }
    //회원탈퇴
    @Operation(summary = "회원탈퇴")
    @SecurityRequirement(name = "BearerAuth")
    @DeleteMapping("/account")
    public ResponseEntity<?> deleteAccount(@RequestHeader(value = "Authorization", required = false) String authorization){
        Long userId = authTokenResolver.requireUserId(authorization);
        boolean isDeleted = service.deleteAccount(userId);
        if(!isDeleted)return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("delete_failed"));
        return ResponseEntity.ok(ApiResponse.ok("delete_success",null));
    }

    // 회원 정보 변경
    @Operation(summary = "회원 정보 변경")
    @SecurityRequirement(name = "BearerAuth")
    @PatchMapping("/account")
    public ResponseEntity<?> updateAccount(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                           @Valid @RequestBody AccountUpdateReq req) {
        Long userId = authTokenResolver.requireUserId(authorization);
        AccountUpdateRes updated = service.updateAccount(userId, req);
        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("account_update_failed"));
        }
        return ResponseEntity.ok(ApiResponse.ok("account_update_success", updated));
    }
}
