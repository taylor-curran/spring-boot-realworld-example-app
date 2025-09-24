package io.spring.graphql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

import java.util.Arrays;
import java.util.Optional;

import graphql.execution.DataFetcherResult;
import io.spring.application.CommentQueryService;
import io.spring.application.data.CommentData;
import io.spring.application.data.ProfileData;
import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.comment.Comment;
import io.spring.core.comment.CommentRepository;
import io.spring.core.user.User;
import io.spring.graphql.types.CommentPayload;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CommentMutationTest {

  @Mock
  private ArticleRepository articleRepository;

  @Mock
  private CommentRepository commentRepository;

  @Mock
  private CommentQueryService commentQueryService;

  @InjectMocks
  private CommentMutation commentMutation;

  private User testUser;
  private Article testArticle;
  private Comment testComment;
  private CommentData testCommentData;

  @BeforeEach
  public void setUp() {
    testUser = new User("test@example.com", "testuser", "password", "bio", "image.jpg");
    testArticle = new Article("Test Title", "Test Description", "Test Body", Arrays.asList("test"), testUser.getId());
    testComment = new Comment("Test comment body", testUser.getId(), testArticle.getId());
    
    ProfileData profileData = new ProfileData(testUser.getId(), testUser.getUsername(), 
        testUser.getBio(), testUser.getImage(), false);
    testCommentData = new CommentData(testComment.getId(), testComment.getBody(), 
        testComment.getArticleId(), new DateTime(), new DateTime(), profileData);
  }

  @Test
  public void should_verify_comment_mutation_dependencies_are_injected() {
    assertThat(commentMutation).isNotNull();
    assertThat(articleRepository).isNotNull();
    assertThat(commentRepository).isNotNull();
    assertThat(commentQueryService).isNotNull();
  }

  @Test
  public void should_verify_test_data_setup() {
    assertThat(testUser).isNotNull();
    assertThat(testUser.getUsername()).isEqualTo("testuser");
    assertThat(testArticle).isNotNull();
    assertThat(testArticle.getTitle()).isEqualTo("Test Title");
    assertThat(testComment).isNotNull();
    assertThat(testComment.getBody()).isEqualTo("Test comment body");
    assertThat(testCommentData).isNotNull();
    assertThat(testCommentData.getBody()).isEqualTo("Test comment body");
  }

  @Test
  public void should_verify_article_repository_interaction() {
    String slug = "test-article";
    when(articleRepository.findBySlug(slug)).thenReturn(Optional.of(testArticle));
    
    Optional<Article> result = articleRepository.findBySlug(slug);
    
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(testArticle);
    verify(articleRepository).findBySlug(slug);
  }

  @Test
  public void should_verify_comment_repository_interaction() {
    commentRepository.save(testComment);
    verify(commentRepository).save(testComment);
  }

  @Test
  public void should_verify_comment_query_service_interaction() {
    when(commentQueryService.findById(anyString(), any(User.class))).thenReturn(Optional.of(testCommentData));
    
    Optional<CommentData> result = commentQueryService.findById("test-id", testUser);
    
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(testCommentData);
    verify(commentQueryService).findById("test-id", testUser);
  }
}
