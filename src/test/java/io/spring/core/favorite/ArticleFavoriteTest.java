package io.spring.core.favorite;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ArticleFavoriteTest {

    @Test
    void shouldCreateArticleFavoriteWithValidData() {
        ArticleFavorite favorite = new ArticleFavorite("article123", "user456");

        assertNotNull(favorite);
        assertEquals("article123", favorite.getArticleId());
        assertEquals("user456", favorite.getUserId());
    }

    @Test
    void shouldCreateDifferentFavorites() {
        ArticleFavorite favorite1 = new ArticleFavorite("article123", "user456");
        ArticleFavorite favorite2 = new ArticleFavorite("article789", "user456");

        assertNotEquals(favorite1, favorite2);
        assertEquals("article123", favorite1.getArticleId());
        assertEquals("article789", favorite2.getArticleId());
        assertEquals("user456", favorite1.getUserId());
        assertEquals("user456", favorite2.getUserId());
    }

    @Test
    void shouldCreateFavoritesForSameArticleDifferentUsers() {
        ArticleFavorite favorite1 = new ArticleFavorite("article123", "user456");
        ArticleFavorite favorite2 = new ArticleFavorite("article123", "user789");

        assertNotEquals(favorite1, favorite2);
        assertEquals("article123", favorite1.getArticleId());
        assertEquals("article123", favorite2.getArticleId());
        assertNotEquals(favorite1.getUserId(), favorite2.getUserId());
    }

    @Test
    void shouldHandleNullArticleId() {
        ArticleFavorite favorite = new ArticleFavorite(null, "user456");

        assertNull(favorite.getArticleId());
        assertEquals("user456", favorite.getUserId());
    }

    @Test
    void shouldHandleNullUserId() {
        ArticleFavorite favorite = new ArticleFavorite("article123", null);

        assertEquals("article123", favorite.getArticleId());
        assertNull(favorite.getUserId());
    }

    @Test
    void shouldHandleEmptyArticleId() {
        ArticleFavorite favorite = new ArticleFavorite("", "user456");

        assertEquals("", favorite.getArticleId());
        assertEquals("user456", favorite.getUserId());
    }

    @Test
    void shouldHandleEmptyUserId() {
        ArticleFavorite favorite = new ArticleFavorite("article123", "");

        assertEquals("article123", favorite.getArticleId());
        assertEquals("", favorite.getUserId());
    }

    @Test
    void shouldHandleSpecialCharactersInIds() {
        ArticleFavorite favorite = new ArticleFavorite("article-123_test", "user-456_test");

        assertEquals("article-123_test", favorite.getArticleId());
        assertEquals("user-456_test", favorite.getUserId());
    }

    @Test
    void shouldHandleUuidFormatIds() {
        String articleUuid = "550e8400-e29b-41d4-a716-446655440000";
        String userUuid = "6ba7b810-9dad-11d1-80b4-00c04fd430c8";
        ArticleFavorite favorite = new ArticleFavorite(articleUuid, userUuid);

        assertEquals(articleUuid, favorite.getArticleId());
        assertEquals(userUuid, favorite.getUserId());
    }

    @Test
    void shouldHandleLongIds() {
        String longArticleId = "very-long-article-id-that-might-be-used-in-some-systems".repeat(5);
        String longUserId = "very-long-user-id-that-might-be-used-in-some-systems".repeat(5);
        ArticleFavorite favorite = new ArticleFavorite(longArticleId, longUserId);

        assertEquals(longArticleId, favorite.getArticleId());
        assertEquals(longUserId, favorite.getUserId());
    }

    @Test
    void shouldHandleNumericIds() {
        ArticleFavorite favorite = new ArticleFavorite("123456", "789012");

        assertEquals("123456", favorite.getArticleId());
        assertEquals("789012", favorite.getUserId());
    }

    @Test
    void shouldCreateMultipleFavoritesForSameUser() {
        String userId = "user456";
        ArticleFavorite favorite1 = new ArticleFavorite("article1", userId);
        ArticleFavorite favorite2 = new ArticleFavorite("article2", userId);
        ArticleFavorite favorite3 = new ArticleFavorite("article3", userId);

        assertEquals(userId, favorite1.getUserId());
        assertEquals(userId, favorite2.getUserId());
        assertEquals(userId, favorite3.getUserId());
        
        assertNotEquals(favorite1.getArticleId(), favorite2.getArticleId());
        assertNotEquals(favorite2.getArticleId(), favorite3.getArticleId());
        assertNotEquals(favorite1, favorite2);
        assertNotEquals(favorite2, favorite3);
    }

    @Test
    void shouldCreateMultipleFavoritesForSameArticle() {
        String articleId = "article123";
        ArticleFavorite favorite1 = new ArticleFavorite(articleId, "user1");
        ArticleFavorite favorite2 = new ArticleFavorite(articleId, "user2");
        ArticleFavorite favorite3 = new ArticleFavorite(articleId, "user3");

        assertEquals(articleId, favorite1.getArticleId());
        assertEquals(articleId, favorite2.getArticleId());
        assertEquals(articleId, favorite3.getArticleId());
        
        assertNotEquals(favorite1.getUserId(), favorite2.getUserId());
        assertNotEquals(favorite2.getUserId(), favorite3.getUserId());
        assertNotEquals(favorite1, favorite2);
        assertNotEquals(favorite2, favorite3);
    }

    @Test
    void shouldTestEqualsAndHashCode() {
        ArticleFavorite favorite1 = new ArticleFavorite("article123", "user456");
        ArticleFavorite favorite2 = new ArticleFavorite("article123", "user456");
        ArticleFavorite favorite3 = new ArticleFavorite("article456", "user456");

        assertEquals(favorite1, favorite2);
        assertEquals(favorite1.hashCode(), favorite2.hashCode());
        assertNotEquals(favorite1, favorite3);
        assertNotEquals(favorite1.hashCode(), favorite3.hashCode());
    }

    @Test
    void shouldTestEqualsWithNullFields() {
        ArticleFavorite favorite1 = new ArticleFavorite(null, null);
        ArticleFavorite favorite2 = new ArticleFavorite(null, null);
        ArticleFavorite favorite3 = new ArticleFavorite("article123", null);
        ArticleFavorite favorite4 = new ArticleFavorite(null, "user456");

        assertEquals(favorite1, favorite2);
        assertEquals(favorite1.hashCode(), favorite2.hashCode());
        assertNotEquals(favorite1, favorite3);
        assertNotEquals(favorite1, favorite4);
        assertNotEquals(favorite3, favorite4);
    }

    @Test
    void shouldTestEqualsWithSelf() {
        ArticleFavorite favorite = new ArticleFavorite("article123", "user456");
        
        assertEquals(favorite, favorite);
        assertTrue(favorite.equals(favorite));
    }

    @Test
    void shouldTestEqualsWithNull() {
        ArticleFavorite favorite = new ArticleFavorite("article123", "user456");
        
        assertNotEquals(favorite, null);
        assertFalse(favorite.equals(null));
    }

    @Test
    void shouldTestEqualsWithDifferentClass() {
        ArticleFavorite favorite = new ArticleFavorite("article123", "user456");
        String other = "not an ArticleFavorite";
        
        assertNotEquals(favorite, other);
        assertFalse(favorite.equals(other));
    }

    @Test
    void shouldTestCanEqual() {
        ArticleFavorite favorite1 = new ArticleFavorite("article123", "user456");
        ArticleFavorite favorite2 = new ArticleFavorite("article456", "user789");
        String other = "not an ArticleFavorite";
        
        assertTrue(favorite1.canEqual(favorite2));
        assertFalse(favorite1.canEqual(other));
    }

    @Test
    void shouldTestToString() {
        ArticleFavorite favorite = new ArticleFavorite("article123", "user456");
        
        String toString = favorite.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("ArticleFavorite"));
    }

    @Test
    void shouldTestToStringWithNullFields() {
        ArticleFavorite favorite = new ArticleFavorite(null, null);
        
        String toString = favorite.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("ArticleFavorite"));
    }

    @Test
    void shouldTestHashCodeConsistency() {
        ArticleFavorite favorite = new ArticleFavorite("article123", "user456");
        
        int hashCode1 = favorite.hashCode();
        int hashCode2 = favorite.hashCode();
        
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void shouldTestHashCodeWithNullFields() {
        ArticleFavorite favorite1 = new ArticleFavorite(null, null);
        ArticleFavorite favorite2 = new ArticleFavorite(null, null);
        ArticleFavorite favorite3 = new ArticleFavorite("article123", null);
        ArticleFavorite favorite4 = new ArticleFavorite(null, "user456");
        
        assertEquals(favorite1.hashCode(), favorite2.hashCode());
        assertNotEquals(favorite1.hashCode(), favorite3.hashCode());
        assertNotEquals(favorite1.hashCode(), favorite4.hashCode());
        assertNotEquals(favorite3.hashCode(), favorite4.hashCode());
    }

    @Test
    void shouldTestNoArgsConstructor() {
        ArticleFavorite favorite = new ArticleFavorite();

        assertNull(favorite.getArticleId());
        assertNull(favorite.getUserId());
    }
}
