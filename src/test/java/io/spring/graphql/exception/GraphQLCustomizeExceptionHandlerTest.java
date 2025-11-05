package io.spring.graphql.exception;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.netflix.graphql.dgs.exceptions.DefaultDataFetcherExceptionHandler;
import com.netflix.graphql.types.errors.ErrorType;
import com.netflix.graphql.types.errors.TypedGraphQLError;
import graphql.GraphQLError;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import graphql.execution.ResultPath;
import io.spring.api.exception.InvalidAuthenticationException;
import io.spring.graphql.types.Error;
import io.spring.graphql.types.ErrorItem;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GraphQLCustomizeExceptionHandlerTest {

    private GraphQLCustomizeExceptionHandler exceptionHandler;

    @Mock
    private DataFetcherExceptionHandlerParameters handlerParameters;

    @Mock
    private ConstraintViolation<?> constraintViolation;

    @Mock
    private ConstraintDescriptor<javax.validation.constraints.NotNull> constraintDescriptor;

    @Mock
    private Path propertyPath;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GraphQLCustomizeExceptionHandler();
    }

    @Test
    void shouldHandleInvalidAuthenticationException() {
        InvalidAuthenticationException exception = new InvalidAuthenticationException();
        ResultPath path = ResultPath.parse("/user");
        
        when(handlerParameters.getException()).thenReturn(exception);
        when(handlerParameters.getPath()).thenReturn(path);

        DataFetcherExceptionHandlerResult result = exceptionHandler.onException(handlerParameters);

        assertNotNull(result);
        assertEquals(1, result.getErrors().size());
        
        GraphQLError error = result.getErrors().get(0);
        assertEquals("invalid email or password", error.getMessage());
        assertEquals(path.toList(), error.getPath());
    }

    @Test
    void shouldHandleConstraintViolationException() {
        ConstraintViolationException exception = mock(ConstraintViolationException.class);
        Set<ConstraintViolation<?>> violations = Set.of(constraintViolation);
        ResultPath path = ResultPath.parse("/createUser");
        
        when(handlerParameters.getException()).thenReturn(exception);
        when(handlerParameters.getPath()).thenReturn(path);
        when(exception.getConstraintViolations()).thenReturn(violations);
        when(exception.getMessage()).thenReturn("Validation failed");
        
        when(constraintViolation.getRootBeanClass()).thenReturn((Class) String.class);
        when(constraintViolation.getPropertyPath()).thenReturn(propertyPath);
        when(constraintViolation.getMessage()).thenReturn("Field is required");
        when(constraintViolation.getConstraintDescriptor()).thenReturn((ConstraintDescriptor) constraintDescriptor);
        when(constraintDescriptor.getAnnotation()).thenReturn(mock(javax.validation.constraints.NotNull.class));
        when(constraintDescriptor.getAnnotation().annotationType()).thenReturn((Class) javax.validation.constraints.NotNull.class);
        when(propertyPath.toString()).thenReturn("username");

        DataFetcherExceptionHandlerResult result = exceptionHandler.onException(handlerParameters);

        assertNotNull(result);
        assertEquals(1, result.getErrors().size());
        
        GraphQLError error = result.getErrors().get(0);
        assertEquals("Validation failed", error.getMessage());
        assertEquals(path.toList(), error.getPath());
        assertNotNull(error.getExtensions());
    }

    @Test
    void shouldDelegateToDefaultHandlerForOtherExceptions() {
        RuntimeException exception = new RuntimeException("Unknown error");
        ResultPath path = ResultPath.parse("/someField");
        
        when(handlerParameters.getException()).thenReturn(exception);
        when(handlerParameters.getPath()).thenReturn(path);

        DataFetcherExceptionHandlerResult result = exceptionHandler.onException(handlerParameters);

        assertNotNull(result);
    }

    @Test
    void shouldGetErrorsAsDataFromConstraintViolationException() {
        ConstraintViolationException exception = mock(ConstraintViolationException.class);
        Set<ConstraintViolation<?>> violations = Set.of(constraintViolation);
        
        when(exception.getConstraintViolations()).thenReturn(violations);
        when(constraintViolation.getRootBeanClass()).thenReturn((Class) String.class);
        when(constraintViolation.getPropertyPath()).thenReturn(propertyPath);
        when(constraintViolation.getMessage()).thenReturn("Field is required");
        when(constraintViolation.getConstraintDescriptor()).thenReturn((ConstraintDescriptor) constraintDescriptor);
        when(constraintDescriptor.getAnnotation()).thenReturn(mock(javax.validation.constraints.NotNull.class));
        when(constraintDescriptor.getAnnotation().annotationType()).thenReturn((Class) javax.validation.constraints.NotNull.class);
        when(propertyPath.toString()).thenReturn("username");

        Error error = GraphQLCustomizeExceptionHandler.getErrorsAsData(exception);

        assertNotNull(error);
        assertEquals("BAD_REQUEST", error.getMessage());
        assertNotNull(error.getErrors());
        assertEquals(1, error.getErrors().size());
        
        ErrorItem errorItem = error.getErrors().get(0);
        assertEquals("username", errorItem.getKey());
        assertEquals(List.of("Field is required"), errorItem.getValue());
    }

    @Test
    void shouldHandleMultipleConstraintViolations() {
        ConstraintViolation<?> violation1 = mock(ConstraintViolation.class);
        ConstraintViolation<?> violation2 = mock(ConstraintViolation.class);
        ConstraintViolationException exception = mock(ConstraintViolationException.class);
        Set<ConstraintViolation<?>> violations = Set.of(violation1, violation2);
        
        when(exception.getConstraintViolations()).thenReturn(violations);
        
        Path path1 = mock(Path.class);
        when(violation1.getRootBeanClass()).thenReturn((Class) String.class);
        when(violation1.getPropertyPath()).thenReturn(path1);
        when(violation1.getMessage()).thenReturn("Username is required");
        when(violation1.getConstraintDescriptor()).thenReturn((ConstraintDescriptor) constraintDescriptor);
        when(path1.toString()).thenReturn("username");
        
        Path path2 = mock(Path.class);
        when(violation2.getRootBeanClass()).thenReturn((Class) String.class);
        when(violation2.getPropertyPath()).thenReturn(path2);
        when(violation2.getMessage()).thenReturn("Email is required");
        when(violation2.getConstraintDescriptor()).thenReturn((ConstraintDescriptor) constraintDescriptor);
        when(path2.toString()).thenReturn("email");
        
        when(constraintDescriptor.getAnnotation()).thenReturn(mock(javax.validation.constraints.NotNull.class));
        when(constraintDescriptor.getAnnotation().annotationType()).thenReturn((Class) javax.validation.constraints.NotNull.class);

        Error error = GraphQLCustomizeExceptionHandler.getErrorsAsData(exception);

        assertNotNull(error);
        assertEquals("BAD_REQUEST", error.getMessage());
        assertEquals(2, error.getErrors().size());
    }

    @Test
    void shouldHandleComplexPropertyPath() {
        ConstraintViolationException exception = mock(ConstraintViolationException.class);
        Set<ConstraintViolation<?>> violations = Set.of(constraintViolation);
        
        when(exception.getConstraintViolations()).thenReturn(violations);
        when(constraintViolation.getRootBeanClass()).thenReturn((Class) String.class);
        when(constraintViolation.getPropertyPath()).thenReturn(propertyPath);
        when(constraintViolation.getMessage()).thenReturn("Field is required");
        when(constraintViolation.getConstraintDescriptor()).thenReturn((ConstraintDescriptor) constraintDescriptor);
        when(constraintDescriptor.getAnnotation()).thenReturn(mock(javax.validation.constraints.NotNull.class));
        when(constraintDescriptor.getAnnotation().annotationType()).thenReturn((Class) javax.validation.constraints.NotNull.class);
        when(propertyPath.toString()).thenReturn("createUser.user.username");

        Error error = GraphQLCustomizeExceptionHandler.getErrorsAsData(exception);

        assertNotNull(error);
        assertEquals(1, error.getErrors().size());
        
        ErrorItem errorItem = error.getErrors().get(0);
        assertEquals("username", errorItem.getKey());
    }

    @Test
    void shouldHandleSinglePropertyPath() {
        ConstraintViolationException exception = mock(ConstraintViolationException.class);
        Set<ConstraintViolation<?>> violations = Set.of(constraintViolation);
        
        when(exception.getConstraintViolations()).thenReturn(violations);
        when(constraintViolation.getRootBeanClass()).thenReturn((Class) String.class);
        when(constraintViolation.getPropertyPath()).thenReturn(propertyPath);
        when(constraintViolation.getMessage()).thenReturn("Field is required");
        when(constraintViolation.getConstraintDescriptor()).thenReturn((ConstraintDescriptor) constraintDescriptor);
        when(constraintDescriptor.getAnnotation()).thenReturn(mock(javax.validation.constraints.NotNull.class));
        when(constraintDescriptor.getAnnotation().annotationType()).thenReturn((Class) javax.validation.constraints.NotNull.class);
        when(propertyPath.toString()).thenReturn("username");

        Error error = GraphQLCustomizeExceptionHandler.getErrorsAsData(exception);

        assertNotNull(error);
        assertEquals(1, error.getErrors().size());
        
        ErrorItem errorItem = error.getErrors().get(0);
        assertEquals("username", errorItem.getKey());
    }

    @Test
    void shouldHandleEmptyConstraintViolations() {
        ConstraintViolationException exception = mock(ConstraintViolationException.class);
        Set<ConstraintViolation<?>> emptyViolations = Set.of();
        
        when(exception.getConstraintViolations()).thenReturn(emptyViolations);

        Error error = GraphQLCustomizeExceptionHandler.getErrorsAsData(exception);

        assertNotNull(error);
        assertEquals("BAD_REQUEST", error.getMessage());
        assertTrue(error.getErrors().isEmpty());
    }

    @Test
    void shouldHandleMultipleViolationsForSameField() {
        ConstraintViolation<?> violation1 = mock(ConstraintViolation.class);
        ConstraintViolation<?> violation2 = mock(ConstraintViolation.class);
        ConstraintViolationException exception = mock(ConstraintViolationException.class);
        Set<ConstraintViolation<?>> violations = Set.of(violation1, violation2);
        
        when(exception.getConstraintViolations()).thenReturn(violations);
        
        Path path1 = mock(Path.class);
        Path path2 = mock(Path.class);
        when(violation1.getRootBeanClass()).thenReturn((Class) String.class);
        when(violation1.getPropertyPath()).thenReturn(path1);
        when(violation1.getMessage()).thenReturn("Username is required");
        when(violation1.getConstraintDescriptor()).thenReturn((ConstraintDescriptor) constraintDescriptor);
        when(path1.toString()).thenReturn("username");
        
        when(violation2.getRootBeanClass()).thenReturn((Class) String.class);
        when(violation2.getPropertyPath()).thenReturn(path2);
        when(violation2.getMessage()).thenReturn("Username must be at least 3 characters");
        when(violation2.getConstraintDescriptor()).thenReturn((ConstraintDescriptor) constraintDescriptor);
        when(path2.toString()).thenReturn("username");
        
        when(constraintDescriptor.getAnnotation()).thenReturn(mock(javax.validation.constraints.NotNull.class));
        when(constraintDescriptor.getAnnotation().annotationType()).thenReturn((Class) javax.validation.constraints.NotNull.class);

        Error error = GraphQLCustomizeExceptionHandler.getErrorsAsData(exception);

        assertNotNull(error);
        assertEquals("BAD_REQUEST", error.getMessage());
        assertEquals(1, error.getErrors().size());
        
        ErrorItem errorItem = error.getErrors().get(0);
        assertEquals("username", errorItem.getKey());
        assertEquals(2, errorItem.getValue().size());
        assertTrue(errorItem.getValue().contains("Username is required"));
        assertTrue(errorItem.getValue().contains("Username must be at least 3 characters"));
    }
}
