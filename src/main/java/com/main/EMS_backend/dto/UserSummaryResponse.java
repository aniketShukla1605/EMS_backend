package com.main.EMS_backend.dto;

import com.main.EMS_backend.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSummaryResponse {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String branch;
    private String contact;
    private String address;
    private String instituteID;
    private String profilePicture;

    public static UserSummaryResponse from(User user) {
        UserSummaryResponse response = new UserSummaryResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setBranch(user.getBranch());
        response.setContact(user.getContact());
        response.setAddress(user.getAddress());
        response.setInstituteID(user.getInstituteID());
        response.setProfilePicture(user.getProfilePicture());
        return response;
    }
}
