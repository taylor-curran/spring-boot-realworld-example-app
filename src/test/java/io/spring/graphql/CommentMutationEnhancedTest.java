package io.spring.graphql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import graphql.execution.DataFetcherResult;
import io.spring.api.exception.NoAuthorizationException;
import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.CommentQueryService;
import io.spring.application.data.CommentData;
import io.spring.application.data.ProfileData;
import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.comment.Comment;
import io.spring.core.comment.CommentRepository;
import io.spring.core.user.User;
import io.spring.graphql.exception.AuthenticationException;
import io.spring.graphql.types.CommentPayload;
import java.util.Arrays;
import java.util.Optional;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CommentMutationEnhancedTest {

  @Mock private ArticleRepository articleRepository;

  @Mock private CommentRepository commentRepository;

  @Mock private CommentQueryService commentQueryService;

  @InjectMocks private CommentMutation commentMutation;

  @Test
  public void createComment_should_throw_authentication_exception_when_user_not_authenticated() {
    try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.empty());

      assertThrows(
          AuthenticationException.class,
          () -> commentMutation.createComment("test-slug", "Test comment body"));
    }

    verify(articleRepository, never()).findBySlug(any());
    verify(commentRepository, never()).save(any());
  }

  @Test
  public void createComment_should_throw_resource_not_found_when_article_not_exists() {
    User mockUser = createMockUser();

    try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(mockUser));
      when(articleRepository.findBySlug("nonexistent-slug")).thenReturn(Optional.empty());

      assertThrows(
          ResourceNotFoundException.class,
          () -> commentMutation.createComment("nonexistent-slug", "Test comment body"));
    }

    verify(articleRepository).findBySlug("nonexistent-slug");
    verify(commentRepository, never()).save(any());
  }

  @Test
  public void createComment_should_create_comment_successfully() {
    User mockUser = createMockUser();
    Article mockArticle = createMockArticle();
    CommentData mockCommentData = createMockCommentData();

    try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(mockUser));
      when(articleRepository.findBySlug("test-slug")).thenReturn(Optional.of(mockArticle));
      doNothing().when(commentRepository).save(any(Comment.class));
      when(commentQueryService.findById(any(), eq(mockUser)))
          .thenReturn(Optional.of(mockCommentData));

      DataFetcherResult<CommentPayload> result =
          commentMutation.createComment("test-slug", "Test comment body");

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
      verify(articleRepository).findBySlug("test-slug");
      verify(commentRepository).save(any(Comment.class));
      verify(commentQueryService).findById(any(), eq(mockUser));
    }
  }

  @Test
  public void removeComment_should_throw_authentication_exception_when_user_not_authenticated() {
    try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.empty());

      assertThrows(
          AuthenticationException.class,
          () -> commentMutation.removeComment("test-slug", "comment-id"));
    }
  }

  @Test
  public void removeComment_should_throw_resource_not_found_when_article_not_exists() {
    User mockUser = createMockUser();

    try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(mockUser));
      when(articleRepository.findBySlug("nonexistent-slug")).thenReturn(Optional.empty());

      assertThrows(
          ResourceNotFoundException.class,
          () -> commentMutation.removeComment("nonexistent-slug", "comment-id"));
    }
  }

  @Test
  public void removeComment_should_throw_no_authorization_exception_when_user_cannot_delete() {
    User mockUser = createMockUser();
    Article mockArticle = createMockArticle();
    Comment mockComment = createMockComment();

    try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
      mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(mockUser));
      when(articleRepository.findBySlug("test-slug")).thenReturn(Optional.of(mockArticle));
      when(commentRepository.findById(mockArticle.getId(), "comment-id"))
          .thenReturn(Optional.of(mockComment));

      assertThrows(
          NoAuthorizationException.class,
          () -> commentMutation.removeComment("test-slug", "comment-id"));

      verify(commentRepository).findById(mockArticle.getId(), "comment-id");
      verify(commentRepository, never()).remove(mockComment);
    }
  }

  private User createMockUser() {
    User user = new User("test@example.com", "testuser", "password", "bio", "image");
    return user;
  }

  private Article createMockArticle() {
    Article article =
        new Article(
            "Test Title", "Test Description", "Test Body", Arrays.asList("tag1"), "user-id");
    return article;
  }

  private Comment createMockComment() {
    Comment comment = new Comment("Test comment body", "user-id", "article-id");
    return comment;
  }

  private CommentData createMockCommentData() {
    return new CommentData(
        "comment-id",
        "Test comment body",
        "article-id",
        DateTime.now(),
        DateTime.now(),
        createMockProfileData());
  }

  private ProfileData createMockProfileData() {
    return new ProfileData("user-id", "testuser", "Test Bio", "image.jpg", false);
  }
}
