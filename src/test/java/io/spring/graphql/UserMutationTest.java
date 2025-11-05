package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.netflix.graphql.dgs.exceptions.DgsEntityNotFoundException;
import graphql.execution.DataFetcherResult;
import io.spring.api.exception.InvalidAuthenticationException;
import io.spring.application.user.RegisterParam;
import io.spring.application.user.UpdateUserCommand;
import io.spring.application.user.UpdateUserParam;
import io.spring.application.user.UserService;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.graphql.types.CreateUserInput;
import io.spring.graphql.types.UpdateUserInput;
import io.spring.graphql.types.UserPayload;
import io.spring.graphql.types.UserResult;
import java.util.Optional;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserMutationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserService userService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserMutation userMutation;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "testuser", "hashedpassword", "Test Bio", "avatar.jpg");
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void shouldCreateUserSuccessfully() {
        CreateUserInput input = CreateUserInput.newBuilder()
            .email("test@example.com")
            .username("testuser")
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
    void shouldHandleConstraintViolationInCreateUser() {
        CreateUserInput input = CreateUserInput.newBuilder()
            .email("invalid-email")
            .username("testuser")
            .password("password123")
            .build();

        ConstraintViolationException exception = mock(ConstraintViolationException.class);
        when(userService.createUser(any(RegisterParam.class))).thenThrow(exception);

        DataFetcherResult<UserResult> result = userMutation.createUser(input);

        assertNotNull(result);
        assertNotNull(result.getData());
        verify(userService).createUser(any(RegisterParam.class));
    }

    @Test
    void shouldLoginWithValidCredentials() {
        String email = "test@example.com";
        String password = "password123";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(password, testUser.getPassword())).thenReturn(true);

        DataFetcherResult<UserPayload> result = userMutation.login(password, email);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(testUser, result.getLocalContext());
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, testUser.getPassword());
    }

    @Test
    void shouldRejectInvalidCredentials() {
        String email = "test@example.com";
        String password = "wrongpassword";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(password, testUser.getPassword())).thenReturn(false);

        assertThrows(InvalidAuthenticationException.class, () -> {
            userMutation.login(password, email);
        });

        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, testUser.getPassword());
    }

    @Test
    void shouldRejectLoginForNonExistentUser() {
        String email = "nonexistent@example.com";
        String password = "password123";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(InvalidAuthenticationException.class, () -> {
            userMutation.login(password, email);
        });

        verify(userRepository).findByEmail(email);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        UpdateUserInput input = UpdateUserInput.newBuilder()
            .username("newusername")
            .email("newemail@example.com")
            .bio("New bio")
            .password("newpassword")
            .image("newavatar.jpg")
            .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);

        DataFetcherResult<UserPayload> result = userMutation.updateUser(input);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(testUser, result.getLocalContext());
        verify(userService).updateUser(any(UpdateUserCommand.class));
    }

    @Test
    void shouldReturnNullForAnonymousUser() {
        UpdateUserInput input = UpdateUserInput.newBuilder()
            .username("newusername")
            .build();

        AnonymousAuthenticationToken anonymousAuth = mock(AnonymousAuthenticationToken.class);
        when(securityContext.getAuthentication()).thenReturn(anonymousAuth);

        DataFetcherResult<UserPayload> result = userMutation.updateUser(input);

        assertNull(result);
        verify(userService, never()).updateUser(any(UpdateUserCommand.class));
    }

    @Test
    void shouldReturnNullForNullPrincipal() {
        UpdateUserInput input = UpdateUserInput.newBuilder()
            .username("newusername")
            .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(null);

        DataFetcherResult<UserPayload> result = userMutation.updateUser(input);

        assertNull(result);
        verify(userService, never()).updateUser(any(UpdateUserCommand.class));
    }
}
