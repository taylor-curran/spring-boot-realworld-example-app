package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.ProfileQueryService;
import io.spring.application.data.ProfileData;
import io.spring.core.user.FollowRelation;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.graphql.exception.AuthenticationException;
import io.spring.graphql.types.ProfilePayload;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RelationMutationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileQueryService profileQueryService;

    @InjectMocks
    private RelationMutation relationMutation;

    private User testUser;
    private User targetUser;
    private ProfileData profileData;
    private FollowRelation followRelation;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "testuser", "hashedpassword", "Test Bio", "avatar.jpg");
        targetUser = new User("target@example.com", "targetuser", "password", "Target Bio", "target.jpg");
        profileData = new ProfileData(targetUser.getId(), "targetuser", "Target Bio", "target.jpg", true);
        followRelation = new FollowRelation(testUser.getId(), targetUser.getId());
    }

    @Test
    void shouldFollowUserSuccessfully() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(userRepository.findByUsername("targetuser")).thenReturn(Optional.of(targetUser));
            when(profileQueryService.findByUsername("targetuser", testUser)).thenReturn(Optional.of(profileData));

            ProfilePayload result = relationMutation.follow("targetuser");

            assertNotNull(result);
            assertNotNull(result.getProfile());
            assertEquals("targetuser", result.getProfile().getUsername());
            assertEquals("Target Bio", result.getProfile().getBio());
            assertEquals("target.jpg", result.getProfile().getImage());
            assertTrue(result.getProfile().getFollowing());
            
            verify(userRepository).findByUsername("targetuser");
            verify(userRepository).saveRelation(any(FollowRelation.class));
            verify(profileQueryService).findByUsername("targetuser", testUser);
        }
    }

    @Test
    void shouldThrowAuthenticationExceptionWhenUserNotAuthenticated() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.empty());

            assertThrows(AuthenticationException.class, () -> {
                relationMutation.follow("targetuser");
            });
        }
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenTargetUserNotFound() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> {
                relationMutation.follow("nonexistent");
            });
        }
    }

    @Test
    void shouldUnfollowUserSuccessfully() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(userRepository.findByUsername("targetuser")).thenReturn(Optional.of(targetUser));
            when(userRepository.findRelation(testUser.getId(), targetUser.getId()))
                .thenReturn(Optional.of(followRelation));
            when(profileQueryService.findByUsername("targetuser", testUser)).thenReturn(Optional.of(profileData));

            ProfilePayload result = relationMutation.unfollow("targetuser");

            assertNotNull(result);
            assertNotNull(result.getProfile());
            assertEquals("targetuser", result.getProfile().getUsername());
            
            verify(userRepository).findByUsername("targetuser");
            verify(userRepository).findRelation(testUser.getId(), targetUser.getId());
            verify(userRepository).removeRelation(followRelation);
            verify(profileQueryService).findByUsername("targetuser", testUser);
        }
    }

    @Test
    void shouldThrowAuthenticationExceptionWhenUserNotAuthenticatedForUnfollow() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.empty());

            assertThrows(AuthenticationException.class, () -> {
                relationMutation.unfollow("targetuser");
            });
        }
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenTargetUserNotFoundForUnfollow() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> {
                relationMutation.unfollow("nonexistent");
            });
        }
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenRelationNotFound() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(userRepository.findByUsername("targetuser")).thenReturn(Optional.of(targetUser));
            when(userRepository.findRelation(testUser.getId(), targetUser.getId()))
                .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> {
                relationMutation.unfollow("targetuser");
            });
        }
    }

    @Test
    void shouldHandleFollowWithDifferentUsernames() {
        User specialUser = new User("special@example.com", "special-user_123", "password", "Special Bio", "special.jpg");
        ProfileData specialProfileData = new ProfileData(specialUser.getId(), "special-user_123", "Special Bio", "special.jpg", false);
        
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(userRepository.findByUsername("special-user_123")).thenReturn(Optional.of(specialUser));
            when(profileQueryService.findByUsername("special-user_123", testUser)).thenReturn(Optional.of(specialProfileData));

            ProfilePayload result = relationMutation.follow("special-user_123");

            assertNotNull(result);
            assertEquals("special-user_123", result.getProfile().getUsername());
            assertFalse(result.getProfile().getFollowing());
            verify(userRepository).saveRelation(argThat(relation -> 
                relation.getUserId().equals(testUser.getId()) && 
                relation.getTargetId().equals(specialUser.getId())
            ));
        }
    }

    @Test
    void shouldHandleUnfollowWithEmptyBioAndImage() {
        ProfileData emptyProfileData = new ProfileData(targetUser.getId(), "targetuser", "", "", false);
        
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(testUser));
            when(userRepository.findByUsername("targetuser")).thenReturn(Optional.of(targetUser));
            when(userRepository.findRelation(testUser.getId(), targetUser.getId()))
                .thenReturn(Optional.of(followRelation));
            when(profileQueryService.findByUsername("targetuser", testUser)).thenReturn(Optional.of(emptyProfileData));

            ProfilePayload result = relationMutation.unfollow("targetuser");

            assertNotNull(result);
            assertEquals("targetuser", result.getProfile().getUsername());
            assertEquals("", result.getProfile().getBio());
            assertEquals("", result.getProfile().getImage());
            assertFalse(result.getProfile().getFollowing());
        }
    }
}
