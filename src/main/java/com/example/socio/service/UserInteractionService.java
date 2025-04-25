package com.example.socio.service;

import com.example.socio.entity.Post;
import com.example.socio.entity.User;
import com.example.socio.entity.UserInteraction;
import com.example.socio.model.InteractionRequest;
import com.example.socio.repository.PostRepository;
import com.example.socio.repository.UserInteractionRepository;
import com.example.socio.repository.UserRepository;
import com.example.socio.security.CustomAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserInteractionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserInteractionRepository userInteractionRepository;

    public void followUser(Long targetUserId) {
        User follower = userRepository.findById(getAuthenticatedUserId())
                .orElseThrow(() -> new RuntimeException("Follower not found"));
        User followee = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("User to follow not found"));

        if (userInteractionRepository.existsByFollowerAndFollowee(follower, followee)) {
            throw new RuntimeException("Already following this user");
        }

        if(targetUserId.equals(getAuthenticatedUserId())) {
            throw new RuntimeException("Cannot follow yourself");
        }

        UserInteraction interaction = new UserInteraction();
        interaction.setFollower(follower);
        interaction.setFollowee(followee);
        userInteractionRepository.save(interaction);
    }

    public void unfollowUser(Long targetUserId) {
        User follower = userRepository.findById(getAuthenticatedUserId())
                .orElseThrow(() -> new RuntimeException("Follower not found"));
        User followee = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("User to unfollow not found"));

        UserInteraction interaction = userInteractionRepository.findByFollowerAndFollowee(follower, followee)
                .orElseThrow(() -> new RuntimeException("Not following this user"));

        userInteractionRepository.delete(interaction);
    }

    public void likePost(InteractionRequest request) {
        User user = userRepository.findById(getAuthenticatedUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (post.getLikes().contains(user)) {
            throw new RuntimeException("Post already liked");
        }

        post.getLikes().add(user);
        postRepository.save(post);
    }

    public void unlikePost(InteractionRequest request) {
        User user = userRepository.findById(getAuthenticatedUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getLikes().contains(user)) {
            throw new RuntimeException("Post not liked");
        }

        post.getLikes().remove(user);
        postRepository.save(post);
    }

    private Long getAuthenticatedUserId() {
        CustomAuthenticationToken authentication = (CustomAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return authentication.getUserId();
    }
}