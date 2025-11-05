package io.spring.application.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

public class DataDTOsFinalEqualsEdgeCasesTest {

  @Test
  void should_test_articledata_equals_reflexivity() {
    DateTime now = DateTime.now();
    ProfileData profile = new ProfileData("id", "user", "bio", "image", false);
    ArticleData article =
        new ArticleData(
            "id",
            "slug",
            "title",
            "desc",
            "body",
            false,
            0,
            now,
            now,
            Arrays.asList("tag"),
            profile);

    assertThat(article.equals(article)).isTrue();
  }

  @Test
  void should_test_articledata_equals_with_different_primitive_types() {
    DateTime now = DateTime.now();
    ProfileData profile = new ProfileData("id", "user", "bio", "image", false);

    ArticleData article1 =
        new ArticleData(
            "id",
            "slug",
            "title",
            "desc",
            "body",
            true,
            5,
            now,
            now,
            Arrays.asList("tag"),
            profile);
    ArticleData article2 =
        new ArticleData(
            "id",
            "slug",
            "title",
            "desc",
            "body",
            false,
            5,
            now,
            now,
            Arrays.asList("tag"),
            profile);
    ArticleData article3 =
        new ArticleData(
            "id",
            "slug",
            "title",
            "desc",
            "body",
            true,
            10,
            now,
            now,
            Arrays.asList("tag"),
            profile);

    assertThat(article1).isNotEqualTo(article2);
    assertThat(article1).isNotEqualTo(article3);
  }

  @Test
  void should_test_commentdata_equals_reflexivity() {
    DateTime now = DateTime.now();
    ProfileData profile = new ProfileData("id", "user", "bio", "image", false);
    CommentData comment = new CommentData("id", "body", "article1", now, now, profile);

    assertThat(comment.equals(comment)).isTrue();
  }

  @Test
  void should_test_userdata_equals_reflexivity() {
    UserData user = new UserData("id", "email", "username", "bio", "image");

    assertThat(user.equals(user)).isTrue();
  }

  @Test
  void should_test_profiledata_equals_reflexivity() {
    ProfileData profile = new ProfileData("id", "username", "bio", "image", true);

    assertThat(profile.equals(profile)).isTrue();
  }

  @Test
  void should_test_articledata_equals_with_datetime_precision() {
    DateTime now1 = DateTime.now();
    DateTime now2 = now1.plusMillis(1);
    ProfileData profile = new ProfileData("id", "user", "bio", "image", false);

    ArticleData article1 =
        new ArticleData(
            "id",
            "slug",
            "title",
            "desc",
            "body",
            false,
            0,
            now1,
            now1,
            Arrays.asList("tag"),
            profile);
    ArticleData article2 =
        new ArticleData(
            "id",
            "slug",
            "title",
            "desc",
            "body",
            false,
            0,
            now2,
            now1,
            Arrays.asList("tag"),
            profile);
    ArticleData article3 =
        new ArticleData(
            "id",
            "slug",
            "title",
            "desc",
            "body",
            false,
            0,
            now1,
            now2,
            Arrays.asList("tag"),
            profile);

    assertThat(article1).isNotEqualTo(article2);
    assertThat(article1).isNotEqualTo(article3);
  }

  @Test
  void should_test_commentdata_equals_with_datetime_precision() {
    DateTime now1 = DateTime.now();
    DateTime now2 = now1.plusMillis(1);
    ProfileData profile = new ProfileData("id", "user", "bio", "image", false);

    CommentData comment1 = new CommentData("id", "body", "article1", now1, now1, profile);
    CommentData comment2 = new CommentData("id", "body", "article1", now2, now1, profile);
    CommentData comment3 = new CommentData("id", "body", "article1", now1, now2, profile);

    assertThat(comment1).isNotEqualTo(comment2);
    assertThat(comment1).isNotEqualTo(comment3);
  }

  @Test
  void should_test_profiledata_equals_with_nested_profile_differences() {
    ProfileData profile1 = new ProfileData("id1", "user", "bio", "image", false);
    ProfileData profile2 = new ProfileData("id2", "user", "bio", "image", false);

    DateTime now = DateTime.now();
    ArticleData article1 =
        new ArticleData(
            "id",
            "slug",
            "title",
            "desc",
            "body",
            false,
            0,
            now,
            now,
            Arrays.asList("tag"),
            profile1);
    ArticleData article2 =
        new ArticleData(
            "id",
            "slug",
            "title",
            "desc",
            "body",
            false,
            0,
            now,
            now,
            Arrays.asList("tag"),
            profile2);

    assertThat(article1).isNotEqualTo(article2);
  }

  @Test
  void should_test_all_dtos_equals_symmetry() {
    DateTime now = DateTime.now();
    ProfileData profile = new ProfileData("id", "user", "bio", "image", false);

    ArticleData article1 =
        new ArticleData(
            "id",
            "slug",
            "title",
            "desc",
            "body",
            false,
            0,
            now,
            now,
            Arrays.asList("tag"),
            profile);
    ArticleData article2 =
        new ArticleData(
            "id",
            "slug",
            "title",
            "desc",
            "body",
            false,
            0,
            now,
            now,
            Arrays.asList("tag"),
            profile);

    CommentData comment1 = new CommentData("id", "body", "article1", now, now, profile);
    CommentData comment2 = new CommentData("id", "body", "article1", now, now, profile);

    UserData user1 = new UserData("id", "email", "username", "bio", "image");
    UserData user2 = new UserData("id", "email", "username", "bio", "image");

    ProfileData profile1 = new ProfileData("id", "username", "bio", "image", true);
    ProfileData profile2 = new ProfileData("id", "username", "bio", "image", true);

    assertThat(article1.equals(article2)).isEqualTo(article2.equals(article1));
    assertThat(comment1.equals(comment2)).isEqualTo(comment2.equals(comment1));
    assertThat(user1.equals(user2)).isEqualTo(user2.equals(user1));
    assertThat(profile1.equals(profile2)).isEqualTo(profile2.equals(profile1));
  }

  @Test
  void should_test_articledata_equals_with_collection_order() {
    DateTime now = DateTime.now();
    ProfileData profile = new ProfileData("id", "user", "bio", "image", false);

    ArticleData article1 =
        new ArticleData(
            "id",
            "slug",
            "title",
            "desc",
            "body",
            false,
            0,
            now,
            now,
            Arrays.asList("tag1", "tag2"),
            profile);
    ArticleData article2 =
        new ArticleData(
            "id",
            "slug",
            "title",
            "desc",
            "body",
            false,
            0,
            now,
            now,
            Arrays.asList("tag2", "tag1"),
            profile);

    assertThat(article1).isNotEqualTo(article2);
  }

  @Test
  void should_test_equals_with_subclass_instances() {
    DateTime now = DateTime.now();
    ProfileData profile = new ProfileData("id", "user", "bio", "image", false);

    ArticleData article =
        new ArticleData(
            "id",
            "slug",
            "title",
            "desc",
            "body",
            false,
            0,
            now,
            now,
            Arrays.asList("tag"),
            profile);

    Object anonymousSubclass =
        new ArticleData(
            "id",
            "slug",
            "title",
            "desc",
            "body",
            false,
            0,
            now,
            now,
            Arrays.asList("tag"),
            profile) {
          @Override
          public String toString() {
            return "anonymous subclass";
          }
        };

    assertThat(article.equals(anonymousSubclass)).isTrue();
  }
}
