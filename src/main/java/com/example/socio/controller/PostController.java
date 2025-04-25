package com.example.socio.controller;

import com.example.socio.model.CommentRequest;
import com.example.socio.model.PostRequest;
import com.example.socio.model.PostResponse;
import com.example.socio.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping("/")
    public ResponseEntity<PostResponse> createPost(@RequestBody PostRequest request) {
        return ResponseEntity.ok(postService.createPost(request));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long postId, @RequestBody PostRequest request) {
        return ResponseEntity.ok(postService.updatePost(postId, request));
    }

    @GetMapping("/")
    public ResponseEntity<List<PostResponse>> getPostsByUser(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(postService.getPostsByUser(search));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<String> addComment(
            @PathVariable Long postId,
            @RequestBody CommentRequest request) {
        return ResponseEntity.ok(postService.addComment(postId, request));
    }
}