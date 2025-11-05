package io.spring.application;

import static org.assertj.core.api.Assertions.assertThat;

import io.spring.application.data.ArticleData;
import io.spring.application.data.ProfileData;
import java.util.Arrays;
import java.util.Collections;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

public class CursorPagerEdgeCasesTest {

  @Test
  void should_handle_next_direction_with_extra_data() {
    ArticleData article = createSampleArticleData();
    CursorPager<ArticleData> pager =
        new CursorPager<>(Arrays.asList(article), CursorPager.Direction.NEXT, true);

    assertThat(pager.hasNext()).isTrue();
    assertThat(pager.hasPrevious()).isFalse();
    assertThat(pager.isNext()).isTrue();
    assertThat(pager.isPrevious()).isFalse();
  }

  @Test
  void should_handle_next_direction_without_extra_data() {
    ArticleData article = createSampleArticleData();
    CursorPager<ArticleData> pager =
        new CursorPager<>(Arrays.asList(article), CursorPager.Direction.NEXT, false);

    assertThat(pager.hasNext()).isFalse();
    assertThat(pager.hasPrevious()).isFalse();
    assertThat(pager.isNext()).isFalse();
    assertThat(pager.isPrevious()).isFalse();
  }

  @Test
  void should_handle_prev_direction_with_extra_data() {
    ArticleData article = createSampleArticleData();
    CursorPager<ArticleData> pager =
        new CursorPager<>(Arrays.asList(article), CursorPager.Direction.PREV, true);

    assertThat(pager.hasNext()).isFalse();
    assertThat(pager.hasPrevious()).isTrue();
    assertThat(pager.isNext()).isFalse();
    assertThat(pager.isPrevious()).isTrue();
  }

  @Test
  void should_handle_prev_direction_without_extra_data() {
    ArticleData article = createSampleArticleData();
    CursorPager<ArticleData> pager =
        new CursorPager<>(Arrays.asList(article), CursorPager.Direction.PREV, false);

    assertThat(pager.hasNext()).isFalse();
    assertThat(pager.hasPrevious()).isFalse();
    assertThat(pager.isNext()).isFalse();
    assertThat(pager.isPrevious()).isFalse();
  }

  @Test
  void should_return_null_cursors_for_empty_data() {
    CursorPager<ArticleData> pager =
        new CursorPager<>(Collections.emptyList(), CursorPager.Direction.NEXT, false);

    assertThat(pager.getStartCursor()).isNull();
    assertThat(pager.getEndCursor()).isNull();
    assertThat(pager.getData()).isEmpty();
  }

  @Test
  void should_return_correct_cursors_for_single_item() {
    ArticleData article = createSampleArticleData();
    CursorPager<ArticleData> pager =
        new CursorPager<>(Arrays.asList(article), CursorPager.Direction.NEXT, false);

    assertThat(pager.getStartCursor()).isNotNull();
    assertThat(pager.getEndCursor()).isNotNull();
    assertThat(pager.getStartCursor().toString()).isEqualTo(pager.getEndCursor().toString());
  }

  @Test
  void should_return_different_cursors_for_multiple_items() {
    ArticleData article1 = createSampleArticleData();
    ArticleData article2 = createSampleArticleDataWithDifferentTime();
    CursorPager<ArticleData> pager =
        new CursorPager<>(Arrays.asList(article1, article2), CursorPager.Direction.NEXT, false);

    assertThat(pager.getStartCursor()).isNotNull();
    assertThat(pager.getEndCursor()).isNotNull();
    assertThat(pager.getStartCursor().toString()).isNotEqualTo(pager.getEndCursor().toString());
    assertThat(pager.getData()).hasSize(2);
  }

  private ArticleData createSampleArticleData() {
    DateTime now = DateTime.now();
    ProfileData profile = new ProfileData("user-id", "testuser", "Bio", "image.jpg", false);
    return new ArticleData(
        "id", "slug", "title", "desc", "body", false, 0, now, now, Arrays.asList("tag1"), profile);
  }

  private ArticleData createSampleArticleDataWithDifferentTime() {
    DateTime later = DateTime.now().plusMinutes(30);
    ProfileData profile = new ProfileData("user-id2", "testuser2", "Bio2", "image2.jpg", false);
    return new ArticleData(
        "id2",
        "slug2",
        "title2",
        "desc2",
        "body2",
        false,
        0,
        later,
        later,
        Arrays.asList("tag2"),
        profile);
  }
}
