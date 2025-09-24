package io.spring.core.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class FollowRelationTest {

    @Test
    void shouldCreateFollowRelationWithValidData() {
        FollowRelation followRelation = new FollowRelation("user123", "target456");

        assertEquals("user123", followRelation.getUserId());
        assertEquals("target456", followRelation.getTargetId());
    }

    @Test
    void shouldCreateFollowRelationWithNullValues() {
        FollowRelation followRelation = new FollowRelation(null, null);

        assertNull(followRelation.getUserId());
        assertNull(followRelation.getTargetId());
    }

    @Test
    void shouldCreateFollowRelationWithEmptyStrings() {
        FollowRelation followRelation = new FollowRelation("", "");

        assertEquals("", followRelation.getUserId());
        assertEquals("", followRelation.getTargetId());
    }

    @Test
    void shouldCreateFollowRelationWithSpecialCharacters() {
        String userId = "user-123_test@domain.com";
        String targetId = "target!@#$%^&*()";
        FollowRelation followRelation = new FollowRelation(userId, targetId);

        assertEquals(userId, followRelation.getUserId());
        assertEquals(targetId, followRelation.getTargetId());
    }

    @Test
    void shouldCreateFollowRelationWithUnicodeCharacters() {
        String userId = "用户123";
        String targetId = "目标456";
        FollowRelation followRelation = new FollowRelation(userId, targetId);

        assertEquals(userId, followRelation.getUserId());
        assertEquals(targetId, followRelation.getTargetId());
    }

    @Test
    void shouldCreateFollowRelationWithLongIds() {
        String longUserId = "very-long-user-id-".repeat(50);
        String longTargetId = "very-long-target-id-".repeat(50);
        FollowRelation followRelation = new FollowRelation(longUserId, longTargetId);

        assertEquals(longUserId, followRelation.getUserId());
        assertEquals(longTargetId, followRelation.getTargetId());
    }

    @Test
    void shouldCreateWithNoArgsConstructor() {
        FollowRelation followRelation = new FollowRelation();

        assertNull(followRelation.getUserId());
        assertNull(followRelation.getTargetId());
    }

    @Test
    void shouldSetUserIdAfterCreation() {
        FollowRelation followRelation = new FollowRelation();
        followRelation.setUserId("newUser123");

        assertEquals("newUser123", followRelation.getUserId());
        assertNull(followRelation.getTargetId());
    }

    @Test
    void shouldSetTargetIdAfterCreation() {
        FollowRelation followRelation = new FollowRelation();
        followRelation.setTargetId("newTarget456");

        assertNull(followRelation.getUserId());
        assertEquals("newTarget456", followRelation.getTargetId());
    }

    @Test
    void shouldSetBothIdsAfterCreation() {
        FollowRelation followRelation = new FollowRelation();
        followRelation.setUserId("user123");
        followRelation.setTargetId("target456");

        assertEquals("user123", followRelation.getUserId());
        assertEquals("target456", followRelation.getTargetId());
    }

    @Test
    void shouldOverrideExistingIds() {
        FollowRelation followRelation = new FollowRelation("oldUser", "oldTarget");
        followRelation.setUserId("newUser");
        followRelation.setTargetId("newTarget");

        assertEquals("newUser", followRelation.getUserId());
        assertEquals("newTarget", followRelation.getTargetId());
    }

    @Test
    void shouldHandleEqualsAndHashCode() {
        FollowRelation relation1 = new FollowRelation("user123", "target456");
        FollowRelation relation2 = new FollowRelation("user123", "target456");
        FollowRelation relation3 = new FollowRelation("user456", "target789");

        assertEquals(relation1, relation2);
        assertEquals(relation1.hashCode(), relation2.hashCode());
        assertNotEquals(relation1, relation3);
        assertNotEquals(relation1.hashCode(), relation3.hashCode());
    }

    @Test
    void shouldHandleEqualsWithNullValues() {
        FollowRelation relation1 = new FollowRelation(null, null);
        FollowRelation relation2 = new FollowRelation(null, null);
        FollowRelation relation3 = new FollowRelation("user", null);

        assertEquals(relation1, relation2);
        assertEquals(relation1.hashCode(), relation2.hashCode());
        assertNotEquals(relation1, relation3);
    }

    @Test
    void shouldHandleEqualsWithPartialNullValues() {
        FollowRelation relation1 = new FollowRelation("user123", null);
        FollowRelation relation2 = new FollowRelation("user123", null);
        FollowRelation relation3 = new FollowRelation(null, "target456");

        assertEquals(relation1, relation2);
        assertEquals(relation1.hashCode(), relation2.hashCode());
        assertNotEquals(relation1, relation3);
    }

    @Test
    void shouldNotEqualNull() {
        FollowRelation followRelation = new FollowRelation("user123", "target456");

        assertNotEquals(followRelation, null);
    }

    @Test
    void shouldNotEqualDifferentClass() {
        FollowRelation followRelation = new FollowRelation("user123", "target456");
        String notAFollowRelation = "not a follow relation";

        assertNotEquals(followRelation, notAFollowRelation);
    }

    @Test
    void shouldEqualSelf() {
        FollowRelation followRelation = new FollowRelation("user123", "target456");

        assertEquals(followRelation, followRelation);
        assertEquals(followRelation.hashCode(), followRelation.hashCode());
    }

    @Test
    void shouldHandleToString() {
        FollowRelation followRelation = new FollowRelation("user123", "target456");

        String toString = followRelation.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("FollowRelation"));
        assertTrue(toString.contains("user123"));
        assertTrue(toString.contains("target456"));
    }

    @Test
    void shouldHandleToStringWithNullValues() {
        FollowRelation followRelation = new FollowRelation(null, null);

        String toString = followRelation.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("FollowRelation"));
    }

    @Test
    void shouldHandleToStringWithEmptyValues() {
        FollowRelation followRelation = new FollowRelation("", "");

        String toString = followRelation.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("FollowRelation"));
    }
}
