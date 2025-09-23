package io.spring.application.data;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

class ArticleDataTest {

    @Test
    void shouldCreateArticleDataWithAllFields() {
        String id = "article-id";
        String slug = "test-article";
        String title = "Test Article";
        String description = "Test Description";
        String body = "Test Body";
        List<String> tagList = Arrays.asList("java", "spring");
        DateTime createdAt = DateTime.now();
        DateTime updatedAt = DateTime.now().plusHours(1);
        boolean favorited = true;
        int favoritesCount = 5;
        ProfileData profileData = new ProfileData("user-id", "testuser", "Test Bio", "avatar.jpg", false);

        ArticleData articleData = new ArticleData(
            id, slug, title, description, body, favorited, favoritesCount, 
            createdAt, updatedAt, tagList, profileData
        );

        assertEquals(id, articleData.getId());
        assertEquals(slug, articleData.getSlug());
        assertEquals(title, articleData.getTitle());
        assertEquals(description, articleData.getDescription());
        assertEquals(body, articleData.getBody());
        assertEquals(tagList, articleData.getTagList());
        assertEquals(createdAt, articleData.getCreatedAt());
        assertEquals(updatedAt, articleData.getUpdatedAt());
        assertTrue(articleData.isFavorited());
        assertEquals(favoritesCount, articleData.getFavoritesCount());
        assertEquals(profileData, articleData.getProfileData());
    }

    @Test
    void shouldCreateArticleDataWithEmptyTagList() {
        ArticleData articleData = new ArticleData(
            "id", "slug", "title", "desc", "body", false, 0,
            DateTime.now(), DateTime.now(), Collections.emptyList(),
            new ProfileData("user-id", "user", "bio", "image", false)
        );

        assertNotNull(articleData.getTagList());
        assertTrue(articleData.getTagList().isEmpty());
    }

    @Test
    void shouldCreateArticleDataWithNullTagList() {
        ArticleData articleData = new ArticleData(
            "id", "slug", "title", "desc", "body", false, 0,
            DateTime.now(), DateTime.now(), null,
            new ProfileData("user-id", "user", "bio", "image", false)
        );

        assertNull(articleData.getTagList());
    }

    @Test
    void shouldHandleZeroFavoritesCount() {
        ArticleData articleData = new ArticleData(
            "id", "slug", "title", "desc", "body", false, 0,
            DateTime.now(), DateTime.now(), Arrays.asList("tag"),
            new ProfileData("user-id", "user", "bio", "image", false)
        );

        assertEquals(0, articleData.getFavoritesCount());
        assertFalse(articleData.isFavorited());
    }

    @Test
    void shouldHandleHighFavoritesCount() {
        ArticleData articleData = new ArticleData(
            "id", "slug", "title", "desc", "body", true, 1000,
            DateTime.now(), DateTime.now(), Arrays.asList("popular"),
            new ProfileData("user-id", "user", "bio", "image", true)
        );

        assertEquals(1000, articleData.getFavoritesCount());
        assertTrue(articleData.isFavorited());
    }

    @Test
    void shouldHandleEmptyStrings() {
        ArticleData articleData = new ArticleData(
            "", "", "", "", "", false, 0,
            DateTime.now(), DateTime.now(), Collections.emptyList(),
            new ProfileData("", "", "", "", false)
        );

        assertEquals("", articleData.getId());
        assertEquals("", articleData.getSlug());
        assertEquals("", articleData.getTitle());
        assertEquals("", articleData.getDescription());
        assertEquals("", articleData.getBody());
    }

    @Test
    void shouldHandleSameDateTimes() {
        DateTime sameTime = DateTime.now();
        ArticleData articleData = new ArticleData(
            "id", "slug", "title", "desc", "body", false, 0,
            sameTime, sameTime, Arrays.asList("tag"),
            new ProfileData("user-id", "user", "bio", "image", false)
        );

        assertEquals(sameTime, articleData.getCreatedAt());
        assertEquals(sameTime, articleData.getUpdatedAt());
    }

    @Test
    void shouldHandleLongContent() {
        String longTitle = "This is a very long article title that might be used in some edge cases to test the system's ability to handle longer content";
        String longDescription = "This is a very long description that contains multiple sentences and might be used to test how the system handles longer text content in article descriptions.";
        String longBody = "This is a very long article body that contains multiple paragraphs and extensive content. ".repeat(10);
        
        ArticleData articleData = new ArticleData(
            "id", "long-article-slug", longTitle, longDescription, longBody, 
            false, 0, DateTime.now(), DateTime.now(),
            Arrays.asList("long", "content", "test"),
            new ProfileData("user-id", "user", "bio", "image", false)
        );

        assertEquals(longTitle, articleData.getTitle());
        assertEquals(longDescription, articleData.getDescription());
        assertEquals(longBody, articleData.getBody());
        assertEquals(3, articleData.getTagList().size());
    }

    @Test
    void shouldHandleSpecialCharactersInContent() {
        String titleWithSpecialChars = "Test Article with Special Characters: !@#$%^&*()";
        String bodyWithUnicode = "Article with unicode: 你好世界 🌍 café naïve résumé";
        
        ArticleData articleData = new ArticleData(
            "id", "special-chars-slug", titleWithSpecialChars, "desc", bodyWithUnicode,
            false, 0, DateTime.now(), DateTime.now(),
            Arrays.asList("unicode", "special-chars"),
            new ProfileData("user-id", "user", "bio", "image", false)
        );

        assertEquals(titleWithSpecialChars, articleData.getTitle());
        assertEquals(bodyWithUnicode, articleData.getBody());
    }
}
