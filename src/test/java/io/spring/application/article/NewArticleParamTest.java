package io.spring.application.article;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;

public class NewArticleParamTest {

  @Test
  public void should_create_new_article_param_with_builder() {
    NewArticleParam param = NewArticleParam.builder()
        .title("Test Article")
        .description("Test description")
        .body("Test body content")
        .tagList(Arrays.asList("java", "spring"))
        .build();

    assertThat(param.getTitle(), is("Test Article"));
    assertThat(param.getDescription(), is("Test description"));
    assertThat(param.getBody(), is("Test body content"));
    assertThat(param.getTagList().size(), is(2));
    assertThat(param.getTagList().get(0), is("java"));
    assertThat(param.getTagList().get(1), is("spring"));
  }

  @Test
  public void should_create_new_article_param_with_default_values() {
    NewArticleParam param = NewArticleParam.builder().build();

    assertThat(param.getTitle(), is((String) null));
    assertThat(param.getDescription(), is((String) null));
    assertThat(param.getBody(), is((String) null));
    assertThat(param.getTagList(), is((java.util.List<String>) null));
  }

  @Test
  public void should_create_new_article_param_with_partial_values() {
    NewArticleParam param = NewArticleParam.builder()
        .title("Partial Article")
        .description("Partial description")
        .build();

    assertThat(param.getTitle(), is("Partial Article"));
    assertThat(param.getDescription(), is("Partial description"));
    assertThat(param.getBody(), is((String) null));
    assertThat(param.getTagList(), is((java.util.List<String>) null));
  }

  @Test
  public void should_test_builder_toString_method() {
    NewArticleParam.NewArticleParamBuilder builder = NewArticleParam.builder()
        .title("Test Article")
        .description("Test description")
        .body("Test body")
        .tagList(Arrays.asList("tag1", "tag2"));

    String toStringResult = builder.toString();
    
    assertThat(toStringResult, notNullValue());
    assertThat(toStringResult, containsString("NewArticleParamBuilder"));
    assertThat(toStringResult, containsString("title=Test Article"));
    assertThat(toStringResult, containsString("description=Test description"));
    assertThat(toStringResult, containsString("body=Test body"));
    assertThat(toStringResult, containsString("tagList=[tag1, tag2]"));
  }

  @Test
  public void should_test_builder_toString_with_empty_values() {
    NewArticleParam.NewArticleParamBuilder builder = NewArticleParam.builder();

    String toStringResult = builder.toString();
    
    assertThat(toStringResult, notNullValue());
    assertThat(toStringResult, containsString("NewArticleParamBuilder"));
  }

  @Test
  public void should_test_builder_individual_setters() {
    NewArticleParam.NewArticleParamBuilder builder = NewArticleParam.builder();
    
    builder.title("Individual Title");
    builder.description("Individual Description");
    builder.body("Individual Body");
    builder.tagList(Arrays.asList("individual", "tags"));
    
    NewArticleParam param = builder.build();
    
    assertThat(param.getTitle(), is("Individual Title"));
    assertThat(param.getDescription(), is("Individual Description"));
    assertThat(param.getBody(), is("Individual Body"));
    assertThat(param.getTagList().size(), is(2));
    assertThat(param.getTagList().get(0), is("individual"));
    assertThat(param.getTagList().get(1), is("tags"));
  }

  @Test
  public void should_test_no_args_constructor() {
    NewArticleParam param = new NewArticleParam();
    
    assertThat(param.getTitle(), is((String) null));
    assertThat(param.getDescription(), is((String) null));
    assertThat(param.getBody(), is((String) null));
    assertThat(param.getTagList(), is((java.util.List<String>) null));
  }

  @Test
  public void should_test_all_args_constructor() {
    NewArticleParam param = new NewArticleParam(
        "All Args Title",
        "All Args Description",
        "All Args Body",
        Arrays.asList("all", "args", "tags")
    );
    
    assertThat(param.getTitle(), is("All Args Title"));
    assertThat(param.getDescription(), is("All Args Description"));
    assertThat(param.getBody(), is("All Args Body"));
    assertThat(param.getTagList().size(), is(3));
    assertThat(param.getTagList().get(0), is("all"));
    assertThat(param.getTagList().get(1), is("args"));
    assertThat(param.getTagList().get(2), is("tags"));
  }

  @Test
  public void should_handle_null_values_in_builder() {
    NewArticleParam param = NewArticleParam.builder()
        .title(null)
        .description(null)
        .body(null)
        .tagList(null)
        .build();

    assertThat(param.getTitle(), is((String) null));
    assertThat(param.getDescription(), is((String) null));
    assertThat(param.getBody(), is((String) null));
    assertThat(param.getTagList(), is((java.util.List<String>) null));
  }

  @Test
  public void should_test_builder_method_chaining() {
    NewArticleParam param = NewArticleParam.builder()
        .title("Chain Title")
        .description("Chain Description")
        .body("Chain Body")
        .tagList(Arrays.asList("chain", "tags"))
        .build();

    assertThat(param.getTitle(), is("Chain Title"));
    assertThat(param.getDescription(), is("Chain Description"));
    assertThat(param.getBody(), is("Chain Body"));
    assertThat(param.getTagList().size(), is(2));
    assertThat(param.getTagList().get(0), is("chain"));
    assertThat(param.getTagList().get(1), is("tags"));
  }

  @Test
  public void should_handle_empty_tag_list() {
    NewArticleParam param = NewArticleParam.builder()
        .title("Empty Tags Title")
        .description("Empty Tags Description")
        .body("Empty Tags Body")
        .tagList(Collections.emptyList())
        .build();

    assertThat(param.getTitle(), is("Empty Tags Title"));
    assertThat(param.getDescription(), is("Empty Tags Description"));
    assertThat(param.getBody(), is("Empty Tags Body"));
    assertThat(param.getTagList().size(), is(0));
  }

  @Test
  public void should_test_builder_toString_with_null_values() {
    NewArticleParam.NewArticleParamBuilder builder = NewArticleParam.builder()
        .title(null)
        .description(null)
        .body(null)
        .tagList(null);

    String toStringResult = builder.toString();
    
    assertThat(toStringResult, notNullValue());
    assertThat(toStringResult, containsString("NewArticleParamBuilder"));
    assertThat(toStringResult, containsString("title=null"));
    assertThat(toStringResult, containsString("description=null"));
    assertThat(toStringResult, containsString("body=null"));
    assertThat(toStringResult, containsString("tagList=null"));
  }
}
