package io.spring.core.article;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;

public class ArticleTest {

  @Test
  public void should_get_right_slug() {
    Article article = new Article("a new   title", "desc", "body", Arrays.asList("java"), "123");
    assertThat(article.getSlug(), is("a-new-title"));
  }

  @Test
  public void should_get_right_slug_with_number_in_title() {
    Article article = new Article("a new title 2", "desc", "body", Arrays.asList("java"), "123");
    assertThat(article.getSlug(), is("a-new-title-2"));
  }

  @Test
  public void should_get_lower_case_slug() {
    Article article = new Article("A NEW TITLE", "desc", "body", Arrays.asList("java"), "123");
    assertThat(article.getSlug(), is("a-new-title"));
  }

  @Test
  public void should_handle_other_language() {
    Article article = new Article("中文：标题", "desc", "body", Arrays.asList("java"), "123");
    assertThat(article.getSlug(), is("中文-标题"));
  }

  @Test
  public void should_handle_commas() {
    Article article = new Article("what?the.hell,w", "desc", "body", Arrays.asList("java"), "123");
    assertThat(article.getSlug(), is("what-the-hell-w"));
  }

  @Test
  public void should_test_equals_with_different_articles() {
    Article article1 = new Article("title", "desc", "body", Arrays.asList("java"), "123");
    Article article2 = new Article("title", "desc", "body", Arrays.asList("java"), "123");

    assertThat(article1.equals(article2), is(false));
    assertThat(article2.equals(article1), is(false));
  }

