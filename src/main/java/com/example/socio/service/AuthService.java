package com.example.socio.service;

import com.example.socio.entity.User;
import com.example.socio.enums.ROLE;
import com.example.socio.enums.VISIBILITY;
import com.example.socio.model.LoginRequest;
import com.example.socio.model.ResetPasswordPayload;
import com.example.socio.model.UserRegistrationRequest;
import com.example.socio.model.UserResponse;
import com.example.socio.repository.UserRepository;
import com.example.socio.security.CustomAuthenticationToken;
import com.example.socio.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Transactional
    public UserResponse register(UserRegistrationRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(ROLE.USER.getRole().toLowerCase());
        user.setPasswordLastUpdated(new Date());
        user.setProfileVisibility(VISIBILITY.PUBLIC.getVisibility());
        user = userRepository.save(user);

        return mapToUserResponse(user);
    }

    public String login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            User user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (isPasswordExpired(user.getPasswordLastUpdated())) {
                throw new RuntimeException("Password expired. Please reset your password.");
            }

            return jwtTokenProvider.generateToken(user);
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid email or password");
        }
    }

    @Transactional
    public void resetPassword(ResetPasswordPayload request) {
        if(request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            throw new RuntimeException("New password cannot be empty");
        }
        User user = userRepository.findByEmail(getAuthenticatedEmailId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(passwordEncoder.encode(request.getNewPassword())); // Replace with actual logic
        user.setPasswordLastUpdated(new Date());
        userRepository.save(user);
    }

    private boolean isPasswordExpired(Date lastUpdated) {
        if (lastUpdated == null) {
            return true;
        }

        long thirtyDaysInMillis = 30L * 24 * 60 * 60 * 1000;
        return new Date().getTime() - lastUpdated.getTime() > thirtyDaysInMillis;
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        return response;
    }

    private String getAuthenticatedEmailId() {
        CustomAuthenticationToken authentication = (CustomAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return authentication.getEmail();
    }
}