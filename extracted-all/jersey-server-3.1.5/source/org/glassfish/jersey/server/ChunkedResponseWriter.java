package org.glassfish.jersey.server;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public final class ChunkedResponseWriter implements MessageBodyWriter<ChunkedOutput<?>> {
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return ChunkedOutput.class.isAssignableFrom(type);
   }

   public long getSize(ChunkedOutput<?> chunkedOutput, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return -1L;
   }

   public void writeTo(
      ChunkedOutput<?> chunkedOutput,
      Class<?> type,
      Type genericType,
      Annotation[] annotations,
      MediaType mediaType,
      MultivaluedMap<String, Object> httpHeaders,
      OutputStream entityStream
   ) throws IOException, WebApplicationException {
   }
}
