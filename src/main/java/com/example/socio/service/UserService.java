package com.example.socio.service;

import com.example.socio.entity.User;
import com.example.socio.model.UserResponse;
import com.example.socio.model.UserUpdateRequest;
import com.example.socio.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<UserResponse> getAllUsers(String searchTerm) {
        List<User> users = (searchTerm != null && !searchTerm.isEmpty())
                ? userRepository.findByUsernameContainingIgnoreCase(searchTerm)
                : userRepository.findAll();

        return users.stream().map(user -> {
            List<String> followers = userRepository.findFollowersByUserId(user.getId())
                    .orElse(List.of())
                    .stream()
                    .map(User::getUsername)
                    .toList();

            List<String> following = userRepository.findFollowingByUserId(user.getId())
                    .orElse(List.of())
                    .stream()
                    .map(User::getUsername)
                    .toList();

            UserResponse response = mapToUserResponse(user);
            response.setFollowedBy(followers);
            response.setFollowing(following);

            return response;
        }).collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> followers = userRepository.findFollowersByUserId(id)
                .orElse(List.of())
                .stream()
                .map(User::getUsername)
                .toList();

        List<String> following = userRepository.findFollowingByUserId(id)
                .orElse(List.of())
                .stream()
                .map(User::getUsername)
                .toList();

        UserResponse response = mapToUserResponse(user);
        response.setFollowedBy(followers);
        response.setFollowing(following);

        return response;
    }

    public UserResponse updateUser(UserUpdateRequest request) {
        Long authenticatedUserId = getAuthenticatedUser();
        User user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user = userRepository.save(user);

        return mapToUserResponse(user);
    }

    public void deleteUser(Long id) {
        Long authenticatedUserId = getAuthenticatedUser();
        if (!authenticatedUserId.equals(id)) {
            throw new RuntimeException("You can only delete your own account");
        }

        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setProfileVisibility(user.getProfileVisibility());
        return response;
    }

    private Long getAuthenticatedUser() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}