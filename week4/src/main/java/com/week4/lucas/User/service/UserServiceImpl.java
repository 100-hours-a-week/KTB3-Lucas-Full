package com.week4.lucas.User.service;

import com.week4.lucas.User.entity.User;
import com.week4.lucas.User.repository.UserRepository;
import com.week4.lucas.User.dto.request.UserReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Map;
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

    @Transactional(readOnly = true)
    @Override
    public User get(Long id) {
        return repo.findById(id).orElseThrow(() ->
                new IllegalArgumentException("not found"));
    }

    @Transactional(readOnly = true)
    @Override
    public User login(String email, String password) {
        User u = repo.findByEmail(email).orElseThrow(UnauthorizedException::new);

        byte[] inputHash = sha256(password);            // 들어온 비밀번호 해시
        if (!Arrays.equals(u.getPasswordHash(), inputHash)) {
            throw new UnauthorizedException();
        }
        return u;
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
}
