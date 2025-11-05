package io.spring.api.exception;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

@ExtendWith(MockitoExtension.class)
class CustomizeExceptionHandlerTest {

    @InjectMocks
    private CustomizeExceptionHandler exceptionHandler;

    @Mock
    private WebRequest webRequest;

    @Mock
    private Errors errors;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        exceptionHandler = new CustomizeExceptionHandler();
    }

    @Test
    void shouldHandleInvalidRequestException() {
        FieldError fieldError = new FieldError("user", "email", "rejected", false, new String[]{"invalid"}, null, "Email is invalid");
        when(errors.getFieldErrors()).thenReturn(Arrays.asList(fieldError));
        
        InvalidRequestException exception = new InvalidRequestException(errors);
        
        ResponseEntity<Object> response = exceptionHandler.handleInvalidRequest(exception, webRequest);
        
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof ErrorResource);
        
        ErrorResource errorResource = (ErrorResource) response.getBody();
        assertEquals(1, errorResource.getFieldErrors().size());
        
        FieldErrorResource fieldErrorResource = errorResource.getFieldErrors().get(0);
        assertEquals("user", fieldErrorResource.getResource());
        assertEquals("email", fieldErrorResource.getField());
        assertEquals("invalid", fieldErrorResource.getCode());
        assertEquals("Email is invalid", fieldErrorResource.getMessage());
    }

    @Test
    void shouldHandleInvalidRequestExceptionWithMultipleErrors() {
        FieldError emailError = new FieldError("user", "email", "rejected", false, new String[]{"invalid"}, null, "Email is invalid");
        FieldError usernameError = new FieldError("user", "username", "rejected", false, new String[]{"required"}, null, "Username is required");
        when(errors.getFieldErrors()).thenReturn(Arrays.asList(emailError, usernameError));
        
        InvalidRequestException exception = new InvalidRequestException(errors);
        
        ResponseEntity<Object> response = exceptionHandler.handleInvalidRequest(exception, webRequest);
        
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        ErrorResource errorResource = (ErrorResource) response.getBody();
        assertEquals(2, errorResource.getFieldErrors().size());
    }

    @Test
    void shouldHandleInvalidAuthenticationException() {
        InvalidAuthenticationException exception = new InvalidAuthenticationException();
        
        ResponseEntity<Object> response = exceptionHandler.handleInvalidAuthentication(exception, webRequest);
        
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof HashMap);
        
        @SuppressWarnings("unchecked")
        HashMap<String, Object> body = (HashMap<String, Object>) response.getBody();
        assertNotNull(body.get("message"));
    }

    @Test
    void shouldHandleMethodArgumentNotValidException() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        FieldError fieldError = new FieldError("article", "title", "rejected", false, new String[]{"required"}, null, "Title is required");
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError));
        
        HttpHeaders headers = new HttpHeaders();
        HttpStatus status = HttpStatus.BAD_REQUEST;
        
        ResponseEntity<Object> response = exceptionHandler.handleMethodArgumentNotValid(
            exception, headers, status, webRequest);
        
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof ErrorResource);
        
        ErrorResource errorResource = (ErrorResource) response.getBody();
        assertEquals(1, errorResource.getFieldErrors().size());
        
        FieldErrorResource fieldErrorResource = errorResource.getFieldErrors().get(0);
        assertEquals("article", fieldErrorResource.getResource());
        assertEquals("title", fieldErrorResource.getField());
        assertEquals("required", fieldErrorResource.getCode());
        assertEquals("Title is required", fieldErrorResource.getMessage());
    }

    @Test
    void shouldHandleConstraintViolationException() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        ConstraintDescriptor<?> descriptor = mock(ConstraintDescriptor.class);
        Path propertyPath = mock(Path.class);
        java.lang.annotation.Annotation annotation = mock(java.lang.annotation.Annotation.class);
        
        when(violation.getRootBeanClass()).thenReturn((Class) String.class);
        when(violation.getPropertyPath()).thenReturn(propertyPath);
        when(propertyPath.toString()).thenReturn("email");
        when(violation.getConstraintDescriptor()).thenReturn((ConstraintDescriptor) descriptor);
        when(descriptor.getAnnotation()).thenReturn(annotation);
        when(annotation.annotationType()).thenReturn((Class) Override.class);
        when(violation.getMessage()).thenReturn("Email format is invalid");
        
        Set<ConstraintViolation<?>> violations = Set.of(violation);
        ConstraintViolationException exception = new ConstraintViolationException(violations);
        
        ErrorResource response = exceptionHandler.handleConstraintViolation(exception, webRequest);
        
        assertNotNull(response);
        assertEquals(1, response.getFieldErrors().size());
        
        FieldErrorResource fieldErrorResource = response.getFieldErrors().get(0);
        assertEquals("java.lang.String", fieldErrorResource.getResource());
        assertEquals("email", fieldErrorResource.getField());
        assertEquals("Override", fieldErrorResource.getCode());
        assertEquals("Email format is invalid", fieldErrorResource.getMessage());
    }

    @Test
    void shouldHandleConstraintViolationExceptionWithNestedProperty() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        ConstraintDescriptor<?> descriptor = mock(ConstraintDescriptor.class);
        Path propertyPath = mock(Path.class);
        java.lang.annotation.Annotation annotation = mock(java.lang.annotation.Annotation.class);
        
        when(violation.getRootBeanClass()).thenReturn((Class) String.class);
        when(violation.getPropertyPath()).thenReturn(propertyPath);
        when(propertyPath.toString()).thenReturn("user.profile.email");
        when(violation.getConstraintDescriptor()).thenReturn((ConstraintDescriptor) descriptor);
        when(descriptor.getAnnotation()).thenReturn(annotation);
        when(annotation.annotationType()).thenReturn((Class) Override.class);
        when(violation.getMessage()).thenReturn("Email format is invalid");
        
        Set<ConstraintViolation<?>> violations = Set.of(violation);
        ConstraintViolationException exception = new ConstraintViolationException(violations);
        
        ErrorResource response = exceptionHandler.handleConstraintViolation(exception, webRequest);
        
        assertNotNull(response);
        assertEquals(1, response.getFieldErrors().size());
        
        FieldErrorResource fieldErrorResource = response.getFieldErrors().get(0);
        assertEquals("email", fieldErrorResource.getField());
    }

    @Test
    void shouldHandleConstraintViolationExceptionWithMultipleViolations() {
        ConstraintViolation<?> violation1 = mock(ConstraintViolation.class);
        ConstraintViolation<?> violation2 = mock(ConstraintViolation.class);
        ConstraintDescriptor<?> descriptor1 = mock(ConstraintDescriptor.class);
        ConstraintDescriptor<?> descriptor2 = mock(ConstraintDescriptor.class);
        Path propertyPath1 = mock(Path.class);
        Path propertyPath2 = mock(Path.class);
        java.lang.annotation.Annotation annotation1 = mock(java.lang.annotation.Annotation.class);
        java.lang.annotation.Annotation annotation2 = mock(java.lang.annotation.Annotation.class);
        
        when(violation1.getRootBeanClass()).thenReturn((Class) String.class);
        when(violation1.getPropertyPath()).thenReturn(propertyPath1);
        when(propertyPath1.toString()).thenReturn("email");
        when(violation1.getConstraintDescriptor()).thenReturn((ConstraintDescriptor) descriptor1);
        when(descriptor1.getAnnotation()).thenReturn(annotation1);
        when(annotation1.annotationType()).thenReturn((Class) Override.class);
        when(violation1.getMessage()).thenReturn("Email is invalid");
        
        when(violation2.getRootBeanClass()).thenReturn((Class) String.class);
        when(violation2.getPropertyPath()).thenReturn(propertyPath2);
        when(propertyPath2.toString()).thenReturn("username");
        when(violation2.getConstraintDescriptor()).thenReturn((ConstraintDescriptor) descriptor2);
        when(descriptor2.getAnnotation()).thenReturn(annotation2);
        when(annotation2.annotationType()).thenReturn((Class) Override.class);
        when(violation2.getMessage()).thenReturn("Username is required");
        
        Set<ConstraintViolation<?>> violations = Set.of(violation1, violation2);
        ConstraintViolationException exception = new ConstraintViolationException(violations);
        
        ErrorResource response = exceptionHandler.handleConstraintViolation(exception, webRequest);
        
        assertNotNull(response);
        assertEquals(2, response.getFieldErrors().size());
    }

}
