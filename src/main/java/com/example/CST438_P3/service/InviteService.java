package com.example.CST438_P3.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.CST438_P3.model.Group;
import com.example.CST438_P3.model.Invite;
import com.example.CST438_P3.model.InviteStatus;
import com.example.CST438_P3.model.User;
import com.example.CST438_P3.repo.GroupRepository;
import com.example.CST438_P3.repo.InviteRepository;
import com.example.CST438_P3.repo.UserRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class InviteService {
   
    @Autowired
    private InviteRepository inviteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    public Invite createInvite(Long groupId, Long inviterId, Long inviteeId){
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        User inviter = userRepository.findById(inviterId)
                .orElseThrow(() -> new RuntimeException("Inviter not found"));
        User invitee = userRepository.findById(inviteeId)
                .orElseThrow(() -> new RuntimeException("Invitee not found"));

        if(group.getMembers().contains(invitee)){
            throw new RuntimeException("User is already a member of the group");
        }

        Optional<Invite> existingInvite = inviteRepository.findByGroupIdAndInviteeIdAndStatus(groupId, inviteeId, InviteStatus.PENDING);
        if(existingInvite.isPresent()){
            throw new RuntimeException("An invite is already pending for this user to join the group");
        }

        Invite invite = new Invite(group, inviter, invitee);
        return inviteRepository.save(invite);
    }

    //all of the below functions will be for
    //operations that haven't been created yet
    //on the frontend

    public Invite acceptInvite(Long inviteId){
        Invite invite = inviteRepository.findById(inviteId)
                .orElseThrow(() -> new RuntimeException("Invite not found"));

        if(invite.getStatus() != InviteStatus.PENDING){
            throw new RuntimeException("Invite has already been responded to");
        }

        Group group = invite.getGroup();
        if(group.getMaxMembers() != null && group.getCurrentMemberCount() >= group.getMaxMembers()){
            throw new RuntimeException("Group is full");
        }

        group.getMembers().add(invite.getInvitee());
        groupRepository.save(group);

        invite.setStatus(InviteStatus.ACCEPTED);
        invite.setRespondedAt(LocalDateTime.now());
        return inviteRepository.save(invite);
    }

    public Invite declineInvite(Long inviteId){
        Invite invite = inviteRepository.findById(inviteId)
                .orElseThrow(() -> new RuntimeException("Invite not found"));

        if(invite.getStatus() != InviteStatus.PENDING){
            throw new RuntimeException("Invite has already been responded to");
        }

        invite.setStatus(InviteStatus.DECLINED);
        invite.setRespondedAt(LocalDateTime.now());
        return inviteRepository.save(invite);
    }

    public List<Invite> getUserInvites(Long userId){
        return inviteRepository.findByInviteeId(userId);
    }

    public List<Invite> getPendingInvites(Long userId){
        return inviteRepository.findByInviteeIdAndStatus(userId, InviteStatus.PENDING);
    }

    public List<Invite> getGroupInvites(Long groupId){
        return inviteRepository.findByGroupId(groupId);
    }

    public void deleteInvite(Long inviteId){
        Invite invite = inviteRepository.findById(inviteId)
                .orElseThrow(() -> new RuntimeException("Invite not found"));
        inviteRepository.delete(invite);
    }
}
