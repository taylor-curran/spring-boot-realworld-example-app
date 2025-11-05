package io.spring.application.user;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserServiceTest {

  private UserService userService;
  private UserRepository userRepository;
  private PasswordEncoder passwordEncoder;
  private String defaultImage;

  @BeforeEach
  public void setUp() {
    userRepository = mock(UserRepository.class);
    passwordEncoder = mock(PasswordEncoder.class);
    defaultImage = "https://static.productionready.io/images/smiley-cyrus.jpg";
    userService = new UserService(userRepository, defaultImage, passwordEncoder);
  }

  @Test
  public void should_create_user_success() {
    String email = "test@example.com";
    String username = "testuser";
    String password = "password";
    String encodedPassword = "encoded-password";
    
    RegisterParam registerParam = new RegisterParam(email, username, password);
    
    when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
    
    User result = userService.createUser(registerParam);
    
    assertThat(result, notNullValue());
    assertThat(result.getEmail(), is(email));
    assertThat(result.getUsername(), is(username));
    assertThat(result.getPassword(), is(encodedPassword));
    assertThat(result.getBio(), is(""));
    assertThat(result.getImage(), is(defaultImage));
    
    verify(passwordEncoder).encode(password);
    verify(userRepository).save(any(User.class));
  }

  @Test
  public void should_create_user_with_encoded_password() {
    String rawPassword = "mypassword";
    String encodedPassword = "encoded-mypassword";
    
    RegisterParam registerParam = new RegisterParam("user@test.com", "user", rawPassword);
    
    when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
    
    User result = userService.createUser(registerParam);
    
    assertThat(result.getPassword(), is(encodedPassword));
    verify(passwordEncoder).encode(rawPassword);
  }

  @Test
  public void should_create_user_with_default_image() {
    RegisterParam registerParam = new RegisterParam("test@example.com", "testuser", "password");
    
    when(passwordEncoder.encode(anyString())).thenReturn("encoded");
    
    User result = userService.createUser(registerParam);
    
    assertThat(result.getImage(), is(defaultImage));
  }

  @Test
  public void should_create_user_with_empty_bio() {
    RegisterParam registerParam = new RegisterParam("test@example.com", "testuser", "password");
    
    when(passwordEncoder.encode(anyString())).thenReturn("encoded");
    
    User result = userService.createUser(registerParam);
    
    assertThat(result.getBio(), is(""));
  }

  @Test
  public void should_save_user_to_repository() {
    RegisterParam registerParam = new RegisterParam("test@example.com", "testuser", "password");
    
    when(passwordEncoder.encode(anyString())).thenReturn("encoded");
    
    userService.createUser(registerParam);
    
    verify(userRepository).save(any(User.class));
  }

  @Test
  public void should_update_user_success() {
    User targetUser = new User("old@example.com", "olduser", "oldpass", "old bio", "old image");
    
    UpdateUserParam updateParam = new UpdateUserParam("new@example.com", "newpass", "newuser", "new bio", "new image");
    
    UpdateUserCommand command = new UpdateUserCommand(targetUser, updateParam);
    
    userService.updateUser(command);
    
    verify(userRepository).save(targetUser);
  }

  @Test
  public void should_update_user_with_partial_data() {
    User targetUser = new User("old@example.com", "olduser", "oldpass", "old bio", "old image");
    
    UpdateUserParam updateParam = UpdateUserParam.builder()
        .email("new@example.com")
        .build();
    
    UpdateUserCommand command = new UpdateUserCommand(targetUser, updateParam);
    
    userService.updateUser(command);
    
    verify(userRepository).save(targetUser);
  }
}
