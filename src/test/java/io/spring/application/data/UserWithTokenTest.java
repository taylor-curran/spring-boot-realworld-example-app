package io.spring.application.data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UserWithTokenTest {

    @Test
    void shouldCreateUserWithTokenFromUserData() {
        UserData userData = new UserData("user-id", "test@example.com", "testuser", "Test Bio", "avatar.jpg");
        String token = "jwt-token-123";

        UserWithToken userWithToken = new UserWithToken(userData, token);

        assertEquals("test@example.com", userWithToken.getEmail());
        assertEquals("testuser", userWithToken.getUsername());
        assertEquals("Test Bio", userWithToken.getBio());
        assertEquals("avatar.jpg", userWithToken.getImage());
        assertEquals("jwt-token-123", userWithToken.getToken());
    }

    @Test
    void shouldHandleEmptyUserDataFields() {
        UserData userData = new UserData("id", "", "", "", "");
        String token = "token";

        UserWithToken userWithToken = new UserWithToken(userData, token);

        assertEquals("", userWithToken.getEmail());
        assertEquals("", userWithToken.getUsername());
        assertEquals("", userWithToken.getBio());
        assertEquals("", userWithToken.getImage());
        assertEquals("token", userWithToken.getToken());
    }

    @Test
    void shouldHandleNullUserDataFields() {
        UserData userData = new UserData("id", null, null, null, null);
        String token = "token";

        UserWithToken userWithToken = new UserWithToken(userData, token);

        assertNull(userWithToken.getEmail());
        assertNull(userWithToken.getUsername());
        assertNull(userWithToken.getBio());
        assertNull(userWithToken.getImage());
        assertEquals("token", userWithToken.getToken());
    }

    @Test
    void shouldHandleEmptyToken() {
        UserData userData = new UserData("id", "email@test.com", "user", "bio", "image.jpg");
        String emptyToken = "";

        UserWithToken userWithToken = new UserWithToken(userData, emptyToken);

        assertEquals("email@test.com", userWithToken.getEmail());
        assertEquals("user", userWithToken.getUsername());
        assertEquals("bio", userWithToken.getBio());
        assertEquals("image.jpg", userWithToken.getImage());
        assertEquals("", userWithToken.getToken());
    }

    @Test
    void shouldHandleNullToken() {
        UserData userData = new UserData("id", "email@test.com", "user", "bio", "image.jpg");
        String nullToken = null;

        UserWithToken userWithToken = new UserWithToken(userData, nullToken);

        assertEquals("email@test.com", userWithToken.getEmail());
        assertEquals("user", userWithToken.getUsername());
        assertEquals("bio", userWithToken.getBio());
        assertEquals("image.jpg", userWithToken.getImage());
        assertNull(userWithToken.getToken());
    }

    @Test
    void shouldHandleLongToken() {
        UserData userData = new UserData("id", "email@test.com", "user", "bio", "image.jpg");
        String longToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

        UserWithToken userWithToken = new UserWithToken(userData, longToken);

        assertEquals(longToken, userWithToken.getToken());
    }

    @Test
    void shouldHandleSpecialCharactersInUserData() {
        UserData userData = new UserData(
            "id", 
            "test+user@example.com", 
            "user_123-test.name", 
            "Bio with special chars: !@#$%^&*()", 
            "https://example.com/avatar/user123.jpg"
        );
        String token = "token";

        UserWithToken userWithToken = new UserWithToken(userData, token);

        assertEquals("test+user@example.com", userWithToken.getEmail());
        assertEquals("user_123-test.name", userWithToken.getUsername());
        assertEquals("Bio with special chars: !@#$%^&*()", userWithToken.getBio());
        assertEquals("https://example.com/avatar/user123.jpg", userWithToken.getImage());
    }

    @Test
    void shouldHandleUnicodeInUserData() {
        UserData userData = new UserData(
            "id", 
            "用户@example.com", 
            "用户名", 
            "用户简介 with emoji 🌍", 
            "avatar.jpg"
        );
        String token = "token";

        UserWithToken userWithToken = new UserWithToken(userData, token);

        assertEquals("用户@example.com", userWithToken.getEmail());
        assertEquals("用户名", userWithToken.getUsername());
        assertEquals("用户简介 with emoji 🌍", userWithToken.getBio());
    }

    @Test
    void shouldHandleLongBio() {
        String longBio = "This is a very long user biography that contains multiple sentences and might be used to test how the system handles longer text content in user profiles. ".repeat(5);
        UserData userData = new UserData("id", "email@test.com", "user", longBio, "image.jpg");
        String token = "token";

        UserWithToken userWithToken = new UserWithToken(userData, token);

        assertEquals(longBio, userWithToken.getBio());
    }

    @Test
    void shouldHandleImageUrls() {
        UserData userData = new UserData(
            "id", 
            "email@test.com", 
            "user", 
            "bio", 
            "https://cdn.example.com/avatars/user123.png?size=200&format=webp"
        );
        String token = "token";

        UserWithToken userWithToken = new UserWithToken(userData, token);

        assertEquals("https://cdn.example.com/avatars/user123.png?size=200&format=webp", userWithToken.getImage());
    }

    @Test
    void shouldHandleUuidId() {
        String uuidId = "550e8400-e29b-41d4-a716-446655440000";
        UserData userData = new UserData(uuidId, "email@test.com", "user", "bio", "image.jpg");
        String token = "token";

        UserWithToken userWithToken = new UserWithToken(userData, token);

        assertEquals("email@test.com", userWithToken.getEmail());
        assertEquals("user", userWithToken.getUsername());
    }
}
