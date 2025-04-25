package com.example.socio.repository;

import com.example.socio.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT ui.follower FROM UserInteraction ui WHERE ui.followee.id = :id")
    Optional<List<User>> findFollowersByUserId(@Param("id") Long id);

    @Query("SELECT ui.followee FROM UserInteraction ui WHERE ui.follower.id = :id")
    Optional<List<User>> findFollowingByUserId(@Param("id") Long id);

    @Query("SELECT ui.followee.id FROM UserInteraction ui WHERE ui.follower.id = :userId")
    List<Long> findFollowingIds(@Param("userId") Long userId);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByUsernameIgnoreCase(String username);

    List<User> findByEmailContainingIgnoreCase(String search);

    List<User> findByUsernameContainingIgnoreCase(String search);
}