package com.example.socio.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GroupMemberResponse {
    private Long id;
    private String username;
    private LocalDateTime joinedAt;
}