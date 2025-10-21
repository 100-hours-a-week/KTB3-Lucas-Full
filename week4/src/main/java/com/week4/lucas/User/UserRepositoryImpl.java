package com.week4.lucas.User;

import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepositoryImpl implements UserRepository{

    private final Map<Long, User> store = new ConcurrentHashMap<>();
    private final Map<String, Long> emailIdx = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public Optional<User> findByEmail(String email) {
        Long id = emailIdx.get(email);
        return id == null ? Optional.empty() : Optional.ofNullable(store.get(id));
    }

    public List<User> findAll() {
        return new ArrayList<>(store.values());
    }

    public User save(User u) {
        if (u.getId() == null) u.setId(seq.incrementAndGet());
        store.put(u.getId(), u);
        emailIdx.put(u.getEmail(), u.getId());
        return u;
    }
}
