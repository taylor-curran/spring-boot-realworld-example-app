package io.spring.application.data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UserDataEqualsTest {

    @Test
    void shouldReturnTrueForSameInstance() {
        UserData userData = new UserData("id1", "user1", "user1@example.com", "bio1", "image1");
        
        assertTrue(userData.equals(userData));
    }

    @Test
    void shouldReturnFalseForNull() {
        UserData userData = new UserData("id1", "user1", "user1@example.com", "bio1", "image1");
        
        assertFalse(userData.equals(null));
    }

    @Test
    void shouldReturnFalseForDifferentClass() {
        UserData userData = new UserData("id1", "user1", "user1@example.com", "bio1", "image1");
        String differentObject = "not a UserData";
        
        assertFalse(userData.equals(differentObject));
    }

    @Test
    void shouldReturnTrueForEqualObjects() {
        UserData userData1 = new UserData("id1", "user1", "user1@example.com", "bio1", "image1");
        UserData userData2 = new UserData("id1", "user1", "user1@example.com", "bio1", "image1");
        
        assertTrue(userData1.equals(userData2));
        assertTrue(userData2.equals(userData1));
    }

    @Test
    void shouldReturnFalseForDifferentId() {
        UserData userData1 = new UserData("id1", "user1", "user1@example.com", "bio1", "image1");
        UserData userData2 = new UserData("id2", "user1", "user1@example.com", "bio1", "image1");
        
        assertFalse(userData1.equals(userData2));
        assertFalse(userData2.equals(userData1));
    }

    @Test
    void shouldReturnFalseForDifferentUsername() {
        UserData userData1 = new UserData("id1", "user1", "user1@example.com", "bio1", "image1");
        UserData userData2 = new UserData("id1", "user2", "user1@example.com", "bio1", "image1");
        
        assertFalse(userData1.equals(userData2));
        assertFalse(userData2.equals(userData1));
    }

    @Test
    void shouldReturnFalseForDifferentEmail() {
        UserData userData1 = new UserData("id1", "user1", "user1@example.com", "bio1", "image1");
        UserData userData2 = new UserData("id1", "user1", "user2@example.com", "bio1", "image1");
        
        assertFalse(userData1.equals(userData2));
        assertFalse(userData2.equals(userData1));
    }

    @Test
    void shouldReturnFalseForDifferentBio() {
        UserData userData1 = new UserData("id1", "user1", "user1@example.com", "bio1", "image1");
        UserData userData2 = new UserData("id1", "user1", "user1@example.com", "bio2", "image1");
        
        assertFalse(userData1.equals(userData2));
        assertFalse(userData2.equals(userData1));
    }

    @Test
    void shouldReturnFalseForDifferentImage() {
        UserData userData1 = new UserData("id1", "user1", "user1@example.com", "bio1", "image1");
        UserData userData2 = new UserData("id1", "user1", "user1@example.com", "bio1", "image2");
        
        assertFalse(userData1.equals(userData2));
        assertFalse(userData2.equals(userData1));
    }

    @Test
    void shouldHandleNullIdInEquals() {
        UserData userData1 = new UserData(null, "user1", "user1@example.com", "bio1", "image1");
        UserData userData2 = new UserData(null, "user1", "user1@example.com", "bio1", "image1");
        UserData userData3 = new UserData("id1", "user1", "user1@example.com", "bio1", "image1");
        
        assertTrue(userData1.equals(userData2));
        assertFalse(userData1.equals(userData3));
        assertFalse(userData3.equals(userData1));
    }

    @Test
    void shouldHandleNullUsernameInEquals() {
        UserData userData1 = new UserData("id1", null, "user1@example.com", "bio1", "image1");
        UserData userData2 = new UserData("id1", null, "user1@example.com", "bio1", "image1");
        UserData userData3 = new UserData("id1", "user1", "user1@example.com", "bio1", "image1");
        
        assertTrue(userData1.equals(userData2));
        assertFalse(userData1.equals(userData3));
        assertFalse(userData3.equals(userData1));
    }

    @Test
    void shouldHandleNullEmailInEquals() {
        UserData userData1 = new UserData("id1", "user1", null, "bio1", "image1");
        UserData userData2 = new UserData("id1", "user1", null, "bio1", "image1");
        UserData userData3 = new UserData("id1", "user1", "user1@example.com", "bio1", "image1");
        
        assertTrue(userData1.equals(userData2));
        assertFalse(userData1.equals(userData3));
        assertFalse(userData3.equals(userData1));
    }

    @Test
    void shouldHandleNullBioInEquals() {
        UserData userData1 = new UserData("id1", "user1", "user1@example.com", null, "image1");
        UserData userData2 = new UserData("id1", "user1", "user1@example.com", null, "image1");
        UserData userData3 = new UserData("id1", "user1", "user1@example.com", "bio1", "image1");
        
        assertTrue(userData1.equals(userData2));
        assertFalse(userData1.equals(userData3));
        assertFalse(userData3.equals(userData1));
    }

    @Test
    void shouldHandleNullImageInEquals() {
        UserData userData1 = new UserData("id1", "user1", "user1@example.com", "bio1", null);
        UserData userData2 = new UserData("id1", "user1", "user1@example.com", "bio1", null);
        UserData userData3 = new UserData("id1", "user1", "user1@example.com", "bio1", "image1");
        
        assertTrue(userData1.equals(userData2));
        assertFalse(userData1.equals(userData3));
        assertFalse(userData3.equals(userData1));
    }

    @Test
    void shouldHandleAllNullFieldsInEquals() {
        UserData userData1 = new UserData(null, null, null, null, null);
        UserData userData2 = new UserData(null, null, null, null, null);
        UserData userData3 = new UserData("id1", "user1", "user1@example.com", "bio1", "image1");
        
        assertTrue(userData1.equals(userData2));
        assertFalse(userData1.equals(userData3));
        assertFalse(userData3.equals(userData1));
    }

    @Test
    void shouldMaintainHashCodeContract() {
        UserData userData1 = new UserData("id1", "user1", "user1@example.com", "bio1", "image1");
        UserData userData2 = new UserData("id1", "user1", "user1@example.com", "bio1", "image1");
        
        assertTrue(userData1.equals(userData2));
        assertEquals(userData1.hashCode(), userData2.hashCode());
    }

    @Test
    void shouldMaintainHashCodeContractWithNulls() {
        UserData userData1 = new UserData(null, null, null, null, null);
        UserData userData2 = new UserData(null, null, null, null, null);
        
        assertTrue(userData1.equals(userData2));
        assertEquals(userData1.hashCode(), userData2.hashCode());
    }

    @Test
    void shouldHandleCanEqualMethod() {
        UserData userData1 = new UserData("id1", "user1", "user1@example.com", "bio1", "image1");
        UserData userData2 = new UserData("id1", "user1", "user1@example.com", "bio1", "image1");
        
        assertTrue(userData1.canEqual(userData2));
        assertTrue(userData2.canEqual(userData1));
        assertFalse(userData1.canEqual("not a UserData"));
        assertFalse(userData1.canEqual(null));
    }

    @Test
    void shouldTestTransitivityOfEquals() {
        UserData userData1 = new UserData("id1", "user1", "user1@example.com", "bio1", "image1");
        UserData userData2 = new UserData("id1", "user1", "user1@example.com", "bio1", "image1");
        UserData userData3 = new UserData("id1", "user1", "user1@example.com", "bio1", "image1");
        
        assertTrue(userData1.equals(userData2));
        assertTrue(userData2.equals(userData3));
        assertTrue(userData1.equals(userData3));
    }

    @Test
    void shouldTestSymmetryOfEquals() {
        UserData userData1 = new UserData("id1", "user1", "user1@example.com", "bio1", "image1");
        UserData userData2 = new UserData("id1", "user1", "user1@example.com", "bio1", "image1");
        
        assertTrue(userData1.equals(userData2));
        assertTrue(userData2.equals(userData1));
    }

    @Test
    void shouldTestReflexivityOfEquals() {
        UserData userData = new UserData("id1", "user1", "user1@example.com", "bio1", "image1");
        
        assertTrue(userData.equals(userData));
    }
}
