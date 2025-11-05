package io.spring.api.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import javax.validation.constraints.NotNull;
import javax.validation.Payload;
import java.lang.annotation.Annotation;

@ExtendWith(MockitoExtension.class)
public class CustomizeExceptionHandlerTest {

  @InjectMocks
  private CustomizeExceptionHandler exceptionHandler;

  @Mock
  private WebRequest webRequest;

  @BeforeEach
  public void setUp() {
    exceptionHandler = new CustomizeExceptionHandler();
  }

  @Test
  public void should_handle_invalid_request_exception() {
    Errors errors = mock(Errors.class);
    FieldError fieldError = new FieldError("user", "email", null, false, new String[]{"invalid"}, null, "Email is invalid");
    when(errors.getFieldErrors()).thenReturn(Arrays.asList(fieldError));
    
    InvalidRequestException exception = new InvalidRequestException(errors);
    
    ResponseEntity<Object> response = exceptionHandler.handleInvalidRequest(exception, webRequest);
    
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    assertThat(response.getBody()).isInstanceOf(ErrorResource.class);
    
    ErrorResource errorResource = (ErrorResource) response.getBody();
    assertThat(errorResource.getFieldErrors()).hasSize(1);
    
    FieldErrorResource fieldErrorResource = errorResource.getFieldErrors().get(0);
    assertThat(fieldErrorResource.getResource()).isEqualTo("user");
    assertThat(fieldErrorResource.getField()).isEqualTo("email");
    assertThat(fieldErrorResource.getCode()).isEqualTo("invalid");
    assertThat(fieldErrorResource.getMessage()).isEqualTo("Email is invalid");
  }

  @Test
  public void should_handle_invalid_request_exception_with_multiple_errors() {
    Errors errors = mock(Errors.class);
    FieldError emailError = new FieldError("user", "email", null, false, new String[]{"invalid"}, null, "Email is invalid");
    FieldError usernameError = new FieldError("user", "username", null, false, new String[]{"required"}, null, "Username is required");
    when(errors.getFieldErrors()).thenReturn(Arrays.asList(emailError, usernameError));
    
    InvalidRequestException exception = new InvalidRequestException(errors);
    
    ResponseEntity<Object> response = exceptionHandler.handleInvalidRequest(exception, webRequest);
    
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    ErrorResource errorResource = (ErrorResource) response.getBody();
    assertThat(errorResource.getFieldErrors()).hasSize(2);
  }

  @Test
  public void should_handle_invalid_authentication_exception() {
    InvalidAuthenticationException exception = new InvalidAuthenticationException();
    
    ResponseEntity<Object> response = exceptionHandler.handleInvalidAuthentication(exception, webRequest);
    
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    assertThat(response.getBody()).isInstanceOf(HashMap.class);
    
    @SuppressWarnings("unchecked")
    HashMap<String, Object> body = (HashMap<String, Object>) response.getBody();
    assertThat(body.get("message")).isEqualTo("invalid email or password");
  }

