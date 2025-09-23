package io.spring.application;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import io.spring.application.CursorPager.Direction;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

public class CursorPageParameterTest {

  @Test
  public void should_create_with_default_values() {
    CursorPageParameter<String> parameter = new CursorPageParameter<>();

    assertThat(parameter.getLimit(), is(20));
    assertThat(parameter.getCursor(), is((String) null));
    assertThat(parameter.getDirection(), is((Direction) null));
  }

  @Test
  public void should_create_with_constructor_parameters() {
    DateTime cursor = new DateTime();
    CursorPageParameter<DateTime> parameter = new CursorPageParameter<>(cursor, 10, Direction.NEXT);

    assertThat(parameter.getCursor(), is(cursor));
    assertThat(parameter.getLimit(), is(10));
    assertThat(parameter.getDirection(), is(Direction.NEXT));
  }

  @Test
  public void should_limit_maximum_limit_to_1000() {
    CursorPageParameter<String> parameter = new CursorPageParameter<>("cursor", 2000, Direction.NEXT);

    assertThat(parameter.getLimit(), is(1000));
  }

  @Test
  public void should_ignore_zero_or_negative_limit() {
    CursorPageParameter<String> parameter1 = new CursorPageParameter<>("cursor", 0, Direction.NEXT);
    CursorPageParameter<String> parameter2 = new CursorPageParameter<>("cursor", -5, Direction.NEXT);

    assertThat(parameter1.getLimit(), is(20));
    assertThat(parameter2.getLimit(), is(20));
  }

  @Test
  public void should_accept_valid_limit() {
    CursorPageParameter<String> parameter = new CursorPageParameter<>("cursor", 50, Direction.NEXT);

    assertThat(parameter.getLimit(), is(50));
  }

  @Test
  public void should_return_true_for_next_direction() {
    CursorPageParameter<String> parameter = new CursorPageParameter<>("cursor", 10, Direction.NEXT);

    assertThat(parameter.isNext(), is(true));
  }

  @Test
  public void should_return_false_for_prev_direction() {
    CursorPageParameter<String> parameter = new CursorPageParameter<>("cursor", 10, Direction.PREV);

    assertThat(parameter.isNext(), is(false));
  }

  @Test
  public void should_return_false_for_null_direction() {
    CursorPageParameter<String> parameter = new CursorPageParameter<>();

    assertThat(parameter.isNext(), is(false));
  }

  @Test
  public void should_return_query_limit_plus_one() {
    CursorPageParameter<String> parameter = new CursorPageParameter<>("cursor", 25, Direction.NEXT);

    assertThat(parameter.getQueryLimit(), is(26));
  }

  @Test
  public void should_return_query_limit_plus_one_for_default_limit() {
    CursorPageParameter<String> parameter = new CursorPageParameter<>();

    assertThat(parameter.getQueryLimit(), is(21));
  }

  @Test
  public void should_handle_boundary_limit_values() {
    CursorPageParameter<String> parameter1 = new CursorPageParameter<>("cursor", 1, Direction.NEXT);
    CursorPageParameter<String> parameter2 = new CursorPageParameter<>("cursor", 999, Direction.NEXT);
    CursorPageParameter<String> parameter3 = new CursorPageParameter<>("cursor", 1000, Direction.NEXT);

    assertThat(parameter1.getLimit(), is(1));
    assertThat(parameter1.getQueryLimit(), is(2));
    assertThat(parameter2.getLimit(), is(999));
    assertThat(parameter2.getQueryLimit(), is(1000));
    assertThat(parameter3.getLimit(), is(1000));
    assertThat(parameter3.getQueryLimit(), is(1001));
  }

  @Test
  public void should_handle_different_cursor_types() {
    CursorPageParameter<String> stringParameter = new CursorPageParameter<>("string-cursor", 10, Direction.NEXT);
    CursorPageParameter<Integer> intParameter = new CursorPageParameter<>(42, 15, Direction.PREV);
    CursorPageParameter<DateTime> dateParameter = new CursorPageParameter<>(new DateTime(), 20, Direction.NEXT);

    assertThat(stringParameter.getCursor(), is("string-cursor"));
    assertThat(intParameter.getCursor(), is(42));
    assertThat(dateParameter.getCursor(), is(dateParameter.getCursor()));
  }

