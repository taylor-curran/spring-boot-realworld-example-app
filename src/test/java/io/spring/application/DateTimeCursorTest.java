package io.spring.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

public class DateTimeCursorTest {

  @Test
  public void should_parse_valid_timestamp_string() {
    String timestampStr = "1640995200000";
    long expectedTimestamp = 1640995200000L;

    DateTime parsedDateTime = DateTimeCursor.parse(timestampStr);

    assertThat(parsedDateTime).isNotNull();
    assertThat(parsedDateTime.getMillis()).isEqualTo(expectedTimestamp);
  }

  @Test
  public void should_parse_zero_timestamp() {
    String timestampStr = "0";
    long expectedTimestamp = 0L;

    DateTime parsedDateTime = DateTimeCursor.parse(timestampStr);

    assertThat(parsedDateTime).isNotNull();
    assertThat(parsedDateTime.getMillis()).isEqualTo(expectedTimestamp);
  }

  @Test
  public void should_parse_negative_timestamp() {
    String timestampStr = "-1000";
    long expectedTimestamp = -1000L;

    DateTime parsedDateTime = DateTimeCursor.parse(timestampStr);

    assertThat(parsedDateTime).isNotNull();
    assertThat(parsedDateTime.getMillis()).isEqualTo(expectedTimestamp);
  }

  @Test
  public void should_parse_large_timestamp() {
    String timestampStr = "9223372036854775807";
    long expectedTimestamp = Long.MAX_VALUE;

    DateTime parsedDateTime = DateTimeCursor.parse(timestampStr);

    assertThat(parsedDateTime).isNotNull();
    assertThat(parsedDateTime.getMillis()).isEqualTo(expectedTimestamp);
  }

  @Test
  public void should_throw_exception_for_invalid_timestamp_string() {
    String invalidTimestamp = "invalid-timestamp";

    assertThatThrownBy(() -> DateTimeCursor.parse(invalidTimestamp))
        .isInstanceOf(NumberFormatException.class);
  }

  @Test
  public void should_return_null_for_null_timestamp() {
    String nullTimestamp = null;

    DateTime result = DateTimeCursor.parse(nullTimestamp);

    assertThat(result).isNull();
  }

  @Test
  public void should_throw_exception_for_empty_timestamp() {
    String emptyTimestamp = "";

    assertThatThrownBy(() -> DateTimeCursor.parse(emptyTimestamp))
        .isInstanceOf(NumberFormatException.class);
  }

  @Test
  public void should_throw_exception_for_whitespace_timestamp() {
    String whitespaceTimestamp = "   ";

    assertThatThrownBy(() -> DateTimeCursor.parse(whitespaceTimestamp))
        .isInstanceOf(NumberFormatException.class);
  }

  @Test
  public void should_throw_exception_for_decimal_timestamp() {
    String decimalTimestamp = "1640995200.123";

    assertThatThrownBy(() -> DateTimeCursor.parse(decimalTimestamp))
        .isInstanceOf(NumberFormatException.class);
  }

  @Test
  public void should_create_cursor_from_datetime() {
    DateTime dateTime = new DateTime(2022, 1, 1, 0, 0, 0);
    long expectedTimestamp = dateTime.getMillis();

    DateTimeCursor cursor = new DateTimeCursor(dateTime);

    assertThat(cursor.getData()).isEqualTo(dateTime);
    assertThat(cursor.getData().getMillis()).isEqualTo(expectedTimestamp);
  }

  @Test
  public void should_test_equals_and_hashcode() {
    DateTime dateTime1 = new DateTime(1640995200000L);
    DateTime dateTime2 = new DateTime(1640995200000L);
    DateTime dateTime3 = new DateTime(1640995300000L);

    DateTimeCursor cursor1 = new DateTimeCursor(dateTime1);
    DateTimeCursor cursor2 = new DateTimeCursor(dateTime2);
    DateTimeCursor cursor3 = new DateTimeCursor(dateTime3);

    // DateTimeCursor inherits equals/hashCode from PageCursor, test the data equality
    assertThat(cursor1.getData()).isEqualTo(cursor2.getData());
    assertThat(cursor1.toString()).isEqualTo(cursor2.toString());

    assertThat(cursor1.getData()).isNotEqualTo(cursor3.getData());
    assertThat(cursor1.toString()).isNotEqualTo(cursor3.toString());
  }

  @Test
  public void should_test_toString() {
    DateTime dateTime = new DateTime(1640995200000L);
    DateTimeCursor cursor = new DateTimeCursor(dateTime);

    String toString = cursor.toString();

    assertThat(toString).isNotNull();
    assertThat(toString).contains("1640995200000");
  }

  @Test
  public void should_handle_current_time_parsing() {
    DateTime now = DateTime.now();
    String nowStr = String.valueOf(now.getMillis());

    DateTime parsedDateTime = DateTimeCursor.parse(nowStr);

    assertThat(parsedDateTime.getMillis()).isEqualTo(now.getMillis());
  }
}
