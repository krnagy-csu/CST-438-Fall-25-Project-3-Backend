package com.example.CST438_P3.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import com.example.CST438_P3.model.Invite;
import com.example.CST438_P3.service.InviteService;
import java.util.Map;
import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder.In;

@RestController
@RequestMapping("/api/invites")
@CrossOrigin(origins = "*")
public class InviteController {
    
    @Autowired
    private InviteService inviteService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createInvite(@RequestBody Map<String, Long> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long groupId = request.get("groupId");
            Long inviterId = request.get("inviterId");
            Long inviteeId = request.get("inviteeId");

            Invite invite = inviteService.createInvite(groupId, inviterId, inviteeId);

            response.put("status", "success");
            response.put("message", "Invite created successfully");
            response.put("invite", invite);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }     
}
