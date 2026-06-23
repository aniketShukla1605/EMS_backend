package com.main.EMS_backend.service;

import com.main.EMS_backend.dto.AnnouncementRequest;
import com.main.EMS_backend.entity.Announcement;
import com.main.EMS_backend.entity.Event;
import com.main.EMS_backend.entity.User;
import com.main.EMS_backend.repository.AnnouncementRepository;
import com.main.EMS_backend.repository.EventRepository;
import com.main.EMS_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnnouncementService {
    @Autowired
    private AnnouncementRepository announcementRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;

    @CacheEvict(value = {
            "userAnnouncements",
            "announcementCount"
    }, allEntries = true)
    public void createGlobal(String title,String message,String email){
        User user = userRepository.findByEmail(email);
        Announcement announcement = new Announcement();
        announcement.setTitle(title);
        announcement.setMessage(message);
        announcement.setType("GLOBAL");
        announcement.setCreatedBy(user);
        announcement.setCreatedAt(LocalDateTime.now());
        announcementRepository.save(announcement);
    }
    @CacheEvict(value = {
            "userAnnouncements",
            "announcementCount"
    }, allEntries = true)
    public void createEvent(Long eventId,String title,String message,String email){
        Event event = eventRepository.findById(eventId).orElse(null);
        User user = userRepository.findByEmail(email);
        Announcement announcement = new Announcement();
        announcement.setTitle(title);
        announcement.setMessage(message);
        announcement.setType("EVENT");
        announcement.setEvent(event);
        announcement.setCreatedBy(user);
        announcement.setCreatedAt(LocalDateTime.now());
        announcementRepository.save(announcement);
    }

    @Cacheable(value = "userAnnouncements", key = "#email")
    public List<Announcement> getUserAnnouncements(String email){
        List<Announcement> global = announcementRepository.findByType("GLOBAL");
        List<Announcement> event = announcementRepository.findEventAnnouncementsForUser(email);

        global.addAll(event);
        return global;
    }
    @Cacheable(value = "userAnnouncount", key = "#email")
    public long getAnnouncementsCount(String email){
        return announcementRepository.countAnnouncementsForUser(email);
    }
}
