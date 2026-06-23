package com.main.EMS_backend.service;

import com.main.EMS_backend.entity.Event;
import com.main.EMS_backend.entity.EventRegistration;
import com.main.EMS_backend.repository.EventRegistrationRepository;
import com.main.EMS_backend.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
//import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecommendationService {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EventRegistrationRepository registrationRepository;

    @Cacheable(value = "recommendations", key = "#userId")
    public List<Event> recommendations(long userId) {
        List<EventRegistration> registrations = registrationRepository.findByUserId(userId);
        Map<String,Integer> categories = new HashMap<>();
        for (EventRegistration registration : registrations) {
            String category = registration.getEvent().getCategory();

            categories.put(
                    category,
                    categories.getOrDefault(category, 0)+1
            );
        }
        List<Event> upcomingEvents = eventRepository.findByDateGreaterThanEqual(LocalDate.now());

        upcomingEvents.sort((e1,e2) -> {
            int score1 = categories.getOrDefault(e1.getCategory(), 0);
            int score2 = categories.getOrDefault(e2.getCategory(), 0);
            return score2 - score1;
        });
        return upcomingEvents;
    }
}
