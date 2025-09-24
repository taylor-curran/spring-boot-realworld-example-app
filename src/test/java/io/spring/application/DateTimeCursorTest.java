package io.spring.application;

import static org.junit.jupiter.api.Assertions.*;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.Test;

class DateTimeCursorTest {

    @Test
    void shouldCreateDateTimeCursorWithDateTime() {
        DateTime dateTime = new DateTime(2023, 1, 15, 10, 30, 0, DateTimeZone.UTC);
        DateTimeCursor cursor = new DateTimeCursor(dateTime);
        
        assertEquals(dateTime, cursor.getData());
    }

    @Test
    void shouldConvertToStringWithMillis() {
        DateTime dateTime = new DateTime(2023, 1, 15, 10, 30, 0, DateTimeZone.UTC);
        DateTimeCursor cursor = new DateTimeCursor(dateTime);
        
        String expected = String.valueOf(dateTime.getMillis());
        assertEquals(expected, cursor.toString());
    }

    @Test
    void shouldParseValidCursorString() {
        DateTime originalDateTime = new DateTime(2023, 1, 15, 10, 30, 0, DateTimeZone.UTC);
        String cursorString = String.valueOf(originalDateTime.getMillis());
        
        DateTime parsedDateTime = DateTimeCursor.parse(cursorString);
        
        assertNotNull(parsedDateTime);
        assertEquals(originalDateTime.getMillis(), parsedDateTime.getMillis());
        assertEquals(DateTimeZone.UTC, parsedDateTime.getZone());
    }

    @Test
    void shouldReturnNullForNullCursorString() {
        DateTime result = DateTimeCursor.parse(null);
        
        assertNull(result);
    }

    @Test
    void shouldParseZeroMillis() {
        DateTime result = DateTimeCursor.parse("0");
        
        assertNotNull(result);
        assertEquals(0, result.getMillis());
        assertEquals(DateTimeZone.UTC, result.getZone());
    }

    @Test
    void shouldParseNegativeMillis() {
        DateTime result = DateTimeCursor.parse("-1000");
        
        assertNotNull(result);
        assertEquals(-1000, result.getMillis());
        assertEquals(DateTimeZone.UTC, result.getZone());
    }

    @Test
    void shouldParseLargeMillisValue() {
        long largeMillis = System.currentTimeMillis();
        DateTime result = DateTimeCursor.parse(String.valueOf(largeMillis));
        
        assertNotNull(result);
        assertEquals(largeMillis, result.getMillis());
        assertEquals(DateTimeZone.UTC, result.getZone());
    }

    @Test
    void shouldThrowExceptionForInvalidCursorString() {
        assertThrows(NumberFormatException.class, () -> {
            DateTimeCursor.parse("invalid-number");
        });
    }

    @Test
    void shouldThrowExceptionForEmptyString() {
        assertThrows(NumberFormatException.class, () -> {
            DateTimeCursor.parse("");
        });
    }

    @Test
    void shouldThrowExceptionForNonNumericString() {
        assertThrows(NumberFormatException.class, () -> {
            DateTimeCursor.parse("abc123");
        });
    }

    @Test
    void shouldHandleMaxLongValue() {
        String maxLongString = String.valueOf(Long.MAX_VALUE);
        DateTime result = DateTimeCursor.parse(maxLongString);
        
        assertNotNull(result);
        assertEquals(Long.MAX_VALUE, result.getMillis());
    }

    @Test
    void shouldHandleMinLongValue() {
        String minLongString = String.valueOf(Long.MIN_VALUE);
        DateTime result = DateTimeCursor.parse(minLongString);
        
        assertNotNull(result);
        assertEquals(Long.MIN_VALUE, result.getMillis());
    }

    @Test
    void shouldCreateCursorWithCurrentTime() {
        DateTime now = new DateTime();
        DateTimeCursor cursor = new DateTimeCursor(now);
        
        assertEquals(now, cursor.getData());
        assertEquals(String.valueOf(now.getMillis()), cursor.toString());
    }

    @Test
    void shouldCreateCursorWithSpecificTimezone() {
        DateTime dateTime = new DateTime(2023, 6, 15, 14, 30, 0, DateTimeZone.forID("America/New_York"));
        DateTimeCursor cursor = new DateTimeCursor(dateTime);
        
        assertEquals(dateTime, cursor.getData());
        assertEquals(String.valueOf(dateTime.getMillis()), cursor.toString());
    }

    @Test
    void shouldParseAndMaintainUTCTimezone() {
        DateTime originalDateTime = new DateTime(2023, 1, 15, 10, 30, 0, DateTimeZone.forID("Europe/London"));
        String cursorString = String.valueOf(originalDateTime.getMillis());
        
        DateTime parsedDateTime = DateTimeCursor.parse(cursorString);
        
        assertNotNull(parsedDateTime);
        assertEquals(originalDateTime.getMillis(), parsedDateTime.getMillis());
        assertEquals(DateTimeZone.UTC, parsedDateTime.getZone());
        assertNotEquals(originalDateTime.getZone(), parsedDateTime.getZone());
    }

    @Test
    void shouldHandleRoundTripConversion() {
        DateTime originalDateTime = new DateTime(2023, 3, 20, 8, 45, 30, DateTimeZone.UTC);
        DateTimeCursor cursor = new DateTimeCursor(originalDateTime);
        String cursorString = cursor.toString();
        DateTime parsedDateTime = DateTimeCursor.parse(cursorString);
        
        assertEquals(originalDateTime.getMillis(), parsedDateTime.getMillis());
        assertEquals(DateTimeZone.UTC, parsedDateTime.getZone());
    }

    @Test
    void shouldInheritFromPageCursor() {
        DateTime dateTime = new DateTime();
        DateTimeCursor cursor = new DateTimeCursor(dateTime);
        
        assertTrue(cursor instanceof PageCursor);
        assertEquals(dateTime, cursor.getData());
    }

    @Test
    void shouldHandleMillisecondPrecision() {
        DateTime dateTime = new DateTime(2023, 1, 1, 0, 0, 0, 123, DateTimeZone.UTC);
        DateTimeCursor cursor = new DateTimeCursor(dateTime);
        
        String cursorString = cursor.toString();
        DateTime parsedDateTime = DateTimeCursor.parse(cursorString);
        
        assertEquals(dateTime.getMillis(), parsedDateTime.getMillis());
        assertEquals(123, dateTime.getMillisOfSecond());
        assertEquals(123, parsedDateTime.getMillisOfSecond());
    }

    @Test
    void shouldHandleLeapYear() {
        DateTime leapYearDate = new DateTime(2020, 2, 29, 12, 0, 0, DateTimeZone.UTC);
        DateTimeCursor cursor = new DateTimeCursor(leapYearDate);
        
        String cursorString = cursor.toString();
        DateTime parsedDateTime = DateTimeCursor.parse(cursorString);
        
        assertEquals(leapYearDate.getMillis(), parsedDateTime.getMillis());
        assertEquals(29, parsedDateTime.getDayOfMonth());
        assertEquals(2, parsedDateTime.getMonthOfYear());
        assertEquals(2020, parsedDateTime.getYear());
    }
}
