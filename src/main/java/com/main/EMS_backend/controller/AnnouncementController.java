package com.main.EMS_backend.controller;

import com.main.EMS_backend.dto.AnnouncementRequest;
import com.main.EMS_backend.service.AnnouncementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/announcements")
@Slf4j
public class AnnouncementController {
    @Autowired
    private AnnouncementService announcementService;

    @PostMapping("/global")
    public ResponseEntity<?> global(@RequestBody AnnouncementRequest announcementRequest,
                                    Authentication authentication) {
        announcementService.createGlobal(announcementRequest.getTitle(),
                announcementRequest.getMessage(),
                authentication.getName());
        return ResponseEntity.ok("Announcement Posted Successfully");
    }
    @PostMapping("/event/{eventId}")
    public ResponseEntity<?> event(
            @PathVariable Long eventId,
            @RequestBody AnnouncementRequest req,
            Authentication auth){

        announcementService.createEvent(eventId,req.getTitle(),req.getMessage(),auth.getName());

        return ResponseEntity.ok("Event announcement posted");
    }
    @GetMapping("/user")
    public ResponseEntity<?> user(Authentication auth){
        return ResponseEntity.ok(
                announcementService.getUserAnnouncements(auth.getName())
        );
    }
    @GetMapping("/count")
    public ResponseEntity<?> getCount(Authentication auth){
        return ResponseEntity.ok(announcementService.getAnnouncementsCount(auth.getName()));
    }
}
