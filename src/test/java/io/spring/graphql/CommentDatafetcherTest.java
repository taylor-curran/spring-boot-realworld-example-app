package io.spring.graphql;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import io.spring.application.CommentQueryService;
import io.spring.application.CursorPageParameter;
import io.spring.application.CursorPager;
import io.spring.application.CursorPager.Direction;
import io.spring.application.data.ArticleData;
import io.spring.application.data.CommentData;
import io.spring.application.data.ProfileData;
import io.spring.core.user.User;
import io.spring.graphql.SecurityUtil;
import io.spring.graphql.types.Article;
import io.spring.graphql.types.Comment;
import io.spring.graphql.types.CommentsConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class CommentDatafetcherTest {

  @Mock private CommentQueryService commentQueryService;
  @Mock private DataFetchingEnvironment dataFetchingEnvironment;

  private CommentDatafetcher commentDatafetcher;
  private CommentData testCommentData;
  private ArticleData testArticleData;
  private User user;
  private Article testArticle;

  @BeforeEach
  public void setUp() {
    commentDatafetcher = new CommentDatafetcher(commentQueryService);
    user = new User("test@example.com", "testuser", "password123", "Test bio", "test.jpg");
    
    ProfileData profileData = new ProfileData("author-id", "testauthor", "Author Bio", "author.jpg", false);
    
    testCommentData = new CommentData(
        "comment-id",
        "Test comment body",
        "article-id",
        DateTime.now(),
        DateTime.now(),
        profileData
    );

    testArticleData = new ArticleData(
        "article-id",
        "test-article", 
        "Test Article",
        "Test Description",
        "Test Body",
        false,
        0,
        DateTime.now(),
        DateTime.now(),
        Arrays.asList("tag1", "tag2"),
        profileData
    );

    testArticle = Article.newBuilder()
        .slug("test-article")
        .title("Test Article")
        .description("Test Description")
        .body("Test Body")
        .build();
  }

  @AfterEach
  public void cleanup() {
    SecurityContextHolder.clearContext();
  }

  @Test
  public void should_throw_exception_when_both_first_and_last_are_null() {
    try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
      DgsDataFetchingEnvironment mockDfe = mock(DgsDataFetchingEnvironment.class);
      
      assertThrows(IllegalArgumentException.class, () -> {
        commentDatafetcher.articleComments(null, null, null, null, mockDfe);
      });
    }
  }

  @Test
  public void should_get_article_comments_with_first_parameter() {
    try (MockedStatic<SecurityUtil> securityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(java.util.Optional.of(user));
      
      Map<String, ArticleData> localContext = new HashMap<>();
      localContext.put("test-article", testArticleData);
      
      CursorPager<CommentData> mockPager = createMockPager(Arrays.asList(testCommentData));
      when(commentQueryService.findByArticleIdWithCursor(eq("article-id"), eq(user), any(CursorPageParameter.class)))
          .thenReturn(mockPager);
      
      DgsDataFetchingEnvironment mockDfe = mock(DgsDataFetchingEnvironment.class);
      when(mockDfe.getSource()).thenReturn(testArticle);
      when(mockDfe.getLocalContext()).thenReturn(localContext);
      
      DataFetcherResult<CommentsConnection> result = commentDatafetcher.articleComments(10, null, null, null, mockDfe);
      
      assertThat(result, notNullValue());
      assertThat(result.getData(), notNullValue());
      verify(commentQueryService).findByArticleIdWithCursor(eq("article-id"), eq(user), any(CursorPageParameter.class));
    }
  }

  @Test
  public void should_get_article_comments_with_last_parameter() {
    try (MockedStatic<SecurityUtil> securityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(java.util.Optional.of(user));
      
      Map<String, ArticleData> localContext = new HashMap<>();
      localContext.put("test-article", testArticleData);
      
      CursorPager<CommentData> mockPager = createMockPager(Arrays.asList(testCommentData));
      when(commentQueryService.findByArticleIdWithCursor(eq("article-id"), eq(user), any(CursorPageParameter.class)))
          .thenReturn(mockPager);
      
      DgsDataFetchingEnvironment mockDfe = mock(DgsDataFetchingEnvironment.class);
      when(mockDfe.getSource()).thenReturn(testArticle);
      when(mockDfe.getLocalContext()).thenReturn(localContext);
      
      DataFetcherResult<CommentsConnection> result = commentDatafetcher.articleComments(null, null, 10, null, mockDfe);
      
      assertThat(result, notNullValue());
      assertThat(result.getData(), notNullValue());
      verify(commentQueryService).findByArticleIdWithCursor(eq("article-id"), eq(user), any(CursorPageParameter.class));
    }
  }

  @Test
  public void should_get_comment_from_local_context() {
    DgsDataFetchingEnvironment mockDfe = mock(DgsDataFetchingEnvironment.class);
    when(mockDfe.getLocalContext()).thenReturn(testCommentData);
    
    DataFetcherResult<Comment> result = commentDatafetcher.getComment(mockDfe);
    
    assertThat(result, notNullValue());
    assertThat(result.getData(), notNullValue());
    assertThat(result.getData().getId(), equalTo("comment-id"));
    assertThat(result.getData().getBody(), equalTo("Test comment body"));
  }

  @Test
  public void should_handle_empty_comment_list() {
    try (MockedStatic<SecurityUtil> securityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(java.util.Optional.of(user));
      
      Map<String, ArticleData> localContext = new HashMap<>();
      localContext.put("test-article", testArticleData);
      
      CursorPager<CommentData> emptyPager = createMockPager(Collections.emptyList());
      when(commentQueryService.findByArticleIdWithCursor(eq("article-id"), eq(user), any(CursorPageParameter.class)))
          .thenReturn(emptyPager);
      
      DgsDataFetchingEnvironment mockDfe = mock(DgsDataFetchingEnvironment.class);
      when(mockDfe.getSource()).thenReturn(testArticle);
      when(mockDfe.getLocalContext()).thenReturn(localContext);
      
      DataFetcherResult<CommentsConnection> result = commentDatafetcher.articleComments(10, null, null, null, mockDfe);
      
      assertThat(result, notNullValue());
      assertThat(result.getData(), notNullValue());
      assertThat(result.getData().getEdges().size(), is(0));
    }
  }

  @Test
  public void should_handle_anonymous_user() {
    try (MockedStatic<SecurityUtil> securityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(java.util.Optional.empty());
      
      Map<String, ArticleData> localContext = new HashMap<>();
      localContext.put("test-article", testArticleData);
      
      CursorPager<CommentData> mockPager = createMockPager(Arrays.asList(testCommentData));
      when(commentQueryService.findByArticleIdWithCursor(eq("article-id"), eq(null), any(CursorPageParameter.class)))
          .thenReturn(mockPager);
      
      DgsDataFetchingEnvironment mockDfe = mock(DgsDataFetchingEnvironment.class);
      when(mockDfe.getSource()).thenReturn(testArticle);
      when(mockDfe.getLocalContext()).thenReturn(localContext);
      
      DataFetcherResult<CommentsConnection> result = commentDatafetcher.articleComments(10, null, null, null, mockDfe);
      
      assertThat(result, notNullValue());
      assertThat(result.getData(), notNullValue());
      verify(commentQueryService).findByArticleIdWithCursor(eq("article-id"), eq(null), any(CursorPageParameter.class));
    }
  }

  @Test
  public void should_handle_pagination_with_after_cursor() {
    try (MockedStatic<SecurityUtil> securityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(java.util.Optional.of(user));
      
      Map<String, ArticleData> localContext = new HashMap<>();
      localContext.put("test-article", testArticleData);
      
      CursorPager<CommentData> mockPager = createMockPager(Arrays.asList(testCommentData));
      when(commentQueryService.findByArticleIdWithCursor(eq("article-id"), eq(user), any(CursorPageParameter.class)))
          .thenReturn(mockPager);
      
      DgsDataFetchingEnvironment mockDfe = mock(DgsDataFetchingEnvironment.class);
      when(mockDfe.getSource()).thenReturn(testArticle);
      when(mockDfe.getLocalContext()).thenReturn(localContext);
      
      DataFetcherResult<CommentsConnection> result = commentDatafetcher.articleComments(10, "1672531200000", null, null, mockDfe);
      
      assertThat(result, notNullValue());
      assertThat(result.getData(), notNullValue());
      verify(commentQueryService).findByArticleIdWithCursor(eq("article-id"), eq(user), any(CursorPageParameter.class));
    }
  }

  @Test
  public void should_handle_pagination_with_before_cursor() {
    try (MockedStatic<SecurityUtil> securityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(java.util.Optional.of(user));
      
      Map<String, ArticleData> localContext = new HashMap<>();
      localContext.put("test-article", testArticleData);
      
      CursorPager<CommentData> mockPager = createMockPager(Arrays.asList(testCommentData));
      when(commentQueryService.findByArticleIdWithCursor(eq("article-id"), eq(user), any(CursorPageParameter.class)))
          .thenReturn(mockPager);
      
      DgsDataFetchingEnvironment mockDfe = mock(DgsDataFetchingEnvironment.class);
      when(mockDfe.getSource()).thenReturn(testArticle);
      when(mockDfe.getLocalContext()).thenReturn(localContext);
      
      DataFetcherResult<CommentsConnection> result = commentDatafetcher.articleComments(null, null, 10, "1672531200000", mockDfe);
      
      assertThat(result, notNullValue());
      assertThat(result.getData(), notNullValue());
      verify(commentQueryService).findByArticleIdWithCursor(eq("article-id"), eq(user), any(CursorPageParameter.class));
    }
  }

  @Test
  public void should_build_comment_result_with_proper_formatting() {
    DgsDataFetchingEnvironment mockDfe = mock(DgsDataFetchingEnvironment.class);
    when(mockDfe.getLocalContext()).thenReturn(testCommentData);
    
    DataFetcherResult<Comment> result = commentDatafetcher.getComment(mockDfe);
    
    assertThat(result, notNullValue());
    assertThat(result.getData(), notNullValue());
    assertThat(result.getData().getId(), equalTo("comment-id"));
    assertThat(result.getData().getBody(), equalTo("Test comment body"));
    assertThat(result.getData().getCreatedAt(), notNullValue());
    assertThat(result.getData().getUpdatedAt(), notNullValue());
  }

  private CursorPager<CommentData> createMockPager(List<CommentData> data) {
    return new CursorPager<>(data, Direction.NEXT, false);
  }
}
