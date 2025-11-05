package io.spring.core.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UserComprehensiveTest {

    @Test
    void shouldCreateUserWithAllFields() {
        User user = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
        
        assertNotNull(user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("testuser", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("bio", user.getBio());
        assertEquals("image.jpg", user.getImage());
    }

    @Test
    void shouldCreateUserWithDefaultConstructor() {
        User user = new User();
        
        assertNull(user.getId());
        assertNull(user.getEmail());
        assertNull(user.getUsername());
        assertNull(user.getPassword());
        assertNull(user.getBio());
        assertNull(user.getImage());
    }

    @Test
    void shouldImplementEqualsBasedOnIdOnly() {
        User user1 = new User("email1@example.com", "user1", "pass1", "bio1", "image1.jpg");
        User user2 = new User("email2@example.com", "user2", "pass2", "bio2", "image2.jpg");
        
        assertNotEquals(user1, user2);
        assertNotEquals(user1.getId(), user2.getId());
    }

    @Test
    void shouldImplementEqualsWithNullIds() {
        User user1 = new User();
        User user2 = new User();
        
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void shouldImplementHashCodeCorrectly() {
        User user = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
        
        int hashCode1 = user.hashCode();
        int hashCode2 = user.hashCode();
        
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void shouldImplementHashCodeWithNullId() {
        User user1 = new User();
        User user2 = new User();
        
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void shouldTestCanEqualMethod() {
        User user1 = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
        User user2 = new User("test2@example.com", "testuser2", "password2", "bio2", "image2.jpg");
        String other = "not a User";
        
        assertTrue(user1.canEqual(user2));
        assertFalse(user1.canEqual(other));
    }

    @Test
    void shouldNotBeEqualToNull() {
        User user = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
        
        assertNotEquals(user, null);
    }

    @Test
    void shouldNotBeEqualToDifferentClass() {
        User user = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
        String other = "not a User";
        
        assertNotEquals(user, other);
    }

    @Test
    void shouldBeEqualToItself() {
        User user = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
        
        assertEquals(user, user);
    }

    @Test
    void shouldUpdateEmailWhenNotEmpty() {
        User user = new User("old@example.com", "testuser", "password", "bio", "image.jpg");
        
        user.update("new@example.com", null, null, null, null);
        
        assertEquals("new@example.com", user.getEmail());
        assertEquals("testuser", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("bio", user.getBio());
        assertEquals("image.jpg", user.getImage());
    }

    @Test
    void shouldNotUpdateEmailWhenEmpty() {
        User user = new User("old@example.com", "testuser", "password", "bio", "image.jpg");
        
        user.update("", null, null, null, null);
        
        assertEquals("old@example.com", user.getEmail());
    }

    @Test
    void shouldNotUpdateEmailWhenNull() {
        User user = new User("old@example.com", "testuser", "password", "bio", "image.jpg");
        
        user.update(null, null, null, null, null);
        
        assertEquals("old@example.com", user.getEmail());
    }

    @Test
    void shouldUpdateUsernameWhenNotEmpty() {
        User user = new User("test@example.com", "olduser", "password", "bio", "image.jpg");
        
        user.update(null, "newuser", null, null, null);
        
        assertEquals("test@example.com", user.getEmail());
        assertEquals("newuser", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("bio", user.getBio());
        assertEquals("image.jpg", user.getImage());
    }

    @Test
    void shouldNotUpdateUsernameWhenEmpty() {
        User user = new User("test@example.com", "olduser", "password", "bio", "image.jpg");
        
        user.update(null, "", null, null, null);
        
        assertEquals("olduser", user.getUsername());
    }

    @Test
    void shouldNotUpdateUsernameWhenNull() {
        User user = new User("test@example.com", "olduser", "password", "bio", "image.jpg");
        
        user.update(null, null, null, null, null);
        
        assertEquals("olduser", user.getUsername());
    }

    @Test
    void shouldUpdatePasswordWhenNotEmpty() {
        User user = new User("test@example.com", "testuser", "oldpass", "bio", "image.jpg");
        
        user.update(null, null, "newpass", null, null);
        
        assertEquals("test@example.com", user.getEmail());
        assertEquals("testuser", user.getUsername());
        assertEquals("newpass", user.getPassword());
        assertEquals("bio", user.getBio());
        assertEquals("image.jpg", user.getImage());
    }

    @Test
    void shouldNotUpdatePasswordWhenEmpty() {
        User user = new User("test@example.com", "testuser", "oldpass", "bio", "image.jpg");
        
        user.update(null, null, "", null, null);
        
        assertEquals("oldpass", user.getPassword());
    }

    @Test
    void shouldNotUpdatePasswordWhenNull() {
        User user = new User("test@example.com", "testuser", "oldpass", "bio", "image.jpg");
        
        user.update(null, null, null, null, null);
        
        assertEquals("oldpass", user.getPassword());
    }

    @Test
    void shouldUpdateBioWhenNotEmpty() {
        User user = new User("test@example.com", "testuser", "password", "oldbio", "image.jpg");
        
        user.update(null, null, null, "newbio", null);
        
        assertEquals("test@example.com", user.getEmail());
        assertEquals("testuser", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("newbio", user.getBio());
        assertEquals("image.jpg", user.getImage());
    }

    @Test
    void shouldNotUpdateBioWhenEmpty() {
        User user = new User("test@example.com", "testuser", "password", "oldbio", "image.jpg");
        
        user.update(null, null, null, "", null);
        
        assertEquals("oldbio", user.getBio());
    }

    @Test
    void shouldNotUpdateBioWhenNull() {
        User user = new User("test@example.com", "testuser", "password", "oldbio", "image.jpg");
        
        user.update(null, null, null, null, null);
        
        assertEquals("oldbio", user.getBio());
    }

    @Test
    void shouldUpdateImageWhenNotEmpty() {
        User user = new User("test@example.com", "testuser", "password", "bio", "oldimage.jpg");
        
        user.update(null, null, null, null, "newimage.jpg");
        
        assertEquals("test@example.com", user.getEmail());
        assertEquals("testuser", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("bio", user.getBio());
        assertEquals("newimage.jpg", user.getImage());
    }

    @Test
    void shouldNotUpdateImageWhenEmpty() {
        User user = new User("test@example.com", "testuser", "password", "bio", "oldimage.jpg");
        
        user.update(null, null, null, null, "");
        
        assertEquals("oldimage.jpg", user.getImage());
    }

    @Test
    void shouldNotUpdateImageWhenNull() {
        User user = new User("test@example.com", "testuser", "password", "bio", "oldimage.jpg");
        
        user.update(null, null, null, null, null);
        
        assertEquals("oldimage.jpg", user.getImage());
    }

    @Test
    void shouldUpdateAllFieldsWhenNotEmpty() {
        User user = new User("old@example.com", "olduser", "oldpass", "oldbio", "oldimage.jpg");
        
        user.update("new@example.com", "newuser", "newpass", "newbio", "newimage.jpg");
        
        assertEquals("new@example.com", user.getEmail());
        assertEquals("newuser", user.getUsername());
        assertEquals("newpass", user.getPassword());
        assertEquals("newbio", user.getBio());
        assertEquals("newimage.jpg", user.getImage());
    }

    @Test
    void shouldHandleEmptyStringsInConstructor() {
        User user = new User("", "", "", "", "");
        
        assertEquals("", user.getEmail());
        assertEquals("", user.getUsername());
        assertEquals("", user.getPassword());
        assertEquals("", user.getBio());
        assertEquals("", user.getImage());
    }

    @Test
    void shouldHandleNullParametersInConstructor() {
        User user = new User(null, null, null, null, null);
        
        assertNotNull(user.getId());
        assertNull(user.getEmail());
        assertNull(user.getUsername());
        assertNull(user.getPassword());
        assertNull(user.getBio());
        assertNull(user.getImage());
    }

    @Test
    void shouldGenerateUniqueIds() {
        User user1 = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
        User user2 = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
        
        assertNotEquals(user1.getId(), user2.getId());
        assertNotEquals(user1, user2);
    }

    @Test
    void shouldTestToStringMethod() {
        User user = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
        
        String toString = user.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("User"));
    }

    @Test
    void shouldTestToStringWithNullFields() {
        User user = new User();
        
        String toString = user.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("User"));
    }

    @Test
    void shouldTestHashCodeConsistency() {
        User user = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
        
        int hashCode1 = user.hashCode();
        int hashCode2 = user.hashCode();
        
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void shouldTestHashCodeWithNullFieldsVariations() {
        User user1 = new User();
        User user2 = new User();
        User user3 = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
        
        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1.hashCode(), user3.hashCode());
    }

    @Test
    void shouldHandleLongContent() {
        String longEmail = "very-long-email-address@".repeat(10) + "example.com";
        String longUsername = "very-long-username-".repeat(10);
        String longPassword = "very-long-password-".repeat(10);
        String longBio = "This is a very long bio that might be used in real applications. ".repeat(100);
        String longImage = "https://very-long-image-url.com/".repeat(10) + "image.jpg";
        
        User user = new User(longEmail, longUsername, longPassword, longBio, longImage);
        
        assertEquals(longEmail, user.getEmail());
        assertEquals(longUsername, user.getUsername());
        assertEquals(longPassword, user.getPassword());
        assertEquals(longBio, user.getBio());
        assertEquals(longImage, user.getImage());
    }

    @Test
    void shouldHandleSpecialCharactersInContent() {
        String specialEmail = "user!@#$%@example.com";
        String specialUsername = "user!@#$%^&*()";
        String specialPassword = "pass!@#$%^&*()_+-=[]{}|;':\",./<>?";
        String specialBio = "Bio with special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?";
        String specialImage = "https://example.com/image!@#$%.jpg";
        
        User user = new User(specialEmail, specialUsername, specialPassword, specialBio, specialImage);
        
        assertEquals(specialEmail, user.getEmail());
        assertEquals(specialUsername, user.getUsername());
        assertEquals(specialPassword, user.getPassword());
        assertEquals(specialBio, user.getBio());
        assertEquals(specialImage, user.getImage());
    }

    @Test
    void shouldUpdateWithMixedEmptyAndNonEmptyValues() {
        User user = new User("old@example.com", "olduser", "oldpass", "oldbio", "oldimage.jpg");
        
        user.update("new@example.com", "", "newpass", null, "newimage.jpg");
        
        assertEquals("new@example.com", user.getEmail());
        assertEquals("olduser", user.getUsername());
        assertEquals("newpass", user.getPassword());
        assertEquals("oldbio", user.getBio());
        assertEquals("newimage.jpg", user.getImage());
    }
}
