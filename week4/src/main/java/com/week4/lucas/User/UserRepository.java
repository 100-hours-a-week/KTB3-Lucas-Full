package com.week4.lucas.User;

import java.util.*;

public interface UserRepository {
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    User save(User u);
}