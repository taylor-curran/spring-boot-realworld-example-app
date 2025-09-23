package io.spring.api.exception;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

public class InvalidRequestExceptionTest {

  @Test
  public void should_create_exception_with_errors() {
    Errors errors = mock(Errors.class);
    
    InvalidRequestException exception = new InvalidRequestException(errors);
    
    assertThat(exception, notNullValue());
    assertThat(exception.getErrors(), is(errors));
  }

  @Test
  public void should_have_empty_message() {
    Errors errors = mock(Errors.class);
    
    InvalidRequestException exception = new InvalidRequestException(errors);
    
    assertThat(exception.getMessage(), equalTo(""));
  }

  @Test
  public void should_be_runtime_exception() {
    Errors errors = mock(Errors.class);
    
    InvalidRequestException exception = new InvalidRequestException(errors);
    
    assertThat(exception instanceof RuntimeException, is(true));
  }

  @Test
  public void should_store_and_retrieve_errors() {
    Errors errors = mock(Errors.class);
    FieldError fieldError = new FieldError("user", "email", "invalid email");
    when(errors.hasErrors()).thenReturn(true);
    
    InvalidRequestException exception = new InvalidRequestException(errors);
    
    assertThat(exception.getErrors(), is(errors));
    assertThat(exception.getErrors().hasErrors(), is(true));
  }
}
