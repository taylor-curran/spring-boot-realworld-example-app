package io.spring.core.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import io.spring.core.article.Article;
import io.spring.core.comment.Comment;
import io.spring.core.user.User;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AuthorizationServiceTest {

  private User articleOwner;
  private User commentOwner;
  private User otherUser;
  private Article article;
  private Comment comment;

  @BeforeEach
  public void setUp() {
    articleOwner = new User("article@example.com", "articleowner", "password123", "Article owner bio", "article.jpg");
    commentOwner = new User("comment@example.com", "commentowner", "password456", "Comment owner bio", "comment.jpg");
    otherUser = new User("other@example.com", "otheruser", "password789", "Other user bio", "other.jpg");
    
    article = new Article("Test Article", "Test description", "Test body", Collections.emptyList(), articleOwner.getId());
    comment = new Comment("Test comment", commentOwner.getId(), article.getId());
  }

  @Test
  public void should_create_authorization_service_instance() {
    AuthorizationService service = new AuthorizationService();
    assertThat(service, is(org.hamcrest.CoreMatchers.notNullValue()));
  }

  @Test
  public void should_allow_article_owner_to_write_article() {
    boolean canWrite = AuthorizationService.canWriteArticle(articleOwner, article);
    assertThat(canWrite, is(true));
  }

  @Test
  public void should_not_allow_non_owner_to_write_article() {
    boolean canWrite = AuthorizationService.canWriteArticle(otherUser, article);
    assertThat(canWrite, is(false));
  }

  @Test
  public void should_not_allow_comment_owner_to_write_article() {
    boolean canWrite = AuthorizationService.canWriteArticle(commentOwner, article);
    assertThat(canWrite, is(false));
  }

  @Test
  public void should_allow_article_owner_to_write_comment() {
    boolean canWrite = AuthorizationService.canWriteComment(articleOwner, article, comment);
    assertThat(canWrite, is(true));
  }

  @Test
  public void should_allow_comment_owner_to_write_comment() {
    boolean canWrite = AuthorizationService.canWriteComment(commentOwner, article, comment);
    assertThat(canWrite, is(true));
  }

  @Test
  public void should_not_allow_other_user_to_write_comment() {
    boolean canWrite = AuthorizationService.canWriteComment(otherUser, article, comment);
    assertThat(canWrite, is(false));
  }

  @Test
  public void should_handle_same_user_as_article_and_comment_owner() {
    Comment sameOwnerComment = new Comment("Same owner comment", articleOwner.getId(), article.getId());
    boolean canWrite = AuthorizationService.canWriteComment(articleOwner, article, sameOwnerComment);
    assertThat(canWrite, is(true));
  }

  @Test
  public void should_test_article_authorization_with_different_users() {
    User user1 = new User("user1@example.com", "user1", "pass1", "Bio1", "img1.jpg");
    User user2 = new User("user2@example.com", "user2", "pass2", "Bio2", "img2.jpg");
    Article user1Article = new Article("User1 Article", "Description", "Body", Collections.emptyList(), user1.getId());
    
    assertThat(AuthorizationService.canWriteArticle(user1, user1Article), is(true));
    assertThat(AuthorizationService.canWriteArticle(user2, user1Article), is(false));
  }

  @Test
  public void should_test_comment_authorization_with_different_scenarios() {
    User user1 = new User("user1@example.com", "user1", "pass1", "Bio1", "img1.jpg");
    User user2 = new User("user2@example.com", "user2", "pass2", "Bio2", "img2.jpg");
    User user3 = new User("user3@example.com", "user3", "pass3", "Bio3", "img3.jpg");
    
    Article user1Article = new Article("User1 Article", "Description", "Body", Arrays.asList("tag1", "tag2"), user1.getId());
    Comment user2Comment = new Comment("User2 comment", user2.getId(), user1Article.getId());
    
    assertThat(AuthorizationService.canWriteComment(user1, user1Article, user2Comment), is(true));
    assertThat(AuthorizationService.canWriteComment(user2, user1Article, user2Comment), is(true));
    assertThat(AuthorizationService.canWriteComment(user3, user1Article, user2Comment), is(false));
  }

  @Test
  public void should_handle_edge_case_with_null_checks() {
    User user = new User("test@example.com", "testuser", "password", "Bio", "img.jpg");
    Article userArticle = new Article("Test Article", "Description", "Body", Collections.emptyList(), user.getId());
    Comment userComment = new Comment("Test comment", user.getId(), userArticle.getId());
    
    assertThat(AuthorizationService.canWriteArticle(user, userArticle), is(true));
    assertThat(AuthorizationService.canWriteComment(user, userArticle, userComment), is(true));
  }

  @Test
  public void should_test_authorization_with_multiple_comment_scenarios() {
    Comment articleOwnerComment = new Comment("Article owner comment", articleOwner.getId(), article.getId());
    Comment otherUserComment = new Comment("Other user comment", otherUser.getId(), article.getId());
    
    assertThat(AuthorizationService.canWriteComment(articleOwner, article, articleOwnerComment), is(true));
    assertThat(AuthorizationService.canWriteComment(articleOwner, article, otherUserComment), is(true));
    assertThat(AuthorizationService.canWriteComment(commentOwner, article, articleOwnerComment), is(false));
    assertThat(AuthorizationService.canWriteComment(otherUser, article, articleOwnerComment), is(false));
  }
}
