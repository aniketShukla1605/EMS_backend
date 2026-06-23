package com.main.EMS_backend.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
@Getter
@Setter
public class EventUpdateRequest {

    private String eventName;
    private String category;
    private String date;
    private String time;
    private String venue;
    private String description;
    private MultipartFile banner;
}
