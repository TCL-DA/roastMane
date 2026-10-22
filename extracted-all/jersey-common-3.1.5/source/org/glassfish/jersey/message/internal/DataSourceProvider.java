package org.glassfish.jersey.message.internal;

import jakarta.activation.DataSource;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Produces({"application/octet-stream", "*/*"})
@Consumes({"application/octet-stream", "*/*"})
public class DataSourceProvider extends AbstractMessageReaderWriterProvider<DataSource> {
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return DataSource.class == type;
   }

   public DataSource readFrom(
      Class<DataSource> type,
      Type genericType,
      Annotation[] annotations,
      MediaType mediaType,
      MultivaluedMap<String, String> httpHeaders,
      InputStream entityStream
   ) throws IOException {
      return new DataSourceProvider.ByteArrayDataSource(entityStream, mediaType == null ? null : mediaType.toString());
   }

   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return DataSource.class.isAssignableFrom(type);
   }

   public void writeTo(
      DataSource t,
      Class<?> type,
      Type genericType,
      Annotation[] annotations,
      MediaType mediaType,
      MultivaluedMap<String, Object> httpHeaders,
      OutputStream entityStream
   ) throws IOException {
      InputStream in = t.getInputStream();

      try {
         ReaderWriter.writeTo(in, entityStream);
      } finally {
         in.close();
      }
   }

   public static class ByteArrayDataSource implements DataSource {
      private final String type;
      private byte[] data;
      private int len = -1;
      private String name = "";

      public ByteArrayDataSource(InputStream is, String type) throws IOException {
         DataSourceProvider.ByteArrayDataSource.DSByteArrayOutputStream os = new DataSourceProvider.ByteArrayDataSource.DSByteArrayOutputStream();
         ReaderWriter.writeTo(is, os);
         this.data = os.getBuf();
         this.len = os.getCount();
         if (this.data.length - this.len > 262144) {
            this.data = os.toByteArray();
            this.len = this.data.length;
         }

         this.type = type;
      }

      public InputStream getInputStream() throws IOException {
         if (this.data == null) {
            throw new IOException("no data");
         } else {
            if (this.len < 0) {
               this.len = this.data.length;
            }

            return new ByteArrayInputStream(this.data, 0, this.len);
         }
      }

      public OutputStream getOutputStream() throws IOException {
         throw new IOException("cannot do this");
      }

      public String getContentType() {
         return this.type;
      }

      public String getName() {
         return this.name;
      }

      public void setName(String name) {
         this.name = name;
      }

      static class DSByteArrayOutputStream extends ByteArrayOutputStream {
         public byte[] getBuf() {
            return this.buf;
         }

         public int getCount() {
            return this.count;
         }
      }
   }
}
