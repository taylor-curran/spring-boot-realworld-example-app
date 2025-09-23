package io.spring.application.data;

import static org.junit.jupiter.api.Assertions.*;

import io.spring.application.DateTimeCursor;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

class CommentDataTest {

    @Test
    void shouldCreateCommentDataWithAllFields() {
        String id = "comment-id";
        String body = "This is a test comment";
        String articleId = "article-id";
        DateTime createdAt = DateTime.now();
        DateTime updatedAt = DateTime.now().plusMinutes(5);
        ProfileData profileData = new ProfileData("user-id", "testuser", "Test Bio", "avatar.jpg", false);

        CommentData commentData = new CommentData(id, body, articleId, createdAt, updatedAt, profileData);

        assertEquals(id, commentData.getId());
        assertEquals(body, commentData.getBody());
        assertEquals(articleId, commentData.getArticleId());
        assertEquals(createdAt, commentData.getCreatedAt());
        assertEquals(updatedAt, commentData.getUpdatedAt());
        assertEquals(profileData, commentData.getProfileData());
    }

    @Test
    void shouldCreateCommentDataWithEmptyBody() {
        CommentData commentData = new CommentData(
            "id", "", "article-id", DateTime.now(), DateTime.now(),
            new ProfileData("user-id", "user", "bio", "image", false)
        );

        assertEquals("", commentData.getBody());
    }

    @Test
    void shouldCreateCommentDataWithNullBody() {
        CommentData commentData = new CommentData(
            "id", null, "article-id", DateTime.now(), DateTime.now(),
            new ProfileData("user-id", "user", "bio", "image", false)
        );

        assertNull(commentData.getBody());
    }

    @Test
    void shouldHandleLongCommentBody() {
        String longBody = "This is a very long comment that contains multiple sentences and might be used to test how the system handles longer text content in comments. ".repeat(10);
        CommentData commentData = new CommentData(
            "id", longBody, "article-id", DateTime.now(), DateTime.now(),
            new ProfileData("user-id", "user", "bio", "image", false)
        );

        assertEquals(longBody, commentData.getBody());
    }

    @Test
    void shouldHandleSpecialCharactersInBody() {
        String bodyWithSpecialChars = "Comment with special characters: !@#$%^&*() and unicode: 你好世界 🌍 café naïve";
        CommentData commentData = new CommentData(
            "id", bodyWithSpecialChars, "article-id", DateTime.now(), DateTime.now(),
            new ProfileData("user-id", "user", "bio", "image", false)
        );

        assertEquals(bodyWithSpecialChars, commentData.getBody());
    }

    @Test
    void shouldHandleSameDateTimes() {
        DateTime sameTime = DateTime.now();
        CommentData commentData = new CommentData(
            "id", "body", "article-id", sameTime, sameTime,
            new ProfileData("user-id", "user", "bio", "image", false)
        );

        assertEquals(sameTime, commentData.getCreatedAt());
        assertEquals(sameTime, commentData.getUpdatedAt());
    }

    @Test
    void shouldHandleEmptyStrings() {
        CommentData commentData = new CommentData(
            "", "", "", DateTime.now(), DateTime.now(),
            new ProfileData("", "", "", "", false)
        );

        assertEquals("", commentData.getId());
        assertEquals("", commentData.getBody());
        assertEquals("", commentData.getArticleId());
    }

    @Test
    void shouldImplementNodeInterfaceCorrectly() {
        DateTime createdAt = DateTime.now();
        CommentData commentData = new CommentData(
            "id", "body", "article-id", createdAt, DateTime.now().plusMinutes(1),
            new ProfileData("user-id", "user", "bio", "image", false)
        );

        DateTimeCursor cursor = commentData.getCursor();
        assertNotNull(cursor);
        assertEquals(createdAt, cursor.getData());
    }

    @Test
    void shouldHandleNullProfileData() {
        CommentData commentData = new CommentData(
            "id", "body", "article-id", DateTime.now(), DateTime.now(), null
        );

        assertNull(commentData.getProfileData());
    }

    @Test
    void shouldHandleUuidIds() {
        String commentUuid = "550e8400-e29b-41d4-a716-446655440000";
        String articleUuid = "660e8400-e29b-41d4-a716-446655440001";
        CommentData commentData = new CommentData(
            commentUuid, "body", articleUuid, DateTime.now(), DateTime.now(),
            new ProfileData("user-uuid", "user", "bio", "image", false)
        );

        assertEquals(commentUuid, commentData.getId());
        assertEquals(articleUuid, commentData.getArticleId());
    }

    @Test
    void shouldHandleMultilineCommentBody() {
        String multilineBody = "This is line 1\nThis is line 2\nThis is line 3\n\nWith empty line above";
        CommentData commentData = new CommentData(
            "id", multilineBody, "article-id", DateTime.now(), DateTime.now(),
            new ProfileData("user-id", "user", "bio", "image", false)
        );

        assertEquals(multilineBody, commentData.getBody());
        assertTrue(commentData.getBody().contains("\n"));
    }

