package io.spring.application.data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ProfileDataTest {

    @Test
    void shouldCreateProfileDataWithAllFields() {
        String id = "user-id";
        String username = "testuser";
        String bio = "Test Bio";
        String image = "avatar.jpg";
        boolean following = true;

        ProfileData profileData = new ProfileData(id, username, bio, image, following);

        assertEquals(id, profileData.getId());
        assertEquals(username, profileData.getUsername());
        assertEquals(bio, profileData.getBio());
        assertEquals(image, profileData.getImage());
        assertTrue(profileData.isFollowing());
    }

    @Test
    void shouldCreateProfileDataWithEmptyBio() {
        ProfileData profileData = new ProfileData("id", "user", "", "image.jpg", false);

        assertEquals("", profileData.getBio());
        assertFalse(profileData.isFollowing());
    }

    @Test
    void shouldCreateProfileDataWithEmptyImage() {
        ProfileData profileData = new ProfileData("id", "user", "bio", "", true);

        assertEquals("", profileData.getImage());
        assertTrue(profileData.isFollowing());
    }

    @Test
    void shouldCreateProfileDataWithNullBio() {
        ProfileData profileData = new ProfileData("id", "user", null, "image.jpg", false);

        assertNull(profileData.getBio());
    }

    @Test
    void shouldCreateProfileDataWithNullImage() {
        ProfileData profileData = new ProfileData("id", "user", "bio", null, true);

        assertNull(profileData.getImage());
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
    void shouldHandleSpecialCharactersInUsername() {
        String specialUsername = "user_123-test.name";
        ProfileData profileData = new ProfileData("id", specialUsername, "bio", "image", true);

        assertEquals(specialUsername, profileData.getUsername());
    }

    @Test
    void shouldHandleUnicodeInBio() {
        String unicodeBio = "Hello 世界! Café naïve résumé 🌍";
        ProfileData profileData = new ProfileData("id", "user", unicodeBio, "image", false);

        assertEquals(unicodeBio, profileData.getBio());
    }

    @Test
    void shouldHandleLongBio() {
        String longBio = "This is a very long bio that contains multiple sentences and might be used to test how the system handles longer text content in user biographies. ".repeat(5);
        ProfileData profileData = new ProfileData("id", "user", longBio, "image", true);

        assertEquals(longBio, profileData.getBio());
    }

    @Test
    void shouldHandleImageUrls() {
        String imageUrl = "https://example.com/avatar/user123.jpg";
        ProfileData profileData = new ProfileData("id", "user", "bio", imageUrl, false);

        assertEquals(imageUrl, profileData.getImage());
    }

    @Test
    void shouldHandleFollowingStatusCorrectly() {
        ProfileData followingProfile = new ProfileData("id1", "user1", "bio", "image", true);
        ProfileData notFollowingProfile = new ProfileData("id2", "user2", "bio", "image", false);

        assertTrue(followingProfile.isFollowing());
        assertFalse(notFollowingProfile.isFollowing());
    }

    @Test
    void shouldHandleUuidIds() {
        String uuidId = "550e8400-e29b-41d4-a716-446655440000";
        ProfileData profileData = new ProfileData(uuidId, "user", "bio", "image", true);

        assertEquals(uuidId, profileData.getId());
    }

    @Test
    void shouldImplementEqualsCorrectly() {
        ProfileData profile1 = new ProfileData("id", "username", "bio", "image.jpg", true);
        ProfileData profile2 = new ProfileData("id", "username", "bio", "image.jpg", true);
        ProfileData profile3 = new ProfileData("different-id", "username", "bio", "image.jpg", true);

        assertEquals(profile1, profile2);
        assertEquals(profile1, profile1);
        assertNotEquals(profile1, profile3);
        assertNotEquals(profile1, null);
        assertNotEquals(profile1, "not a ProfileData");
    }

    @Test
    void shouldImplementEqualsWithNullFields() {
        ProfileData profile1 = new ProfileData(null, null, null, null, false);
        ProfileData profile2 = new ProfileData(null, null, null, null, false);
        ProfileData profile3 = new ProfileData("id", null, null, null, false);

        assertEquals(profile1, profile2);
        assertNotEquals(profile1, profile3);
    }

    @Test
    void shouldImplementEqualsWithDifferentFields() {
        ProfileData baseProfile = new ProfileData("id", "username", "bio", "image.jpg", true);

        ProfileData differentUsername = new ProfileData("id", "different-username", "bio", "image.jpg", true);
        ProfileData differentBio = new ProfileData("id", "username", "different-bio", "image.jpg", true);
        ProfileData differentImage = new ProfileData("id", "username", "bio", "different-image.jpg", true);
        ProfileData differentFollowing = new ProfileData("id", "username", "bio", "image.jpg", false);

        assertNotEquals(baseProfile, differentUsername);
        assertNotEquals(baseProfile, differentBio);
        assertNotEquals(baseProfile, differentImage);
        assertNotEquals(baseProfile, differentFollowing);
    }

    @Test
    void shouldImplementHashCodeCorrectly() {
        ProfileData profile1 = new ProfileData("id", "username", "bio", "image.jpg", true);
        ProfileData profile2 = new ProfileData("id", "username", "bio", "image.jpg", true);

        assertEquals(profile1.hashCode(), profile2.hashCode());
    }

    @Test
    void shouldImplementHashCodeWithNullFields() {
        ProfileData profile1 = new ProfileData(null, null, null, null, false);
        ProfileData profile2 = new ProfileData(null, null, null, null, false);

        assertEquals(profile1.hashCode(), profile2.hashCode());
    }

    @Test
    void shouldImplementToStringCorrectly() {
        ProfileData profileData = new ProfileData("id", "username", "bio", "image.jpg", true);
        String toString = profileData.toString();

        assertTrue(toString.contains("ProfileData"));
        assertTrue(toString.contains("id=id"));
        assertTrue(toString.contains("username=username"));
        assertTrue(toString.contains("bio=bio"));
        assertTrue(toString.contains("image=image.jpg"));
        assertTrue(toString.contains("following=true"));
    }

    @Test
    void shouldImplementToStringWithNullFields() {
        ProfileData profileData = new ProfileData(null, null, null, null, false);
        String toString = profileData.toString();

        assertTrue(toString.contains("ProfileData"));
        assertTrue(toString.contains("id=null"));
        assertTrue(toString.contains("username=null"));
        assertTrue(toString.contains("bio=null"));
        assertTrue(toString.contains("image=null"));
        assertTrue(toString.contains("following=false"));
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

        profileData.setId("new-id");
        profileData.setUsername("new-username");
        profileData.setBio("new-bio");
        profileData.setImage("new-image.jpg");
        profileData.setFollowing(true);

        assertEquals("new-id", profileData.getId());
        assertEquals("new-username", profileData.getUsername());
        assertEquals("new-bio", profileData.getBio());
        assertEquals("new-image.jpg", profileData.getImage());
        assertTrue(profileData.isFollowing());
    }
}
