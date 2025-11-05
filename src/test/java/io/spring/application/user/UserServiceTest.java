package io.spring.application.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import javax.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private Validator validator;

  @InjectMocks private UserService userService;

  private RegisterParam validRegisterParam;
  private UpdateUserCommand validUpdateCommand;
  private User testUser;

  @BeforeEach
  public void setUp() {
    userService = new UserService(userRepository, "default-image.jpg", passwordEncoder);

    validRegisterParam = new RegisterParam("test@example.com", "testuser", "password123");
    testUser = new User("test@example.com", "testuser", "encoded-password", "bio", "image.jpg");

    UpdateUserParam updateParam =
        new UpdateUserParam(
            "new@example.com", "newpassword", "newuser", "new bio", "new-image.jpg");
    validUpdateCommand = new UpdateUserCommand(testUser, updateParam);
  }

  @Test
  public void should_create_user_successfully() {
    when(passwordEncoder.encode("password123")).thenReturn("encoded-password");

    User result = userService.createUser(validRegisterParam);

    assertThat(result).isNotNull();
    assertThat(result.getEmail()).isEqualTo("test@example.com");
    assertThat(result.getUsername()).isEqualTo("testuser");
    assertThat(result.getBio()).isEqualTo("");
    assertThat(result.getImage()).isEqualTo("default-image.jpg");

    verify(passwordEncoder).encode("password123");
    verify(userRepository).save(any(User.class));
  }

  @Test
  public void should_create_user_with_encoded_password() {
    when(passwordEncoder.encode("password123")).thenReturn("super-secure-encoded-password");

    User result = userService.createUser(validRegisterParam);

    verify(passwordEncoder).encode("password123");
    assertThat(result).isNotNull();
  }

  @Test
  public void should_create_user_with_default_image() {
    when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");

    User result = userService.createUser(validRegisterParam);

    assertThat(result.getImage()).isEqualTo("default-image.jpg");
  }

  @Test
  public void should_create_user_with_empty_bio() {
    when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");

    User result = userService.createUser(validRegisterParam);

    assertThat(result.getBio()).isEqualTo("");
  }

  @Test
  public void should_update_user_successfully() {
    UpdateUserParam updateParam =
        new UpdateUserParam(
            "updated@example.com",
            "newpassword",
            "updateduser",
            "updated bio",
            "updated-image.jpg");
    UpdateUserCommand updateCommand = new UpdateUserCommand(testUser, updateParam);

    userService.updateUser(updateCommand);

    verify(userRepository).save(testUser);
  }

  @Test
  public void should_update_user_with_all_fields() {
    UpdateUserParam updateParam =
        new UpdateUserParam(
            "new@example.com", "newpassword", "newusername", "new bio", "new-image.jpg");
    UpdateUserCommand updateCommand = new UpdateUserCommand(testUser, updateParam);

    userService.updateUser(updateCommand);

    verify(userRepository).save(testUser);
  }

  @Test
  public void should_update_user_with_partial_fields() {
    UpdateUserParam updateParam =
        new UpdateUserParam("new@example.com", null, null, "new bio", null);
    UpdateUserCommand updateCommand = new UpdateUserCommand(testUser, updateParam);

    userService.updateUser(updateCommand);

    verify(userRepository).save(testUser);
  }

  @Test
  public void should_handle_special_characters_in_registration() {
    RegisterParam specialParam =
        new RegisterParam("test+special@example.com", "user_name-123", "p@ssw0rd!");
    when(passwordEncoder.encode("p@ssw0rd!")).thenReturn("encoded-special-password");

    User result = userService.createUser(specialParam);

    assertThat(result).isNotNull();
    verify(passwordEncoder).encode("p@ssw0rd!");
  }

  @Test
  public void should_handle_unicode_characters_in_registration() {
    RegisterParam unicodeParam = new RegisterParam("测试@example.com", "用户名", "密码123");
    when(passwordEncoder.encode("密码123")).thenReturn("encoded-unicode-password");

    User result = userService.createUser(unicodeParam);

    assertThat(result).isNotNull();
    verify(passwordEncoder).encode("密码123");
  }

  @Test
  public void should_handle_long_input_values() {
    String longEmail = "very.long.email.address.that.might.exceed.normal.limits@example.com";
    String longUsername = "verylongusernamethatmightexceedtypicallimits";
    String longPassword =
        "verylongpasswordthatmightexceedtypicallimitsandcontainspecialcharacters123!@#";

    RegisterParam longParam = new RegisterParam(longEmail, longUsername, longPassword);
    when(passwordEncoder.encode(longPassword)).thenReturn("encoded-long-password");

    User result = userService.createUser(longParam);

    assertThat(result).isNotNull();
    verify(passwordEncoder).encode(longPassword);
  }

  @Test
  public void should_handle_minimum_length_values() {
    RegisterParam minParam = new RegisterParam("a@b.c", "u", "p");
    when(passwordEncoder.encode("p")).thenReturn("encoded-min-password");

    User result = userService.createUser(minParam);

    assertThat(result).isNotNull();
    verify(passwordEncoder).encode("p");
  }

  @Test
  public void should_handle_update_with_same_values() {
    UpdateUserParam sameParam =
        new UpdateUserParam(
            testUser.getEmail(),
            "password",
            testUser.getUsername(),
            testUser.getBio(),
            testUser.getImage());
    UpdateUserCommand sameCommand = new UpdateUserCommand(testUser, sameParam);

    userService.updateUser(sameCommand);

    verify(userRepository).save(testUser);
  }

  @Test
  public void should_handle_update_with_empty_optional_fields() {
    UpdateUserParam emptyParam = new UpdateUserParam("new@example.com", "", "newuser", "", "");
    UpdateUserCommand emptyCommand = new UpdateUserCommand(testUser, emptyParam);

    userService.updateUser(emptyCommand);

    verify(userRepository).save(testUser);
  }

  @Test
  public void should_handle_update_with_null_optional_fields() {
    UpdateUserParam nullParam = new UpdateUserParam("new@example.com", null, "newuser", null, null);
    UpdateUserCommand nullCommand = new UpdateUserCommand(testUser, nullParam);

    userService.updateUser(nullCommand);

    verify(userRepository).save(testUser);
  }
}
