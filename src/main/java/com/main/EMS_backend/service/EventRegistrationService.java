package com.main.EMS_backend.service;

import com.main.EMS_backend.dto.OrganiserEventDTO;
import com.main.EMS_backend.entity.Event;
import com.main.EMS_backend.entity.EventRegistration;
import com.main.EMS_backend.entity.User;
import com.main.EMS_backend.exception.EventNotFoundException;
import com.main.EMS_backend.repository.EventRegistrationRepository;
import com.main.EMS_backend.repository.EventRepository;
import com.main.EMS_backend.repository.UserRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventRegistrationService {
    private final EventRegistrationRepository eventRegistrationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public EventRegistrationService(EventRegistrationRepository eventRegistrationRepository, UserRepository userRepository, EventRepository eventRepository) {
        this.eventRegistrationRepository = eventRegistrationRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @CacheEvict(value = {
            "userRegistrations",
            "eventRegistrations",
            "organiserRegistrations",
            "pendingApprovals",
            "recommendations"
    }, allEntries = true)
    public String registerUser(String email,Long eventId){
        User user = userRepository.findByEmail(email);
        Event event = eventRepository.findById(eventId).orElseThrow(()->new EventNotFoundException("Event not found"));

        Optional<EventRegistration> eventRegis = eventRegistrationRepository.findByUserIdAndEventId(user.getId(),eventId);
        if(eventRegis.isPresent() && eventRegis.get().getStatus().equals("CANCELLED")){
            eventRegis.get().setStatus("PENDING");
            eventRegistrationRepository.save(eventRegis.get());
            return "Event registration has been sent";
        } else if (eventRegis.isPresent() && eventRegis.get().getStatus().equals("APPROVED")) {
            return "User already exists";
        }

        EventRegistration eventRegistration = new EventRegistration();
        eventRegistration.setUser(user);
        eventRegistration.setEvent(event);
        eventRegistrationRepository.save(eventRegistration);
        return "Event registered successfully";
    }

    @Cacheable(value = "userRegistrations", key = "#email")
    public List<EventRegistration> getUserRegistrations(String email){
        User user = userRepository.findByEmail(email);
        return eventRegistrationRepository.findByUserId(user.getId());
    }


    @CacheEvict(value = {
            "userRegistrations",
            "eventRegistrations",
            "organiserRegistrations",
            "pendingApprovals",
            "recommendations"
    }, allEntries = true)
    public String cancelRegistration(String email, Long eventId) {
        User user = userRepository.findByEmail(email);
        EventRegistration registration = eventRegistrationRepository.findByUserIdAndEventId(user.getId(),eventId).orElseThrow(()->new RuntimeException("Event not found"));
        if(registration.getStatus().equals("APPROVED")){
            return "User already approved";
        }
        registration.setStatus("CANCELLED");
        eventRegistrationRepository.save(registration);
        return "Event cancelled successfully";
    }

    @Cacheable(value = "eventRegistrations", key = "#eventId")
    public List<EventRegistration> getRegistrationsForEvent(Long eventId){
        return eventRegistrationRepository.findByEventId(eventId);
    }

    @CacheEvict(value = {
            "userRegistrations",
            "eventRegistrations",
            "organiserRegistrations",
            "pendingApprovals",
            "recommendations"
    }, allEntries = true)
    public String updateStatus(Long registrationId, String status){
        EventRegistration registration = eventRegistrationRepository.findById(registrationId).orElseThrow(()->new EventNotFoundException("Event not found"));
        registration.setStatus(status);
        eventRegistrationRepository.save(registration);
        return "Status updated successfully "+ status;
    }

    @Cacheable(value = "organiserRegistrations", key = "#email")
    public List<EventRegistration> getRegistrationsByOrganiser(String email) {
        return eventRegistrationRepository.findByOrganizerEmail(email);
    }

    @Cacheable(value = "pendingApprovals", key = "#email")
    public long getPendingApprovalsCount(String email) {
        return eventRegistrationRepository.countByEvent_CreatedBy_EmailAndStatus(email, "PENDING");
    }
//    public long getRegistrationCount(Long eventId) {
//        return eventRegistrationRepository.countByEventId(eventId);
//    }
}
