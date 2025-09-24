package io.spring.application.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

public class DataDTOsComprehensiveEqualsTest {

  @Test
  void should_test_articledata_equals_with_all_null_combinations() {
    ArticleData article1 = new ArticleData(null, null, null, null, null, false, 0, null, null, null, null);
    ArticleData article2 = new ArticleData(null, null, null, null, null, false, 0, null, null, null, null);
    ArticleData article3 = new ArticleData("id", null, null, null, null, false, 0, null, null, null, null);
    
    assertThat(article1).isEqualTo(article2);
    assertThat(article1).isNotEqualTo(article3);
    assertThat(article1.hashCode()).isEqualTo(article2.hashCode());
  }

  @Test
  void should_test_articledata_equals_with_mixed_null_fields() {
    DateTime now = DateTime.now();
    ProfileData profile = new ProfileData("id", "user", "bio", "image", false);
    
    ArticleData article1 = new ArticleData("id", null, "title", null, "body", false, 0, now, null, Arrays.asList("tag"), null);
    ArticleData article2 = new ArticleData("id", null, "title", null, "body", false, 0, now, null, Arrays.asList("tag"), null);
    ArticleData article3 = new ArticleData("id", "slug", "title", null, "body", false, 0, now, null, Arrays.asList("tag"), null);
    
    assertThat(article1).isEqualTo(article2);
    assertThat(article1).isNotEqualTo(article3);
  }

  @Test
  void should_test_commentdata_equals_with_all_null_combinations() {
    CommentData comment1 = new CommentData(null, null, null, null, null, null);
    CommentData comment2 = new CommentData(null, null, null, null, null, null);
    CommentData comment3 = new CommentData("id", null, null, null, null, null);
    
    assertThat(comment1).isEqualTo(comment2);
    assertThat(comment1).isNotEqualTo(comment3);
    assertThat(comment1.hashCode()).isEqualTo(comment2.hashCode());
  }

  @Test
  void should_test_commentdata_equals_with_mixed_null_fields() {
    DateTime now = DateTime.now();
    ProfileData profile = new ProfileData("id", "user", "bio", "image", false);
    
    CommentData comment1 = new CommentData("id", null, "article1", now, null, profile);
    CommentData comment2 = new CommentData("id", null, "article1", now, null, profile);
    CommentData comment3 = new CommentData("id", "body", "article1", now, null, profile);
    
    assertThat(comment1).isEqualTo(comment2);
    assertThat(comment1).isNotEqualTo(comment3);
  }

  @Test
  void should_test_userdata_equals_with_all_null_combinations() {
    UserData user1 = new UserData(null, null, null, null, null);
    UserData user2 = new UserData(null, null, null, null, null);
    UserData user3 = new UserData("id", null, null, null, null);
    
    assertThat(user1).isEqualTo(user2);
    assertThat(user1).isNotEqualTo(user3);
    assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
  }

  @Test
  void should_test_userdata_equals_with_mixed_null_fields() {
    UserData user1 = new UserData("id", null, "username", "bio", null);
    UserData user2 = new UserData("id", null, "username", "bio", null);
    UserData user3 = new UserData("id", "email", "username", "bio", null);
    
    assertThat(user1).isEqualTo(user2);
    assertThat(user1).isNotEqualTo(user3);
  }

  @Test
  void should_test_profiledata_equals_with_all_null_combinations() {
    ProfileData profile1 = new ProfileData(null, null, null, null, false);
    ProfileData profile2 = new ProfileData(null, null, null, null, false);
    ProfileData profile3 = new ProfileData("id", null, null, null, false);
    
    assertThat(profile1).isEqualTo(profile2);
    assertThat(profile1).isNotEqualTo(profile3);
    assertThat(profile1.hashCode()).isEqualTo(profile2.hashCode());
  }

  @Test
  void should_test_profiledata_equals_with_mixed_null_fields() {
    ProfileData profile1 = new ProfileData("id", null, "bio", null, true);
    ProfileData profile2 = new ProfileData("id", null, "bio", null, true);
    ProfileData profile3 = new ProfileData("id", "username", "bio", null, true);
    
    assertThat(profile1).isEqualTo(profile2);
    assertThat(profile1).isNotEqualTo(profile3);
  }

  @Test
  void should_test_profiledata_equals_with_different_boolean_values() {
    ProfileData profile1 = new ProfileData("id", "user", "bio", "image", true);
    ProfileData profile2 = new ProfileData("id", "user", "bio", "image", false);
    
    assertThat(profile1).isNotEqualTo(profile2);
  }

  @Test
  void should_test_articledata_equals_with_empty_vs_null_collections() {
    DateTime now = DateTime.now();
    ProfileData profile = new ProfileData("id", "user", "bio", "image", false);
    
    ArticleData article1 = new ArticleData("id", "slug", "title", "desc", "body", false, 0, now, now, null, profile);
    ArticleData article2 = new ArticleData("id", "slug", "title", "desc", "body", false, 0, now, now, Collections.emptyList(), profile);
    
    assertThat(article1).isNotEqualTo(article2);
  }

  @Test
  void should_test_all_dtos_equals_with_different_object_types() {
    ArticleData article = new ArticleData("id", "slug", "title", "desc", "body", false, 0, DateTime.now(), DateTime.now(), Arrays.asList("tag"), null);
    CommentData comment = new CommentData("id", "body", "article1", DateTime.now(), DateTime.now(), null);
    UserData user = new UserData("id", "email", "username", "bio", "image");
    ProfileData profile = new ProfileData("id", "username", "bio", "image", false);
    
    assertThat(article.equals(null)).isFalse();
    assertThat(article.equals("string")).isFalse();
    assertThat(article.equals(comment)).isFalse();
    
    assertThat(comment.equals(null)).isFalse();
    assertThat(comment.equals("string")).isFalse();
    assertThat(comment.equals(user)).isFalse();
    
    assertThat(user.equals(null)).isFalse();
    assertThat(user.equals("string")).isFalse();
    assertThat(user.equals(profile)).isFalse();
    
    assertThat(profile.equals(null)).isFalse();
    assertThat(profile.equals("string")).isFalse();
    assertThat(profile.equals(article)).isFalse();
  }
}
