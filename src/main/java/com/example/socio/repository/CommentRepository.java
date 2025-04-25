package com.example.socio.repository;

import com.example.socio.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c " +
            "LEFT JOIN FETCH c.user " +
            "WHERE c.post.id = :postId " +
            "ORDER BY c.createdAt DESC")
    List<Comment> findByPostIdWithUser(@Param("postId") Long postId);
}