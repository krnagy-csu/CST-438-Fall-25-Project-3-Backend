package com.example.CST438_P3.model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class UserTesting {

    @Test
    void constructorShouldSetFields() {
        // arrange
        String username = "testUser";
        String email = "test@example.com";
        String password = "secret";
        String zipCode = "93955";

        // act
        User user = new User(username, email, password, zipCode);

        // assert
        assertNull(user.getId()); // not set yet (JPA will set it later)
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(zipCode, user.getZipCode());
    }

    @Test
    void settersShouldUpdateFields() {
        User user = new User("oldUser", "old@example.com", "oldPass", "00000");

        user.setUsername("newUser");
        user.setEmail("new@example.com");
        user.setPassword("newPass");
        user.setZipCode("93955");

        assertEquals("newUser", user.getUsername());
        assertEquals("new@example.com", user.getEmail());
        assertEquals("newPass", user.getPassword());
        assertEquals("93955", user.getZipCode());
    }

    @Test
    void groupsShouldBeEmptyByDefault() {
        User user = new User("testUser", "test@example.com", "secret", "93955");

        assertNotNull(user.getGroups());
        assertTrue(user.getGroups().isEmpty());

        assertNotNull(user.getCreatedGroups());
        assertTrue(user.getCreatedGroups().isEmpty());
    }

    @Test
    void canSetGroupsCollections() {
        User user = new User("testUser", "test@example.com", "secret", "93955");

        // in a real test you might use real Group objects;
        // here we just care that the collection is stored correctly
        var groups = new HashSet<Group>();
        var createdGroups = new HashSet<Group>();

        user.setGroups(groups);
        user.setCreatedGroups(createdGroups);

        assertSame(groups, user.getGroups());
        assertSame(createdGroups, user.getCreatedGroups());
    }

    @Test
    void toStringShouldContainKeyFields() {
        User user = new User("testUser", "test@example.com", "secret", "93955");

        String asString = user.toString();

        assertTrue(asString.contains("testUser"));
        assertTrue(asString.contains("test@example.com"));
        assertTrue(asString.contains("93955"));
    }
}

