package org.glassfish.jersey.message;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.WriterInterceptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import org.glassfish.jersey.internal.PropertiesDelegate;

public interface MessageBodyWorkers {
   Map<MediaType, List<MessageBodyReader>> getReaders(MediaType var1);

   Map<MediaType, List<MessageBodyWriter>> getWriters(MediaType var1);

   String readersToString(Map<MediaType, List<MessageBodyReader>> var1);

   String writersToString(Map<MediaType, List<MessageBodyWriter>> var1);

   <T> MessageBodyReader<T> getMessageBodyReader(Class<T> var1, Type var2, Annotation[] var3, MediaType var4);

   <T> MessageBodyReader<T> getMessageBodyReader(Class<T> var1, Type var2, Annotation[] var3, MediaType var4, PropertiesDelegate var5);

   <T> MessageBodyWriter<T> getMessageBodyWriter(Class<T> var1, Type var2, Annotation[] var3, MediaType var4);

   <T> MessageBodyWriter<T> getMessageBodyWriter(Class<T> var1, Type var2, Annotation[] var3, MediaType var4, PropertiesDelegate var5);

   List<MediaType> getMessageBodyReaderMediaTypes(Class<?> var1, Type var2, Annotation[] var3);

   List<MediaType> getMessageBodyReaderMediaTypesByType(Class<?> var1);

   List<MessageBodyReader> getMessageBodyReadersForType(Class<?> var1);

   List<ReaderModel> getReaderModelsForType(Class<?> var1);

   List<MediaType> getMessageBodyWriterMediaTypes(Class<?> var1, Type var2, Annotation[] var3);

   List<MediaType> getMessageBodyWriterMediaTypesByType(Class<?> var1);

   List<MessageBodyWriter> getMessageBodyWritersForType(Class<?> var1);

   List<WriterModel> getWritersModelsForType(Class<?> var1);

   MediaType getMessageBodyWriterMediaType(Class<?> var1, Type var2, Annotation[] var3, List<MediaType> var4);

   Object readFrom(
      Class<?> var1,
      Type var2,
      Annotation[] var3,
      MediaType var4,
      MultivaluedMap<String, String> var5,
      PropertiesDelegate var6,
      InputStream var7,
      Iterable<ReaderInterceptor> var8,
      boolean var9
   ) throws WebApplicationException, IOException;

   OutputStream writeTo(
      Object var1,
      Class<?> var2,
      Type var3,
      Annotation[] var4,
      MediaType var5,
      MultivaluedMap<String, Object> var6,
      PropertiesDelegate var7,
      OutputStream var8,
      Iterable<WriterInterceptor> var9
   ) throws IOException, WebApplicationException;
}
