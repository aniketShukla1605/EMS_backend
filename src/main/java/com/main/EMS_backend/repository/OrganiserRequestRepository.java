package com.main.EMS_backend.repository;

import com.main.EMS_backend.entity.OrganiserRequest;
import com.main.EMS_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganiserRequestRepository extends JpaRepository<OrganiserRequest, Long> {
    List<OrganiserRequest> findByStatus(String status);
    boolean existsByUserAndStatus(User user, String status);
    long countByStatus(String status);
}
