package com.main.EMS_backend.controller;

import com.main.EMS_backend.service.EventRegistrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/registrations")
@Slf4j
public class EventRegistrationController {
    private final EventRegistrationService eventRegistrationService;
    EventRegistrationController(EventRegistrationService eventRegistrationService) {
        this.eventRegistrationService = eventRegistrationService;
    }
    @PostMapping("/{eventId}")
    public ResponseEntity<?> register(@PathVariable Long eventId, Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(eventRegistrationService.registerUser(email, eventId));
    }
    @GetMapping("/my")
    public ResponseEntity<?> getMyEventRegistrations(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(
                eventRegistrationService.getUserRegistrations(email)
        );
    }
    @PutMapping("/cancel/{eventId}")
    public ResponseEntity<?> cancel(@PathVariable Long eventId, Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(eventRegistrationService.cancelRegistration(email,eventId));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<?> getEventRegistrations(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventRegistrationService.getRegistrationsForEvent(eventId));
    }

    @PutMapping("/approve/{registrationId}")
    public ResponseEntity<?> approve(@PathVariable Long registrationId) {
        return ResponseEntity.ok(eventRegistrationService.updateStatus(registrationId,"APPROVED"));
    }

    @PutMapping("/reject/{registrationId}")
    public ResponseEntity<?> reject(@PathVariable Long registrationId) {
        return ResponseEntity.ok(eventRegistrationService.updateStatus(registrationId,"REJECTED"));
    }

    @GetMapping("/organiser-registrations")
    public ResponseEntity<?> getOrganiserRegistrations(Authentication authentication) {
        String email = authentication.getName();


        return ResponseEntity.ok(eventRegistrationService.getRegistrationsByOrganiser(email));
    }

    @GetMapping("/pending-count")
    public ResponseEntity<?> getPendingApprovalsCount(Authentication authentication) {
        String email = authentication.getName();
        long count = eventRegistrationService.getPendingApprovalsCount(email);
        return ResponseEntity.ok(count);
    }

//    @GetMapping("/count/{eventId}")
//    public ResponseEntity<?> getEventRegistrationsCount(@PathVariable Long eventId) {
//        Long count = eventRegistrationService.getRegistrationCount(eventId);
//        return ResponseEntity.ok(count);
//    }
}
