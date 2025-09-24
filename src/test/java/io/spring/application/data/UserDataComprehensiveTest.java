package io.spring.application.data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UserDataComprehensiveTest {

    @Test
    void shouldCreateUserDataWithAllFields() {
        UserData userData = new UserData("id1", "user@example.com", "username", "bio", "image.jpg");
        
        assertEquals("id1", userData.getId());
        assertEquals("user@example.com", userData.getEmail());
        assertEquals("username", userData.getUsername());
        assertEquals("bio", userData.getBio());
        assertEquals("image.jpg", userData.getImage());
    }

    @Test
    void shouldCreateUserDataWithDefaultConstructor() {
        UserData userData = new UserData();
        
        assertNull(userData.getId());
        assertNull(userData.getEmail());
        assertNull(userData.getUsername());
        assertNull(userData.getBio());
        assertNull(userData.getImage());
    }

    @Test
    void shouldSetAllFields() {
        UserData userData = new UserData();
        
        userData.setId("id1");
        userData.setEmail("user@example.com");
        userData.setUsername("username");
        userData.setBio("bio");
        userData.setImage("image.jpg");
        
        assertEquals("id1", userData.getId());
        assertEquals("user@example.com", userData.getEmail());
        assertEquals("username", userData.getUsername());
        assertEquals("bio", userData.getBio());
        assertEquals("image.jpg", userData.getImage());
    }

    @Test
    void shouldHandleNullFieldsInConstructor() {
        UserData userData = new UserData(null, null, null, null, null);
        
        assertNull(userData.getId());
        assertNull(userData.getEmail());
        assertNull(userData.getUsername());
        assertNull(userData.getBio());
        assertNull(userData.getImage());
    }

    @Test
    void shouldSetNullFields() {
        UserData userData = new UserData("id1", "user@example.com", "username", "bio", "image.jpg");
        
        userData.setId(null);
        userData.setEmail(null);
        userData.setUsername(null);
        userData.setBio(null);
        userData.setImage(null);
        
        assertNull(userData.getId());
        assertNull(userData.getEmail());
        assertNull(userData.getUsername());
        assertNull(userData.getBio());
        assertNull(userData.getImage());
    }

    @Test
    void shouldBeEqualWhenAllFieldsMatch() {
        UserData userData1 = new UserData("id1", "user@example.com", "username", "bio", "image.jpg");
        UserData userData2 = new UserData("id1", "user@example.com", "username", "bio", "image.jpg");
        
        assertEquals(userData1, userData2);
        assertEquals(userData1.hashCode(), userData2.hashCode());
    }

    @Test
    void shouldBeEqualWhenAllFieldsAreNull() {
        UserData userData1 = new UserData(null, null, null, null, null);
        UserData userData2 = new UserData(null, null, null, null, null);
        
        assertEquals(userData1, userData2);
        assertEquals(userData1.hashCode(), userData2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenIdDiffers() {
        UserData userData1 = new UserData("id1", "user@example.com", "username", "bio", "image.jpg");
        UserData userData2 = new UserData("id2", "user@example.com", "username", "bio", "image.jpg");
        
        assertNotEquals(userData1, userData2);
    }

    @Test
    void shouldNotBeEqualWhenEmailDiffers() {
        UserData userData1 = new UserData("id1", "user1@example.com", "username", "bio", "image.jpg");
        UserData userData2 = new UserData("id1", "user2@example.com", "username", "bio", "image.jpg");
        
        assertNotEquals(userData1, userData2);
    }

    @Test
    void shouldNotBeEqualWhenUsernameDiffers() {
        UserData userData1 = new UserData("id1", "user@example.com", "username1", "bio", "image.jpg");
        UserData userData2 = new UserData("id1", "user@example.com", "username2", "bio", "image.jpg");
        
        assertNotEquals(userData1, userData2);
    }

    @Test
    void shouldNotBeEqualWhenBioDiffers() {
        UserData userData1 = new UserData("id1", "user@example.com", "username", "bio1", "image.jpg");
        UserData userData2 = new UserData("id1", "user@example.com", "username", "bio2", "image.jpg");
        
        assertNotEquals(userData1, userData2);
    }

    @Test
    void shouldNotBeEqualWhenImageDiffers() {
        UserData userData1 = new UserData("id1", "user@example.com", "username", "bio", "image1.jpg");
        UserData userData2 = new UserData("id1", "user@example.com", "username", "bio", "image2.jpg");
        
        assertNotEquals(userData1, userData2);
    }

    @Test
    void shouldNotBeEqualWhenOneFieldIsNull() {
        UserData userData1 = new UserData("id1", "user@example.com", "username", "bio", "image.jpg");
        UserData userData2 = new UserData(null, "user@example.com", "username", "bio", "image.jpg");
        
        assertNotEquals(userData1, userData2);
    }

    @Test
    void shouldNotBeEqualWhenEmailIsNull() {
        UserData userData1 = new UserData("id1", "user@example.com", "username", "bio", "image.jpg");
        UserData userData2 = new UserData("id1", null, "username", "bio", "image.jpg");
        
        assertNotEquals(userData1, userData2);
    }

    @Test
    void shouldNotBeEqualWhenUsernameIsNull() {
        UserData userData1 = new UserData("id1", "user@example.com", "username", "bio", "image.jpg");
        UserData userData2 = new UserData("id1", "user@example.com", null, "bio", "image.jpg");
        
        assertNotEquals(userData1, userData2);
    }

    @Test
    void shouldNotBeEqualWhenBioIsNull() {
        UserData userData1 = new UserData("id1", "user@example.com", "username", "bio", "image.jpg");
        UserData userData2 = new UserData("id1", "user@example.com", "username", null, "image.jpg");
        
        assertNotEquals(userData1, userData2);
    }

    @Test
    void shouldNotBeEqualWhenImageIsNull() {
        UserData userData1 = new UserData("id1", "user@example.com", "username", "bio", "image.jpg");
        UserData userData2 = new UserData("id1", "user@example.com", "username", "bio", null);
        
        assertNotEquals(userData1, userData2);
    }

    @Test
    void shouldNotBeEqualToNull() {
        UserData userData = new UserData("id1", "user@example.com", "username", "bio", "image.jpg");
        
        assertNotEquals(userData, null);
    }

    @Test
    void shouldNotBeEqualToDifferentClass() {
        UserData userData = new UserData("id1", "user@example.com", "username", "bio", "image.jpg");
        String other = "not a UserData";
        
        assertNotEquals(userData, other);
    }

    @Test
    void shouldBeEqualToItself() {
        UserData userData = new UserData("id1", "user@example.com", "username", "bio", "image.jpg");
        
        assertEquals(userData, userData);
    }

    @Test
    void shouldHaveConsistentHashCode() {
        UserData userData = new UserData("id1", "user@example.com", "username", "bio", "image.jpg");
        
        int hashCode1 = userData.hashCode();
        int hashCode2 = userData.hashCode();
        
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void shouldHaveDifferentHashCodeForDifferentObjects() {
        UserData userData1 = new UserData("id1", "user@example.com", "username", "bio", "image.jpg");
        UserData userData2 = new UserData("id2", "user@example.com", "username", "bio", "image.jpg");
        
        assertNotEquals(userData1.hashCode(), userData2.hashCode());
    }

    @Test
    void shouldHandleNullFieldsInHashCode() {
        UserData userData1 = new UserData(null, null, null, null, null);
        UserData userData2 = new UserData(null, null, null, null, null);
        
        assertEquals(userData1.hashCode(), userData2.hashCode());
    }

    @Test
    void shouldHandleMixedNullFieldsInHashCode() {
        UserData userData1 = new UserData("id1", null, "username", null, "image.jpg");
        UserData userData2 = new UserData("id1", null, "username", null, "image.jpg");
        
        assertEquals(userData1.hashCode(), userData2.hashCode());
    }

    @Test
    void shouldGenerateToString() {
        UserData userData = new UserData("id1", "user@example.com", "username", "bio", "image.jpg");
        
        String toString = userData.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("UserData"));
        assertTrue(toString.contains("id1"));
        assertTrue(toString.contains("user@example.com"));
        assertTrue(toString.contains("username"));
        assertTrue(toString.contains("bio"));
        assertTrue(toString.contains("image.jpg"));
    }

    @Test
    void shouldGenerateToStringWithNullFields() {
        UserData userData = new UserData(null, null, null, null, null);
        
        String toString = userData.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("UserData"));
    }

    @Test
    void shouldSupportCanEqual() {
        UserData userData1 = new UserData("id1", "user@example.com", "username", "bio", "image.jpg");
        UserData userData2 = new UserData("id1", "user@example.com", "username", "bio", "image.jpg");
        
        assertTrue(userData1.canEqual(userData2));
        assertTrue(userData2.canEqual(userData1));
    }

    @Test
    void shouldNotCanEqualDifferentClass() {
        UserData userData = new UserData("id1", "user@example.com", "username", "bio", "image.jpg");
        String other = "not a UserData";
        
        assertFalse(userData.canEqual(other));
    }
}
