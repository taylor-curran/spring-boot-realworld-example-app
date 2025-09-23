package io.spring;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class UtilTest {

  @Test
  public void should_create_util_instance() {
    Util util = new Util();
    assertThat(util, is(org.hamcrest.CoreMatchers.notNullValue()));
  }

  @Test
  public void should_return_true_for_null_string() {
    boolean result = Util.isEmpty(null);
    assertThat(result, is(true));
  }

  @Test
  public void should_return_true_for_empty_string() {
    boolean result = Util.isEmpty("");
    assertThat(result, is(true));
  }

  @Test
  public void should_return_false_for_non_empty_string() {
    boolean result = Util.isEmpty("test");
    assertThat(result, is(false));
  }

  @Test
  public void should_return_false_for_whitespace_string() {
    boolean result = Util.isEmpty(" ");
    assertThat(result, is(false));
  }

  @Test
  public void should_return_false_for_string_with_content() {
    boolean result = Util.isEmpty("hello world");
    assertThat(result, is(false));
  }

  @Test
  public void should_handle_string_with_only_spaces() {
    boolean result = Util.isEmpty("   ");
    assertThat(result, is(false));
  }

  @Test
  public void should_handle_string_with_special_characters() {
    boolean result = Util.isEmpty("@#$%");
    assertThat(result, is(false));
  }

  @Test
  public void should_handle_string_with_numbers() {
    boolean result = Util.isEmpty("123");
    assertThat(result, is(false));
  }

  @Test
  public void should_handle_single_character_string() {
    boolean result = Util.isEmpty("a");
    assertThat(result, is(false));
  }
}
