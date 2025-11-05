package io.spring.application.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

public class DataDTOsUltimateCoverageTest {

  @Test
  void should_test_articledata_canEqual_method() {
    DateTime now = DateTime.now();
    ProfileData profile = new ProfileData("id", "user", "bio", "image", false);
    ArticleData article = new ArticleData("id", "slug", "title", "desc", "body", false, 0, now, now, Arrays.asList("tag"), profile);
    
    assertThat(article.canEqual(article)).isTrue();
    assertThat(article.canEqual(new ArticleData("id", "slug", "title", "desc", "body", false, 0, now, now, Arrays.asList("tag"), profile))).isTrue();
    assertThat(article.canEqual("string")).isFalse();
    assertThat(article.canEqual(null)).isFalse();
  }

  @Test
  void should_test_commentdata_canEqual_method() {
    DateTime now = DateTime.now();
    ProfileData profile = new ProfileData("id", "user", "bio", "image", false);
    CommentData comment = new CommentData("id", "body", "article1", now, now, profile);
    
    assertThat(comment.canEqual(comment)).isTrue();
    assertThat(comment.canEqual(new CommentData("id", "body", "article1", now, now, profile))).isTrue();
    assertThat(comment.canEqual("string")).isFalse();
    assertThat(comment.canEqual(null)).isFalse();
  }

  @Test
  void should_test_userdata_canEqual_method() {
    UserData user = new UserData("id", "email", "username", "bio", "image");
    
    assertThat(user.canEqual(user)).isTrue();
    assertThat(user.canEqual(new UserData("id", "email", "username", "bio", "image"))).isTrue();
    assertThat(user.canEqual("string")).isFalse();
    assertThat(user.canEqual(null)).isFalse();
  }

  @Test
  void should_test_profiledata_canEqual_method() {
    ProfileData profile = new ProfileData("id", "username", "bio", "image", true);
    
    assertThat(profile.canEqual(profile)).isTrue();
    assertThat(profile.canEqual(new ProfileData("id", "username", "bio", "image", true))).isTrue();
    assertThat(profile.canEqual("string")).isFalse();
    assertThat(profile.canEqual(null)).isFalse();
  }

  @Test
  void should_test_articledata_equals_with_canEqual_false() {
    DateTime now = DateTime.now();
    ProfileData profile = new ProfileData("id", "user", "bio", "image", false);
    ArticleData article = new ArticleData("id", "slug", "title", "desc", "body", false, 0, now, now, Arrays.asList("tag"), profile);
    
    Object other = new Object() {
      @Override
      public boolean equals(Object obj) {
        return obj instanceof ArticleData;
      }
    };
    
    assertThat(article.equals(other)).isFalse();
  }

  @Test
  void should_test_commentdata_equals_with_canEqual_false() {
    DateTime now = DateTime.now();
    ProfileData profile = new ProfileData("id", "user", "bio", "image", false);
    CommentData comment = new CommentData("id", "body", "article1", now, now, profile);
    
    Object other = new Object() {
      @Override
      public boolean equals(Object obj) {
        return obj instanceof CommentData;
      }
    };
    
    assertThat(comment.equals(other)).isFalse();
  }

  @Test
  void should_test_userdata_equals_with_canEqual_false() {
    UserData user = new UserData("id", "email", "username", "bio", "image");
    
    Object other = new Object() {
      @Override
      public boolean equals(Object obj) {
        return obj instanceof UserData;
      }
    };
    
    assertThat(user.equals(other)).isFalse();
  }

  @Test
  void should_test_profiledata_equals_with_canEqual_false() {
    ProfileData profile = new ProfileData("id", "username", "bio", "image", true);
    
    Object other = new Object() {
      @Override
      public boolean equals(Object obj) {
        return obj instanceof ProfileData;
      }
    };
    
    assertThat(profile.equals(other)).isFalse();
  }

  @Test
  void should_test_articledata_equals_with_different_field_combinations() {
    DateTime now1 = DateTime.now();
    DateTime now2 = now1.plusMillis(1);
    ProfileData profile1 = new ProfileData("id1", "user", "bio", "image", false);
    ProfileData profile2 = new ProfileData("id2", "user", "bio", "image", false);
    
    ArticleData base = new ArticleData("id", "slug", "title", "desc", "body", false, 0, now1, now1, Arrays.asList("tag"), profile1);
    
    assertThat(base).isNotEqualTo(new ArticleData("id2", "slug", "title", "desc", "body", false, 0, now1, now1, Arrays.asList("tag"), profile1));
    assertThat(base).isNotEqualTo(new ArticleData("id", "slug2", "title", "desc", "body", false, 0, now1, now1, Arrays.asList("tag"), profile1));
    assertThat(base).isNotEqualTo(new ArticleData("id", "slug", "title2", "desc", "body", false, 0, now1, now1, Arrays.asList("tag"), profile1));
    assertThat(base).isNotEqualTo(new ArticleData("id", "slug", "title", "desc2", "body", false, 0, now1, now1, Arrays.asList("tag"), profile1));
    assertThat(base).isNotEqualTo(new ArticleData("id", "slug", "title", "desc", "body2", false, 0, now1, now1, Arrays.asList("tag"), profile1));
    assertThat(base).isNotEqualTo(new ArticleData("id", "slug", "title", "desc", "body", true, 0, now1, now1, Arrays.asList("tag"), profile1));
    assertThat(base).isNotEqualTo(new ArticleData("id", "slug", "title", "desc", "body", false, 1, now1, now1, Arrays.asList("tag"), profile1));
    assertThat(base).isNotEqualTo(new ArticleData("id", "slug", "title", "desc", "body", false, 0, now2, now1, Arrays.asList("tag"), profile1));
    assertThat(base).isNotEqualTo(new ArticleData("id", "slug", "title", "desc", "body", false, 0, now1, now2, Arrays.asList("tag"), profile1));
    assertThat(base).isNotEqualTo(new ArticleData("id", "slug", "title", "desc", "body", false, 0, now1, now1, Arrays.asList("tag2"), profile1));
    assertThat(base).isNotEqualTo(new ArticleData("id", "slug", "title", "desc", "body", false, 0, now1, now1, Arrays.asList("tag"), profile2));
  }
}
