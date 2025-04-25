package com.example.socio.service;

import com.example.socio.entity.Comment;
import com.example.socio.entity.Post;
import com.example.socio.entity.User;
import com.example.socio.model.CommentRequest;
import com.example.socio.model.CommentResponse;
import com.example.socio.model.PostRequest;
import com.example.socio.model.PostResponse;
import com.example.socio.repository.CommentRepository;
import com.example.socio.repository.PostRepository;
import com.example.socio.repository.UserRepository;
import com.example.socio.security.CustomAuthenticationToken;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

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
        Post post = postRepository.findPostById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        // Check if the user is authorized to update the post
        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("You are not authorized to update this post");
        }

        post.setContent(request.getContent());
        post = postRepository.save(post);
        return mapToResponse(post);
    }

    public List<PostResponse> getPostsByUser(String searchTerm) {
        Long loggedInUserId = getAuthenticatedUserId();
        User currentUser = userRepository.findById(loggedInUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get all followed users' IDs
        List<Long> followingIds = userRepository.findFollowingIds(loggedInUserId);
        followingIds.add(loggedInUserId);

        // Add new repository method to fetch posts with all necessary data
        List<Post> posts = postRepository.findPostsByFollowedUsers(followingIds);

        if (searchTerm != null && !searchTerm.isEmpty()) {
            String searchTermLower = searchTerm.toLowerCase();
            posts = posts.stream()
                    .filter(post -> {
                        User user = userRepository.findById(post.getUserId())
                                .orElseThrow(() -> new RuntimeException("User not found"));
                        return post.getContent().toLowerCase().contains(searchTermLower) ||
                                user.getEmail().toLowerCase().contains(searchTermLower) ||
                                user.getUsername().toLowerCase().contains(searchTermLower);
                    })
                    .toList();
        }

        posts.forEach(post -> {
            List<Comment> comments = commentRepository.findByPostIdWithUser(post.getId());
            post.setComments(new HashSet<>(comments));
        });

        return posts.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
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

    public String addComment(Long postId, CommentRequest request) {
        Long userId = getAuthenticatedUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findPostById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setUser(user);
        comment.setPost(post);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        commentRepository.save(comment);

        return "Comment added successfully";
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

        response.setComments(post.getComments().stream()
                .map(comment -> {
                    CommentResponse commentResponse = new CommentResponse();
                    commentResponse.setId(comment.getId());
                    commentResponse.setContent(comment.getContent());
                    commentResponse.setUserId(comment.getUser().getId());
                    commentResponse.setUsername(comment.getUser().getUsername());
                    commentResponse.setCreatedAt(comment.getCreatedAt());
                    commentResponse.setUpdatedAt(comment.getUpdatedAt());
                    return commentResponse;
                })
                .collect(Collectors.toSet()));

        return response;
    }

    private Long getAuthenticatedUserId() {
        CustomAuthenticationToken authentication = (CustomAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return authentication.getUserId();
    }
}