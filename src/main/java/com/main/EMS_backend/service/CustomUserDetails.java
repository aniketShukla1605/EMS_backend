package com.main.EMS_backend.service;

import com.main.EMS_backend.entity.User;
import com.main.EMS_backend.exception.UserNotFoundException;
import com.main.EMS_backend.repository.UserRepository;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class CustomUserDetails implements UserDetailsService {
    private UserRepository userRepository;
    public CustomUserDetails(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        User user = userRepository.findByUsername(username);
        User user = userRepository.findByEmail(email);
        if(user == null){throw new UsernameNotFoundException("User not found");}
        return org.springframework.security.core.userdetails.User.withUsername(user.getEmail()).password(user.getPassword())
                .roles(user.getRole()).build();
    }
}
