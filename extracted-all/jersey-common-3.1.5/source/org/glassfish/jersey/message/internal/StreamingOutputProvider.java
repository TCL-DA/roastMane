package org.glassfish.jersey.message.internal;

import jakarta.inject.Singleton;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.StreamingOutput;
import jakarta.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Produces({"application/octet-stream", "*/*"})
@Singleton
public final class StreamingOutputProvider implements MessageBodyWriter<StreamingOutput> {
   public boolean isWriteable(Class<?> t, Type gt, Annotation[] as, MediaType mediaType) {
      return StreamingOutput.class.isAssignableFrom(t);
   }

   public long getSize(StreamingOutput o, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return -1L;
   }

   public void writeTo(
      StreamingOutput o, Class<?> t, Type gt, Annotation[] as, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entity
   ) throws IOException {
      o.write(entity);
   }
}
