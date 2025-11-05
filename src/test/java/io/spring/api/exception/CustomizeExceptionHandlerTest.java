package io.spring.api.exception;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

public class CustomizeExceptionHandlerTest {

  private CustomizeExceptionHandler handler;
  private WebRequest webRequest;

  @BeforeEach
  public void setUp() {
    handler = new CustomizeExceptionHandler();
    webRequest = mock(WebRequest.class);
  }

  @Test
  public void should_handle_invalid_request_exception() {
    Errors errors = mock(Errors.class);
    FieldError fieldError = new FieldError("user", "email", "Email is required");
    when(errors.getFieldErrors()).thenReturn(Arrays.asList(fieldError));
    
    InvalidRequestException exception = new InvalidRequestException(errors);
    
    ResponseEntity<Object> response = handler.handleInvalidRequest(exception, webRequest);
    
    assertThat(response, notNullValue());
    assertThat(response.getStatusCode(), is(HttpStatus.UNPROCESSABLE_ENTITY));
    assertThat(response.getBody(), notNullValue());
    assertThat(response.getBody() instanceof ErrorResource, is(true));
  }

  @Test
  public void should_handle_invalid_request_exception_with_multiple_errors() {
    Errors errors = mock(Errors.class);
    FieldError fieldError1 = new FieldError("user", "email", "Email is required");
    FieldError fieldError2 = new FieldError("user", "username", "Username must be between 1 and 255 characters");
    when(errors.getFieldErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));
    
    InvalidRequestException exception = new InvalidRequestException(errors);
    
    ResponseEntity<Object> response = handler.handleInvalidRequest(exception, webRequest);
    
