package io.spring.application.data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ArticleFavoriteCountTest {

    @Test
    void shouldCreateArticleFavoriteCountWithAllFields() {
        String id = "article-id-123";
        Integer count = 42;

        ArticleFavoriteCount favoriteCount = new ArticleFavoriteCount(id, count);

        assertEquals(id, favoriteCount.getId());
        assertEquals(count, favoriteCount.getCount());
    }

    @Test
    void shouldCreateArticleFavoriteCountWithZeroCount() {
        String id = "article-id";
        Integer count = 0;

        ArticleFavoriteCount favoriteCount = new ArticleFavoriteCount(id, count);

        assertEquals(id, favoriteCount.getId());
        assertEquals(0, favoriteCount.getCount());
    }

    @Test
    void shouldCreateArticleFavoriteCountWithNullId() {
        String id = null;
        Integer count = 5;

        ArticleFavoriteCount favoriteCount = new ArticleFavoriteCount(id, count);

        assertNull(favoriteCount.getId());
        assertEquals(5, favoriteCount.getCount());
    }

    @Test
    void shouldCreateArticleFavoriteCountWithNullCount() {
        String id = "article-id";
        Integer count = null;

        ArticleFavoriteCount favoriteCount = new ArticleFavoriteCount(id, count);

        assertEquals(id, favoriteCount.getId());
        assertNull(favoriteCount.getCount());
    }

    @Test
    void shouldCreateArticleFavoriteCountWithBothNullFields() {
        ArticleFavoriteCount favoriteCount = new ArticleFavoriteCount(null, null);

        assertNull(favoriteCount.getId());
        assertNull(favoriteCount.getCount());
    }

    @Test
    void shouldHandleLargeCount() {
        String id = "popular-article";
        Integer largeCount = Integer.MAX_VALUE;

        ArticleFavoriteCount favoriteCount = new ArticleFavoriteCount(id, largeCount);

        assertEquals(id, favoriteCount.getId());
        assertEquals(Integer.MAX_VALUE, favoriteCount.getCount());
    }

    @Test
    void shouldHandleNegativeCount() {
        String id = "article-id";
        Integer negativeCount = -1;

        ArticleFavoriteCount favoriteCount = new ArticleFavoriteCount(id, negativeCount);

        assertEquals(id, favoriteCount.getId());
        assertEquals(-1, favoriteCount.getCount());
    }

    @Test
    void shouldImplementEqualsCorrectly() {
        ArticleFavoriteCount favoriteCount1 = new ArticleFavoriteCount("id", 10);
        ArticleFavoriteCount favoriteCount2 = new ArticleFavoriteCount("id", 10);
        ArticleFavoriteCount favoriteCount3 = new ArticleFavoriteCount("different-id", 10);
        ArticleFavoriteCount favoriteCount4 = new ArticleFavoriteCount("id", 20);

        assertEquals(favoriteCount1, favoriteCount2);
        assertNotEquals(favoriteCount1, favoriteCount3);
        assertNotEquals(favoriteCount1, favoriteCount4);
        assertNotEquals(favoriteCount1, null);
        assertNotEquals(favoriteCount1, "not an ArticleFavoriteCount object");
    }

    @Test
    void shouldImplementHashCodeCorrectly() {
        ArticleFavoriteCount favoriteCount1 = new ArticleFavoriteCount("id", 10);
        ArticleFavoriteCount favoriteCount2 = new ArticleFavoriteCount("id", 10);

        assertEquals(favoriteCount1.hashCode(), favoriteCount2.hashCode());
    }

    @Test
    void shouldImplementToStringCorrectly() {
        ArticleFavoriteCount favoriteCount = new ArticleFavoriteCount("article-123", 42);
        String toString = favoriteCount.toString();

        assertTrue(toString.contains("ArticleFavoriteCount"));
        assertTrue(toString.contains("id=article-123"));
        assertTrue(toString.contains("count=42"));
    }

    @Test
    void shouldHandleEmptyStringId() {
        String emptyId = "";
        Integer count = 5;

        ArticleFavoriteCount favoriteCount = new ArticleFavoriteCount(emptyId, count);

        assertEquals("", favoriteCount.getId());
        assertEquals(5, favoriteCount.getCount());
    }

    @Test
    void shouldHandleUuidId() {
        String uuidId = "550e8400-e29b-41d4-a716-446655440000";
        Integer count = 15;

        ArticleFavoriteCount favoriteCount = new ArticleFavoriteCount(uuidId, count);

        assertEquals(uuidId, favoriteCount.getId());
        assertEquals(15, favoriteCount.getCount());
    }

    @Test
    void shouldHandleSpecialCharactersInId() {
        String specialId = "article-id-with-special-chars!@#$%^&*()";
        Integer count = 3;

        ArticleFavoriteCount favoriteCount = new ArticleFavoriteCount(specialId, count);

        assertEquals(specialId, favoriteCount.getId());
        assertEquals(3, favoriteCount.getCount());
    }

    @Test
    void shouldBeImmutable() {
        String id = "article-id";
        Integer count = 10;
        ArticleFavoriteCount favoriteCount = new ArticleFavoriteCount(id, count);

        assertEquals(id, favoriteCount.getId());
        assertEquals(count, favoriteCount.getCount());
        
        assertNotNull(favoriteCount.getId());
        assertNotNull(favoriteCount.getCount());
    }

    @Test
    void shouldHandleEqualsWithNullFields() {
        ArticleFavoriteCount favoriteCount1 = new ArticleFavoriteCount(null, null);
        ArticleFavoriteCount favoriteCount2 = new ArticleFavoriteCount(null, null);
        ArticleFavoriteCount favoriteCount3 = new ArticleFavoriteCount("id", null);

        assertEquals(favoriteCount1, favoriteCount2);
        assertNotEquals(favoriteCount1, favoriteCount3);
    }
}
