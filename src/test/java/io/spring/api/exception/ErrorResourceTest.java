package io.spring.api.exception;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class ErrorResourceTest {

    @Test
    void shouldCreateErrorResourceWithFieldErrors() {
        FieldErrorResource fieldError1 = new FieldErrorResource("user", "email", "invalid", "Email is invalid");
        FieldErrorResource fieldError2 = new FieldErrorResource("user", "username", "required", "Username is required");
        List<FieldErrorResource> fieldErrors = Arrays.asList(fieldError1, fieldError2);
        
        ErrorResource errorResource = new ErrorResource(fieldErrors);
        
        assertNotNull(errorResource);
        assertEquals(fieldErrors, errorResource.getFieldErrors());
        assertEquals(2, errorResource.getFieldErrors().size());
    }

    @Test
    void shouldCreateErrorResourceWithEmptyFieldErrors() {
        List<FieldErrorResource> fieldErrors = Collections.emptyList();
        
        ErrorResource errorResource = new ErrorResource(fieldErrors);
        
        assertNotNull(errorResource);
        assertEquals(fieldErrors, errorResource.getFieldErrors());
        assertTrue(errorResource.getFieldErrors().isEmpty());
    }

    @Test
    void shouldCreateErrorResourceWithNullFieldErrors() {
        ErrorResource errorResource = new ErrorResource(null);
        
        assertNotNull(errorResource);
        assertNull(errorResource.getFieldErrors());
    }

    @Test
    void shouldCreateErrorResourceWithSingleFieldError() {
        FieldErrorResource fieldError = new FieldErrorResource("article", "title", "required", "Title is required");
        List<FieldErrorResource> fieldErrors = Arrays.asList(fieldError);
        
        ErrorResource errorResource = new ErrorResource(fieldErrors);
        
        assertNotNull(errorResource);
        assertEquals(1, errorResource.getFieldErrors().size());
        assertEquals(fieldError, errorResource.getFieldErrors().get(0));
    }

    @Test
    void shouldReturnCorrectFieldErrors() {
        FieldErrorResource fieldError = new FieldErrorResource("comment", "body", "blank", "Body cannot be blank");
        List<FieldErrorResource> fieldErrors = Arrays.asList(fieldError);
        
        ErrorResource errorResource = new ErrorResource(fieldErrors);
        
        assertSame(fieldErrors, errorResource.getFieldErrors());
    }
}
