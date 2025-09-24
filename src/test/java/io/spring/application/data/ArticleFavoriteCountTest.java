package io.spring.application.data;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ArticleFavoriteCountTest {

  @Test
  public void should_create_article_favorite_count_with_id_and_count() {
    ArticleFavoriteCount favoriteCount = new ArticleFavoriteCount("article123", 5);

    assertThat(favoriteCount.getId()).isEqualTo("article123");
    assertThat(favoriteCount.getCount()).isEqualTo(5);
  }

  @Test
  public void should_create_article_favorite_count_with_zero_count() {
    ArticleFavoriteCount favoriteCount = new ArticleFavoriteCount("article456", 0);

    assertThat(favoriteCount.getId()).isEqualTo("article456");
    assertThat(favoriteCount.getCount()).isEqualTo(0);
  }

  @Test
  public void should_create_article_favorite_count_with_null_id() {
    ArticleFavoriteCount favoriteCount = new ArticleFavoriteCount(null, 3);

    assertThat(favoriteCount.getId()).isNull();
    assertThat(favoriteCount.getCount()).isEqualTo(3);
  }

  @Test
  public void should_create_article_favorite_count_with_null_count() {
    ArticleFavoriteCount favoriteCount = new ArticleFavoriteCount("article789", null);

    assertThat(favoriteCount.getId()).isEqualTo("article789");
    assertThat(favoriteCount.getCount()).isNull();
  }

  @Test
  public void should_create_article_favorite_count_with_both_null() {
    ArticleFavoriteCount favoriteCount = new ArticleFavoriteCount(null, null);

    assertThat(favoriteCount.getId()).isNull();
    assertThat(favoriteCount.getCount()).isNull();
  }

  @Test
  public void should_handle_negative_count() {
    ArticleFavoriteCount favoriteCount = new ArticleFavoriteCount("article999", -1);

    assertThat(favoriteCount.getId()).isEqualTo("article999");
    assertThat(favoriteCount.getCount()).isEqualTo(-1);
  }

  @Test
  public void should_handle_large_count() {
    ArticleFavoriteCount favoriteCount =
        new ArticleFavoriteCount("popular-article", Integer.MAX_VALUE);

    assertThat(favoriteCount.getId()).isEqualTo("popular-article");
    assertThat(favoriteCount.getCount()).isEqualTo(Integer.MAX_VALUE);
  }

  @Test
  public void should_handle_empty_string_id() {
    ArticleFavoriteCount favoriteCount = new ArticleFavoriteCount("", 10);

    assertThat(favoriteCount.getId()).isEqualTo("");
    assertThat(favoriteCount.getCount()).isEqualTo(10);
  }

  @Test
  public void should_handle_long_id() {
    String longId = "very-long-article-id-".repeat(20);
    ArticleFavoriteCount favoriteCount = new ArticleFavoriteCount(longId, 25);

    assertThat(favoriteCount.getId()).isEqualTo(longId);
    assertThat(favoriteCount.getCount()).isEqualTo(25);
  }

  @Test
  public void should_handle_special_characters_in_id() {
    String specialId = "article-with-special-chars!@#$%^&*()_+-=[]{}|;':\",./<>?";
    ArticleFavoriteCount favoriteCount = new ArticleFavoriteCount(specialId, 7);

    assertThat(favoriteCount.getId()).isEqualTo(specialId);
    assertThat(favoriteCount.getCount()).isEqualTo(7);
  }

  @Test
  public void should_handle_unicode_in_id() {
    String unicodeId = "文章-123-测试";
    ArticleFavoriteCount favoriteCount = new ArticleFavoriteCount(unicodeId, 15);

    assertThat(favoriteCount.getId()).isEqualTo(unicodeId);
    assertThat(favoriteCount.getCount()).isEqualTo(15);
  }

  @Test
  public void should_handle_whitespace_in_id() {
    String whitespaceId = "  article with spaces  ";
    ArticleFavoriteCount favoriteCount = new ArticleFavoriteCount(whitespaceId, 8);

    assertThat(favoriteCount.getId()).isEqualTo(whitespaceId);
    assertThat(favoriteCount.getCount()).isEqualTo(8);
  }

  @Test
  public void should_be_immutable_value_object() {
    ArticleFavoriteCount favoriteCount1 = new ArticleFavoriteCount("article123", 5);
    ArticleFavoriteCount favoriteCount2 = new ArticleFavoriteCount("article123", 5);
    ArticleFavoriteCount favoriteCount3 = new ArticleFavoriteCount("article456", 5);
    ArticleFavoriteCount favoriteCount4 = new ArticleFavoriteCount("article123", 10);

    assertThat(favoriteCount1).isEqualTo(favoriteCount2);
    assertThat(favoriteCount1).isNotEqualTo(favoriteCount3);
    assertThat(favoriteCount1).isNotEqualTo(favoriteCount4);
    assertThat(favoriteCount1.hashCode()).isEqualTo(favoriteCount2.hashCode());
  }

  @Test
  public void should_handle_equals_edge_cases() {
    ArticleFavoriteCount favoriteCount = new ArticleFavoriteCount("article123", 5);

    assertThat(favoriteCount).isEqualTo(favoriteCount);
    assertThat(favoriteCount).isNotEqualTo(null);
    assertThat(favoriteCount).isNotEqualTo("not an ArticleFavoriteCount");
    assertThat(favoriteCount).isNotEqualTo(new Object());
  }

  @Test
  public void should_handle_equals_with_null_fields() {
    ArticleFavoriteCount favoriteCount1 = new ArticleFavoriteCount(null, null);
    ArticleFavoriteCount favoriteCount2 = new ArticleFavoriteCount(null, null);
    ArticleFavoriteCount favoriteCount3 = new ArticleFavoriteCount("article123", null);
    ArticleFavoriteCount favoriteCount4 = new ArticleFavoriteCount(null, 5);

    assertThat(favoriteCount1).isEqualTo(favoriteCount2);
    assertThat(favoriteCount1).isNotEqualTo(favoriteCount3);
    assertThat(favoriteCount1).isNotEqualTo(favoriteCount4);
    assertThat(favoriteCount3).isNotEqualTo(favoriteCount4);
  }

  @Test
  public void should_handle_hashcode_consistency() {
    ArticleFavoriteCount favoriteCount1 = new ArticleFavoriteCount("article123", 5);
    ArticleFavoriteCount favoriteCount2 = new ArticleFavoriteCount("article123", 5);
    ArticleFavoriteCount favoriteCount3 = new ArticleFavoriteCount(null, null);
    ArticleFavoriteCount favoriteCount4 = new ArticleFavoriteCount(null, null);

    assertThat(favoriteCount1.hashCode()).isEqualTo(favoriteCount2.hashCode());
    assertThat(favoriteCount3.hashCode()).isEqualTo(favoriteCount4.hashCode());

    int hash1 = favoriteCount1.hashCode();
    int hash2 = favoriteCount1.hashCode();
    assertThat(hash1).isEqualTo(hash2);
  }

  @Test
  public void should_handle_equals_with_different_null_combinations() {
    ArticleFavoriteCount allNull = new ArticleFavoriteCount(null, null);
    ArticleFavoriteCount idNull = new ArticleFavoriteCount(null, 5);
    ArticleFavoriteCount countNull = new ArticleFavoriteCount("article123", null);
    ArticleFavoriteCount noneNull = new ArticleFavoriteCount("article123", 5);

    assertThat(allNull).isNotEqualTo(idNull);
    assertThat(allNull).isNotEqualTo(countNull);
    assertThat(allNull).isNotEqualTo(noneNull);
    assertThat(idNull).isNotEqualTo(countNull);
    assertThat(idNull).isNotEqualTo(noneNull);
    assertThat(countNull).isNotEqualTo(noneNull);
  }

  @Test
  public void should_have_meaningful_string_representation() {
    ArticleFavoriteCount favoriteCount = new ArticleFavoriteCount("article123", 5);

    String stringRepresentation = favoriteCount.toString();

    assertThat(stringRepresentation).contains("article123");
    assertThat(stringRepresentation).contains("5");
  }
}
