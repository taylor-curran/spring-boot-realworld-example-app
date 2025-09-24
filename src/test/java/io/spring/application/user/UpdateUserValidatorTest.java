package io.spring.application.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
class UpdateUserValidatorTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder;

    @InjectMocks
    private UpdateUserValidator validator;

    private User targetUser;
    private User otherUser;
    private UpdateUserCommand command;
    private UpdateUserParam param;

    @BeforeEach
    void setUp() {
        targetUser = new User("target@example.com", "targetuser", "password", "bio", "image");
        otherUser = new User("other@example.com", "otheruser", "password", "bio", "image");
        
        param = new UpdateUserParam("new@example.com", "newpassword", "newuser", "new bio", "new image");
        command = new UpdateUserCommand(targetUser, param);

        lenient().when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        lenient().when(violationBuilder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        lenient().when(nodeBuilder.addConstraintViolation()).thenReturn(context);
    }

    @Test
    void shouldReturnTrueWhenEmailAndUsernameAreAvailable() {
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());

        boolean result = validator.isValid(command, context);

        assertTrue(result);
        verify(userRepository).findByEmail("new@example.com");
        verify(userRepository).findByUsername("newuser");
        verifyNoInteractions(context);
    }

    @Test
    void shouldReturnTrueWhenEmailBelongsToTargetUser() {
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.of(targetUser));
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());

        boolean result = validator.isValid(command, context);

        assertTrue(result);
        verify(userRepository).findByEmail("new@example.com");
        verify(userRepository).findByUsername("newuser");
        verifyNoInteractions(context);
    }

    @Test
    void shouldReturnTrueWhenUsernameBelongsToTargetUser() {
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.of(targetUser));

        boolean result = validator.isValid(command, context);

        assertTrue(result);
        verify(userRepository).findByEmail("new@example.com");
        verify(userRepository).findByUsername("newuser");
        verifyNoInteractions(context);
    }

    @Test
    void shouldReturnTrueWhenBothEmailAndUsernameBelongToTargetUser() {
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.of(targetUser));
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.of(targetUser));

        boolean result = validator.isValid(command, context);

        assertTrue(result);
        verify(userRepository).findByEmail("new@example.com");
        verify(userRepository).findByUsername("newuser");
        verifyNoInteractions(context);
    }

    @Test
    void shouldReturnFalseWhenEmailBelongsToOtherUser() {
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.of(otherUser));
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());

        boolean result = validator.isValid(command, context);

        assertFalse(result);
        verify(userRepository).findByEmail("new@example.com");
        verify(userRepository).findByUsername("newuser");
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("email already exist");
        verify(violationBuilder).addPropertyNode("email");
        verify(nodeBuilder).addConstraintViolation();
    }

    @Test
    void shouldReturnFalseWhenUsernameBelongsToOtherUser() {
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.of(otherUser));

        boolean result = validator.isValid(command, context);

        assertFalse(result);
        verify(userRepository).findByEmail("new@example.com");
        verify(userRepository).findByUsername("newuser");
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("username already exist");
        verify(violationBuilder).addPropertyNode("username");
        verify(nodeBuilder).addConstraintViolation();
    }

    @Test
    void shouldReturnFalseWhenBothEmailAndUsernameBelongToOtherUsers() {
        User anotherUser = new User("another@example.com", "anotheruser", "password", "bio", "image");
        
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.of(otherUser));
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.of(anotherUser));

        boolean result = validator.isValid(command, context);

        assertFalse(result);
        verify(userRepository).findByEmail("new@example.com");
        verify(userRepository).findByUsername("newuser");
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("email already exist");
        verify(context).buildConstraintViolationWithTemplate("username already exist");
        verify(violationBuilder, times(2)).addPropertyNode(anyString());
        verify(nodeBuilder, times(2)).addConstraintViolation();
    }

    @Test
    void shouldReturnFalseWhenEmailBelongsToOtherUserAndUsernameToTargetUser() {
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.of(otherUser));
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.of(targetUser));

        boolean result = validator.isValid(command, context);

        assertFalse(result);
        verify(userRepository).findByEmail("new@example.com");
        verify(userRepository).findByUsername("newuser");
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("email already exist");
        verify(violationBuilder).addPropertyNode("email");
        verify(nodeBuilder).addConstraintViolation();
    }

    @Test
    void shouldReturnFalseWhenUsernameBelongsToOtherUserAndEmailToTargetUser() {
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.of(targetUser));
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.of(otherUser));

        boolean result = validator.isValid(command, context);

        assertFalse(result);
        verify(userRepository).findByEmail("new@example.com");
        verify(userRepository).findByUsername("newuser");
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("username already exist");
        verify(violationBuilder).addPropertyNode("username");
        verify(nodeBuilder).addConstraintViolation();
    }

    @Test
    void shouldHandleNullEmailInParam() {
        UpdateUserParam paramWithNullEmail = new UpdateUserParam(null, "password", "newuser", "bio", "image");
        UpdateUserCommand commandWithNullEmail = new UpdateUserCommand(targetUser, paramWithNullEmail);

        when(userRepository.findByEmail(null)).thenReturn(Optional.empty());
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());

        boolean result = validator.isValid(commandWithNullEmail, context);

        assertTrue(result);
        verify(userRepository).findByEmail(null);
        verify(userRepository).findByUsername("newuser");
    }

    @Test
    void shouldHandleNullUsernameInParam() {
        UpdateUserParam paramWithNullUsername = new UpdateUserParam("new@example.com", "password", null, "bio", "image");
        UpdateUserCommand commandWithNullUsername = new UpdateUserCommand(targetUser, paramWithNullUsername);

        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername(null)).thenReturn(Optional.empty());

        boolean result = validator.isValid(commandWithNullUsername, context);

        assertTrue(result);
        verify(userRepository).findByEmail("new@example.com");
        verify(userRepository).findByUsername(null);
    }

    @Test
    void shouldHandleEmptyEmailInParam() {
        UpdateUserParam paramWithEmptyEmail = new UpdateUserParam("", "password", "newuser", "bio", "image");
        UpdateUserCommand commandWithEmptyEmail = new UpdateUserCommand(targetUser, paramWithEmptyEmail);

        when(userRepository.findByEmail("")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());

        boolean result = validator.isValid(commandWithEmptyEmail, context);

        assertTrue(result);
        verify(userRepository).findByEmail("");
        verify(userRepository).findByUsername("newuser");
    }

    @Test
    void shouldHandleEmptyUsernameInParam() {
        UpdateUserParam paramWithEmptyUsername = new UpdateUserParam("new@example.com", "password", "", "bio", "image");
        UpdateUserCommand commandWithEmptyUsername = new UpdateUserCommand(targetUser, paramWithEmptyUsername);

        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("")).thenReturn(Optional.empty());

        boolean result = validator.isValid(commandWithEmptyUsername, context);

        assertTrue(result);
        verify(userRepository).findByEmail("new@example.com");
        verify(userRepository).findByUsername("");
    }

    @Test
    void shouldHandleRepositoryExceptionForEmail() {
        when(userRepository.findByEmail("new@example.com")).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> validator.isValid(command, context));
        verify(userRepository).findByEmail("new@example.com");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void shouldHandleRepositoryExceptionForUsername() {
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("newuser")).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> validator.isValid(command, context));
        verify(userRepository).findByEmail("new@example.com");
        verify(userRepository).findByUsername("newuser");
    }

    @Test
    void shouldHandleSpecialCharactersInEmailAndUsername() {
        UpdateUserParam specialParam = new UpdateUserParam("user+test@example-domain.co.uk", "password", "user!@#$%", "bio", "image");
        UpdateUserCommand specialCommand = new UpdateUserCommand(targetUser, specialParam);

        when(userRepository.findByEmail("user+test@example-domain.co.uk")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("user!@#$%")).thenReturn(Optional.empty());

        boolean result = validator.isValid(specialCommand, context);

        assertTrue(result);
        verify(userRepository).findByEmail("user+test@example-domain.co.uk");
        verify(userRepository).findByUsername("user!@#$%");
    }

    @Test
    void shouldHandleLongEmailAndUsername() {
        String longEmail = "very-long-email-".repeat(10) + "@example.com";
        String longUsername = "very-long-username-".repeat(10);
        
        UpdateUserParam longParam = new UpdateUserParam(longEmail, "password", longUsername, "bio", "image");
        UpdateUserCommand longCommand = new UpdateUserCommand(targetUser, longParam);

        when(userRepository.findByEmail(longEmail)).thenReturn(Optional.empty());
        when(userRepository.findByUsername(longUsername)).thenReturn(Optional.empty());

        boolean result = validator.isValid(longCommand, context);

        assertTrue(result);
        verify(userRepository).findByEmail(longEmail);
        verify(userRepository).findByUsername(longUsername);
    }

    @Test
    void shouldHandleUnicodeInEmailAndUsername() {
        UpdateUserParam unicodeParam = new UpdateUserParam("用户@例え.テスト", "password", "用户名", "bio", "image");
        UpdateUserCommand unicodeCommand = new UpdateUserCommand(targetUser, unicodeParam);

        when(userRepository.findByEmail("用户@例え.テスト")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("用户名")).thenReturn(Optional.empty());

        boolean result = validator.isValid(unicodeCommand, context);

        assertTrue(result);
        verify(userRepository).findByEmail("用户@例え.テスト");
        verify(userRepository).findByUsername("用户名");
    }
}
