package io.spring;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.spring.JacksonCustomizations.DateTimeSerializer;
import io.spring.JacksonCustomizations.RealWorldModules;
import java.io.IOException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JacksonCustomizationsTest {

  @Mock private JsonGenerator jsonGenerator;
  @Mock private SerializerProvider serializerProvider;

  private DateTimeSerializer dateTimeSerializer;
  private JacksonCustomizations jacksonCustomizations;

  @BeforeEach
  public void setUp() {
    dateTimeSerializer = new DateTimeSerializer();
    jacksonCustomizations = new JacksonCustomizations();
  }

  @Test
  public void should_create_real_world_modules_bean() {
    Module module = jacksonCustomizations.realWorldModules();
    
    assertThat(module, is(org.hamcrest.CoreMatchers.notNullValue()));
    assertThat(module instanceof RealWorldModules, is(true));
  }

  @Test
  public void should_create_real_world_modules_instance() {
    RealWorldModules modules = new RealWorldModules();
    
    assertThat(modules, is(org.hamcrest.CoreMatchers.notNullValue()));
  }

  @Test
  public void should_create_date_time_serializer_instance() {
    DateTimeSerializer serializer = new DateTimeSerializer();
    
    assertThat(serializer, is(org.hamcrest.CoreMatchers.notNullValue()));
  }

  @Test
  public void should_serialize_null_datetime_value() throws IOException {
    dateTimeSerializer.serialize(null, jsonGenerator, serializerProvider);
    
    verify(jsonGenerator).writeNull();
  }

  @Test
  public void should_serialize_non_null_datetime_value() throws IOException {
    DateTime testDateTime = new DateTime(2023, 6, 15, 14, 30, 45, DateTimeZone.UTC);
    String expectedIsoString = "2023-06-15T14:30:45.000Z";
    
    dateTimeSerializer.serialize(testDateTime, jsonGenerator, serializerProvider);
    
    verify(jsonGenerator).writeString(expectedIsoString);
  }

  @Test
  public void should_serialize_datetime_with_different_timezone() throws IOException {
    DateTime testDateTime = new DateTime(2023, 12, 25, 10, 15, 30, DateTimeZone.forID("America/New_York"));
    String expectedIsoString = "2023-12-25T15:15:30.000Z";
    
    dateTimeSerializer.serialize(testDateTime, jsonGenerator, serializerProvider);
    
    verify(jsonGenerator).writeString(expectedIsoString);
  }

  @Test
  public void should_serialize_datetime_at_epoch() throws IOException {
    DateTime epochDateTime = new DateTime(0, DateTimeZone.UTC);
    String expectedIsoString = "1970-01-01T00:00:00.000Z";
    
    dateTimeSerializer.serialize(epochDateTime, jsonGenerator, serializerProvider);
    
    verify(jsonGenerator).writeString(expectedIsoString);
  }

  @Test
  public void should_serialize_datetime_with_milliseconds() throws IOException {
    DateTime testDateTime = new DateTime(2023, 8, 10, 16, 45, 30, 123, DateTimeZone.UTC);
    String expectedIsoString = "2023-08-10T16:45:30.123Z";
    
    dateTimeSerializer.serialize(testDateTime, jsonGenerator, serializerProvider);
    
    verify(jsonGenerator).writeString(expectedIsoString);
  }

  @Test
  public void should_handle_datetime_serialization_edge_cases() throws IOException {
    DateTime futureDateTime = new DateTime(2099, 12, 31, 23, 59, 59, 999, DateTimeZone.UTC);
    String expectedIsoString = "2099-12-31T23:59:59.999Z";
    
    dateTimeSerializer.serialize(futureDateTime, jsonGenerator, serializerProvider);
    
    verify(jsonGenerator).writeString(expectedIsoString);
  }

  @Test
  public void should_serialize_datetime_with_different_zones_to_utc() throws IOException {
    DateTime tokyoDateTime = new DateTime(2023, 7, 4, 12, 0, 0, DateTimeZone.forID("Asia/Tokyo"));
    String expectedIsoString = "2023-07-04T03:00:00.000Z";
    
    dateTimeSerializer.serialize(tokyoDateTime, jsonGenerator, serializerProvider);
    
    verify(jsonGenerator).writeString(expectedIsoString);
  }

  @Test
  public void should_test_jackson_customizations_configuration() {
    JacksonCustomizations config = new JacksonCustomizations();
    
    assertThat(config, is(org.hamcrest.CoreMatchers.notNullValue()));
    
    Module module = config.realWorldModules();
    assertThat(module, is(org.hamcrest.CoreMatchers.notNullValue()));
    assertThat(module instanceof RealWorldModules, is(true));
  }
}
