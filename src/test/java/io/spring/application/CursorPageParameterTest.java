package io.spring.application;

import static org.junit.jupiter.api.Assertions.*;

import io.spring.application.CursorPager.Direction;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

class CursorPageParameterTest {

    @Test
    void shouldCreateCursorPageParameterWithDefaults() {
        CursorPageParameter<String> pageParam = new CursorPageParameter<>();

        assertEquals(20, pageParam.getLimit());
        assertNull(pageParam.getCursor());
        assertNull(pageParam.getDirection());
        assertEquals(21, pageParam.getQueryLimit());
    }

    @Test
    void shouldCreateCursorPageParameterWithAllParameters() {
        String cursor = "test-cursor";
        int limit = 10;
        Direction direction = Direction.NEXT;

        CursorPageParameter<String> pageParam = new CursorPageParameter<>(cursor, limit, direction);

        assertEquals(limit, pageParam.getLimit());
        assertEquals(cursor, pageParam.getCursor());
        assertEquals(direction, pageParam.getDirection());
        assertEquals(11, pageParam.getQueryLimit());
    }

    @Test
    void shouldEnforceLimitMaximum() {
        CursorPageParameter<String> pageParam = new CursorPageParameter<>("cursor", 2000, Direction.NEXT);

        assertEquals(1000, pageParam.getLimit());
        assertEquals(1001, pageParam.getQueryLimit());
    }

    @Test
    void shouldEnforceLimitMinimum() {
        CursorPageParameter<String> pageParam = new CursorPageParameter<>("cursor", -5, Direction.NEXT);

        assertEquals(20, pageParam.getLimit());
        assertEquals(21, pageParam.getQueryLimit());
    }

    @Test
    void shouldEnforceLimitZero() {
        CursorPageParameter<String> pageParam = new CursorPageParameter<>("cursor", 0, Direction.NEXT);

        assertEquals(20, pageParam.getLimit());
        assertEquals(21, pageParam.getQueryLimit());
    }

    @Test
    void shouldSetValidLimit() {
        CursorPageParameter<String> pageParam = new CursorPageParameter<>("cursor", 50, Direction.NEXT);

        assertEquals(50, pageParam.getLimit());
        assertEquals(51, pageParam.getQueryLimit());
    }

    @Test
    void shouldReturnTrueForNextDirection() {
        CursorPageParameter<String> pageParam = new CursorPageParameter<>("cursor", 10, Direction.NEXT);

        assertTrue(pageParam.isNext());
    }

    @Test
    void shouldReturnFalseForPrevDirection() {
        CursorPageParameter<String> pageParam = new CursorPageParameter<>("cursor", 10, Direction.PREV);

        assertFalse(pageParam.isNext());
    }

    @Test
    void shouldReturnFalseForNullDirection() {
        CursorPageParameter<String> pageParam = new CursorPageParameter<>("cursor", 10, null);

        assertFalse(pageParam.isNext());
    }

    @Test
    void shouldHandleDateTimeCursor() {
        DateTime cursor = DateTime.now();
        CursorPageParameter<DateTime> pageParam = new CursorPageParameter<>(cursor, 15, Direction.PREV);

        assertEquals(cursor, pageParam.getCursor());
        assertEquals(15, pageParam.getLimit());
        assertEquals(Direction.PREV, pageParam.getDirection());
        assertFalse(pageParam.isNext());
    }

    @Test
    void shouldHandleNullCursor() {
        CursorPageParameter<String> pageParam = new CursorPageParameter<>(null, 25, Direction.NEXT);

        assertNull(pageParam.getCursor());
        assertEquals(25, pageParam.getLimit());
        assertTrue(pageParam.isNext());
    }

    @Test
    void shouldCalculateQueryLimitCorrectly() {
        CursorPageParameter<String> pageParam1 = new CursorPageParameter<>("cursor", 1, Direction.NEXT);
        CursorPageParameter<String> pageParam2 = new CursorPageParameter<>("cursor", 999, Direction.NEXT);

        assertEquals(2, pageParam1.getQueryLimit());
        assertEquals(1000, pageParam2.getQueryLimit());
    }

    @Test
    void shouldHandleMaxLimitBoundary() {
        CursorPageParameter<String> pageParam = new CursorPageParameter<>("cursor", 1000, Direction.NEXT);

        assertEquals(1000, pageParam.getLimit());
        assertEquals(1001, pageParam.getQueryLimit());
    }

    @Test
    void shouldHandleMaxLimitExceeded() {
        CursorPageParameter<String> pageParam = new CursorPageParameter<>("cursor", 1001, Direction.NEXT);

        assertEquals(1000, pageParam.getLimit());
        assertEquals(1001, pageParam.getQueryLimit());
    }

    @Test
    void shouldHandleConstructorParameterValidation() {
        CursorPageParameter<String> pageParam = new CursorPageParameter<>("cursor", 30, Direction.PREV);

        assertEquals(30, pageParam.getLimit());
        assertEquals("cursor", pageParam.getCursor());
        assertEquals(Direction.PREV, pageParam.getDirection());
        assertFalse(pageParam.isNext());
    }

    @Test
    void shouldHandleIntegerCursor() {
        CursorPageParameter<Integer> pageParam = new CursorPageParameter<>(42, 5, Direction.NEXT);

        assertEquals(Integer.valueOf(42), pageParam.getCursor());
        assertEquals(5, pageParam.getLimit());
        assertTrue(pageParam.isNext());
    }

    @Test
    void shouldHandleStringCursorWithSpecialCharacters() {
        String specialCursor = "cursor-with-special-chars!@#$%^&*()";
        CursorPageParameter<String> pageParam = new CursorPageParameter<>(specialCursor, 10, Direction.PREV);

        assertEquals(specialCursor, pageParam.getCursor());
        assertEquals(10, pageParam.getLimit());
        assertFalse(pageParam.isNext());
    }
}