    assertThat(response, notNullValue());
    assertThat(response.getStatusCode(), is(HttpStatus.UNPROCESSABLE_ENTITY));
    ErrorResource errorResource = (ErrorResource) response.getBody();
    assertThat(errorResource.getFieldErrors().size(), is(2));
  }

  @Test
  public void should_handle_invalid_authentication_exception() {
    InvalidAuthenticationException exception = new InvalidAuthenticationException();
    
    ResponseEntity<Object> response = handler.handleInvalidAuthentication(exception, webRequest);
    
    assertThat(response, notNullValue());
    assertThat(response.getStatusCode(), is(HttpStatus.UNPROCESSABLE_ENTITY));
    assertThat(response.getBody(), notNullValue());
    
    @SuppressWarnings("unchecked")
    Map<String, Object> body = (Map<String, Object>) response.getBody();
    assertThat(body.get("message"), equalTo("invalid email or password"));
  }

  @Test
  public void should_handle_method_argument_not_valid_exception() {
    Errors errors = mock(Errors.class);
    FieldError fieldError = new FieldError("user", "password", "Password is required");
    when(errors.getFieldErrors()).thenReturn(Arrays.asList(fieldError));
    
    InvalidRequestException exception = new InvalidRequestException(errors);
    
    ResponseEntity<Object> response = handler.handleInvalidRequest(exception, webRequest);
    
    assertThat(response, notNullValue());
    assertThat(response.getStatusCode(), is(HttpStatus.UNPROCESSABLE_ENTITY));
    assertThat(response.getBody() instanceof ErrorResource, is(true));
    ErrorResource errorResource = (ErrorResource) response.getBody();
    assertThat(errorResource.getFieldErrors().size(), is(1));
  }

  @Test
  public void should_handle_constraint_violation_exception() {
    ConstraintViolation<?> violation = createMockConstraintViolation();
    Set<ConstraintViolation<?>> violations = Set.of(violation);
    ConstraintViolationException exception = new ConstraintViolationException("Validation failed", violations);
    
    ErrorResource response = handler.handleConstraintViolation(exception, webRequest);
    
    assertThat(response, notNullValue());
    assertThat(response.getFieldErrors().size(), is(1));
    FieldErrorResource fieldError = response.getFieldErrors().get(0);
    assertThat(fieldError.getField(), equalTo("email"));
    assertThat(fieldError.getMessage(), equalTo("must not be blank"));
  }

  @Test
  public void should_handle_constraint_violation_exception_with_nested_property_path() {
    ConstraintViolation<?> violation = createMockConstraintViolationWithNestedPath();
    Set<ConstraintViolation<?>> violations = Set.of(violation);
    ConstraintViolationException exception = new ConstraintViolationException("Validation failed", violations);
    
    ErrorResource response = handler.handleConstraintViolation(exception, webRequest);
    
    assertThat(response, notNullValue());
    assertThat(response.getFieldErrors().size(), is(1));
    FieldErrorResource fieldError = response.getFieldErrors().get(0);
    assertThat(fieldError.getField(), equalTo("user.email"));
  }

  @Test
  public void should_handle_constraint_violation_exception_with_single_property() {
    ConstraintViolation<?> violation = createMockConstraintViolationWithSingleProperty();
    Set<ConstraintViolation<?>> violations = Set.of(violation);
    ConstraintViolationException exception = new ConstraintViolationException("Validation failed", violations);
    
    ErrorResource response = handler.handleConstraintViolation(exception, webRequest);
    
    assertThat(response, notNullValue());
    assertThat(response.getFieldErrors().size(), is(1));
    FieldErrorResource fieldError = response.getFieldErrors().get(0);
    assertThat(fieldError.getField(), equalTo("email"));
  }

  @Test
  public void should_handle_constraint_violation_exception_with_multiple_violations() {
    ConstraintViolation<?> violation1 = createMockConstraintViolation();
    ConstraintViolation<?> violation2 = createMockConstraintViolationWithNestedPath();
    Set<ConstraintViolation<?>> violations = Set.of(violation1, violation2);
    ConstraintViolationException exception = new ConstraintViolationException("Validation failed", violations);
    
    ErrorResource response = handler.handleConstraintViolation(exception, webRequest);
    
    assertThat(response, notNullValue());
    assertThat(response.getFieldErrors().size(), is(2));
  }

  @SuppressWarnings("unchecked")
  private ConstraintViolation<?> createMockConstraintViolation() {
    ConstraintViolation<?> violation = mock(ConstraintViolation.class);
    ConstraintDescriptor descriptor = mock(ConstraintDescriptor.class);
    Path propertyPath = mock(Path.class);
    
    when(violation.getRootBeanClass()).thenReturn((Class) String.class);
    when(violation.getPropertyPath()).thenReturn(propertyPath);
    when(propertyPath.toString()).thenReturn("createUser.arg0.email");
    when(violation.getMessage()).thenReturn("must not be blank");
    when(violation.getConstraintDescriptor()).thenReturn(descriptor);
    when(descriptor.getAnnotation()).thenReturn(createMockAnnotation());
    
    return violation;
  }

  @SuppressWarnings("unchecked")
  private ConstraintViolation<?> createMockConstraintViolationWithNestedPath() {
    ConstraintViolation<?> violation = mock(ConstraintViolation.class);
    ConstraintDescriptor descriptor = mock(ConstraintDescriptor.class);
    Path propertyPath = mock(Path.class);
    
    when(violation.getRootBeanClass()).thenReturn((Class) String.class);
    when(violation.getPropertyPath()).thenReturn(propertyPath);
    when(propertyPath.toString()).thenReturn("createUser.arg0.user.email");
    when(violation.getMessage()).thenReturn("must not be blank");
    when(violation.getConstraintDescriptor()).thenReturn(descriptor);
    when(descriptor.getAnnotation()).thenReturn(createMockAnnotation());
    
    return violation;
  }

  @SuppressWarnings("unchecked")
  private ConstraintViolation<?> createMockConstraintViolationWithSingleProperty() {
    ConstraintViolation<?> violation = mock(ConstraintViolation.class);
    ConstraintDescriptor descriptor = mock(ConstraintDescriptor.class);
    Path propertyPath = mock(Path.class);
    
    when(violation.getRootBeanClass()).thenReturn((Class) String.class);
    when(violation.getPropertyPath()).thenReturn(propertyPath);
    when(propertyPath.toString()).thenReturn("email");
    when(violation.getMessage()).thenReturn("must not be blank");
    when(violation.getConstraintDescriptor()).thenReturn(descriptor);
    when(descriptor.getAnnotation()).thenReturn(createMockAnnotation());
    
    return violation;
  }

  private Annotation createMockAnnotation() {
    return new Annotation() {
      @Override
      public Class<? extends Annotation> annotationType() {
        return javax.validation.constraints.NotBlank.class;
      }
    };
  }
}
