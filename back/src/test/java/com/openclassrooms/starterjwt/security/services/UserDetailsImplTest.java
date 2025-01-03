package com.openclassrooms.starterjwt.security.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;

import org.junit.jupiter.api.Test;

class UserDetailsImplTest {

    @Test
    void testConstructorAndGetters() {
        // Arrange
        Long id = 1L;
        String username = "test@example.com";
        String firstName = "John";
        String lastName = "Doe";
        Boolean admin = true;
        String password = "password123";

        // Act
        UserDetailsImpl userDetails = new UserDetailsImpl(firstName, lastName, username, password);

        // Assert
        assertEquals(username, userDetails.getUsername());
        assertEquals(firstName, userDetails.getFirstName());
        assertEquals(lastName, userDetails.getLastName());
        assertEquals(password, userDetails.getPassword());
        assertNotNull(userDetails.getAuthorities());
        assertEquals(0, userDetails.getAuthorities().size()); // Par défaut, aucune autorité
    }

    @Test
    void testIsAccountNonExpired() {
        // Act
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .password("password123")
                .build();

        // Assert
        assertTrue(userDetails.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked() {
        // Act
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .password("password123")
                .build();

        // Assert
        assertTrue(userDetails.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired() {
        // Act
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .password("password123")
                .build();

        // Assert
        assertTrue(userDetails.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled() {
        // Act
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .password("password123")
                .build();

        // Assert
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void testEquals() {
        // Arrange
        UserDetailsImpl user1 = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .password("password123")
                .build();

        UserDetailsImpl user2 = UserDetailsImpl.builder()
                .id(1L)
                .username("other@example.com")
                .password("password456")
                .build();

        UserDetailsImpl user3 = UserDetailsImpl.builder()
                .id(2L)
                .username("test@example.com")
                .password("password123")
                .build();

        // Assert
        assertEquals(user1, user2); // Même ID
        assertNotEquals(user1, user3); // ID différent
    }

    @Test
    void testAuthorities() {
        // Act
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .password("password123")
                .build();

        // Assert
        assertNotNull(userDetails.getAuthorities());
        assertTrue(userDetails.getAuthorities() instanceof HashSet);
        assertTrue(userDetails.getAuthorities().isEmpty());
    }
}