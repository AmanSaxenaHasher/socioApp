package com.example.socio.repository;

import com.example.socio.entity.UserInteraction;
import com.example.socio.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInteractionRepository extends JpaRepository<UserInteraction, Long> {
    boolean existsByFollowerAndFollowee(User follower, User followee);

    Optional<UserInteraction> findByFollowerAndFollowee(User follower, User followee);
}