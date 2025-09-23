package io.spring.application.data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UserDataTest {

    @Test
    void shouldCreateUserDataWithAllFields() {
        String id = "user-id-123";
        String email = "test@example.com";
        String username = "testuser";
        String bio = "Test user biography";
        String image = "avatar.jpg";

        UserData userData = new UserData(id, email, username, bio, image);

        assertEquals(id, userData.getId());
        assertEquals(email, userData.getEmail());
        assertEquals(username, userData.getUsername());
        assertEquals(bio, userData.getBio());
        assertEquals(image, userData.getImage());
    }

    @Test
    void shouldCreateUserDataWithNoArgsConstructor() {
        UserData userData = new UserData();

        assertNull(userData.getId());
        assertNull(userData.getEmail());
        assertNull(userData.getUsername());
        assertNull(userData.getBio());
        assertNull(userData.getImage());
    }

    @Test
    void shouldSetFieldsUsingSetters() {
        UserData userData = new UserData();
        
        userData.setId("new-id");
        userData.setEmail("new@example.com");
        userData.setUsername("newuser");
        userData.setBio("New bio");
        userData.setImage("new-avatar.jpg");

        assertEquals("new-id", userData.getId());
        assertEquals("new@example.com", userData.getEmail());
        assertEquals("newuser", userData.getUsername());
        assertEquals("New bio", userData.getBio());
        assertEquals("new-avatar.jpg", userData.getImage());
    }

    @Test
    void shouldHandleNullFields() {
        UserData userData = new UserData(null, null, null, null, null);

        assertNull(userData.getId());
        assertNull(userData.getEmail());
        assertNull(userData.getUsername());
        assertNull(userData.getBio());
        assertNull(userData.getImage());
    }

    @Test
    void shouldHandleEmptyStrings() {
        UserData userData = new UserData("", "", "", "", "");

        assertEquals("", userData.getId());
        assertEquals("", userData.getEmail());
        assertEquals("", userData.getUsername());
        assertEquals("", userData.getBio());
        assertEquals("", userData.getImage());
    }

    @Test
    void shouldImplementEqualsCorrectly() {
        UserData userData1 = new UserData("id", "email@test.com", "user", "bio", "image.jpg");
        UserData userData2 = new UserData("id", "email@test.com", "user", "bio", "image.jpg");
        UserData userData3 = new UserData("different-id", "email@test.com", "user", "bio", "image.jpg");

        assertEquals(userData1, userData2);
        assertNotEquals(userData1, userData3);
        assertNotEquals(userData1, null);
        assertNotEquals(userData1, "not a UserData object");
    }

    @Test
    void shouldImplementHashCodeCorrectly() {
        UserData userData1 = new UserData("id", "email@test.com", "user", "bio", "image.jpg");
        UserData userData2 = new UserData("id", "email@test.com", "user", "bio", "image.jpg");

        assertEquals(userData1.hashCode(), userData2.hashCode());
    }

    @Test
    void shouldImplementToStringCorrectly() {
        UserData userData = new UserData("id", "email@test.com", "user", "bio", "image.jpg");
        String toString = userData.toString();

        assertTrue(toString.contains("UserData"));
        assertTrue(toString.contains("id=id"));
        assertTrue(toString.contains("email=email@test.com"));
        assertTrue(toString.contains("username=user"));
        assertTrue(toString.contains("bio=bio"));
        assertTrue(toString.contains("image=image.jpg"));
    }

    @Test
    void shouldHandleSpecialCharactersInFields() {
        UserData userData = new UserData(
            "id-with-special-chars!@#$",
            "test+user@example.com",
            "user_name-123",
            "Bio with special chars: !@#$%^&*() and unicode: 你好世界 🌍",
            "https://example.com/avatar/user123.jpg?size=200"
        );

        assertEquals("id-with-special-chars!@#$", userData.getId());
        assertEquals("test+user@example.com", userData.getEmail());
        assertEquals("user_name-123", userData.getUsername());
        assertEquals("Bio with special chars: !@#$%^&*() and unicode: 你好世界 🌍", userData.getBio());
        assertEquals("https://example.com/avatar/user123.jpg?size=200", userData.getImage());
    }

    @Test
    void shouldHandleLongFields() {
        String longBio = "This is a very long user biography that contains multiple sentences and might be used to test how the system handles longer text content in user profiles. ".repeat(5);
        String longImageUrl = "https://cdn.example.com/very/long/path/to/user/avatar/images/with/many/subdirectories/user123.png?size=200&format=webp&quality=high&cache=true";
        
        UserData userData = new UserData("id", "email@test.com", "user", longBio, longImageUrl);

        assertEquals(longBio, userData.getBio());
        assertEquals(longImageUrl, userData.getImage());
    }

    @Test
    void shouldHandleUuidIds() {
        String uuidId = "550e8400-e29b-41d4-a716-446655440000";
        UserData userData = new UserData(uuidId, "email@test.com", "user", "bio", "image.jpg");

        assertEquals(uuidId, userData.getId());
    }

    @Test
    void shouldHandleMultilineFields() {
        String multilineBio = "Line 1 of bio\nLine 2 of bio\nLine 3 of bio\n\nWith empty line above";
        UserData userData = new UserData("id", "email@test.com", "user", multilineBio, "image.jpg");

        assertEquals(multilineBio, userData.getBio());
        assertTrue(userData.getBio().contains("\n"));
    }
}
