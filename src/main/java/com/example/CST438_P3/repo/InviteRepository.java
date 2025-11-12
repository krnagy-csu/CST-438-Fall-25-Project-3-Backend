package com.example.CST438_P3.repo;
import com.example.CST438_P3.model.Invite;
import com.example.CST438_P3.model.InviteStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public interface InviteRepository extends JpaRepository<Invite, Long> {
    
    List<Invite> findByInviteeId(Long inviteeId);

    List<Invite> findByInviteeIdAndStatus(Long inviteeId, InviteStatus status);

    List<Invite> findByGroupId(Long groupId);

    List<Invite> findByInviterId(Long inviterId);

    //Will prevent duplicate/spam invites
    Optional<Invite> findByGroupIdAndInviteeId(Long groupId, Long inviteeId, InviteStatus status);
}
