package org.glassfish.jersey.message.internal;

import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import org.glassfish.jersey.message.MessageUtils;

@Produces({"text/plain", "*/*"})
@Consumes({"text/plain", "*/*"})
@Singleton
public final class ReaderProvider extends AbstractMessageReaderWriterProvider<Reader> {
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return Reader.class == type;
   }

   public Reader readFrom(
      Class<Reader> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream inputStream
   ) throws IOException {
      EntityInputStream entityStream = EntityInputStream.create(inputStream);
      return entityStream.isEmpty()
         ? new BufferedReader(new InputStreamReader(new ByteArrayInputStream(new byte[0]), MessageUtils.getCharset(mediaType)))
         : new BufferedReader(new InputStreamReader(entityStream, ReaderWriter.getCharset(mediaType)));
   }

   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return Reader.class.isAssignableFrom(type);
   }

   public void writeTo(
      Reader t,
      Class<?> type,
      Type genericType,
      Annotation[] annotations,
      MediaType mediaType,
      MultivaluedMap<String, Object> httpHeaders,
      OutputStream entityStream
   ) throws IOException {
      try {
         OutputStreamWriter out = new OutputStreamWriter(entityStream, ReaderWriter.getCharset(mediaType));
         ReaderWriter.writeTo(t, out);
         out.flush();
      } finally {
         t.close();
      }
   }
}
