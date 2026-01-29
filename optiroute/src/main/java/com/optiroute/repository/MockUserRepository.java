package com.optiroute.repository;

import com.optiroute.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class MockUserRepository {
    private final List<User> users = new ArrayList<>();

    public MockUserRepository() {
        // Mock User: user1 / password
        // Note: In a real app, password should be hashed. keeping it simple for mock as requested.
        users.add(new User("1", "user1", "password")); 
    }

    public Optional<User> findByUsername(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
    }
}
