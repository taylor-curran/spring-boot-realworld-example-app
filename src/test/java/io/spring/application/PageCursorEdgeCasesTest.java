package io.spring.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

public class PageCursorEdgeCasesTest {

  @Test
  void should_store_and_retrieve_string_data() {
    TestPageCursor cursor = new TestPageCursor("test-data");
    
    assertThat(cursor.getData()).isEqualTo("test-data");
    assertThat(cursor.toString()).isEqualTo("test-data");
  }

  @Test
  void should_store_and_retrieve_datetime_data() {
    DateTime now = DateTime.now();
    TestPageCursor cursor = new TestPageCursor(now);
    
    assertThat(cursor.getData()).isEqualTo(now);
    assertThat(cursor.toString()).isEqualTo(now.toString());
  }

  @Test
  void should_store_and_retrieve_integer_data() {
    TestPageCursor cursor = new TestPageCursor(42);
    
    assertThat(cursor.getData()).isEqualTo(42);
    assertThat(cursor.toString()).isEqualTo("42");
  }

  @Test
  void should_handle_null_data() {
    TestPageCursor cursor = new TestPageCursor(null);
    
    assertThat(cursor.getData()).isNull();
  }

  @Test
  void should_handle_empty_string_data() {
    TestPageCursor cursor = new TestPageCursor("");
    
    assertThat(cursor.getData()).isEqualTo("");
    assertThat(cursor.toString()).isEqualTo("");
  }

  @Test
  void should_handle_complex_object_data() {
    ComplexData data = new ComplexData("name", 123);
    TestPageCursor cursor = new TestPageCursor(data);
    
    assertThat(cursor.getData()).isEqualTo(data);
    assertThat(cursor.toString()).isEqualTo("ComplexData{name='name', value=123}");
  }

  private static class TestPageCursor extends PageCursor<Object> {
    public TestPageCursor(Object data) {
      super(data);
    }
  }

  private static class ComplexData {
    private final String name;
    private final int value;

    public ComplexData(String name, int value) {
      this.name = name;
      this.value = value;
    }

    @Override
    public String toString() {
      return "ComplexData{name='" + name + "', value=" + value + "}";
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ComplexData that = (ComplexData) obj;
      return value == that.value && name.equals(that.name);
    }

    @Override
    public int hashCode() {
      return name.hashCode() + value;
    }
  }
}
