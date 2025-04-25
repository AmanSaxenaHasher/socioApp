package com.example.socio.repository;

import com.example.socio.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p " +
            "WHERE p.id = :id")
    Optional<Post> findPostById(@Param("id") Long id);

    List<Post> findByUserId(Long userId);

    @Query("SELECT DISTINCT p FROM Post p " +
            "LEFT JOIN FETCH p.comments c " +
            "LEFT JOIN FETCH c.user " +
            "LEFT JOIN FETCH p.likes " +
            "WHERE p.userId IN :followingIds " +
            "ORDER BY p.createdAt DESC")
    List<Post> findPostsByFollowedUsers(@Param("followingIds") List<Long> followingIds);
}