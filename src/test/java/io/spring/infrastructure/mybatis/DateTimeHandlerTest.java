package io.spring.infrastructure.mybatis;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;
import org.apache.ibatis.type.JdbcType;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DateTimeHandlerTest {

    private DateTimeHandler dateTimeHandler;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Mock
    private CallableStatement callableStatement;

    @BeforeEach
    void setUp() {
        dateTimeHandler = new DateTimeHandler();
    }

    @Test
    void shouldSetParameterWithNonNullDateTime() throws SQLException {
        DateTime dateTime = new DateTime(2023, 12, 25, 10, 30, 0, DateTimeZone.UTC);
        Timestamp expectedTimestamp = new Timestamp(dateTime.getMillis());

        dateTimeHandler.setParameter(preparedStatement, 1, dateTime, JdbcType.TIMESTAMP);

        verify(preparedStatement).setTimestamp(eq(1), eq(expectedTimestamp), any(Calendar.class));
    }

    @Test
    void shouldSetParameterWithNullDateTime() throws SQLException {
        dateTimeHandler.setParameter(preparedStatement, 1, null, JdbcType.TIMESTAMP);

        verify(preparedStatement).setTimestamp(eq(1), isNull(), any(Calendar.class));
    }

    @Test
    void shouldGetResultFromResultSetByColumnName() throws SQLException {
        String columnName = "created_at";
        DateTime expectedDateTime = new DateTime(2023, 12, 25, 10, 30, 0, DateTimeZone.UTC);
        Timestamp timestamp = new Timestamp(expectedDateTime.getMillis());

        when(resultSet.getTimestamp(eq(columnName), any(Calendar.class))).thenReturn(timestamp);

        DateTime result = dateTimeHandler.getResult(resultSet, columnName);

        assertNotNull(result);
        assertEquals(expectedDateTime.getMillis(), result.getMillis());
        verify(resultSet).getTimestamp(eq(columnName), any(Calendar.class));
    }

    @Test
    void shouldGetNullResultFromResultSetByColumnNameWhenTimestampIsNull() throws SQLException {
        String columnName = "created_at";

        when(resultSet.getTimestamp(eq(columnName), any(Calendar.class))).thenReturn(null);

        DateTime result = dateTimeHandler.getResult(resultSet, columnName);

        assertNull(result);
        verify(resultSet).getTimestamp(eq(columnName), any(Calendar.class));
    }

    @Test
    void shouldGetResultFromResultSetByColumnIndex() throws SQLException {
        int columnIndex = 1;
        DateTime expectedDateTime = new DateTime(2023, 12, 25, 10, 30, 0, DateTimeZone.UTC);
        Timestamp timestamp = new Timestamp(expectedDateTime.getMillis());

        when(resultSet.getTimestamp(eq(columnIndex), any(Calendar.class))).thenReturn(timestamp);

        DateTime result = dateTimeHandler.getResult(resultSet, columnIndex);

        assertNotNull(result);
        assertEquals(expectedDateTime.getMillis(), result.getMillis());
        verify(resultSet).getTimestamp(eq(columnIndex), any(Calendar.class));
    }

    @Test
    void shouldGetNullResultFromResultSetByColumnIndexWhenTimestampIsNull() throws SQLException {
        int columnIndex = 1;

        when(resultSet.getTimestamp(eq(columnIndex), any(Calendar.class))).thenReturn(null);

        DateTime result = dateTimeHandler.getResult(resultSet, columnIndex);

        assertNull(result);
        verify(resultSet).getTimestamp(eq(columnIndex), any(Calendar.class));
    }

    @Test
    void shouldGetResultFromCallableStatement() throws SQLException {
        int columnIndex = 1;
        DateTime expectedDateTime = new DateTime(2023, 12, 25, 10, 30, 0, DateTimeZone.UTC);
        Timestamp timestamp = new Timestamp(expectedDateTime.getMillis());

        when(callableStatement.getTimestamp(eq(columnIndex), any(Calendar.class))).thenReturn(timestamp);

        DateTime result = dateTimeHandler.getResult(callableStatement, columnIndex);

        assertNotNull(result);
        assertEquals(expectedDateTime.getMillis(), result.getMillis());
        verify(callableStatement).getTimestamp(eq(columnIndex), any(Calendar.class));
    }

    @Test
    void shouldGetNullResultFromCallableStatementWhenTimestampIsNull() throws SQLException {
        int columnIndex = 1;

        when(callableStatement.getTimestamp(eq(columnIndex), any(Calendar.class))).thenReturn(null);

        DateTime result = dateTimeHandler.getResult(callableStatement, columnIndex);

        assertNull(result);
        verify(callableStatement).getTimestamp(eq(columnIndex), any(Calendar.class));
    }

    @Test
    void shouldHandleSQLExceptionInSetParameter() throws SQLException {
        DateTime dateTime = new DateTime(2023, 12, 25, 10, 30, 0, DateTimeZone.UTC);
        SQLException expectedException = new SQLException("Database connection failed");

        doThrow(expectedException).when(preparedStatement).setTimestamp(anyInt(), any(Timestamp.class), any(Calendar.class));

        SQLException thrownException = assertThrows(SQLException.class, () -> {
            dateTimeHandler.setParameter(preparedStatement, 1, dateTime, JdbcType.TIMESTAMP);
        });

        assertEquals(expectedException, thrownException);
    }

    @Test
    void shouldHandleSQLExceptionInGetResultFromResultSetByColumnName() throws SQLException {
        String columnName = "created_at";
        SQLException expectedException = new SQLException("Column not found");

        when(resultSet.getTimestamp(eq(columnName), any(Calendar.class))).thenThrow(expectedException);

        SQLException thrownException = assertThrows(SQLException.class, () -> {
            dateTimeHandler.getResult(resultSet, columnName);
        });

        assertEquals(expectedException, thrownException);
    }

    @Test
    void shouldHandleSQLExceptionInGetResultFromResultSetByColumnIndex() throws SQLException {
        int columnIndex = 1;
        SQLException expectedException = new SQLException("Invalid column index");

        when(resultSet.getTimestamp(eq(columnIndex), any(Calendar.class))).thenThrow(expectedException);

        SQLException thrownException = assertThrows(SQLException.class, () -> {
            dateTimeHandler.getResult(resultSet, columnIndex);
        });

        assertEquals(expectedException, thrownException);
    }

    @Test
    void shouldHandleSQLExceptionInGetResultFromCallableStatement() throws SQLException {
        int columnIndex = 1;
        SQLException expectedException = new SQLException("Callable statement error");

        when(callableStatement.getTimestamp(eq(columnIndex), any(Calendar.class))).thenThrow(expectedException);

        SQLException thrownException = assertThrows(SQLException.class, () -> {
            dateTimeHandler.getResult(callableStatement, columnIndex);
        });

        assertEquals(expectedException, thrownException);
    }

    @Test
    void shouldUseUTCCalendarForAllOperations() throws SQLException {
        DateTime dateTime = new DateTime(2023, 12, 25, 10, 30, 0, DateTimeZone.UTC);
        String columnName = "created_at";
        int columnIndex = 1;
        Timestamp timestamp = new Timestamp(dateTime.getMillis());

        dateTimeHandler.setParameter(preparedStatement, 1, dateTime, JdbcType.TIMESTAMP);
        verify(preparedStatement).setTimestamp(eq(1), any(Timestamp.class), argThat(calendar -> 
            calendar.getTimeZone().equals(TimeZone.getTimeZone("UTC"))));

        when(resultSet.getTimestamp(eq(columnName), any(Calendar.class))).thenReturn(timestamp);
        dateTimeHandler.getResult(resultSet, columnName);
        verify(resultSet).getTimestamp(eq(columnName), argThat(calendar -> 
            calendar.getTimeZone().equals(TimeZone.getTimeZone("UTC"))));

        when(resultSet.getTimestamp(eq(columnIndex), any(Calendar.class))).thenReturn(timestamp);
        dateTimeHandler.getResult(resultSet, columnIndex);
        verify(resultSet).getTimestamp(eq(columnIndex), argThat(calendar -> 
            calendar.getTimeZone().equals(TimeZone.getTimeZone("UTC"))));

        when(callableStatement.getTimestamp(eq(columnIndex), any(Calendar.class))).thenReturn(timestamp);
        dateTimeHandler.getResult(callableStatement, columnIndex);
        verify(callableStatement).getTimestamp(eq(columnIndex), argThat(calendar -> 
            calendar.getTimeZone().equals(TimeZone.getTimeZone("UTC"))));
    }

    @Test
    void shouldPreserveMillisecondsInConversion() throws SQLException {
        long expectedMillis = 1703505000123L; // Specific timestamp with milliseconds
        DateTime dateTime = new DateTime(expectedMillis, DateTimeZone.UTC);
        Timestamp timestamp = new Timestamp(expectedMillis);

        dateTimeHandler.setParameter(preparedStatement, 1, dateTime, JdbcType.TIMESTAMP);
        verify(preparedStatement).setTimestamp(eq(1), argThat(ts -> ts.getTime() == expectedMillis), any(Calendar.class));

        when(resultSet.getTimestamp(eq(1), any(Calendar.class))).thenReturn(timestamp);
        DateTime result = dateTimeHandler.getResult(resultSet, 1);
        assertEquals(expectedMillis, result.getMillis());
    }
}
