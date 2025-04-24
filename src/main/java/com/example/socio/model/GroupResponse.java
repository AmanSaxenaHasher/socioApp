package com.example.socio.model;

import lombok.Data;
import java.util.Set;

@Data
public class GroupResponse {
    private Long id;
    private String name;
    private Long creatorId;
    private Set<GroupMemberResponse> members;
}