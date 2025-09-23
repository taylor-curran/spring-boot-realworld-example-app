package io.spring.application.data;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

class ArticleDataListTest {

    @Test
    void shouldCreateArticleDataListWithArticles() {
        ProfileData profileData = new ProfileData("user-id", "testuser", "Test Bio", "avatar.jpg", false);
        ArticleData article1 = new ArticleData(
            "id1", "slug1", "Title 1", "Description 1", "Body 1", 
            false, 0, DateTime.now(), DateTime.now(), 
            Arrays.asList("tag1"), profileData
        );
        ArticleData article2 = new ArticleData(
            "id2", "slug2", "Title 2", "Description 2", "Body 2", 
            true, 5, DateTime.now(), DateTime.now(), 
            Arrays.asList("tag2", "tag3"), profileData
        );
        List<ArticleData> articles = Arrays.asList(article1, article2);
        int count = 2;

        ArticleDataList articleDataList = new ArticleDataList(articles, count);

        assertEquals(articles, articleDataList.getArticleDatas());
        assertEquals(count, articleDataList.getCount());
        assertEquals(2, articleDataList.getArticleDatas().size());
    }

    @Test
    void shouldCreateArticleDataListWithEmptyList() {
        List<ArticleData> emptyArticles = Collections.emptyList();
        int count = 0;

        ArticleDataList articleDataList = new ArticleDataList(emptyArticles, count);

        assertEquals(emptyArticles, articleDataList.getArticleDatas());
        assertEquals(0, articleDataList.getCount());
        assertTrue(articleDataList.getArticleDatas().isEmpty());
    }

    @Test
    void shouldCreateArticleDataListWithNullList() {
        List<ArticleData> nullArticles = null;
        int count = 0;

        ArticleDataList articleDataList = new ArticleDataList(nullArticles, count);

        assertNull(articleDataList.getArticleDatas());
        assertEquals(0, articleDataList.getCount());
    }

    @Test
    void shouldHandleCountMismatchWithActualSize() {
        ProfileData profileData = new ProfileData("user-id", "testuser", "Test Bio", "avatar.jpg", false);
        ArticleData article = new ArticleData(
            "id", "slug", "Title", "Description", "Body", 
            false, 0, DateTime.now(), DateTime.now(), 
            Arrays.asList("tag"), profileData
        );
        List<ArticleData> articles = Arrays.asList(article);
        int count = 10; // Different from actual size

        ArticleDataList articleDataList = new ArticleDataList(articles, count);

        assertEquals(articles, articleDataList.getArticleDatas());
        assertEquals(10, articleDataList.getCount()); // Should preserve the provided count
        assertEquals(1, articleDataList.getArticleDatas().size()); // Actual size is still 1
    }

    @Test
    void shouldHandleZeroCount() {
        List<ArticleData> articles = Collections.emptyList();
        int count = 0;

        ArticleDataList articleDataList = new ArticleDataList(articles, count);

        assertEquals(0, articleDataList.getCount());
        assertTrue(articleDataList.getArticleDatas().isEmpty());
    }

    @Test
    void shouldHandleNegativeCount() {
        List<ArticleData> articles = Collections.emptyList();
        int negativeCount = -1;

        ArticleDataList articleDataList = new ArticleDataList(articles, negativeCount);

        assertEquals(-1, articleDataList.getCount());
        assertTrue(articleDataList.getArticleDatas().isEmpty());
    }

    @Test
    void shouldHandleLargeCount() {
        List<ArticleData> articles = Collections.emptyList();
        int largeCount = 1000000;

        ArticleDataList articleDataList = new ArticleDataList(articles, largeCount);

        assertEquals(1000000, articleDataList.getCount());
        assertTrue(articleDataList.getArticleDatas().isEmpty());
    }

    @Test
    void shouldHandleSingleArticle() {
        ProfileData profileData = new ProfileData("user-id", "testuser", "Test Bio", "avatar.jpg", false);
        ArticleData singleArticle = new ArticleData(
            "single-id", "single-slug", "Single Title", "Single Description", "Single Body", 
            true, 1, DateTime.now(), DateTime.now(), 
            Arrays.asList("single-tag"), profileData
        );
        List<ArticleData> articles = Arrays.asList(singleArticle);
        int count = 1;

        ArticleDataList articleDataList = new ArticleDataList(articles, count);

        assertEquals(1, articleDataList.getArticleDatas().size());
        assertEquals(1, articleDataList.getCount());
        assertEquals("single-id", articleDataList.getArticleDatas().get(0).getId());
        assertEquals("Single Title", articleDataList.getArticleDatas().get(0).getTitle());
    }

    @Test
    void shouldHandleMultipleArticlesWithDifferentAuthors() {
        ProfileData author1 = new ProfileData("user1", "author1", "Bio 1", "avatar1.jpg", false);
        ProfileData author2 = new ProfileData("user2", "author2", "Bio 2", "avatar2.jpg", true);
        
        ArticleData article1 = new ArticleData(
            "id1", "slug1", "Title by Author 1", "Description 1", "Body 1", 
            false, 2, DateTime.now(), DateTime.now(), 
            Arrays.asList("java", "spring"), author1
        );
        ArticleData article2 = new ArticleData(
            "id2", "slug2", "Title by Author 2", "Description 2", "Body 2", 
            true, 10, DateTime.now(), DateTime.now(), 
            Arrays.asList("react", "javascript"), author2
        );
        
        List<ArticleData> articles = Arrays.asList(article1, article2);
        int count = 2;

        ArticleDataList articleDataList = new ArticleDataList(articles, count);

        assertEquals(2, articleDataList.getArticleDatas().size());
        assertEquals(2, articleDataList.getCount());
        assertEquals("author1", articleDataList.getArticleDatas().get(0).getProfileData().getUsername());
        assertEquals("author2", articleDataList.getArticleDatas().get(1).getProfileData().getUsername());
        assertFalse(articleDataList.getArticleDatas().get(0).getProfileData().isFollowing());
        assertTrue(articleDataList.getArticleDatas().get(1).getProfileData().isFollowing());
    }

    @Test
    void shouldHandleArticlesWithEmptyTags() {
        ProfileData profileData = new ProfileData("user-id", "testuser", "Test Bio", "avatar.jpg", false);
        ArticleData articleWithEmptyTags = new ArticleData(
            "id", "slug", "Title", "Description", "Body", 
            false, 0, DateTime.now(), DateTime.now(), 
            Collections.emptyList(), profileData
        );
        List<ArticleData> articles = Arrays.asList(articleWithEmptyTags);
        int count = 1;

        ArticleDataList articleDataList = new ArticleDataList(articles, count);

        assertEquals(1, articleDataList.getArticleDatas().size());
        assertTrue(articleDataList.getArticleDatas().get(0).getTagList().isEmpty());
    }

    @Test
    void shouldHandleArticlesWithNullTags() {
        ProfileData profileData = new ProfileData("user-id", "testuser", "Test Bio", "avatar.jpg", false);
        ArticleData articleWithNullTags = new ArticleData(
            "id", "slug", "Title", "Description", "Body", 
            false, 0, DateTime.now(), DateTime.now(), 
            null, profileData
        );
        List<ArticleData> articles = Arrays.asList(articleWithNullTags);
        int count = 1;

        ArticleDataList articleDataList = new ArticleDataList(articles, count);

        assertEquals(1, articleDataList.getArticleDatas().size());
        assertNull(articleDataList.getArticleDatas().get(0).getTagList());
    }
}
