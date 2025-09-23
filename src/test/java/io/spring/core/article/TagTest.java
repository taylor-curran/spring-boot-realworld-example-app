package io.spring.core.article;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class TagTest {

  @Test
  public void should_create_tag_with_constructor() {
    Tag tag = new Tag("java");

    assertThat(tag.getName(), is("java"));
    assertThat(tag.getId(), notNullValue());
  }

  @Test
  public void should_create_tag_with_no_args_constructor() {
    Tag tag = new Tag();

    assertThat(tag.getName(), is((String) null));
    assertThat(tag.getId(), is((String) null));
  }

  @Test
  public void should_set_and_get_name() {
    Tag tag = new Tag();
    tag.setName("spring");

    assertThat(tag.getName(), is("spring"));
  }

  @Test
  public void should_set_and_get_id() {
    Tag tag = new Tag();
    tag.setId("test-id");

    assertThat(tag.getId(), is("test-id"));
  }

  @Test
  public void should_test_equals_with_same_name() {
    Tag tag1 = new Tag("java");
    Tag tag2 = new Tag("java");

    assertThat(tag1.equals(tag2), is(true));
    assertThat(tag2.equals(tag1), is(true));
  }

  @Test
  public void should_test_equals_with_different_names() {
    Tag tag1 = new Tag("java");
    Tag tag2 = new Tag("spring");

    assertThat(tag1.equals(tag2), is(false));
    assertThat(tag2.equals(tag1), is(false));
  }

  @Test
  public void should_test_equals_with_null_names() {
    Tag tag1 = new Tag();
    Tag tag2 = new Tag();
    tag1.setName(null);
    tag2.setName(null);

    assertThat(tag1.equals(tag2), is(true));
  }

  @Test
  public void should_test_equals_with_one_null_name() {
    Tag tag1 = new Tag("java");
    Tag tag2 = new Tag();
    tag2.setName(null);

    assertThat(tag1.equals(tag2), is(false));
    assertThat(tag2.equals(tag1), is(false));
  }

  @Test
  public void should_test_equals_with_null_object() {
    Tag tag = new Tag("java");

    assertThat(tag.equals(null), is(false));
  }

  @Test
  public void should_test_equals_with_different_class() {
    Tag tag = new Tag("java");
    String notATag = "not a tag";

    assertThat(tag.equals(notATag), is(false));
  }

  @Test
  public void should_test_equals_with_same_object() {
    Tag tag = new Tag("java");

    assertThat(tag.equals(tag), is(true));
  }

  @Test
  public void should_test_equals_ignores_id_differences() {
    Tag tag1 = new Tag("java");
    Tag tag2 = new Tag("java");
    tag1.setId("id1");
    tag2.setId("id2");

    assertThat(tag1.equals(tag2), is(true));
  }

  @Test
  public void should_test_hashcode_with_same_name() {
    Tag tag1 = new Tag("java");
    Tag tag2 = new Tag("java");

    assertThat(tag1.hashCode(), equalTo(tag2.hashCode()));
  }

  @Test
  public void should_test_hashcode_with_different_names() {
    Tag tag1 = new Tag("java");
    Tag tag2 = new Tag("spring");

    assertThat(tag1.hashCode(), not(equalTo(tag2.hashCode())));
  }

  @Test
  public void should_test_hashcode_with_null_name() {
    Tag tag1 = new Tag();
    Tag tag2 = new Tag();
    tag1.setName(null);
    tag2.setName(null);

    assertThat(tag1.hashCode(), equalTo(tag2.hashCode()));
  }

  @Test
  public void should_test_hashcode_ignores_id_differences() {
    Tag tag1 = new Tag("java");
    Tag tag2 = new Tag("java");
    tag1.setId("id1");
    tag2.setId("id2");

    assertThat(tag1.hashCode(), equalTo(tag2.hashCode()));
  }

  @Test
  public void should_test_toString_method() {
    Tag tag = new Tag("java");
    tag.setId("test-id");

    String toString = tag.toString();

    assertThat(toString, notNullValue());
    assertThat(toString.contains("Tag"), is(true));
    assertThat(toString.contains("java"), is(true));
    assertThat(toString.contains("test-id"), is(true));
  }

  @Test
  public void should_test_toString_with_null_values() {
    Tag tag = new Tag();

    String toString = tag.toString();

    assertThat(toString, notNullValue());
    assertThat(toString.contains("Tag"), is(true));
  }

  @Test
  public void should_test_canEqual_method() {
    Tag tag = new Tag("java");
    Tag otherTag = new Tag("spring");

    assertThat(tag.canEqual(otherTag), is(true));
    assertThat(tag.canEqual(tag), is(true));
    assertThat(tag.canEqual("not a tag"), is(false));
    assertThat(tag.canEqual(null), is(false));
  }
}
