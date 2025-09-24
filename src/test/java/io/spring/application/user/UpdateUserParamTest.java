package io.spring.application.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UpdateUserParamTest {

    @Test
    void shouldCreateUpdateUserParamWithDefaultConstructor() {
        UpdateUserParam param = new UpdateUserParam();
        
        assertEquals("", param.getEmail());
        assertEquals("", param.getPassword());
        assertEquals("", param.getUsername());
        assertEquals("", param.getBio());
        assertEquals("", param.getImage());
    }

    @Test
    void shouldCreateUpdateUserParamWithAllArgsConstructor() {
        UpdateUserParam param = new UpdateUserParam(
            "test@example.com",
            "password123",
            "testuser",
            "Test bio",
            "https://example.com/image.jpg"
        );
        
        assertEquals("test@example.com", param.getEmail());
        assertEquals("password123", param.getPassword());
        assertEquals("testuser", param.getUsername());
        assertEquals("Test bio", param.getBio());
        assertEquals("https://example.com/image.jpg", param.getImage());
    }

    @Test
    void shouldCreateUpdateUserParamWithBuilder() {
        UpdateUserParam param = UpdateUserParam.builder()
            .email("builder@example.com")
            .password("builderpass")
            .username("builderuser")
            .bio("Builder bio")
            .image("https://example.com/builder.jpg")
            .build();
        
        assertEquals("builder@example.com", param.getEmail());
        assertEquals("builderpass", param.getPassword());
        assertEquals("builderuser", param.getUsername());
        assertEquals("Builder bio", param.getBio());
        assertEquals("https://example.com/builder.jpg", param.getImage());
    }

    @Test
    void shouldCreateUpdateUserParamWithPartialBuilder() {
        UpdateUserParam param = UpdateUserParam.builder()
            .email("partial@example.com")
            .username("partialuser")
            .build();
        
        assertEquals("partial@example.com", param.getEmail());
        assertEquals("", param.getPassword());
        assertEquals("partialuser", param.getUsername());
        assertEquals("", param.getBio());
        assertEquals("", param.getImage());
    }

    @Test
    void shouldCreateUpdateUserParamWithEmptyBuilder() {
        UpdateUserParam param = UpdateUserParam.builder().build();
        
        assertEquals("", param.getEmail());
        assertEquals("", param.getPassword());
        assertEquals("", param.getUsername());
        assertEquals("", param.getBio());
        assertEquals("", param.getImage());
    }

    @Test
    void shouldHandleNullValuesInConstructor() {
        UpdateUserParam param = new UpdateUserParam(null, null, null, null, null);
        
        assertNull(param.getEmail());
        assertNull(param.getPassword());
        assertNull(param.getUsername());
        assertNull(param.getBio());
        assertNull(param.getImage());
    }

    @Test
    void shouldHandleNullValuesInBuilder() {
        UpdateUserParam param = UpdateUserParam.builder()
            .email(null)
            .password(null)
            .username(null)
            .bio(null)
            .image(null)
            .build();
        
        assertNull(param.getEmail());
        assertNull(param.getPassword());
        assertNull(param.getUsername());
        assertNull(param.getBio());
        assertNull(param.getImage());
    }

    @Test
    void shouldHandleEmptyStrings() {
        UpdateUserParam param = new UpdateUserParam("", "", "", "", "");
        
        assertEquals("", param.getEmail());
        assertEquals("", param.getPassword());
        assertEquals("", param.getUsername());
        assertEquals("", param.getBio());
        assertEquals("", param.getImage());
    }

    @Test
    void shouldHandleLongContent() {
        String longEmail = "very-long-email-".repeat(10) + "@example.com";
        String longPassword = "very-long-password-".repeat(20);
        String longUsername = "very-long-username-".repeat(10);
        String longBio = "This is a very long bio that might be used in real applications. ".repeat(100);
        String longImage = "https://very-long-image-url.com/".repeat(10);
        
        UpdateUserParam param = new UpdateUserParam(longEmail, longPassword, longUsername, longBio, longImage);
        
        assertEquals(longEmail, param.getEmail());
        assertEquals(longPassword, param.getPassword());
        assertEquals(longUsername, param.getUsername());
        assertEquals(longBio, param.getBio());
        assertEquals(longImage, param.getImage());
    }

    @Test
    void shouldHandleSpecialCharactersInContent() {
        String specialEmail = "user+test@example-domain.co.uk";
        String specialPassword = "pass!@#$%^&*()_+-=[]{}|;':\",./<>?";
        String specialUsername = "user!@#$%^&*()";
        String specialBio = "Bio with special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?";
        String specialImage = "https://example.com/image!@#$%.jpg";
        
        UpdateUserParam param = new UpdateUserParam(specialEmail, specialPassword, specialUsername, specialBio, specialImage);
        
        assertEquals(specialEmail, param.getEmail());
        assertEquals(specialPassword, param.getPassword());
        assertEquals(specialUsername, param.getUsername());
        assertEquals(specialBio, param.getBio());
        assertEquals(specialImage, param.getImage());
    }

    @Test
    void shouldUseDefaultObjectEquality() {
        UpdateUserParam param1 = new UpdateUserParam("test@example.com", "password", "user", "bio", "image");
        UpdateUserParam param2 = new UpdateUserParam("test@example.com", "password", "user", "bio", "image");
        
        assertNotEquals(param1, param2);
        assertNotEquals(param1.hashCode(), param2.hashCode());
    }

    @Test
    void shouldBeEqualToItself() {
        UpdateUserParam param = new UpdateUserParam("test@example.com", "password", "user", "bio", "image");
        
        assertEquals(param, param);
    }

    @Test
    void shouldImplementToStringCorrectly() {
        UpdateUserParam param = new UpdateUserParam("test@example.com", "password", "user", "bio", "image");
        
        String toString = param.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("UpdateUserParam"));
    }

    @Test
    void shouldImplementToStringWithNullFields() {
        UpdateUserParam param = new UpdateUserParam(null, null, null, null, null);
        
        String toString = param.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("UpdateUserParam"));
    }

    @Test
    void shouldTestHashCodeConsistency() {
        UpdateUserParam param = new UpdateUserParam("test@example.com", "password", "user", "bio", "image");
        
        int hashCode1 = param.hashCode();
        int hashCode2 = param.hashCode();
        
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void shouldTestBuilderChaining() {
        UpdateUserParam param = UpdateUserParam.builder()
            .email("chain@example.com")
            .password("chainpass")
            .username("chainuser")
            .bio("Chain bio")
            .image("https://example.com/chain.jpg")
            .build();
        
        assertEquals("chain@example.com", param.getEmail());
        assertEquals("chainpass", param.getPassword());
        assertEquals("chainuser", param.getUsername());
        assertEquals("Chain bio", param.getBio());
        assertEquals("https://example.com/chain.jpg", param.getImage());
    }

    @Test
    void shouldTestBuilderWithOverrides() {
        UpdateUserParam param = UpdateUserParam.builder()
            .email("first@example.com")
            .email("second@example.com")
            .username("firstuser")
            .username("seconduser")
            .build();
        
        assertEquals("second@example.com", param.getEmail());
        assertEquals("seconduser", param.getUsername());
        assertEquals("", param.getPassword());
        assertEquals("", param.getBio());
        assertEquals("", param.getImage());
    }
}
