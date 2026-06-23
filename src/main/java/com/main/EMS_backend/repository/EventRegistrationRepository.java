package com.main.EMS_backend.repository;

import com.main.EMS_backend.entity.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
//import org.springframework.data.repository.CrudRepository;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration,Long> {
    Optional<EventRegistration> findByUserIdAndEventId(Long userId, Long eventId);

    List<EventRegistration> findByUserId(Long userId);

    List<EventRegistration> findByEventId(Long eventId);

    @Query("""
        SELECT r FROM EventRegistration r WHERE r.event.createdBy.email = :email
        """)
    List<EventRegistration> findByOrganizerEmail(@Param("email") String email);

    Long countByEventId(Long eventId);

    long countByEvent_CreatedBy_EmailAndStatus(String email, String status);
}
