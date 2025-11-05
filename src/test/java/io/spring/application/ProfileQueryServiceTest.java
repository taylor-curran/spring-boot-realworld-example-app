package io.spring.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.spring.application.data.ProfileData;
import io.spring.application.data.UserData;
import io.spring.core.user.User;
import io.spring.infrastructure.mybatis.readservice.UserReadService;
import io.spring.infrastructure.mybatis.readservice.UserRelationshipQueryService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProfileQueryServiceTest {

    @Mock
    private UserReadService userReadService;

    @Mock
    private UserRelationshipQueryService userRelationshipQueryService;

    @InjectMocks
    private ProfileQueryService profileQueryService;

    @Test
    void shouldFindProfileByUsernameSuccessfully() {
        String username = "testuser";
        User currentUser = new User("current@example.com", "currentuser", "password", "bio", "image.jpg");
        UserData userData = new UserData("user123", "test@example.com", username, "Test bio", "avatar.jpg");

        when(userReadService.findByUsername(username)).thenReturn(userData);
        when(userRelationshipQueryService.isUserFollowing(currentUser.getId(), userData.getId())).thenReturn(true);

        Optional<ProfileData> result = profileQueryService.findByUsername(username, currentUser);

        assertTrue(result.isPresent());
        ProfileData profile = result.get();
        assertEquals(userData.getId(), profile.getId());
        assertEquals(userData.getUsername(), profile.getUsername());
        assertEquals(userData.getBio(), profile.getBio());
        assertEquals(userData.getImage(), profile.getImage());
        assertTrue(profile.isFollowing());
        verify(userReadService).findByUsername(username);
        verify(userRelationshipQueryService).isUserFollowing(currentUser.getId(), userData.getId());
    }

    @Test
    void shouldFindProfileByUsernameWhenNotFollowing() {
        String username = "testuser";
        User currentUser = new User("current@example.com", "currentuser", "password", "bio", "image.jpg");
        UserData userData = new UserData("user123", "test@example.com", username, "Test bio", "avatar.jpg");

        when(userReadService.findByUsername(username)).thenReturn(userData);
        when(userRelationshipQueryService.isUserFollowing(currentUser.getId(), userData.getId())).thenReturn(false);

        Optional<ProfileData> result = profileQueryService.findByUsername(username, currentUser);

        assertTrue(result.isPresent());
        ProfileData profile = result.get();
        assertEquals(userData.getId(), profile.getId());
        assertEquals(userData.getUsername(), profile.getUsername());
        assertEquals(userData.getBio(), profile.getBio());
        assertEquals(userData.getImage(), profile.getImage());
        assertFalse(profile.isFollowing());
        verify(userReadService).findByUsername(username);
        verify(userRelationshipQueryService).isUserFollowing(currentUser.getId(), userData.getId());
    }

    @Test
    void shouldFindProfileByUsernameWhenCurrentUserIsNull() {
        String username = "testuser";
        UserData userData = new UserData("user123", "test@example.com", username, "Test bio", "avatar.jpg");

        when(userReadService.findByUsername(username)).thenReturn(userData);

        Optional<ProfileData> result = profileQueryService.findByUsername(username, null);

        assertTrue(result.isPresent());
        ProfileData profile = result.get();
        assertEquals(userData.getId(), profile.getId());
        assertEquals(userData.getUsername(), profile.getUsername());
        assertEquals(userData.getBio(), profile.getBio());
        assertEquals(userData.getImage(), profile.getImage());
        assertFalse(profile.isFollowing());
        verify(userReadService).findByUsername(username);
        verify(userRelationshipQueryService, never()).isUserFollowing(any(), any());
    }

    @Test
    void shouldReturnEmptyWhenUserNotFound() {
        String username = "nonexistentuser";
        User currentUser = new User("current@example.com", "currentuser", "password", "bio", "image.jpg");

        when(userReadService.findByUsername(username)).thenReturn(null);

        Optional<ProfileData> result = profileQueryService.findByUsername(username, currentUser);

        assertFalse(result.isPresent());
        verify(userReadService).findByUsername(username);
        verify(userRelationshipQueryService, never()).isUserFollowing(any(), any());
    }

    @Test
    void shouldHandleNullUsername() {
        String username = null;
        User currentUser = new User("current@example.com", "currentuser", "password", "bio", "image.jpg");

        when(userReadService.findByUsername(username)).thenReturn(null);

        Optional<ProfileData> result = profileQueryService.findByUsername(username, currentUser);

        assertFalse(result.isPresent());
        verify(userReadService).findByUsername(username);
    }

    @Test
    void shouldHandleEmptyUsername() {
        String username = "";
        User currentUser = new User("current@example.com", "currentuser", "password", "bio", "image.jpg");

        when(userReadService.findByUsername(username)).thenReturn(null);

        Optional<ProfileData> result = profileQueryService.findByUsername(username, currentUser);

        assertFalse(result.isPresent());
        verify(userReadService).findByUsername(username);
    }

    @Test
    void shouldHandleSpecialCharactersInUsername() {
        String username = "test_user-123";
        User currentUser = new User("current@example.com", "currentuser", "password", "bio", "image.jpg");
        UserData userData = new UserData("user123", "test@example.com", username, "Test bio", "avatar.jpg");

        when(userReadService.findByUsername(username)).thenReturn(userData);
        when(userRelationshipQueryService.isUserFollowing(currentUser.getId(), userData.getId())).thenReturn(false);

        Optional<ProfileData> result = profileQueryService.findByUsername(username, currentUser);

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
    }

    @Test
    void shouldHandleUnicodeInUsername() {
        String username = "用户名";
        User currentUser = new User("current@example.com", "currentuser", "password", "bio", "image.jpg");
        UserData userData = new UserData("user123", "test@example.com", username, "用户简介", "avatar.jpg");

        when(userReadService.findByUsername(username)).thenReturn(userData);
        when(userRelationshipQueryService.isUserFollowing(currentUser.getId(), userData.getId())).thenReturn(true);

        Optional<ProfileData> result = profileQueryService.findByUsername(username, currentUser);

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
        assertEquals("用户简介", result.get().getBio());
    }

    @Test
    void shouldHandleUserDataWithNullFields() {
        String username = "testuser";
        User currentUser = new User("current@example.com", "currentuser", "password", "bio", "image.jpg");
        UserData userData = new UserData("user123", null, username, null, null);

        when(userReadService.findByUsername(username)).thenReturn(userData);
        when(userRelationshipQueryService.isUserFollowing(currentUser.getId(), userData.getId())).thenReturn(false);

        Optional<ProfileData> result = profileQueryService.findByUsername(username, currentUser);

        assertTrue(result.isPresent());
        ProfileData profile = result.get();
        assertEquals(userData.getId(), profile.getId());
        assertEquals(username, profile.getUsername());
        assertNull(profile.getBio());
        assertNull(profile.getImage());
        assertFalse(profile.isFollowing());
    }

    @Test
    void shouldHandleUserDataWithEmptyFields() {
        String username = "testuser";
        User currentUser = new User("current@example.com", "currentuser", "password", "bio", "image.jpg");
        UserData userData = new UserData("user123", "", username, "", "");

        when(userReadService.findByUsername(username)).thenReturn(userData);
        when(userRelationshipQueryService.isUserFollowing(currentUser.getId(), userData.getId())).thenReturn(true);

        Optional<ProfileData> result = profileQueryService.findByUsername(username, currentUser);

        assertTrue(result.isPresent());
        ProfileData profile = result.get();
        assertEquals("", profile.getBio());
        assertEquals("", profile.getImage());
        assertTrue(profile.isFollowing());
    }

    @Test
    void shouldHandleMultipleCallsWithSameUsername() {
        String username = "testuser";
        User currentUser = new User("current@example.com", "currentuser", "password", "bio", "image.jpg");
        UserData userData = new UserData("user123", "test@example.com", username, "Test bio", "avatar.jpg");

        when(userReadService.findByUsername(username)).thenReturn(userData);
        when(userRelationshipQueryService.isUserFollowing(currentUser.getId(), userData.getId())).thenReturn(true);

        Optional<ProfileData> result1 = profileQueryService.findByUsername(username, currentUser);
        Optional<ProfileData> result2 = profileQueryService.findByUsername(username, currentUser);

        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        assertEquals(result1.get().getUsername(), result2.get().getUsername());
        verify(userReadService, times(2)).findByUsername(username);
        verify(userRelationshipQueryService, times(2)).isUserFollowing(currentUser.getId(), userData.getId());
    }
}
