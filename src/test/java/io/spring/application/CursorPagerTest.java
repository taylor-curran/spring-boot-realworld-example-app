package io.spring.application;

import static org.assertj.core.api.Assertions.assertThat;

import io.spring.application.CursorPager.Direction;
import io.spring.application.data.ArticleData;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

public class CursorPagerTest {

  @Test
  public void should_create_cursor_pager_with_next_direction() {
    ArticleData article1 =
        new ArticleData(
            "1",
            "slug1",
            "Title1",
            "Desc1",
            "Body1",
            false,
            0,
            DateTime.now(),
            DateTime.now(),
            Arrays.asList("tag1"),
            null);
    ArticleData article2 =
        new ArticleData(
            "2",
            "slug2",
            "Title2",
            "Desc2",
            "Body2",
            false,
            0,
            DateTime.now(),
            DateTime.now(),
            Arrays.asList("tag2"),
            null);
    List<ArticleData> data = Arrays.asList(article1, article2);
    Direction direction = Direction.NEXT;
    boolean hasExtra = false;

    CursorPager<ArticleData> pager = new CursorPager<>(data, direction, hasExtra);

    assertThat(pager.getData()).isEqualTo(data);
    assertThat(pager.hasNext()).isEqualTo(hasExtra);
    assertThat(pager.hasPrevious()).isFalse();
  }

  @Test
  public void should_create_cursor_pager_with_prev_direction() {
    ArticleData article1 =
        new ArticleData(
            "1",
            "slug1",
            "Title1",
            "Desc1",
            "Body1",
            false,
            0,
            DateTime.now(),
            DateTime.now(),
            Arrays.asList("tag1"),
            null);
    List<ArticleData> data = Arrays.asList(article1);
    Direction direction = Direction.PREV;
    boolean hasExtra = true;

    CursorPager<ArticleData> pager = new CursorPager<>(data, direction, hasExtra);

    assertThat(pager.getData()).isEqualTo(data);
    assertThat(pager.hasNext()).isFalse();
    assertThat(pager.hasPrevious()).isTrue();
  }

  @Test
  public void should_handle_empty_data_list() {
    List<ArticleData> data = Collections.emptyList();
    Direction direction = Direction.NEXT;
    boolean hasExtra = false;

    CursorPager<ArticleData> pager = new CursorPager<>(data, direction, hasExtra);

    assertThat(pager.getData()).isEmpty();
    assertThat(pager.hasNext()).isFalse();
    assertThat(pager.hasPrevious()).isFalse();
  }

  @Test
  public void should_handle_null_data_list() {
    List<ArticleData> data = null;
    Direction direction = Direction.NEXT;
    boolean hasExtra = false;

    CursorPager<ArticleData> pager = new CursorPager<>(data, direction, hasExtra);

    assertThat(pager.getData()).isNull();
    assertThat(pager.hasNext()).isFalse();
    assertThat(pager.hasPrevious()).isFalse();
  }

  @Test
  public void should_handle_single_item() {
    ArticleData article =
        new ArticleData(
            "1",
            "slug1",
            "Title1",
            "Desc1",
            "Body1",
            false,
            0,
            DateTime.now(),
            DateTime.now(),
            Arrays.asList("tag1"),
            null);
    List<ArticleData> data = Arrays.asList(article);
    CursorPager<ArticleData> pager = new CursorPager<>(data, Direction.NEXT, false);

    assertThat(pager.getData()).hasSize(1);
    assertThat(pager.getData().get(0)).isEqualTo(article);
    assertThat(pager.hasNext()).isFalse();
    assertThat(pager.hasPrevious()).isFalse();
  }

  @Test
  public void should_test_cursor_methods() {
    DateTime now = DateTime.now();
    ArticleData article1 =
        new ArticleData(
            "1",
            "slug1",
            "Title1",
            "Desc1",
            "Body1",
            false,
            0,
            now,
            now,
            Arrays.asList("tag1"),
            null);
    ArticleData article2 =
        new ArticleData(
            "2",
            "slug2",
            "Title2",
            "Desc2",
            "Body2",
            false,
            0,
            now.plusMinutes(1),
            now.plusMinutes(1),
            Arrays.asList("tag2"),
            null);
    List<ArticleData> data = Arrays.asList(article1, article2);
    CursorPager<ArticleData> pager = new CursorPager<>(data, Direction.NEXT, false);

    assertThat(pager.getStartCursor()).isNotNull();
    assertThat(pager.getEndCursor()).isNotNull();
    assertThat(pager.getStartCursor().toString()).isEqualTo(article1.getCursor().toString());
    assertThat(pager.getEndCursor().toString()).isEqualTo(article2.getCursor().toString());
  }

  @Test
  public void should_handle_empty_list_cursors() {
    List<ArticleData> data = Collections.emptyList();
    CursorPager<ArticleData> pager = new CursorPager<>(data, Direction.NEXT, false);

    assertThat(pager.getStartCursor()).isNull();
    assertThat(pager.getEndCursor()).isNull();
  }

  @Test
  public void should_test_direction_enum() {
    assertThat(Direction.NEXT).isNotNull();
    assertThat(Direction.PREV).isNotNull();
    assertThat(Direction.valueOf("NEXT")).isEqualTo(Direction.NEXT);
    assertThat(Direction.valueOf("PREV")).isEqualTo(Direction.PREV);
    assertThat(Direction.values()).hasSize(2);
  }
}
