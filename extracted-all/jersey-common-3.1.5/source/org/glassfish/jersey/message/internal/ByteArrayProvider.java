package org.glassfish.jersey.message.internal;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Produces({"application/octet-stream", "*/*"})
@Consumes({"application/octet-stream", "*/*"})
public final class ByteArrayProvider extends AbstractMessageReaderWriterProvider<byte[]> {
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return type == byte[].class;
   }

   public byte[] readFrom(
      Class<byte[]> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream
   ) throws IOException {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ReaderWriter.writeTo(entityStream, out);
      return out.toByteArray();
   }

   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return type == byte[].class;
   }

   public void writeTo(
      byte[] t,
      Class<?> type,
      Type genericType,
      Annotation[] annotations,
      MediaType mediaType,
      MultivaluedMap<String, Object> httpHeaders,
      OutputStream entityStream
   ) throws IOException {
      entityStream.write(t);
   }

   public long getSize(byte[] t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return (long)t.length;
   }
}
