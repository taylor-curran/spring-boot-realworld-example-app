package io.spring.application.data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ProfileDataComprehensiveTest {

    @Test
    void shouldCreateProfileDataWithAllFields() {
        ProfileData profileData = new ProfileData("profile-id", "username", "bio", "image", true);
        
        assertEquals("profile-id", profileData.getId());
        assertEquals("username", profileData.getUsername());
        assertEquals("bio", profileData.getBio());
        assertEquals("image", profileData.getImage());
        assertTrue(profileData.isFollowing());
    }

    @Test
    void shouldCreateProfileDataWithDefaultConstructor() {
        ProfileData profileData = new ProfileData();
        
        assertNull(profileData.getId());
        assertNull(profileData.getUsername());
        assertNull(profileData.getBio());
        assertNull(profileData.getImage());
        assertFalse(profileData.isFollowing());
    }

    @Test
    void shouldSetFieldsUsingSetters() {
        ProfileData profileData = new ProfileData();
        
        profileData.setId("profile-id");
        profileData.setUsername("username");
        profileData.setBio("bio");
        profileData.setImage("image");
        profileData.setFollowing(true);
        
        assertEquals("profile-id", profileData.getId());
        assertEquals("username", profileData.getUsername());
        assertEquals("bio", profileData.getBio());
        assertEquals("image", profileData.getImage());
        assertTrue(profileData.isFollowing());
    }

    @Test
    void shouldImplementEqualsCorrectly() {
        ProfileData profileData1 = new ProfileData("profile-id", "username", "bio", "image", true);
        ProfileData profileData2 = new ProfileData("profile-id", "username", "bio", "image", true);
        
        assertEquals(profileData1, profileData2);
        assertEquals(profileData1.hashCode(), profileData2.hashCode());
    }

    @Test
    void shouldImplementEqualsWithNullFields() {
        ProfileData profileData1 = new ProfileData(null, null, null, null, false);
        ProfileData profileData2 = new ProfileData(null, null, null, null, false);
        
        assertEquals(profileData1, profileData2);
        assertEquals(profileData1.hashCode(), profileData2.hashCode());
    }

    @Test
    void shouldImplementEqualsWithDifferentFields() {
        ProfileData profileData1 = new ProfileData("profile-id-1", "username1", "bio1", "image1", true);
        ProfileData profileData2 = new ProfileData("profile-id-2", "username2", "bio2", "image2", false);
        
        assertNotEquals(profileData1, profileData2);
        assertNotEquals(profileData1.hashCode(), profileData2.hashCode());
    }

    @Test
    void shouldImplementHashCodeCorrectly() {
        ProfileData profileData = new ProfileData("profile-id", "username", "bio", "image", true);
        
        int hashCode1 = profileData.hashCode();
        int hashCode2 = profileData.hashCode();
        
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void shouldImplementHashCodeWithNullFields() {
        ProfileData profileData1 = new ProfileData(null, null, null, null, false);
        ProfileData profileData2 = new ProfileData(null, null, null, null, false);
        
        assertEquals(profileData1.hashCode(), profileData2.hashCode());
    }

    @Test
    void shouldImplementToStringCorrectly() {
        ProfileData profileData = new ProfileData("profile-id", "username", "bio", "image", true);
        
        String toString = profileData.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("ProfileData"));
    }

    @Test
    void shouldImplementToStringWithNullFields() {
        ProfileData profileData = new ProfileData(null, null, null, null, false);
        
        String toString = profileData.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("ProfileData"));
    }

    @Test
    void shouldTestCanEqualMethod() {
        ProfileData profileData1 = new ProfileData("profile-id", "username", "bio", "image", true);
        ProfileData profileData2 = new ProfileData("profile-id-2", "username2", "bio2", "image2", false);
        String other = "not a ProfileData";
        
        assertTrue(profileData1.canEqual(profileData2));
        assertFalse(profileData1.canEqual(other));
    }

    @Test
    void shouldTestEqualsWithMixedNullAndNonNullFields() {
        ProfileData profileData1 = new ProfileData("profile-id", null, "bio", null, true);
        ProfileData profileData2 = new ProfileData("profile-id", null, "bio", null, true);
        ProfileData profileData3 = new ProfileData("profile-id", "username", "bio", null, true);
        
        assertEquals(profileData1, profileData2);
        assertNotEquals(profileData1, profileData3);
    }

    @Test
    void shouldTestEqualsWithNullVsNonNullComparisons() {
        ProfileData profileData1 = new ProfileData(null, "username", "bio", "image", true);
        ProfileData profileData2 = new ProfileData("profile-id", "username", "bio", "image", true);
        assertNotEquals(profileData1, profileData2);
        
        ProfileData profileData3 = new ProfileData("profile-id", null, "bio", "image", true);
        ProfileData profileData4 = new ProfileData("profile-id", "username", "bio", "image", true);
        assertNotEquals(profileData3, profileData4);
        
        ProfileData profileData5 = new ProfileData("profile-id", "username", null, "image", true);
        ProfileData profileData6 = new ProfileData("profile-id", "username", "bio", "image", true);
        assertNotEquals(profileData5, profileData6);
        
        ProfileData profileData7 = new ProfileData("profile-id", "username", "bio", null, true);
        ProfileData profileData8 = new ProfileData("profile-id", "username", "bio", "image", true);
        assertNotEquals(profileData7, profileData8);
        
        ProfileData profileData9 = new ProfileData("profile-id", "username", "bio", "image", false);
        ProfileData profileData10 = new ProfileData("profile-id", "username", "bio", "image", true);
        assertNotEquals(profileData9, profileData10);
    }

    @Test
    void shouldNotBeEqualToNull() {
        ProfileData profileData = new ProfileData("profile-id", "username", "bio", "image", true);
        
        assertNotEquals(profileData, null);
    }

    @Test
    void shouldNotBeEqualToDifferentClass() {
        ProfileData profileData = new ProfileData("profile-id", "username", "bio", "image", true);
        String other = "not a ProfileData";
        
        assertNotEquals(profileData, other);
    }

    @Test
    void shouldBeEqualToItself() {
        ProfileData profileData = new ProfileData("profile-id", "username", "bio", "image", true);
        
        assertEquals(profileData, profileData);
    }

    @Test
    void shouldHandleEmptyStrings() {
        ProfileData profileData = new ProfileData("", "", "", "", false);
        
        assertEquals("", profileData.getId());
        assertEquals("", profileData.getUsername());
        assertEquals("", profileData.getBio());
        assertEquals("", profileData.getImage());
        assertFalse(profileData.isFollowing());
    }

    @Test
    void shouldHandleLongContent() {
        String longId = "very-long-profile-id-".repeat(10);
        String longUsername = "very-long-username-".repeat(10);
        String longBio = "This is a very long bio that might be used in real applications. ".repeat(100);
        String longImage = "https://very-long-image-url.com/".repeat(10);
        
        ProfileData profileData = new ProfileData(longId, longUsername, longBio, longImage, true);
        
        assertEquals(longId, profileData.getId());
        assertEquals(longUsername, profileData.getUsername());
        assertEquals(longBio, profileData.getBio());
        assertEquals(longImage, profileData.getImage());
        assertTrue(profileData.isFollowing());
    }

    @Test
    void shouldHandleSpecialCharactersInContent() {
        String specialId = "profile-id-with-special-chars-!@#$%";
        String specialUsername = "user!@#$%^&*()";
        String specialBio = "Bio with special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?";
        String specialImage = "https://example.com/image!@#$%.jpg";
        
        ProfileData profileData = new ProfileData(specialId, specialUsername, specialBio, specialImage, false);
        
        assertEquals(specialId, profileData.getId());
        assertEquals(specialUsername, profileData.getUsername());
        assertEquals(specialBio, profileData.getBio());
        assertEquals(specialImage, profileData.getImage());
        assertFalse(profileData.isFollowing());
    }

    @Test
    void shouldHandleFollowingFlagCorrectly() {
        ProfileData followingProfile = new ProfileData("profile-id", "username", "bio", "image", true);
        ProfileData notFollowingProfile = new ProfileData("profile-id", "username", "bio", "image", false);
        
        assertTrue(followingProfile.isFollowing());
        assertFalse(notFollowingProfile.isFollowing());
        assertNotEquals(followingProfile, notFollowingProfile);
    }

    @Test
    void shouldHandleNullBioAndImage() {
        ProfileData profileData = new ProfileData("profile-id", "username", null, null, true);
        
        assertEquals("profile-id", profileData.getId());
        assertEquals("username", profileData.getUsername());
        assertNull(profileData.getBio());
        assertNull(profileData.getImage());
        assertTrue(profileData.isFollowing());
    }

    @Test
    void shouldTestHashCodeConsistency() {
        ProfileData profileData = new ProfileData("profile-id", "username", "bio", "image", true);
        
        int hashCode1 = profileData.hashCode();
        int hashCode2 = profileData.hashCode();
        
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void shouldTestHashCodeWithNullFieldsVariations() {
        ProfileData profileData1 = new ProfileData(null, null, null, null, false);
        ProfileData profileData2 = new ProfileData(null, null, null, null, false);
        ProfileData profileData3 = new ProfileData("profile-id", null, null, null, false);
        ProfileData profileData4 = new ProfileData(null, "username", null, null, false);
        
        assertEquals(profileData1.hashCode(), profileData2.hashCode());
        assertNotEquals(profileData1.hashCode(), profileData3.hashCode());
        assertNotEquals(profileData1.hashCode(), profileData4.hashCode());
        assertNotEquals(profileData3.hashCode(), profileData4.hashCode());
    }
}
