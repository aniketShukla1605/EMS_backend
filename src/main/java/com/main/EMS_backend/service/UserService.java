package com.main.EMS_backend.service;

import com.main.EMS_backend.entity.User;
import com.main.EMS_backend.exception.UserNotFoundException;
import com.main.EMS_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @CacheEvict(value = {
            "usersByRole",
            "userCount",
            "dashboardStats"
    }, allEntries = true)
    public void registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }
    @Cacheable(value = "userCount", key = "#role")
    public long countUsers(String role) {
        return userRepository.countByRole(role);
    }

    @CacheEvict(value = {
            "usersByRole",
            "userCount",
            "dashboardStats"
    }, allEntries = true)
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    @CacheEvict(value = {
            "usersByRole",
            "userCount",
            "dashboardStats"
    }, allEntries = true)
    public void changeRole(Long id) {
        User user = userRepository.findById(id).orElseThrow(()->new UserNotFoundException("User not found"));
        user.setRole("USER");
        userRepository.save(user);
    }
}
