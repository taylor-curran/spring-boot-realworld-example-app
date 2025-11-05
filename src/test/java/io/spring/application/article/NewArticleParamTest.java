package io.spring.application.article;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;

public class NewArticleParamTest {

  @Test
  public void should_create_new_article_param_with_all_fields() {
    NewArticleParam param =
        new NewArticleParam(
            "Test Title", "Test Description", "Test Body", Arrays.asList("tag1", "tag2"));

    assertThat(param.getTitle()).isEqualTo("Test Title");
    assertThat(param.getDescription()).isEqualTo("Test Description");
    assertThat(param.getBody()).isEqualTo("Test Body");
    assertThat(param.getTagList()).containsExactly("tag1", "tag2");
  }

  @Test
  public void should_create_new_article_param_with_no_args_constructor() {
    NewArticleParam param = new NewArticleParam();

    assertThat(param.getTitle()).isNull();
    assertThat(param.getDescription()).isNull();
    assertThat(param.getBody()).isNull();
    assertThat(param.getTagList()).isNull();
  }

  @Test
  public void should_create_new_article_param_with_builder() {
    NewArticleParam param =
        NewArticleParam.builder()
            .title("Builder Title")
            .description("Builder Description")
            .body("Builder Body")
            .tagList(Arrays.asList("builder-tag1", "builder-tag2"))
            .build();

    assertThat(param.getTitle()).isEqualTo("Builder Title");
    assertThat(param.getDescription()).isEqualTo("Builder Description");
    assertThat(param.getBody()).isEqualTo("Builder Body");
    assertThat(param.getTagList()).containsExactly("builder-tag1", "builder-tag2");
  }

  @Test
  public void should_handle_empty_tag_list() {
    NewArticleParam param =
        new NewArticleParam("Title", "Description", "Body", Collections.emptyList());

    assertThat(param.getTagList()).isEmpty();
  }

  @Test
  public void should_handle_null_tag_list() {
    NewArticleParam param = new NewArticleParam("Title", "Description", "Body", null);

    assertThat(param.getTagList()).isNull();
  }

  @Test
  public void should_handle_single_tag() {
    NewArticleParam param =
        new NewArticleParam(
            "Title", "Description", "Body", Collections.singletonList("single-tag"));

    assertThat(param.getTagList()).containsExactly("single-tag");
  }

  @Test
  public void should_handle_multiple_tags() {
    NewArticleParam param =
        new NewArticleParam(
            "Title", "Description", "Body", Arrays.asList("tag1", "tag2", "tag3", "tag4", "tag5"));

    assertThat(param.getTagList()).containsExactly("tag1", "tag2", "tag3", "tag4", "tag5");
  }

  @Test
  public void should_handle_special_characters_in_fields() {
    NewArticleParam param =
        new NewArticleParam(
            "Title with Special Chars!@#$%^&*()",
            "Description with émojis 🚀 and symbols ©®™",
            "Body with unicode: 测试内容 and newlines\n\nMultiple paragraphs",
            Arrays.asList("special-tag!@#", "unicode-标签", "emoji-🏷️"));

    assertThat(param.getTitle()).isEqualTo("Title with Special Chars!@#$%^&*()");
    assertThat(param.getDescription()).isEqualTo("Description with émojis 🚀 and symbols ©®™");
    assertThat(param.getBody())
        .isEqualTo("Body with unicode: 测试内容 and newlines\n\nMultiple paragraphs");
    assertThat(param.getTagList()).containsExactly("special-tag!@#", "unicode-标签", "emoji-🏷️");
  }

  @Test
  public void should_handle_long_content() {
    String longTitle = "Very Long Title ".repeat(10);
    String longDescription = "Very Long Description ".repeat(20);
    String longBody = "Very Long Body Content ".repeat(100);

    NewArticleParam param =
        new NewArticleParam(
            longTitle, longDescription, longBody, Arrays.asList("tag1", "tag2", "tag3"));

    assertThat(param.getTitle()).isEqualTo(longTitle);
    assertThat(param.getDescription()).isEqualTo(longDescription);
    assertThat(param.getBody()).isEqualTo(longBody);
  }

  @Test
  public void should_handle_minimum_length_content() {
    NewArticleParam param = new NewArticleParam("T", "D", "B", Collections.singletonList("t"));

    assertThat(param.getTitle()).isEqualTo("T");
    assertThat(param.getDescription()).isEqualTo("D");
    assertThat(param.getBody()).isEqualTo("B");
    assertThat(param.getTagList()).containsExactly("t");
  }

  @Test
  public void should_handle_empty_strings() {
    NewArticleParam param = new NewArticleParam("", "", "", Collections.emptyList());

    assertThat(param.getTitle()).isEqualTo("");
    assertThat(param.getDescription()).isEqualTo("");
    assertThat(param.getBody()).isEqualTo("");
    assertThat(param.getTagList()).isEmpty();
  }

  @Test
  public void should_handle_whitespace_content() {
    NewArticleParam param =
        new NewArticleParam(
            "  Title with spaces  ",
            "  Description with tabs\t\t",
            "  Body with newlines\n\n  ",
            Arrays.asList("  spaced-tag  ", "\ttab-tag\t"));

    assertThat(param.getTitle()).isEqualTo("  Title with spaces  ");
    assertThat(param.getDescription()).isEqualTo("  Description with tabs\t\t");
    assertThat(param.getBody()).isEqualTo("  Body with newlines\n\n  ");
    assertThat(param.getTagList()).containsExactly("  spaced-tag  ", "\ttab-tag\t");
  }

  @Test
  public void should_handle_duplicate_tags() {
    NewArticleParam param =
        new NewArticleParam(
            "Title", "Description", "Body", Arrays.asList("tag1", "tag2", "tag1", "tag3", "tag2"));

    assertThat(param.getTagList()).containsExactly("tag1", "tag2", "tag1", "tag3", "tag2");
  }
}
