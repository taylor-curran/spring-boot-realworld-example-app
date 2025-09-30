package io.spring.infrastructure.mybatis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import org.apache.ibatis.type.JdbcType;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DateTimeHandlerTest {

  private DateTimeHandler dateTimeHandler;

  @Mock private PreparedStatement preparedStatement;

  @Mock private ResultSet resultSet;

  @Mock private CallableStatement callableStatement;

  @BeforeEach
  public void setUp() {
    dateTimeHandler = new DateTimeHandler();
  }

  @Test
  public void should_set_parameter_with_datetime() throws SQLException {
    DateTime dateTime = new DateTime(2023, 10, 15, 14, 30, 0);

    dateTimeHandler.setParameter(preparedStatement, 1, dateTime, JdbcType.TIMESTAMP);

    verify(preparedStatement).setTimestamp(eq(1), any(Timestamp.class), any(Calendar.class));
  }

  @Test
  public void should_set_parameter_with_null_datetime() throws SQLException {
    dateTimeHandler.setParameter(preparedStatement, 1, null, JdbcType.TIMESTAMP);

    verify(preparedStatement).setTimestamp(eq(1), eq(null), any(Calendar.class));
  }

  @Test
  public void should_get_result_from_resultset_by_column_name() throws SQLException {
    DateTime expectedDateTime = new DateTime(2023, 10, 15, 14, 30, 0);
    Timestamp timestamp = new Timestamp(expectedDateTime.getMillis());
    when(resultSet.getTimestamp(eq("created_at"), any(Calendar.class))).thenReturn(timestamp);

    DateTime result = dateTimeHandler.getResult(resultSet, "created_at");

    assertThat(result).isNotNull();
    assertThat(result.getMillis()).isEqualTo(expectedDateTime.getMillis());
  }

  @Test
  public void should_get_null_result_from_resultset_by_column_name_when_timestamp_is_null()
      throws SQLException {
    when(resultSet.getTimestamp(eq("created_at"), any(Calendar.class))).thenReturn(null);

    DateTime result = dateTimeHandler.getResult(resultSet, "created_at");

    assertThat(result).isNull();
  }

  @Test
  public void should_get_result_from_resultset_by_column_index() throws SQLException {
    DateTime expectedDateTime = new DateTime(2023, 10, 15, 14, 30, 0);
    Timestamp timestamp = new Timestamp(expectedDateTime.getMillis());
    when(resultSet.getTimestamp(eq(1), any(Calendar.class))).thenReturn(timestamp);

    DateTime result = dateTimeHandler.getResult(resultSet, 1);

    assertThat(result).isNotNull();
    assertThat(result.getMillis()).isEqualTo(expectedDateTime.getMillis());
  }

  @Test
  public void should_get_null_result_from_resultset_by_column_index_when_timestamp_is_null()
      throws SQLException {
    when(resultSet.getTimestamp(eq(1), any(Calendar.class))).thenReturn(null);

    DateTime result = dateTimeHandler.getResult(resultSet, 1);

    assertThat(result).isNull();
  }

  @Test
  public void should_get_result_from_callable_statement() throws SQLException {
    DateTime expectedDateTime = new DateTime(2023, 10, 15, 14, 30, 0);
    Timestamp timestamp = new Timestamp(expectedDateTime.getMillis());
    when(callableStatement.getTimestamp(eq(1), any(Calendar.class))).thenReturn(timestamp);

    DateTime result = dateTimeHandler.getResult(callableStatement, 1);

    assertThat(result).isNotNull();
    assertThat(result.getMillis()).isEqualTo(expectedDateTime.getMillis());
  }

  @Test
  public void should_get_null_result_from_callable_statement_when_timestamp_is_null()
      throws SQLException {
    when(callableStatement.getTimestamp(eq(1), any(Calendar.class))).thenReturn(null);

    DateTime result = dateTimeHandler.getResult(callableStatement, 1);

    assertThat(result).isNull();
  }

  @Test
  public void should_handle_sql_exception_in_get_result_by_column_name() throws SQLException {
    SQLException expectedException = new SQLException("Database error");
    when(resultSet.getTimestamp(anyString(), any(Calendar.class))).thenThrow(expectedException);

    try {
      dateTimeHandler.getResult(resultSet, "created_at");
    } catch (SQLException e) {
      assertThat((Throwable) e).isEqualTo(expectedException);
    }
  }

  @Test
  public void should_handle_sql_exception_in_get_result_by_column_index() throws SQLException {
    SQLException expectedException = new SQLException("Database error");
    when(resultSet.getTimestamp(anyInt(), any(Calendar.class))).thenThrow(expectedException);

    try {
      dateTimeHandler.getResult(resultSet, 1);
    } catch (SQLException e) {
      assertThat((Throwable) e).isEqualTo(expectedException);
    }
  }

  @Test
  public void should_handle_sql_exception_in_get_result_from_callable_statement()
      throws SQLException {
    SQLException expectedException = new SQLException("Database error");
    when(callableStatement.getTimestamp(anyInt(), any(Calendar.class)))
        .thenThrow(expectedException);

    try {
      dateTimeHandler.getResult(callableStatement, 1);
    } catch (SQLException e) {
      assertThat((Throwable) e).isEqualTo(expectedException);
    }
  }

  @Test
  public void should_preserve_timezone_information() throws SQLException {
    DateTime utcDateTime = new DateTime(2023, 10, 15, 14, 30, 0);
    Timestamp timestamp = new Timestamp(utcDateTime.getMillis());
    when(resultSet.getTimestamp(eq("created_at"), any(Calendar.class))).thenReturn(timestamp);

    DateTime result = dateTimeHandler.getResult(resultSet, "created_at");

    assertThat(result).isNotNull();
    assertThat(result.getMillis()).isEqualTo(utcDateTime.getMillis());
  }

  @Test
  public void should_handle_edge_case_timestamps() throws SQLException {
    DateTime epochDateTime = new DateTime(0);
    Timestamp epochTimestamp = new Timestamp(0);
    when(resultSet.getTimestamp(eq(1), any(Calendar.class))).thenReturn(epochTimestamp);

    DateTime result = dateTimeHandler.getResult(resultSet, 1);

    assertThat(result).isNotNull();
    assertThat(result.getMillis()).isEqualTo(epochDateTime.getMillis());
  }

  @Test
  public void should_handle_future_timestamps() throws SQLException {
    DateTime futureDateTime = new DateTime(2099, 12, 31, 23, 59, 59);
    Timestamp futureTimestamp = new Timestamp(futureDateTime.getMillis());
    when(callableStatement.getTimestamp(eq(1), any(Calendar.class))).thenReturn(futureTimestamp);

    DateTime result = dateTimeHandler.getResult(callableStatement, 1);

    assertThat(result).isNotNull();
    assertThat(result.getMillis()).isEqualTo(futureDateTime.getMillis());
  }
}
