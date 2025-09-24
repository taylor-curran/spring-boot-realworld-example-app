package io.spring.application.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

public class DataDTOsHashCodeEdgeCasesTest {

  @Test
  void should_test_articledata_hashcode_consistency() {
    DateTime now = DateTime.now();
    ProfileData profile = new ProfileData("id", "user", "bio", "image", false);
    
    ArticleData article = new ArticleData("id", "slug", "title", "desc", "body", false, 0, now, now, Arrays.asList("tag"), profile);
    
    int hashCode1 = article.hashCode();
    int hashCode2 = article.hashCode();
    
    assertThat(hashCode1).isEqualTo(hashCode2);
  }

  @Test
  void should_test_articledata_hashcode_with_null_fields() {
    ArticleData article1 = new ArticleData(null, null, null, null, null, false, 0, null, null, null, null);
    ArticleData article2 = new ArticleData(null, null, null, null, null, false, 0, null, null, null, null);
    
    assertThat(article1.hashCode()).isEqualTo(article2.hashCode());
  }

  @Test
  void should_test_commentdata_hashcode_consistency() {
    DateTime now = DateTime.now();
    ProfileData profile = new ProfileData("id", "user", "bio", "image", false);
    
    CommentData comment = new CommentData("id", "body", "article1", now, now, profile);
    
    int hashCode1 = comment.hashCode();
    int hashCode2 = comment.hashCode();
    
    assertThat(hashCode1).isEqualTo(hashCode2);
  }

  @Test
  void should_test_commentdata_hashcode_with_null_fields() {
    CommentData comment1 = new CommentData(null, null, null, null, null, null);
    CommentData comment2 = new CommentData(null, null, null, null, null, null);
    
    assertThat(comment1.hashCode()).isEqualTo(comment2.hashCode());
  }

  @Test
  void should_test_userdata_hashcode_consistency() {
    UserData user = new UserData("id", "email", "username", "bio", "image");
    
    int hashCode1 = user.hashCode();
    int hashCode2 = user.hashCode();
    
    assertThat(hashCode1).isEqualTo(hashCode2);
  }

  @Test
  void should_test_userdata_hashcode_with_null_fields() {
    UserData user1 = new UserData(null, null, null, null, null);
    UserData user2 = new UserData(null, null, null, null, null);
    
    assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
  }

  @Test
  void should_test_profiledata_hashcode_consistency() {
    ProfileData profile = new ProfileData("id", "username", "bio", "image", true);
    
    int hashCode1 = profile.hashCode();
    int hashCode2 = profile.hashCode();
    
    assertThat(hashCode1).isEqualTo(hashCode2);
  }

  @Test
  void should_test_profiledata_hashcode_with_null_fields() {
    ProfileData profile1 = new ProfileData(null, null, null, null, false);
    ProfileData profile2 = new ProfileData(null, null, null, null, false);
    
    assertThat(profile1.hashCode()).isEqualTo(profile2.hashCode());
  }

  @Test
  void should_test_different_objects_have_different_hashcodes() {
    DateTime now = DateTime.now();
    ProfileData profile = new ProfileData("id", "user", "bio", "image", false);
    
    ArticleData article1 = new ArticleData("id1", "slug", "title", "desc", "body", false, 0, now, now, Arrays.asList("tag"), profile);
    ArticleData article2 = new ArticleData("id2", "slug", "title", "desc", "body", false, 0, now, now, Arrays.asList("tag"), profile);
    
    assertThat(article1.hashCode()).isNotEqualTo(article2.hashCode());
  }

  @Test
  void should_test_articledata_hashcode_with_different_collections() {
    DateTime now = DateTime.now();
    ProfileData profile = new ProfileData("id", "user", "bio", "image", false);
    
    ArticleData article1 = new ArticleData("id", "slug", "title", "desc", "body", false, 0, now, now, Arrays.asList("tag1"), profile);
    ArticleData article2 = new ArticleData("id", "slug", "title", "desc", "body", false, 0, now, now, Arrays.asList("tag2"), profile);
    ArticleData article3 = new ArticleData("id", "slug", "title", "desc", "body", false, 0, now, now, Collections.emptyList(), profile);
    ArticleData article4 = new ArticleData("id", "slug", "title", "desc", "body", false, 0, now, now, null, profile);
    
    assertThat(article1.hashCode()).isNotEqualTo(article2.hashCode());
    assertThat(article1.hashCode()).isNotEqualTo(article3.hashCode());
    assertThat(article1.hashCode()).isNotEqualTo(article4.hashCode());
    assertThat(article3.hashCode()).isNotEqualTo(article4.hashCode());
  }
}
