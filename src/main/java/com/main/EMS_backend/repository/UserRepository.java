package com.main.EMS_backend.repository;

import com.main.EMS_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);
    List<User> findByRole(String role);
    Long countByRole(String role);
}
