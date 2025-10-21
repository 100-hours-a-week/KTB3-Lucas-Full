package com.week5.lucas.User;

import com.week5.lucas.User.dto.request.UserDto;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserServiceImpl implements UserService {
    private final Map<String, Long> activeTokens = new ConcurrentHashMap<>();
    private final UserRepository repo;

    public UserServiceImpl(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public Long signup(UserDto dto) {
        // 이메일 중복 체크 → 400
        repo.findByEmail(dto.email()).ifPresent(u -> {
            throw new IllegalArgumentException("email duplicated");
        });

        User saved = repo.save(UserMapper.toEntity(dto)); // DTO → 엔티티
        return saved.getId();
    }

    @Override
    public User get(Long id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("not found"));
    }

    @Override
    public User login(String email, String password) {
        User u = repo.findByEmail(email).orElseThrow(UserService.UnauthorizedException::new);
        if (!u.getPassword().equals(password)) {
            throw new UserService.UnauthorizedException();
        }
        return u; // 컨트롤러에서 토큰 생성 및 응답 포맷 구성
    }
    @Override
    public void registerToken(String token, Long userId) {
        activeTokens.put(token, userId);
    }

    @Override
    public void logout(String token) {
        if (token == null || !activeTokens.containsKey(token)) {
            throw new UserService.UnauthorizedException();
        }
        activeTokens.remove(token);
    }
}