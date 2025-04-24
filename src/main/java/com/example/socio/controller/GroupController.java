package com.example.socio.controller;

import com.example.socio.model.GroupRequest;
import com.example.socio.model.GroupResponse;
import com.example.socio.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(@RequestBody GroupRequest request) {
        return ResponseEntity.ok(groupService.createGroup(request));
    }

    @GetMapping
    public ResponseEntity<List<GroupResponse>> getAllGroups(@RequestParam(required = false) String searchTerm) {
        return ResponseEntity.ok(groupService.getAllGroups(searchTerm));
    }

    @GetMapping("/user-specific")
    public ResponseEntity<List<GroupResponse>> getUserSpecificGroups() {
        return ResponseEntity.ok(groupService.getUserSpecificGroups());
    }

    @PostMapping("/{groupId}/addUser/{userId}")
    public ResponseEntity<Void> addUserToGroup(@PathVariable Long groupId, @PathVariable Long userId) {
        groupService.addUserToGroup(groupId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{groupId}/removeUser/{userId}")
    public ResponseEntity<Void> removeUserFromGroup(@PathVariable Long groupId, @PathVariable Long userId) {
        groupService.removeUserFromGroup(groupId, userId);
        return ResponseEntity.ok().build();
    }
}