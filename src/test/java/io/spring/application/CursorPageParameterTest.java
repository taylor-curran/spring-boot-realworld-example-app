package io.spring.application;

import static org.assertj.core.api.Assertions.assertThat;

import io.spring.application.CursorPager.Direction;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

public class CursorPageParameterTest {

  @Test
  public void should_create_cursor_page_parameter_with_valid_parameters() {
    String cursor = "1640995200000";
    int limit = 20;
    Direction direction = Direction.NEXT;

    CursorPageParameter<String> parameter = new CursorPageParameter<>(cursor, limit, direction);

    assertThat(parameter.getCursor()).isEqualTo(cursor);
    assertThat(parameter.getLimit()).isEqualTo(limit);
    assertThat(parameter.getDirection()).isEqualTo(direction);
  }

  @Test
  public void should_create_cursor_page_parameter_with_null_cursor() {
    String cursor = null;
    int limit = 10;
    Direction direction = Direction.PREV;

    CursorPageParameter<String> parameter = new CursorPageParameter<>(cursor, limit, direction);

    assertThat(parameter.getCursor()).isNull();
    assertThat(parameter.getLimit()).isEqualTo(limit);
    assertThat(parameter.getDirection()).isEqualTo(direction);
  }

  @Test
  public void should_handle_zero_limit_with_default_fallback() {
    String cursor = "1640995200000";
    int limit = 0;
    Direction direction = Direction.NEXT;

    CursorPageParameter<String> parameter = new CursorPageParameter<>(cursor, limit, direction);

    assertThat(parameter.getCursor()).isEqualTo(cursor);
    assertThat(parameter.getLimit()).isEqualTo(20); // Default fallback for invalid limit
    assertThat(parameter.getDirection()).isEqualTo(direction);
  }

  @Test
  public void should_handle_negative_limit_with_default_fallback() {
    String cursor = "1640995200000";
    int limit = -5;
    Direction direction = Direction.NEXT;

    CursorPageParameter<String> parameter = new CursorPageParameter<>(cursor, limit, direction);

    assertThat(parameter.getCursor()).isEqualTo(cursor);
    assertThat(parameter.getLimit()).isEqualTo(20); // Default fallback for invalid limit
    assertThat(parameter.getDirection()).isEqualTo(direction);
  }

  @Test
  public void should_handle_large_limit_with_max_cap() {
    String cursor = "1640995200000";
    int limit = 2000; // Over MAX_LIMIT of 1000
    Direction direction = Direction.NEXT;

    CursorPageParameter<String> parameter = new CursorPageParameter<>(cursor, limit, direction);

    assertThat(parameter.getCursor()).isEqualTo(cursor);
    assertThat(parameter.getLimit()).isEqualTo(1000); // Capped at MAX_LIMIT
    assertThat(parameter.getDirection()).isEqualTo(direction);
  }

  @Test
  public void should_handle_valid_limit_within_bounds() {
    String cursor = "1640995200000";
    int limit = 50;
    Direction direction = Direction.PREV;

    CursorPageParameter<String> parameter = new CursorPageParameter<>(cursor, limit, direction);

    assertThat(parameter.getCursor()).isEqualTo(cursor);
    assertThat(parameter.getLimit()).isEqualTo(50);
    assertThat(parameter.getDirection()).isEqualTo(direction);
  }

  @Test
  public void should_test_is_next_method() {
    CursorPageParameter<String> nextParam = new CursorPageParameter<>("cursor", 10, Direction.NEXT);
    CursorPageParameter<String> prevParam = new CursorPageParameter<>("cursor", 10, Direction.PREV);

    assertThat(nextParam.isNext()).isTrue();
    assertThat(prevParam.isNext()).isFalse();
  }

  @Test
  public void should_test_get_query_limit() {
    CursorPageParameter<String> parameter = new CursorPageParameter<>("cursor", 20, Direction.NEXT);

    assertThat(parameter.getQueryLimit()).isEqualTo(21); // limit + 1
  }

  @Test
  public void should_work_with_different_cursor_types() {
    // String cursor
    CursorPageParameter<String> stringParam =
        new CursorPageParameter<>("string-cursor", 10, Direction.NEXT);
    assertThat(stringParam.getCursor()).isEqualTo("string-cursor");

    CursorPageParameter<Integer> intParam = new CursorPageParameter<>(12345, 15, Direction.PREV);
    assertThat(intParam.getCursor()).isEqualTo(12345);

    DateTime now = DateTime.now();
    CursorPageParameter<DateTime> dateParam = new CursorPageParameter<>(now, 25, Direction.NEXT);
    assertThat(dateParam.getCursor()).isEqualTo(now);
  }

  @Test
  public void should_test_no_args_constructor() {
    CursorPageParameter<String> parameter = new CursorPageParameter<>();

    assertThat(parameter.getCursor()).isNull();
    assertThat(parameter.getLimit()).isEqualTo(20); // Default limit
    assertThat(parameter.getDirection()).isNull();
  }

