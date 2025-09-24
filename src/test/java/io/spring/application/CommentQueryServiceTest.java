package io.spring.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.spring.application.CursorPager.Direction;
import io.spring.application.data.CommentData;
import io.spring.application.data.ProfileData;
import io.spring.core.user.User;
import io.spring.infrastructure.mybatis.readservice.CommentReadService;
import io.spring.infrastructure.mybatis.readservice.UserRelationshipQueryService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentQueryServiceTest {

    @Mock
    private CommentReadService commentReadService;

    @Mock
    private UserRelationshipQueryService userRelationshipQueryService;

    @InjectMocks
    private CommentQueryService commentQueryService;

    private User testUser;
    private CommentData testComment;
    private ProfileData testProfile;

    @BeforeEach
    void setUp() {
        testUser = new User("user@example.com", "testuser", "password", "bio", "image");
        testProfile = new ProfileData("profile-id", "author", "Author bio", "author.jpg", false);
        DateTime now = DateTime.now();
        testComment = new CommentData("comment-id", "Test comment body", "article-id", now, now, testProfile);
    }

    @Test
    void shouldFindCommentByIdSuccessfully() {
        when(commentReadService.findById("comment-id")).thenReturn(testComment);
        when(userRelationshipQueryService.isUserFollowing(testUser.getId(), testProfile.getId()))
            .thenReturn(true);

        Optional<CommentData> result = commentQueryService.findById("comment-id", testUser);

        assertTrue(result.isPresent());
        assertEquals(testComment, result.get());
        assertTrue(result.get().getProfileData().isFollowing());
        verify(commentReadService).findById("comment-id");
        verify(userRelationshipQueryService).isUserFollowing(testUser.getId(), testProfile.getId());
    }

    @Test
    void shouldReturnEmptyWhenCommentNotFound() {
        when(commentReadService.findById("nonexistent-id")).thenReturn(null);

        Optional<CommentData> result = commentQueryService.findById("nonexistent-id", testUser);

        assertFalse(result.isPresent());
        verify(commentReadService).findById("nonexistent-id");
        verifyNoInteractions(userRelationshipQueryService);
    }

    @Test
    void shouldFindCommentByIdWithoutFollowingRelationship() {
        when(commentReadService.findById("comment-id")).thenReturn(testComment);
        when(userRelationshipQueryService.isUserFollowing(testUser.getId(), testProfile.getId()))
            .thenReturn(false);

        Optional<CommentData> result = commentQueryService.findById("comment-id", testUser);

        assertTrue(result.isPresent());
        assertFalse(result.get().getProfileData().isFollowing());
    }

    @Test
    void shouldFindCommentsByArticleIdWithUser() {
        String articleId = "article-id";
        List<CommentData> comments = Arrays.asList(testComment);
        Set<String> followingAuthors = Set.of(testProfile.getId());

        when(commentReadService.findByArticleId(articleId)).thenReturn(comments);
        when(userRelationshipQueryService.followingAuthors(eq(testUser.getId()), anyList()))
            .thenReturn(followingAuthors);

        List<CommentData> result = commentQueryService.findByArticleId(articleId, testUser);

        assertEquals(1, result.size());
        assertTrue(result.get(0).getProfileData().isFollowing());
        verify(commentReadService).findByArticleId(articleId);
        verify(userRelationshipQueryService).followingAuthors(eq(testUser.getId()), anyList());
    }

    @Test
    void shouldFindCommentsByArticleIdWithoutUser() {
        String articleId = "article-id";
        List<CommentData> comments = Arrays.asList(testComment);

        when(commentReadService.findByArticleId(articleId)).thenReturn(comments);

        List<CommentData> result = commentQueryService.findByArticleId(articleId, null);

        assertEquals(1, result.size());
        assertFalse(result.get(0).getProfileData().isFollowing());
        verify(commentReadService).findByArticleId(articleId);
        verifyNoInteractions(userRelationshipQueryService);
    }

    @Test
    void shouldFindCommentsByArticleIdWithEmptyResults() {
        String articleId = "article-id";
        List<CommentData> emptyComments = new ArrayList<>();

        when(commentReadService.findByArticleId(articleId)).thenReturn(emptyComments);

        List<CommentData> result = commentQueryService.findByArticleId(articleId, testUser);

        assertTrue(result.isEmpty());
        verify(commentReadService).findByArticleId(articleId);
        verifyNoInteractions(userRelationshipQueryService);
    }

    @Test
    void shouldFindCommentsByArticleIdWithNonFollowingAuthors() {
        String articleId = "article-id";
        List<CommentData> comments = Arrays.asList(testComment);
        Set<String> followingAuthors = Collections.emptySet();

        when(commentReadService.findByArticleId(articleId)).thenReturn(comments);
        when(userRelationshipQueryService.followingAuthors(eq(testUser.getId()), anyList()))
            .thenReturn(followingAuthors);

        List<CommentData> result = commentQueryService.findByArticleId(articleId, testUser);

        assertEquals(1, result.size());
        assertFalse(result.get(0).getProfileData().isFollowing());
    }


    @Test
    void shouldHandleMultipleCommentsWithMixedFollowingStatus() {
        String articleId = "article-id";
        ProfileData followedProfile = new ProfileData("followed-id", "followed", "bio", "image", false);
        ProfileData notFollowedProfile = new ProfileData("not-followed-id", "notfollowed", "bio", "image", false);
        
        DateTime now = DateTime.now();
        CommentData followedComment = new CommentData("comment1", "body1", "article-id", now, now, followedProfile);
        CommentData notFollowedComment = new CommentData("comment2", "body2", "article-id", now, now, notFollowedProfile);
        
        List<CommentData> comments = Arrays.asList(followedComment, notFollowedComment);
        Set<String> followingAuthors = Set.of(followedProfile.getId());

        when(commentReadService.findByArticleId(articleId)).thenReturn(comments);
        when(userRelationshipQueryService.followingAuthors(eq(testUser.getId()), anyList()))
            .thenReturn(followingAuthors);

        List<CommentData> result = commentQueryService.findByArticleId(articleId, testUser);

        assertEquals(2, result.size());
        assertTrue(result.get(0).getProfileData().isFollowing());
        assertFalse(result.get(1).getProfileData().isFollowing());
    }

    @Test
    void shouldFindCommentsByArticleIdWithCursorNext() {
        String articleId = "article-id";
        CursorPageParameter<DateTime> pageParam = new CursorPageParameter<>(DateTime.now(), 2, Direction.NEXT);
        List<CommentData> comments = new ArrayList<>(Arrays.asList(testComment, testComment, testComment));
        Set<String> followingAuthors = Set.of(testProfile.getId());

        when(commentReadService.findByArticleIdWithCursor(articleId, pageParam)).thenReturn(comments);
        when(userRelationshipQueryService.followingAuthors(eq(testUser.getId()), anyList()))
            .thenReturn(followingAuthors);

        CursorPager<CommentData> result = commentQueryService.findByArticleIdWithCursor(articleId, testUser, pageParam);

        assertEquals(2, result.getData().size());
        assertTrue(result.hasNext());
        assertTrue(result.getData().get(0).getProfileData().isFollowing());
        verify(commentReadService).findByArticleIdWithCursor(articleId, pageParam);
        verify(userRelationshipQueryService).followingAuthors(eq(testUser.getId()), anyList());
    }

    @Test
    void shouldFindCommentsByArticleIdWithCursorPrev() {
        String articleId = "article-id";
        CursorPageParameter<DateTime> pageParam = new CursorPageParameter<>(DateTime.now(), 2, Direction.PREV);
        List<CommentData> comments = new ArrayList<>(Arrays.asList(testComment, testComment));

        when(commentReadService.findByArticleIdWithCursor(articleId, pageParam)).thenReturn(comments);

        CursorPager<CommentData> result = commentQueryService.findByArticleIdWithCursor(articleId, null, pageParam);

        assertEquals(2, result.getData().size());
        assertFalse(result.hasNext());
        verify(commentReadService).findByArticleIdWithCursor(articleId, pageParam);
        verifyNoInteractions(userRelationshipQueryService);
    }

    @Test
    void shouldFindCommentsByArticleIdWithCursorEmptyResults() {
        String articleId = "article-id";
        CursorPageParameter<DateTime> pageParam = new CursorPageParameter<>(DateTime.now(), 10, Direction.NEXT);
        List<CommentData> emptyComments = new ArrayList<>();

        when(commentReadService.findByArticleIdWithCursor(articleId, pageParam)).thenReturn(emptyComments);

        CursorPager<CommentData> result = commentQueryService.findByArticleIdWithCursor(articleId, testUser, pageParam);

        assertTrue(result.getData().isEmpty());
        assertFalse(result.hasNext());
        verify(commentReadService).findByArticleIdWithCursor(articleId, pageParam);
        verifyNoInteractions(userRelationshipQueryService);
    }

    @Test
    void shouldFindCommentsByArticleIdWithCursorWithoutUser() {
        String articleId = "article-id";
        CursorPageParameter<DateTime> pageParam = new CursorPageParameter<>(DateTime.now(), 5, Direction.NEXT);
        List<CommentData> comments = new ArrayList<>(Arrays.asList(testComment));

        when(commentReadService.findByArticleIdWithCursor(articleId, pageParam)).thenReturn(comments);

        CursorPager<CommentData> result = commentQueryService.findByArticleIdWithCursor(articleId, null, pageParam);

        assertEquals(1, result.getData().size());
        assertFalse(result.hasNext());
        assertFalse(result.getData().get(0).getProfileData().isFollowing());
        verify(commentReadService).findByArticleIdWithCursor(articleId, pageParam);
        verifyNoInteractions(userRelationshipQueryService);
    }

    @Test
    void shouldReverseCommentsForPrevDirection() {
        String articleId = "article-id";
        CursorPageParameter<DateTime> pageParam = new CursorPageParameter<>(DateTime.now(), 3, Direction.PREV);
        
        DateTime now = DateTime.now();
        CommentData comment1 = new CommentData("comment1", "body1", "article-id", now, now, testProfile);
        CommentData comment2 = new CommentData("comment2", "body2", "article-id", now, now, testProfile);
        List<CommentData> comments = new ArrayList<>(Arrays.asList(comment1, comment2));

        when(commentReadService.findByArticleIdWithCursor(articleId, pageParam)).thenReturn(comments);

        CursorPager<CommentData> result = commentQueryService.findByArticleIdWithCursor(articleId, null, pageParam);

        assertEquals(2, result.getData().size());
        verify(commentReadService).findByArticleIdWithCursor(articleId, pageParam);
    }

    @Test
    void shouldHandleCursorPaginationWithLimitExceeded() {
        String articleId = "article-id";
        CursorPageParameter<DateTime> pageParam = new CursorPageParameter<>(DateTime.now(), 2, Direction.NEXT);
        
        DateTime now = DateTime.now();
        CommentData comment1 = new CommentData("comment1", "body1", "article-id", now, now, testProfile);
        CommentData comment2 = new CommentData("comment2", "body2", "article-id", now, now, testProfile);
        CommentData comment3 = new CommentData("comment3", "body3", "article-id", now, now, testProfile);
        List<CommentData> comments = new ArrayList<>(Arrays.asList(comment1, comment2, comment3));

        when(commentReadService.findByArticleIdWithCursor(articleId, pageParam)).thenReturn(comments);

        CursorPager<CommentData> result = commentQueryService.findByArticleIdWithCursor(articleId, null, pageParam);

        assertEquals(2, result.getData().size());
        assertTrue(result.hasNext());
        verify(commentReadService).findByArticleIdWithCursor(articleId, pageParam);
    }

}