  @Test
  public void should_test_equals_method() {
    CursorPageParameter<String> parameter1 = new CursorPageParameter<>("cursor", 10, Direction.NEXT);
    CursorPageParameter<String> parameter2 = new CursorPageParameter<>("cursor", 10, Direction.NEXT);
    CursorPageParameter<String> parameter3 = new CursorPageParameter<>("different", 10, Direction.NEXT);
    CursorPageParameter<String> parameter4 = new CursorPageParameter<>("cursor", 20, Direction.NEXT);
    CursorPageParameter<String> parameter5 = new CursorPageParameter<>("cursor", 10, Direction.PREV);

    assertThat(parameter1.equals(parameter2), is(true));
    assertThat(parameter1.equals(parameter3), is(false));
    assertThat(parameter1.equals(parameter4), is(false));
    assertThat(parameter1.equals(parameter5), is(false));
    assertThat(parameter1.equals(null), is(false));
    assertThat(parameter1.equals("not a parameter"), is(false));
    assertThat(parameter1.equals(parameter1), is(true));
  }

  @Test
  public void should_test_equals_with_null_values() {
    CursorPageParameter<String> parameter1 = new CursorPageParameter<>(null, 10, null);
    CursorPageParameter<String> parameter2 = new CursorPageParameter<>(null, 10, null);
    CursorPageParameter<String> parameter3 = new CursorPageParameter<>("cursor", 10, null);
    CursorPageParameter<String> parameter4 = new CursorPageParameter<>(null, 10, Direction.NEXT);

    assertThat(parameter1.equals(parameter2), is(true));
    assertThat(parameter1.equals(parameter3), is(false));
    assertThat(parameter1.equals(parameter4), is(false));
  }

  @Test
  public void should_test_hashcode_method() {
    CursorPageParameter<String> parameter1 = new CursorPageParameter<>("cursor", 10, Direction.NEXT);
    CursorPageParameter<String> parameter2 = new CursorPageParameter<>("cursor", 10, Direction.NEXT);
    CursorPageParameter<String> parameter3 = new CursorPageParameter<>("different", 10, Direction.NEXT);

    assertThat(parameter1.hashCode(), equalTo(parameter2.hashCode()));
    assertThat(parameter1.hashCode(), not(equalTo(parameter3.hashCode())));
  }

  @Test
  public void should_test_hashcode_with_null_values() {
    CursorPageParameter<String> parameter1 = new CursorPageParameter<>(null, 10, null);
    CursorPageParameter<String> parameter2 = new CursorPageParameter<>(null, 10, null);

    assertThat(parameter1.hashCode(), equalTo(parameter2.hashCode()));
  }

  @Test
  public void should_test_toString_method() {
    CursorPageParameter<String> parameter = new CursorPageParameter<>("cursor", 10, Direction.NEXT);
    String toString = parameter.toString();

    assertThat(toString, notNullValue());
    assertThat(toString.contains("CursorPageParameter"), is(true));
    assertThat(toString.contains("cursor"), is(true));
    assertThat(toString.contains("10"), is(true));
    assertThat(toString.contains("NEXT"), is(true));
  }

  @Test
  public void should_test_toString_with_null_values() {
    CursorPageParameter<String> parameter = new CursorPageParameter<>(null, 20, null);
    String toString = parameter.toString();

    assertThat(toString, notNullValue());
    assertThat(toString.contains("CursorPageParameter"), is(true));
    assertThat(toString.contains("null"), is(true));
    assertThat(toString.contains("20"), is(true));
  }

  @Test
  public void should_test_canEqual_method() {
    CursorPageParameter<String> parameter = new CursorPageParameter<>("cursor", 10, Direction.NEXT);
    CursorPageParameter<String> otherParameter = new CursorPageParameter<>("other", 20, Direction.PREV);

    assertThat(parameter.canEqual(otherParameter), is(true));
    assertThat(parameter.canEqual(parameter), is(true));
    assertThat(parameter.canEqual("not a parameter"), is(false));
    assertThat(parameter.canEqual(null), is(false));
  }
}
