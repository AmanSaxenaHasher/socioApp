package com.example.socio.controller;

import com.example.socio.model.InteractionRequest;
import com.example.socio.service.UserInteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interactions")
public class UserInteractionController {

    @Autowired
    private UserInteractionService interactionService;

    @PostMapping("/follow/{targetUserId}")
    public ResponseEntity<Void> followUser(@PathVariable Long targetUserId) {
        interactionService.followUser(targetUserId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unfollow/{targetUserId}")
    public ResponseEntity<Void> unfollowUser(@PathVariable Long targetUserId) {
        interactionService.unfollowUser(targetUserId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/like")
    public ResponseEntity<Void> likePost(@RequestBody InteractionRequest request) {
        interactionService.likePost(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unlike")
    public ResponseEntity<Void> unlikePost(@RequestBody InteractionRequest request) {
        interactionService.unlikePost(request);
        return ResponseEntity.ok().build();
    }
}