package io.spring.graphql.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.netflix.graphql.types.errors.ErrorType;
import com.netflix.graphql.types.errors.TypedGraphQLError;
import graphql.GraphQLError;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import graphql.execution.ResultPath;
import io.spring.api.exception.FieldErrorResource;
import io.spring.api.exception.InvalidAuthenticationException;
import io.spring.graphql.types.Error;
import io.spring.graphql.types.ErrorItem;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GraphQLCustomizeExceptionHandlerEnhancedTest {

  private GraphQLCustomizeExceptionHandler handler;

  @BeforeEach
  public void setUp() {
    handler = new GraphQLCustomizeExceptionHandler();
  }

  @Test
  public void onException_should_handle_invalid_authentication_exception() {
    InvalidAuthenticationException exception = new InvalidAuthenticationException();
    DataFetcherExceptionHandlerParameters parameters = createMockParameters(exception);

    DataFetcherExceptionHandlerResult result = handler.onException(parameters);

    assertThat(result).isNotNull();
    assertThat(result.getErrors()).hasSize(1);
    GraphQLError error = result.getErrors().get(0);
    assertThat(error.getMessage()).isEqualTo("invalid email or password");
    assertThat(error.getExtensions().get("errorType")).isEqualTo("UNAUTHENTICATED");
  }

  @Test
  public void onException_should_handle_constraint_violation_exception() {
    ConstraintViolationException exception = createConstraintViolationException();
    DataFetcherExceptionHandlerParameters parameters = createMockParameters(exception);

    DataFetcherExceptionHandlerResult result = handler.onException(parameters);

    assertThat(result).isNotNull();
    assertThat(result.getErrors()).hasSize(1);
    GraphQLError error = result.getErrors().get(0);
    assertThat(error.getMessage()).isEqualTo("Validation failed");
    assertThat(error.getExtensions()).isNotEmpty();
  }

  @Test
  public void onException_should_delegate_to_default_handler_for_other_exceptions() {
    RuntimeException exception = new RuntimeException("Generic error");
    DataFetcherExceptionHandlerParameters parameters = createMockParameters(exception);

    DataFetcherExceptionHandlerResult result = handler.onException(parameters);

    assertThat(result).isNotNull();
  }

  @Test
  public void getErrorsAsData_should_convert_constraint_violations_to_error_format() {
    ConstraintViolationException exception = createConstraintViolationException();

    Error result = GraphQLCustomizeExceptionHandler.getErrorsAsData(exception);

    assertThat(result).isNotNull();
    assertThat(result.getMessage()).isEqualTo("BAD_REQUEST");
    assertThat(result.getErrors()).isNotEmpty();
    
    ErrorItem errorItem = result.getErrors().get(0);
    assertThat(errorItem.getKey()).isEqualTo("email");
    assertThat(errorItem.getValue()).contains("must be valid");
  }

  @Test
  public void getErrorsAsData_should_group_multiple_violations_by_field() {
    Set<ConstraintViolation<?>> violations = new HashSet<>();
    violations.add(createMockViolation("email", "must be valid"));
    violations.add(createMockViolation("email", "cannot be empty"));
    violations.add(createMockViolation("username", "must be unique"));
    
    ConstraintViolationException exception = new ConstraintViolationException("Multiple validation errors", violations);

    Error result = GraphQLCustomizeExceptionHandler.getErrorsAsData(exception);

    assertThat(result).isNotNull();
    assertThat(result.getErrors()).hasSize(2);
    
    ErrorItem emailError = result.getErrors().stream()
        .filter(item -> "email".equals(item.getKey()))
        .findFirst()
        .orElse(null);
    assertThat(emailError).isNotNull();
    assertThat(emailError.getValue()).hasSize(2);
    assertThat(emailError.getValue()).contains("must be valid", "cannot be empty");
    
    ErrorItem usernameError = result.getErrors().stream()
        .filter(item -> "username".equals(item.getKey()))
        .findFirst()
        .orElse(null);
    assertThat(usernameError).isNotNull();
    assertThat(usernameError.getValue()).hasSize(1);
    assertThat(usernameError.getValue()).contains("must be unique");
  }

  @Test
  public void getErrorsAsData_should_handle_nested_property_paths() {
    Set<ConstraintViolation<?>> violations = new HashSet<>();
    violations.add(createMockViolation("user.profile.email", "must be valid"));
    
    ConstraintViolationException exception = new ConstraintViolationException("Nested validation error", violations);

    Error result = GraphQLCustomizeExceptionHandler.getErrorsAsData(exception);

    assertThat(result).isNotNull();
    assertThat(result.getErrors()).hasSize(1);
    
    ErrorItem errorItem = result.getErrors().get(0);
    assertThat(errorItem.getKey()).isEqualTo("email");
    assertThat(errorItem.getValue()).contains("must be valid");
  }

  @Test
  public void getErrorsAsData_should_handle_single_level_property_paths() {
    Set<ConstraintViolation<?>> violations = new HashSet<>();
    violations.add(createMockViolation("email", "must be valid"));
    
    ConstraintViolationException exception = new ConstraintViolationException("Single level validation error", violations);

    Error result = GraphQLCustomizeExceptionHandler.getErrorsAsData(exception);

    assertThat(result).isNotNull();
    assertThat(result.getErrors()).hasSize(1);
    
    ErrorItem errorItem = result.getErrors().get(0);
    assertThat(errorItem.getKey()).isEqualTo("email");
    assertThat(errorItem.getValue()).contains("must be valid");
  }

  @Test
  public void onException_should_handle_empty_constraint_violations() {
    Set<ConstraintViolation<?>> emptyViolations = new HashSet<>();
    ConstraintViolationException exception = new ConstraintViolationException("No violations", emptyViolations);
    DataFetcherExceptionHandlerParameters parameters = createMockParameters(exception);

    DataFetcherExceptionHandlerResult result = handler.onException(parameters);

    assertThat(result).isNotNull();
    assertThat(result.getErrors()).hasSize(1);
    GraphQLError error = result.getErrors().get(0);
    assertThat(error.getExtensions()).isNotEmpty();
  }

  @Test
  public void onException_should_preserve_execution_path() {
    InvalidAuthenticationException exception = new InvalidAuthenticationException();
    DataFetcherExceptionHandlerParameters parameters = createMockParameters(exception);

    DataFetcherExceptionHandlerResult result = handler.onException(parameters);

    assertThat(result).isNotNull();
    GraphQLError error = result.getErrors().get(0);
    assertThat(error.getPath()).isNotNull();
  }

  private DataFetcherExceptionHandlerParameters createMockParameters(Exception exception) {
    DataFetcherExceptionHandlerParameters parameters = mock(DataFetcherExceptionHandlerParameters.class);
    when(parameters.getException()).thenReturn(exception);
    when(parameters.getPath()).thenReturn(ResultPath.parse("/user/profile"));
    return parameters;
  }

  private ConstraintViolationException createConstraintViolationException() {
    Set<ConstraintViolation<?>> violations = new HashSet<>();
    violations.add(createMockViolation("email", "must be valid"));
    return new ConstraintViolationException("Validation failed", violations);
  }

  private ConstraintViolation<?> createMockViolation(String propertyPath, String message) {
    ConstraintViolation<?> violation = mock(ConstraintViolation.class);
    when(violation.getMessage()).thenReturn(message);
    
    Path path = mock(Path.class);
    when(path.toString()).thenReturn(propertyPath);
    when(violation.getPropertyPath()).thenReturn(path);
    
    when(violation.getRootBeanClass()).thenReturn((Class) Object.class);
    
    ConstraintDescriptor descriptor = mock(ConstraintDescriptor.class);
    Annotation annotation = mock(Annotation.class);
    when(annotation.annotationType()).thenReturn((Class) Override.class);
    when(descriptor.getAnnotation()).thenReturn(annotation);
    when(violation.getConstraintDescriptor()).thenReturn(descriptor);
    
    return violation;
  }
}
