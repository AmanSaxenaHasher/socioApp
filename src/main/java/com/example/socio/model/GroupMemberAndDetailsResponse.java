package com.example.socio.model;

import lombok.Data;

import java.util.Set;

@Data
public class GroupMemberAndDetailsResponse {
    private Long groupId;
    private String groupName;
    private Set<GroupMemberResponse> members;
}
