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
class DuplicatedEmailValidatorTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ConstraintValidatorContext context;

    @InjectMocks
    private DuplicatedEmailValidator validator;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new User("existing@example.com", "existinguser", "password", "bio", "image");
    }

    @Test
    void shouldReturnTrueForNullEmail() {
        boolean result = validator.isValid(null, context);
        
        assertTrue(result);
        verifyNoInteractions(userRepository);
    }

    @Test
    void shouldReturnTrueForEmptyEmail() {
        boolean result = validator.isValid("", context);
        
        assertTrue(result);
        verifyNoInteractions(userRepository);
    }

    @Test
    void shouldReturnTrueForWhitespaceOnlyEmail() {
        String email = "   ";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        
        boolean result = validator.isValid(email, context);
        
        assertTrue(result);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void shouldReturnTrueWhenEmailDoesNotExist() {
        String email = "new@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        
        boolean result = validator.isValid(email, context);
        
        assertTrue(result);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void shouldReturnFalseWhenEmailAlreadyExists() {
        String email = "existing@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));
        
        boolean result = validator.isValid(email, context);
        
        assertFalse(result);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void shouldReturnTrueForValidEmailFormat() {
        String email = "test.user+tag@example.co.uk";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        
        boolean result = validator.isValid(email, context);
        
        assertTrue(result);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void shouldReturnTrueForLongEmail() {
        String email = "very-long-email-address-".repeat(5) + "@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        
        boolean result = validator.isValid(email, context);
        
        assertTrue(result);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void shouldReturnFalseForLongEmailWhenExists() {
        String email = "very-long-email-address-".repeat(5) + "@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));
        
        boolean result = validator.isValid(email, context);
        
        assertFalse(result);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void shouldHandleCaseSensitiveEmails() {
        String email = "Test.User@Example.COM";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        
        boolean result = validator.isValid(email, context);
        
        assertTrue(result);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void shouldHandleNumericEmails() {
        String email = "12345@67890.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        
        boolean result = validator.isValid(email, context);
        
        assertTrue(result);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void shouldHandleSpecialCharactersInEmail() {
        String email = "user+test-tag@sub.example-domain.org";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        
        boolean result = validator.isValid(email, context);
        
        assertTrue(result);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void shouldHandleInternationalDomains() {
        String email = "user@例え.テスト";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        
        boolean result = validator.isValid(email, context);
        
        assertTrue(result);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void shouldHandleRepositoryException() {
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenThrow(new RuntimeException("Database error"));
        
        assertThrows(RuntimeException.class, () -> validator.isValid(email, context));
        verify(userRepository).findByEmail(email);
    }

    @Test
    void shouldValidateEmailsWithPlusAddressing() {
        String email = "user+newsletter@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        
        boolean result = validator.isValid(email, context);
        
        assertTrue(result);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void shouldValidateEmailsWithDots() {
        String email = "first.last@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        
        boolean result = validator.isValid(email, context);
        
        assertTrue(result);
        verify(userRepository).findByEmail(email);
    }
}
