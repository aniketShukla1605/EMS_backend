package com.main.EMS_backend.controller;

import com.main.EMS_backend.dto.EventUpdateRequest;
import com.main.EMS_backend.entity.Event;
import com.main.EMS_backend.entity.User;
import com.main.EMS_backend.repository.UserRepository;
import com.main.EMS_backend.service.CloudinaryService;
import com.main.EMS_backend.service.EventRegistrationService;
import com.main.EMS_backend.service.EventService;
import com.main.EMS_backend.service.RecommendationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@Slf4j
public class EventController {
    private EventService eventService;
    private final CloudinaryService cloudinaryService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventRegistrationService eventRegistrationService;
    @Autowired
    private RecommendationService recommendationService;

//    private RecommendationService  recommendationService;

    public EventController(EventService eventService, CloudinaryService cloudinaryService) {
        this.eventService = eventService;
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping
    public ResponseEntity<?> createEvent(
            @RequestParam("eventName") String eventName,
            @RequestParam("venue") String venue,
            @RequestParam("date") String date,
            @RequestParam("time") String time,
            @RequestParam("category") String category,
            @RequestParam("description") String description,
            @RequestParam("banner") MultipartFile banner,
            Authentication authentication
    ) throws IOException {

        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body("Authentication is required");
        }

        if (banner == null || banner.isEmpty()) {
            return ResponseEntity.badRequest().body("Event banner is required");
        }

        if (banner.getContentType() == null || !banner.getContentType().startsWith("image/")) {
            return ResponseEntity.badRequest().body("Upload image only");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        String bannerUrl;
        try {
            bannerUrl = cloudinaryService.uploadEventBanner(banner);
        } catch (RuntimeException ex) {
            log.error("Cloudinary event banner upload failed for {}", eventName, ex);
            return ResponseEntity.internalServerError().body("Event banner upload failed");
        }

        Event event = new Event();
        event.setEventName(eventName);
        event.setVenue(venue);
        event.setDate(LocalDate.parse(date));
        event.setTime(LocalTime.parse(time));
        event.setCategory(category);
        event.setDescription(description);
        event.setBannerPath(bannerUrl);
//        event.setCreatedBy(authentication.getName());
        event.setCreatedBy(user);

        return ResponseEntity.ok(eventService.createEvent(event));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }
    
    @GetMapping("/recommended-events")
    public List<Event> getRecommendations(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);

        return recommendationService.recommendations(user.getId());
    }

    @PutMapping(value = "/update/{id}", consumes = "multipart/form-data")
    public ResponseEntity<?> updateEvent(
            @PathVariable Long id,
            @ModelAttribute EventUpdateRequest request
    ) throws IOException {

        try {
            return ResponseEntity.ok(
                    eventService.updateEvent(id, request)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            log.error("Event update failed for {}", id, e);
            return ResponseEntity.internalServerError().body("Event update failed");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.findById(id));
    }

    @GetMapping
    public ResponseEntity<?> getEvents(@RequestParam(required = false) String category, @RequestParam(required = false) String search) {
        return ResponseEntity.ok(eventService.getFilteredEvents(category, search));
    }

    @GetMapping("/my-events")
    public ResponseEntity<?> getMyEvents(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(eventService.getMyEvents(email));
    }

    @GetMapping("/organiser-events")
    public ResponseEntity<?> getOrganiserEvents(Authentication authentication) {
        return ResponseEntity.ok(eventService.getOrganiserEvents(authentication.getName()));
    }

    @DeleteMapping("/admin/events/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id){
        eventService.deleteEvent(id);
        return ResponseEntity.ok("Event deleted");
    }
}
