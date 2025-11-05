package io.spring.core.service;

import static org.assertj.core.api.Assertions.assertThat;

import io.spring.core.article.Article;
import io.spring.core.comment.Comment;
import io.spring.core.user.User;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class AuthorizationServiceTest {

  @Test
  public void canWriteArticle_should_return_true_when_user_is_article_author() {
    User user = new User("test@example.com", "testuser", "password", "bio", "image");
    Article article =
        new Article(
            "Test Title", "Test Description", "Test Body", Arrays.asList("tag1"), user.getId());

    boolean result = AuthorizationService.canWriteArticle(user, article);

    assertThat(result).isTrue();
  }

  @Test
  public void canWriteArticle_should_return_false_when_user_is_not_article_author() {
    User user = new User("test@example.com", "testuser", "password", "bio", "image");
    User otherUser = new User("other@example.com", "otheruser", "password", "bio", "image");
    Article article =
        new Article(
            "Test Title",
            "Test Description",
            "Test Body",
            Arrays.asList("tag1"),
            otherUser.getId());

    boolean result = AuthorizationService.canWriteArticle(user, article);

    assertThat(result).isFalse();
  }

  @Test
  public void canWriteComment_should_return_true_when_user_is_article_author() {
    User user = new User("test@example.com", "testuser", "password", "bio", "image");
    User commentAuthor = new User("comment@example.com", "commentuser", "password", "bio", "image");
    Article article =
        new Article(
            "Test Title", "Test Description", "Test Body", Arrays.asList("tag1"), user.getId());
    Comment comment = new Comment("Test comment", commentAuthor.getId(), article.getId());

    boolean result = AuthorizationService.canWriteComment(user, article, comment);

    assertThat(result).isTrue();
  }

  @Test
  public void canWriteComment_should_return_true_when_user_is_comment_author() {
    User user = new User("test@example.com", "testuser", "password", "bio", "image");
    User articleAuthor = new User("article@example.com", "articleuser", "password", "bio", "image");
    Article article =
        new Article(
            "Test Title",
            "Test Description",
            "Test Body",
            Arrays.asList("tag1"),
            articleAuthor.getId());
    Comment comment = new Comment("Test comment", user.getId(), article.getId());

    boolean result = AuthorizationService.canWriteComment(user, article, comment);

    assertThat(result).isTrue();
  }

  @Test
  public void
      canWriteComment_should_return_false_when_user_is_neither_article_nor_comment_author() {
    User user = new User("test@example.com", "testuser", "password", "bio", "image");
    User articleAuthor = new User("article@example.com", "articleuser", "password", "bio", "image");
    User commentAuthor = new User("comment@example.com", "commentuser", "password", "bio", "image");
    Article article =
        new Article(
            "Test Title",
            "Test Description",
            "Test Body",
            Arrays.asList("tag1"),
            articleAuthor.getId());
    Comment comment = new Comment("Test comment", commentAuthor.getId(), article.getId());

    boolean result = AuthorizationService.canWriteComment(user, article, comment);

    assertThat(result).isFalse();
  }

  @Test
  public void canWriteComment_should_return_true_when_user_is_both_article_and_comment_author() {
    User user = new User("test@example.com", "testuser", "password", "bio", "image");
    Article article =
        new Article(
            "Test Title", "Test Description", "Test Body", Arrays.asList("tag1"), user.getId());
    Comment comment = new Comment("Test comment", user.getId(), article.getId());

    boolean result = AuthorizationService.canWriteComment(user, article, comment);

    assertThat(result).isTrue();
  }

  @Test
  public void canWriteArticle_should_handle_different_user_ids() {
    User user1 = new User("test1@example.com", "testuser1", "password", "bio", "image");
    User user2 = new User("test2@example.com", "testuser2", "password", "bio", "image");

    Article article1 =
        new Article(
            "Test Title 1", "Test Description", "Test Body", Arrays.asList("tag1"), user1.getId());
    Article article2 =
        new Article(
            "Test Title 2", "Test Description", "Test Body", Arrays.asList("tag1"), user2.getId());

    assertThat(AuthorizationService.canWriteArticle(user1, article1)).isTrue();
    assertThat(AuthorizationService.canWriteArticle(user1, article2)).isFalse();
    assertThat(AuthorizationService.canWriteArticle(user2, article1)).isFalse();
    assertThat(AuthorizationService.canWriteArticle(user2, article2)).isTrue();
  }

  @Test
  public void canWriteComment_should_handle_complex_scenarios() {
    User articleAuthor = new User("article@example.com", "articleuser", "password", "bio", "image");
    User commentAuthor = new User("comment@example.com", "commentuser", "password", "bio", "image");
    User randomUser = new User("random@example.com", "randomuser", "password", "bio", "image");

    Article article =
        new Article(
            "Test Title",
            "Test Description",
            "Test Body",
            Arrays.asList("tag1"),
            articleAuthor.getId());
    Comment comment = new Comment("Test comment", commentAuthor.getId(), article.getId());

    assertThat(AuthorizationService.canWriteComment(articleAuthor, article, comment)).isTrue();
    assertThat(AuthorizationService.canWriteComment(commentAuthor, article, comment)).isTrue();
    assertThat(AuthorizationService.canWriteComment(randomUser, article, comment)).isFalse();
  }
}
