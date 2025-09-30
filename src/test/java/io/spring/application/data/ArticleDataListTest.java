package io.spring.application.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

public class ArticleDataListTest {

  @Test
  public void should_create_article_data_list_with_articles_and_count() {
    ProfileData profileData =
        new ProfileData("user123", "testuser", "Test bio", "image.jpg", false);

    List<ArticleData> articles =
        Arrays.asList(
            new ArticleData(
                "1",
                "article-1",
                "Article 1",
                "Description 1",
                "Body 1",
                false,
                0,
                DateTime.now(),
                DateTime.now(),
                Arrays.asList("tag1"),
                profileData),
            new ArticleData(
                "2",
                "article-2",
                "Article 2",
                "Description 2",
                "Body 2",
                true,
                5,
                DateTime.now(),
                DateTime.now(),
                Arrays.asList("tag2"),
                profileData));

    ArticleDataList articleDataList = new ArticleDataList(articles, 2);

    assertThat(articleDataList.getArticleDatas()).hasSize(2);
    assertThat(articleDataList.getCount()).isEqualTo(2);
    assertThat(articleDataList.getArticleDatas()).containsExactlyElementsOf(articles);
  }

  @Test
  public void should_create_empty_article_data_list() {
    List<ArticleData> emptyArticles = Collections.emptyList();

    ArticleDataList articleDataList = new ArticleDataList(emptyArticles, 0);

    assertThat(articleDataList.getArticleDatas()).isEmpty();
    assertThat(articleDataList.getCount()).isEqualTo(0);
  }

  @Test
  public void should_handle_null_article_list() {
    ArticleDataList articleDataList = new ArticleDataList(null, 0);

    assertThat(articleDataList.getArticleDatas()).isNull();
    assertThat(articleDataList.getCount()).isEqualTo(0);
  }

  @Test
  public void should_handle_mismatched_count_and_list_size() {
    ProfileData profileData =
        new ProfileData("user123", "testuser", "Test bio", "image.jpg", false);

    List<ArticleData> articles =
        Arrays.asList(
            new ArticleData(
                "1",
                "article-1",
                "Article 1",
                "Description 1",
                "Body 1",
                false,
                0,
                DateTime.now(),
                DateTime.now(),
                Arrays.asList("tag1"),
                profileData));

    ArticleDataList articleDataList = new ArticleDataList(articles, 10);

    assertThat(articleDataList.getArticleDatas()).hasSize(1);
    assertThat(articleDataList.getCount()).isEqualTo(10);
  }

  @Test
  public void should_handle_negative_count() {
    ProfileData profileData =
        new ProfileData("user123", "testuser", "Test bio", "image.jpg", false);

    List<ArticleData> articles =
        Arrays.asList(
            new ArticleData(
                "1",
                "article-1",
                "Article 1",
                "Description 1",
                "Body 1",
                false,
                0,
                DateTime.now(),
                DateTime.now(),
                Arrays.asList("tag1"),
                profileData));

    ArticleDataList articleDataList = new ArticleDataList(articles, -1);

    assertThat(articleDataList.getArticleDatas()).hasSize(1);
    assertThat(articleDataList.getCount()).isEqualTo(-1);
  }

  @Test
  public void should_handle_large_article_list() {
    ProfileData profileData =
        new ProfileData("user123", "testuser", "Test bio", "image.jpg", false);

    List<ArticleData> largeArticleList =
        Collections.nCopies(
            100,
            new ArticleData(
                "1",
                "article-1",
                "Article 1",
                "Description 1",
                "Body 1",
                false,
                0,
                DateTime.now(),
                DateTime.now(),
                Arrays.asList("tag1"),
                profileData));

    ArticleDataList articleDataList = new ArticleDataList(largeArticleList, 100);

    assertThat(articleDataList.getArticleDatas()).hasSize(100);
    assertThat(articleDataList.getCount()).isEqualTo(100);
  }

  @Test
  public void should_handle_single_article() {
    ProfileData profileData =
        new ProfileData("user123", "testuser", "Test bio", "image.jpg", false);

    ArticleData singleArticle =
        new ArticleData(
            "1",
            "single-article",
            "Single Article",
            "Single Description",
            "Single Body",
            true,
            3,
            DateTime.now(),
            DateTime.now(),
            Arrays.asList("single-tag"),
            profileData);

    List<ArticleData> articles = Collections.singletonList(singleArticle);

    ArticleDataList articleDataList = new ArticleDataList(articles, 1);

    assertThat(articleDataList.getArticleDatas()).hasSize(1);
    assertThat(articleDataList.getCount()).isEqualTo(1);
    assertThat(articleDataList.getArticleDatas().get(0)).isEqualTo(singleArticle);
  }

  @Test
  public void should_handle_articles_with_different_properties() {
    ProfileData profileData1 = new ProfileData("user1", "user1", "Bio 1", "image1.jpg", false);
    ProfileData profileData2 = new ProfileData("user2", "user2", "Bio 2", "image2.jpg", true);

    List<ArticleData> articles =
        Arrays.asList(
            new ArticleData(
                "1",
                "favorited-article",
                "Favorited Article",
                "Desc",
                "Body",
                true,
                10,
                DateTime.now(),
                DateTime.now(),
                Arrays.asList("popular", "trending"),
                profileData1),
            new ArticleData(
                "2",
                "unfavorited-article",
                "Unfavorited Article",
                "Desc",
                "Body",
                false,
                0,
                DateTime.now(),
                DateTime.now(),
                Collections.emptyList(),
                profileData2));

    ArticleDataList articleDataList = new ArticleDataList(articles, 2);

    assertThat(articleDataList.getArticleDatas()).hasSize(2);
    assertThat(articleDataList.getCount()).isEqualTo(2);
    assertThat(articleDataList.getArticleDatas().get(0).isFavorited()).isTrue();
    assertThat(articleDataList.getArticleDatas().get(1).isFavorited()).isFalse();
  }

  @Test
  public void should_handle_zero_count_with_articles() {
    ProfileData profileData =
        new ProfileData("user123", "testuser", "Test bio", "image.jpg", false);

    List<ArticleData> articles =
        Arrays.asList(
            new ArticleData(
                "1",
                "article-1",
                "Article 1",
                "Description 1",
                "Body 1",
                false,
                0,
                DateTime.now(),
                DateTime.now(),
                Arrays.asList("tag1"),
                profileData));

    ArticleDataList articleDataList = new ArticleDataList(articles, 0);

    assertThat(articleDataList.getArticleDatas()).hasSize(1);
    assertThat(articleDataList.getCount()).isEqualTo(0);
  }

  @Test
  public void should_handle_very_large_count() {
    ProfileData profileData =
        new ProfileData("user123", "testuser", "Test bio", "image.jpg", false);

    List<ArticleData> articles =
        Arrays.asList(
            new ArticleData(
                "1",
                "article-1",
                "Article 1",
                "Description 1",
                "Body 1",
                false,
                0,
                DateTime.now(),
                DateTime.now(),
                Arrays.asList("tag1"),
                profileData));

    ArticleDataList articleDataList = new ArticleDataList(articles, Integer.MAX_VALUE);

    assertThat(articleDataList.getArticleDatas()).hasSize(1);
    assertThat(articleDataList.getCount()).isEqualTo(Integer.MAX_VALUE);
  }
}
