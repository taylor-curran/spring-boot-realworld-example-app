package io.spring.graphql.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.netflix.graphql.types.errors.TypedGraphQLError;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import graphql.execution.ResultPath;
import io.spring.api.exception.InvalidAuthenticationException;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GraphQLCustomizeExceptionHandlerTest {

  @InjectMocks private GraphQLCustomizeExceptionHandler exceptionHandler;

  private DataFetcherExceptionHandlerParameters mockParameters;

  @BeforeEach
  public void setUp() {
    mockParameters = mock(DataFetcherExceptionHandlerParameters.class);
    ResultPath mockPath = mock(ResultPath.class);
    when(mockParameters.getPath()).thenReturn(mockPath);
  }

  @Test
  public void should_handle_invalid_authentication_exception() {
    InvalidAuthenticationException exception = new InvalidAuthenticationException();
    when(mockParameters.getException()).thenReturn(exception);

    DataFetcherExceptionHandlerResult result = exceptionHandler.onException(mockParameters);

    assertThat(result.getErrors()).hasSize(1);
    assertThat(result.getErrors().get(0).getMessage()).isEqualTo("invalid email or password");
    assertThat(result.getErrors().get(0)).isInstanceOf(TypedGraphQLError.class);
  }

  @Test
  public void should_handle_constraint_violation_exception() {
    ConstraintViolation<?> violation = mock(ConstraintViolation.class);
    when(violation.getMessage()).thenReturn("Field is required");
    when(violation.getPropertyPath()).thenReturn(mock(javax.validation.Path.class));
    when(violation.getPropertyPath().toString()).thenReturn("field.name");
    when(violation.getRootBeanClass()).thenReturn((Class) String.class);

    javax.validation.metadata.ConstraintDescriptor descriptor =
        mock(javax.validation.metadata.ConstraintDescriptor.class);
    java.lang.annotation.Annotation annotation = mock(java.lang.annotation.Annotation.class);
    when(descriptor.getAnnotation()).thenReturn(annotation);
    when(annotation.annotationType())
        .thenReturn((Class) javax.validation.constraints.NotNull.class);
    when(violation.getConstraintDescriptor()).thenReturn(descriptor);

    Set<ConstraintViolation<?>> violations = Set.of(violation);
    ConstraintViolationException exception =
        new ConstraintViolationException("Validation failed", violations);
    when(mockParameters.getException()).thenReturn(exception);

    DataFetcherExceptionHandlerResult result = exceptionHandler.onException(mockParameters);

    assertThat(result.getErrors()).hasSize(1);
    assertThat(result.getErrors().get(0).getMessage()).isEqualTo("Validation failed");
  }

  @Test
  public void should_delegate_to_default_handler_for_other_exceptions() {
    RuntimeException exception = new RuntimeException("Other error");
    when(mockParameters.getException()).thenReturn(exception);

    DataFetcherExceptionHandlerResult result = exceptionHandler.onException(mockParameters);

    assertThat(result).isNotNull();
  }
}
