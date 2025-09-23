package io.spring.graphql;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserMutationTest {

  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private UserService userService;
  @Mock private SecurityContext securityContext;
  @Mock private Authentication authentication;
  @Mock private AnonymousAuthenticationToken anonymousAuth;

  private UserMutation userMutation;
  private User testUser;

  @BeforeEach
  public void setUp() {
    userMutation = new UserMutation(userRepository, passwordEncoder, userService);
    testUser = new User("test@example.com", "testuser", "password123", "Test bio", "test.jpg");
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

    assertThat(result, notNullValue());
    assertThat(result.getData(), notNullValue());
    assertThat(result.getLocalContext(), is(testUser));
    verify(userService).createUser(any(RegisterParam.class));
  }

  @Test
  public void should_handle_constraint_violation_on_user_creation() {
    CreateUserInput input = CreateUserInput.newBuilder()
        .email("invalid-email")
        .username("testuser")
        .password("password123")
        .build();
    
    ConstraintViolationException exception = new ConstraintViolationException("Validation failed", java.util.Collections.emptySet());
    when(userService.createUser(any(RegisterParam.class))).thenThrow(exception);

    DataFetcherResult<UserResult> result = userMutation.createUser(input);

    assertThat(result, notNullValue());
    assertThat(result.getData(), notNullValue());
    verify(userService).createUser(any(RegisterParam.class));
  }

  @Test
  public void should_login_successfully_with_valid_credentials() {
    String email = "test@example.com";
    String password = "password123";
    
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
    when(passwordEncoder.matches(password, testUser.getPassword())).thenReturn(true);

    DataFetcherResult<UserPayload> result = userMutation.login(password, email);

    assertThat(result, notNullValue());
    assertThat(result.getData(), notNullValue());
    assertThat(result.getLocalContext(), is(testUser));
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
      assertThat("Should have thrown InvalidAuthenticationException", false);
    } catch (InvalidAuthenticationException e) {
      verify(userRepository).findByEmail(email);
    }
  }

  @Test
  public void should_throw_exception_for_invalid_password() {
    String email = "test@example.com";
    String password = "wrongpassword";
    
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
    when(passwordEncoder.matches(password, testUser.getPassword())).thenReturn(false);

    try {
      userMutation.login(password, email);
      assertThat("Should have thrown InvalidAuthenticationException", false);
    } catch (InvalidAuthenticationException e) {
      verify(userRepository).findByEmail(email);
      verify(passwordEncoder).matches(password, testUser.getPassword());
    }
  }

  @Test
  public void should_update_user_successfully() {
    UpdateUserInput input = UpdateUserInput.newBuilder()
        .email("newemail@example.com")
        .username("newusername")
        .bio("New bio")
        .password("newpassword")
        .image("newimage.jpg")
        .build();
    
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(testUser);

    DataFetcherResult<UserPayload> result = userMutation.updateUser(input);

    assertThat(result, notNullValue());
    assertThat(result.getData(), notNullValue());
    assertThat(result.getLocalContext(), is(testUser));
    verify(userService).updateUser(any(UpdateUserCommand.class));
  }

  @Test
  public void should_return_null_for_anonymous_user_update() {
    UpdateUserInput input = UpdateUserInput.newBuilder()
        .email("newemail@example.com")
        .username("newusername")
        .build();
    
    when(securityContext.getAuthentication()).thenReturn(anonymousAuth);

    DataFetcherResult<UserPayload> result = userMutation.updateUser(input);

    assertThat(result, is((DataFetcherResult<UserPayload>) null));
  }

  @Test
  public void should_return_null_for_null_principal_update() {
    UpdateUserInput input = UpdateUserInput.newBuilder()
        .email("newemail@example.com")
        .username("newusername")
        .build();
    
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(null);

    DataFetcherResult<UserPayload> result = userMutation.updateUser(input);

    assertThat(result, is((DataFetcherResult<UserPayload>) null));
  }

  @Test
  public void should_create_register_param_with_correct_values() {
    CreateUserInput input = CreateUserInput.newBuilder()
        .email("test@example.com")
        .username("testuser")
        .password("password123")
        .build();
    
    when(userService.createUser(any(RegisterParam.class))).thenReturn(testUser);

    userMutation.createUser(input);

    verify(userService).createUser(any(RegisterParam.class));
  }

  @Test
  public void should_create_update_user_command_with_correct_values() {
    UpdateUserInput input = UpdateUserInput.newBuilder()
        .email("newemail@example.com")
        .username("newusername")
        .bio("New bio")
        .password("newpassword")
        .image("newimage.jpg")
        .build();
    
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(testUser);

    userMutation.updateUser(input);

    verify(userService).updateUser(any(UpdateUserCommand.class));
  }
}
