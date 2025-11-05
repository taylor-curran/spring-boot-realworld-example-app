package io.spring;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.spring.JacksonCustomizations.DateTimeSerializer;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class JacksonCustomizationsTest {
    @Test
    public void testDateTimeSerializer_withNull() throws IOException {
        DateTimeSerializer serializer = new DateTimeSerializer();
        JsonGenerator gen = mock(JsonGenerator.class);
        SerializerProvider provider = mock(SerializerProvider.class);
        
        serializer.serialize(null, gen, provider);
        
        verify(gen).writeNull();
    }

    @Test
    public void testDateTimeSerializer_withDateTime() throws IOException {
        DateTimeSerializer serializer = new DateTimeSerializer();
        JsonGenerator gen = mock(JsonGenerator.class);
        SerializerProvider provider = mock(SerializerProvider.class);
        DateTime dateTime = new DateTime(2024, 1, 1, 12, 0, 0);
        
        serializer.serialize(dateTime, gen, provider);
        
        verify(gen).writeString(anyString());
    }
}
