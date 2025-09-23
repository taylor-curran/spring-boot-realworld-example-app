package io.spring.application.data;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.application.DateTimeCursor;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

public class CommentDataTest {

  @Test
  public void should_create_comment_data_with_all_fields() {
    String id = "comment-id";
    String body = "Test comment body";
    String articleId = "article-id";
    DateTime createdAt = new DateTime();
    DateTime updatedAt = new DateTime();
    ProfileData profileData = new ProfileData("user-id", "testuser", "Test Bio", "image.jpg", false);

    CommentData commentData = new CommentData(id, body, articleId, createdAt, updatedAt, profileData);

    assertThat(commentData.getId()).isEqualTo(id);
    assertThat(commentData.getBody()).isEqualTo(body);
    assertThat(commentData.getArticleId()).isEqualTo(articleId);
    assertThat(commentData.getCreatedAt()).isEqualTo(createdAt);
    assertThat(commentData.getUpdatedAt()).isEqualTo(updatedAt);
    assertThat(commentData.getProfileData()).isEqualTo(profileData);
  }

  @Test
  public void should_create_comment_data_with_no_args_constructor() {
    CommentData commentData = new CommentData();
    
    assertThat(commentData.getId()).isNull();
    assertThat(commentData.getBody()).isNull();
    assertThat(commentData.getArticleId()).isNull();
    assertThat(commentData.getCreatedAt()).isNull();
    assertThat(commentData.getUpdatedAt()).isNull();
    assertThat(commentData.getProfileData()).isNull();
  }

  @Test
  public void should_set_and_get_all_fields() {
    CommentData commentData = new CommentData();
    String id = "test-id";
    String body = "Test body";
    String articleId = "test-article-id";
    DateTime createdAt = new DateTime();
    DateTime updatedAt = new DateTime();
    ProfileData profileData = new ProfileData("author-id", "author", "Author Bio", "author.jpg", true);

    commentData.setId(id);
    commentData.setBody(body);
    commentData.setArticleId(articleId);
    commentData.setCreatedAt(createdAt);
    commentData.setUpdatedAt(updatedAt);
    commentData.setProfileData(profileData);

    assertThat(commentData.getId()).isEqualTo(id);
    assertThat(commentData.getBody()).isEqualTo(body);
    assertThat(commentData.getArticleId()).isEqualTo(articleId);
    assertThat(commentData.getCreatedAt()).isEqualTo(createdAt);
    assertThat(commentData.getUpdatedAt()).isEqualTo(updatedAt);
    assertThat(commentData.getProfileData()).isEqualTo(profileData);
  }

  @Test
  public void should_return_cursor_based_on_created_at() {
    DateTime createdAt = new DateTime();
    CommentData commentData = new CommentData();
    commentData.setCreatedAt(createdAt);

    DateTimeCursor cursor = commentData.getCursor();

    assertThat(cursor).isNotNull();
    assertThat(cursor.getData()).isEqualTo(createdAt);
  }

  @Test
  public void should_handle_null_created_at_in_cursor() {
    CommentData commentData = new CommentData();
    commentData.setCreatedAt(null);

    DateTimeCursor cursor = commentData.getCursor();

    assertThat(cursor).isNotNull();
    assertThat(cursor.getData()).isNull();
  }

  @Test
  public void should_handle_profile_data_relationship() {
    CommentData commentData = new CommentData();
    ProfileData profileData = new ProfileData("author-id", "author", "Author Bio", "author.jpg", true);
    
    commentData.setProfileData(profileData);
    
    assertThat(commentData.getProfileData()).isNotNull();
    assertThat(commentData.getProfileData().getUsername()).isEqualTo("author");
    assertThat(commentData.getProfileData().isFollowing()).isTrue();
  }

  @Test
  public void should_handle_date_operations() {
    CommentData commentData = new CommentData();
    DateTime createdAt = new DateTime();
    DateTime updatedAt = createdAt.plusHours(1);
    
    commentData.setCreatedAt(createdAt);
    commentData.setUpdatedAt(updatedAt);
    
    assertThat(commentData.getCreatedAt()).isEqualTo(createdAt);
    assertThat(commentData.getUpdatedAt()).isEqualTo(updatedAt);
    assertThat(commentData.getUpdatedAt().isAfter(commentData.getCreatedAt())).isTrue();
  }

  @Test
  public void should_handle_equals_and_hashcode() {
    DateTime now = new DateTime();
    ProfileData profileData = new ProfileData("user-id", "testuser", "Bio", "image.jpg", false);
    
    CommentData commentData1 = new CommentData("id", "body", "article-id", now, now, profileData);
    CommentData commentData2 = new CommentData("id", "body", "article-id", now, now, profileData);

    assertThat(commentData1).isEqualTo(commentData2);
    assertThat(commentData1.hashCode()).isEqualTo(commentData2.hashCode());
  }

  @Test
  public void should_handle_toString() {
    CommentData commentData = new CommentData();
    commentData.setId("test-id");
    commentData.setBody("Test comment");

    String toString = commentData.toString();

    assertThat(toString).contains("CommentData");
    assertThat(toString).contains("test-id");
    assertThat(toString).contains("Test comment");
  }
}
