package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import graphql.execution.DataFetcherResult;
import io.spring.api.exception.InvalidAuthenticationException;
import io.spring.application.user.RegisterParam;
import io.spring.application.user.UserService;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.graphql.types.CreateUserInput;
import io.spring.graphql.types.UpdateUserInput;
import io.spring.graphql.types.UserPayload;
import io.spring.graphql.types.UserResult;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserMutationSimpleTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder encryptService;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserMutation userMutation;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "testuser", "encodedPassword", "Test Bio", "test.jpg");
    }

    @Test
    void shouldCreateUserSuccessfully() {
        CreateUserInput input = CreateUserInput.newBuilder()
            .email("new@example.com")
            .username("newuser")
            .password("password123")
            .build();

        when(userService.createUser(any(RegisterParam.class))).thenReturn(testUser);

        DataFetcherResult<UserResult> result = userMutation.createUser(input);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(testUser, result.getLocalContext());
        verify(userService).createUser(any(RegisterParam.class));
    }

    @Test
    void shouldLoginWithValidCredentials() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(encryptService.matches("password123", "encodedPassword")).thenReturn(true);

        DataFetcherResult<UserPayload> result = userMutation.login("password123", "test@example.com");

        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(testUser, result.getLocalContext());
        verify(userRepository).findByEmail("test@example.com");
        verify(encryptService).matches("password123", "encodedPassword");
    }

    @Test
    void shouldThrowExceptionForInvalidEmail() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(InvalidAuthenticationException.class, () -> {
            userMutation.login("password123", "nonexistent@example.com");
        });

        verify(userRepository).findByEmail("nonexistent@example.com");
        verify(encryptService, never()).matches(anyString(), anyString());
    }

    @Test
    void shouldThrowExceptionForInvalidPassword() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(encryptService.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        assertThrows(InvalidAuthenticationException.class, () -> {
            userMutation.login("wrongpassword", "test@example.com");
        });

        verify(userRepository).findByEmail("test@example.com");
        verify(encryptService).matches("wrongpassword", "encodedPassword");
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = new UsernamePasswordAuthenticationToken(testUser, null);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        UpdateUserInput input = UpdateUserInput.newBuilder()
            .email("updated@example.com")
            .username("updateduser")
            .bio("Updated bio")
            .image("updated.jpg")
            .build();

        DataFetcherResult<UserPayload> result = userMutation.updateUser(input);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(testUser, result.getLocalContext());
        verify(userService).updateUser(any());
        
        SecurityContextHolder.clearContext();
    }
}
