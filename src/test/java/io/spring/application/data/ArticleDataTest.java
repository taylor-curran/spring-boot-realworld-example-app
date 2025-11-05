package io.spring.application.data;

import static org.assertj.core.api.Assertions.assertThat;

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
    ProfileData profileData =
        new ProfileData("user-id", "testuser", "Test Bio", "image.jpg", false);

    ArticleData articleData =
        new ArticleData(
            id,
            slug,
            title,
            description,
            body,
            favorited,
            favoritesCount,
            createdAt,
            updatedAt,
            tagList,
            profileData);

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
    ProfileData profileData =
        new ProfileData("author-id", "author", "Author Bio", "author.jpg", true);

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

    ArticleData articleData1 =
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
            Arrays.asList("java"),
            profileData);

    ArticleData articleData2 =
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
            Arrays.asList("java"),
            profileData);

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

  @Test
  public void should_handle_equals_with_different_types() {
    ArticleData article = createSampleArticleData();

    assertThat(article.equals(null)).isFalse();
    assertThat(article.equals("not an article")).isFalse();
    assertThat(article.equals(new Object())).isFalse();
  }

  @Test
  public void should_handle_equals_with_different_fields() {
    DateTime now = DateTime.now();
    ProfileData profile = createSampleProfileData();

    ArticleData article1 =
        new ArticleData(
            "id1",
            "slug1",
            "title1",
            "desc1",
            "body1",
            true,
            5,
            now,
            now,
            Arrays.asList("tag1"),
            profile);

    ArticleData article2 =
        new ArticleData(
            "id2",
            "slug1",
            "title1",
            "desc1",
            "body1",
            true,
            5,
            now,
            now,
            Arrays.asList("tag1"),
            profile);
    assertThat(article1).isNotEqualTo(article2);

    ArticleData article3 =
        new ArticleData(
            "id1",
            "slug2",
            "title1",
            "desc1",
            "body1",
            true,
            5,
            now,
            now,
            Arrays.asList("tag1"),
            profile);
    assertThat(article1).isNotEqualTo(article3);

    ArticleData article4 =
        new ArticleData(
            "id1",
            "slug1",
            "title2",
            "desc1",
            "body1",
            true,
            5,
            now,
            now,
            Arrays.asList("tag1"),
            profile);
    assertThat(article1).isNotEqualTo(article4);

    ArticleData article5 =
        new ArticleData(
            "id1",
            "slug1",
            "title1",
            "desc1",
            "body1",
            false,
            5,
            now,
            now,
            Arrays.asList("tag1"),
            profile);
    assertThat(article1).isNotEqualTo(article5);

    ArticleData article6 =
        new ArticleData(
            "id1",
            "slug1",
            "title1",
            "desc1",
            "body1",
            true,
            10,
            now,
            now,
            Arrays.asList("tag1"),
            profile);
    assertThat(article1).isNotEqualTo(article6);
  }

  @Test
  public void should_handle_equals_with_null_fields() {
    ArticleData article1 =
        new ArticleData(null, null, null, null, null, false, 0, null, null, null, null);
    ArticleData article2 =
        new ArticleData(null, null, null, null, null, false, 0, null, null, null, null);
    ArticleData article3 =
        new ArticleData("id", null, null, null, null, false, 0, null, null, null, null);

    assertThat(article1).isEqualTo(article2);
    assertThat(article1).isNotEqualTo(article3);
    assertThat(article1.hashCode()).isEqualTo(article2.hashCode());
  }

  @Test
  public void should_handle_can_equal_method() {
    ArticleData article = createSampleArticleData();

    assertThat(article.canEqual(article)).isTrue();
    assertThat(article.canEqual(new ArticleData())).isTrue();
    assertThat(article.canEqual("not an article")).isFalse();
    assertThat(article.canEqual(null)).isFalse();
  }

  @Test
  public void should_handle_hash_code_consistency() {
    DateTime now = DateTime.now();
    ProfileData profile = createSampleProfileData();

    ArticleData article1 =
        new ArticleData(
            "id1",
            "slug1",
            "title1",
            "desc1",
            "body1",
            true,
            5,
            now,
            now,
            Arrays.asList("tag1"),
            profile);
    ArticleData article2 =
        new ArticleData(
            "id1",
            "slug1",
            "title1",
            "desc1",
            "body1",
            true,
            5,
            now,
            now,
            Arrays.asList("tag1"),
            profile);

    assertThat(article1.hashCode()).isEqualTo(article2.hashCode());

    int hash1 = article1.hashCode();
    int hash2 = article1.hashCode();
    assertThat(hash1).isEqualTo(hash2);
  }

  @Test
  public void should_handle_equals_with_mixed_null_and_non_null_fields() {
    DateTime now = DateTime.now();
    ProfileData profile = createSampleProfileData();

    ArticleData article1 =
        new ArticleData(
            null,
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
            true,
            5,
            now,
            now,
            Arrays.asList("tag"),
            profile);
    assertThat(article1).isNotEqualTo(article2);

    ArticleData article3 =
        new ArticleData(
            "id", null, "title", "desc", "body", true, 5, now, now, Arrays.asList("tag"), profile);
    ArticleData article4 =
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
    assertThat(article3).isNotEqualTo(article4);

    ArticleData article5 =
        new ArticleData(
            "id", "slug", null, "desc", "body", true, 5, now, now, Arrays.asList("tag"), profile);
    ArticleData article6 =
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
    assertThat(article5).isNotEqualTo(article6);

    ArticleData article7 =
        new ArticleData(
            "id", "slug", "title", null, "body", true, 5, now, now, Arrays.asList("tag"), profile);
    ArticleData article8 =
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
    assertThat(article7).isNotEqualTo(article8);

    ArticleData article9 =
        new ArticleData(
            "id", "slug", "title", "desc", null, true, 5, now, now, Arrays.asList("tag"), profile);
    ArticleData article10 =
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
    assertThat(article9).isNotEqualTo(article10);
  }

  @Test
  public void should_handle_equals_with_date_time_differences() {
    DateTime now1 = DateTime.now();
    DateTime now2 = now1.plusMinutes(1);
    ProfileData profile = createSampleProfileData();

    ArticleData article1 =
        new ArticleData(
            "id",
            "slug",
            "title",
            "desc",
            "body",
            true,
            5,
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
            true,
            5,
            now2,
            now1,
            Arrays.asList("tag"),
            profile);
    assertThat(article1).isNotEqualTo(article2);

    ArticleData article3 =
        new ArticleData(
            "id",
            "slug",
            "title",
            "desc",
            "body",
            true,
            5,
            now1,
            now2,
            Arrays.asList("tag"),
            profile);
    assertThat(article1).isNotEqualTo(article3);

    ArticleData article4 =
        new ArticleData(
            "id",
            "slug",
            "title",
            "desc",
            "body",
            true,
            5,
            null,
            now1,
            Arrays.asList("tag"),
            profile);
    assertThat(article1).isNotEqualTo(article4);

    ArticleData article5 =
        new ArticleData(
            "id",
            "slug",
            "title",
            "desc",
            "body",
            true,
            5,
            now1,
            null,
            Arrays.asList("tag"),
            profile);
    assertThat(article1).isNotEqualTo(article5);
  }

  @Test
  public void should_handle_equals_with_tag_list_differences() {
    DateTime now = DateTime.now();
    ProfileData profile = createSampleProfileData();

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
            Arrays.asList("tag1", "tag2"),
            profile);
    ArticleData article2 =
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
            Arrays.asList("tag1", "tag3"),
            profile);
    assertThat(article1).isNotEqualTo(article2);

    ArticleData article3 =
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
    assertThat(article1).isNotEqualTo(article3);

    ArticleData article4 =
        new ArticleData("id", "slug", "title", "desc", "body", true, 5, now, now, null, profile);
    assertThat(article1).isNotEqualTo(article4);

    ArticleData article5 =
        new ArticleData(
            "id", "slug", "title", "desc", "body", true, 5, now, now, Arrays.asList(), profile);
    assertThat(article1).isNotEqualTo(article5);
  }

  @Test
  public void should_handle_equals_with_profile_data_differences() {
    DateTime now = DateTime.now();
    ProfileData profile1 = new ProfileData("user1", "user1", "bio1", "img1.jpg", false);
    ProfileData profile2 = new ProfileData("user2", "user2", "bio2", "img2.jpg", true);

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
            profile1);
    ArticleData article2 =
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
            profile2);
    assertThat(article1).isNotEqualTo(article2);

    ArticleData article3 =
        new ArticleData(
            "id", "slug", "title", "desc", "body", true, 5, now, now, Arrays.asList("tag"), null);
    assertThat(article1).isNotEqualTo(article3);
  }

  @Test
  public void should_handle_equals_reflexivity() {
    ArticleData article = createSampleArticleData();
    assertThat(article).isEqualTo(article);
  }

  @Test
  public void should_handle_equals_symmetry() {
    DateTime now = DateTime.now();
    ProfileData profile = createSampleProfileData();

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
            true,
            5,
            now,
            now,
            Arrays.asList("tag"),
            profile);

    assertThat(article1.equals(article2)).isEqualTo(article2.equals(article1));
  }

  @Test
  public void should_handle_equals_transitivity() {
    DateTime now = DateTime.now();
    ProfileData profile = createSampleProfileData();

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
            true,
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
            5,
            now,
            now,
            Arrays.asList("tag"),
            profile);

    assertThat(article1).isEqualTo(article2);
    assertThat(article2).isEqualTo(article3);
    assertThat(article1).isEqualTo(article3);
  }

  private ArticleData createSampleArticleData() {
    DateTime now = DateTime.now();
    ProfileData profile = createSampleProfileData();
    return new ArticleData(
        "id", "slug", "title", "desc", "body", true, 5, now, now, Arrays.asList("tag"), profile);
  }

  private ProfileData createSampleProfileData() {
    return new ProfileData("user-id", "testuser", "Test Bio", "image.jpg", false);
  }
}
