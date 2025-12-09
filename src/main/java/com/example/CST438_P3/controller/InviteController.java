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
import com.example.CST438_P3.dto.InviteDTO;

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


 
    @GetMapping("/user/{userId}/pending")
    public ResponseEntity<List<Invite>> getPendingInvites(@PathVariable Long userId) {
        List<Invite> invites = inviteService.getPendingInvites(userId);
        return ResponseEntity.ok(invites);
    }


    }  
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserInvites(@PathVariable Long userId){
        Map<String, Object> response = new HashMap<>();
        try {
            List<InviteDTO> invites = inviteService.getUserInvitesDTO(userId);
            response.put("status", "success");
            response.put("invites", invites);
            response.put("count", invites.size());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/user/{userId}/pending")
    public ResponseEntity<Map<String, Object>> getPendingInvites(@PathVariable Long userId){
        Map<String, Object> response = new HashMap<>();
        try {
            List<InviteDTO> invites = inviteService.getPendingInvitesDTO(userId);
            response.put("status", "success");
            response.put("invites", invites);
            response.put("count", invites.size());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/{inviteId}/accept")
    public ResponseEntity<Map<String, Object>> acceptInvite(@PathVariable Long inviteId){
        Map<String, Object> response = new HashMap<>();
        try {
            Invite invite = inviteService.acceptInvite(inviteId);
            response.put("status", "success");
            response.put("message", "Invite accepted successfully");
            response.put("invite", invite);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/{inviteId}/decline")
    public ResponseEntity<Map<String, Object>> declineInvite(@PathVariable Long inviteId){
        Map<String, Object> response = new HashMap<>();
        try {
            Invite invite = inviteService.declineInvite(inviteId);
            response.put("status", "success");
            response.put("message", "Invite declined successfully");
            response.put("invite", invite);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

}
