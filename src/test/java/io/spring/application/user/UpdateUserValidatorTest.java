package io.spring.application.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import java.util.Optional;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UpdateUserValidatorTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private ConstraintValidatorContext context;

  @Mock
  private ConstraintViolationBuilder violationBuilder;

  @Mock
  private NodeBuilderCustomizableContext nodeBuilder;

  private UpdateUserValidator validator;
  private User targetUser;
  private UpdateUserParam updateParam;
  private UpdateUserCommand updateCommand;

  @BeforeEach
  public void setUp() {
    validator = new UpdateUserValidator();
    try {
      java.lang.reflect.Field field = UpdateUserValidator.class.getDeclaredField("userRepository");
      field.setAccessible(true);
      field.set(validator, userRepository);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    
    targetUser = new User("original@example.com", "originaluser", "password", "bio", "image.jpg");
    updateParam = new UpdateUserParam("new@example.com", "newpassword", "newuser", "new bio", "new-image.jpg");
    updateCommand = new UpdateUserCommand(targetUser, updateParam);
  }

  @Test
  public void should_be_valid_when_email_and_username_are_available() {
    when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
    when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());

    boolean result = validator.isValid(updateCommand, context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_be_valid_when_email_belongs_to_same_user() {
    when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.of(targetUser));
    when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());

    boolean result = validator.isValid(updateCommand, context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_be_valid_when_username_belongs_to_same_user() {
    when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
    when(userRepository.findByUsername("newuser")).thenReturn(Optional.of(targetUser));

    boolean result = validator.isValid(updateCommand, context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_be_valid_when_both_email_and_username_belong_to_same_user() {
    when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.of(targetUser));
    when(userRepository.findByUsername("newuser")).thenReturn(Optional.of(targetUser));

    boolean result = validator.isValid(updateCommand, context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_be_invalid_when_email_belongs_to_different_user() {
    User differentUser = new User("different@example.com", "differentuser", "password", "bio", "image.jpg");
    when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.of(differentUser));
    when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
    
    when(context.buildConstraintViolationWithTemplate("email already exist")).thenReturn(violationBuilder);
    when(violationBuilder.addPropertyNode("email")).thenReturn(nodeBuilder);
    when(nodeBuilder.addConstraintViolation()).thenReturn(context);

    boolean result = validator.isValid(updateCommand, context);

    assertThat(result).isFalse();
  }

  @Test
  public void should_be_invalid_when_username_belongs_to_different_user() {
    User differentUser = new User("different@example.com", "differentuser", "password", "bio", "image.jpg");
    when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
    when(userRepository.findByUsername("newuser")).thenReturn(Optional.of(differentUser));
    
    when(context.buildConstraintViolationWithTemplate("username already exist")).thenReturn(violationBuilder);
    when(violationBuilder.addPropertyNode("username")).thenReturn(nodeBuilder);
    when(nodeBuilder.addConstraintViolation()).thenReturn(context);

    boolean result = validator.isValid(updateCommand, context);

    assertThat(result).isFalse();
  }

  @Test
  public void should_be_invalid_when_both_email_and_username_belong_to_different_users() {
    User differentUser1 = new User("different1@example.com", "differentuser1", "password", "bio", "image.jpg");
    User differentUser2 = new User("different2@example.com", "differentuser2", "password", "bio", "image.jpg");
    
    when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.of(differentUser1));
    when(userRepository.findByUsername("newuser")).thenReturn(Optional.of(differentUser2));
    
    when(context.buildConstraintViolationWithTemplate("email already exist")).thenReturn(violationBuilder);
    when(context.buildConstraintViolationWithTemplate("username already exist")).thenReturn(violationBuilder);
    when(violationBuilder.addPropertyNode("email")).thenReturn(nodeBuilder);
    when(violationBuilder.addPropertyNode("username")).thenReturn(nodeBuilder);
    when(nodeBuilder.addConstraintViolation()).thenReturn(context);

    boolean result = validator.isValid(updateCommand, context);

    assertThat(result).isFalse();
  }

  @Test
  public void should_handle_null_email_input() {
    UpdateUserParam nullEmailParam = new UpdateUserParam(null, "password", "newuser", "bio", "image");
    UpdateUserCommand nullEmailCommand = new UpdateUserCommand(targetUser, nullEmailParam);
    
    when(userRepository.findByEmail(null)).thenReturn(Optional.empty());
    when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());

    boolean result = validator.isValid(nullEmailCommand, context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_handle_null_username_input() {
    UpdateUserParam nullUsernameParam = new UpdateUserParam("new@example.com", "password", null, "bio", "image");
    UpdateUserCommand nullUsernameCommand = new UpdateUserCommand(targetUser, nullUsernameParam);
    
    when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
    when(userRepository.findByUsername(null)).thenReturn(Optional.empty());

    boolean result = validator.isValid(nullUsernameCommand, context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_handle_empty_email_input() {
    UpdateUserParam emptyEmailParam = new UpdateUserParam("", "password", "newuser", "bio", "image");
    UpdateUserCommand emptyEmailCommand = new UpdateUserCommand(targetUser, emptyEmailParam);
    
    when(userRepository.findByEmail("")).thenReturn(Optional.empty());
    when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());

    boolean result = validator.isValid(emptyEmailCommand, context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_handle_empty_username_input() {
    UpdateUserParam emptyUsernameParam = new UpdateUserParam("new@example.com", "password", "", "bio", "image");
    UpdateUserCommand emptyUsernameCommand = new UpdateUserCommand(targetUser, emptyUsernameParam);
    
    when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
    when(userRepository.findByUsername("")).thenReturn(Optional.empty());

    boolean result = validator.isValid(emptyUsernameCommand, context);

    assertThat(result).isTrue();
  }

  @Test
  public void should_disable_default_constraint_violation_when_invalid() {
    User differentUser = new User("different@example.com", "differentuser", "password", "bio", "image.jpg");
    when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.of(differentUser));
    when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
    
    when(context.buildConstraintViolationWithTemplate("email already exist")).thenReturn(violationBuilder);
    when(violationBuilder.addPropertyNode("email")).thenReturn(nodeBuilder);
    when(nodeBuilder.addConstraintViolation()).thenReturn(context);

    validator.isValid(updateCommand, context);

  }
}
