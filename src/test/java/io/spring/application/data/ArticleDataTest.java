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

    @Test
    void shouldImplementEqualsCorrectly() {
        DateTime now = DateTime.now();
        ProfileData profileData = new ProfileData("profile-id", "author", "bio", "image.jpg", false);
        List<String> tagList = Arrays.asList("tag1", "tag2");

        ArticleData article1 = new ArticleData("id", "slug", "title", "desc", "body", true, 5, now, now, tagList, profileData);
        ArticleData article2 = new ArticleData("id", "slug", "title", "desc", "body", true, 5, now, now, tagList, profileData);
        ArticleData article3 = new ArticleData("different-id", "slug", "title", "desc", "body", true, 5, now, now, tagList, profileData);

        assertEquals(article1, article2);
        assertEquals(article1, article1);
        assertNotEquals(article1, article3);
        assertNotEquals(article1, null);
        assertNotEquals(article1, "not an ArticleData");
    }

    @Test
    void shouldImplementEqualsWithNullFields() {
        ArticleData article1 = new ArticleData(null, null, null, null, null, false, 0, null, null, null, null);
        ArticleData article2 = new ArticleData(null, null, null, null, null, false, 0, null, null, null, null);
        ArticleData article3 = new ArticleData("id", null, null, null, null, false, 0, null, null, null, null);

        assertEquals(article1, article2);
        assertNotEquals(article1, article3);
    }

    @Test
    void shouldImplementEqualsWithDifferentFields() {
        DateTime now = DateTime.now();
        DateTime later = now.plusHours(1);
        ProfileData profile1 = new ProfileData("profile1", "author1", "bio", "image", false);
        ProfileData profile2 = new ProfileData("profile2", "author2", "bio", "image", false);
        List<String> tags1 = Arrays.asList("tag1");
        List<String> tags2 = Arrays.asList("tag2");

        ArticleData baseArticle = new ArticleData("id", "slug", "title", "desc", "body", true, 5, now, now, tags1, profile1);

        ArticleData differentSlug = new ArticleData("id", "different-slug", "title", "desc", "body", true, 5, now, now, tags1, profile1);
        ArticleData differentTitle = new ArticleData("id", "slug", "different-title", "desc", "body", true, 5, now, now, tags1, profile1);
        ArticleData differentDescription = new ArticleData("id", "slug", "title", "different-desc", "body", true, 5, now, now, tags1, profile1);
        ArticleData differentBody = new ArticleData("id", "slug", "title", "desc", "different-body", true, 5, now, now, tags1, profile1);
        ArticleData differentFavorited = new ArticleData("id", "slug", "title", "desc", "body", false, 5, now, now, tags1, profile1);
        ArticleData differentCount = new ArticleData("id", "slug", "title", "desc", "body", true, 10, now, now, tags1, profile1);
        ArticleData differentCreated = new ArticleData("id", "slug", "title", "desc", "body", true, 5, later, now, tags1, profile1);
        ArticleData differentUpdated = new ArticleData("id", "slug", "title", "desc", "body", true, 5, now, later, tags1, profile1);
        ArticleData differentTags = new ArticleData("id", "slug", "title", "desc", "body", true, 5, now, now, tags2, profile1);
        ArticleData differentProfile = new ArticleData("id", "slug", "title", "desc", "body", true, 5, now, now, tags1, profile2);

        assertNotEquals(baseArticle, differentSlug);
        assertNotEquals(baseArticle, differentTitle);
        assertNotEquals(baseArticle, differentDescription);
        assertNotEquals(baseArticle, differentBody);
        assertNotEquals(baseArticle, differentFavorited);
        assertNotEquals(baseArticle, differentCount);
        assertNotEquals(baseArticle, differentCreated);
        assertNotEquals(baseArticle, differentUpdated);
        assertNotEquals(baseArticle, differentTags);
        assertNotEquals(baseArticle, differentProfile);
    }

    @Test
    void shouldImplementHashCodeCorrectly() {
        DateTime now = DateTime.now();
        ProfileData profileData = new ProfileData("profile-id", "author", "bio", "image", false);
        List<String> tagList = Arrays.asList("tag1", "tag2");

        ArticleData article1 = new ArticleData("id", "slug", "title", "desc", "body", true, 5, now, now, tagList, profileData);
        ArticleData article2 = new ArticleData("id", "slug", "title", "desc", "body", true, 5, now, now, tagList, profileData);

        assertEquals(article1.hashCode(), article2.hashCode());
    }

    @Test
    void shouldImplementHashCodeWithNullFields() {
        ArticleData article1 = new ArticleData(null, null, null, null, null, false, 0, null, null, null, null);
        ArticleData article2 = new ArticleData(null, null, null, null, null, false, 0, null, null, null, null);

        assertEquals(article1.hashCode(), article2.hashCode());
    }

    @Test
    void shouldImplementToStringCorrectly() {
        DateTime now = DateTime.now();
        ProfileData profileData = new ProfileData("profile-id", "author", "bio", "image", false);
        List<String> tagList = Arrays.asList("tag1", "tag2");

        ArticleData articleData = new ArticleData("id", "slug", "title", "desc", "body", true, 5, now, now, tagList, profileData);
        String toString = articleData.toString();

        assertTrue(toString.contains("ArticleData"));
        assertTrue(toString.contains("id=id"));
        assertTrue(toString.contains("slug=slug"));
        assertTrue(toString.contains("title=title"));
        assertTrue(toString.contains("favorited=true"));
        assertTrue(toString.contains("favoritesCount=5"));
    }

    @Test
    void shouldImplementToStringWithNullFields() {
        ArticleData articleData = new ArticleData(null, null, null, null, null, false, 0, null, null, null, null);
        String toString = articleData.toString();

        assertTrue(toString.contains("ArticleData"));
        assertTrue(toString.contains("id=null"));
        assertTrue(toString.contains("favorited=false"));
        assertTrue(toString.contains("favoritesCount=0"));
    }

    @Test
    void shouldCreateArticleDataWithDefaultConstructor() {
        ArticleData articleData = new ArticleData();

        assertNull(articleData.getId());
        assertNull(articleData.getSlug());
        assertNull(articleData.getTitle());
        assertNull(articleData.getDescription());
        assertNull(articleData.getBody());
        assertFalse(articleData.isFavorited());
        assertEquals(0, articleData.getFavoritesCount());
        assertNull(articleData.getCreatedAt());
        assertNull(articleData.getUpdatedAt());
        assertNull(articleData.getTagList());
        assertNull(articleData.getProfileData());
    }

    @Test
    void shouldSetFieldsUsingSetters() {
        ArticleData articleData = new ArticleData();
        DateTime now = DateTime.now();
        ProfileData profileData = new ProfileData("profile-id", "author", "bio", "image", false);
        List<String> tagList = Arrays.asList("tag1", "tag2");

        articleData.setId("new-id");
        articleData.setSlug("new-slug");
        articleData.setTitle("new-title");
        articleData.setDescription("new-description");
        articleData.setBody("new-body");
        articleData.setFavorited(true);
        articleData.setFavoritesCount(10);
        articleData.setCreatedAt(now);
        articleData.setUpdatedAt(now);
        articleData.setTagList(tagList);
        articleData.setProfileData(profileData);

        assertEquals("new-id", articleData.getId());
        assertEquals("new-slug", articleData.getSlug());
        assertEquals("new-title", articleData.getTitle());
        assertEquals("new-description", articleData.getDescription());
        assertEquals("new-body", articleData.getBody());
        assertTrue(articleData.isFavorited());
        assertEquals(10, articleData.getFavoritesCount());
        assertEquals(now, articleData.getCreatedAt());
        assertEquals(now, articleData.getUpdatedAt());
        assertEquals(tagList, articleData.getTagList());
        assertEquals(profileData, articleData.getProfileData());
    }
}