  @Test
  public void should_test_equals_and_hashcode() {
    CursorPageParameter<String> param1 = new CursorPageParameter<>("cursor1", 10, Direction.NEXT);
    CursorPageParameter<String> param2 = new CursorPageParameter<>("cursor1", 10, Direction.NEXT);
    CursorPageParameter<String> param3 = new CursorPageParameter<>("cursor2", 10, Direction.NEXT);
    CursorPageParameter<String> param4 = new CursorPageParameter<>("cursor1", 20, Direction.NEXT);
    CursorPageParameter<String> param5 = new CursorPageParameter<>("cursor1", 10, Direction.PREV);

    assertThat(param1).isEqualTo(param2);
    assertThat(param1.hashCode()).isEqualTo(param2.hashCode());

    assertThat(param1).isNotEqualTo(param3);
    assertThat(param1).isNotEqualTo(param4);
    assertThat(param1).isNotEqualTo(param5);
    assertThat(param1).isNotEqualTo(null);
    assertThat(param1).isNotEqualTo("string");
  }

  @Test
  public void should_test_equals_with_null_values() {
    CursorPageParameter<String> param1 = new CursorPageParameter<>(null, 10, Direction.NEXT);
    CursorPageParameter<String> param2 = new CursorPageParameter<>(null, 10, Direction.NEXT);
    CursorPageParameter<String> param3 = new CursorPageParameter<>("cursor", 10, Direction.NEXT);
    CursorPageParameter<String> param4 = new CursorPageParameter<>(null, 10, null);
    CursorPageParameter<String> param5 = new CursorPageParameter<>(null, 10, null);

    assertThat(param1).isEqualTo(param2);
    assertThat(param1).isNotEqualTo(param3);
    assertThat(param1).isNotEqualTo(param4);
    assertThat(param4).isEqualTo(param5);
  }

  @Test
  public void should_test_equals_reflexivity() {
    CursorPageParameter<String> param = new CursorPageParameter<>("cursor", 10, Direction.NEXT);

    assertThat(param).isEqualTo(param);
  }

  @Test
  public void should_test_equals_symmetry() {
    CursorPageParameter<String> param1 = new CursorPageParameter<>("cursor", 10, Direction.NEXT);
    CursorPageParameter<String> param2 = new CursorPageParameter<>("cursor", 10, Direction.NEXT);

    assertThat(param1.equals(param2)).isEqualTo(param2.equals(param1));
  }

  @Test
  public void should_test_equals_transitivity() {
    CursorPageParameter<String> param1 = new CursorPageParameter<>("cursor", 10, Direction.NEXT);
    CursorPageParameter<String> param2 = new CursorPageParameter<>("cursor", 10, Direction.NEXT);
    CursorPageParameter<String> param3 = new CursorPageParameter<>("cursor", 10, Direction.NEXT);

    assertThat(param1).isEqualTo(param2);
    assertThat(param2).isEqualTo(param3);
    assertThat(param1).isEqualTo(param3);
  }

  @Test
  public void should_test_hashcode_with_null_values() {
    CursorPageParameter<String> param1 = new CursorPageParameter<>(null, 10, Direction.NEXT);
    CursorPageParameter<String> param2 = new CursorPageParameter<>(null, 10, Direction.NEXT);
    CursorPageParameter<String> param3 = new CursorPageParameter<>(null, 10, null);
    CursorPageParameter<String> param4 = new CursorPageParameter<>(null, 10, null);

    assertThat(param1.hashCode()).isEqualTo(param2.hashCode());
    assertThat(param3.hashCode()).isEqualTo(param4.hashCode());
    assertThat(param1.hashCode()).isNotEqualTo(param3.hashCode());
  }

  @Test
  public void should_test_hashcode_consistency() {
    CursorPageParameter<String> param = new CursorPageParameter<>("cursor", 10, Direction.NEXT);

    int hash1 = param.hashCode();
    int hash2 = param.hashCode();
    assertThat(hash1).isEqualTo(hash2);
  }

  @Test
  public void should_test_equals_with_mixed_null_combinations() {
    CursorPageParameter<String> paramWithNullCursor =
        new CursorPageParameter<>(null, 10, Direction.NEXT);
    CursorPageParameter<String> paramWithCursor =
        new CursorPageParameter<>("cursor", 10, Direction.NEXT);
    CursorPageParameter<String> paramWithNullDirection =
        new CursorPageParameter<>("cursor", 10, null);
    CursorPageParameter<String> paramWithDirection =
        new CursorPageParameter<>("cursor", 10, Direction.NEXT);

    assertThat(paramWithNullCursor).isNotEqualTo(paramWithCursor);
    assertThat(paramWithCursor).isNotEqualTo(paramWithNullCursor);
    assertThat(paramWithNullDirection).isNotEqualTo(paramWithDirection);
    assertThat(paramWithDirection).isNotEqualTo(paramWithNullDirection);
  }

  @Test
  public void should_test_toString() {
    CursorPageParameter<String> parameter =
        new CursorPageParameter<>("test-cursor", 15, Direction.NEXT);

    String toString = parameter.toString();

    assertThat(toString).isNotNull();
    assertThat(toString).contains("test-cursor");
    assertThat(toString).contains("15");
    assertThat(toString).contains("NEXT");
  }
}
