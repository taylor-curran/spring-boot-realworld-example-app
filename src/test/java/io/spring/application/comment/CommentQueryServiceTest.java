package io.spring.application.comment;

import io.spring.application.CommentQueryService;
import io.spring.application.CursorPageParameter;
import io.spring.application.CursorPager;
import io.spring.application.CursorPager.Direction;
import io.spring.application.data.CommentData;
import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.comment.Comment;
import io.spring.core.comment.CommentRepository;
import io.spring.core.user.FollowRelation;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.infrastructure.DbTestBase;
import io.spring.infrastructure.repository.MyBatisArticleRepository;
import io.spring.infrastructure.repository.MyBatisCommentRepository;
import io.spring.infrastructure.repository.MyBatisUserRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import({
  MyBatisCommentRepository.class,
  MyBatisUserRepository.class,
  CommentQueryService.class,
  MyBatisArticleRepository.class
})
public class CommentQueryServiceTest extends DbTestBase {
  @Autowired private CommentRepository commentRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private CommentQueryService commentQueryService;

  @Autowired private ArticleRepository articleRepository;

  private User user;

  @BeforeEach
  public void setUp() {
    user = new User("aisensiy@test.com", "aisensiy", "123", "", "");
    userRepository.save(user);
  }

  @Test
  public void should_read_comment_success() {
    Comment comment = new Comment("content", user.getId(), "123");
    commentRepository.save(comment);

    Optional<CommentData> optional = commentQueryService.findById(comment.getId(), user);
    Assertions.assertTrue(optional.isPresent());
    CommentData commentData = optional.get();
    Assertions.assertEquals(commentData.getProfileData().getUsername(), user.getUsername());
  }

