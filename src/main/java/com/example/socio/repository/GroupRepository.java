package com.example.socio.repository;

import com.example.socio.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    List<Group> findByNameContainingIgnoreCase(String searchTerm);

    List<Group> findByMembers_Id(Long userId);

    List<Group> findByCreatorId(Long creatorId);
}