package io.spring.graphql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import io.spring.api.exception.NoAuthorizationException;
import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.CommentQueryService;
import io.spring.application.data.CommentData;
import io.spring.application.data.ProfileData;
import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.comment.Comment;
import io.spring.core.comment.CommentRepository;
import io.spring.core.service.AuthorizationService;
import io.spring.core.user.User;
import io.spring.graphql.exception.AuthenticationException;
import io.spring.graphql.types.DeletionStatus;
import java.util.Arrays;
import java.util.Optional;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CommentMutationMissingCoverageTest {

  @Mock private ArticleRepository articleRepository;
  @Mock private CommentRepository commentRepository;
  @Mock private CommentQueryService commentQueryService;

  private CommentMutation commentMutation;

  @BeforeEach
  void setUp() {
    commentMutation =
        new CommentMutation(articleRepository, commentRepository, commentQueryService);
  }

  @Test
  void should_handle_createComment_when_article_not_found() {
    try (MockedStatic<SecurityUtil> securityUtilMock = Mockito.mockStatic(SecurityUtil.class)) {
      User currentUser = new User("user@example.com", "testuser", "123", "", "");
      securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(currentUser));

      when(articleRepository.findBySlug("non-existent-slug")).thenReturn(Optional.empty());

      assertThatThrownBy(() -> commentMutation.createComment("non-existent-slug", "Test comment"))
          .isInstanceOf(ResourceNotFoundException.class);
    }
  }

  @Test
  void should_handle_deleteComment_when_comment_not_found() {
    try (MockedStatic<SecurityUtil> securityUtilMock = Mockito.mockStatic(SecurityUtil.class)) {
      User currentUser = new User("user@example.com", "testuser", "123", "", "");
      securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(currentUser));

      Article article =
          new Article(
              "Test Title",
              "Test Description",
              "Test Body",
              Arrays.asList("tag1"),
              currentUser.getId());
      when(articleRepository.findBySlug("test-slug")).thenReturn(Optional.of(article));
      when(commentRepository.findById(article.getId(), "non-existent-comment"))
          .thenReturn(Optional.empty());

      assertThatThrownBy(() -> commentMutation.removeComment("test-slug", "non-existent-comment"))
          .isInstanceOf(ResourceNotFoundException.class);
    }
  }

  @Test
  void should_handle_deleteComment_when_user_not_authorized() {
    try (MockedStatic<SecurityUtil> securityUtilMock = Mockito.mockStatic(SecurityUtil.class);
        MockedStatic<AuthorizationService> authServiceMock =
            Mockito.mockStatic(AuthorizationService.class)) {

      User currentUser = new User("user@example.com", "testuser", "123", "", "");
      User commentAuthor = new User("other@example.com", "otheruser", "456", "", "");
      securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(currentUser));

      Article article =
          new Article(
              "Test Title",
              "Test Description",
              "Test Body",
              Arrays.asList("tag1"),
              commentAuthor.getId());
      Comment existingComment = new Comment("Test comment", commentAuthor.getId(), article.getId());

      when(articleRepository.findBySlug("test-slug")).thenReturn(Optional.of(article));
      when(commentRepository.findById(article.getId(), "comment-id"))
          .thenReturn(Optional.of(existingComment));
      authServiceMock
          .when(() -> AuthorizationService.canWriteComment(currentUser, article, existingComment))
          .thenReturn(false);

      assertThatThrownBy(() -> commentMutation.removeComment("test-slug", "comment-id"))
          .isInstanceOf(NoAuthorizationException.class);
    }
  }

  @Test
  void should_handle_createComment_successful_path() {
    try (MockedStatic<SecurityUtil> securityUtilMock = Mockito.mockStatic(SecurityUtil.class)) {
      User currentUser = new User("user@example.com", "testuser", "123", "", "");
      securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(currentUser));

      Article article =
          new Article(
              "Test Title",
              "Test Description",
              "Test Body",
              Arrays.asList("tag1"),
              currentUser.getId());
      when(articleRepository.findBySlug("test-slug")).thenReturn(Optional.of(article));

      Comment savedComment = new Comment("Test comment", currentUser.getId(), article.getId());
      doNothing().when(commentRepository).save(any(Comment.class));

      ProfileData profileData =
          new ProfileData(
              currentUser.getId(),
              currentUser.getUsername(),
              currentUser.getBio(),
              currentUser.getImage(),
              false);

      CommentData commentData =
          new CommentData(
              savedComment.getId(),
              savedComment.getBody(),
              savedComment.getArticleId(),
              DateTime.now(),
              DateTime.now(),
              profileData);

      when(commentQueryService.findById(any(String.class), eq(currentUser)))
          .thenReturn(Optional.of(commentData));

      var result = commentMutation.createComment("test-slug", "Test comment");

      assertThat(result).isNotNull();
      assertThat(result.getData()).isNotNull();
    }
  }

  @Test
  void should_handle_deleteComment_successful_path() {
    try (MockedStatic<SecurityUtil> securityUtilMock = Mockito.mockStatic(SecurityUtil.class);
        MockedStatic<AuthorizationService> authServiceMock =
            Mockito.mockStatic(AuthorizationService.class)) {

      User currentUser = new User("user@example.com", "testuser", "123", "", "");
      securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(currentUser));

      Article article =
          new Article(
              "Test Title",
              "Test Description",
              "Test Body",
              Arrays.asList("tag1"),
              currentUser.getId());
      Comment existingComment = new Comment("Test comment", currentUser.getId(), article.getId());

      when(articleRepository.findBySlug("test-slug")).thenReturn(Optional.of(article));
      when(commentRepository.findById(article.getId(), "comment-id"))
          .thenReturn(Optional.of(existingComment));
      authServiceMock
          .when(() -> AuthorizationService.canWriteComment(currentUser, article, existingComment))
          .thenReturn(true);
      doNothing().when(commentRepository).remove(existingComment);

      DeletionStatus result = commentMutation.removeComment("test-slug", "comment-id");

      assertThat(result).isNotNull();
      assertThat(result.getSuccess()).isTrue();
    }
  }

  @Test
  void should_handle_createComment_when_no_authentication() {
    try (MockedStatic<SecurityUtil> securityUtilMock = Mockito.mockStatic(SecurityUtil.class)) {
      securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.empty());

      assertThatThrownBy(() -> commentMutation.createComment("test-slug", "Test comment"))
          .isInstanceOf(AuthenticationException.class);
    }
  }

  @Test
  void should_handle_deleteComment_when_no_authentication() {
    try (MockedStatic<SecurityUtil> securityUtilMock = Mockito.mockStatic(SecurityUtil.class)) {
      securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.empty());

      assertThatThrownBy(() -> commentMutation.removeComment("test-slug", "comment-id"))
          .isInstanceOf(AuthenticationException.class);
    }
  }
}
