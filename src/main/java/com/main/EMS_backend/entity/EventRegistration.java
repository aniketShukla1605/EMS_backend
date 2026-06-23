package com.main.EMS_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class EventRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name="event_id")
    private Event event;

    private String status = "PENDING";
}
