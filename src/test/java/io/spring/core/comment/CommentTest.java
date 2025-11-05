package io.spring.core.comment;

import static org.junit.jupiter.api.Assertions.*;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

class CommentTest {

    @Test
    void shouldCreateCommentWithValidData() {
        Comment comment = new Comment("This is a test comment", "user123", "article456");

        assertNotNull(comment);
        assertEquals("This is a test comment", comment.getBody());
        assertEquals("user123", comment.getUserId());
        assertEquals("article456", comment.getArticleId());
        assertNotNull(comment.getId());
        assertNotNull(comment.getCreatedAt());
        assertTrue(comment.getCreatedAt().isBeforeNow() || comment.getCreatedAt().isEqualNow());
    }

    @Test
    void shouldCreateCommentWithEmptyBody() {
        Comment comment = new Comment("", "user123", "article456");

        assertNotNull(comment);
        assertEquals("", comment.getBody());
        assertEquals("user123", comment.getUserId());
        assertEquals("article456", comment.getArticleId());
    }

    @Test
    void shouldCreateCommentWithLongBody() {
        String longBody = "This is a very long comment body that contains multiple sentences and paragraphs. ".repeat(50);
        Comment comment = new Comment(longBody, "user123", "article456");

        assertNotNull(comment);
        assertEquals(longBody, comment.getBody());
        assertEquals("user123", comment.getUserId());
        assertEquals("article456", comment.getArticleId());
    }

    @Test
    void shouldCreateCommentWithSpecialCharacters() {
        String bodyWithSpecialChars = "Comment with special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?";
        Comment comment = new Comment(bodyWithSpecialChars, "user123", "article456");

        assertNotNull(comment);
        assertEquals(bodyWithSpecialChars, comment.getBody());
    }

    @Test
    void shouldCreateCommentWithUnicodeCharacters() {
        String unicodeBody = "Unicode comment: 测试评论 🌍 🚀 ✨ こんにちは";
        Comment comment = new Comment(unicodeBody, "user123", "article456");

        assertNotNull(comment);
        assertEquals(unicodeBody, comment.getBody());
    }

    @Test
    void shouldCreateCommentWithNewlinesAndTabs() {
        String bodyWithFormatting = "Line 1\nLine 2\nLine 3\tTabbed content";
        Comment comment = new Comment(bodyWithFormatting, "user123", "article456");

        assertNotNull(comment);
        assertEquals(bodyWithFormatting, comment.getBody());
    }

    @Test
    void shouldCreateCommentWithNullBody() {
        Comment comment = new Comment(null, "user123", "article456");

        assertNotNull(comment);
        assertNull(comment.getBody());
        assertEquals("user123", comment.getUserId());
        assertEquals("article456", comment.getArticleId());
    }

    @Test
    void shouldCreateCommentWithNullUserId() {
        Comment comment = new Comment("Test comment", null, "article456");

        assertNotNull(comment);
        assertEquals("Test comment", comment.getBody());
        assertNull(comment.getUserId());
        assertEquals("article456", comment.getArticleId());
    }

    @Test
    void shouldCreateCommentWithNullArticleId() {
        Comment comment = new Comment("Test comment", "user123", null);

        assertNotNull(comment);
        assertEquals("Test comment", comment.getBody());
        assertEquals("user123", comment.getUserId());
        assertNull(comment.getArticleId());
    }

    @Test
    void shouldCreateCommentWithAllNullValues() {
        Comment comment = new Comment(null, null, null);

        assertNotNull(comment);
        assertNull(comment.getBody());
        assertNull(comment.getUserId());
        assertNull(comment.getArticleId());
        assertNotNull(comment.getId());
        assertNotNull(comment.getCreatedAt());
    }

    @Test
    void shouldGenerateUniqueIds() {
        Comment comment1 = new Comment("Comment 1", "user1", "article1");
        Comment comment2 = new Comment("Comment 2", "user2", "article2");

        assertNotEquals(comment1.getId(), comment2.getId());
    }

    @Test
    void shouldGenerateCreatedAtTimestamp() {
        DateTime beforeCreation = new DateTime();
        Comment comment = new Comment("Test comment", "user123", "article456");
        DateTime afterCreation = new DateTime();

        assertNotNull(comment.getCreatedAt());
        assertTrue(comment.getCreatedAt().isAfter(beforeCreation.minusSeconds(1)));
        assertTrue(comment.getCreatedAt().isBefore(afterCreation.plusSeconds(1)));
    }

    @Test
    void shouldHandleEqualsAndHashCode() {
        Comment comment1 = new Comment("Test comment", "user123", "article456");
        Comment comment2 = new Comment("Different comment", "user456", "article789");

        assertNotEquals(comment1, comment2);
        assertNotEquals(comment1.hashCode(), comment2.hashCode());
    }

    @Test
    void shouldHandleEqualityBasedOnId() {
        Comment comment1 = new Comment("Test comment", "user123", "article456");
        Comment comment2 = new Comment("Test comment", "user123", "article456");

        assertNotEquals(comment1, comment2);
        assertNotEquals(comment1.hashCode(), comment2.hashCode());
    }

    @Test
    void shouldHandleToString() {
        Comment comment = new Comment("Test comment", "user123", "article456");

        String toString = comment.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Comment"));
    }

    @Test
    void shouldHandleNoArgsConstructor() {
        Comment comment = new Comment();

        assertNotNull(comment);
        assertNull(comment.getId());
        assertNull(comment.getBody());
        assertNull(comment.getUserId());
        assertNull(comment.getArticleId());
        assertNull(comment.getCreatedAt());
    }

    @Test
    void shouldHandleMultilineComments() {
        String multilineBody = "This is a multiline comment\n" +
            "with multiple paragraphs.\n\n" +
            "It contains various formatting\n" +
            "and should be handled properly.";
        Comment comment = new Comment(multilineBody, "user123", "article456");

        assertNotNull(comment);
        assertEquals(multilineBody, comment.getBody());
    }

    @Test
    void shouldHandleHtmlInBody() {
        String htmlBody = "<p>This is a comment with <strong>HTML</strong> tags and <a href='#'>links</a></p>";
        Comment comment = new Comment(htmlBody, "user123", "article456");

        assertNotNull(comment);
        assertEquals(htmlBody, comment.getBody());
    }

    @Test
    void shouldHandleMarkdownInBody() {
        String markdownBody = "# Comment Title\n\n**Bold text** and *italic text*\n\n- List item 1\n- List item 2";
        Comment comment = new Comment(markdownBody, "user123", "article456");

        assertNotNull(comment);
        assertEquals(markdownBody, comment.getBody());
    }
}
