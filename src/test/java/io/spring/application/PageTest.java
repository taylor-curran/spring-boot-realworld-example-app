package io.spring.application;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class PageTest {

  @Test
  public void should_create_page_with_default_values() {
    Page page = new Page();
    
    assertThat(page.getOffset(), is(0));
    assertThat(page.getLimit(), is(20));
  }

  @Test
  public void should_create_page_with_valid_offset_and_limit() {
    Page page = new Page(10, 50);
    
    assertThat(page.getOffset(), is(10));
    assertThat(page.getLimit(), is(50));
  }

  @Test
  public void should_ignore_negative_offset() {
    Page page = new Page(-5, 30);
    
    assertThat(page.getOffset(), is(0));
    assertThat(page.getLimit(), is(30));
  }

  @Test
  public void should_ignore_zero_offset() {
    Page page = new Page(0, 30);
    
    assertThat(page.getOffset(), is(0));
    assertThat(page.getLimit(), is(30));
  }

  @Test
  public void should_set_positive_offset() {
    Page page = new Page(25, 30);
    
    assertThat(page.getOffset(), is(25));
    assertThat(page.getLimit(), is(30));
  }

  @Test
  public void should_cap_limit_at_max_limit() {
    Page page = new Page(10, 150);
    
    assertThat(page.getOffset(), is(10));
    assertThat(page.getLimit(), is(100));
  }

  @Test
  public void should_ignore_negative_limit() {
    Page page = new Page(10, -5);
    
    assertThat(page.getOffset(), is(10));
    assertThat(page.getLimit(), is(20));
  }

  @Test
  public void should_ignore_zero_limit() {
    Page page = new Page(10, 0);
    
    assertThat(page.getOffset(), is(10));
    assertThat(page.getLimit(), is(20));
  }

  @Test
  public void should_set_limit_exactly_at_max_limit() {
    Page page = new Page(10, 100);
    
    assertThat(page.getOffset(), is(10));
    assertThat(page.getLimit(), is(100));
  }

  @Test
  public void should_set_valid_positive_limit() {
    Page page = new Page(10, 75);
    
    assertThat(page.getOffset(), is(10));
    assertThat(page.getLimit(), is(75));
  }

  @Test
  public void should_test_equals_with_same_values() {
    Page page1 = new Page(10, 50);
    Page page2 = new Page(10, 50);
    
    assertThat(page1.equals(page2), is(true));
    assertThat(page1, equalTo(page2));
  }

  @Test
  public void should_test_equals_with_different_offset() {
    Page page1 = new Page(10, 50);
    Page page2 = new Page(20, 50);
    
    assertThat(page1.equals(page2), is(false));
    assertThat(page1, not(equalTo(page2)));
  }

  @Test
  public void should_test_equals_with_different_limit() {
    Page page1 = new Page(10, 50);
    Page page2 = new Page(10, 60);
    
    assertThat(page1.equals(page2), is(false));
    assertThat(page1, not(equalTo(page2)));
  }

  @Test
  public void should_test_equals_with_null() {
    Page page = new Page(10, 50);
    
    assertThat(page.equals(null), is(false));
  }

  @Test
  public void should_test_equals_with_different_class() {
    Page page = new Page(10, 50);
    String notAPage = "not a page";
    
    assertThat(page.equals(notAPage), is(false));
  }

  @Test
  public void should_test_equals_with_same_instance() {
    Page page = new Page(10, 50);
    
    assertThat(page.equals(page), is(true));
  }

  @Test
  public void should_test_hashcode_consistency() {
    Page page1 = new Page(10, 50);
    Page page2 = new Page(10, 50);
    
    assertThat(page1.hashCode(), is(page2.hashCode()));
  }

  @Test
  public void should_test_hashcode_different_for_different_objects() {
    Page page1 = new Page(10, 50);
    Page page2 = new Page(20, 60);
    
    assertThat(page1.hashCode(), not(equalTo(page2.hashCode())));
  }

  @Test
  public void should_test_hashcode_same_for_same_instance() {
    Page page = new Page(10, 50);
    
    assertThat(page.hashCode(), is(page.hashCode()));
  }

  @Test
  public void should_test_toString_contains_field_values() {
    Page page = new Page(10, 50);
    String toString = page.toString();
    
    assertThat(toString.contains("offset=10"), is(true));
    assertThat(toString.contains("limit=50"), is(true));
  }

  @Test
  public void should_test_toString_with_default_values() {
    Page page = new Page();
    String toString = page.toString();
    
    assertThat(toString.contains("offset=0"), is(true));
    assertThat(toString.contains("limit=20"), is(true));
  }

  @Test
  public void should_test_toString_with_max_limit() {
    Page page = new Page(5, 150);
    String toString = page.toString();
    
    assertThat(toString.contains("offset=5"), is(true));
    assertThat(toString.contains("limit=100"), is(true));
  }

  @Test
  public void should_test_getters_with_default_constructor() {
    Page page = new Page();
    
    assertThat(page.getOffset(), is(0));
    assertThat(page.getLimit(), is(20));
  }

  @Test
  public void should_test_constructor_with_negative_offset() {
    Page page = new Page(-10, 50);
    
    assertThat(page.getOffset(), is(0));
    assertThat(page.getLimit(), is(50));
  }

  @Test
  public void should_test_constructor_with_limit_over_max() {
    Page page = new Page(10, 200);
    
    assertThat(page.getOffset(), is(10));
    assertThat(page.getLimit(), is(100));
  }

  @Test
  public void should_test_constructor_with_negative_limit() {
    Page page = new Page(10, -5);
    
    assertThat(page.getOffset(), is(10));
    assertThat(page.getLimit(), is(20));
  }

  @Test
  public void should_test_edge_case_with_zero_values() {
    Page page = new Page(0, 0);
    
    assertThat(page.getOffset(), is(0));
    assertThat(page.getLimit(), is(20));
  }

  @Test
  public void should_test_edge_case_with_boundary_values() {
    Page page = new Page(1, 1);
    
    assertThat(page.getOffset(), is(1));
    assertThat(page.getLimit(), is(1));
  }
}
