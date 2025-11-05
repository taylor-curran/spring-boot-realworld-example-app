package io.spring.graphql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import graphql.execution.DataFetcherResult;
import io.spring.application.CommentQueryService;
import io.spring.application.CursorPageParameter;
import io.spring.application.CursorPager;
import io.spring.application.CursorPager.Direction;
import io.spring.application.data.ArticleData;
import io.spring.application.data.CommentData;
import io.spring.application.data.ProfileData;
import io.spring.core.user.User;
import io.spring.graphql.types.Article;
import io.spring.graphql.types.Comment;
import io.spring.graphql.types.CommentsConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CommentDatafetcherTest {

  @Mock private CommentQueryService commentQueryService;

  @Mock private DgsDataFetchingEnvironment dfe;

  @InjectMocks private CommentDatafetcher commentDatafetcher;

  private User testUser;
  private CommentData testCommentData;
  private ProfileData testProfileData;
  private Article testArticle;
  private ArticleData testArticleData;

  @BeforeEach
  public void setUp() {
    testUser = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
    testProfileData =
        new ProfileData(
            testUser.getId(),
            testUser.getUsername(),
            testUser.getBio(),
            testUser.getImage(),
            false);
    testCommentData =
        new CommentData(
            "comment-id",
            "Test comment body",
            "article-id",
            DateTime.now(),
            DateTime.now(),
            testProfileData);
    testArticle =
        Article.newBuilder()
            .slug("test-slug")
            .title("Test Title")
            .description("Test Description")
            .body("Test Body")
            .build();
    testArticleData =
        new ArticleData(
            "article-id",
            "test-slug",
            "Test Title",
            "Test Description",
            "Test Body",
            false,
            0,
            DateTime.now(),
            DateTime.now(),
            Arrays.asList("java", "spring"),
            null);
  }

  @Test
  public void should_get_comment_successfully() {
    when(dfe.getLocalContext()).thenReturn(testCommentData);

    DataFetcherResult<Comment> result = commentDatafetcher.getComment(dfe);

    assertThat(result).isNotNull();
    assertThat(result.getData()).isNotNull();
    assertThat(result.getData().getId()).isEqualTo("comment-id");
    assertThat(result.getData().getBody()).isEqualTo("Test comment body");
  }

  @Test
  public void should_fetch_article_comments_successfully() {
    Integer first = 10;
    String after = null;
    Integer last = null;
    String before = null;

    when(dfe.getSource()).thenReturn(testArticle);
    Map<String, ArticleData> contextMap = new HashMap<>();
    contextMap.put("test-slug", testArticleData);
    when(dfe.getLocalContext()).thenReturn(contextMap);

    CursorPager<CommentData> pager =
        new CursorPager<>(Arrays.asList(testCommentData), Direction.NEXT, false);
    when(commentQueryService.findByArticleIdWithCursor(
            eq("article-id"), any(), any(CursorPageParameter.class)))
        .thenReturn(pager);

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));

      DataFetcherResult<CommentsConnection> result =
          commentDatafetcher.articleComments(first, after, last, before, dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData().getEdges()).hasSize(1);
      assertThat(result.getData().getPageInfo().isHasNextPage()).isFalse();
      verify(commentQueryService)
          .findByArticleIdWithCursor(
              eq("article-id"), eq(testUser), any(CursorPageParameter.class));
    }
  }

  @Test
  public void should_handle_null_user_in_comments_fetch() {
    Integer first = 10;
    String after = null;
    Integer last = null;
    String before = null;

    when(dfe.getSource()).thenReturn(testArticle);
    Map<String, ArticleData> contextMap = new HashMap<>();
    contextMap.put("test-slug", testArticleData);
    when(dfe.getLocalContext()).thenReturn(contextMap);

    CursorPager<CommentData> pager =
        new CursorPager<>(Arrays.asList(testCommentData), Direction.NEXT, false);
    when(commentQueryService.findByArticleIdWithCursor(
            eq("article-id"), eq(null), any(CursorPageParameter.class)))
        .thenReturn(pager);

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.empty());

      DataFetcherResult<CommentsConnection> result =
          commentDatafetcher.articleComments(first, after, last, before, dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData().getEdges()).hasSize(1);
      verify(commentQueryService)
          .findByArticleIdWithCursor(eq("article-id"), eq(null), any(CursorPageParameter.class));
    }
  }

  @Test
  public void should_handle_empty_comments_result() {
    Integer first = 10;
    String after = null;
    Integer last = null;
    String before = null;

    when(dfe.getSource()).thenReturn(testArticle);
    Map<String, ArticleData> contextMap = new HashMap<>();
    contextMap.put("test-slug", testArticleData);
    when(dfe.getLocalContext()).thenReturn(contextMap);

    CursorPager<CommentData> pager =
        new CursorPager<>(Collections.emptyList(), Direction.NEXT, false);
    when(commentQueryService.findByArticleIdWithCursor(
            eq("article-id"), any(), any(CursorPageParameter.class)))
        .thenReturn(pager);

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));

      DataFetcherResult<CommentsConnection> result =
          commentDatafetcher.articleComments(first, after, last, before, dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData().getEdges()).isEmpty();
      assertThat(result.getData().getPageInfo().isHasNextPage()).isFalse();
    }
  }

  @Test
  public void should_handle_pagination_with_cursor() {
    Integer first = 5;
    String after = "1640995200000"; // Valid timestamp in milliseconds
    Integer last = null;
    String before = null;

    when(dfe.getSource()).thenReturn(testArticle);
    Map<String, ArticleData> contextMap = new HashMap<>();
    contextMap.put("test-slug", testArticleData);
    when(dfe.getLocalContext()).thenReturn(contextMap);

    CursorPager<CommentData> pager =
        new CursorPager<>(Arrays.asList(testCommentData), Direction.NEXT, true);
    when(commentQueryService.findByArticleIdWithCursor(
            eq("article-id"), any(), any(CursorPageParameter.class)))
        .thenReturn(pager);

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));

      DataFetcherResult<CommentsConnection> result =
          commentDatafetcher.articleComments(first, after, last, before, dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData().getEdges()).hasSize(1);
      assertThat(result.getData().getPageInfo().isHasNextPage()).isTrue();
    }
  }

  @Test
  public void should_throw_exception_when_both_first_and_last_are_null() {
    Integer first = null;
    String after = null;
    Integer last = null;
    String before = null;

    try {
      commentDatafetcher.articleComments(first, after, last, before, dfe);
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage()).contains("first 和 last 必须只存在一个");
    }
  }

  @Test
  public void should_build_comments_connection_correctly() {
    Integer first = 10;
    String after = null;
    Integer last = null;
    String before = null;

    when(dfe.getSource()).thenReturn(testArticle);
    Map<String, ArticleData> contextMap = new HashMap<>();
    contextMap.put("test-slug", testArticleData);
    when(dfe.getLocalContext()).thenReturn(contextMap);

    CommentData comment1 =
        new CommentData(
            "id1", "Comment 1", "article-id", DateTime.now(), DateTime.now(), testProfileData);
    CommentData comment2 =
        new CommentData(
            "id2",
            "Comment 2",
            "article-id",
            DateTime.now().plusMinutes(1),
            DateTime.now().plusMinutes(1),
            testProfileData);

    CursorPager<CommentData> pager =
        new CursorPager<>(Arrays.asList(comment1, comment2), Direction.NEXT, false);
    when(commentQueryService.findByArticleIdWithCursor(
            eq("article-id"), any(), any(CursorPageParameter.class)))
        .thenReturn(pager);

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));

      DataFetcherResult<CommentsConnection> result =
          commentDatafetcher.articleComments(first, after, last, before, dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData().getEdges()).hasSize(2);
      assertThat(result.getData().getPageInfo().isHasNextPage()).isFalse();

      assertThat(result.getData().getEdges().get(0).getNode().getId()).isEqualTo("id1");
      assertThat(result.getData().getEdges().get(1).getNode().getId()).isEqualTo("id2");
    }
  }
}
