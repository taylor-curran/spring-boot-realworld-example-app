package io.spring.core.article;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

class ArticleComprehensiveTest {

    @Test
    void shouldCreateArticleWithAllFields() {
        List<String> tags = Arrays.asList("java", "spring");
        Article article = new Article("Test Title", "Test Description", "Test Body", tags, "user123");
        
        assertNotNull(article.getId());
        assertEquals("test-title", article.getSlug());
        assertEquals("Test Title", article.getTitle());
        assertEquals("Test Description", article.getDescription());
        assertEquals("Test Body", article.getBody());
        assertEquals(2, article.getTags().size());
        assertEquals("user123", article.getUserId());
        assertNotNull(article.getCreatedAt());
        assertNotNull(article.getUpdatedAt());
    }

    @Test
    void shouldCreateArticleWithDateTime() {
        DateTime now = DateTime.now();
        List<String> tags = Arrays.asList("test");
        Article article = new Article("Test Title", "Test Description", "Test Body", tags, "user123", now);
        
        assertEquals("test-title", article.getSlug());
        assertEquals("Test Title", article.getTitle());
        assertEquals("Test Description", article.getDescription());
        assertEquals("Test Body", article.getBody());
        assertEquals(1, article.getTags().size());
        assertEquals("user123", article.getUserId());
        assertEquals(now, article.getCreatedAt());
        assertEquals(now, article.getUpdatedAt());
    }

    @Test
    void shouldCreateArticleWithEmptyTags() {
        Article article = new Article("Test Title", "Test Description", "Test Body", Collections.emptyList(), "user123");
        
        assertEquals("test-title", article.getSlug());
        assertTrue(article.getTags().isEmpty());
    }

    @Test
    void shouldUpdateArticleFields() {
        Article article = new Article("Original Title", "Original Description", "Original Body", Arrays.asList("tag1"), "user123");
        DateTime originalCreated = article.getCreatedAt();
        DateTime originalUpdated = article.getUpdatedAt();
        
        article.update("Updated Title", "Updated Description", "Updated Body");
        
        assertEquals("updated-title", article.getSlug());
        assertEquals("Updated Title", article.getTitle());
        assertEquals("Updated Description", article.getDescription());
        assertEquals("Updated Body", article.getBody());
        assertEquals(originalCreated, article.getCreatedAt());
        assertTrue(article.getUpdatedAt().isAfter(originalUpdated) || article.getUpdatedAt().equals(originalUpdated));
    }

    @Test
    void shouldUpdateWithNullFields() {
        Article article = new Article("Original Title", "Original Description", "Original Body", Arrays.asList("tag1"), "user123");
        
        article.update(null, null, null);
        
        assertEquals("Original Title", article.getTitle());
        assertEquals("Original Description", article.getDescription());
        assertEquals("Original Body", article.getBody());
    }

    @Test
    void shouldUpdateWithEmptyFields() {
        Article article = new Article("Original Title", "Original Description", "Original Body", Arrays.asList("tag1"), "user123");
        
        article.update("", "", "");
        
        assertEquals("Original Title", article.getTitle());
        assertEquals("Original Description", article.getDescription());
        assertEquals("Original Body", article.getBody());
    }

    @Test
    void shouldGenerateSlugFromTitle() {
        assertEquals("hello-world", Article.toSlug("Hello World"));
        assertEquals("test-article-with-spaces", Article.toSlug("Test Article With Spaces"));
        assertEquals("special!@#$%^-*()characters", Article.toSlug("Special!@#$%^&*()Characters"));
        assertEquals("multiple-spaces", Article.toSlug("Multiple   Spaces"));
        assertEquals("", Article.toSlug(""));
    }

