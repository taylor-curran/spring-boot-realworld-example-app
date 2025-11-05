package io.spring.core.comment;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class CommentTest {

  @Test
  public void should_create_comment_with_valid_parameters() {
    String body = "This is a test comment";
    String userId = "user123";
    String articleId = "article456";

    Comment comment = new Comment(body, userId, articleId);

    assertThat(comment.getBody()).isEqualTo(body);
    assertThat(comment.getUserId()).isEqualTo(userId);
    assertThat(comment.getArticleId()).isEqualTo(articleId);
    assertThat(comment.getId()).isNotNull();
    assertThat(comment.getCreatedAt()).isNotNull();
  }

  @Test
  public void should_create_comment_with_empty_body() {
    String body = "";
    String userId = "user123";
    String articleId = "article456";

    Comment comment = new Comment(body, userId, articleId);

    assertThat(comment.getBody()).isEqualTo(body);
    assertThat(comment.getUserId()).isEqualTo(userId);
    assertThat(comment.getArticleId()).isEqualTo(articleId);
  }

  @Test
  public void should_create_comment_with_long_body() {
    String longBody = "This is a very long comment body that contains a lot of text to test how the comment entity handles longer comments that users might want to write when commenting on articles in the system. It should handle this gracefully without any issues.";
    String userId = "user123";
    String articleId = "article456";

    Comment comment = new Comment(longBody, userId, articleId);

    assertThat(comment.getBody()).isEqualTo(longBody);
    assertThat(comment.getUserId()).isEqualTo(userId);
    assertThat(comment.getArticleId()).isEqualTo(articleId);
  }

  @Test
  public void should_handle_special_characters_in_body() {
    String body = "Comment with special chars: !@#$%^&*() and unicode: 中文";
    String userId = "user123";
    String articleId = "article456";

    Comment comment = new Comment(body, userId, articleId);

    assertThat(comment.getBody()).isEqualTo(body);
  }

  @Test
  public void should_handle_multiline_body() {
    String body = "This is a multiline comment.\nIt has multiple lines.\nAnd should work properly.";
    String userId = "user123";
    String articleId = "article456";

    Comment comment = new Comment(body, userId, articleId);

    assertThat(comment.getBody()).isEqualTo(body);
    assertThat(comment.getBody()).contains("\n");
  }

  @Test
  public void should_test_equals_and_hashcode() {
    Comment comment1 = new Comment("body", "user123", "article456");
    Comment comment2 = new Comment("body", "user123", "article456");
    Comment comment3 = new Comment("different body", "user123", "article456");

    assertThat(comment1).isNotEqualTo(comment2);
    assertThat(comment1).isNotEqualTo(comment3);
    assertThat(comment1).isNotEqualTo(null);
    assertThat(comment1).isNotEqualTo("string");
    assertThat(comment1).isEqualTo(comment1);
  }

  @Test
  public void should_generate_unique_ids() {
    Comment comment1 = new Comment("body1", "user123", "article456");
    Comment comment2 = new Comment("body2", "user123", "article456");

    assertThat(comment1.getId()).isNotEqualTo(comment2.getId());
    assertThat(comment1.getId()).isNotNull();
    assertThat(comment2.getId()).isNotNull();
  }

  @Test
  public void should_handle_null_parameters() {
    String body = null;
    String userId = null;
    String articleId = null;

    Comment comment = new Comment(body, userId, articleId);

    assertThat(comment.getBody()).isNull();
    assertThat(comment.getUserId()).isNull();
    assertThat(comment.getArticleId()).isNull();
    assertThat(comment.getId()).isNotNull();
    assertThat(comment.getCreatedAt()).isNotNull();
  }

  @Test
  public void should_handle_whitespace_body() {
    String body = "   ";
    String userId = "user123";
    String articleId = "article456";

    Comment comment = new Comment(body, userId, articleId);

    assertThat(comment.getBody()).isEqualTo(body);
  }

  @Test
  public void should_create_comments_at_different_times() {
    Comment comment1 = new Comment("body1", "user123", "article456");
    
    try {
      Thread.sleep(1);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    
    Comment comment2 = new Comment("body2", "user123", "article456");

    assertThat(comment2.getCreatedAt().getMillis()).isGreaterThanOrEqualTo(comment1.getCreatedAt().getMillis());
  }

  @Test
  public void should_handle_html_content_in_body() {
    String body = "<p>This is HTML content</p><script>alert('test')</script>";
    String userId = "user123";
    String articleId = "article456";

    Comment comment = new Comment(body, userId, articleId);

    assertThat(comment.getBody()).isEqualTo(body);
  }

  @Test
  public void should_handle_markdown_content_in_body() {
    String body = "# Header\n\n**Bold text** and *italic text*\n\n- List item 1\n- List item 2";
    String userId = "user123";
    String articleId = "article456";

    Comment comment = new Comment(body, userId, articleId);

    assertThat(comment.getBody()).isEqualTo(body);
  }

  @Test
  public void should_create_comment_with_no_args_constructor() {
    Comment comment = new Comment();
    
    assertThat(comment.getId()).isNull();
    assertThat(comment.getBody()).isNull();
    assertThat(comment.getUserId()).isNull();
    assertThat(comment.getArticleId()).isNull();
    assertThat(comment.getCreatedAt()).isNull();
  }


  @Test
  public void should_handle_can_equal_method() {
    Comment comment = new Comment("body", "user123", "article456");
    
    assertThat(comment.canEqual(comment)).isTrue();
    assertThat(comment.canEqual(new Comment())).isTrue();
    assertThat(comment.canEqual("not a comment")).isFalse();
    assertThat(comment.canEqual(null)).isFalse();
  }

  @Test
  public void should_handle_hash_code_consistency() {
    Comment comment1 = new Comment("body", "user123", "article456");
    Comment comment2 = new Comment("body", "user123", "article456");
    
    assertThat(comment1.hashCode()).isNotEqualTo(comment2.hashCode());
    
    int hash1 = comment1.hashCode();
    int hash2 = comment1.hashCode();
    assertThat(hash1).isEqualTo(hash2);
  }

  @Test
  public void should_handle_equals_with_same_id() {
    Comment comment1 = new Comment("body1", "user123", "article456");
    Comment comment2 = new Comment("body2", "user789", "article789");
    
    java.lang.reflect.Field idField;
    try {
      idField = Comment.class.getDeclaredField("id");
      idField.setAccessible(true);
      String sameId = "same-id-123";
      idField.set(comment1, sameId);
      idField.set(comment2, sameId);
      
      assertThat(comment1).isEqualTo(comment2);
      assertThat(comment1.hashCode()).isEqualTo(comment2.hashCode());
    } catch (Exception e) {
      assertThat(comment1).isNotEqualTo(comment2);
    }
  }

  @Test
  public void should_handle_equals_edge_cases() {
    Comment comment = new Comment("body", "user123", "article456");
    Comment nullIdComment = new Comment();
    
    assertThat(comment.equals(comment)).isTrue();
    assertThat(comment.equals(null)).isFalse();
    assertThat(comment.equals("string")).isFalse();
    assertThat(comment.equals(new Object())).isFalse();
    
    assertThat(nullIdComment.equals(nullIdComment)).isTrue();
    assertThat(nullIdComment.equals(comment)).isFalse();
    assertThat(comment.equals(nullIdComment)).isFalse();
  }

  @Test
  public void should_handle_two_null_id_comments() {
    Comment comment1 = new Comment();
    Comment comment2 = new Comment();
    
    assertThat(comment1).isEqualTo(comment2);
    assertThat(comment1.hashCode()).isEqualTo(comment2.hashCode());
  }

  @Test
  public void should_handle_very_long_content() {
    String veryLongBody = "a".repeat(10000);
    String veryLongUserId = "user-" + "b".repeat(1000);
    String veryLongArticleId = "article-" + "c".repeat(1000);
    
    Comment comment = new Comment(veryLongBody, veryLongUserId, veryLongArticleId);
    
    assertThat(comment.getBody()).isEqualTo(veryLongBody);
    assertThat(comment.getUserId()).isEqualTo(veryLongUserId);
    assertThat(comment.getArticleId()).isEqualTo(veryLongArticleId);
    assertThat(comment.getId()).isNotNull();
    assertThat(comment.getCreatedAt()).isNotNull();
  }

  @Test
  public void should_handle_unicode_content() {
    String unicodeBody = "评论内容 🚀 ñáéíóú";
    String unicodeUserId = "用户123";
    String unicodeArticleId = "文章456";
    
    Comment comment = new Comment(unicodeBody, unicodeUserId, unicodeArticleId);
    
    assertThat(comment.getBody()).isEqualTo(unicodeBody);
    assertThat(comment.getUserId()).isEqualTo(unicodeUserId);
    assertThat(comment.getArticleId()).isEqualTo(unicodeArticleId);
  }

  @Test
  public void should_handle_json_like_content() {
    String jsonBody = "{\"message\": \"This is a JSON-like comment\", \"rating\": 5}";
    String userId = "user123";
    String articleId = "article456";
    
    Comment comment = new Comment(jsonBody, userId, articleId);
    
    assertThat(comment.getBody()).isEqualTo(jsonBody);
  }
}
