package org.glassfish.jersey.message.internal;

import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Singleton
@Consumes({"text/plain"})
@Produces({"text/plain"})
final class EnumMessageProvider extends AbstractMessageReaderWriterProvider<Enum> {
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return type.isEnum();
   }

   public Enum readFrom(
      Class<Enum> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream
   ) throws IOException, WebApplicationException {
      String value = ReaderWriter.readFromAsString(entityStream, mediaType);
      return Enum.valueOf(type, value);
   }

   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return type.isEnum();
   }

   public void writeTo(
      Enum anEnum,
      Class<?> type,
      Type genericType,
      Annotation[] annotations,
      MediaType mediaType,
      MultivaluedMap<String, Object> httpHeaders,
      OutputStream entityStream
   ) throws IOException, WebApplicationException {
      ReaderWriter.writeToAsString(anEnum.name(), entityStream, mediaType);
   }
}
