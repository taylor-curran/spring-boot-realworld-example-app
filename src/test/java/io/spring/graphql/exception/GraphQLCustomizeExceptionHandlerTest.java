package io.spring.graphql.exception;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.netflix.graphql.types.errors.ErrorType;
import com.netflix.graphql.types.errors.TypedGraphQLError;
import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import graphql.execution.ResultPath;
import io.spring.api.exception.InvalidAuthenticationException;
import io.spring.graphql.types.Error;
import io.spring.graphql.types.ErrorItem;
import java.util.Arrays;
import java.util.Collections;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GraphQLCustomizeExceptionHandlerTest {

  private GraphQLCustomizeExceptionHandler handler;

  @Mock private DataFetcherExceptionHandlerParameters handlerParameters;
  @Mock private ResultPath resultPath;

  @BeforeEach
  public void setUp() {
    handler = new GraphQLCustomizeExceptionHandler();
    lenient().when(handlerParameters.getPath()).thenReturn(resultPath);
  }

  @Test
  public void should_get_errors_as_data_from_constraint_violation_exception() {
    ConstraintViolation<?> violation1 = createMockViolation("email", "must not be blank");
    ConstraintViolation<?> violation2 = createMockViolation("username", "must not be null");
    Set<ConstraintViolation<?>> violations = new HashSet<>();
    violations.add(violation1);
    violations.add(violation2);
    ConstraintViolationException exception = new ConstraintViolationException("Validation failed", violations);

    Error result = GraphQLCustomizeExceptionHandler.getErrorsAsData(exception);

    assertThat(result, notNullValue());
    assertThat(result.getMessage(), is("BAD_REQUEST"));
    assertThat(result.getErrors(), notNullValue());
    assertThat(result.getErrors().size(), is(2));
    
    List<ErrorItem> errorItems = result.getErrors();
    boolean hasEmailError = errorItems.stream().anyMatch(item -> "email".equals(item.getKey()));
    boolean hasUsernameError = errorItems.stream().anyMatch(item -> "username".equals(item.getKey()));
    assertThat(hasEmailError, is(true));
    assertThat(hasUsernameError, is(true));
  }

  @Test
  public void should_get_errors_as_data_with_multiple_messages_for_same_field() {
    ConstraintViolation<?> violation1 = createMockViolation("email", "must not be blank");
    ConstraintViolation<?> violation2 = createMockViolation("email", "must be valid email");
    Set<ConstraintViolation<?>> violations = new HashSet<>();
    violations.add(violation1);
    violations.add(violation2);
    ConstraintViolationException exception = new ConstraintViolationException("Validation failed", violations);

    Error result = GraphQLCustomizeExceptionHandler.getErrorsAsData(exception);

    assertThat(result, notNullValue());
    assertThat(result.getErrors().size(), is(1));
    ErrorItem emailError = result.getErrors().get(0);
    assertThat(emailError.getKey(), is("email"));
    assertThat(emailError.getValue().size(), is(2));
  }

  @Test
  public void should_handle_simple_property_path_in_getParam() {
    ConstraintViolation<?> violation = createMockViolation("email", "must not be blank");
    Set<ConstraintViolation<?>> violations = new HashSet<>();
    violations.add(violation);
    ConstraintViolationException exception = new ConstraintViolationException("Validation failed", violations);

    Error result = GraphQLCustomizeExceptionHandler.getErrorsAsData(exception);

    assertThat(result, notNullValue());
    assertThat(result.getErrors().size(), is(1));
    assertThat(result.getErrors().get(0).getKey(), is("email"));
  }

  @Test
  public void should_handle_nested_property_path() {
    ConstraintViolation<?> violation = createMockViolation("createUser.input.email", "must not be blank");
    Set<ConstraintViolation<?>> violations = new HashSet<>();
    violations.add(violation);
    ConstraintViolationException exception = new ConstraintViolationException("Validation failed", violations);

    Error result = GraphQLCustomizeExceptionHandler.getErrorsAsData(exception);

    assertThat(result, notNullValue());
    assertThat(result.getErrors().size(), is(1));
    assertThat(result.getErrors().get(0).getKey(), is("email"));
  }

  @Test
  public void should_handle_empty_constraint_violations() {
    Set<ConstraintViolation<?>> violations = new HashSet<>();
    ConstraintViolationException exception = new ConstraintViolationException("Validation failed", violations);

    Error result = GraphQLCustomizeExceptionHandler.getErrorsAsData(exception);

    assertThat(result, notNullValue());
    assertThat(result.getMessage(), is("BAD_REQUEST"));
    assertThat(result.getErrors(), notNullValue());
    assertThat(result.getErrors().size(), is(0));
  }

  @Test
  public void should_handle_getParam_indirectly_through_getErrorsAsData() {
    ConstraintViolation<?> violation = createMockViolation("createUser.input.email", "must be valid");
    Set<ConstraintViolation<?>> violations = new HashSet<>();
    violations.add(violation);
    ConstraintViolationException exception = new ConstraintViolationException("Validation failed", violations);

    Error result = GraphQLCustomizeExceptionHandler.getErrorsAsData(exception);

    assertThat(result, notNullValue());
    assertThat(result.getErrors().size(), is(1));
    assertThat(result.getErrors().get(0).getKey(), is("email"));
  }

  @Test
  public void should_handle_invalid_authentication_exception_in_onException() {
    InvalidAuthenticationException exception = new InvalidAuthenticationException();
    when(handlerParameters.getException()).thenReturn(exception);

    DataFetcherExceptionHandlerResult result = handler.onException(handlerParameters);

    assertNotNull(result);
    assertEquals(1, result.getErrors().size());
    GraphQLError error = result.getErrors().get(0);
    assertEquals("invalid email or password", error.getMessage());
  }

  @Test
  public void should_handle_constraint_violation_exception_in_onException() {
    ConstraintViolation<?> violation = createMockViolation("email", "must not be blank");
    Set<ConstraintViolation<?>> violations = new HashSet<>();
    violations.add(violation);
    ConstraintViolationException exception = new ConstraintViolationException("Validation failed", violations);
    when(handlerParameters.getException()).thenReturn(exception);

    DataFetcherExceptionHandlerResult result = handler.onException(handlerParameters);

    assertNotNull(result);
    assertEquals(1, result.getErrors().size());
    GraphQLError error = result.getErrors().get(0);
    assertEquals("Validation failed", error.getMessage());
    assertNotNull(error.getExtensions());
  }

  @Test
  public void should_handle_constraint_violation_exception_with_multiple_violations_in_onException() {
    ConstraintViolation<?> violation1 = createMockViolation("email", "must not be blank");
    ConstraintViolation<?> violation2 = createMockViolation("username", "must not be null");
    Set<ConstraintViolation<?>> violations = new HashSet<>();
    violations.add(violation1);
    violations.add(violation2);
    ConstraintViolationException exception = new ConstraintViolationException("Multiple validation errors", violations);
    when(handlerParameters.getException()).thenReturn(exception);

    DataFetcherExceptionHandlerResult result = handler.onException(handlerParameters);

    assertNotNull(result);
    assertEquals(1, result.getErrors().size());
    GraphQLError error = result.getErrors().get(0);
    assertEquals("Multiple validation errors", error.getMessage());
    assertNotNull(error.getExtensions());
    
    Map<String, Object> extensions = error.getExtensions();
    assertTrue(extensions.containsKey("email"));
    assertTrue(extensions.containsKey("username"));
  }

  @Test
  public void should_handle_constraint_violation_exception_with_nested_property_in_onException() {
    ConstraintViolation<?> violation = createMockViolation("createUser.input.email", "must be valid email");
    Set<ConstraintViolation<?>> violations = new HashSet<>();
    violations.add(violation);
    ConstraintViolationException exception = new ConstraintViolationException("Nested validation failed", violations);
    when(handlerParameters.getException()).thenReturn(exception);

    DataFetcherExceptionHandlerResult result = handler.onException(handlerParameters);

    assertNotNull(result);
    assertEquals(1, result.getErrors().size());
    GraphQLError error = result.getErrors().get(0);
    assertNotNull(error.getExtensions());
    
    Map<String, Object> extensions = error.getExtensions();
    assertTrue(extensions.containsKey("email"));
  }

  @Test
  public void should_handle_other_exceptions_with_default_handler() {
    RuntimeException exception = new RuntimeException("Some other error");
    when(handlerParameters.getException()).thenReturn(exception);

    DataFetcherExceptionHandlerResult result = handler.onException(handlerParameters);

    assertNotNull(result);
  }

  @Test
  public void should_handle_null_pointer_exception_with_default_handler() {
    NullPointerException exception = new NullPointerException("Null pointer error");
    when(handlerParameters.getException()).thenReturn(exception);

    DataFetcherExceptionHandlerResult result = handler.onException(handlerParameters);

    assertNotNull(result);
  }

  @Test
  public void should_handle_illegal_argument_exception_with_default_handler() {
    IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");
    when(handlerParameters.getException()).thenReturn(exception);

    DataFetcherExceptionHandlerResult result = handler.onException(handlerParameters);

    assertNotNull(result);
  }

  @Test
  public void should_handle_constraint_violation_exception_with_empty_violations_in_onException() {
    Set<ConstraintViolation<?>> violations = new HashSet<>();
    ConstraintViolationException exception = new ConstraintViolationException("No violations", violations);
    when(handlerParameters.getException()).thenReturn(exception);

    DataFetcherExceptionHandlerResult result = handler.onException(handlerParameters);

    assertNotNull(result);
    assertEquals(1, result.getErrors().size());
    GraphQLError error = result.getErrors().get(0);
    assertEquals("No violations", error.getMessage());
    assertNotNull(error.getExtensions());
  }

  @Test
  public void should_handle_constraint_violation_exception_with_same_field_multiple_errors_in_onException() {
    ConstraintViolation<?> violation1 = createMockViolation("email", "must not be blank");
    ConstraintViolation<?> violation2 = createMockViolation("email", "must be valid email");
    Set<ConstraintViolation<?>> violations = new HashSet<>();
    violations.add(violation1);
    violations.add(violation2);
    ConstraintViolationException exception = new ConstraintViolationException("Multiple email errors", violations);
    when(handlerParameters.getException()).thenReturn(exception);

    DataFetcherExceptionHandlerResult result = handler.onException(handlerParameters);

    assertNotNull(result);
    assertEquals(1, result.getErrors().size());
    GraphQLError error = result.getErrors().get(0);
    assertNotNull(error.getExtensions());
    
    Map<String, Object> extensions = error.getExtensions();
    assertTrue(extensions.containsKey("email"));
    List<String> emailErrors = (List<String>) extensions.get("email");
    assertEquals(2, emailErrors.size());
  }

  @Test
  public void should_handle_invalid_authentication_exception_with_fixed_message() {
    InvalidAuthenticationException exception = new InvalidAuthenticationException();
    when(handlerParameters.getException()).thenReturn(exception);

    DataFetcherExceptionHandlerResult result = handler.onException(handlerParameters);

    assertNotNull(result);
    assertEquals(1, result.getErrors().size());
    GraphQLError error = result.getErrors().get(0);
    assertEquals("invalid email or password", error.getMessage());
  }

  @Test
  public void should_handle_multiple_invalid_authentication_exceptions() {
    InvalidAuthenticationException exception1 = new InvalidAuthenticationException();
    InvalidAuthenticationException exception2 = new InvalidAuthenticationException();
    when(handlerParameters.getException()).thenReturn(exception1);

    DataFetcherExceptionHandlerResult result1 = handler.onException(handlerParameters);
    when(handlerParameters.getException()).thenReturn(exception2);
    DataFetcherExceptionHandlerResult result2 = handler.onException(handlerParameters);

    assertNotNull(result1);
    assertNotNull(result2);
    assertEquals(1, result1.getErrors().size());
    assertEquals(1, result2.getErrors().size());
    
    GraphQLError error1 = result1.getErrors().get(0);
    GraphQLError error2 = result2.getErrors().get(0);
    assertEquals("invalid email or password", error1.getMessage());
    assertEquals("invalid email or password", error2.getMessage());
  }

  @Test
  public void should_handle_constraint_violation_exception_with_complex_property_paths_in_onException() {
    ConstraintViolation<?> violation = createMockViolation("createUser.input.profile.email", "must be valid");
    Set<ConstraintViolation<?>> violations = new HashSet<>();
    violations.add(violation);
    ConstraintViolationException exception = new ConstraintViolationException("Complex path validation", violations);
    when(handlerParameters.getException()).thenReturn(exception);

    DataFetcherExceptionHandlerResult result = handler.onException(handlerParameters);

    assertNotNull(result);
    assertEquals(1, result.getErrors().size());
    GraphQLError error = result.getErrors().get(0);
    assertNotNull(error.getExtensions());
    
    Map<String, Object> extensions = error.getExtensions();
    assertTrue(extensions.containsKey("profile.email"));
  }

  private ConstraintViolation<?> createMockViolation(String propertyPath, String message) {
    ConstraintViolation<?> violation = mock(ConstraintViolation.class);
    Path path = mock(Path.class);
    ConstraintDescriptor descriptor = mock(ConstraintDescriptor.class);
    java.lang.annotation.Annotation annotation = mock(java.lang.annotation.Annotation.class);
    
    when(path.toString()).thenReturn(propertyPath);
    when(violation.getPropertyPath()).thenReturn(path);
    when(violation.getMessage()).thenReturn(message);
    when(violation.getRootBeanClass()).thenReturn((Class) Object.class);
    when(violation.getConstraintDescriptor()).thenReturn(descriptor);
    when(descriptor.getAnnotation()).thenReturn(annotation);
    when(annotation.annotationType()).thenReturn((Class) java.lang.annotation.Annotation.class);
    
    return violation;
  }

}
