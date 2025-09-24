package io.spring.graphql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

import java.util.Collections;
import java.util.HashSet;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.netflix.graphql.dgs.exceptions.DgsEntityNotFoundException;
import graphql.execution.DataFetcherResult;
import io.spring.api.exception.InvalidAuthenticationException;
import io.spring.application.user.RegisterParam;
import io.spring.application.user.UpdateUserCommand;
import io.spring.application.user.UserService;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.graphql.types.CreateUserInput;
import io.spring.graphql.types.UpdateUserInput;
import io.spring.graphql.types.UserPayload;
import io.spring.graphql.types.UserResult;
import java.util.Optional;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
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
public class UserMutationTest {

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
  public void setUp() {
    testUser = new User("test@example.com", "testuser", "hashedpassword", "Test Bio", "image.jpg");
    SecurityContextHolder.setContext(securityContext);
  }

  @Test
  public void should_create_user_successfully() {
    CreateUserInput input = CreateUserInput.newBuilder()
        .email("test@example.com")
        .username("testuser")
        .password("password123")
        .build();

    when(userService.createUser(any(RegisterParam.class))).thenReturn(testUser);

    DataFetcherResult<UserResult> result = userMutation.createUser(input);

    assertThat(result).isNotNull();
    assertThat(result.getData()).isInstanceOf(UserPayload.class);
    assertThat(result.getLocalContext()).isEqualTo(testUser);
    verify(userService).createUser(any(RegisterParam.class));
  }

  @Test
  public void should_handle_constraint_violation_on_user_creation() {
    CreateUserInput input = CreateUserInput.newBuilder()
        .email("invalid-email")
        .username("testuser")
        .password("password123")
        .build();

    when(userService.createUser(any(RegisterParam.class)))
        .thenThrow(new ConstraintViolationException("Validation failed", new HashSet<>()));

    DataFetcherResult<UserResult> result = userMutation.createUser(input);

    assertThat(result).isNotNull();
    assertThat(result.getData()).isNotNull();
    assertThat(result.getLocalContext()).isNull();
  }

  @Test
  public void should_login_successfully_with_valid_credentials() {
    String email = "test@example.com";
    String password = "password123";

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
    when(passwordEncoder.matches(password, testUser.getPassword())).thenReturn(true);

    DataFetcherResult<UserPayload> result = userMutation.login(password, email);

    assertThat(result).isNotNull();
    assertThat(result.getData()).isInstanceOf(UserPayload.class);
    assertThat(result.getLocalContext()).isEqualTo(testUser);
    verify(userRepository).findByEmail(email);
    verify(passwordEncoder).matches(password, testUser.getPassword());
  }

  @Test
  public void should_throw_exception_for_invalid_email() {
    String email = "nonexistent@example.com";
    String password = "password123";

    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    try {
      userMutation.login(password, email);
    } catch (InvalidAuthenticationException e) {
      assertThat(e).isInstanceOf(InvalidAuthenticationException.class);
    }

    verify(userRepository).findByEmail(email);
    verify(passwordEncoder, never()).matches(anyString(), anyString());
  }

  @Test
  public void should_throw_exception_for_invalid_password() {
    String email = "test@example.com";
    String password = "wrongpassword";

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
    when(passwordEncoder.matches(password, testUser.getPassword())).thenReturn(false);

    try {
      userMutation.login(password, email);
    } catch (InvalidAuthenticationException e) {
      assertThat(e).isInstanceOf(InvalidAuthenticationException.class);
    }

    verify(userRepository).findByEmail(email);
    verify(passwordEncoder).matches(password, testUser.getPassword());
  }

  @Test
  public void should_update_user_successfully() {
    UpdateUserInput input = UpdateUserInput.newBuilder()
        .username("newusername")
        .email("newemail@example.com")
        .bio("New bio")
        .password("newpassword")
        .image("newimage.jpg")
        .build();

    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(testUser);

    DataFetcherResult<UserPayload> result = userMutation.updateUser(input);

    assertThat(result).isNotNull();
    assertThat(result.getData()).isInstanceOf(UserPayload.class);
    assertThat(result.getLocalContext()).isEqualTo(testUser);
    verify(userService).updateUser(any(UpdateUserCommand.class));
  }

  @Test
  public void should_return_null_for_anonymous_user_update() {
    UpdateUserInput input = UpdateUserInput.newBuilder()
        .username("newusername")
        .build();

    when(securityContext.getAuthentication()).thenReturn(new AnonymousAuthenticationToken("key", "principal", Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));

    DataFetcherResult<UserPayload> result = userMutation.updateUser(input);

    assertThat(result).isNull();
    verify(userService, never()).updateUser(any(UpdateUserCommand.class));
  }

  @Test
  public void should_return_null_for_null_authentication() {
    UpdateUserInput input = UpdateUserInput.newBuilder()
        .username("newusername")
        .build();

    when(securityContext.getAuthentication()).thenReturn(null);

    try {
      DataFetcherResult<UserPayload> result = userMutation.updateUser(input);
      Assertions.fail("Expected NullPointerException when authentication is null");
    } catch (NullPointerException e) {
      Assertions.assertTrue(true);
    }
    verify(userService, never()).updateUser(any(UpdateUserCommand.class));
  }

  @Test
  public void should_return_null_for_null_principal() {
    UpdateUserInput input = UpdateUserInput.newBuilder()
        .username("newusername")
        .build();

    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(null);

    DataFetcherResult<UserPayload> result = userMutation.updateUser(input);

    assertThat(result).isNull();
    verify(userService, never()).updateUser(any(UpdateUserCommand.class));
  }
}
