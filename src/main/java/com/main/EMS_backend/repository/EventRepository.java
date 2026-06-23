package com.main.EMS_backend.repository;

import com.main.EMS_backend.entity.Event;
import com.main.EMS_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByDateGreaterThanEqual(LocalDate date);
    List<Event> findByDateGreaterThanEqualAndCategory(LocalDate date, String category);
    List<Event> findByDateGreaterThanEqualAndEventNameContainingIgnoreCase(LocalDate date, String name);
    List<Event> findByDateGreaterThanEqualAndCategoryAndEventNameContainingIgnoreCase(LocalDate date, String category, String name);
    List<Event> findByDateBefore(LocalDate date);
    List<Event> findByCreatedBy(User user);
//    List<Event> findByCreatedByAndEventName(User user, String name);
//    void deleteById(Long id);
    long count();
}
