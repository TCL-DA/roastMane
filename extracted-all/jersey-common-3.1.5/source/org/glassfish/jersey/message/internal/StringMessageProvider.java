package org.glassfish.jersey.message.internal;

import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Produces({"text/plain", "*/*"})
@Consumes({"text/plain", "*/*"})
@Singleton
final class StringMessageProvider extends AbstractMessageReaderWriterProvider<String> {
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return type == String.class;
   }

   public String readFrom(
      Class<String> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream
   ) throws IOException {
      return ReaderWriter.readFromAsString(entityStream, mediaType);
   }

   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return type == String.class;
   }

   public long getSize(String s, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return (long)s.length();
   }

   public void writeTo(
      String t,
      Class<?> type,
      Type genericType,
      Annotation[] annotations,
      MediaType mediaType,
      MultivaluedMap<String, Object> httpHeaders,
      OutputStream entityStream
   ) throws IOException {
      ReaderWriter.writeToAsString(t, entityStream, mediaType);
   }
}
