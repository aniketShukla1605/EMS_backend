package com.main.EMS_backend.service;

import com.main.EMS_backend.repository.EventRepository;
import com.main.EMS_backend.repository.OrganiserRequestRepository;
import com.main.EMS_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrganiserRequestRepository organiserRequestRepository;
    @Autowired
    private EventRepository eventRepository;

    @Cacheable("dashboardStats")
    public Map<String, Long> getStats() {
        Map<String, Long> map = new HashMap<String, Long>();
        map.put("students", userRepository.countByRole("USER"));
        map.put("organisers",userRepository.countByRole("ORGANISER"));
        map.put("events",eventRepository.count());
        map.put("requests",organiserRequestRepository.countByStatus("PENDING"));
        return map;
    }
}
