package io.spring.application.data;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.application.DateTimeCursor;
import java.util.Arrays;
import java.util.List;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

public class ArticleDataTest {

  @Test
  public void should_create_article_data_with_all_fields() {
    String id = "article-id";
    String slug = "test-article";
    String title = "Test Article";
    String description = "Test description";
    String body = "Test body content";
    boolean favorited = true;
    int favoritesCount = 5;
    DateTime createdAt = new DateTime();
    DateTime updatedAt = new DateTime();
    List<String> tagList = Arrays.asList("java", "spring");
    ProfileData profileData = new ProfileData("user-id", "testuser", "Test Bio", "image.jpg", false);

    ArticleData articleData = new ArticleData(
        id, slug, title, description, body, favorited, favoritesCount,
        createdAt, updatedAt, tagList, profileData);

    assertThat(articleData.getId()).isEqualTo(id);
    assertThat(articleData.getSlug()).isEqualTo(slug);
    assertThat(articleData.getTitle()).isEqualTo(title);
    assertThat(articleData.getDescription()).isEqualTo(description);
    assertThat(articleData.getBody()).isEqualTo(body);
    assertThat(articleData.isFavorited()).isEqualTo(favorited);
    assertThat(articleData.getFavoritesCount()).isEqualTo(favoritesCount);
    assertThat(articleData.getCreatedAt()).isEqualTo(createdAt);
    assertThat(articleData.getUpdatedAt()).isEqualTo(updatedAt);
    assertThat(articleData.getTagList()).isEqualTo(tagList);
    assertThat(articleData.getProfileData()).isEqualTo(profileData);
  }

  @Test
  public void should_create_article_data_with_no_args_constructor() {
    ArticleData articleData = new ArticleData();
    
    assertThat(articleData.getId()).isNull();
    assertThat(articleData.getSlug()).isNull();
    assertThat(articleData.getTitle()).isNull();
    assertThat(articleData.getDescription()).isNull();
    assertThat(articleData.getBody()).isNull();
    assertThat(articleData.isFavorited()).isFalse();
    assertThat(articleData.getFavoritesCount()).isEqualTo(0);
    assertThat(articleData.getCreatedAt()).isNull();
    assertThat(articleData.getUpdatedAt()).isNull();
    assertThat(articleData.getTagList()).isNull();
    assertThat(articleData.getProfileData()).isNull();
  }

  @Test
  public void should_set_and_get_all_fields() {
    ArticleData articleData = new ArticleData();
    String id = "test-id";
    String slug = "test-slug";
    String title = "Test Title";
    String description = "Test Description";
    String body = "Test Body";
    boolean favorited = true;
    int favoritesCount = 10;
    DateTime createdAt = new DateTime();
    DateTime updatedAt = new DateTime();
    List<String> tagList = Arrays.asList("test", "article");
    ProfileData profileData = new ProfileData("author-id", "author", "Author Bio", "author.jpg", true);

    articleData.setId(id);
    articleData.setSlug(slug);
    articleData.setTitle(title);
    articleData.setDescription(description);
    articleData.setBody(body);
    articleData.setFavorited(favorited);
    articleData.setFavoritesCount(favoritesCount);
    articleData.setCreatedAt(createdAt);
    articleData.setUpdatedAt(updatedAt);
    articleData.setTagList(tagList);
    articleData.setProfileData(profileData);

    assertThat(articleData.getId()).isEqualTo(id);
    assertThat(articleData.getSlug()).isEqualTo(slug);
    assertThat(articleData.getTitle()).isEqualTo(title);
    assertThat(articleData.getDescription()).isEqualTo(description);
    assertThat(articleData.getBody()).isEqualTo(body);
    assertThat(articleData.isFavorited()).isEqualTo(favorited);
    assertThat(articleData.getFavoritesCount()).isEqualTo(favoritesCount);
    assertThat(articleData.getCreatedAt()).isEqualTo(createdAt);
    assertThat(articleData.getUpdatedAt()).isEqualTo(updatedAt);
    assertThat(articleData.getTagList()).isEqualTo(tagList);
    assertThat(articleData.getProfileData()).isEqualTo(profileData);
  }

  @Test
  public void should_return_cursor_based_on_updated_at() {
    DateTime updatedAt = new DateTime();
    ArticleData articleData = new ArticleData();
    articleData.setUpdatedAt(updatedAt);

    DateTimeCursor cursor = articleData.getCursor();

    assertThat(cursor).isNotNull();
    assertThat(cursor.getData()).isEqualTo(updatedAt);
  }

  @Test
  public void should_handle_null_updated_at_in_cursor() {
    ArticleData articleData = new ArticleData();
    articleData.setUpdatedAt(null);

    DateTimeCursor cursor = articleData.getCursor();

    assertThat(cursor).isNotNull();
    assertThat(cursor.getData()).isNull();
  }

  @Test
  public void should_handle_tag_list_operations() {
    ArticleData articleData = new ArticleData();
    List<String> tags = Arrays.asList("java", "spring", "boot");
    
    articleData.setTagList(tags);
    
    assertThat(articleData.getTagList()).hasSize(3);
    assertThat(articleData.getTagList()).containsExactly("java", "spring", "boot");
  }

  @Test
  public void should_handle_empty_tag_list() {
    ArticleData articleData = new ArticleData();
    List<String> emptyTags = Arrays.asList();
    
    articleData.setTagList(emptyTags);
    
    assertThat(articleData.getTagList()).isEmpty();
  }

  @Test
  public void should_handle_equals_and_hashcode() {
    DateTime now = new DateTime();
    ProfileData profileData = new ProfileData("user-id", "testuser", "Bio", "image.jpg", false);
    
    ArticleData articleData1 = new ArticleData(
        "id", "slug", "title", "desc", "body", true, 5,
        now, now, Arrays.asList("java"), profileData);
    
    ArticleData articleData2 = new ArticleData(
        "id", "slug", "title", "desc", "body", true, 5,
        now, now, Arrays.asList("java"), profileData);

    assertThat(articleData1).isEqualTo(articleData2);
    assertThat(articleData1.hashCode()).isEqualTo(articleData2.hashCode());
  }

  @Test
  public void should_handle_toString() {
    ArticleData articleData = new ArticleData();
    articleData.setId("test-id");
    articleData.setTitle("Test Title");

    String toString = articleData.toString();

    assertThat(toString).contains("ArticleData");
    assertThat(toString).contains("test-id");
    assertThat(toString).contains("Test Title");
  }
}
