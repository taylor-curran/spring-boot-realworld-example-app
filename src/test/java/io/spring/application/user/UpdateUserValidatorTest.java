package io.spring.application.user;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class UpdateUserValidatorTest {

  @Mock private UserRepository userRepository;
  @Mock private ConstraintValidatorContext context;
  @Mock private ConstraintViolationBuilder violationBuilder;
  @Mock private NodeBuilderCustomizableContext nodeBuilder;

  private UpdateUserValidator validator;
  private User targetUser;
  private User otherUser;
  private UpdateUserCommand command;
  private UpdateUserParam param;

  @BeforeEach
  public void setUp() {
    validator = new UpdateUserValidator();
    ReflectionTestUtils.setField(validator, "userRepository", userRepository);

    targetUser = new User("target@example.com", "targetuser", "password", "bio", "image");
    otherUser = new User("other@example.com", "otheruser", "password", "bio", "image");
    
    param = new UpdateUserParam("newemail@example.com", "newpassword", "newusername", "newbio", "newimage");
    command = new UpdateUserCommand(targetUser, param);
  }

  private void setupEmailConstraintViolationMocks() {
    when(context.buildConstraintViolationWithTemplate(eq("email already exist")))
        .thenReturn(violationBuilder);
    when(violationBuilder.addPropertyNode(eq("email"))).thenReturn(nodeBuilder);
    when(nodeBuilder.addConstraintViolation()).thenReturn(context);
  }

  private void setupUsernameConstraintViolationMocks() {
    when(context.buildConstraintViolationWithTemplate(eq("username already exist")))
        .thenReturn(violationBuilder);
    when(violationBuilder.addPropertyNode(eq("username"))).thenReturn(nodeBuilder);
    when(nodeBuilder.addConstraintViolation()).thenReturn(context);
  }

  private void setupBothConstraintViolationMocks() {
    when(context.buildConstraintViolationWithTemplate(eq("email already exist")))
        .thenReturn(violationBuilder);
    when(context.buildConstraintViolationWithTemplate(eq("username already exist")))
        .thenReturn(violationBuilder);
    when(violationBuilder.addPropertyNode(eq("email"))).thenReturn(nodeBuilder);
    when(violationBuilder.addPropertyNode(eq("username"))).thenReturn(nodeBuilder);
    when(nodeBuilder.addConstraintViolation()).thenReturn(context);
  }

  @Test
  public void should_return_true_when_email_and_username_are_available() {
    when(userRepository.findByEmail(eq("newemail@example.com"))).thenReturn(Optional.empty());
    when(userRepository.findByUsername(eq("newusername"))).thenReturn(Optional.empty());

    boolean result = validator.isValid(command, context);

    assertThat(result, is(true));
  }

  @Test
  public void should_return_true_when_email_belongs_to_target_user() {
    when(userRepository.findByEmail(eq("newemail@example.com"))).thenReturn(Optional.of(targetUser));
    when(userRepository.findByUsername(eq("newusername"))).thenReturn(Optional.empty());

    boolean result = validator.isValid(command, context);

    assertThat(result, is(true));
  }

  @Test
  public void should_return_true_when_username_belongs_to_target_user() {
    when(userRepository.findByEmail(eq("newemail@example.com"))).thenReturn(Optional.empty());
    when(userRepository.findByUsername(eq("newusername"))).thenReturn(Optional.of(targetUser));

    boolean result = validator.isValid(command, context);

    assertThat(result, is(true));
  }

  @Test
  public void should_return_true_when_both_email_and_username_belong_to_target_user() {
    when(userRepository.findByEmail(eq("newemail@example.com"))).thenReturn(Optional.of(targetUser));
    when(userRepository.findByUsername(eq("newusername"))).thenReturn(Optional.of(targetUser));

    boolean result = validator.isValid(command, context);

    assertThat(result, is(true));
  }

  @Test
  public void should_return_false_when_email_belongs_to_other_user() {
    setupEmailConstraintViolationMocks();
    when(userRepository.findByEmail(eq("newemail@example.com"))).thenReturn(Optional.of(otherUser));
    when(userRepository.findByUsername(eq("newusername"))).thenReturn(Optional.empty());

    boolean result = validator.isValid(command, context);

    assertThat(result, is(false));
    verify(context).disableDefaultConstraintViolation();
    verify(context).buildConstraintViolationWithTemplate(eq("email already exist"));
    verify(violationBuilder).addPropertyNode(eq("email"));
    verify(nodeBuilder).addConstraintViolation();
  }

  @Test
  public void should_return_false_when_username_belongs_to_other_user() {
    setupUsernameConstraintViolationMocks();
    when(userRepository.findByEmail(eq("newemail@example.com"))).thenReturn(Optional.empty());
    when(userRepository.findByUsername(eq("newusername"))).thenReturn(Optional.of(otherUser));

    boolean result = validator.isValid(command, context);

    assertThat(result, is(false));
    verify(context).disableDefaultConstraintViolation();
    verify(context).buildConstraintViolationWithTemplate(eq("username already exist"));
    verify(violationBuilder).addPropertyNode(eq("username"));
    verify(nodeBuilder).addConstraintViolation();
  }

  @Test
  public void should_return_false_when_both_email_and_username_belong_to_other_users() {
    setupBothConstraintViolationMocks();
    when(userRepository.findByEmail(eq("newemail@example.com"))).thenReturn(Optional.of(otherUser));
    when(userRepository.findByUsername(eq("newusername"))).thenReturn(Optional.of(otherUser));

    boolean result = validator.isValid(command, context);

    assertThat(result, is(false));
    verify(context).disableDefaultConstraintViolation();
    verify(context).buildConstraintViolationWithTemplate(eq("email already exist"));
    verify(context).buildConstraintViolationWithTemplate(eq("username already exist"));
    verify(violationBuilder).addPropertyNode(eq("email"));
    verify(violationBuilder).addPropertyNode(eq("username"));
  }

  @Test
  public void should_handle_null_email_in_param() {
    UpdateUserParam paramWithNullEmail = new UpdateUserParam(null, "newpassword", "newusername", "newbio", "newimage");
    UpdateUserCommand commandWithNullEmail = new UpdateUserCommand(targetUser, paramWithNullEmail);

    when(userRepository.findByEmail(eq(null))).thenReturn(Optional.empty());
    when(userRepository.findByUsername(eq("newusername"))).thenReturn(Optional.empty());

    boolean result = validator.isValid(commandWithNullEmail, context);

    assertThat(result, is(true));
  }

  @Test
  public void should_handle_null_username_in_param() {
    UpdateUserParam paramWithNullUsername = new UpdateUserParam("newemail@example.com", "newpassword", null, "newbio", "newimage");
    UpdateUserCommand commandWithNullUsername = new UpdateUserCommand(targetUser, paramWithNullUsername);

    when(userRepository.findByEmail(eq("newemail@example.com"))).thenReturn(Optional.empty());
    when(userRepository.findByUsername(eq(null))).thenReturn(Optional.empty());

    boolean result = validator.isValid(commandWithNullUsername, context);

    assertThat(result, is(true));
  }

  @Test
  public void should_handle_empty_email_in_param() {
    UpdateUserParam paramWithEmptyEmail = new UpdateUserParam("", "newpassword", "newusername", "newbio", "newimage");
    UpdateUserCommand commandWithEmptyEmail = new UpdateUserCommand(targetUser, paramWithEmptyEmail);

    when(userRepository.findByEmail(eq(""))).thenReturn(Optional.empty());
    when(userRepository.findByUsername(eq("newusername"))).thenReturn(Optional.empty());

    boolean result = validator.isValid(commandWithEmptyEmail, context);

    assertThat(result, is(true));
  }

  @Test
  public void should_handle_empty_username_in_param() {
    UpdateUserParam paramWithEmptyUsername = new UpdateUserParam("newemail@example.com", "newpassword", "", "newbio", "newimage");
    UpdateUserCommand commandWithEmptyUsername = new UpdateUserCommand(targetUser, paramWithEmptyUsername);

    when(userRepository.findByEmail(eq("newemail@example.com"))).thenReturn(Optional.empty());
    when(userRepository.findByUsername(eq(""))).thenReturn(Optional.empty());

    boolean result = validator.isValid(commandWithEmptyUsername, context);

    assertThat(result, is(true));
  }

  @Test
  public void should_test_lambda_expression_for_email_validation_with_same_user() {
    when(userRepository.findByEmail(eq("newemail@example.com"))).thenReturn(Optional.of(targetUser));
    when(userRepository.findByUsername(eq("newusername"))).thenReturn(Optional.empty());

    boolean result = validator.isValid(command, context);

    assertThat(result, is(true));
  }

  @Test
  public void should_test_lambda_expression_for_username_validation_with_same_user() {
    when(userRepository.findByEmail(eq("newemail@example.com"))).thenReturn(Optional.empty());
    when(userRepository.findByUsername(eq("newusername"))).thenReturn(Optional.of(targetUser));

    boolean result = validator.isValid(command, context);

    assertThat(result, is(true));
  }

  @Test
  public void should_test_lambda_expression_for_email_validation_with_different_user() {
    setupEmailConstraintViolationMocks();
    User differentUser = new User("different@example.com", "differentuser", "password", "bio", "image");
    when(userRepository.findByEmail(eq("newemail@example.com"))).thenReturn(Optional.of(differentUser));
    when(userRepository.findByUsername(eq("newusername"))).thenReturn(Optional.empty());

    boolean result = validator.isValid(command, context);

    assertThat(result, is(false));
    verify(context).disableDefaultConstraintViolation();
    verify(context).buildConstraintViolationWithTemplate(eq("email already exist"));
  }

  @Test
  public void should_test_lambda_expression_for_username_validation_with_different_user() {
    setupUsernameConstraintViolationMocks();
    User differentUser = new User("different@example.com", "differentuser", "password", "bio", "image");
    when(userRepository.findByEmail(eq("newemail@example.com"))).thenReturn(Optional.empty());
    when(userRepository.findByUsername(eq("newusername"))).thenReturn(Optional.of(differentUser));

    boolean result = validator.isValid(command, context);

    assertThat(result, is(false));
    verify(context).disableDefaultConstraintViolation();
    verify(context).buildConstraintViolationWithTemplate(eq("username already exist"));
  }
}
