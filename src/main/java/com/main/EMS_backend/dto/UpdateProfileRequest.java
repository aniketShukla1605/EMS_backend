package com.main.EMS_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequest {
    private String username;
    private String email;
    private String branch;
    private String address;
    private String contact;
    private String instituteID;
}