  @Test
  public void should_read_comments_of_article() {
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), user.getId());
    articleRepository.save(article);

    User user2 = new User("user2@email.com", "user2", "123", "", "");
    userRepository.save(user2);
    userRepository.saveRelation(new FollowRelation(user.getId(), user2.getId()));

    Comment comment1 = new Comment("content1", user.getId(), article.getId());
    commentRepository.save(comment1);
    Comment comment2 = new Comment("content2", user2.getId(), article.getId());
    commentRepository.save(comment2);

    List<CommentData> comments = commentQueryService.findByArticleId(article.getId(), user);
    Assertions.assertEquals(comments.size(), 2);
  }

  @Test
  public void should_return_empty_when_comment_not_found() {
    Optional<CommentData> optional = commentQueryService.findById("non-existent-id", user);
    Assertions.assertFalse(optional.isPresent());
  }

  @Test
  public void should_handle_null_user_in_find_by_id() {
    Comment comment = new Comment("content", user.getId(), "123");
    commentRepository.save(comment);

    try {
      Optional<CommentData> optional = commentQueryService.findById(comment.getId(), null);
      Assertions.fail("Expected NullPointerException when user is null");
    } catch (NullPointerException e) {
      Assertions.assertTrue(true);
    }
  }

  @Test
  public void should_return_empty_list_when_no_comments_for_article() {
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), user.getId());
    articleRepository.save(article);

    List<CommentData> comments = commentQueryService.findByArticleId(article.getId(), user);
    Assertions.assertEquals(comments.size(), 0);
  }

  @Test
  public void should_handle_null_user_in_find_by_article_id() {
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), user.getId());
    articleRepository.save(article);

    Comment comment = new Comment("content", user.getId(), article.getId());
    commentRepository.save(comment);

    List<CommentData> comments = commentQueryService.findByArticleId(article.getId(), null);
    Assertions.assertEquals(comments.size(), 1);
    Assertions.assertFalse(comments.get(0).getProfileData().isFollowing());
  }

  @Test
  public void should_handle_cursor_pagination_with_empty_results() {
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), user.getId());
    articleRepository.save(article);

    CursorPageParameter<org.joda.time.DateTime> page =
        new CursorPageParameter<>(null, 10, Direction.NEXT);
    CursorPager<CommentData> result =
        commentQueryService.findByArticleIdWithCursor(article.getId(), user, page);

    Assertions.assertTrue(result.getData().isEmpty());
    Assertions.assertFalse(result.hasNext());
  }

  @Test
  public void should_handle_cursor_pagination_with_null_user() {
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), user.getId());
    articleRepository.save(article);

    Comment comment = new Comment("content", user.getId(), article.getId());
    commentRepository.save(comment);

    CursorPageParameter<org.joda.time.DateTime> page =
        new CursorPageParameter<>(null, 10, Direction.NEXT);
    CursorPager<CommentData> result =
        commentQueryService.findByArticleIdWithCursor(article.getId(), null, page);

    Assertions.assertEquals(result.getData().size(), 1);
    Assertions.assertFalse(result.getData().get(0).getProfileData().isFollowing());
  }

  @Test
  public void should_handle_cursor_pagination_with_multiple_comments() {
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), user.getId());
    articleRepository.save(article);

    User user2 = new User("user2@email.com", "user2", "123", "", "");
    userRepository.save(user2);
    userRepository.saveRelation(new FollowRelation(user.getId(), user2.getId()));

    Comment comment1 = new Comment("content1", user.getId(), article.getId());
    commentRepository.save(comment1);
    Comment comment2 = new Comment("content2", user2.getId(), article.getId());
    commentRepository.save(comment2);
    Comment comment3 = new Comment("content3", user.getId(), article.getId());
    commentRepository.save(comment3);

    CursorPageParameter<org.joda.time.DateTime> page =
        new CursorPageParameter<>(null, 2, Direction.NEXT);
    CursorPager<CommentData> result =
        commentQueryService.findByArticleIdWithCursor(article.getId(), user, page);

    Assertions.assertEquals(result.getData().size(), 2);
    Assertions.assertTrue(result.hasNext());
    Assertions.assertNotNull(result.getEndCursor());
  }

  @Test
  public void should_handle_cursor_pagination_with_previous_direction() {
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), user.getId());
    articleRepository.save(article);

    Comment comment1 = new Comment("content1", user.getId(), article.getId());
    commentRepository.save(comment1);
    Comment comment2 = new Comment("content2", user.getId(), article.getId());
    commentRepository.save(comment2);

    CursorPageParameter<org.joda.time.DateTime> page =
        new CursorPageParameter<>(null, 10, Direction.PREV);
    CursorPager<CommentData> result =
        commentQueryService.findByArticleIdWithCursor(article.getId(), user, page);

    Assertions.assertEquals(result.getData().size(), 2);
    Assertions.assertFalse(result.hasPrevious());
  }

  @Test
  public void should_handle_cursor_pagination_with_specific_cursor() {
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), user.getId());
    articleRepository.save(article);

    Comment comment1 = new Comment("content1", user.getId(), article.getId());
    commentRepository.save(comment1);
    Comment comment2 = new Comment("content2", user.getId(), article.getId());
    commentRepository.save(comment2);
    Comment comment3 = new Comment("content3", user.getId(), article.getId());
    commentRepository.save(comment3);

    CursorPageParameter<org.joda.time.DateTime> firstPage =
        new CursorPageParameter<>(null, 2, Direction.NEXT);
    CursorPager<CommentData> firstResult =
        commentQueryService.findByArticleIdWithCursor(article.getId(), user, firstPage);

    Assertions.assertEquals(firstResult.getData().size(), 2);
    Assertions.assertTrue(firstResult.hasNext());

    org.joda.time.DateTime endCursor =
        (org.joda.time.DateTime) firstResult.getEndCursor().getData();
    CursorPageParameter<org.joda.time.DateTime> secondPage =
        new CursorPageParameter<>(endCursor, 2, Direction.NEXT);
    CursorPager<CommentData> secondResult =
        commentQueryService.findByArticleIdWithCursor(article.getId(), user, secondPage);

    Assertions.assertEquals(secondResult.getData().size(), 1);
    Assertions.assertFalse(secondResult.hasNext());
  }

  @Test
  public void should_handle_cursor_pagination_with_zero_limit() {
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), user.getId());
    articleRepository.save(article);

    Comment comment = new Comment("content", user.getId(), article.getId());
    commentRepository.save(comment);

    CursorPageParameter<org.joda.time.DateTime> page =
        new CursorPageParameter<>(null, 0, Direction.NEXT);
    CursorPager<CommentData> result =
        commentQueryService.findByArticleIdWithCursor(article.getId(), user, page);

    Assertions.assertEquals(result.getData().size(), 1);
    Assertions.assertFalse(result.hasNext());
  }

  @Test
  public void should_handle_cursor_pagination_with_large_limit() {
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), user.getId());
    articleRepository.save(article);

    Comment comment1 = new Comment("content1", user.getId(), article.getId());
    commentRepository.save(comment1);
    Comment comment2 = new Comment("content2", user.getId(), article.getId());
    commentRepository.save(comment2);

    CursorPageParameter<org.joda.time.DateTime> page =
        new CursorPageParameter<>(null, 1000, Direction.NEXT);
    CursorPager<CommentData> result =
        commentQueryService.findByArticleIdWithCursor(article.getId(), user, page);

    Assertions.assertEquals(result.getData().size(), 2);
    Assertions.assertFalse(result.hasNext());
  }
}
