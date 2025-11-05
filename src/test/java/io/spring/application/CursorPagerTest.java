package io.spring.application;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class CursorPagerTest {

    private static class TestNode implements Node {
        private final String id;
        private final PageCursor cursor;

        public TestNode(String id, PageCursor cursor) {
            this.id = id;
            this.cursor = cursor;
        }

        @Override
        public PageCursor getCursor() {
            return cursor;
        }

        public String getId() {
            return id;
        }
    }

    private static class TestPageCursor extends PageCursor<String> {
        public TestPageCursor(String value) {
            super(value);
        }
    }

    @Test
    void shouldCreateCursorPagerWithNextDirectionAndHasExtra() {
        TestPageCursor cursor1 = new TestPageCursor("cursor1");
        TestPageCursor cursor2 = new TestPageCursor("cursor2");
        List<TestNode> data = Arrays.asList(
            new TestNode("1", cursor1),
            new TestNode("2", cursor2)
        );

        CursorPager<TestNode> pager = new CursorPager<>(data, CursorPager.Direction.NEXT, true);

        assertEquals(data, pager.getData());
        assertTrue(pager.hasNext());
        assertFalse(pager.hasPrevious());
        assertTrue(pager.isNext());
        assertFalse(pager.isPrevious());
    }

    @Test
    void shouldCreateCursorPagerWithNextDirectionAndNoExtra() {
        TestPageCursor cursor1 = new TestPageCursor("cursor1");
        List<TestNode> data = Arrays.asList(new TestNode("1", cursor1));

        CursorPager<TestNode> pager = new CursorPager<>(data, CursorPager.Direction.NEXT, false);

        assertEquals(data, pager.getData());
        assertFalse(pager.hasNext());
        assertFalse(pager.hasPrevious());
        assertFalse(pager.isNext());
        assertFalse(pager.isPrevious());
    }

    @Test
    void shouldCreateCursorPagerWithPrevDirectionAndHasExtra() {
        TestPageCursor cursor1 = new TestPageCursor("cursor1");
        TestPageCursor cursor2 = new TestPageCursor("cursor2");
        List<TestNode> data = Arrays.asList(
            new TestNode("1", cursor1),
            new TestNode("2", cursor2)
        );

        CursorPager<TestNode> pager = new CursorPager<>(data, CursorPager.Direction.PREV, true);

        assertEquals(data, pager.getData());
        assertFalse(pager.hasNext());
        assertTrue(pager.hasPrevious());
        assertFalse(pager.isNext());
        assertTrue(pager.isPrevious());
    }

    @Test
    void shouldCreateCursorPagerWithPrevDirectionAndNoExtra() {
        TestPageCursor cursor1 = new TestPageCursor("cursor1");
        List<TestNode> data = Arrays.asList(new TestNode("1", cursor1));

        CursorPager<TestNode> pager = new CursorPager<>(data, CursorPager.Direction.PREV, false);

        assertEquals(data, pager.getData());
        assertFalse(pager.hasNext());
        assertFalse(pager.hasPrevious());
        assertFalse(pager.isNext());
        assertFalse(pager.isPrevious());
    }

    @Test
    void shouldReturnStartCursorForNonEmptyData() {
        TestPageCursor cursor1 = new TestPageCursor("start");
        TestPageCursor cursor2 = new TestPageCursor("end");
        List<TestNode> data = Arrays.asList(
            new TestNode("1", cursor1),
            new TestNode("2", cursor2)
        );

        CursorPager<TestNode> pager = new CursorPager<>(data, CursorPager.Direction.NEXT, false);

        assertEquals(cursor1, pager.getStartCursor());
    }

    @Test
    void shouldReturnEndCursorForNonEmptyData() {
        TestPageCursor cursor1 = new TestPageCursor("start");
        TestPageCursor cursor2 = new TestPageCursor("end");
        List<TestNode> data = Arrays.asList(
            new TestNode("1", cursor1),
            new TestNode("2", cursor2)
        );

        CursorPager<TestNode> pager = new CursorPager<>(data, CursorPager.Direction.NEXT, false);

        assertEquals(cursor2, pager.getEndCursor());
    }

    @Test
    void shouldReturnNullStartCursorForEmptyData() {
        List<TestNode> data = Collections.emptyList();

        CursorPager<TestNode> pager = new CursorPager<>(data, CursorPager.Direction.NEXT, false);

        assertNull(pager.getStartCursor());
    }

    @Test
    void shouldReturnNullEndCursorForEmptyData() {
        List<TestNode> data = Collections.emptyList();

        CursorPager<TestNode> pager = new CursorPager<>(data, CursorPager.Direction.NEXT, false);

        assertNull(pager.getEndCursor());
    }

    @Test
    void shouldReturnSameCursorForSingleItemData() {
        TestPageCursor cursor = new TestPageCursor("single");
        List<TestNode> data = Arrays.asList(new TestNode("1", cursor));

        CursorPager<TestNode> pager = new CursorPager<>(data, CursorPager.Direction.NEXT, false);

        assertEquals(cursor, pager.getStartCursor());
        assertEquals(cursor, pager.getEndCursor());
    }

    @Test
    void shouldHandleEmptyDataWithNextDirection() {
        List<TestNode> data = Collections.emptyList();

        CursorPager<TestNode> pager = new CursorPager<>(data, CursorPager.Direction.NEXT, true);

        assertEquals(data, pager.getData());
        assertTrue(pager.hasNext());
        assertFalse(pager.hasPrevious());
        assertTrue(pager.isNext());
        assertFalse(pager.isPrevious());
        assertNull(pager.getStartCursor());
        assertNull(pager.getEndCursor());
    }

    @Test
    void shouldHandleEmptyDataWithPrevDirection() {
        List<TestNode> data = Collections.emptyList();

        CursorPager<TestNode> pager = new CursorPager<>(data, CursorPager.Direction.PREV, true);

        assertEquals(data, pager.getData());
        assertFalse(pager.hasNext());
        assertTrue(pager.hasPrevious());
        assertFalse(pager.isNext());
        assertTrue(pager.isPrevious());
        assertNull(pager.getStartCursor());
        assertNull(pager.getEndCursor());
    }

    @Test
    void shouldHandleLargeDataSet() {
        List<TestNode> data = Arrays.asList(
            new TestNode("1", new TestPageCursor("cursor1")),
            new TestNode("2", new TestPageCursor("cursor2")),
            new TestNode("3", new TestPageCursor("cursor3")),
            new TestNode("4", new TestPageCursor("cursor4")),
            new TestNode("5", new TestPageCursor("cursor5"))
        );

        CursorPager<TestNode> pager = new CursorPager<>(data, CursorPager.Direction.NEXT, true);

        assertEquals(data, pager.getData());
        assertTrue(pager.hasNext());
        assertFalse(pager.hasPrevious());
        assertEquals("cursor1", pager.getStartCursor().toString());
        assertEquals("cursor5", pager.getEndCursor().toString());
    }

    @Test
    void shouldHandleNullCursorsInNodes() {
        List<TestNode> data = Arrays.asList(
            new TestNode("1", null),
            new TestNode("2", new TestPageCursor("cursor2"))
        );

        CursorPager<TestNode> pager = new CursorPager<>(data, CursorPager.Direction.NEXT, false);

        assertEquals(data, pager.getData());
        assertNull(pager.getStartCursor());
        assertEquals("cursor2", pager.getEndCursor().toString());
    }

    @Test
    void shouldTestDirectionEnumValues() {
        assertEquals("PREV", CursorPager.Direction.PREV.toString());
        assertEquals("NEXT", CursorPager.Direction.NEXT.toString());
        
        CursorPager.Direction[] values = CursorPager.Direction.values();
        assertEquals(2, values.length);
        assertEquals(CursorPager.Direction.PREV, values[0]);
        assertEquals(CursorPager.Direction.NEXT, values[1]);
    }

    @Test
    void shouldTestDirectionEnumValueOf() {
        assertEquals(CursorPager.Direction.PREV, CursorPager.Direction.valueOf("PREV"));
        assertEquals(CursorPager.Direction.NEXT, CursorPager.Direction.valueOf("NEXT"));
    }

    @Test
    void shouldThrowExceptionForInvalidDirectionValueOf() {
        assertThrows(IllegalArgumentException.class, () -> {
            CursorPager.Direction.valueOf("INVALID");
        });
    }

    @Test
    void shouldTestGettersConsistency() {
        TestPageCursor cursor = new TestPageCursor("test");
        List<TestNode> data = Arrays.asList(new TestNode("1", cursor));

        CursorPager<TestNode> pager = new CursorPager<>(data, CursorPager.Direction.NEXT, true);

        assertEquals(pager.hasNext(), pager.isNext());
        assertEquals(pager.hasPrevious(), pager.isPrevious());
    }

    @Test
    void shouldTestBooleanGettersWithDifferentScenarios() {
        List<TestNode> data = Arrays.asList(new TestNode("1", new TestPageCursor("test")));

        CursorPager<TestNode> nextWithExtra = new CursorPager<>(data, CursorPager.Direction.NEXT, true);
        assertTrue(nextWithExtra.isNext());
        assertFalse(nextWithExtra.isPrevious());

        CursorPager<TestNode> nextWithoutExtra = new CursorPager<>(data, CursorPager.Direction.NEXT, false);
        assertFalse(nextWithoutExtra.isNext());
        assertFalse(nextWithoutExtra.isPrevious());

        CursorPager<TestNode> prevWithExtra = new CursorPager<>(data, CursorPager.Direction.PREV, true);
        assertFalse(prevWithExtra.isNext());
        assertTrue(prevWithExtra.isPrevious());

        CursorPager<TestNode> prevWithoutExtra = new CursorPager<>(data, CursorPager.Direction.PREV, false);
        assertFalse(prevWithoutExtra.isNext());
        assertFalse(prevWithoutExtra.isPrevious());
    }
}
