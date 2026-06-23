package com.main.EMS_backend.controller;

import com.main.EMS_backend.repository.OrganiserRequestRepository;
import com.main.EMS_backend.service.OrganiserRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organiser-requests")
@Slf4j
public class OrganiserRequestController {
    @Autowired
    private OrganiserRequestService organiserRequestService;

    @PostMapping("/request")
    public ResponseEntity<?> request(Authentication authentication){
        return ResponseEntity.ok(organiserRequestService.requestOrganiser(authentication.getName()));
    }
    @GetMapping("/pending")
    public ResponseEntity<?> getRequests(){
        return ResponseEntity.ok(organiserRequestService.getPendingRequests());
    }
    @PutMapping("/approve/{id}")
    public ResponseEntity<?> approve(@PathVariable Long id){
        return ResponseEntity.ok(organiserRequestService.approveRequest(id));
    }
    @PutMapping("/reject/{id}")
    public ResponseEntity<?> reject(@PathVariable("id") Long id){
        return ResponseEntity.ok(organiserRequestService.rejectRequest(id));
    }
}
