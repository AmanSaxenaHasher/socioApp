package com.example.socio.service;

import com.example.socio.entity.Post;
import com.example.socio.entity.User;
import com.example.socio.model.PostRequest;
import com.example.socio.model.PostResponse;
import com.example.socio.repository.PostRepository;
import com.example.socio.repository.UserRepository;
import com.example.socio.security.CustomAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public PostResponse createPost(PostRequest request) {
        Long userId = getAuthenticatedUserId(); // Retrieve userId from SecurityContext
        Post post = new Post();
        post.setContent(request.getContent());
        post.setUserId(userId);
        post = postRepository.save(post);
        return mapToResponse(post);
    }

    public PostResponse updatePost(Long postId, PostRequest request) {
        Long userId = getAuthenticatedUserId(); // Retrieve userId from SecurityContext
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        // Check if the user is authorized to update the post
        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("You are not authorized to update this post");
        }

        post.setContent(request.getContent());
        post = postRepository.save(post);
        return mapToResponse(post);
    }

    public List<PostResponse> getPostsByUser(String searchTerm) {
        List<Post> posts = postRepository.findAll();

        if (searchTerm != null && !searchTerm.isEmpty()) {
            posts = posts.stream()
                    .filter(post -> {
                        User user = userRepository.findById(post.getUserId())
                                .orElseThrow(() -> new RuntimeException("User not found"));
                        return post.getContent().toLowerCase().contains(searchTerm.toLowerCase()) ||
                                user.getEmail().toLowerCase().contains(searchTerm.toLowerCase()) ||
                                user.getUsername().toLowerCase().contains(searchTerm.toLowerCase());
                    })
                    .toList();
        }

        return posts.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public void deletePost(Long postId) {
        Long userId = getAuthenticatedUserId(); // Retrieve userId from SecurityContext
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Check if the user is authorized to delete the post
        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("You are not authorized to delete this post");
        }

        // Delete the post
        postRepository.deleteById(postId);
    }

    private PostResponse mapToResponse(Post post) {
        User user = userRepository.findById(post.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setContent(post.getContent());
        response.setUserId(post.getUserId());
        response.setUsername(user.getUsername());
        response.setLikedByUsers(post.getLikes().stream()
                .peek(u -> {
                    u.setPassword("");
                    u.setFollowers(Collections.emptyList());
                    u.setFollowing(Collections.emptyList());
                    u.setPasswordLastUpdated(null);
                })
                .collect(Collectors.toSet()));

        return response;
    }

    private Long getAuthenticatedUserId() {
        CustomAuthenticationToken authentication = (CustomAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return authentication.getUserId();
    }
}