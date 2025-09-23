package io.spring.core.favorite;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class ArticleFavoriteTest {

  @Test
  public void should_create_article_favorite_with_constructor() {
    String articleId = "article-123";
    String userId = "user-456";

    ArticleFavorite favorite = new ArticleFavorite(articleId, userId);

    assertThat(favorite, notNullValue());
    assertThat(favorite.getArticleId(), is(articleId));
    assertThat(favorite.getUserId(), is(userId));
  }

  @Test
  public void should_create_article_favorite_with_no_args_constructor() {
    ArticleFavorite favorite = new ArticleFavorite();

    assertThat(favorite, notNullValue());
  }

  @Test
  public void should_have_proper_equals_and_hashcode() {
    String articleId = "article-123";
    String userId = "user-456";
    
    ArticleFavorite favorite1 = new ArticleFavorite(articleId, userId);
    ArticleFavorite favorite2 = new ArticleFavorite(articleId, userId);
    ArticleFavorite favorite3 = new ArticleFavorite("different-article", userId);

    assertThat(favorite1.equals(favorite2), is(true));
    assertThat(favorite1.hashCode(), is(favorite2.hashCode()));
    assertThat(favorite1.equals(favorite3), is(false));
    assertThat(favorite1.hashCode(), not(favorite3.hashCode()));
  }

  @Test
  public void should_not_be_equal_to_null() {
    ArticleFavorite favorite = new ArticleFavorite("article-123", "user-456");

    assertThat(favorite.equals(null), is(false));
  }

  @Test
  public void should_not_be_equal_to_different_class() {
    ArticleFavorite favorite = new ArticleFavorite("article-123", "user-456");

    assertThat(favorite.equals("not an ArticleFavorite"), is(false));
  }

  @Test
  public void should_be_equal_to_itself() {
    ArticleFavorite favorite = new ArticleFavorite("article-123", "user-456");

    assertThat(favorite.equals(favorite), is(true));
  }

  @Test
  public void should_handle_null_values_in_constructor() {
    ArticleFavorite favorite = new ArticleFavorite(null, null);

    assertThat(favorite, notNullValue());
    assertThat(favorite.getArticleId(), is((String) null));
    assertThat(favorite.getUserId(), is((String) null));
  }

  @Test
  public void should_handle_empty_strings_in_constructor() {
    ArticleFavorite favorite = new ArticleFavorite("", "");

    assertThat(favorite, notNullValue());
    assertThat(favorite.getArticleId(), is(""));
    assertThat(favorite.getUserId(), is(""));
  }

  @Test
  public void should_test_equals_with_null_articleId() {
    ArticleFavorite favorite1 = new ArticleFavorite(null, "user-456");
    ArticleFavorite favorite2 = new ArticleFavorite(null, "user-456");
    ArticleFavorite favorite3 = new ArticleFavorite("article-123", "user-456");

    assertThat(favorite1.equals(favorite2), is(true));
    assertThat(favorite1.equals(favorite3), is(false));
    assertThat(favorite3.equals(favorite1), is(false));
  }

  @Test
  public void should_test_equals_with_null_userId() {
    ArticleFavorite favorite1 = new ArticleFavorite("article-123", null);
    ArticleFavorite favorite2 = new ArticleFavorite("article-123", null);
    ArticleFavorite favorite3 = new ArticleFavorite("article-123", "user-456");

    assertThat(favorite1.equals(favorite2), is(true));
    assertThat(favorite1.equals(favorite3), is(false));
    assertThat(favorite3.equals(favorite1), is(false));
  }

  @Test
  public void should_test_equals_with_both_null_fields() {
    ArticleFavorite favorite1 = new ArticleFavorite(null, null);
    ArticleFavorite favorite2 = new ArticleFavorite(null, null);
    ArticleFavorite favorite3 = new ArticleFavorite("article-123", null);

    assertThat(favorite1.equals(favorite2), is(true));
    assertThat(favorite1.equals(favorite3), is(false));
    assertThat(favorite3.equals(favorite1), is(false));
  }

  @Test
  public void should_test_equals_with_mixed_null_fields() {
    ArticleFavorite favorite1 = new ArticleFavorite(null, "user-456");
    ArticleFavorite favorite2 = new ArticleFavorite("article-123", null);

    assertThat(favorite1.equals(favorite2), is(false));
    assertThat(favorite2.equals(favorite1), is(false));
  }

  @Test
  public void should_test_hashcode_with_null_articleId() {
    ArticleFavorite favorite1 = new ArticleFavorite(null, "user-456");
    ArticleFavorite favorite2 = new ArticleFavorite(null, "user-456");

    assertThat(favorite1.hashCode(), is(favorite2.hashCode()));
  }

  @Test
  public void should_test_hashcode_with_null_userId() {
    ArticleFavorite favorite1 = new ArticleFavorite("article-123", null);
    ArticleFavorite favorite2 = new ArticleFavorite("article-123", null);

    assertThat(favorite1.hashCode(), is(favorite2.hashCode()));
  }

  @Test
  public void should_test_hashcode_with_both_null_fields() {
    ArticleFavorite favorite1 = new ArticleFavorite(null, null);
    ArticleFavorite favorite2 = new ArticleFavorite(null, null);

    assertThat(favorite1.hashCode(), is(favorite2.hashCode()));
  }

  @Test
  public void should_test_hashcode_consistency_with_null_values() {
    ArticleFavorite favorite = new ArticleFavorite(null, "user-456");
    int hashCode1 = favorite.hashCode();
    int hashCode2 = favorite.hashCode();

    assertThat(hashCode1, is(hashCode2));
  }

  @Test
  public void should_test_equals_with_different_articleId_only() {
    ArticleFavorite favorite1 = new ArticleFavorite("article-123", "user-456");
    ArticleFavorite favorite2 = new ArticleFavorite("article-789", "user-456");

    assertThat(favorite1.equals(favorite2), is(false));
    assertThat(favorite2.equals(favorite1), is(false));
  }

  @Test
  public void should_test_equals_with_different_userId_only() {
    ArticleFavorite favorite1 = new ArticleFavorite("article-123", "user-456");
    ArticleFavorite favorite2 = new ArticleFavorite("article-123", "user-789");

    assertThat(favorite1.equals(favorite2), is(false));
    assertThat(favorite2.equals(favorite1), is(false));
  }
}
