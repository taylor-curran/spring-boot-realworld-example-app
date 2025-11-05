package io.spring.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class PageEdgeCasesTest {

  @Test
  void should_use_default_values_with_no_args_constructor() {
    Page page = new Page();
    
    assertThat(page.getOffset()).isEqualTo(0);
    assertThat(page.getLimit()).isEqualTo(20);
  }

  @Test
  void should_set_valid_offset_and_limit() {
    Page page = new Page(10, 50);
    
    assertThat(page.getOffset()).isEqualTo(10);
    assertThat(page.getLimit()).isEqualTo(50);
  }

  @Test
  void should_ignore_negative_offset() {
    Page page = new Page(-5, 30);
    
    assertThat(page.getOffset()).isEqualTo(0);
    assertThat(page.getLimit()).isEqualTo(30);
  }

  @Test
  void should_ignore_zero_offset() {
    Page page = new Page(0, 30);
    
    assertThat(page.getOffset()).isEqualTo(0);
    assertThat(page.getLimit()).isEqualTo(30);
  }

  @Test
  void should_cap_limit_at_max_limit() {
    Page page = new Page(10, 150);
    
    assertThat(page.getOffset()).isEqualTo(10);
    assertThat(page.getLimit()).isEqualTo(100);
  }

  @Test
  void should_ignore_negative_limit() {
    Page page = new Page(10, -5);
    
    assertThat(page.getOffset()).isEqualTo(10);
    assertThat(page.getLimit()).isEqualTo(20);
  }

  @Test
  void should_ignore_zero_limit() {
    Page page = new Page(10, 0);
    
    assertThat(page.getOffset()).isEqualTo(10);
    assertThat(page.getLimit()).isEqualTo(20);
  }

  @Test
  void should_handle_limit_exactly_at_max() {
    Page page = new Page(5, 100);
    
    assertThat(page.getOffset()).isEqualTo(5);
    assertThat(page.getLimit()).isEqualTo(100);
  }

  @Test
  void should_handle_both_invalid_values() {
    Page page = new Page(-10, -20);
    
    assertThat(page.getOffset()).isEqualTo(0);
    assertThat(page.getLimit()).isEqualTo(20);
  }

  @Test
  void should_handle_both_invalid_values_with_large_limit() {
    Page page = new Page(-10, 200);
    
    assertThat(page.getOffset()).isEqualTo(0);
    assertThat(page.getLimit()).isEqualTo(100);
  }
}
