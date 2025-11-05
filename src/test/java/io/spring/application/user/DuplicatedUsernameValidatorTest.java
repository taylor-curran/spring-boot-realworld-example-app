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
class DuplicatedUsernameValidatorTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ConstraintValidatorContext context;

    @InjectMocks
    private DuplicatedUsernameValidator validator;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new User("existing@example.com", "existinguser", "password", "bio", "image");
    }

    @Test
    void shouldReturnTrueForNullUsername() {
        boolean result = validator.isValid(null, context);
        
        assertTrue(result);
        verifyNoInteractions(userRepository);
    }

    @Test
    void shouldReturnTrueForEmptyUsername() {
        boolean result = validator.isValid("", context);
        
        assertTrue(result);
        verifyNoInteractions(userRepository);
    }

    @Test
    void shouldReturnTrueForWhitespaceOnlyUsername() {
        String username = "   ";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        
        boolean result = validator.isValid(username, context);
        
        assertTrue(result);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void shouldReturnTrueWhenUsernameDoesNotExist() {
        String username = "newuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        
        boolean result = validator.isValid(username, context);
        
        assertTrue(result);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void shouldReturnFalseWhenUsernameAlreadyExists() {
        String username = "existinguser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));
        
        boolean result = validator.isValid(username, context);
        
        assertFalse(result);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void shouldReturnTrueForUsernameWithSpecialCharacters() {
        String username = "user!@#$%";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        
        boolean result = validator.isValid(username, context);
        
        assertTrue(result);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void shouldReturnTrueForLongUsername() {
        String username = "very-long-username-".repeat(10);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        
        boolean result = validator.isValid(username, context);
        
        assertTrue(result);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void shouldReturnFalseForLongUsernameWhenExists() {
        String username = "very-long-username-".repeat(10);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));
        
        boolean result = validator.isValid(username, context);
        
        assertFalse(result);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void shouldHandleCaseSensitiveUsernames() {
        String username = "TestUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        
        boolean result = validator.isValid(username, context);
        
        assertTrue(result);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void shouldHandleNumericUsernames() {
        String username = "12345";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        
        boolean result = validator.isValid(username, context);
        
        assertTrue(result);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void shouldHandleUnicodeUsernames() {
        String username = "用户名";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        
        boolean result = validator.isValid(username, context);
        
        assertTrue(result);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void shouldHandleRepositoryException() {
        String username = "testuser";
        when(userRepository.findByUsername(username)).thenThrow(new RuntimeException("Database error"));
        
        assertThrows(RuntimeException.class, () -> validator.isValid(username, context));
        verify(userRepository).findByUsername(username);
    }
}
