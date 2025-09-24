package io.spring.graphql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mockStatic;

import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import graphql.execution.DataFetcherResult;
import io.spring.application.CommentQueryService;
import io.spring.application.CursorPageParameter;
import io.spring.application.CursorPager;
import io.spring.application.DateTimeCursor;
import io.spring.application.data.ArticleData;
import io.spring.application.data.CommentData;
import io.spring.core.user.User;
import io.spring.graphql.SecurityUtil;
import io.spring.graphql.types.Article;
import io.spring.graphql.types.CommentsConnection;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CommentDatafetcherSimpleTest {

  @Mock
  private CommentQueryService commentQueryService;

  private CommentDatafetcher commentDatafetcher;
  private User testUser;

  @BeforeEach
  public void setUp() {
    commentDatafetcher = new CommentDatafetcher(commentQueryService);
    testUser = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
  }

  @Test
  public void articleComments_should_handle_first_parameter_with_data() {
    DgsDataFetchingEnvironment dfe = mock(DgsDataFetchingEnvironment.class);
    Article article = Article.newBuilder().slug("article-slug").build();
    when(dfe.getSource()).thenReturn(article);
    
    Map<String, ArticleData> localContext = new HashMap<>();
    ArticleData articleData = mock(ArticleData.class);
    when(articleData.getId()).thenReturn("article1");
    localContext.put("article-slug", articleData);
    when(dfe.getLocalContext()).thenReturn(localContext);

    CommentData comment1 = createMockCommentData("comment1", "First comment");
    CommentData comment2 = createMockCommentData("comment2", "Second comment");

    CursorPager<CommentData> mockResult = mock(CursorPager.class);
    when(mockResult.getData()).thenReturn(Arrays.asList(comment1, comment2));
    when(mockResult.getStartCursor()).thenReturn(new DateTimeCursor(DateTime.now()));
    when(mockResult.getEndCursor()).thenReturn(new DateTimeCursor(DateTime.now()));
    when(mockResult.hasPrevious()).thenReturn(false);
    when(mockResult.hasNext()).thenReturn(true);

    when(commentQueryService.findByArticleIdWithCursor(
        eq("article1"), any(User.class), any(CursorPageParameter.class)))
        .thenReturn(mockResult);

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));

      DataFetcherResult<CommentsConnection> result = commentDatafetcher.articleComments(10, null, null, null, dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
      assertThat(result.getData().getEdges()).hasSize(2);
      assertThat(result.getData().getEdges().get(0).getNode().getId()).isEqualTo("comment1");
      assertThat(result.getData().getEdges().get(1).getNode().getId()).isEqualTo("comment2");
      assertThat(result.getData().getPageInfo().isHasNextPage()).isTrue();
      assertThat((Map<String, Object>) result.getLocalContext()).containsKeys("comment1", "comment2");
    }
  }

  @Test
  public void articleComments_should_handle_null_user_with_data() {
    DgsDataFetchingEnvironment dfe = mock(DgsDataFetchingEnvironment.class);
    Article article = Article.newBuilder().slug("article-slug").build();
    when(dfe.getSource()).thenReturn(article);
    
    Map<String, ArticleData> localContext = new HashMap<>();
    ArticleData articleData = mock(ArticleData.class);
    when(articleData.getId()).thenReturn("article1");
    localContext.put("article-slug", articleData);
    when(dfe.getLocalContext()).thenReturn(localContext);

    CommentData comment = createMockCommentData("comment1", "Anonymous comment");

    CursorPager<CommentData> mockResult = mock(CursorPager.class);
    when(mockResult.getData()).thenReturn(Arrays.asList(comment));
    when(mockResult.getStartCursor()).thenReturn(new DateTimeCursor(DateTime.now()));
    when(mockResult.getEndCursor()).thenReturn(new DateTimeCursor(DateTime.now()));
    when(mockResult.hasPrevious()).thenReturn(false);
    when(mockResult.hasNext()).thenReturn(false);

    when(commentQueryService.findByArticleIdWithCursor(
        eq("article1"), eq(null), any(CursorPageParameter.class)))
        .thenReturn(mockResult);

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.empty());

      DataFetcherResult<CommentsConnection> result = commentDatafetcher.articleComments(10, null, null, null, dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
      assertThat(result.getData().getEdges()).hasSize(1);
      assertThat(result.getData().getEdges().get(0).getNode().getId()).isEqualTo("comment1");
      assertThat((Map<String, Object>) result.getLocalContext()).containsKey("comment1");
    }
  }

  @Test
  public void articleComments_should_handle_last_parameter() {
    DgsDataFetchingEnvironment dfe = mock(DgsDataFetchingEnvironment.class);
    Article article = Article.newBuilder().slug("article-slug").build();
    when(dfe.getSource()).thenReturn(article);
    
    Map<String, ArticleData> localContext = new HashMap<>();
    ArticleData articleData = mock(ArticleData.class);
    when(articleData.getId()).thenReturn("article1");
    localContext.put("article-slug", articleData);
    when(dfe.getLocalContext()).thenReturn(localContext);

    CursorPager<CommentData> mockResult = mock(CursorPager.class);
    when(mockResult.getData()).thenReturn(Collections.emptyList());
    when(mockResult.getStartCursor()).thenReturn(null);
    when(mockResult.getEndCursor()).thenReturn(null);
    when(mockResult.hasPrevious()).thenReturn(false);
    when(mockResult.hasNext()).thenReturn(false);

    when(commentQueryService.findByArticleIdWithCursor(
        eq("article1"), any(User.class), any(CursorPageParameter.class)))
        .thenReturn(mockResult);

    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));

      DataFetcherResult<CommentsConnection> result = commentDatafetcher.articleComments(null, null, 10, "1234567890", dfe);

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
      assertThat(result.getData().getEdges()).isEmpty();
    }
  }

  private CommentData createMockCommentData(String id, String body) {
    CommentData commentData = mock(CommentData.class);
    when(commentData.getId()).thenReturn(id);
    when(commentData.getBody()).thenReturn(body);
    when(commentData.getCursor()).thenReturn(new DateTimeCursor(DateTime.now()));
    return commentData;
  }
}
