package com.main.EMS_backend.repository;

import com.main.EMS_backend.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement,Long> {
    List<Announcement> findByType(String type);
    @Query("""
        select a from Announcement a where a.type='EVENT' and a.event.id in (select r.event.id from EventRegistration r where r.user.email = :email)
    """)
    List<Announcement> findEventAnnouncementsForUser(String email);

    @Query("""
       select count(a) from Announcement a where a.type='GLOBAL' or a.event.id in(select r.event.id from EventRegistration r where r.user.email = :email)
        """)
    long countAnnouncementsForUser(String email);


}
