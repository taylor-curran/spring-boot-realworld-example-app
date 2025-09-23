package io.spring.core.comment;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CommentTest {

  @Test
  public void should_create_comment_with_constructor() {
    String body = "This is a test comment";
    String userId = "user123";
    String articleId = "article456";

    Comment comment = new Comment(body, userId, articleId);

    assertThat(comment.getBody(), is(body));
    assertThat(comment.getUserId(), is(userId));
    assertThat(comment.getArticleId(), is(articleId));
    assertThat(comment.getId(), notNullValue());
    assertThat(comment.getCreatedAt(), notNullValue());
  }

  @Test
  public void should_create_comment_with_default_constructor() {
    Comment comment = new Comment();

    assertThat(comment, notNullValue());
  }

  @Test
  public void should_create_comment_with_empty_body() {
    Comment comment = new Comment("", "user123", "article456");

    assertThat(comment.getBody(), is(""));
    assertThat(comment.getUserId(), is("user123"));
    assertThat(comment.getArticleId(), is("article456"));
  }

  @Test
  public void should_create_comment_with_long_body() {
    String longBody = "This is a very long comment body that contains multiple sentences and should be handled correctly by the comment entity. It includes various types of content and formatting that might be present in a real comment scenario with lots of text content.";
    Comment comment = new Comment(longBody, "user123", "article456");

    assertThat(comment.getBody(), is(longBody));
  }

  @Test
  public void should_create_comment_with_special_characters() {
    String bodyWithSpecialChars = "Comment with special chars: @#$%^&*() and unicode: 你好世界 and quotes: \"Hello 'World'\"";
    Comment comment = new Comment(bodyWithSpecialChars, "user123", "article456");

    assertThat(comment.getBody(), is(bodyWithSpecialChars));
  }

  @Test
  public void should_test_equals_with_same_object() {
    Comment comment = new Comment("Test body", "user123", "article456");

    assertTrue(comment.equals(comment));
  }

  @Test
  public void should_test_equals_with_null() {
    Comment comment = new Comment("Test body", "user123", "article456");

    assertFalse(comment.equals(null));
  }

  @Test
  public void should_test_equals_with_different_class() {
    Comment comment = new Comment("Test body", "user123", "article456");
    String notAComment = "Not a comment";

    assertFalse(comment.equals(notAComment));
  }

  @Test
  public void should_test_equals_with_different_ids() {
    Comment comment1 = new Comment("Test body", "user123", "article456");
    Comment comment2 = new Comment("Test body", "user123", "article456");

    assertFalse(comment1.equals(comment2));
  }

  @Test
  public void should_test_equals_with_different_bodies_same_id() {
    Comment comment1 = new Comment("Test body 1", "user123", "article456");
    Comment comment2 = new Comment("Test body 2", "user123", "article456");
    
    assertFalse(comment1.equals(comment2));
  }

  @Test
  public void should_test_equals_with_different_user_ids_same_content() {
    Comment comment1 = new Comment("Test body", "user123", "article456");
    Comment comment2 = new Comment("Test body", "user456", "article456");
    
    assertFalse(comment1.equals(comment2));
  }

  @Test
  public void should_test_equals_with_different_article_ids_same_content() {
    Comment comment1 = new Comment("Test body", "user123", "article456");
    Comment comment2 = new Comment("Test body", "user123", "article789");
    
    assertFalse(comment1.equals(comment2));
  }

  @Test
  public void should_test_hashcode_consistency() {
    Comment comment = new Comment("Test body", "user123", "article456");
    
    int hashCode1 = comment.hashCode();
    int hashCode2 = comment.hashCode();
    
    assertEquals(hashCode1, hashCode2);
  }

  @Test
  public void should_test_hashcode_different_for_different_ids() {
    Comment comment1 = new Comment("Test body", "user123", "article456");
    Comment comment2 = new Comment("Test body", "user123", "article456");

    assertNotEquals(comment1.hashCode(), comment2.hashCode());
  }

  @Test
  public void should_test_hashcode_inequality_for_different_objects() {
    Comment comment1 = new Comment("Test body", "user123", "article456");
    Comment comment2 = new Comment("Different body", "user123", "article456");

    assertNotEquals(comment1.hashCode(), comment2.hashCode());
  }

  @Test
  public void should_test_hashcode_with_different_ids() {
    Comment comment1 = new Comment("Test body", "user123", "article456");
    Comment comment2 = new Comment("Test body", "user123", "article456");

    assertNotEquals(comment1.hashCode(), comment2.hashCode());
  }

  @Test
  public void should_test_can_equal_with_same_class() {
    Comment comment1 = new Comment("Test body", "user123", "article456");
    Comment comment2 = new Comment("Test body", "user123", "article456");

    assertTrue(comment1.canEqual(comment2));
  }

  @Test
  public void should_test_can_equal_with_different_class() {
    Comment comment = new Comment("Test body", "user123", "article456");
    String notAComment = "Not a comment";

    assertFalse(comment.canEqual(notAComment));
  }

  @Test
  public void should_handle_null_values_in_constructor() {
    Comment comment = new Comment(null, null, null);

    assertThat(comment.getBody(), is((String) null));
    assertThat(comment.getUserId(), is((String) null));
    assertThat(comment.getArticleId(), is((String) null));
    assertThat(comment.getId(), notNullValue());
    assertThat(comment.getCreatedAt(), notNullValue());
  }

  @Test
  public void should_test_equals_with_null_fields() {
    Comment comment1 = new Comment(null, null, null);
    Comment comment2 = new Comment(null, null, null);

    assertFalse(comment1.equals(comment2));
  }

  @Test
  public void should_test_equals_with_mixed_null_fields() {
    Comment comment1 = new Comment("Test body", null, "article456");
    Comment comment2 = new Comment(null, "user123", "article456");

    assertFalse(comment1.equals(comment2));
  }

  @Test
  public void should_test_hashcode_with_null_fields() {
    Comment comment1 = new Comment(null, null, null);
    Comment comment2 = new Comment(null, null, null);

    assertNotEquals(comment1.hashCode(), comment2.hashCode());
  }

  @Test
  public void should_generate_unique_ids_for_different_comments() {
    Comment comment1 = new Comment("Test body 1", "user123", "article456");
    Comment comment2 = new Comment("Test body 2", "user456", "article789");

    assertThat(comment1.getId(), not(comment2.getId()));
  }

  @Test
  public void should_generate_different_created_at_for_different_comments() {
    Comment comment1 = new Comment("Test body 1", "user123", "article456");
    
    try {
      Thread.sleep(1);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    
    Comment comment2 = new Comment("Test body 2", "user456", "article789");

    assertThat(comment1.getCreatedAt(), not(comment2.getCreatedAt()));
  }

  @Test
  public void should_handle_whitespace_in_body() {
    String bodyWithWhitespace = "   Test body with leading and trailing spaces   ";
    Comment comment = new Comment(bodyWithWhitespace, "user123", "article456");

    assertThat(comment.getBody(), is(bodyWithWhitespace));
  }

  @Test
  public void should_handle_multiline_body() {
    String multilineBody = "This is line 1\nThis is line 2\nThis is line 3";
    Comment comment = new Comment(multilineBody, "user123", "article456");

    assertThat(comment.getBody(), is(multilineBody));
  }

  @Test
  public void should_handle_html_content_in_body() {
    String htmlBody = "<p>This is a <strong>bold</strong> comment with <em>italic</em> text.</p>";
    Comment comment = new Comment(htmlBody, "user123", "article456");

    assertThat(comment.getBody(), is(htmlBody));
  }

  @Test
  public void should_handle_markdown_content_in_body() {
    String markdownBody = "# Header\n\n**Bold text** and *italic text*\n\n- List item 1\n- List item 2";
    Comment comment = new Comment(markdownBody, "user123", "article456");

    assertThat(comment.getBody(), is(markdownBody));
  }

  @Test
  public void should_test_equals_with_comment_having_null_id() {
    Comment comment1 = new Comment();
    Comment comment2 = new Comment();
    
    assertTrue(comment1.equals(comment2));
  }

  @Test
  public void should_test_equals_with_same_comment_instance() {
    Comment comment = new Comment("Test body", "user123", "article456");
    
    assertTrue(comment.equals(comment));
  }

  @Test
  public void should_test_hashcode_with_comment_having_null_id() {
    Comment comment1 = new Comment();
    Comment comment2 = new Comment();
    
    assertEquals(comment1.hashCode(), comment2.hashCode());
  }

  @Test
  public void should_test_can_equal_with_null() {
    Comment comment = new Comment("Test body", "user123", "article456");
    
    assertFalse(comment.canEqual(null));
  }

  @Test
  public void should_test_can_equal_with_subclass() {
    Comment comment = new Comment("Test body", "user123", "article456");
    Object subclassInstance = new Object() {
      @Override
      public boolean equals(Object obj) {
        return super.equals(obj);
      }
    };
    
    assertFalse(comment.canEqual(subclassInstance));
  }

  @Test
  public void should_test_equals_edge_case_with_different_types() {
    Comment comment = new Comment("Test body", "user123", "article456");
    Integer notAComment = 42;
    
    assertFalse(comment.equals(notAComment));
  }

  @Test
  public void should_test_equals_with_comment_subclass() {
    Comment comment = new Comment("Test body", "user123", "article456");
    
    class CommentSubclass extends Comment {
      public CommentSubclass(String body, String userId, String articleId) {
        super(body, userId, articleId);
      }
    }
    
    CommentSubclass subComment = new CommentSubclass("Test body", "user123", "article456");
    
    assertFalse(comment.equals(subComment));
    assertFalse(subComment.equals(comment));
  }

  @Test
  public void should_test_hashcode_consistency_with_multiple_calls() {
    Comment comment = new Comment("Test body", "user123", "article456");
    
    int hash1 = comment.hashCode();
    int hash2 = comment.hashCode();
    int hash3 = comment.hashCode();
    
    assertEquals(hash1, hash2);
    assertEquals(hash2, hash3);
    assertEquals(hash1, hash3);
  }

  @Test
  public void should_test_equals_reflexivity() {
    Comment comment = new Comment("Test body", "user123", "article456");
    
    assertTrue(comment.equals(comment));
  }

  @Test
  public void should_test_equals_symmetry() {
    Comment comment1 = new Comment("Test body", "user123", "article456");
    Comment comment2 = new Comment("Different body", "user456", "article789");
    
    assertFalse(comment1.equals(comment2));
    assertFalse(comment2.equals(comment1));
  }

  @Test
  public void should_test_equals_transitivity() {
    Comment comment1 = new Comment("Test body", "user123", "article456");
    Comment comment2 = new Comment("Different body", "user456", "article789");
    Comment comment3 = new Comment("Another body", "user789", "article123");
    
    assertFalse(comment1.equals(comment2));
    assertFalse(comment2.equals(comment3));
    assertFalse(comment1.equals(comment3));
  }
}
