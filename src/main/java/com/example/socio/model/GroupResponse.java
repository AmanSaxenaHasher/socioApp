package com.example.socio.model;

import lombok.Data;

@Data
public class GroupResponse {
    private Long id;
    private String name;
    private Long creatorId;
}