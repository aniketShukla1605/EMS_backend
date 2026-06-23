package com.main.EMS_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileResponse {
    private String username;
    private String email;
    private String role;
    private String contact;
    private String address;
    private String branch;
    private String instituteID;
    private String profilePicture;
}
