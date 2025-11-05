package io.spring.core.article;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.joda.time.DateTime;
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
  public void should_create_article_with_all_fields() {
    String title = "Test Article";
    String description = "Test description";
    String body = "Test body content";
    List<String> tagList = Arrays.asList("java", "spring", "test");
    String userId = "user-123";

    Article article = new Article(title, description, body, tagList, userId);

    assertThat(article.getTitle()).isEqualTo(title);
    assertThat(article.getDescription()).isEqualTo(description);
    assertThat(article.getBody()).isEqualTo(body);
    assertThat(article.getUserId()).isEqualTo(userId);
    assertThat(article.getId()).isNotNull();
    assertThat(article.getSlug()).isEqualTo("test-article");
    assertThat(article.getTags()).hasSize(3);
    assertThat(article.getCreatedAt()).isNotNull();
    assertThat(article.getUpdatedAt()).isNotNull();
    assertThat(article.getCreatedAt()).isEqualTo(article.getUpdatedAt());
  }

  @Test
  public void should_create_article_with_custom_created_at() {
    DateTime customCreatedAt = new DateTime().minusDays(1);
    String title = "Test Article";
    String description = "Test description";
    String body = "Test body content";
    List<String> tagList = Arrays.asList("java");
    String userId = "user-123";

    Article article = new Article(title, description, body, tagList, userId, customCreatedAt);

    assertThat(article.getCreatedAt()).isEqualTo(customCreatedAt);
    assertThat(article.getUpdatedAt()).isEqualTo(customCreatedAt);
  }

  @Test
  public void should_handle_empty_tag_list() {
    Article article = new Article("Title", "desc", "body", Collections.emptyList(), "user-123");

    assertThat(article.getTags()).isEmpty();
  }

  @Test
  public void should_remove_duplicate_tags() {
    List<String> duplicateTags = Arrays.asList("java", "spring", "java", "test", "spring");
    Article article = new Article("Title", "desc", "body", duplicateTags, "user-123");

    assertThat(article.getTags()).hasSize(3);
    List<String> tagNames = article.getTags().stream()
        .map(Tag::getName)
        .sorted()
        .collect(java.util.stream.Collectors.toList());
    assertThat(tagNames).containsExactly("java", "spring", "test");
  }

  @Test
  public void should_update_title_and_regenerate_slug() {
    Article article = new Article("Old Title", "desc", "body", Arrays.asList("java"), "user-123");
    DateTime originalUpdatedAt = article.getUpdatedAt();
    
    try { Thread.sleep(1); } catch (InterruptedException e) {}
    
    article.update("New Amazing Title", null, null);

    assertThat(article.getTitle()).isEqualTo("New Amazing Title");
    assertThat(article.getSlug()).isEqualTo("new-amazing-title");
    assertThat(article.getUpdatedAt().isAfter(originalUpdatedAt)).isTrue();
  }

  @Test
  public void should_update_description() {
    Article article = new Article("Title", "old desc", "body", Arrays.asList("java"), "user-123");
    DateTime originalUpdatedAt = article.getUpdatedAt();
    
    try { Thread.sleep(1); } catch (InterruptedException e) {}
    
    article.update(null, "new description", null);

    assertThat(article.getDescription()).isEqualTo("new description");
    assertThat(article.getUpdatedAt().isAfter(originalUpdatedAt)).isTrue();
  }

  @Test
  public void should_update_body() {
    Article article = new Article("Title", "desc", "old body", Arrays.asList("java"), "user-123");
    DateTime originalUpdatedAt = article.getUpdatedAt();
    
    try { Thread.sleep(1); } catch (InterruptedException e) {}
    
    article.update(null, null, "new body content");

    assertThat(article.getBody()).isEqualTo("new body content");
    assertThat(article.getUpdatedAt().isAfter(originalUpdatedAt)).isTrue();
  }

  @Test
  public void should_update_all_fields() {
    Article article = new Article("Old Title", "old desc", "old body", Arrays.asList("java"), "user-123");
    DateTime originalUpdatedAt = article.getUpdatedAt();
    
    try { Thread.sleep(1); } catch (InterruptedException e) {}
    
    article.update("New Title", "new desc", "new body");

    assertThat(article.getTitle()).isEqualTo("New Title");
    assertThat(article.getDescription()).isEqualTo("new desc");
    assertThat(article.getBody()).isEqualTo("new body");
    assertThat(article.getSlug()).isEqualTo("new-title");
    assertThat(article.getUpdatedAt().isAfter(originalUpdatedAt)).isTrue();
  }

  @Test
  public void should_not_update_with_empty_strings() {
    Article article = new Article("Title", "desc", "body", Arrays.asList("java"), "user-123");
    DateTime originalUpdatedAt = article.getUpdatedAt();
    
    article.update("", "", "");

    assertThat(article.getTitle()).isEqualTo("Title");
    assertThat(article.getDescription()).isEqualTo("desc");
    assertThat(article.getBody()).isEqualTo("body");
    assertThat(article.getUpdatedAt()).isEqualTo(originalUpdatedAt);
  }

  @Test
  public void should_not_update_with_null_values() {
    Article article = new Article("Title", "desc", "body", Arrays.asList("java"), "user-123");
    DateTime originalUpdatedAt = article.getUpdatedAt();
    
    article.update(null, null, null);

    assertThat(article.getTitle()).isEqualTo("Title");
    assertThat(article.getDescription()).isEqualTo("desc");
    assertThat(article.getBody()).isEqualTo("body");
    assertThat(article.getUpdatedAt()).isEqualTo(originalUpdatedAt);
  }

  @Test
  public void should_test_equals_and_hashcode() {
    Article article1 = new Article("Title", "desc", "body", Arrays.asList("java"), "user-123");
    Article article2 = new Article("Title", "desc", "body", Arrays.asList("java"), "user-123");
    Article article3 = new Article("Different Title", "desc", "body", Arrays.asList("java"), "user-123");

    assertThat(article1).isNotEqualTo(article2); // Different IDs
    assertThat(article1).isNotEqualTo(article3);
    assertThat(article1).isNotEqualTo(null);
    assertThat(article1).isNotEqualTo("string");
    assertThat(article1).isEqualTo(article1);
  }

  @Test
  public void should_handle_equals_edge_cases() {
    Article article = new Article("Title", "desc", "body", Arrays.asList("java"), "user-123");
    
    assertThat(article).isEqualTo(article);
    assertThat(article).isNotEqualTo(null);
    assertThat(article).isNotEqualTo("not an Article");
    
    Article differentIdArticle = new Article("Title", "desc", "body", Arrays.asList("java"), "user-123");
    assertThat(article).isNotEqualTo(differentIdArticle);
    
    Article nullIdArticle = new Article();
    Article anotherNullIdArticle = new Article();
    
    assertThat(nullIdArticle).isEqualTo(anotherNullIdArticle);
    assertThat(nullIdArticle).isNotEqualTo(article);
    assertThat(article).isNotEqualTo(nullIdArticle);
  }

  @Test
  public void should_handle_hashcode_consistency() {
    Article article = new Article("Title", "desc", "body", Arrays.asList("java"), "user-123");
    
    int hashCode1 = article.hashCode();
    int hashCode2 = article.hashCode();
    
    assertThat(hashCode1).isEqualTo(hashCode2);
    
    Article differentIdArticle = new Article("Title", "desc", "body", Arrays.asList("java"), "user-123");
    assertThat(article.hashCode()).isNotEqualTo(differentIdArticle.hashCode());
  }

  @Test
  public void should_handle_hashcode_with_null_id() {
    Article nullIdArticle = new Article();
    
    int hashCode = nullIdArticle.hashCode();
    int hashCode2 = nullIdArticle.hashCode();
    assertThat(hashCode).isEqualTo(hashCode2);
    
    Article anotherNullIdArticle = new Article();
    assertThat(nullIdArticle.hashCode()).isEqualTo(anotherNullIdArticle.hashCode());
  }

  @Test
  public void should_generate_unique_ids() {
    Article article1 = new Article("Title 1", "desc", "body", Arrays.asList("java"), "user-123");
    Article article2 = new Article("Title 2", "desc", "body", Arrays.asList("java"), "user-123");

    assertThat(article1.getId()).isNotEqualTo(article2.getId());
    assertThat(article1.getId()).isNotNull();
    assertThat(article2.getId()).isNotNull();
  }

  @Test
  public void should_handle_special_characters_in_slug() {
    assertThat(Article.toSlug("Hello & World")).isEqualTo("hello-world");
    assertThat(Article.toSlug("Test's \"Article\"")).isEqualTo("test's-\"article\"");
    assertThat(Article.toSlug("What? The. Hell, World!")).isEqualTo("what-the-hell-world!");
    assertThat(Article.toSlug("Multiple   Spaces")).isEqualTo("multiple-spaces");
  }

  @Test
  public void should_handle_unicode_in_slug() {
    assertThat(Article.toSlug("Café & Restaurant")).isEqualTo("café-restaurant");
    assertThat(Article.toSlug("测试文章标题")).isEqualTo("测试文章标题");
    assertThat(Article.toSlug("Español: Artículo")).isEqualTo("español:-artículo");
  }

  @Test
  public void should_handle_edge_cases_in_slug() {
    assertThat(Article.toSlug("")).isEqualTo("");
    assertThat(Article.toSlug("   ")).isEqualTo("-");
    assertThat(Article.toSlug("!!!")).isEqualTo("!!!");
    assertThat(Article.toSlug("123")).isEqualTo("123");
  }

  @Test
  public void should_create_article_with_no_args_constructor() {
    Article article = new Article();
    
    assertThat(article.getId()).isNull();
    assertThat(article.getTitle()).isNull();
    assertThat(article.getDescription()).isNull();
    assertThat(article.getBody()).isNull();
    assertThat(article.getUserId()).isNull();
    assertThat(article.getSlug()).isNull();
    assertThat(article.getTags()).isNull();
    assertThat(article.getCreatedAt()).isNull();
    assertThat(article.getUpdatedAt()).isNull();
  }
}
