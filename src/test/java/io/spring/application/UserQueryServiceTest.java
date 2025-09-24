package io.spring.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.spring.application.data.UserData;
import io.spring.infrastructure.mybatis.readservice.UserReadService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserQueryServiceTest {

    @Mock
    private UserReadService userReadService;

    @InjectMocks
    private UserQueryService userQueryService;

    @Test
    void shouldFindUserByIdSuccessfully() {
        String userId = "user-123";
        UserData mockUserData = new UserData(userId, "test@example.com", "testuser", "Test Bio", "avatar.jpg");
        when(userReadService.findById(userId)).thenReturn(mockUserData);

        Optional<UserData> result = userQueryService.findById(userId);

        assertTrue(result.isPresent());
        UserData userData = result.get();
        assertEquals(userId, userData.getId());
        assertEquals("test@example.com", userData.getEmail());
        assertEquals("testuser", userData.getUsername());
        assertEquals("Test Bio", userData.getBio());
        assertEquals("avatar.jpg", userData.getImage());
        verify(userReadService).findById(userId);
    }

    @Test
    void shouldReturnEmptyWhenUserNotFound() {
        String userId = "nonexistent-user";
        when(userReadService.findById(userId)).thenReturn(null);

        Optional<UserData> result = userQueryService.findById(userId);

        assertFalse(result.isPresent());
        verify(userReadService).findById(userId);
    }

    @Test
    void shouldHandleNullUserId() {
        String nullUserId = null;
        when(userReadService.findById(nullUserId)).thenReturn(null);

        Optional<UserData> result = userQueryService.findById(nullUserId);

        assertFalse(result.isPresent());
        verify(userReadService).findById(nullUserId);
    }

    @Test
    void shouldHandleEmptyUserId() {
        String emptyUserId = "";
        when(userReadService.findById(emptyUserId)).thenReturn(null);

        Optional<UserData> result = userQueryService.findById(emptyUserId);

        assertFalse(result.isPresent());
        verify(userReadService).findById(emptyUserId);
    }

    @Test
    void shouldHandleUserWithNullFields() {
        String userId = "user-123";
        UserData mockUserData = new UserData(userId, null, null, null, null);
        when(userReadService.findById(userId)).thenReturn(mockUserData);

        Optional<UserData> result = userQueryService.findById(userId);

        assertTrue(result.isPresent());
        UserData userData = result.get();
        assertEquals(userId, userData.getId());
        assertNull(userData.getEmail());
        assertNull(userData.getUsername());
        assertNull(userData.getBio());
        assertNull(userData.getImage());
    }

    @Test
    void shouldHandleUserWithEmptyFields() {
        String userId = "user-123";
        UserData mockUserData = new UserData(userId, "", "", "", "");
        when(userReadService.findById(userId)).thenReturn(mockUserData);

        Optional<UserData> result = userQueryService.findById(userId);

        assertTrue(result.isPresent());
        UserData userData = result.get();
        assertEquals(userId, userData.getId());
        assertEquals("", userData.getEmail());
        assertEquals("", userData.getUsername());
        assertEquals("", userData.getBio());
        assertEquals("", userData.getImage());
    }

    @Test
    void shouldHandleSpecialCharactersInUserData() {
        String userId = "user-123";
        UserData mockUserData = new UserData(
            userId,
            "test+user@example.com", 
            "user_name-123", 
            "Bio with special chars: !@#$%^&*()", 
            "https://example.com/avatar.jpg?size=200"
        );
        when(userReadService.findById(userId)).thenReturn(mockUserData);

        Optional<UserData> result = userQueryService.findById(userId);

        assertTrue(result.isPresent());
        UserData userData = result.get();
        assertEquals("test+user@example.com", userData.getEmail());
        assertEquals("user_name-123", userData.getUsername());
        assertEquals("Bio with special chars: !@#$%^&*()", userData.getBio());
        assertEquals("https://example.com/avatar.jpg?size=200", userData.getImage());
    }

    @Test
    void shouldHandleUnicodeInUserData() {
        String userId = "user-123";
        UserData mockUserData = new UserData(
            userId,
            "用户@example.com", 
            "用户名", 
            "用户简介 with emoji 🌍", 
            "avatar.jpg"
        );
        when(userReadService.findById(userId)).thenReturn(mockUserData);

        Optional<UserData> result = userQueryService.findById(userId);

        assertTrue(result.isPresent());
        UserData userData = result.get();
        assertEquals("用户@example.com", userData.getEmail());
        assertEquals("用户名", userData.getUsername());
        assertEquals("用户简介 with emoji 🌍", userData.getBio());
    }

    @Test
    void shouldHandleLongUserData() {
        String userId = "user-123";
        String longBio = "This is a very long user biography that contains multiple sentences and might be used to test how the system handles longer text content in user profiles. ".repeat(10);
        UserData mockUserData = new UserData(userId, "test@example.com", "testuser", longBio, "avatar.jpg");
        when(userReadService.findById(userId)).thenReturn(mockUserData);

        Optional<UserData> result = userQueryService.findById(userId);

        assertTrue(result.isPresent());
        assertEquals(longBio, result.get().getBio());
    }

    @Test
    void shouldHandleMultipleCallsWithSameId() {
        String userId = "user-123";
        UserData mockUserData = new UserData(userId, "test@example.com", "testuser", "bio", "avatar.jpg");
        when(userReadService.findById(userId)).thenReturn(mockUserData);

        Optional<UserData> result1 = userQueryService.findById(userId);
        Optional<UserData> result2 = userQueryService.findById(userId);

        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        assertEquals(result1.get().getUsername(), result2.get().getUsername());
        verify(userReadService, times(2)).findById(userId);
    }
}
