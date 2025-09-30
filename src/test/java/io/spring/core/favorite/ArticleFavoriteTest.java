package io.spring.core.favorite;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ArticleFavoriteTest {

  @Test
  public void should_create_article_favorite_with_valid_parameters() {
    String articleId = "article123";
    String userId = "user456";

    ArticleFavorite favorite = new ArticleFavorite(articleId, userId);

    assertThat(favorite.getArticleId()).isEqualTo(articleId);
    assertThat(favorite.getUserId()).isEqualTo(userId);
  }

  @Test
  public void should_create_article_favorite_with_null_parameters() {
    String articleId = null;
    String userId = null;

    ArticleFavorite favorite = new ArticleFavorite(articleId, userId);

    assertThat(favorite.getArticleId()).isNull();
    assertThat(favorite.getUserId()).isNull();
  }

  @Test
  public void should_create_article_favorite_with_empty_strings() {
    String articleId = "";
    String userId = "";

    ArticleFavorite favorite = new ArticleFavorite(articleId, userId);

    assertThat(favorite.getArticleId()).isEqualTo("");
    assertThat(favorite.getUserId()).isEqualTo("");
  }

  @Test
  public void should_create_article_favorite_with_long_ids() {
    String articleId =
        "very-long-article-id-that-contains-many-characters-and-should-still-work-properly";
    String userId =
        "very-long-user-id-that-contains-many-characters-and-should-still-work-properly";

    ArticleFavorite favorite = new ArticleFavorite(articleId, userId);

    assertThat(favorite.getArticleId()).isEqualTo(articleId);
    assertThat(favorite.getUserId()).isEqualTo(userId);
  }

  @Test
  public void should_handle_special_characters_in_ids() {
    String articleId = "article-123_with-special@chars";
    String userId = "user-456_with-special@chars";

    ArticleFavorite favorite = new ArticleFavorite(articleId, userId);

    assertThat(favorite.getArticleId()).isEqualTo(articleId);
    assertThat(favorite.getUserId()).isEqualTo(userId);
  }

  @Test
  public void should_handle_unicode_characters_in_ids() {
    String articleId = "article-中文-123";
    String userId = "user-中文-456";

    ArticleFavorite favorite = new ArticleFavorite(articleId, userId);

    assertThat(favorite.getArticleId()).isEqualTo(articleId);
    assertThat(favorite.getUserId()).isEqualTo(userId);
  }

  @Test
  public void should_test_equals_and_hashcode() {
    ArticleFavorite favorite1 = new ArticleFavorite("article123", "user456");
    ArticleFavorite favorite2 = new ArticleFavorite("article123", "user456");
    ArticleFavorite favorite3 = new ArticleFavorite("article456", "user456");
    ArticleFavorite favorite4 = new ArticleFavorite("article123", "user789");

    assertThat(favorite1).isEqualTo(favorite2);
    assertThat(favorite1.hashCode()).isEqualTo(favorite2.hashCode());

    assertThat(favorite1).isNotEqualTo(favorite3);
    assertThat(favorite1).isNotEqualTo(favorite4);
    assertThat(favorite1).isNotEqualTo(null);
    assertThat(favorite1).isNotEqualTo("string");
    assertThat(favorite1).isEqualTo(favorite1);
  }

  @Test
  public void should_test_equals_with_null_fields() {
    ArticleFavorite favorite1 = new ArticleFavorite(null, null);
    ArticleFavorite favorite2 = new ArticleFavorite(null, null);
    ArticleFavorite favorite3 = new ArticleFavorite("article123", null);
    ArticleFavorite favorite4 = new ArticleFavorite(null, "user456");

    assertThat(favorite1).isEqualTo(favorite2);
    assertThat(favorite1.hashCode()).isEqualTo(favorite2.hashCode());

    assertThat(favorite1).isNotEqualTo(favorite3);
    assertThat(favorite1).isNotEqualTo(favorite4);
  }

  @Test
  public void should_test_equals_with_empty_strings() {
    ArticleFavorite favorite1 = new ArticleFavorite("", "");
    ArticleFavorite favorite2 = new ArticleFavorite("", "");
    ArticleFavorite favorite3 = new ArticleFavorite("article123", "");
    ArticleFavorite favorite4 = new ArticleFavorite("", "user456");

    assertThat(favorite1).isEqualTo(favorite2);
    assertThat(favorite1.hashCode()).isEqualTo(favorite2.hashCode());

    assertThat(favorite1).isNotEqualTo(favorite3);
    assertThat(favorite1).isNotEqualTo(favorite4);
  }

  @Test
  public void should_test_no_args_constructor() {
    ArticleFavorite favorite = new ArticleFavorite();

    assertThat(favorite.getArticleId()).isNull();
    assertThat(favorite.getUserId()).isNull();
  }

  @Test
  public void should_handle_whitespace_in_ids() {
    String articleId = "  article123  ";
    String userId = "  user456  ";

    ArticleFavorite favorite = new ArticleFavorite(articleId, userId);

    assertThat(favorite.getArticleId()).isEqualTo(articleId);
    assertThat(favorite.getUserId()).isEqualTo(userId);
  }

  @Test
  public void should_create_multiple_favorites_for_same_user() {
    String userId = "user456";
    String articleId1 = "article123";
    String articleId2 = "article789";

    ArticleFavorite favorite1 = new ArticleFavorite(articleId1, userId);
    ArticleFavorite favorite2 = new ArticleFavorite(articleId2, userId);

    assertThat(favorite1.getUserId()).isEqualTo(favorite2.getUserId());
    assertThat(favorite1.getArticleId()).isNotEqualTo(favorite2.getArticleId());
    assertThat(favorite1).isNotEqualTo(favorite2);
  }

  @Test
  public void should_create_multiple_favorites_for_same_article() {
    String articleId = "article123";
    String userId1 = "user456";
    String userId2 = "user789";

    ArticleFavorite favorite1 = new ArticleFavorite(articleId, userId1);
    ArticleFavorite favorite2 = new ArticleFavorite(articleId, userId2);

    assertThat(favorite1.getArticleId()).isEqualTo(favorite2.getArticleId());
    assertThat(favorite1.getUserId()).isNotEqualTo(favorite2.getUserId());
    assertThat(favorite1).isNotEqualTo(favorite2);
  }
}
