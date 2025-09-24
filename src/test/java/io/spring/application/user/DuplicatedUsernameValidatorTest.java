package io.spring.application.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import java.util.Optional;
import javax.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DuplicatedUsernameValidatorTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private ConstraintValidatorContext context;

  @InjectMocks
  private DuplicatedUsernameValidator validator;

  @BeforeEach
  public void setUp() {
    try {
      java.lang.reflect.Field field = DuplicatedUsernameValidator.class.getDeclaredField("userRepository");
      field.setAccessible(true);
      field.set(validator, userRepository);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void should_be_valid_when_username_does_not_exist() {
    when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.empty());

    boolean result = validator.isValid("newuser", context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_be_invalid_when_username_already_exists() {
    User existingUser = new User("user@example.com", "existinguser", "password", "bio", "image");
    when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(existingUser));

    boolean result = validator.isValid("existinguser", context);

    assertThat(result).isFalse();
  }

  @Test
  public void should_be_valid_when_username_is_null() {
    boolean result = validator.isValid(null, context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_be_valid_when_username_is_empty() {
    boolean result = validator.isValid("", context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_be_valid_when_username_is_whitespace() {
    boolean result = validator.isValid("   ", context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_handle_case_sensitive_usernames() {
    when(userRepository.findByUsername("TestUser")).thenReturn(Optional.empty());

    boolean result = validator.isValid("TestUser", context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_handle_usernames_with_numbers() {
    when(userRepository.findByUsername("user123")).thenReturn(Optional.empty());

    boolean result = validator.isValid("user123", context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_handle_usernames_with_underscores() {
    when(userRepository.findByUsername("user_name")).thenReturn(Optional.empty());

    boolean result = validator.isValid("user_name", context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_handle_usernames_with_hyphens() {
    when(userRepository.findByUsername("user-name")).thenReturn(Optional.empty());

    boolean result = validator.isValid("user-name", context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_handle_long_usernames() {
    String longUsername = "very_long_username_with_many_characters_123456789";
    when(userRepository.findByUsername(longUsername)).thenReturn(Optional.empty());

    boolean result = validator.isValid(longUsername, context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_handle_short_usernames() {
    when(userRepository.findByUsername("a")).thenReturn(Optional.empty());

    boolean result = validator.isValid("a", context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_handle_unicode_usernames() {
    String unicodeUsername = "用户名";
    when(userRepository.findByUsername(unicodeUsername)).thenReturn(Optional.empty());

    boolean result = validator.isValid(unicodeUsername, context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_be_invalid_when_duplicate_username_found() {
    User existingUser = new User("test@example.com", "duplicateuser", "password", "bio", "image");
    when(userRepository.findByUsername("duplicateuser")).thenReturn(Optional.of(existingUser));

    boolean result = validator.isValid("duplicateuser", context);

    assertThat(result).isFalse();
  }

  @Test
  public void should_handle_usernames_with_special_characters() {
    when(userRepository.findByUsername("user@domain")).thenReturn(Optional.empty());

    boolean result = validator.isValid("user@domain", context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_handle_usernames_with_dots() {
    when(userRepository.findByUsername("user.name")).thenReturn(Optional.empty());

    boolean result = validator.isValid("user.name", context);

    assertThat(result).isTrue();
  }
}
