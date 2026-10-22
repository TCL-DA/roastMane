package org.glassfish.jersey.message.internal;

import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Produces({"application/octet-stream", "*/*"})
@Consumes({"application/octet-stream", "*/*"})
@Singleton
public final class InputStreamProvider extends AbstractMessageReaderWriterProvider<InputStream> {
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return InputStream.class == type;
   }

   public InputStream readFrom(
      Class<InputStream> type,
      Type genericType,
      Annotation[] annotations,
      MediaType mediaType,
      MultivaluedMap<String, String> httpHeaders,
      InputStream entityStream
   ) throws IOException {
      return ReaderInterceptorExecutor.closeableInputStream(entityStream);
   }

   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return InputStream.class.isAssignableFrom(type);
   }

   public long getSize(InputStream t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return t instanceof ByteArrayInputStream ? (long)((ByteArrayInputStream)t).available() : -1L;
   }

   public void writeTo(
      InputStream t,
      Class<?> type,
      Type genericType,
      Annotation[] annotations,
      MediaType mediaType,
      MultivaluedMap<String, Object> httpHeaders,
      OutputStream entityStream
   ) throws IOException {
      try {
         ReaderWriter.writeTo(t, entityStream);
      } finally {
         t.close();
      }
   }
}
