package com.main.EMS_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String profilePicture;
    private String username;
    @Column(unique = true)
    private String email;
    @JsonIgnore
    private String password;

    private String role = "USER";
    private String branch;
    private String contact;
    private String Address;
    private String instituteID;
}
