package io.spring.infrastructure.mybatis;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.apache.ibatis.type.JdbcType;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DateTimeHandlerTest {

  @Mock private PreparedStatement preparedStatement;
  @Mock private ResultSet resultSet;
  @Mock private CallableStatement callableStatement;

  private DateTimeHandler dateTimeHandler;
  private DateTime testDateTime;
  private Timestamp testTimestamp;

  @BeforeEach
  public void setUp() {
    dateTimeHandler = new DateTimeHandler();
    testDateTime = new DateTime(2023, 12, 25, 10, 30, 45, DateTimeZone.UTC);
    testTimestamp = new Timestamp(testDateTime.getMillis());
  }

  @Test
  public void should_set_parameter_with_datetime() throws SQLException {
    dateTimeHandler.setParameter(preparedStatement, 1, testDateTime, JdbcType.TIMESTAMP);

    verify(preparedStatement).setTimestamp(eq(1), eq(testTimestamp), any());
  }

  @Test
  public void should_set_parameter_with_null_datetime() throws SQLException {
    dateTimeHandler.setParameter(preparedStatement, 1, null, JdbcType.TIMESTAMP);

    verify(preparedStatement).setTimestamp(eq(1), eq(null), any());
  }

  @Test
  public void should_get_result_by_column_name() throws SQLException {
    when(resultSet.getTimestamp(eq("created_at"), any())).thenReturn(testTimestamp);

    DateTime result = dateTimeHandler.getResult(resultSet, "created_at");

    assertThat(result.getMillis(), is(testDateTime.getMillis()));
    verify(resultSet).getTimestamp(eq("created_at"), any());
  }

  @Test
  public void should_get_result_by_column_name_when_null() throws SQLException {
    when(resultSet.getTimestamp(eq("created_at"), any())).thenReturn(null);

    DateTime result = dateTimeHandler.getResult(resultSet, "created_at");

    assertThat(result, nullValue());
    verify(resultSet).getTimestamp(eq("created_at"), any());
  }

  @Test
  public void should_get_result_by_column_index() throws SQLException {
    when(resultSet.getTimestamp(eq(1), any())).thenReturn(testTimestamp);

    DateTime result = dateTimeHandler.getResult(resultSet, 1);

    assertThat(result.getMillis(), is(testDateTime.getMillis()));
    verify(resultSet).getTimestamp(eq(1), any());
  }

  @Test
  public void should_get_result_by_column_index_when_null() throws SQLException {
    when(resultSet.getTimestamp(eq(1), any())).thenReturn(null);

    DateTime result = dateTimeHandler.getResult(resultSet, 1);

    assertThat(result, nullValue());
    verify(resultSet).getTimestamp(eq(1), any());
  }

  @Test
  public void should_get_result_from_callable_statement() throws SQLException {
    when(callableStatement.getTimestamp(eq(1), any())).thenReturn(testTimestamp);

    DateTime result = dateTimeHandler.getResult(callableStatement, 1);

    assertThat(result.getMillis(), is(testDateTime.getMillis()));
    verify(callableStatement).getTimestamp(eq(1), any());
  }

  @Test
  public void should_get_result_from_callable_statement_when_null() throws SQLException {
    when(callableStatement.getTimestamp(eq(1), any())).thenReturn(null);

    DateTime result = dateTimeHandler.getResult(callableStatement, 1);

    assertThat(result, nullValue());
    verify(callableStatement).getTimestamp(eq(1), any());
  }

  @Test
  public void should_handle_different_timezones_correctly() throws SQLException {
    DateTime utcDateTime = new DateTime(2023, 6, 15, 14, 30, 0, DateTimeZone.UTC);
    Timestamp utcTimestamp = new Timestamp(utcDateTime.getMillis());
    
    when(resultSet.getTimestamp(eq("timestamp_col"), any())).thenReturn(utcTimestamp);

    DateTime result = dateTimeHandler.getResult(resultSet, "timestamp_col");

    assertThat(result.getMillis(), is(utcDateTime.getMillis()));
    assertThat(result.getZone(), is(DateTimeZone.getDefault()));
  }

  @Test
  public void should_preserve_millisecond_precision() throws SQLException {
    DateTime preciseDateTime = new DateTime(2023, 3, 10, 8, 45, 30, 123, DateTimeZone.UTC);
    Timestamp preciseTimestamp = new Timestamp(preciseDateTime.getMillis());
    
    when(resultSet.getTimestamp(eq(2), any())).thenReturn(preciseTimestamp);

    DateTime result = dateTimeHandler.getResult(resultSet, 2);

    assertThat(result.getMillis(), is(preciseDateTime.getMillis()));
  }
}