    @Test
    void shouldHandleHtmlInCommentBody() {
        String htmlBody = "<p>This is a <strong>bold</strong> comment with <em>italic</em> text and <a href='#'>links</a></p>";
        CommentData commentData = new CommentData(
            "id", htmlBody, "article-id", DateTime.now(), DateTime.now(),
            new ProfileData("user-id", "user", "bio", "image", false)
        );

        assertEquals(htmlBody, commentData.getBody());
    }

    @Test
    void shouldImplementEqualsCorrectly() {
        DateTime now = DateTime.now();
        ProfileData profileData = new ProfileData("profile-id", "author", "bio", "image", false);

        CommentData comment1 = new CommentData("id", "body", "article-id", now, now, profileData);
        CommentData comment2 = new CommentData("id", "body", "article-id", now, now, profileData);
        CommentData comment3 = new CommentData("different-id", "body", "article-id", now, now, profileData);

        assertEquals(comment1, comment2);
        assertEquals(comment1, comment1);
        assertNotEquals(comment1, comment3);
        assertNotEquals(comment1, null);
        assertNotEquals(comment1, "not a CommentData");
    }

    @Test
    void shouldImplementEqualsWithNullFields() {
        CommentData comment1 = new CommentData(null, null, null, null, null, null);
        CommentData comment2 = new CommentData(null, null, null, null, null, null);
        CommentData comment3 = new CommentData("id", null, null, null, null, null);

        assertEquals(comment1, comment2);
        assertNotEquals(comment1, comment3);
    }

    @Test
    void shouldImplementEqualsWithDifferentFields() {
        DateTime now = DateTime.now();
        DateTime later = now.plusHours(1);
        ProfileData profile1 = new ProfileData("profile1", "author1", "bio", "image", false);
        ProfileData profile2 = new ProfileData("profile2", "author2", "bio", "image", false);

        CommentData baseComment = new CommentData("id", "body", "article-id", now, now, profile1);

        CommentData differentBody = new CommentData("id", "different-body", "article-id", now, now, profile1);
        CommentData differentArticle = new CommentData("id", "body", "different-article", now, now, profile1);
        CommentData differentCreated = new CommentData("id", "body", "article-id", later, now, profile1);
        CommentData differentUpdated = new CommentData("id", "body", "article-id", now, later, profile1);
        CommentData differentProfile = new CommentData("id", "body", "article-id", now, now, profile2);

        assertNotEquals(baseComment, differentBody);
        assertNotEquals(baseComment, differentArticle);
        assertNotEquals(baseComment, differentCreated);
        assertNotEquals(baseComment, differentUpdated);
        assertNotEquals(baseComment, differentProfile);
    }

    @Test
    void shouldImplementHashCodeCorrectly() {
        DateTime now = DateTime.now();
        ProfileData profileData = new ProfileData("profile-id", "author", "bio", "image", false);

        CommentData comment1 = new CommentData("id", "body", "article-id", now, now, profileData);
        CommentData comment2 = new CommentData("id", "body", "article-id", now, now, profileData);

        assertEquals(comment1.hashCode(), comment2.hashCode());
    }

    @Test
    void shouldImplementHashCodeWithNullFields() {
        CommentData comment1 = new CommentData(null, null, null, null, null, null);
        CommentData comment2 = new CommentData(null, null, null, null, null, null);

        assertEquals(comment1.hashCode(), comment2.hashCode());
    }

    @Test
    void shouldImplementToStringCorrectly() {
        DateTime now = DateTime.now();
        ProfileData profileData = new ProfileData("profile-id", "author", "bio", "image", false);

        CommentData commentData = new CommentData("id", "body", "article-id", now, now, profileData);
        String toString = commentData.toString();

        assertTrue(toString.contains("CommentData"));
        assertTrue(toString.contains("id=id"));
        assertTrue(toString.contains("body=body"));
        assertTrue(toString.contains("articleId=article-id"));
    }

    @Test
    void shouldImplementToStringWithNullFields() {
        CommentData commentData = new CommentData(null, null, null, null, null, null);
        String toString = commentData.toString();

        assertTrue(toString.contains("CommentData"));
        assertTrue(toString.contains("id=null"));
        assertTrue(toString.contains("body=null"));
        assertTrue(toString.contains("articleId=null"));
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
        DateTime now = DateTime.now();
        ProfileData profileData = new ProfileData("profile-id", "author", "bio", "image", false);

        commentData.setId("new-id");
        commentData.setBody("new-body");
        commentData.setArticleId("new-article-id");
        commentData.setCreatedAt(now);
        commentData.setUpdatedAt(now);
        commentData.setProfileData(profileData);

        assertEquals("new-id", commentData.getId());
        assertEquals("new-body", commentData.getBody());
        assertEquals("new-article-id", commentData.getArticleId());
        assertEquals(now, commentData.getCreatedAt());
        assertEquals(now, commentData.getUpdatedAt());
        assertEquals(profileData, commentData.getProfileData());
    }
}
