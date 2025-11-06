package com.example.CST438_P3.model;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class UserTest {
    
    @Test
    public void testUserCreation(){
        User user = new User("testuser", "test@email.com", "password123", "93927");

        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals("test@email.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals("93927", user.getZipCode());
    }

    @Test
    public void testUserGettersAndSetters() {
        User user = new User("john", "john@email.com", "pass", "12345");

        user.setUsername("johndoe");
        user.setEmail("johnNew@email.com");
        user.setZipCode("54321");

        assertEquals("johndoe", user.getUsername());
        assertEquals("johnNew@email.com", user.getEmail());
        assertEquals("54321", user.getZipCode());
    }
}
