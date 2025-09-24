package io.spring.core.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    void shouldCreateUserWithValidData() {
        User user = new User("test@example.com", "testuser", "password123", "Test bio", "avatar.jpg");

        assertNotNull(user);
        assertEquals("test@example.com", user.getEmail());
        assertEquals("testuser", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals("Test bio", user.getBio());
        assertEquals("avatar.jpg", user.getImage());
        assertNotNull(user.getId());
    }

    @Test
    void shouldCreateUserWithMinimalData() {
        User user = new User("test@example.com", "testuser", "password123", "", "");

        assertNotNull(user);
        assertEquals("test@example.com", user.getEmail());
        assertEquals("testuser", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals("", user.getBio());
        assertEquals("", user.getImage());
    }

    @Test
    void shouldUpdateUserProfile() {
        User user = new User("test@example.com", "testuser", "password123", "Old bio", "old.jpg");
        
        user.update("test@example.com", "newuser", "newpassword", "New bio", "new.jpg");
        
        assertEquals("test@example.com", user.getEmail());
        assertEquals("newuser", user.getUsername());
        assertEquals("newpassword", user.getPassword());
        assertEquals("New bio", user.getBio());
        assertEquals("new.jpg", user.getImage());
    }

    @Test
    void shouldUpdateEmailOnly() {
        User user = new User("old@example.com", "testuser", "password123", "bio", "image.jpg");
        
        user.update("new@example.com", "testuser", "password123", "bio", "image.jpg");
        
        assertEquals("new@example.com", user.getEmail());
        assertEquals("testuser", user.getUsername());
    }

    @Test
    void shouldUpdateUsernameOnly() {
        User user = new User("test@example.com", "olduser", "password123", "bio", "image.jpg");
        
        user.update("test@example.com", "newuser", "password123", "bio", "image.jpg");
        
        assertEquals("test@example.com", user.getEmail());
        assertEquals("newuser", user.getUsername());
    }

    @Test
    void shouldUpdatePasswordOnly() {
        User user = new User("test@example.com", "testuser", "oldpassword", "bio", "image.jpg");
        
        user.update("test@example.com", "testuser", "newpassword", "bio", "image.jpg");
        
        assertEquals("test@example.com", user.getEmail());
        assertEquals("testuser", user.getUsername());
        assertEquals("newpassword", user.getPassword());
    }

    @Test
    void shouldUpdateBioOnly() {
        User user = new User("test@example.com", "testuser", "password123", "Old bio", "image.jpg");
        
        user.update("test@example.com", "testuser", "password123", "New bio", "image.jpg");
        
        assertEquals("New bio", user.getBio());
    }

    @Test
    void shouldUpdateImageOnly() {
        User user = new User("test@example.com", "testuser", "password123", "bio", "old.jpg");
        
        user.update("test@example.com", "testuser", "password123", "bio", "new.jpg");
        
        assertEquals("new.jpg", user.getImage());
    }

    @Test
    void shouldHandleNullBio() {
        User user = new User("test@example.com", "testuser", "password123", null, "image.jpg");
        
        assertNull(user.getBio());
    }

    @Test
    void shouldHandleNullImage() {
        User user = new User("test@example.com", "testuser", "password123", "bio", null);
        
        assertNull(user.getImage());
    }

    @Test
    void shouldHandleEmptyBio() {
        User user = new User("test@example.com", "testuser", "password123", "", "image.jpg");
        
        assertEquals("", user.getBio());
    }

    @Test
    void shouldHandleEmptyImage() {
        User user = new User("test@example.com", "testuser", "password123", "bio", "");
        
        assertEquals("", user.getImage());
    }

    @Test
    void shouldHandleSpecialCharactersInEmail() {
        User user = new User("test+user@example.com", "testuser", "password123", "bio", "image.jpg");
        
        assertEquals("test+user@example.com", user.getEmail());
    }

    @Test
    void shouldHandleSpecialCharactersInUsername() {
        User user = new User("test@example.com", "test_user-123", "password123", "bio", "image.jpg");
        
        assertEquals("test_user-123", user.getUsername());
    }

    @Test
    void shouldHandleSpecialCharactersInBio() {
        String specialBio = "Bio with special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?";
        User user = new User("test@example.com", "testuser", "password123", specialBio, "image.jpg");
        
        assertEquals(specialBio, user.getBio());
    }

    @Test
    void shouldHandleUnicodeInBio() {
        String unicodeBio = "Unicode bio: 测试简介 🌍 🚀 ✨";
        User user = new User("test@example.com", "testuser", "password123", unicodeBio, "image.jpg");
        
        assertEquals(unicodeBio, user.getBio());
    }

    @Test
    void shouldHandleLongBio() {
        String longBio = "This is a very long user biography that contains multiple sentences and might be used to test how the system handles longer text content in user profiles. ".repeat(10);
        User user = new User("test@example.com", "testuser", "password123", longBio, "image.jpg");
        
        assertEquals(longBio, user.getBio());
    }

    @Test
    void shouldHandleUrlInImage() {
        String imageUrl = "https://example.com/avatar.jpg?size=200&format=jpg";
        User user = new User("test@example.com", "testuser", "password123", "bio", imageUrl);
        
        assertEquals(imageUrl, user.getImage());
    }

    @Test
    void shouldHandleNewlinesInBio() {
        String bioWithNewlines = "Line 1\nLine 2\nLine 3";
        User user = new User("test@example.com", "testuser", "password123", bioWithNewlines, "image.jpg");
        
        assertEquals(bioWithNewlines, user.getBio());
    }

    @Test
    void shouldHandleTabsInBio() {
        String bioWithTabs = "Column1\tColumn2\tColumn3";
        User user = new User("test@example.com", "testuser", "password123", bioWithTabs, "image.jpg");
        
        assertEquals(bioWithTabs, user.getBio());
    }

    @Test
    void shouldUpdateWithNullValues() {
        User user = new User("test@example.com", "testuser", "password123", "bio", "image.jpg");
        
        user.update("test@example.com", "testuser", "password123", null, null);
        
        assertEquals("bio", user.getBio());
        assertEquals("image.jpg", user.getImage());
    }

    @Test
    void shouldUpdateWithEmptyValues() {
        User user = new User("test@example.com", "testuser", "password123", "bio", "image.jpg");
        
        user.update("test@example.com", "testuser", "password123", "", "");
        
        assertEquals("bio", user.getBio());
        assertEquals("image.jpg", user.getImage());
    }
}
