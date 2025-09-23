package io.spring.application;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import io.spring.application.CursorPager.Direction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class CursorPagerTest {

  private static class TestNode implements Node {
    private final String id;
    private final PageCursor cursor;

    public TestNode(String id, PageCursor cursor) {
      this.id = id;
      this.cursor = cursor;
    }

    @Override
    public PageCursor getCursor() {
      return cursor;
    }

    public String getId() {
      return id;
    }
  }

  @Test
  public void should_create_cursor_pager_with_next_direction_and_extra_data() {
    List<TestNode> data = Arrays.asList(
        new TestNode("1", new DateTimeCursor(null)),
        new TestNode("2", new DateTimeCursor(null))
    );
    
    CursorPager<TestNode> pager = new CursorPager<>(data, Direction.NEXT, true);
    
    assertThat(pager.getData().size(), is(2));
    assertThat(pager.hasNext(), is(true));
    assertThat(pager.hasPrevious(), is(false));
    assertThat(pager.isNext(), is(true));
    assertThat(pager.isPrevious(), is(false));
  }

  @Test
  public void should_create_cursor_pager_with_next_direction_and_no_extra_data() {
    List<TestNode> data = Arrays.asList(
        new TestNode("1", new DateTimeCursor(null)),
        new TestNode("2", new DateTimeCursor(null))
    );
    
    CursorPager<TestNode> pager = new CursorPager<>(data, Direction.NEXT, false);
    
    assertThat(pager.getData().size(), is(2));
    assertThat(pager.hasNext(), is(false));
    assertThat(pager.hasPrevious(), is(false));
    assertThat(pager.isNext(), is(false));
    assertThat(pager.isPrevious(), is(false));
  }

  @Test
  public void should_create_cursor_pager_with_prev_direction_and_extra_data() {
    List<TestNode> data = Arrays.asList(
        new TestNode("1", new DateTimeCursor(null)),
        new TestNode("2", new DateTimeCursor(null))
    );
    
    CursorPager<TestNode> pager = new CursorPager<>(data, Direction.PREV, true);
    
    assertThat(pager.getData().size(), is(2));
    assertThat(pager.hasNext(), is(false));
    assertThat(pager.hasPrevious(), is(true));
    assertThat(pager.isNext(), is(false));
    assertThat(pager.isPrevious(), is(true));
  }

  @Test
  public void should_create_cursor_pager_with_prev_direction_and_no_extra_data() {
    List<TestNode> data = Arrays.asList(
        new TestNode("1", new DateTimeCursor(null)),
        new TestNode("2", new DateTimeCursor(null))
    );
    
    CursorPager<TestNode> pager = new CursorPager<>(data, Direction.PREV, false);
    
    assertThat(pager.getData().size(), is(2));
    assertThat(pager.hasNext(), is(false));
    assertThat(pager.hasPrevious(), is(false));
    assertThat(pager.isNext(), is(false));
    assertThat(pager.isPrevious(), is(false));
  }

  @Test
  public void should_return_start_cursor_for_non_empty_data() {
    PageCursor startCursor = new DateTimeCursor(null);
    PageCursor endCursor = new DateTimeCursor(null);
    List<TestNode> data = Arrays.asList(
        new TestNode("1", startCursor),
        new TestNode("2", endCursor)
    );
    
    CursorPager<TestNode> pager = new CursorPager<>(data, Direction.NEXT, false);
    
    assertThat(pager.getStartCursor(), is(startCursor));
  }

  @Test
  public void should_return_end_cursor_for_non_empty_data() {
    PageCursor startCursor = new DateTimeCursor(null);
    PageCursor endCursor = new DateTimeCursor(null);
    List<TestNode> data = Arrays.asList(
        new TestNode("1", startCursor),
        new TestNode("2", endCursor)
    );
    
    CursorPager<TestNode> pager = new CursorPager<>(data, Direction.NEXT, false);
    
    assertThat(pager.getEndCursor(), is(endCursor));
  }

  @Test
  public void should_return_null_start_cursor_for_empty_data() {
    List<TestNode> data = new ArrayList<>();
    
    CursorPager<TestNode> pager = new CursorPager<>(data, Direction.NEXT, false);
    
    assertThat(pager.getStartCursor(), is(nullValue()));
  }

  @Test
  public void should_return_null_end_cursor_for_empty_data() {
    List<TestNode> data = new ArrayList<>();
    
    CursorPager<TestNode> pager = new CursorPager<>(data, Direction.NEXT, false);
    
    assertThat(pager.getEndCursor(), is(nullValue()));
  }

  @Test
  public void should_return_same_cursor_for_single_item_data() {
    PageCursor cursor = new DateTimeCursor(null);
    List<TestNode> data = Arrays.asList(new TestNode("1", cursor));
    
    CursorPager<TestNode> pager = new CursorPager<>(data, Direction.NEXT, false);
    
    assertThat(pager.getStartCursor(), is(cursor));
    assertThat(pager.getEndCursor(), is(cursor));
  }

  @Test
  public void should_test_lombok_generated_getters() {
    List<TestNode> data = Arrays.asList(
        new TestNode("1", new DateTimeCursor(null)),
        new TestNode("2", new DateTimeCursor(null))
    );
    
    CursorPager<TestNode> pager = new CursorPager<>(data, Direction.NEXT, true);
    
    assertThat(pager.getData(), is(data));
    assertThat(pager.isNext(), is(true));
    assertThat(pager.isPrevious(), is(false));
  }

  @Test
  public void should_handle_direction_enum_values() {
    assertThat(Direction.NEXT, is(Direction.NEXT));
    assertThat(Direction.PREV, is(Direction.PREV));
    assertThat(Direction.values().length, is(2));
  }

  @Test
  public void should_test_all_combinations_of_direction_and_extra_flags() {
    List<TestNode> data = Arrays.asList(new TestNode("1", new DateTimeCursor(null)));
    
    CursorPager<TestNode> nextWithExtra = new CursorPager<>(data, Direction.NEXT, true);
    assertThat(nextWithExtra.hasNext(), is(true));
    assertThat(nextWithExtra.hasPrevious(), is(false));
    
    CursorPager<TestNode> nextWithoutExtra = new CursorPager<>(data, Direction.NEXT, false);
    assertThat(nextWithoutExtra.hasNext(), is(false));
    assertThat(nextWithoutExtra.hasPrevious(), is(false));
    
    CursorPager<TestNode> prevWithExtra = new CursorPager<>(data, Direction.PREV, true);
    assertThat(prevWithExtra.hasNext(), is(false));
    assertThat(prevWithExtra.hasPrevious(), is(true));
    
    CursorPager<TestNode> prevWithoutExtra = new CursorPager<>(data, Direction.PREV, false);
    assertThat(prevWithoutExtra.hasNext(), is(false));
    assertThat(prevWithoutExtra.hasPrevious(), is(false));
  }
}
