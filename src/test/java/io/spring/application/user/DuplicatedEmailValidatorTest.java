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
public class DuplicatedEmailValidatorTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private ConstraintValidatorContext context;

  @InjectMocks
  private DuplicatedEmailValidator validator;

  @BeforeEach
  public void setUp() {
    try {
      java.lang.reflect.Field field = DuplicatedEmailValidator.class.getDeclaredField("userRepository");
      field.setAccessible(true);
      field.set(validator, userRepository);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void should_be_valid_when_email_does_not_exist() {
    when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());

    boolean result = validator.isValid("new@example.com", context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_be_invalid_when_email_already_exists() {
    User existingUser = new User("existing@example.com", "existinguser", "password", "bio", "image");
    when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(existingUser));

    boolean result = validator.isValid("existing@example.com", context);

    assertThat(result).isFalse();
  }

  @Test
  public void should_be_valid_when_email_is_null() {
    boolean result = validator.isValid(null, context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_be_valid_when_email_is_empty() {
    boolean result = validator.isValid("", context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_be_valid_when_email_is_whitespace() {
    boolean result = validator.isValid("   ", context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_handle_case_sensitive_emails() {
    when(userRepository.findByEmail("Test@Example.com")).thenReturn(Optional.empty());

    boolean result = validator.isValid("Test@Example.com", context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_handle_special_characters_in_email() {
    when(userRepository.findByEmail("user+tag@example.com")).thenReturn(Optional.empty());

    boolean result = validator.isValid("user+tag@example.com", context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_handle_long_email_addresses() {
    String longEmail = "very.long.email.address.with.many.dots@very-long-domain-name.example.com";
    when(userRepository.findByEmail(longEmail)).thenReturn(Optional.empty());

    boolean result = validator.isValid(longEmail, context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_handle_unicode_in_email() {
    String unicodeEmail = "测试@example.com";
    when(userRepository.findByEmail(unicodeEmail)).thenReturn(Optional.empty());

    boolean result = validator.isValid(unicodeEmail, context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_be_invalid_when_duplicate_email_found() {
    User existingUser = new User("duplicate@test.com", "user1", "password", "bio", "image");
    when(userRepository.findByEmail("duplicate@test.com")).thenReturn(Optional.of(existingUser));

    boolean result = validator.isValid("duplicate@test.com", context);

    assertThat(result).isFalse();
  }

  @Test
  public void should_handle_malformed_email_addresses() {
    when(userRepository.findByEmail("not-an-email")).thenReturn(Optional.empty());

    boolean result = validator.isValid("not-an-email", context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_handle_email_with_numbers() {
    when(userRepository.findByEmail("user123@example456.com")).thenReturn(Optional.empty());

    boolean result = validator.isValid("user123@example456.com", context);

    assertThat(result).isTrue();
  }
}
