package com.main.EMS_backend.service;

import com.main.EMS_backend.dto.EventUpdateRequest;
import com.main.EMS_backend.dto.OrganiserEventDTO;
import com.main.EMS_backend.entity.Event;
import com.main.EMS_backend.entity.User;
import com.main.EMS_backend.exception.EventNotFoundException;
import com.main.EMS_backend.repository.EventRegistrationRepository;
import com.main.EMS_backend.repository.EventRepository;
import com.main.EMS_backend.repository.UserRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventRegistrationRepository eventRegistrationRepository;
    public EventService(EventRepository eventRepository, UserRepository userRepository, EventRegistrationRepository eventRegistrationRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.eventRegistrationRepository = eventRegistrationRepository;
    }
    @CacheEvict(value = {
            "allEvents",
            "filteredEvents"
    }, allEntries = true)
    public Event createEvent(Event event){
        return eventRepository.save(event);
    }
    @Cacheable(value = "allEvents")
    public List<Event> getAllEvents() { return eventRepository.findAll(); }
    @Cacheable(value = "eventById", key = "#id")
    public Event findById(Long id) { return eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found")); }

    @Cacheable(
            value = "filteredEvents",
            key = "#category + '-' + #search"
    )
    public List<Event> getFilteredEvents(String category, String search) {
        LocalDate today = LocalDate.now();

        if(category!=null && !category.isEmpty()
                && search!=null && !search.isEmpty()){
            return eventRepository
                    .findByDateGreaterThanEqualAndCategoryAndEventNameContainingIgnoreCase(today, category, search);
        } else if (Objects.equals(category, "Previous")) {
            return eventRepository.findByDateBefore(today);
        } else if(category!=null && !category.isEmpty()){
            return eventRepository.findByDateGreaterThanEqualAndCategory(today, category);
        }
        else if(search!=null && !search.isEmpty()){
            return eventRepository
                    .findByDateGreaterThanEqualAndEventNameContainingIgnoreCase(today, search);
        }
        else{
            return eventRepository.findByDateGreaterThanEqual(today);
        }
    }

    public List<Event> getMyEvents(String email) {
        User user = userRepository.findByEmail(email);
        return eventRepository.findByCreatedBy(user);
    }

    public List<OrganiserEventDTO> getOrganiserEvents(String email) {
        User user = userRepository.findByEmail(email);
        List<Event> events = eventRepository.findByCreatedBy(user);
        return events.stream().map(event -> new OrganiserEventDTO(event,eventRegistrationRepository.countByEventId(event.getId()))).toList();
    }

    @CacheEvict(value = {
            "allEvents",
            "eventById",
            "filteredEvents"
    }, allEntries = true)
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    @CacheEvict(value = {
            "allEvents",
            "eventById",
            "filteredEvents"
    }, allEntries = true)
    public Object updateEvent(Long id, EventUpdateRequest req) throws IOException {

        Event event = eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException("Event not found"));


        MultipartFile banner = req.getBanner();

        if (banner != null && !banner.isEmpty()) {
            // handle file
            String fileName = banner.getOriginalFilename();
            Path path = Paths.get("uploads/"+fileName);
            Files.write(path,banner.getBytes());
            event.setBannerPath(fileName);
        }
        event.setEventName(req.getEventName());
        event.setCategory(req.getCategory());
        event.setDate(LocalDate.parse(req.getDate()));
        event.setTime(LocalTime.parse(req.getTime()));
        event.setVenue(req.getVenue());
        event.setDescription(req.getDescription());

        eventRepository.save(event);

        return "Updated successfully";
    }
}
