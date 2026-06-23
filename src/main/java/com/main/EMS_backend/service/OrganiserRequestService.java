package com.main.EMS_backend.service;

import com.main.EMS_backend.entity.OrganiserRequest;
import com.main.EMS_backend.entity.User;
import com.main.EMS_backend.exception.UserNotFoundException;
import com.main.EMS_backend.repository.OrganiserRequestRepository;
import com.main.EMS_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganiserRequestService {
    @Autowired
    private OrganiserRequestRepository organiserRequestRepository;
    @Autowired
    private UserRepository userRepository;

    @CacheEvict(value = {
            "pendingRequests",
            "dashboardStats"
    }, allEntries = true)
    public String requestOrganiser(String email) {
        User user = userRepository.findByEmail(email);
        OrganiserRequest request = new OrganiserRequest();
        request.setUser(user);
        request.setStatus("PENDING");
        organiserRequestRepository.save(request);
        return "success";
    }

    @Cacheable("pendingRequests")
    public List<OrganiserRequest> getPendingRequests() {
        return organiserRequestRepository.findByStatus("PENDING");
    }

    @CacheEvict(value = {
            "pendingRequests",
            "dashboardStats"
    }, allEntries = true)
    public String approveRequest(Long id) {
        OrganiserRequest request = organiserRequestRepository.findById(id).orElseThrow(()-> new UserNotFoundException("User not found"));

        User user = request.getUser();
        user.setRole("ORGANISER");
        userRepository.save(user);
        request.setStatus("APPROVED");
        organiserRequestRepository.save(request);
        return "Approved";
    }

    @CacheEvict(value = {
            "pendingRequests",
            "dashboardStats"
    }, allEntries = true)
    public String rejectRequest(Long id) {
        OrganiserRequest request = organiserRequestRepository.findById(id).orElseThrow(()-> new UserNotFoundException("User not found"));
        request.setStatus("REJECTED");
        organiserRequestRepository.save(request);
        return "Request Rejected";
    }
}
