package com.example.CST438_P3.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class GroupTest {

    @Test
    void constructorShouldSetBasicFieldsAndTimestamps() {
        // Arrange
        User creator = new User("creatorUser", "creator@example.com", "password", "93955");

        // Act
        Group group = new Group(
                "Basketball Group",
                "Pickup games at the park",
                "Sports",
                "93955",
                creator
        );

        // Assert
        assertNull(group.getId()); // not persisted yet
        assertEquals("Basketball Group", group.getName());
        assertEquals("Pickup games at the park", group.getDescription());
        assertEquals("Sports", group.getActivityType());
        assertEquals("93955", group.getZipCode());
        assertEquals(creator, group.getCreator());

        assertNotNull(group.getCreatedAt());
        assertNotNull(group.getUpdatedAt());

        // createdAt should be <= updatedAt (they’re set to now() in constructor)
        assertFalse(group.getCreatedAt().isAfter(group.getUpdatedAt()));
    }

    @Test
    void settersShouldUpdateFields() {
        User creator = new User("creatorUser", "creator@example.com", "password", "93955");
        Group group = new Group(
                "Old Name",
                "Old desc",
                "OldType",
                "00000",
                creator
        );

        LocalDateTime now = LocalDateTime.now();

        group.setName("New Name");
        group.setDescription("New desc");
        group.setActivityType("NewType");
        group.setZipCode("12345");
        group.setMaxMembers(10);
        group.setEventDate(now.plusDays(1));
        group.setIsRecurring(true);
        group.setCreatedAt(now.minusDays(1));
        group.setUpdatedAt(now);

        assertEquals("New Name", group.getName());
        assertEquals("New desc", group.getDescription());
        assertEquals("NewType", group.getActivityType());
        assertEquals("12345", group.getZipCode());
        assertEquals(10, group.getMaxMembers());
        assertEquals(now.plusDays(1), group.getEventDate());
        assertTrue(group.getIsRecurring());
        assertEquals(now.minusDays(1), group.getCreatedAt());
        assertEquals(now, group.getUpdatedAt());
    }

    @Test
    void membersShouldBeEmptyByDefault() {
        User creator = new User("creatorUser", "creator@example.com", "password", "93955");
        Group group = new Group("Name", "Desc", "Type", "93955", creator);

        assertNotNull(group.getMembers());
        assertTrue(group.getMembers().isEmpty());
        assertEquals(0, group.getCurrentMemberCount());
    }

    @Test
    void canSetMembersCollection() {
        User creator = new User("creatorUser", "creator@example.com", "password", "93955");
        Group group = new Group("Name", "Desc", "Type", "93955", creator);

        var members = new HashSet<User>();
        group.setMembers(members);

        assertSame(members, group.getMembers());
    }

    @Test
    void getCurrentMemberCountReturnsSizeOfMembers() {
        User creator = new User("creatorUser", "creator@example.com", "password", "93955");
        Group group = new Group("Name", "Desc", "Type", "93955", creator);

        User u1 = new User("u1", "u1@example.com", "pass", "93955");
        User u2 = new User("u2", "u2@example.com", "pass", "93955");

        group.getMembers().add(u1);
        group.getMembers().add(u2);

        assertEquals(2, group.getCurrentMemberCount());
    }

    @Test
    void removeMemberShouldRemoveFromGroupAndUser() {
        User creator = new User("creatorUser", "creator@example.com", "password", "93955");
        Group group = new Group("Name", "Desc", "Type", "93955", creator);

        User member = new User("member", "member@example.com", "pass", "93955");

        // Simulate bidirectional relationship
        group.getMembers().add(member);
        member.getGroups().add(group);

        assertTrue(group.getMembers().contains(member));
        assertTrue(member.getGroups().contains(group));

        // Act
        group.removeMember(member);

        // Assert
        assertFalse(group.getMembers().contains(member));
        assertFalse(member.getGroups().contains(group));
    }

    @Test
    void toStringShouldContainKeyFields() {
        User creator = new User("creatorUser", "creator@example.com", "password", "93955");
        Group group = new Group("Name", "Desc", "Type", "93955", creator);

        group.setMaxMembers(5);

        String s = group.toString();

        assertTrue(s.contains("Name"));
        assertTrue(s.contains("Desc"));
        assertTrue(s.contains("Type"));
        assertTrue(s.contains("93955"));
        assertTrue(s.contains("creatorUser"));
    }
}
