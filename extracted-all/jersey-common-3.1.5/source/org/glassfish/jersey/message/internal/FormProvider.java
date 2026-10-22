package org.glassfish.jersey.message.internal;

import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Encoded;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import org.glassfish.jersey.internal.util.collection.NullableMultivaluedHashMap;

@Produces({"application/x-www-form-urlencoded", "*/*"})
@Consumes({"application/x-www-form-urlencoded", "*/*"})
@Singleton
public final class FormProvider extends AbstractFormProvider<Form> {
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return type == Form.class;
   }

   public Form readFrom(
      Class<Form> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream
   ) throws IOException {
      return new Form(this.readFrom(new NullableMultivaluedHashMap(), mediaType, this.decode(annotations), entityStream));
   }

   private boolean decode(Annotation[] annotations) {
      for (Annotation annotation : annotations) {
         if (annotation.annotationType().equals(Encoded.class)) {
            return false;
         }
      }

      return true;
   }

   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return type == Form.class;
   }

   public void writeTo(
      Form t,
      Class<?> type,
      Type genericType,
      Annotation[] annotations,
      MediaType mediaType,
      MultivaluedMap<String, Object> httpHeaders,
      OutputStream entityStream
   ) throws IOException {
      this.writeTo(t.asMap(), mediaType, entityStream);
   }
}