  @Test
  public void should_test_equals_with_null_object() {
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), "123");

    assertThat(article.equals(null), is(false));
  }

  @Test
  public void should_test_equals_with_different_class() {
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), "123");
    String notAnArticle = "not an article";

    assertThat(article.equals(notAnArticle), is(false));
  }

  @Test
  public void should_test_equals_with_same_object() {
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), "123");

    assertThat(article.equals(article), is(true));
  }

  @Test
  public void should_test_hashcode_with_different_articles() {
    Article article1 = new Article("title1", "desc", "body", Arrays.asList("java"), "123");
    Article article2 = new Article("title2", "desc", "body", Arrays.asList("java"), "123");

    assertThat(article1.hashCode(), not(equalTo(article2.hashCode())));
  }

  @Test
  public void should_test_hashcode_consistency() {
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), "123");
    int hashCode1 = article.hashCode();
    int hashCode2 = article.hashCode();

    assertThat(hashCode1, equalTo(hashCode2));
  }

  @Test
  public void should_test_hashcode_not_null() {
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), "123");

    assertThat(article.hashCode(), notNullValue());
  }

  @Test
  public void should_test_update_method_with_title() {
    Article article = new Article("original title", "desc", "body", Arrays.asList("java"), "123");
    String originalSlug = article.getSlug();
    
    article.update("new title", null, null);
    
    assertThat(article.getTitle(), is("new title"));
    assertThat(article.getSlug(), is("new-title"));
    assertThat(article.getSlug(), not(equalTo(originalSlug)));
  }

  @Test
  public void should_test_update_method_with_description() {
    Article article = new Article("title", "original desc", "body", Arrays.asList("java"), "123");
    
    article.update(null, "new description", null);
    
    assertThat(article.getDescription(), is("new description"));
  }

  @Test
  public void should_test_update_method_with_body() {
    Article article = new Article("title", "desc", "original body", Arrays.asList("java"), "123");
    
    article.update(null, null, "new body");
    
    assertThat(article.getBody(), is("new body"));
  }

  @Test
  public void should_test_update_method_with_empty_strings() {
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), "123");
    String originalTitle = article.getTitle();
    String originalDesc = article.getDescription();
    String originalBody = article.getBody();
    
    article.update("", "", "");
    
    assertThat(article.getTitle(), is(originalTitle));
    assertThat(article.getDescription(), is(originalDesc));
    assertThat(article.getBody(), is(originalBody));
  }

  @Test
  public void should_test_update_method_with_all_fields() {
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), "123");
    
    article.update("new title", "new desc", "new body");
    
    assertThat(article.getTitle(), is("new title"));
    assertThat(article.getDescription(), is("new desc"));
    assertThat(article.getBody(), is("new body"));
  }

  @Test
  public void should_test_toSlug_static_method() {
    assertThat(Article.toSlug("Test Title"), is("test-title"));
    assertThat(Article.toSlug("Test   Multiple   Spaces"), is("test-multiple-spaces"));
    assertThat(Article.toSlug("Test?Special,Characters."), is("test-special-characters-"));
  }

  @Test
  public void should_test_equals_with_same_articles() {
    Article article1 = new Article("title", "desc", "body", Arrays.asList("java"), "123");
    Article article2 = new Article("title", "desc", "body", Arrays.asList("java"), "123");

    assertThat(article1.equals(article2), is(false));
    assertThat(article2.equals(article1), is(false));
  }

  @Test
  public void should_test_equals_with_different_titles() {
    Article article1 = new Article("title1", "desc", "body", Arrays.asList("java"), "123");
    Article article2 = new Article("title2", "desc", "body", Arrays.asList("java"), "123");

    assertThat(article1.equals(article2), is(false));
    assertThat(article2.equals(article1), is(false));
  }

  @Test
  public void should_test_equals_with_different_descriptions() {
    Article article1 = new Article("title", "desc1", "body", Arrays.asList("java"), "123");
    Article article2 = new Article("title", "desc2", "body", Arrays.asList("java"), "123");

    assertThat(article1.equals(article2), is(false));
  }

  @Test
  public void should_test_equals_with_different_bodies() {
    Article article1 = new Article("title", "desc", "body1", Arrays.asList("java"), "123");
    Article article2 = new Article("title", "desc", "body2", Arrays.asList("java"), "123");

    assertThat(article1.equals(article2), is(false));
  }

  @Test
  public void should_test_equals_with_different_tags() {
    Article article1 = new Article("title", "desc", "body", Arrays.asList("java"), "123");
    Article article2 = new Article("title", "desc", "body", Arrays.asList("spring"), "123");

    assertThat(article1.equals(article2), is(false));
  }

  @Test
  public void should_test_equals_with_different_user_ids() {
    Article article1 = new Article("title", "desc", "body", Arrays.asList("java"), "123");
    Article article2 = new Article("title", "desc", "body", Arrays.asList("java"), "456");

    assertThat(article1.equals(article2), is(false));
  }

  @Test
  public void should_test_equals_with_null_fields() {
    Article article1 = new Article();
    Article article2 = new Article();

    assertThat(article1.equals(article2), is(true));
  }

  @Test
  public void should_test_equals_with_different_constructors() {
    Article article1 = new Article("title", "desc", "body", Arrays.asList("java"), "123");
    Article article2 = new Article();

    assertThat(article1.equals(article2), is(false));
    assertThat(article2.equals(article1), is(false));
  }

  @Test
  public void should_test_equals_with_empty_vs_different_tags() {
    Article article1 = new Article("title", "desc", "body", Collections.emptyList(), "123");
    Article article2 = new Article("title", "desc", "body", Arrays.asList("tag"), "123");

    assertThat(article1.equals(article2), is(false));
  }

  @Test
  public void should_test_hashcode_with_same_articles() {
    Article article1 = new Article("title", "desc", "body", Arrays.asList("java"), "123");
    Article article2 = new Article("title", "desc", "body", Arrays.asList("java"), "123");

    assertThat(article1.hashCode(), not(equalTo(article2.hashCode())));
  }

  @Test
  public void should_test_hashcode_with_null_fields() {
    Article article1 = new Article();
    Article article2 = new Article();

    assertThat(article1.hashCode(), equalTo(article2.hashCode()));
  }

  @Test
  public void should_test_hashcode_with_different_tags() {
    Article article1 = new Article("title", "desc", "body", Arrays.asList("java"), "123");
    Article article2 = new Article("title", "desc", "body", Arrays.asList("spring"), "123");

    assertThat(article1.hashCode(), not(equalTo(article2.hashCode())));
  }

  @Test
  public void should_test_hashcode_with_different_user_ids() {
    Article article1 = new Article("title", "desc", "body", Arrays.asList("java"), "123");
    Article article2 = new Article("title", "desc", "body", Arrays.asList("java"), "456");

    assertThat(article1.hashCode(), not(equalTo(article2.hashCode())));
  }

  @Test
  public void should_test_equals_with_mixed_null_fields() {
    Article article1 = new Article("title", null, "body", Arrays.asList("java"), "123");
    Article article2 = new Article("title", "desc", "body", Arrays.asList("java"), "123");

    assertThat(article1.equals(article2), is(false));
    assertThat(article2.equals(article1), is(false));
  }

  @Test
  public void should_test_equals_with_null_vs_empty_description() {
    Article article1 = new Article("title", null, "body", Arrays.asList("java"), "123");
    Article article2 = new Article("title", "", "body", Arrays.asList("java"), "123");

    assertThat(article1.equals(article2), is(false));
    assertThat(article2.equals(article1), is(false));
  }
}
