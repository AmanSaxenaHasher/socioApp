package com.example.socio.model;

import com.example.socio.entity.User;
import lombok.Data;

import java.util.Set;

@Data
public class PostResponse {
    private Long id;
    private String content;
    private Long userId;
    private String username; // Username of the post owner
    private Set<User> likedByUsers;  // Number of likes on the post
}