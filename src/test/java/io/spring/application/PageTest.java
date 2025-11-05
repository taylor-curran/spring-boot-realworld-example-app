package io.spring.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class PageTest {

  @Test
  public void should_create_page_with_valid_parameters() {
    int offset = 10;
    int limit = 20;

    Page page = new Page(offset, limit);

    assertThat(page.getOffset()).isEqualTo(offset);
    assertThat(page.getLimit()).isEqualTo(limit);
  }

  @Test
  public void should_create_page_with_zero_offset() {
    int offset = 0;
    int limit = 15;

    Page page = new Page(offset, limit);

    assertThat(page.getOffset()).isEqualTo(0);
    assertThat(page.getLimit()).isEqualTo(limit);
  }

  @Test
  public void should_handle_negative_offset_with_default_fallback() {
    int offset = -5;
    int limit = 25;

    Page page = new Page(offset, limit);

    assertThat(page.getOffset()).isEqualTo(0); // Default fallback for negative offset
    assertThat(page.getLimit()).isEqualTo(limit);
  }

  @Test
  public void should_handle_zero_limit_with_default_fallback() {
    int offset = 10;
    int limit = 0;

    Page page = new Page(offset, limit);

    assertThat(page.getOffset()).isEqualTo(offset);
    assertThat(page.getLimit()).isEqualTo(20); // Default fallback for invalid limit
  }

  @Test
  public void should_handle_negative_limit_with_default_fallback() {
    int offset = 5;
    int limit = -10;

    Page page = new Page(offset, limit);

    assertThat(page.getOffset()).isEqualTo(offset);
    assertThat(page.getLimit()).isEqualTo(20); // Default fallback for invalid limit
  }

  @Test
  public void should_handle_large_limit_with_max_cap() {
    int offset = 20;
    int limit = 200; // Over MAX_LIMIT of 100

    Page page = new Page(offset, limit);

    assertThat(page.getOffset()).isEqualTo(offset);
    assertThat(page.getLimit()).isEqualTo(100); // Capped at MAX_LIMIT
  }

  @Test
  public void should_handle_valid_limit_within_bounds() {
    int offset = 30;
    int limit = 50;

    Page page = new Page(offset, limit);

    assertThat(page.getOffset()).isEqualTo(offset);
    assertThat(page.getLimit()).isEqualTo(limit);
  }

  @Test
  public void should_test_no_args_constructor() {
    Page page = new Page();

    assertThat(page.getOffset()).isEqualTo(0); // Default offset
    assertThat(page.getLimit()).isEqualTo(20); // Default limit
  }

  @Test
  public void should_test_boundary_values() {
    Page page1 = new Page(0, 100);
    assertThat(page1.getLimit()).isEqualTo(100);

    Page page2 = new Page(0, 101);
    assertThat(page2.getLimit()).isEqualTo(100);

    Page page3 = new Page(0, 1);
    assertThat(page3.getLimit()).isEqualTo(1);
  }

  @Test
  public void should_test_equals_and_hashcode() {
    Page page1 = new Page(10, 20);
    Page page2 = new Page(10, 20);
    Page page3 = new Page(15, 20);
    Page page4 = new Page(10, 25);

    assertThat(page1).isEqualTo(page2);
    assertThat(page1.hashCode()).isEqualTo(page2.hashCode());
    
    assertThat(page1).isNotEqualTo(page3);
    assertThat(page1).isNotEqualTo(page4);
    assertThat(page1).isNotEqualTo(null);
    assertThat(page1).isNotEqualTo("string");
  }

  @Test
  public void should_test_toString() {
    Page page = new Page(15, 30);
    
    String toString = page.toString();
    
    assertThat(toString).isNotNull();
    assertThat(toString).contains("15");
    assertThat(toString).contains("30");
  }
}
