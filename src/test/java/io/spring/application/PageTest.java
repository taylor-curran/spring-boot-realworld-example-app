package io.spring.application;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PageTest {

    @Test
    void shouldCreatePageWithDefaults() {
        Page page = new Page();

        assertEquals(0, page.getOffset());
        assertEquals(20, page.getLimit());
    }

    @Test
    void shouldCreatePageWithParameters() {
        Page page = new Page(10, 50);

        assertEquals(10, page.getOffset());
        assertEquals(50, page.getLimit());
    }

    @Test
    void shouldEnforceOffsetMinimum() {
        Page page = new Page(-5, 30);

        assertEquals(0, page.getOffset());
        assertEquals(30, page.getLimit());
    }

    @Test
    void shouldSetValidOffset() {
        Page page = new Page(100, 25);

        assertEquals(100, page.getOffset());
        assertEquals(25, page.getLimit());
    }

    @Test
    void shouldEnforceLimitMaximum() {
        Page page = new Page(0, 200);

        assertEquals(0, page.getOffset());
        assertEquals(100, page.getLimit());
    }

    @Test
    void shouldEnforceLimitMinimum() {
        Page page = new Page(0, -10);

        assertEquals(0, page.getOffset());
        assertEquals(20, page.getLimit());
    }

    @Test
    void shouldEnforceLimitZero() {
        Page page = new Page(0, 0);

        assertEquals(0, page.getOffset());
        assertEquals(20, page.getLimit());
    }

    @Test
    void shouldSetValidLimit() {
        Page page = new Page(0, 75);

        assertEquals(0, page.getOffset());
        assertEquals(75, page.getLimit());
    }

    @Test
    void shouldHandleMaxLimitBoundary() {
        Page page = new Page(0, 100);

        assertEquals(0, page.getOffset());
        assertEquals(100, page.getLimit());
    }

    @Test
    void shouldHandleMaxLimitExceeded() {
        Page page = new Page(0, 101);

        assertEquals(0, page.getOffset());
        assertEquals(100, page.getLimit());
    }

    @Test
    void shouldHandleZeroOffset() {
        Page page = new Page(0, 40);

        assertEquals(0, page.getOffset());
        assertEquals(40, page.getLimit());
    }

    @Test
    void shouldImplementEqualsCorrectly() {
        Page page1 = new Page(10, 30);
        Page page2 = new Page(10, 30);
        Page page3 = new Page(20, 30);
        Page page4 = new Page(10, 40);

        assertEquals(page1, page2);
        assertNotEquals(page1, page3);
        assertNotEquals(page1, page4);
        assertNotEquals(page1, null);
        assertNotEquals(page1, "not a Page object");
    }

    @Test
    void shouldImplementHashCodeCorrectly() {
        Page page1 = new Page(10, 30);
        Page page2 = new Page(10, 30);

        assertEquals(page1.hashCode(), page2.hashCode());
    }

    @Test
    void shouldImplementToStringCorrectly() {
        Page page = new Page(25, 50);
        String toString = page.toString();

        assertTrue(toString.contains("Page"));
        assertTrue(toString.contains("offset=25"));
        assertTrue(toString.contains("limit=50"));
    }

    @Test
    void shouldHandleBoundaryValues() {
        Page page1 = new Page(1, 1);
        Page page2 = new Page(Integer.MAX_VALUE, 99);

        assertEquals(1, page1.getOffset());
        assertEquals(1, page1.getLimit());
        assertEquals(Integer.MAX_VALUE, page2.getOffset());
        assertEquals(99, page2.getLimit());
    }

    @Test
    void shouldHandleConstructorParameterValidation() {
        Page page = new Page(5, 15);

        assertEquals(5, page.getOffset());
        assertEquals(15, page.getLimit());
    }
}
