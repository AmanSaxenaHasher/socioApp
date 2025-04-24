package com.example.socio.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String role;
    private String profileVisibility;
    private List<String> followedBy;
    private List<String> following;
}