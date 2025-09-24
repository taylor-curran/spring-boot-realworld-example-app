package io.spring.application.data;

import static org.junit.jupiter.api.Assertions.*;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

class CommentDataComprehensiveTest {

    @Test
    void shouldCreateCommentDataWithAllFields() {
        DateTime createdAt = DateTime.now();
        DateTime updatedAt = DateTime.now().plusMinutes(5);
        ProfileData profileData = new ProfileData("profile-id", "author", "bio", "image", false);
        
        CommentData commentData = new CommentData("comment-id", "body", "article-id", createdAt, updatedAt, profileData);
        
        assertEquals("comment-id", commentData.getId());
        assertEquals("body", commentData.getBody());
        assertEquals("article-id", commentData.getArticleId());
        assertEquals(createdAt, commentData.getCreatedAt());
        assertEquals(updatedAt, commentData.getUpdatedAt());
        assertEquals(profileData, commentData.getProfileData());
    }

    @Test
    void shouldCreateCommentDataWithDefaultConstructor() {
        CommentData commentData = new CommentData();
        
        assertNull(commentData.getId());
        assertNull(commentData.getBody());
        assertNull(commentData.getArticleId());
        assertNull(commentData.getCreatedAt());
        assertNull(commentData.getUpdatedAt());
        assertNull(commentData.getProfileData());
    }

    @Test
    void shouldSetFieldsUsingSetters() {
        CommentData commentData = new CommentData();
        DateTime createdAt = DateTime.now();
        DateTime updatedAt = DateTime.now().plusMinutes(5);
        ProfileData profileData = new ProfileData("profile-id", "author", "bio", "image", false);
        
        commentData.setId("comment-id");
        commentData.setBody("body");
        commentData.setArticleId("article-id");
        commentData.setCreatedAt(createdAt);
        commentData.setUpdatedAt(updatedAt);
        commentData.setProfileData(profileData);
        
        assertEquals("comment-id", commentData.getId());
        assertEquals("body", commentData.getBody());
        assertEquals("article-id", commentData.getArticleId());
        assertEquals(createdAt, commentData.getCreatedAt());
        assertEquals(updatedAt, commentData.getUpdatedAt());
        assertEquals(profileData, commentData.getProfileData());
    }

    @Test
    void shouldImplementEqualsCorrectly() {
        DateTime createdAt = DateTime.now();
        DateTime updatedAt = DateTime.now().plusMinutes(5);
        ProfileData profileData = new ProfileData("profile-id", "author", "bio", "image", false);
        
        CommentData commentData1 = new CommentData("comment-id", "body", "article-id", createdAt, updatedAt, profileData);
        CommentData commentData2 = new CommentData("comment-id", "body", "article-id", createdAt, updatedAt, profileData);
        
        assertEquals(commentData1, commentData2);
        assertEquals(commentData1.hashCode(), commentData2.hashCode());
    }

    @Test
    void shouldImplementEqualsWithNullFields() {
        CommentData commentData1 = new CommentData(null, null, null, null, null, null);
        CommentData commentData2 = new CommentData(null, null, null, null, null, null);
        
        assertEquals(commentData1, commentData2);
        assertEquals(commentData1.hashCode(), commentData2.hashCode());
    }

    @Test
    void shouldImplementEqualsWithDifferentFields() {
        DateTime createdAt = DateTime.now();
        DateTime updatedAt = DateTime.now().plusMinutes(5);
        ProfileData profileData1 = new ProfileData("profile-id-1", "author1", "bio1", "image1", false);
        ProfileData profileData2 = new ProfileData("profile-id-2", "author2", "bio2", "image2", true);
        
        CommentData commentData1 = new CommentData("comment-id-1", "body1", "article-id-1", createdAt, updatedAt, profileData1);
        CommentData commentData2 = new CommentData("comment-id-2", "body2", "article-id-2", createdAt.plusHours(1), updatedAt.plusHours(1), profileData2);
        
        assertNotEquals(commentData1, commentData2);
        assertNotEquals(commentData1.hashCode(), commentData2.hashCode());
    }

    @Test
    void shouldImplementHashCodeCorrectly() {
        DateTime createdAt = DateTime.now();
        DateTime updatedAt = DateTime.now().plusMinutes(5);
        ProfileData profileData = new ProfileData("profile-id", "author", "bio", "image", false);
        
        CommentData commentData = new CommentData("comment-id", "body", "article-id", createdAt, updatedAt, profileData);
        
        int hashCode1 = commentData.hashCode();
        int hashCode2 = commentData.hashCode();
        
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void shouldImplementHashCodeWithNullFields() {
        CommentData commentData1 = new CommentData(null, null, null, null, null, null);
        CommentData commentData2 = new CommentData(null, null, null, null, null, null);
        
        assertEquals(commentData1.hashCode(), commentData2.hashCode());
    }

    @Test
    void shouldImplementToStringCorrectly() {
        DateTime createdAt = DateTime.now();
        DateTime updatedAt = DateTime.now().plusMinutes(5);
        ProfileData profileData = new ProfileData("profile-id", "author", "bio", "image", false);
        
        CommentData commentData = new CommentData("comment-id", "body", "article-id", createdAt, updatedAt, profileData);
        
        String toString = commentData.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("CommentData"));
    }

