package com.main.EMS_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String eventName;
    private String venue;
    private LocalDate date;
    private LocalTime time;
    private String category;

    @Column(length=2000)
    private String description;

    private String bannerPath;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;
}
