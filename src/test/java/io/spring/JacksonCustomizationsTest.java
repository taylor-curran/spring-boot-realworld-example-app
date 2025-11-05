package io.spring;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.spring.JacksonCustomizations.DateTimeSerializer;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

public class JacksonCustomizationsTest {

  @Test
  public void should_serialize_null_datetime() throws Exception {
    DateTimeSerializer serializer = new DateTimeSerializer();
    JsonGenerator gen = Mockito.mock(JsonGenerator.class);
    SerializerProvider provider = Mockito.mock(SerializerProvider.class);
    
    serializer.serialize(null, gen, provider);
    
    verify(gen).writeNull();
  }

  @Test
  public void should_serialize_non_null_datetime() throws Exception {
    DateTimeSerializer serializer = new DateTimeSerializer();
    JsonGenerator gen = Mockito.mock(JsonGenerator.class);
    SerializerProvider provider = Mockito.mock(SerializerProvider.class);
    DateTime dateTime = new DateTime(2024, 1, 1, 0, 0, 0, 0);
    
    serializer.serialize(dateTime, gen, provider);
    
    verify(gen).writeString(anyString());
  }
}
