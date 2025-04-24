package com.example.socio.service;

import com.example.socio.entity.Group;
import com.example.socio.model.GroupRequest;
import com.example.socio.model.GroupResponse;
import com.example.socio.repository.GroupRepository;
import com.example.socio.security.CustomAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    public GroupResponse createGroup(GroupRequest request) {
        Group group = new Group();
        group.setName(request.getName());
        group.setCreatorId(getAuthenticatedUserId());
        group = groupRepository.save(group);
        return mapToResponse(group);
    }

    public List<GroupResponse> getAllGroups() {
        return groupRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private GroupResponse mapToResponse(Group group) {
        GroupResponse response = new GroupResponse();
        response.setId(group.getId());
        response.setName(group.getName());
        response.setCreatorId(group.getCreatorId());
        return response;
    }

    private Long getAuthenticatedUserId() {
        CustomAuthenticationToken authentication = (CustomAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return authentication.getUserId();
    }
}