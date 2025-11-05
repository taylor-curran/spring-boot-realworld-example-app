package io.spring.core.article;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class TagTest {

  @Test
  public void should_create_tag_with_name() {
    String tagName = "java";

    Tag tag = new Tag(tagName);

    assertThat(tag.getName()).isEqualTo(tagName);
    assertThat(tag.getId()).isNotNull();
  }

  @Test
  public void should_create_tag_with_no_args_constructor() {
    Tag tag = new Tag();

    assertThat(tag.getId()).isNull();
    assertThat(tag.getName()).isNull();
  }

  @Test
  public void should_set_and_get_fields() {
    Tag tag = new Tag();
    String id = "test-id";
    String name = "spring";

    tag.setId(id);
    tag.setName(name);

    assertThat(tag.getId()).isEqualTo(id);
    assertThat(tag.getName()).isEqualTo(name);
  }

  @Test
  public void should_generate_unique_ids() {
    Tag tag1 = new Tag("java");
    Tag tag2 = new Tag("spring");

    assertThat(tag1.getId()).isNotEqualTo(tag2.getId());
    assertThat(tag1.getId()).isNotNull();
    assertThat(tag2.getId()).isNotNull();
  }

  @Test
  public void should_handle_equals_and_hashcode_based_on_name() {
    Tag tag1 = new Tag("java");
    Tag tag2 = new Tag("java");
    Tag tag3 = new Tag("spring");

    assertThat(tag1).isEqualTo(tag2); // Same name
    assertThat(tag1).isNotEqualTo(tag3); // Different name
    assertThat(tag1.hashCode()).isEqualTo(tag2.hashCode());
    assertThat(tag1.hashCode()).isNotEqualTo(tag3.hashCode());
  }

  @Test
  public void should_handle_equals_with_different_types() {
    Tag tag = new Tag("java");

    assertThat(tag.equals(null)).isFalse();
    assertThat(tag.equals("not a tag")).isFalse();
    assertThat(tag.equals(new Object())).isFalse();
    assertThat(tag.equals(tag)).isTrue();
  }

  @Test
  public void should_handle_equals_with_null_names() {
    Tag tag1 = new Tag();
    Tag tag2 = new Tag();
    Tag tag3 = new Tag("java");

    tag1.setName(null);
    tag2.setName(null);

    assertThat(tag1).isEqualTo(tag2); // Both null names
    assertThat(tag1).isNotEqualTo(tag3); // One null, one not
  }

  @Test
  public void should_handle_special_characters_in_name() {
    String specialName = "java-spring-boot";
    Tag tag = new Tag(specialName);

    assertThat(tag.getName()).isEqualTo(specialName);
  }

  @Test
  public void should_handle_unicode_in_name() {
    String unicodeName = "编程";
    Tag tag = new Tag(unicodeName);

    assertThat(tag.getName()).isEqualTo(unicodeName);
  }

  @Test
  public void should_handle_empty_name() {
    String emptyName = "";
    Tag tag = new Tag(emptyName);

    assertThat(tag.getName()).isEqualTo(emptyName);
    assertThat(tag.getId()).isNotNull();
  }

  @Test
  public void should_handle_toString() {
    Tag tag = new Tag("java");

    String toString = tag.toString();

    assertThat(toString).contains("Tag");
    assertThat(toString).contains("java");
  }

  @Test
  public void should_handle_can_equal_method() {
    Tag tag = new Tag("java");

    assertThat(tag.canEqual(tag)).isTrue();
    assertThat(tag.canEqual(new Tag())).isTrue();
    assertThat(tag.canEqual("not a tag")).isFalse();
    assertThat(tag.canEqual(null)).isFalse();
  }

  @Test
  public void should_handle_hash_code_consistency() {
    Tag tag1 = new Tag("java");
    Tag tag2 = new Tag("java");

    assertThat(tag1.hashCode()).isEqualTo(tag2.hashCode());

    int hash1 = tag1.hashCode();
    int hash2 = tag1.hashCode();
    assertThat(hash1).isEqualTo(hash2);
  }
}