    @Test
    void shouldBeEqualWhenSameInstance() {
        List<String> tags = Arrays.asList("java", "spring");
        Article article = new Article("Test Title", "Test Description", "Test Body", tags, "user123");
        
        assertEquals(article, article);
        assertEquals(article.hashCode(), article.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenDifferentInstances() {
        List<String> tags = Arrays.asList("java", "spring");
        DateTime now = DateTime.now();
        Article article1 = new Article("Test Title", "Test Description", "Test Body", tags, "user123", now);
        Article article2 = new Article("Test Title", "Test Description", "Test Body", tags, "user123", now);
        
        assertNotEquals(article1, article2);
        assertNotEquals(article1.hashCode(), article2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenTitleDiffers() {
        List<String> tags = Arrays.asList("java", "spring");
        DateTime now = DateTime.now();
        Article article1 = new Article("Test Title 1", "Test Description", "Test Body", tags, "user123", now);
        Article article2 = new Article("Test Title 2", "Test Description", "Test Body", tags, "user123", now);
        
        assertNotEquals(article1, article2);
    }

    @Test
    void shouldNotBeEqualWhenDescriptionDiffers() {
        List<String> tags = Arrays.asList("java", "spring");
        DateTime now = DateTime.now();
        Article article1 = new Article("Test Title", "Test Description 1", "Test Body", tags, "user123", now);
        Article article2 = new Article("Test Title", "Test Description 2", "Test Body", tags, "user123", now);
        
        assertNotEquals(article1, article2);
    }

    @Test
    void shouldNotBeEqualWhenBodyDiffers() {
        List<String> tags = Arrays.asList("java", "spring");
        DateTime now = DateTime.now();
        Article article1 = new Article("Test Title", "Test Description", "Test Body 1", tags, "user123", now);
        Article article2 = new Article("Test Title", "Test Description", "Test Body 2", tags, "user123", now);
        
        assertNotEquals(article1, article2);
    }

    @Test
    void shouldNotBeEqualWhenTagsDiffer() {
        DateTime now = DateTime.now();
        Article article1 = new Article("Test Title", "Test Description", "Test Body", Arrays.asList("java"), "user123", now);
        Article article2 = new Article("Test Title", "Test Description", "Test Body", Arrays.asList("spring"), "user123", now);
        
        assertNotEquals(article1, article2);
    }

    @Test
    void shouldNotBeEqualWhenUserIdDiffers() {
        List<String> tags = Arrays.asList("java", "spring");
        DateTime now = DateTime.now();
        Article article1 = new Article("Test Title", "Test Description", "Test Body", tags, "user123", now);
        Article article2 = new Article("Test Title", "Test Description", "Test Body", tags, "user456", now);
        
        assertNotEquals(article1, article2);
    }

    @Test
    void shouldNotBeEqualWhenCreatedAtDiffers() {
        List<String> tags = Arrays.asList("java", "spring");
        Article article1 = new Article("Test Title", "Test Description", "Test Body", tags, "user123", DateTime.now());
        Article article2 = new Article("Test Title", "Test Description", "Test Body", tags, "user123", DateTime.now().plusHours(1));
        
        assertNotEquals(article1, article2);
    }

    @Test
    void shouldNotBeEqualToNull() {
        Article article = new Article("Test Title", "Test Description", "Test Body", Arrays.asList("tag1"), "user123");
        
        assertNotEquals(article, null);
    }

    @Test
    void shouldNotBeEqualToDifferentClass() {
        Article article = new Article("Test Title", "Test Description", "Test Body", Arrays.asList("tag1"), "user123");
        String other = "not an article";
        
        assertNotEquals(article, other);
    }

    @Test
    void shouldBeEqualToItself() {
        Article article = new Article("Test Title", "Test Description", "Test Body", Arrays.asList("tag1"), "user123");
        
        assertEquals(article, article);
    }

    @Test
    void shouldHaveConsistentHashCode() {
        Article article = new Article("Test Title", "Test Description", "Test Body", Arrays.asList("tag1"), "user123");
        
        int hashCode1 = article.hashCode();
        int hashCode2 = article.hashCode();
        
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void shouldHaveDifferentHashCodeForDifferentObjects() {
        Article article1 = new Article("Test Title 1", "Test Description", "Test Body", Arrays.asList("tag1"), "user123");
        Article article2 = new Article("Test Title 2", "Test Description", "Test Body", Arrays.asList("tag1"), "user123");
        
        assertNotEquals(article1.hashCode(), article2.hashCode());
    }

    @Test
    void shouldHandleNullFieldsInEquals() {
        Article article1 = new Article();
        Article article2 = new Article();
        
        assertEquals(article1, article2);
    }

    @Test
    void shouldHandleNullFieldsInHashCode() {
        Article article1 = new Article();
        Article article2 = new Article();
        
        assertEquals(article1.hashCode(), article2.hashCode());
    }

    @Test
    void shouldSupportCanEqual() {
        Article article1 = new Article("Test Title", "Test Description", "Test Body", Arrays.asList("tag1"), "user123");
        Article article2 = new Article("Test Title", "Test Description", "Test Body", Arrays.asList("tag1"), "user123");
        
        assertTrue(article1.canEqual(article2));
        assertTrue(article2.canEqual(article1));
    }

    @Test
    void shouldNotCanEqualDifferentClass() {
        Article article = new Article("Test Title", "Test Description", "Test Body", Arrays.asList("tag1"), "user123");
        String other = "not an article";
        
        assertFalse(article.canEqual(other));
    }

    @Test
    void shouldHandleMixedNullFieldsInEquals() {
        Article article1 = new Article("Test Title", null, "Test Body", Collections.emptyList(), "user123");
        Article article2 = new Article("Test Title", null, "Test Body", Collections.emptyList(), "user123");
        
        assertNotEquals(article1, article2);
    }

    @Test
    void shouldNotBeEqualWhenOneFieldIsNull() {
        Article article1 = new Article("Test Title", "Test Description", "Test Body", Arrays.asList("tag1"), "user123");
        Article article2 = new Article("Different Title", null, "Test Body", Arrays.asList("tag1"), "user123");
        
        assertNotEquals(article1, article2);
    }

    @Test
    void shouldCreateDefaultArticle() {
        Article article = new Article();
        
        assertNull(article.getId());
        assertNull(article.getSlug());
        assertNull(article.getTitle());
        assertNull(article.getDescription());
        assertNull(article.getBody());
        assertNull(article.getTags());
        assertNull(article.getUserId());
        assertNull(article.getCreatedAt());
        assertNull(article.getUpdatedAt());
    }
}
