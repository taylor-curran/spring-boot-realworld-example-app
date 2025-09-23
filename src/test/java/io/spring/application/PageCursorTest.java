package io.spring.application;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.Test;

public class PageCursorTest {

  private static class TestPageCursor extends PageCursor<String> {
    public TestPageCursor(String data) {
      super(data);
    }
  }

  @Test
  public void should_create_page_cursor_with_data() {
    DateTime testDateTime = new DateTime(2023, 1, 15, 10, 30, 0, DateTimeZone.UTC);
    DateTimeCursor cursor = new DateTimeCursor(testDateTime);
    
    assertThat(cursor.getData(), is(notNullValue()));
    assertThat(cursor.getData(), is(testDateTime));
  }

  @Test
  public void should_return_data_from_getter() {
    DateTime testDateTime = new DateTime(2023, 6, 20, 14, 45, 30, DateTimeZone.UTC);
    DateTimeCursor cursor = new DateTimeCursor(testDateTime);
    
    DateTime retrievedData = cursor.getData();
    
    assertThat(retrievedData, is(testDateTime));
    assertThat(retrievedData.getYear(), is(2023));
    assertThat(retrievedData.getMonthOfYear(), is(6));
    assertThat(retrievedData.getDayOfMonth(), is(20));
  }

  @Test
  public void should_call_toString_on_base_page_cursor() {
    String testData = "test-cursor-data";
    PageCursor<String> cursor = new TestPageCursor(testData);
    
    String result = cursor.toString();
    
    assertThat(result, is(notNullValue()));
    assertThat(result, is(testData));
  }

  @Test
  public void should_test_base_page_cursor_toString_with_different_data() {
    String data1 = "first-data";
    String data2 = "second-data";
    
    PageCursor<String> cursor1 = new TestPageCursor(data1);
    PageCursor<String> cursor2 = new TestPageCursor(data2);
    
    assertThat(cursor1.toString(), is(data1));
    assertThat(cursor2.toString(), is(data2));
    assertThat(cursor1.toString().equals(cursor2.toString()), is(false));
  }

  @Test
  public void should_test_base_page_cursor_toString_with_null_data() {
    PageCursor<String> cursor = new TestPageCursor(null);
    
    try {
      cursor.toString();
      assertThat("Expected NullPointerException", false);
    } catch (NullPointerException e) {
      assertThat(e.getMessage(), is("Cannot invoke \"Object.toString()\" because \"this.data\" is null"));
    }
  }

  @Test
  public void should_test_base_page_cursor_toString_with_empty_string() {
    PageCursor<String> cursor = new TestPageCursor("");
    
    String result = cursor.toString();
    
    assertThat(result, is(""));
  }

  @Test
  public void should_handle_different_datetime_values_in_toString() {
    DateTime earlyDateTime = new DateTime(2020, 1, 1, 0, 0, 0, DateTimeZone.UTC);
    DateTime lateDateTime = new DateTime(2025, 12, 31, 23, 59, 59, DateTimeZone.UTC);
    
    PageCursor<DateTime> earlyCursor = new DateTimeCursor(earlyDateTime);
    PageCursor<DateTime> lateCursor = new DateTimeCursor(lateDateTime);
    
    String earlyResult = earlyCursor.toString();
    String lateResult = lateCursor.toString();
    
    assertThat(earlyResult, is(String.valueOf(earlyDateTime.getMillis())));
    assertThat(lateResult, is(String.valueOf(lateDateTime.getMillis())));
    assertThat(earlyResult.equals(lateResult), is(false));
  }

  @Test
  public void should_test_page_cursor_with_current_time() {
    DateTime currentTime = DateTime.now(DateTimeZone.UTC);
    PageCursor<DateTime> cursor = new DateTimeCursor(currentTime);
    
    assertThat(cursor.getData(), is(currentTime));
    assertThat(cursor.toString(), is(String.valueOf(currentTime.getMillis())));
  }

  @Test
  public void should_test_page_cursor_inheritance() {
    DateTime testDateTime = new DateTime(2023, 8, 10, 16, 20, 10, DateTimeZone.UTC);
    DateTimeCursor dateTimeCursor = new DateTimeCursor(testDateTime);
    
    assertThat(dateTimeCursor instanceof PageCursor, is(true));
    assertThat(dateTimeCursor instanceof DateTimeCursor, is(true));
    
    PageCursor<DateTime> baseCursor = dateTimeCursor;
    assertThat(baseCursor.getData(), is(testDateTime));
    assertThat(baseCursor.toString(), is(String.valueOf(testDateTime.getMillis())));
  }

  @Test
  public void should_test_datetime_cursor_specific_toString() {
    DateTime testDateTime = new DateTime(2023, 3, 15, 12, 0, 0, DateTimeZone.UTC);
    DateTimeCursor cursor = new DateTimeCursor(testDateTime);
    
    String result = cursor.toString();
    String expectedMillis = String.valueOf(testDateTime.getMillis());
    
    assertThat(result, is(expectedMillis));
    assertThat(result, equalTo(expectedMillis));
  }

  @Test
  public void should_test_page_cursor_with_epoch_time() {
    DateTime epochTime = new DateTime(0, DateTimeZone.UTC);
    PageCursor<DateTime> cursor = new DateTimeCursor(epochTime);
    
    assertThat(cursor.getData(), is(epochTime));
    assertThat(cursor.toString(), is("0"));
  }

  @Test
  public void should_verify_page_cursor_data_consistency() {
    DateTime originalDateTime = new DateTime(2023, 7, 4, 18, 30, 45, DateTimeZone.UTC);
    PageCursor<DateTime> cursor = new DateTimeCursor(originalDateTime);
    
    DateTime retrievedData = cursor.getData();
    assertThat(retrievedData, is(originalDateTime));
    assertThat(retrievedData.equals(originalDateTime), is(true));
    assertThat(cursor.toString(), is(String.valueOf(originalDateTime.getMillis())));
  }

  @Test
  public void should_test_base_page_cursor_with_integer_data() {
    PageCursor<Integer> cursor = new PageCursor<Integer>(42) {};
    
    assertThat(cursor.getData(), is(42));
    assertThat(cursor.toString(), is("42"));
  }

  @Test
  public void should_test_base_page_cursor_with_complex_object() {
    StringBuilder sb = new StringBuilder("complex-object");
    PageCursor<StringBuilder> cursor = new PageCursor<StringBuilder>(sb) {};
    
    assertThat(cursor.getData(), is(sb));
    assertThat(cursor.toString(), is("complex-object"));
  }
}
