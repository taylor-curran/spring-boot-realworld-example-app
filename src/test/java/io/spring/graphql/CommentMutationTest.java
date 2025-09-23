package io.spring.graphql;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import graphql.execution.DataFetcherResult;
import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.CommentQueryService;
import io.spring.application.data.CommentData;
import io.spring.application.data.ProfileData;
import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.comment.Comment;
import io.spring.core.comment.CommentRepository;
import io.spring.core.user.User;
import io.spring.api.exception.NoAuthorizationException;
import io.spring.graphql.exception.AuthenticationException;
import io.spring.graphql.types.CommentPayload;
import io.spring.graphql.types.DeletionStatus;
import java.util.Arrays;
import java.util.Optional;
import org.joda.time.DateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class CommentMutationTest {

  @Mock private ArticleRepository articleRepository;
  @Mock private CommentRepository commentRepository;
  @Mock private CommentQueryService commentQueryService;

  private CommentMutation commentMutation;
  private User currentUser;
  private Article article;
  private Comment comment;
  private CommentData commentData;

  @BeforeEach
  public void setUp() {
    commentMutation = new CommentMutation(articleRepository, commentRepository, commentQueryService);
    currentUser = new User("current@example.com", "currentuser", "password123", "Current bio", "current.jpg");
    article = new Article("Test Article", "Test Description", "Test Body", Arrays.asList("tag1", "tag2"), currentUser.getId());
    comment = new Comment("Test comment body", currentUser.getId(), article.getId());
    
    ProfileData profileData = new ProfileData(
        currentUser.getId(),
        currentUser.getUsername(),
        currentUser.getBio(),
        currentUser.getImage(),
        false
    );
    
    commentData = new CommentData(
        comment.getId(),
        comment.getBody(),
        article.getId(),
        DateTime.now(),
        DateTime.now(),
        profileData
    );
  }

  @AfterEach
  public void cleanup() {
    SecurityContextHolder.clearContext();
  }

  @Test
  public void should_create_comment_successfully() {
    SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(currentUser, null));
    
    when(articleRepository.findBySlug(eq(article.getSlug()))).thenReturn(Optional.of(article));
    doNothing().when(commentRepository).save(any(Comment.class));
    when(commentQueryService.findById(any(String.class), eq(currentUser)))
        .thenReturn(Optional.of(commentData));

    DataFetcherResult<CommentPayload> result = commentMutation.createComment(article.getSlug(), "Test comment body");

    assertThat(result, notNullValue());
    assertThat(result.getData(), notNullValue());
    verify(articleRepository).findBySlug(eq(article.getSlug()));
    verify(commentRepository).save(any(Comment.class));
    verify(commentQueryService).findById(any(String.class), eq(currentUser));
  }

  @Test
  public void should_remove_comment_successfully() {
    SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(currentUser, null));
    
    when(articleRepository.findBySlug(eq(article.getSlug()))).thenReturn(Optional.of(article));
    when(commentRepository.findById(eq(article.getId()), eq(comment.getId()))).thenReturn(Optional.of(comment));
    doNothing().when(commentRepository).remove(eq(comment));

    DeletionStatus result = commentMutation.removeComment(article.getSlug(), comment.getId());

    assertThat(result, notNullValue());
    assertThat(result.getSuccess(), is(true));
    verify(articleRepository).findBySlug(eq(article.getSlug()));
    verify(commentRepository).findById(eq(article.getId()), eq(comment.getId()));
    verify(commentRepository).remove(eq(comment));
  }

  @Test
  public void should_throw_exception_when_creating_comment_without_authentication() {
    SecurityContextHolder.getContext().setAuthentication(
        new AnonymousAuthenticationToken("key", "anonymous", Arrays.asList(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));

    assertThrows(AuthenticationException.class, () -> {
      commentMutation.createComment(article.getSlug(), "Test comment body");
    });
  }

  @Test
  public void should_throw_exception_when_removing_comment_without_authentication() {
    SecurityContextHolder.getContext().setAuthentication(
        new AnonymousAuthenticationToken("key", "anonymous", Arrays.asList(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));

    assertThrows(AuthenticationException.class, () -> {
      commentMutation.removeComment(article.getSlug(), comment.getId());
    });
  }

  @Test
  public void should_throw_exception_when_article_not_found_for_create() {
    SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(currentUser, null));
    
    when(articleRepository.findBySlug(eq("nonexistent"))).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> {
      commentMutation.createComment("nonexistent", "Test comment body");
    });
  }

  @Test
  public void should_throw_exception_when_article_not_found_for_remove() {
    SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(currentUser, null));
    
    when(articleRepository.findBySlug(eq("nonexistent"))).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> {
      commentMutation.removeComment("nonexistent", comment.getId());
    });
  }

  @Test
  public void should_throw_exception_when_comment_not_found_for_remove() {
    SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(currentUser, null));
    
    when(articleRepository.findBySlug(eq(article.getSlug()))).thenReturn(Optional.of(article));
    when(commentRepository.findById(eq(article.getId()), eq("nonexistent"))).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> {
      commentMutation.removeComment(article.getSlug(), "nonexistent");
    });
  }

  @Test
  public void should_handle_empty_comment_body() {
    SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(currentUser, null));
    
    when(articleRepository.findBySlug(eq(article.getSlug()))).thenReturn(Optional.of(article));
    doNothing().when(commentRepository).save(any(Comment.class));
    when(commentQueryService.findById(any(String.class), eq(currentUser)))
        .thenReturn(Optional.of(commentData));

    DataFetcherResult<CommentPayload> result = commentMutation.createComment(article.getSlug(), "");

    assertThat(result, notNullValue());
    assertThat(result.getData(), notNullValue());
    verify(articleRepository).findBySlug(eq(article.getSlug()));
    verify(commentRepository).save(any(Comment.class));
  }

  @Test
  public void should_handle_comment_creation_with_long_body() {
    SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(currentUser, null));
    
    String longBody = "This is a very long comment body that exceeds normal length to test edge cases in comment creation functionality and ensure proper handling of large text inputs.";
    
    when(articleRepository.findBySlug(eq(article.getSlug()))).thenReturn(Optional.of(article));
    doNothing().when(commentRepository).save(any(Comment.class));
    when(commentQueryService.findById(any(String.class), eq(currentUser)))
        .thenReturn(Optional.of(commentData));

    DataFetcherResult<CommentPayload> result = commentMutation.createComment(article.getSlug(), longBody);

    assertThat(result, notNullValue());
    assertThat(result.getData(), notNullValue());
    verify(articleRepository).findBySlug(eq(article.getSlug()));
    verify(commentRepository).save(any(Comment.class));
  }

  @Test
  public void should_throw_authorization_exception_when_user_cannot_remove_comment() {
    User articleOwner = new User("owner@example.com", "articleowner", "password123", "Owner bio", "owner.jpg");
    User commentOwner = new User("commenter@example.com", "commenter", "password456", "Commenter bio", "commenter.jpg");
    User unauthorizedUser = new User("unauthorized@example.com", "unauthorized", "password789", "Unauthorized bio", "unauthorized.jpg");
    
    Article otherUserArticle = new Article("Other Article", "Other Description", "Other Body", Arrays.asList("tag1"), articleOwner.getId());
    Comment otherUserComment = new Comment("Comment by commenter", commentOwner.getId(), otherUserArticle.getId());
    
    SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(unauthorizedUser, null));
    
    when(articleRepository.findBySlug(eq(otherUserArticle.getSlug()))).thenReturn(Optional.of(otherUserArticle));
    when(commentRepository.findById(eq(otherUserArticle.getId()), eq(otherUserComment.getId()))).thenReturn(Optional.of(otherUserComment));

    assertThrows(NoAuthorizationException.class, () -> {
      commentMutation.removeComment(otherUserArticle.getSlug(), otherUserComment.getId());
    });
    
    verify(articleRepository).findBySlug(eq(otherUserArticle.getSlug()));
    verify(commentRepository).findById(eq(otherUserArticle.getId()), eq(otherUserComment.getId()));
  }
}
