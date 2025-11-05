package io.spring.application;

import static org.junit.jupiter.api.Assertions.*;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

class PageCursorTest {

    private static class TestPageCursor extends PageCursor<String> {
        public TestPageCursor(String data) {
            super(data);
        }
    }

    private static class DateTimePageCursor extends PageCursor<DateTime> {
        public DateTimePageCursor(DateTime data) {
            super(data);
        }
    }

    private static class IntegerPageCursor extends PageCursor<Integer> {
        public IntegerPageCursor(Integer data) {
            super(data);
        }
    }

    @Test
    void shouldCreatePageCursorWithStringData() {
        String testData = "test-cursor-data";
        TestPageCursor cursor = new TestPageCursor(testData);

        assertEquals(testData, cursor.getData());
        assertEquals(testData, cursor.toString());
    }

    @Test
    void shouldCreatePageCursorWithNullData() {
        TestPageCursor cursor = new TestPageCursor(null);

        assertNull(cursor.getData());
        assertThrows(NullPointerException.class, cursor::toString);
    }

    @Test
    void shouldCreatePageCursorWithEmptyString() {
        String emptyData = "";
        TestPageCursor cursor = new TestPageCursor(emptyData);

        assertEquals(emptyData, cursor.getData());
        assertEquals(emptyData, cursor.toString());
    }

    @Test
    void shouldCreatePageCursorWithDateTimeData() {
        DateTime dateTime = new DateTime();
        DateTimePageCursor cursor = new DateTimePageCursor(dateTime);

        assertEquals(dateTime, cursor.getData());
        assertEquals(dateTime.toString(), cursor.toString());
    }

    @Test
    void shouldCreatePageCursorWithIntegerData() {
        Integer intData = 12345;
        IntegerPageCursor cursor = new IntegerPageCursor(intData);

        assertEquals(intData, cursor.getData());
        assertEquals(intData.toString(), cursor.toString());
    }

    @Test
    void shouldCreatePageCursorWithComplexStringData() {
        String complexData = "user:123|article:456|timestamp:2023-01-01T00:00:00Z";
        TestPageCursor cursor = new TestPageCursor(complexData);

        assertEquals(complexData, cursor.getData());
        assertEquals(complexData, cursor.toString());
    }

    @Test
    void shouldCreatePageCursorWithSpecialCharacters() {
        String specialData = "cursor-with-special-chars!@#$%^&*()_+-=[]{}|;':\",./<>?";
        TestPageCursor cursor = new TestPageCursor(specialData);

        assertEquals(specialData, cursor.getData());
        assertEquals(specialData, cursor.toString());
    }

    @Test
    void shouldCreatePageCursorWithUnicodeData() {
        String unicodeData = "测试游标数据-🌍🚀✨";
        TestPageCursor cursor = new TestPageCursor(unicodeData);

        assertEquals(unicodeData, cursor.getData());
        assertEquals(unicodeData, cursor.toString());
    }

    @Test
    void shouldCreatePageCursorWithMultilineData() {
        String multilineData = "Line 1\nLine 2\nLine 3";
        TestPageCursor cursor = new TestPageCursor(multilineData);

        assertEquals(multilineData, cursor.getData());
        assertEquals(multilineData, cursor.toString());
    }

    @Test
    void shouldCreatePageCursorWithWhitespaceData() {
        String whitespaceData = "   data with spaces   ";
        TestPageCursor cursor = new TestPageCursor(whitespaceData);

        assertEquals(whitespaceData, cursor.getData());
        assertEquals(whitespaceData, cursor.toString());
    }

    @Test
    void shouldCreatePageCursorWithLongData() {
        String longData = "very-long-cursor-data-".repeat(100);
        TestPageCursor cursor = new TestPageCursor(longData);

        assertEquals(longData, cursor.getData());
        assertEquals(longData, cursor.toString());
    }

    @Test
    void shouldMaintainDataIntegrity() {
        String originalData = "original-cursor-data";
        TestPageCursor cursor = new TestPageCursor(originalData);

        assertEquals(originalData, cursor.getData());
        assertEquals(originalData, cursor.toString());

        String retrievedData = cursor.getData();
        assertEquals(originalData, retrievedData);
        assertSame(originalData, retrievedData);
    }

    @Test
    void shouldHandleZeroInteger() {
        Integer zeroData = 0;
        IntegerPageCursor cursor = new IntegerPageCursor(zeroData);

        assertEquals(zeroData, cursor.getData());
        assertEquals("0", cursor.toString());
    }

    @Test
    void shouldHandleNegativeInteger() {
        Integer negativeData = -12345;
        IntegerPageCursor cursor = new IntegerPageCursor(negativeData);

        assertEquals(negativeData, cursor.getData());
        assertEquals("-12345", cursor.toString());
    }

    @Test
    void shouldHandleNullDateTime() {
        DateTimePageCursor cursor = new DateTimePageCursor(null);

        assertNull(cursor.getData());
        assertThrows(NullPointerException.class, cursor::toString);
    }

    @Test
    void shouldHandleNullInteger() {
        IntegerPageCursor cursor = new IntegerPageCursor(null);

        assertNull(cursor.getData());
        assertThrows(NullPointerException.class, cursor::toString);
    }
}