  @Test
  public void should_handle_method_argument_not_valid_exception() {
    MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
    BindingResult bindingResult = mock(BindingResult.class);
    FieldError fieldError = new FieldError("article", "title", null, false, new String[]{"required"}, null, "Title is required");
    
    when(exception.getBindingResult()).thenReturn(bindingResult);
    when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError));
    
    HttpHeaders headers = new HttpHeaders();
    HttpStatus status = HttpStatus.BAD_REQUEST;
    
    ResponseEntity<Object> response = exceptionHandler.handleMethodArgumentNotValid(
        exception, headers, status, webRequest);
    
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    assertThat(response.getBody()).isInstanceOf(ErrorResource.class);
    
    ErrorResource errorResource = (ErrorResource) response.getBody();
    assertThat(errorResource.getFieldErrors()).hasSize(1);
    
    FieldErrorResource fieldErrorResource = errorResource.getFieldErrors().get(0);
    assertThat(fieldErrorResource.getResource()).isEqualTo("article");
    assertThat(fieldErrorResource.getField()).isEqualTo("title");
    assertThat(fieldErrorResource.getCode()).isEqualTo("required");
    assertThat(fieldErrorResource.getMessage()).isEqualTo("Title is required");
  }

  @Test
  public void should_handle_constraint_violation_exception() {
    ConstraintViolation<?> violation = mock(ConstraintViolation.class);
    Path propertyPath = mock(Path.class);
    ConstraintDescriptor<?> constraintDescriptor = mock(ConstraintDescriptor.class);
    
    when(violation.getRootBeanClass()).thenReturn((Class) String.class);
    when(violation.getPropertyPath()).thenReturn(propertyPath);
    when(propertyPath.toString()).thenReturn("method.arg0.email");
    when(violation.getConstraintDescriptor()).thenReturn((ConstraintDescriptor) constraintDescriptor);
    when(constraintDescriptor.getAnnotation()).thenReturn(createNotNullAnnotation());
    when(violation.getMessage()).thenReturn("must not be null");
    
    Set<ConstraintViolation<?>> violations = Set.of(violation);
    ConstraintViolationException exception = new ConstraintViolationException(violations);
    
    ErrorResource response = exceptionHandler.handleConstraintViolation(exception, webRequest);
    
    assertThat(response.getFieldErrors()).hasSize(1);
    
    FieldErrorResource fieldErrorResource = response.getFieldErrors().get(0);
    assertThat(fieldErrorResource.getResource()).isEqualTo("java.lang.String");
    assertThat(fieldErrorResource.getField()).isEqualTo("email");
    assertThat(fieldErrorResource.getCode()).isEqualTo("NotNull");
    assertThat(fieldErrorResource.getMessage()).isEqualTo("must not be null");
  }

  @Test
  public void should_handle_constraint_violation_with_simple_property_path() {
    ConstraintViolation<?> violation = mock(ConstraintViolation.class);
    Path propertyPath = mock(Path.class);
    ConstraintDescriptor<?> constraintDescriptor = mock(ConstraintDescriptor.class);
    
    when(violation.getRootBeanClass()).thenReturn((Class) String.class);
    when(violation.getPropertyPath()).thenReturn(propertyPath);
    when(propertyPath.toString()).thenReturn("email");
    when(violation.getConstraintDescriptor()).thenReturn((ConstraintDescriptor) constraintDescriptor);
    when(constraintDescriptor.getAnnotation()).thenReturn(createNotNullAnnotation());
    when(violation.getMessage()).thenReturn("must not be null");
    
    Set<ConstraintViolation<?>> violations = Set.of(violation);
    ConstraintViolationException exception = new ConstraintViolationException(violations);
    
    ErrorResource response = exceptionHandler.handleConstraintViolation(exception, webRequest);
    
    assertThat(response.getFieldErrors()).hasSize(1);
    FieldErrorResource fieldErrorResource = response.getFieldErrors().get(0);
    assertThat(fieldErrorResource.getField()).isEqualTo("email");
  }

  @Test
  public void should_handle_constraint_violation_with_complex_property_path() {
    ConstraintViolation<?> violation = mock(ConstraintViolation.class);
    Path propertyPath = mock(Path.class);
    ConstraintDescriptor<?> constraintDescriptor = mock(ConstraintDescriptor.class);
    
    when(violation.getRootBeanClass()).thenReturn((Class) String.class);
    when(violation.getPropertyPath()).thenReturn(propertyPath);
    when(propertyPath.toString()).thenReturn("method.arg0.user.email.domain");
    when(violation.getConstraintDescriptor()).thenReturn((ConstraintDescriptor) constraintDescriptor);
    when(constraintDescriptor.getAnnotation()).thenReturn(createNotNullAnnotation());
    when(violation.getMessage()).thenReturn("must not be null");
    
    Set<ConstraintViolation<?>> violations = Set.of(violation);
    ConstraintViolationException exception = new ConstraintViolationException(violations);
    
    ErrorResource response = exceptionHandler.handleConstraintViolation(exception, webRequest);
    
    assertThat(response.getFieldErrors()).hasSize(1);
    FieldErrorResource fieldErrorResource = response.getFieldErrors().get(0);
    assertThat(fieldErrorResource.getField()).isEqualTo("user.email.domain");
  }

  @Test
  public void should_handle_multiple_constraint_violations() {
    ConstraintViolation<?> violation1 = mock(ConstraintViolation.class);
    ConstraintViolation<?> violation2 = mock(ConstraintViolation.class);
    Path propertyPath1 = mock(Path.class);
    Path propertyPath2 = mock(Path.class);
    ConstraintDescriptor<?> constraintDescriptor1 = mock(ConstraintDescriptor.class);
    ConstraintDescriptor<?> constraintDescriptor2 = mock(ConstraintDescriptor.class);
    
    when(violation1.getRootBeanClass()).thenReturn((Class) String.class);
    when(violation1.getPropertyPath()).thenReturn(propertyPath1);
    when(propertyPath1.toString()).thenReturn("email");
    when(violation1.getConstraintDescriptor()).thenReturn((ConstraintDescriptor) constraintDescriptor1);
    when(constraintDescriptor1.getAnnotation()).thenReturn(createNotNullAnnotation());
    when(violation1.getMessage()).thenReturn("must not be null");
    
    when(violation2.getRootBeanClass()).thenReturn((Class) String.class);
    when(violation2.getPropertyPath()).thenReturn(propertyPath2);
    when(propertyPath2.toString()).thenReturn("username");
    when(violation2.getConstraintDescriptor()).thenReturn((ConstraintDescriptor) constraintDescriptor2);
    when(constraintDescriptor2.getAnnotation()).thenReturn(createNotNullAnnotation());
    when(violation2.getMessage()).thenReturn("must not be null");
    
    Set<ConstraintViolation<?>> violations = Set.of(violation1, violation2);
    ConstraintViolationException exception = new ConstraintViolationException(violations);
    
    ErrorResource response = exceptionHandler.handleConstraintViolation(exception, webRequest);
    
    assertThat(response.getFieldErrors()).hasSize(2);
  }

  private NotNull createNotNullAnnotation() {
    return new NotNull() {
      @Override
      public String message() {
        return "must not be null";
      }

      @Override
      public Class<?>[] groups() {
        return new Class[0];
      }

      @Override
      public Class<? extends Payload>[] payload() {
        return new Class[0];
      }

      @Override
      public Class<? extends Annotation> annotationType() {
        return NotNull.class;
      }
    };
  }
}
