package com.example.CST438_P3.controller;

import java.time.LocalDateTime;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.CST438_P3.service.GroupService;
import com.example.CST438_P3.model.Group;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.springframework.http.HttpStatus;




@RestController
@RequestMapping("/api/groups")
@CrossOrigin(origins = "*")
public class GroupController {
    
    @Autowired
    private GroupService groupService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllGroups() {
        Map<String, Object> response = new HashMap<>();

        try{
            List<Group> groups = groupService.getAllGroups();
            response.put("status", "success");
            response.put("groups", groups);
            response.put("count", groups.size());
            return ResponseEntity.ok(response);
        } catch(Exception e){
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getGroupById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try{
            Group group = groupService.getGroupById(id);
            response.put("status", "success");
            response.put("group", group);
            return ResponseEntity.ok(response);
        } catch(Exception e){
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createGroup(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        try{
           String name = (String) request.get("name");
           String description = (String) request.get("description");
           String activityType = (String) request.get("activityType");
           String zipCode = (String) request.get("zipCode");
           Integer maxMembers = request.get("maxMembers") != null ? 
                Integer.parseInt(request.get("maxMembers").toString()) : null;
           Boolean isRecurring = (Boolean) request.get("isRecurring");
           Long creatorId = request.get("creatorId") != null ? 
                Long.parseLong(request.get("creatorId").toString()) : null;
           
           LocalDateTime eventDate = null;
           if(request.get("eventDate") != null){
               eventDate = LocalDateTime.parse((String) request.get("eventDate"));
           }

           Group group = groupService.createGroup(name, description, activityType, 
                zipCode, maxMembers, eventDate, isRecurring, creatorId );

           response.put("status", "success");
           response.put("message", "Group created successfully");
           response.put("group", group);
           return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch(Exception e){
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

    }

    @PostMapping("/{id}/join")
    public ResponseEntity<Map<String, Object>> joinGroup(@PathVariable Long id, @RequestBody Map<String, Long> request) {
        
        Map<String, Object> response = new HashMap<>();

        try{
            Long userId = request.get("userId");
            Group group = groupService.joinGroup(id, userId);

            response.put("status", "success");
            response.put("message", "User joined the group successfully");
            response.put("group", group);
            return ResponseEntity.ok(response);
        } catch(Exception e){
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/user/{userId}/joined")
public ResponseEntity<Map<String, Object>> getJoinedGroups(@PathVariable Long userId) {
    Map<String, Object> response = new HashMap<>();
    try {
        List<Group> groups = groupService.getGroupsByUser(userId);
        response.put("status", "success");
        response.put("groups", groups);
        return ResponseEntity.ok(response);
    } catch(Exception e){
        response.put("status", "error");
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

@DeleteMapping("/{groupId}/leave/{userId}")
public ResponseEntity<String> leaveGroup(
        @PathVariable Long groupId,
        @PathVariable Long userId) {

    boolean success = groupService.leaveGroup(userId, groupId);
    if (success) {
        return ResponseEntity.ok("User left the group successfully");
    } else {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Failed to leave the group");
    }
}


    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchGroups(@RequestParam(required = false) String activityType,
                                                            @RequestParam(required = false) String zipCode) {
        Map<String, Object> response = new HashMap<>();

        try{
            List<Group> groups;

            if(activityType != null){
                groups = groupService.searchByActivityType(activityType);
            } else if(zipCode != null){
                groups = groupService.searchByZipCode(zipCode);
            } else {
                groups = groupService.getAllGroups();
            }

            response.put("status", "success");
            response.put("groups", groups);
            response.put("count", groups.size());
            return ResponseEntity.ok(response);
        } catch(Exception e){
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/activity-types")
    public ResponseEntity<Map<String, Object>> getActivityType(){
        Map<String, Object> response = new HashMap<>();

        try{
            List<String> activityTypes = groupService.getUniqueActivityTypes();
            response.put("status", "success");
            response.put("activityTypes", activityTypes);
            response.put("count", activityTypes.size());
            return ResponseEntity.ok(response);
        }catch (Exception e){
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
