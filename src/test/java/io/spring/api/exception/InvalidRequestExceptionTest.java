package io.spring.api.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

public class InvalidRequestExceptionTest {

  @Test
  public void should_create_exception_with_errors() {
    Errors errors = mock(Errors.class);

    InvalidRequestException exception = new InvalidRequestException(errors);

    assertThat(exception.getErrors()).isEqualTo(errors);
    assertThat(exception.getMessage()).isEqualTo("");
  }

  @Test
  public void should_handle_null_errors() {
    InvalidRequestException exception = new InvalidRequestException(null);

    assertThat(exception.getErrors()).isNull();
    assertThat(exception.getMessage()).isEqualTo("");
  }

  @Test
  public void should_be_runtime_exception() {
    Errors errors = mock(Errors.class);

    InvalidRequestException exception = new InvalidRequestException(errors);

    assertThat(exception).isInstanceOf(RuntimeException.class);
  }

  @Test
  public void should_preserve_errors_object() {
    Errors errors = mock(Errors.class);
    FieldError fieldError = new FieldError("user", "email", "invalid email");
    when(errors.getFieldErrors()).thenReturn(Arrays.asList(fieldError));

    InvalidRequestException exception = new InvalidRequestException(errors);

    assertThat(exception.getErrors()).isEqualTo(errors);
    assertThat(exception.getErrors().getFieldErrors()).hasSize(1);
    assertThat(exception.getErrors().getFieldErrors().get(0).getField()).isEqualTo("email");
  }

  @Test
  public void should_handle_empty_message() {
    Errors errors = mock(Errors.class);

    InvalidRequestException exception = new InvalidRequestException(errors);

    assertThat(exception.getMessage()).isEmpty();
    assertThat(exception.getMessage()).isEqualTo("");
  }

  @Test
  public void should_be_serializable() {
    Errors errors = mock(Errors.class);

    InvalidRequestException exception = new InvalidRequestException(errors);

    assertThat(exception).hasFieldOrProperty("errors");
  }
}
