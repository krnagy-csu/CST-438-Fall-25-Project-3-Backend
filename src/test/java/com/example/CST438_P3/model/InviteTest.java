package com.example.CST438_P3.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InviteTest {

    @Test
    void defaultConstructorShouldSetCreatedAtAndPendingStatus() {
        // act
        Invite invite = new Invite();

        // assert
        assertNull(invite.getId());                // JPA will set this later
        assertNotNull(invite.getCreatedAt());      // set in default constructor
        assertEquals(InviteStatus.PENDING, invite.getStatus());
        assertNull(invite.getGroup());
        assertNull(invite.getInviter());
        assertNull(invite.getInvitee());
        assertNull(invite.getRespondedAt());
    }

    @Test
    void constructorWithArgsShouldSetFieldsAndDefaults() {
        // arrange
        User inviter = new User("inviter", "inviter@example.com", "pass", "93955");
        User invitee = new User("invitee", "invitee@example.com", "pass", "93955");
        Group group = new Group("Study Group", "CS438 study", "Study", "93955", inviter);

        // act
        Invite invite = new Invite(group, inviter, invitee);

        // assert
        assertEquals(group, invite.getGroup());
        assertEquals(inviter, invite.getInviter());
        assertEquals(invitee, invite.getInvitee());
        assertEquals(InviteStatus.PENDING, invite.getStatus());
        assertNotNull(invite.getCreatedAt());
        assertNull(invite.getRespondedAt());
    }

    @Test
    void settersShouldUpdateFields() {
        User inviter = new User("inviter", "inviter@example.com", "pass", "93955");
        User invitee = new User("invitee", "invitee@example.com", "pass", "93955");
        Group group = new Group("Study Group", "CS438 study", "Study", "93955", inviter);

        Invite invite = new Invite(group, inviter, invitee);

        User newInviter = new User("newInviter", "newInviter@example.com", "pass", "93955");
        User newInvitee = new User("newInvitee", "newInvitee@example.com", "pass", "93955");
        Group newGroup = new Group("New Group", "New desc", "Sports", "12345", newInviter);

        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime respondedAt = LocalDateTime.now();

        invite.setGroup(newGroup);
        invite.setInviter(newInviter);
        invite.setInvitee(newInvitee);
        invite.setCreatedAt(createdAt);
        invite.setRespondedAt(respondedAt);
        invite.setStatus(InviteStatus.ACCEPTED);

        assertEquals(newGroup, invite.getGroup());
        assertEquals(newInviter, invite.getInviter());
        assertEquals(newInvitee, invite.getInvitee());
        assertEquals(createdAt, invite.getCreatedAt());
        assertEquals(respondedAt, invite.getRespondedAt());
        assertEquals(InviteStatus.ACCEPTED, invite.getStatus());
    }

    @Test
    void statusShouldAllowAcceptedAndDeclined() {
        User inviter = new User("inviter", "inviter@example.com", "pass", "93955");
        User invitee = new User("invitee", "invitee@example.com", "pass", "93955");
        Group group = new Group("Study Group", "CS438 study", "Study", "93955", inviter);

        Invite invite = new Invite(group, inviter, invitee);

        // Default should be PENDING
        assertEquals(InviteStatus.PENDING, invite.getStatus());

        // Change to ACCEPTED
        invite.setStatus(InviteStatus.ACCEPTED);
        assertEquals(InviteStatus.ACCEPTED, invite.getStatus());

        // Change to DECLINED
        invite.setStatus(InviteStatus.DECLINED);
        assertEquals(InviteStatus.DECLINED, invite.getStatus());
    }

    @Test
    void toStringShouldIncludeKeyInfo() {
        User inviter = new User("inviter", "inviter@example.com", "pass", "93955");
        User invitee = new User("invitee", "invitee@example.com", "pass", "93955");
        Group group = new Group("Study Group", "CS438 study", "Study", "93955", inviter);

        Invite invite = new Invite(group, inviter, invitee);

        String s = invite.toString();

        assertNotNull(s);
        assertTrue(s.contains("Invite"));
        assertTrue(s.contains("PENDING") || s.contains("ACCEPTED") || s.contains("DECLINED"));
        assertTrue(s.contains("Study Group"));      // from Group.toString()
        assertTrue(s.contains("inviter"));          // from User.toString()
        assertTrue(s.contains("invitee"));          // from User.toString()
    }
}
