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
class RelationMutationSimpleTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileQueryService profileQueryService;

    @InjectMocks
    private RelationMutation relationMutation;

    private User currentUser;
    private User targetUser;
    private ProfileData profileData;

    @BeforeEach
    void setUp() {
        currentUser = new User("current@example.com", "currentuser", "password", "Current Bio", "current.jpg");
        targetUser = new User("target@example.com", "targetuser", "password", "Target Bio", "target.jpg");
        profileData = new ProfileData(targetUser.getId(), "targetuser", "Target Bio", "target.jpg", false);
    }

    @Test
    void shouldFollowUserSuccessfully() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(currentUser));
            when(userRepository.findByUsername("targetuser")).thenReturn(Optional.of(targetUser));
            when(profileQueryService.findByUsername("targetuser", currentUser)).thenReturn(Optional.of(profileData));

            ProfilePayload result = relationMutation.follow("targetuser");

            assertNotNull(result);
            assertNotNull(result.getProfile());
            assertEquals("targetuser", result.getProfile().getUsername());
            assertEquals("Target Bio", result.getProfile().getBio());
            assertEquals("target.jpg", result.getProfile().getImage());
            verify(userRepository).saveRelation(any(FollowRelation.class));
            verify(profileQueryService).findByUsername("targetuser", currentUser);
        }
    }

    @Test
    void shouldThrowAuthenticationExceptionWhenUserNotAuthenticated() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.empty());

            assertThrows(AuthenticationException.class, () -> {
                relationMutation.follow("targetuser");
            });

            verify(userRepository, never()).saveRelation(any());
        }
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenTargetUserNotFound() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(currentUser));
            when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> {
                relationMutation.follow("nonexistentuser");
            });

            verify(userRepository, never()).saveRelation(any());
        }
    }

    @Test
    void shouldUnfollowUserSuccessfully() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(currentUser));
            when(userRepository.findByUsername("targetuser")).thenReturn(Optional.of(targetUser));
            
            FollowRelation followRelation = new FollowRelation(currentUser.getId(), targetUser.getId());
            when(userRepository.findRelation(currentUser.getId(), targetUser.getId()))
                .thenReturn(Optional.of(followRelation));
            when(profileQueryService.findByUsername("targetuser", currentUser)).thenReturn(Optional.of(profileData));

            ProfilePayload result = relationMutation.unfollow("targetuser");

            assertNotNull(result);
            assertNotNull(result.getProfile());
            assertEquals("targetuser", result.getProfile().getUsername());
            verify(userRepository).removeRelation(followRelation);
            verify(profileQueryService).findByUsername("targetuser", currentUser);
        }
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenUnfollowTargetUserNotFound() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(currentUser));
            when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> {
                relationMutation.unfollow("nonexistentuser");
            });

            verify(userRepository, never()).removeRelation(any());
        }
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenRelationNotFound() {
        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUser).thenReturn(Optional.of(currentUser));
            when(userRepository.findByUsername("targetuser")).thenReturn(Optional.of(targetUser));
            when(userRepository.findRelation(currentUser.getId(), targetUser.getId()))
                .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> {
                relationMutation.unfollow("targetuser");
            });

            verify(userRepository, never()).removeRelation(any());
        }
    }
}
