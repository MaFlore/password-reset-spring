package com.test.demo.service;

import com.test.demo.model.User;

public interface UserService {
    public void sendPasswordResetEmail(User user);
    public boolean resetPassword(String token, String newPassword);
    User save(User user);
    User findByEmail(String email);
}