    @Test
    void shouldImplementToStringWithNullFields() {
        CommentData commentData = new CommentData(null, null, null, null, null, null);
        
        String toString = commentData.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("CommentData"));
    }

    @Test
    void shouldTestCanEqualMethod() {
        CommentData commentData1 = new CommentData("comment-id", "body", "article-id", DateTime.now(), DateTime.now(), null);
        CommentData commentData2 = new CommentData("comment-id-2", "body2", "article-id-2", DateTime.now(), DateTime.now(), null);
        String other = "not a CommentData";
        
        assertTrue(commentData1.canEqual(commentData2));
        assertFalse(commentData1.canEqual(other));
    }

    @Test
    void shouldTestEqualsWithMixedNullAndNonNullFields() {
        DateTime createdAt = DateTime.now();
        ProfileData profileData = new ProfileData("profile-id", "author", "bio", "image", false);
        
        CommentData commentData1 = new CommentData("comment-id", null, "article-id", createdAt, null, profileData);
        CommentData commentData2 = new CommentData("comment-id", null, "article-id", createdAt, null, profileData);
        CommentData commentData3 = new CommentData("comment-id", "body", "article-id", createdAt, null, profileData);
        
        assertEquals(commentData1, commentData2);
        assertNotEquals(commentData1, commentData3);
    }

    @Test
    void shouldTestEqualsWithNullVsNonNullComparisons() {
        DateTime createdAt = DateTime.now();
        DateTime updatedAt = DateTime.now().plusMinutes(5);
        ProfileData profileData = new ProfileData("profile-id", "author", "bio", "image", false);
        
        CommentData commentData1 = new CommentData(null, "body", "article-id", createdAt, updatedAt, profileData);
        CommentData commentData2 = new CommentData("comment-id", "body", "article-id", createdAt, updatedAt, profileData);
        assertNotEquals(commentData1, commentData2);
        
        CommentData commentData3 = new CommentData("comment-id", null, "article-id", createdAt, updatedAt, profileData);
        CommentData commentData4 = new CommentData("comment-id", "body", "article-id", createdAt, updatedAt, profileData);
        assertNotEquals(commentData3, commentData4);
        
        CommentData commentData5 = new CommentData("comment-id", "body", null, createdAt, updatedAt, profileData);
        CommentData commentData6 = new CommentData("comment-id", "body", "article-id", createdAt, updatedAt, profileData);
        assertNotEquals(commentData5, commentData6);
        
        CommentData commentData7 = new CommentData("comment-id", "body", "article-id", null, updatedAt, profileData);
        CommentData commentData8 = new CommentData("comment-id", "body", "article-id", createdAt, updatedAt, profileData);
        assertNotEquals(commentData7, commentData8);
        
        CommentData commentData9 = new CommentData("comment-id", "body", "article-id", createdAt, null, profileData);
        CommentData commentData10 = new CommentData("comment-id", "body", "article-id", createdAt, updatedAt, profileData);
        assertNotEquals(commentData9, commentData10);
        
        CommentData commentData11 = new CommentData("comment-id", "body", "article-id", createdAt, updatedAt, null);
        CommentData commentData12 = new CommentData("comment-id", "body", "article-id", createdAt, updatedAt, profileData);
        assertNotEquals(commentData11, commentData12);
    }

    @Test
    void shouldTestGetCursorMethod() {
        DateTime createdAt = DateTime.now();
        CommentData commentData = new CommentData("comment-id", "body", "article-id", createdAt, DateTime.now(), null);

        assertNotNull(commentData.getCursor());
        assertEquals(createdAt, commentData.getCursor().getData());
    }

    @Test
    void shouldTestGetCursorWithNullCreatedAt() {
        CommentData commentData = new CommentData("comment-id", "body", "article-id", null, DateTime.now(), null);

        assertNotNull(commentData.getCursor());
        assertNull(commentData.getCursor().getData());
    }

    @Test
    void shouldNotBeEqualToNull() {
        CommentData commentData = new CommentData("comment-id", "body", "article-id", DateTime.now(), DateTime.now(), null);
        
        assertNotEquals(commentData, null);
    }

    @Test
    void shouldNotBeEqualToDifferentClass() {
        CommentData commentData = new CommentData("comment-id", "body", "article-id", DateTime.now(), DateTime.now(), null);
        String other = "not a CommentData";
        
        assertNotEquals(commentData, other);
    }

    @Test
    void shouldBeEqualToItself() {
        CommentData commentData = new CommentData("comment-id", "body", "article-id", DateTime.now(), DateTime.now(), null);
        
        assertEquals(commentData, commentData);
    }

    @Test
    void shouldHandleEmptyStrings() {
        CommentData commentData = new CommentData("", "", "", DateTime.now(), DateTime.now(), null);
        
        assertEquals("", commentData.getId());
        assertEquals("", commentData.getBody());
        assertEquals("", commentData.getArticleId());
    }

    @Test
    void shouldHandleLongContent() {
        String longBody = "This is a very long comment body that might be used in real applications. ".repeat(100);
        String longId = "very-long-comment-id-".repeat(10);
        String longArticleId = "very-long-article-id-".repeat(10);
        
        CommentData commentData = new CommentData(longId, longBody, longArticleId, DateTime.now(), DateTime.now(), null);
        
        assertEquals(longId, commentData.getId());
        assertEquals(longBody, commentData.getBody());
        assertEquals(longArticleId, commentData.getArticleId());
    }

    @Test
    void shouldHandleSpecialCharactersInContent() {
        String specialBody = "Comment with special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?";
        String specialId = "comment-id-with-special-chars-!@#$%";
        String specialArticleId = "article-id-with-special-chars-!@#$%";
        
        CommentData commentData = new CommentData(specialId, specialBody, specialArticleId, DateTime.now(), DateTime.now(), null);
        
        assertEquals(specialId, commentData.getId());
        assertEquals(specialBody, commentData.getBody());
        assertEquals(specialArticleId, commentData.getArticleId());
    }
}
