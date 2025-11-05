package io.spring.application.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

public class ArticleDataComprehensiveEqualsTest {

  @Test
  void should_handle_equals_with_different_object_types() {
    ArticleData article = createSampleArticleData();
    
    assertThat(article.equals(null)).isFalse();
    assertThat(article.equals("not an article")).isFalse();
    assertThat(article.equals(new Object())).isFalse();
  }

  @Test
  void should_handle_equals_with_all_null_fields() {
    ArticleData article1 = new ArticleData(null, null, null, null, null, false, 0, null, null, null, null);
    ArticleData article2 = new ArticleData(null, null, null, null, null, false, 0, null, null, null, null);
    ArticleData article3 = new ArticleData("id", null, null, null, null, false, 0, null, null, null, null);
    
    assertThat(article1).isEqualTo(article2);
    assertThat(article1).isNotEqualTo(article3);
    assertThat(article1.hashCode()).isEqualTo(article2.hashCode());
  }

  @Test
  void should_handle_equals_with_different_primitive_fields() {
    DateTime now = DateTime.now();
    ProfileData profile = createSampleProfileData();
    
    ArticleData article1 = new ArticleData("id", "slug", "title", "desc", "body", true, 5, now, now, Arrays.asList("tag1"), profile);
    ArticleData article2 = new ArticleData("id", "slug", "title", "desc", "body", false, 5, now, now, Arrays.asList("tag1"), profile);
    ArticleData article3 = new ArticleData("id", "slug", "title", "desc", "body", true, 10, now, now, Arrays.asList("tag1"), profile);
    
    assertThat(article1).isNotEqualTo(article2);
    assertThat(article1).isNotEqualTo(article3);
  }

  @Test
  void should_handle_equals_with_different_datetime_fields() {
    DateTime now = DateTime.now();
    DateTime later = now.plusMinutes(30);
    ProfileData profile = createSampleProfileData();
    
    ArticleData article1 = new ArticleData("id", "slug", "title", "desc", "body", false, 0, now, now, Arrays.asList("tag1"), profile);
    ArticleData article2 = new ArticleData("id", "slug", "title", "desc", "body", false, 0, later, now, Arrays.asList("tag1"), profile);
    ArticleData article3 = new ArticleData("id", "slug", "title", "desc", "body", false, 0, now, later, Arrays.asList("tag1"), profile);
    
    assertThat(article1).isNotEqualTo(article2);
    assertThat(article1).isNotEqualTo(article3);
  }

  @Test
  void should_handle_equals_with_different_tag_lists() {
    DateTime now = DateTime.now();
    ProfileData profile = createSampleProfileData();
    
    ArticleData article1 = new ArticleData("id", "slug", "title", "desc", "body", false, 0, now, now, Arrays.asList("tag1", "tag2"), profile);
    ArticleData article2 = new ArticleData("id", "slug", "title", "desc", "body", false, 0, now, now, Arrays.asList("tag1"), profile);
    ArticleData article3 = new ArticleData("id", "slug", "title", "desc", "body", false, 0, now, now, Collections.emptyList(), profile);
    ArticleData article4 = new ArticleData("id", "slug", "title", "desc", "body", false, 0, now, now, null, profile);
    
    assertThat(article1).isNotEqualTo(article2);
    assertThat(article1).isNotEqualTo(article3);
    assertThat(article1).isNotEqualTo(article4);
    assertThat(article3).isNotEqualTo(article4);
  }

  @Test
  void should_handle_equals_with_different_profile_data() {
    DateTime now = DateTime.now();
    ProfileData profile1 = new ProfileData("id1", "user1", "bio1", "image1", false);
    ProfileData profile2 = new ProfileData("id2", "user2", "bio2", "image2", true);
    
    ArticleData article1 = new ArticleData("id", "slug", "title", "desc", "body", false, 0, now, now, Arrays.asList("tag1"), profile1);
    ArticleData article2 = new ArticleData("id", "slug", "title", "desc", "body", false, 0, now, now, Arrays.asList("tag1"), profile2);
    ArticleData article3 = new ArticleData("id", "slug", "title", "desc", "body", false, 0, now, now, Arrays.asList("tag1"), null);
    
    assertThat(article1).isNotEqualTo(article2);
    assertThat(article1).isNotEqualTo(article3);
  }

  @Test
  void should_handle_equals_with_same_reference() {
    ArticleData article = createSampleArticleData();
    
    assertThat(article.equals(article)).isTrue();
    assertThat(article.hashCode()).isEqualTo(article.hashCode());
  }

  @Test
  void should_handle_equals_with_identical_content() {
    ArticleData article1 = createSampleArticleData();
    ArticleData article2 = createSampleArticleData();
    
    assertThat(article1).isEqualTo(article2);
    assertThat(article1.hashCode()).isEqualTo(article2.hashCode());
  }

  private ArticleData createSampleArticleData() {
    DateTime now = DateTime.now();
    ProfileData profile = createSampleProfileData();
    return new ArticleData("id", "slug", "title", "desc", "body", false, 0, now, now, Arrays.asList("tag1"), profile);
  }

  private ProfileData createSampleProfileData() {
    return new ProfileData("profile-id", "testuser", "Test Bio", "image.jpg", false);
  }
}
