package com.example.socio.service;

import com.example.socio.entity.Group;
import com.example.socio.entity.User;
import com.example.socio.model.GroupMemberAndDetailsResponse;
import com.example.socio.model.GroupMemberResponse;
import com.example.socio.model.GroupRequest;
import com.example.socio.model.GroupResponse;
import com.example.socio.repository.GroupRepository;
import com.example.socio.repository.UserRepository;
import com.example.socio.security.CustomAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    public GroupResponse createGroup(GroupRequest request) {
        Group group = new Group();
        group.setName(request.getName());
        Long creatorId = getAuthenticatedUserId();
        group.setCreatorId(creatorId);

        // Add creator as a member
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        group.setMembers(Set.of(creator));

        group = groupRepository.save(group);
        return mapToResponse(group);
    }

    public List<GroupResponse> getAllGroups(String searchTerm) {
        List<Group> groups = (searchTerm != null && !searchTerm.isEmpty())
                ? groupRepository.findByNameContainingIgnoreCase(searchTerm)
                : groupRepository.findAll();

        return groups.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public GroupMemberAndDetailsResponse getGroupMembers(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        GroupMemberAndDetailsResponse response = new GroupMemberAndDetailsResponse();
        response.setGroupId(group.getId());
        response.setGroupName(group.getName());

         response.setMembers(group.getMembers().stream()
                .map(member -> {
                    GroupMemberResponse groupResponse = new GroupMemberResponse();
                    groupResponse.setId(member.getId());
                    groupResponse.setUsername(member.getUsername());
                    groupResponse.setJoinedAt(LocalDateTime.now());
                    return groupResponse;
                })
                .collect(Collectors.toSet()));

         return response;
    }

    public void addUserToGroup(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        group.getMembers().add(user);
        groupRepository.save(group);
    }

    public void removeUserFromGroup(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        group.getMembers().remove(user);
        groupRepository.save(group);
    }

    private GroupResponse mapToResponse(Group group) {
        GroupResponse response = new GroupResponse();
        response.setId(group.getId());
        response.setName(group.getName());
        response.setCreatorId(group.getCreatorId());

        Set<GroupMemberResponse> members = group.getMembers().stream()
                .map(membership -> {
                    GroupMemberResponse memberResponse = new GroupMemberResponse();
                    memberResponse.setId(membership.getId());
                    memberResponse.setUsername(membership.getUsername());
                    memberResponse.setJoinedAt(LocalDateTime.now());
                    return memberResponse;
                })
                .collect(Collectors.toSet());

        response.setMembers(members);
        return response;
    }

    private Long getAuthenticatedUserId() {
        CustomAuthenticationToken authentication = (CustomAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return authentication.getUserId();
    }
}