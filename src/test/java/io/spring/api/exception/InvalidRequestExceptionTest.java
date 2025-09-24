package io.spring.api.exception;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.validation.Errors;

class InvalidRequestExceptionTest {

    @Test
    void shouldCreateInvalidRequestExceptionWithErrors() {
        Errors errors = mock(Errors.class);
        
        InvalidRequestException exception = new InvalidRequestException(errors);
        
        assertNotNull(exception);
        assertEquals(errors, exception.getErrors());
        assertEquals("", exception.getMessage());
    }

    @Test
    void shouldCreateInvalidRequestExceptionWithNullErrors() {
        InvalidRequestException exception = new InvalidRequestException(null);
        
        assertNotNull(exception);
        assertNull(exception.getErrors());
        assertEquals("", exception.getMessage());
    }

    @Test
    void shouldBeRuntimeException() {
        Errors errors = mock(Errors.class);
        InvalidRequestException exception = new InvalidRequestException(errors);
        
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void shouldReturnCorrectErrors() {
        Errors errors = mock(Errors.class);
        InvalidRequestException exception = new InvalidRequestException(errors);
        
        assertSame(errors, exception.getErrors());
    }
}
