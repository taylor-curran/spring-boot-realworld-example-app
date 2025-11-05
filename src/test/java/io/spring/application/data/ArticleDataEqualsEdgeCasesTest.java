package io.spring.application.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

public class ArticleDataEqualsEdgeCasesTest {

  @Test
  void should_handle_equals_with_same_instance() {
    ArticleData article = createSampleArticleData();

    assertThat(article.equals(article)).isTrue();
    assertThat(article.hashCode()).isEqualTo(article.hashCode());
  }

  @Test
  void should_handle_equals_with_different_tag_lists() {
    DateTime now = DateTime.now();
    ProfileData profile = new ProfileData("user-id", "testuser", "Bio", "image.jpg", false);

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
    ArticleData article3 =
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
            Arrays.asList("tag1"),
            profile);

    assertThat(article1).isNotEqualTo(article2);
    assertThat(article1).isNotEqualTo(article3);
  }

  @Test
  void should_handle_equals_with_empty_tag_lists() {
    DateTime now = DateTime.now();
    ProfileData profile = new ProfileData("user-id", "testuser", "Bio", "image.jpg", false);

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
            Collections.emptyList(),
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
            Collections.emptyList(),
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
            now,
            now,
            Arrays.asList("tag1"),
            profile);

    assertThat(article1).isEqualTo(article2);
    assertThat(article1).isNotEqualTo(article3);
  }

  @Test
  void should_handle_equals_with_different_favorite_counts() {
    DateTime now = DateTime.now();
    ProfileData profile = new ProfileData("user-id", "testuser", "Bio", "image.jpg", false);

    ArticleData article1 =
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
            Arrays.asList("tag1"),
            profile);
    ArticleData article2 =
        new ArticleData(
            "id",
            "slug",
            "title",
            "desc",
            "body",
            false,
            10,
            now,
            now,
            Arrays.asList("tag1"),
            profile);

    assertThat(article1).isNotEqualTo(article2);
  }

  @Test
  void should_handle_equals_with_different_favorited_status() {
    DateTime now = DateTime.now();
    ProfileData profile = new ProfileData("user-id", "testuser", "Bio", "image.jpg", false);

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
            Arrays.asList("tag1"),
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
            Arrays.asList("tag1"),
            profile);

    assertThat(article1).isNotEqualTo(article2);
  }

  private ArticleData createSampleArticleData() {
    DateTime now = DateTime.now();
    ProfileData profile = new ProfileData("user-id", "testuser", "Test Bio", "image.jpg", false);
    return new ArticleData(
        "id", "slug", "title", "desc", "body", false, 0, now, now, Arrays.asList("tag1"), profile);
  }
}
