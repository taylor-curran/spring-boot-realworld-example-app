package io.spring.application.data;

import static org.assertj.core.api.Assertions.assertThat;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

public class CommentDataEqualsEdgeCasesTest {

  @Test
  void should_handle_equals_with_same_instance() {
    CommentData comment = createSampleCommentData();

    assertThat(comment.equals(comment)).isTrue();
    assertThat(comment.hashCode()).isEqualTo(comment.hashCode());
  }

  @Test
  void should_handle_equals_with_mixed_null_fields() {
    DateTime now = DateTime.now();
    ProfileData profile = new ProfileData("user-id", "testuser", "Bio", "image.jpg", false);

    CommentData comment1 = new CommentData("id", null, "article", now, null, profile);
    CommentData comment2 = new CommentData("id", null, "article", now, null, profile);
    CommentData comment3 = new CommentData("id", "body", "article", now, null, profile);

    assertThat(comment1).isEqualTo(comment2);
    assertThat(comment1).isNotEqualTo(comment3);
  }

  @Test
  void should_handle_equals_with_null_profile_data() {
    DateTime now = DateTime.now();

    CommentData comment1 = new CommentData("id", "body", "article", now, now, null);
    CommentData comment2 = new CommentData("id", "body", "article", now, now, null);
    ProfileData profile = new ProfileData("user-id", "testuser", "Bio", "image.jpg", false);
    CommentData comment3 = new CommentData("id", "body", "article", now, now, profile);

    assertThat(comment1).isEqualTo(comment2);
    assertThat(comment1).isNotEqualTo(comment3);
  }

  @Test
  void should_handle_equals_with_different_updated_at() {
    DateTime now = DateTime.now();
    DateTime later = now.plusMinutes(30);
    ProfileData profile = new ProfileData("user-id", "testuser", "Bio", "image.jpg", false);

    CommentData comment1 = new CommentData("id", "body", "article", now, now, profile);
    CommentData comment2 = new CommentData("id", "body", "article", now, later, profile);

    assertThat(comment1).isNotEqualTo(comment2);
  }

  @Test
  void should_handle_equals_with_null_updated_at() {
    DateTime now = DateTime.now();
    ProfileData profile = new ProfileData("user-id", "testuser", "Bio", "image.jpg", false);

    CommentData comment1 = new CommentData("id", "body", "article", now, null, profile);
    CommentData comment2 = new CommentData("id", "body", "article", now, null, profile);
    CommentData comment3 = new CommentData("id", "body", "article", now, now, profile);

    assertThat(comment1).isEqualTo(comment2);
    assertThat(comment1).isNotEqualTo(comment3);
  }

  private CommentData createSampleCommentData() {
    DateTime now = DateTime.now();
    ProfileData profile = new ProfileData("user-id", "testuser", "Test Bio", "image.jpg", false);
    return new CommentData("id", "body", "article-id", now, now, profile);
  }
}
