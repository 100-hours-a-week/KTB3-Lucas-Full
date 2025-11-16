package com.week4.lucas.User.service;

import com.week4.lucas.User.dto.response.AccountUpdateRes;
import com.week4.lucas.User.dto.response.LoginUser;
import com.week4.lucas.User.entity.User;
import com.week4.lucas.User.mapper.UserMapper;
import com.week4.lucas.User.repository.UserRepository;
import com.week4.lucas.User.dto.request.UserReq;
import com.week4.lucas.User.dto.request.AccountUpdateReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    public static byte[] sha256(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(raw.getBytes(StandardCharsets.UTF_8)); // byte[] 리턴
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //인메모리 토큰 저장
    private final Map<String, Long> activeTokens = new ConcurrentHashMap<>();
    private final UserRepository repo;


    @Transactional
    @Override
    public Long signup(UserReq dto) {
        // 이메일 중복 체크 → 400
        repo.findByEmail(dto.email()).ifPresent(u -> {
            throw new IllegalArgumentException("email duplicated");
        });
        // 비밀번호 해싱
        byte[] hashed = sha256(dto.password());

        User saved = repo.save(
                User.builder()
                        .email(dto.email())
                        .passwordHash(hashed)
                        .name(dto.name())
                        .profileImage(dto.profileImage())
                        .build()
        );
        return saved.getId();
    }
// 유저 정보 가져오기
    @Transactional(readOnly = true)
    @Override
    public LoginUser get(Long id) {

        User user =  repo.findById(id).orElseThrow(() ->
                new IllegalArgumentException("not found"));
        return new LoginUser(user.getEmail(), user.getName(), user.getProfileImage());
    }

    @Transactional(readOnly = true)
    @Override
    public LoginUser login(String email, String password,String token) {
        User u = repo.findByEmail(email).orElseThrow(UnauthorizedException::new);


        byte[] inputHash = sha256(password);            // 들어온 비밀번호 해시
        if (!Arrays.equals(u.getPasswordHash(), inputHash)) {
            throw new UnauthorizedException();
        }

        registerToken(token, u.getId());
        return new LoginUser(u.getEmail(), u.getName(), u.getProfileImage());
    }

    @Override
    public void registerToken(String token, Long userId) {
        activeTokens.put(token, userId);
    }


    @Override
    public void logout(String token) {
        if (token == null || !activeTokens.containsKey(token)) {
            throw new UnauthorizedException();
        }
        activeTokens.remove(token);
    }

    @Override
    public Long resolveUserIdByToken(String token) {
        Long userId = activeTokens.get(token);
        if (userId == null) {
            throw new UnauthorizedException();
        }
        return userId;
    }

    @Transactional
    @Override
    public boolean deleteAccount(Long userId) {
        User user = repo.findById(userId).orElse(null);
        if(user==null)return false;
        user.softDelete();
        return true;
    }

    @Transactional
    @Override
    public AccountUpdateRes updateAccount(Long userId, AccountUpdateReq req) {
        User user = repo.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }
        if (req.name() != null && !req.name().isBlank()) {
            user.setName(req.name());
        }
        if (req.profileImage() != null) {
            user.setProfileImage(req.profileImage());
        }
        if (req.email()!=null && !req.email().isBlank()){
            user.setEmail(req.email());
        }
        return UserMapper.toUpdateUser(user);
    }

    @Transactional
    @Override
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = repo.findById(userId).orElseThrow(() ->
                new IllegalArgumentException("user_not_found"));

      // 기존 비번 해싱 이후 맞는 비번인지 체크
        byte[] currentHashed = sha256(currentPassword);

        if (!Arrays.equals(user.getPasswordHash(), currentHashed)) {
            throw new IllegalArgumentException("currentPassword_unauthorized");
        }
        // 새로운 비밀번호 해싱 이후 비밀번호 중복 체크
        byte[] newHashed = sha256(newPassword);

        if (Arrays.equals(newHashed, currentHashed)) {
            throw new IllegalArgumentException("password_duplicated"); // "이전 비밀번호와 동일"
        }

        user.setPassword(newHashed);
    }
}
