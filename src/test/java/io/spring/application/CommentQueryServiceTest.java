package io.spring.application;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.spring.application.CursorPager.Direction;
import io.spring.application.data.CommentData;
import io.spring.application.data.ProfileData;
import io.spring.core.user.User;
import io.spring.infrastructure.mybatis.readservice.CommentReadService;
import io.spring.infrastructure.mybatis.readservice.UserRelationshipQueryService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CommentQueryServiceTest {

  @Mock private CommentReadService commentReadService;
  @Mock private UserRelationshipQueryService userRelationshipQueryService;

  private CommentQueryService commentQueryService;
  private User testUser;
  private CommentData testCommentData;
  private ProfileData testProfileData;

  @BeforeEach
  public void setUp() {
    commentQueryService = new CommentQueryService(commentReadService, userRelationshipQueryService);
    testUser = new User("test@example.com", "testuser", "password123", "Test bio", "test.jpg");
    testProfileData = new ProfileData("profile-id", "profileuser", "Profile bio", "profile.jpg", false);
    testCommentData = new CommentData("comment-id", "Comment body", "article-id", new DateTime(), new DateTime(), testProfileData);
  }

  @Test
  public void should_find_comment_by_id_successfully() {
    when(commentReadService.findById(eq("comment-id"))).thenReturn(testCommentData);
    when(userRelationshipQueryService.isUserFollowing(eq(testUser.getId()), eq(testProfileData.getId()))).thenReturn(true);

    Optional<CommentData> result = commentQueryService.findById("comment-id", testUser);

    assertThat(result.isPresent(), is(true));
    assertThat(result.get().getId(), is("comment-id"));
    assertThat(result.get().getProfileData().isFollowing(), is(true));
    verify(commentReadService).findById(eq("comment-id"));
    verify(userRelationshipQueryService).isUserFollowing(eq(testUser.getId()), eq(testProfileData.getId()));
  }

  @Test
  public void should_return_empty_when_comment_not_found() {
    when(commentReadService.findById(eq("nonexistent"))).thenReturn(null);

    Optional<CommentData> result = commentQueryService.findById("nonexistent", testUser);

    assertThat(result.isPresent(), is(false));
    verify(commentReadService).findById(eq("nonexistent"));
    verify(userRelationshipQueryService, never()).isUserFollowing(any(), any());
  }

  @Test
  public void should_find_comments_by_article_id_with_following_status() {
    List<CommentData> comments = Arrays.asList(testCommentData);
    Set<String> followingAuthors = new HashSet<>();
    followingAuthors.add(testProfileData.getId());

    when(commentReadService.findByArticleId(eq("article-id"))).thenReturn(comments);
    when(userRelationshipQueryService.followingAuthors(eq(testUser.getId()), anyList())).thenReturn(followingAuthors);

    List<CommentData> result = commentQueryService.findByArticleId("article-id", testUser);

    assertThat(result.size(), is(1));
    assertThat(result.get(0).getProfileData().isFollowing(), is(true));
    verify(commentReadService).findByArticleId(eq("article-id"));
    verify(userRelationshipQueryService).followingAuthors(eq(testUser.getId()), anyList());
  }

  @Test
  public void should_find_comments_by_article_id_without_user() {
    List<CommentData> comments = Arrays.asList(testCommentData);

    when(commentReadService.findByArticleId(eq("article-id"))).thenReturn(comments);

    List<CommentData> result = commentQueryService.findByArticleId("article-id", null);

    assertThat(result.size(), is(1));
    assertThat(result.get(0).getProfileData().isFollowing(), is(false));
    verify(commentReadService).findByArticleId(eq("article-id"));
    verify(userRelationshipQueryService, never()).followingAuthors(any(), anyList());
  }

  @Test
  public void should_find_comments_by_article_id_with_empty_list() {
    when(commentReadService.findByArticleId(eq("article-id"))).thenReturn(new ArrayList<>());

    List<CommentData> result = commentQueryService.findByArticleId("article-id", testUser);

    assertThat(result.size(), is(0));
    verify(commentReadService).findByArticleId(eq("article-id"));
    verify(userRelationshipQueryService, never()).followingAuthors(any(), anyList());
  }

  @Test
  public void should_find_comments_with_cursor_pagination_next_direction() {
    CursorPageParameter<DateTime> pageParam = new CursorPageParameter<>(new DateTime(), 2, Direction.NEXT);
    List<CommentData> comments = Arrays.asList(testCommentData, testCommentData);
    Set<String> followingAuthors = new HashSet<>();

    when(commentReadService.findByArticleIdWithCursor(eq("article-id"), eq(pageParam))).thenReturn(comments);
    when(userRelationshipQueryService.followingAuthors(eq(testUser.getId()), anyList())).thenReturn(followingAuthors);

    CursorPager<CommentData> result = commentQueryService.findByArticleIdWithCursor("article-id", testUser, pageParam);

    assertThat(result, notNullValue());
    assertThat(result.getData().size(), is(2));
    assertThat(result.hasNext(), is(false));
    verify(commentReadService).findByArticleIdWithCursor(eq("article-id"), eq(pageParam));
  }

  @Test
  public void should_find_comments_with_cursor_pagination_prev_direction() {
    CursorPageParameter<DateTime> pageParam = new CursorPageParameter<>(new DateTime(), 2, Direction.PREV);
    List<CommentData> comments = Arrays.asList(testCommentData, testCommentData);

    when(commentReadService.findByArticleIdWithCursor(eq("article-id"), eq(pageParam))).thenReturn(comments);
    when(userRelationshipQueryService.followingAuthors(eq(testUser.getId()), anyList())).thenReturn(new HashSet<>());

    CursorPager<CommentData> result = commentQueryService.findByArticleIdWithCursor("article-id", testUser, pageParam);

    assertThat(result, notNullValue());
    assertThat(result.getData().size(), is(2));
    assertThat(result.hasPrevious(), is(false));
    verify(commentReadService).findByArticleIdWithCursor(eq("article-id"), eq(pageParam));
  }

  @Test
  public void should_handle_empty_comments_with_cursor() {
    CursorPageParameter<DateTime> pageParam = new CursorPageParameter<>(new DateTime(), 2, Direction.NEXT);

    when(commentReadService.findByArticleIdWithCursor(eq("article-id"), eq(pageParam))).thenReturn(new ArrayList<>());

    CursorPager<CommentData> result = commentQueryService.findByArticleIdWithCursor("article-id", testUser, pageParam);

    assertThat(result, notNullValue());
    assertThat(result.getData().size(), is(0));
    assertThat(result.hasNext(), is(false));
    verify(commentReadService).findByArticleIdWithCursor(eq("article-id"), eq(pageParam));
    verify(userRelationshipQueryService, never()).followingAuthors(any(), anyList());
  }

  @Test
  public void should_handle_cursor_pagination_without_user() {
    CursorPageParameter<DateTime> pageParam = new CursorPageParameter<>(new DateTime(), 2, Direction.NEXT);
    List<CommentData> comments = Arrays.asList(testCommentData);

    when(commentReadService.findByArticleIdWithCursor(eq("article-id"), eq(pageParam))).thenReturn(comments);

    CursorPager<CommentData> result = commentQueryService.findByArticleIdWithCursor("article-id", null, pageParam);

    assertThat(result, notNullValue());
    assertThat(result.getData().size(), is(1));
    verify(commentReadService).findByArticleIdWithCursor(eq("article-id"), eq(pageParam));
    verify(userRelationshipQueryService, never()).followingAuthors(any(), anyList());
  }

  @Test
  public void should_remove_extra_comment_when_has_more() {
    CursorPageParameter<DateTime> pageParam = new CursorPageParameter<>(new DateTime(), 2, Direction.NEXT);
    List<CommentData> comments = new ArrayList<>();
    comments.add(testCommentData);
    comments.add(testCommentData);
    comments.add(testCommentData);

    when(commentReadService.findByArticleIdWithCursor(eq("article-id"), eq(pageParam))).thenReturn(comments);
    when(userRelationshipQueryService.followingAuthors(eq(testUser.getId()), anyList())).thenReturn(new HashSet<>());

    CursorPager<CommentData> result = commentQueryService.findByArticleIdWithCursor("article-id", testUser, pageParam);

    assertThat(result, notNullValue());
    assertThat(result.getData().size(), is(2));
    assertThat(result.hasNext(), is(true));
  }

  @Test
  public void should_set_following_status_for_followed_authors() {
    ProfileData followedProfile = new ProfileData("followed-id", "followeduser", "Followed bio", "followed.jpg", false);
    CommentData followedComment = new CommentData("comment-2", "Another comment", "article-id", new DateTime(), new DateTime(), followedProfile);
    List<CommentData> comments = Arrays.asList(testCommentData, followedComment);
    Set<String> followingAuthors = new HashSet<>();
    followingAuthors.add("followed-id");

    when(commentReadService.findByArticleId(eq("article-id"))).thenReturn(comments);
    when(userRelationshipQueryService.followingAuthors(eq(testUser.getId()), anyList())).thenReturn(followingAuthors);

    List<CommentData> result = commentQueryService.findByArticleId("article-id", testUser);

    assertThat(result.size(), is(2));
    assertThat(result.get(0).getProfileData().isFollowing(), is(false));
    assertThat(result.get(1).getProfileData().isFollowing(), is(true));
  }
}
