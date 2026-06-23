package com.main.EMS_backend.dto;

import com.main.EMS_backend.entity.Event;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
//import java.time.LocalDateTime;
@Getter
@Setter
public class OrganiserEventDTO {
    private Long id;
    private String eventName;
    private LocalDate date;
    private Long registrationCount;
    public OrganiserEventDTO(Event event, Long eventRegistrationCount) {
        this.id = event.getId();
        this.eventName = event.getEventName();
        this.date = event.getDate();
        this.registrationCount = eventRegistrationCount;
    }
}
