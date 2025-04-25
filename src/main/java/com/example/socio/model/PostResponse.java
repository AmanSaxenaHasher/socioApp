package com.example.socio.model;

import com.example.socio.entity.User;
import lombok.Data;

import java.util.Set;

@Data
public class PostResponse {
    private Long id;
    private String content;
    private Long userId;
    private String username;
    private Set<User> likedByUsers;
    private Set<CommentResponse> comments;
}