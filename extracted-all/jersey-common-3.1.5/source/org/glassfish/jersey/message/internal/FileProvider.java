package org.glassfish.jersey.message.internal;

import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Produces({"application/octet-stream", "*/*"})
@Consumes({"application/octet-stream", "*/*"})
@Singleton
public final class FileProvider extends AbstractMessageReaderWriterProvider<File> {
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return File.class == type;
   }

   public File readFrom(
      Class<File> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream
   ) throws IOException {
      File file = Utils.createTempFile();
      Files.copy(entityStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
      return file;
   }

   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return File.class.isAssignableFrom(type);
   }

   public void writeTo(
      File t,
      Class<?> type,
      Type genericType,
      Annotation[] annotations,
      MediaType mediaType,
      MultivaluedMap<String, Object> httpHeaders,
      OutputStream entityStream
   ) throws IOException {
      Files.copy(t.toPath(), entityStream);
   }

   public long getSize(File t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return t.length();
   }
}
