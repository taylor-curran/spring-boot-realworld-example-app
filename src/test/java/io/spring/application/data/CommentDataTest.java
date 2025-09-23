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
}
