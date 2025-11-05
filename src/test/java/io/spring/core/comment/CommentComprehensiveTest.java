package io.spring.core.comment;

import static org.junit.jupiter.api.Assertions.*;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

class CommentComprehensiveTest {

    @Test
    void shouldCreateCommentWithAllFields() {
        Comment comment = new Comment("body", "user-id", "article-id");
        
        assertNotNull(comment.getId());
        assertEquals("body", comment.getBody());
        assertEquals("user-id", comment.getUserId());
        assertEquals("article-id", comment.getArticleId());
        assertNotNull(comment.getCreatedAt());
    }

    @Test
    void shouldCreateCommentWithDefaultConstructor() {
        Comment comment = new Comment();
        
        assertNull(comment.getId());
        assertNull(comment.getBody());
        assertNull(comment.getUserId());
        assertNull(comment.getArticleId());
        assertNull(comment.getCreatedAt());
    }

    @Test
    void shouldImplementEqualsBasedOnIdOnly() {
        Comment comment1 = new Comment("body1", "user-id1", "article-id1");
        Comment comment2 = new Comment("body2", "user-id2", "article-id2");
        
        assertNotEquals(comment1, comment2);
        assertNotEquals(comment1.getId(), comment2.getId());
    }

    @Test
    void shouldImplementEqualsWithNullIds() {
        Comment comment1 = new Comment();
        Comment comment2 = new Comment();
        
        assertEquals(comment1, comment2);
        assertEquals(comment1.hashCode(), comment2.hashCode());
    }

    @Test
    void shouldImplementHashCodeCorrectly() {
        Comment comment = new Comment("body", "user-id", "article-id");
        
        int hashCode1 = comment.hashCode();
        int hashCode2 = comment.hashCode();
        
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void shouldImplementHashCodeWithNullId() {
        Comment comment1 = new Comment();
        Comment comment2 = new Comment();
        
        assertEquals(comment1.hashCode(), comment2.hashCode());
    }

    @Test
    void shouldTestCanEqualMethod() {
        Comment comment1 = new Comment("body", "user-id", "article-id");
        Comment comment2 = new Comment("body2", "user-id2", "article-id2");
        String other = "not a Comment";
        
        assertTrue(comment1.canEqual(comment2));
        assertFalse(comment1.canEqual(other));
    }

    @Test
    void shouldNotBeEqualToNull() {
        Comment comment = new Comment("body", "user-id", "article-id");
        
        assertNotEquals(comment, null);
    }

    @Test
    void shouldNotBeEqualToDifferentClass() {
        Comment comment = new Comment("body", "user-id", "article-id");
        String other = "not a Comment";
        
        assertNotEquals(comment, other);
    }

    @Test
    void shouldBeEqualToItself() {
        Comment comment = new Comment("body", "user-id", "article-id");
        
        assertEquals(comment, comment);
    }

    @Test
    void shouldHandleEmptyStrings() {
        Comment comment = new Comment("", "", "");
        
        assertEquals("", comment.getBody());
        assertEquals("", comment.getUserId());
        assertEquals("", comment.getArticleId());
    }

    @Test
    void shouldHandleNullParameters() {
        Comment comment = new Comment(null, null, null);
        
        assertNotNull(comment.getId());
        assertNull(comment.getBody());
        assertNull(comment.getUserId());
        assertNull(comment.getArticleId());
        assertNotNull(comment.getCreatedAt());
    }

    @Test
    void shouldHandleLongContent() {
        String longBody = "This is a very long comment body that might be used in real applications. ".repeat(100);
        String longUserId = "very-long-user-id-".repeat(10);
        String longArticleId = "very-long-article-id-".repeat(10);
        
        Comment comment = new Comment(longBody, longUserId, longArticleId);
        
        assertEquals(longBody, comment.getBody());
        assertEquals(longUserId, comment.getUserId());
        assertEquals(longArticleId, comment.getArticleId());
    }

    @Test
    void shouldHandleSpecialCharactersInContent() {
        String specialBody = "Comment with special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?";
        String specialUserId = "user-id-with-special-chars-!@#$%";
        String specialArticleId = "article-id-with-special-chars-!@#$%";
        
        Comment comment = new Comment(specialBody, specialUserId, specialArticleId);
        
        assertEquals(specialBody, comment.getBody());
        assertEquals(specialUserId, comment.getUserId());
        assertEquals(specialArticleId, comment.getArticleId());
    }

    @Test
    void shouldTestHashCodeConsistency() {
        Comment comment = new Comment("body", "user-id", "article-id");
        
        int hashCode1 = comment.hashCode();
        int hashCode2 = comment.hashCode();
        
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void shouldGenerateUniqueIds() {
        Comment comment1 = new Comment("body", "user-id", "article-id");
        Comment comment2 = new Comment("body", "user-id", "article-id");
        
        assertNotEquals(comment1.getId(), comment2.getId());
        assertNotEquals(comment1, comment2);
    }

    @Test
    void shouldSetCreatedAtOnConstruction() {
        DateTime before = DateTime.now();
        Comment comment = new Comment("body", "user-id", "article-id");
        DateTime after = DateTime.now();
        
        assertNotNull(comment.getCreatedAt());
        assertTrue(comment.getCreatedAt().isAfter(before.minusSeconds(1)));
        assertTrue(comment.getCreatedAt().isBefore(after.plusSeconds(1)));
    }

    @Test
    void shouldTestToStringMethod() {
        Comment comment = new Comment("body", "user-id", "article-id");
        
        String toString = comment.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Comment"));
    }

    @Test
    void shouldTestToStringWithNullFields() {
        Comment comment = new Comment();
        
        String toString = comment.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Comment"));
    }

    @Test
    void shouldTestConstructorParameterOrder() {
        Comment comment = new Comment("test-body", "test-user", "test-article");
        
        assertEquals("test-body", comment.getBody());
        assertEquals("test-user", comment.getUserId());
        assertEquals("test-article", comment.getArticleId());
    }
}
