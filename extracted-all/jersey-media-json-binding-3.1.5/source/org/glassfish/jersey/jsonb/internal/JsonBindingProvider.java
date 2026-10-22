package org.glassfish.jersey.jsonb.internal;

import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.NoContentException;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.Providers;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import org.glassfish.jersey.jsonb.LocalizationMessages;
import org.glassfish.jersey.message.internal.AbstractMessageReaderWriterProvider;
import org.glassfish.jersey.message.internal.EntityInputStream;
import org.glassfish.jersey.message.internal.ReaderWriter;

@Provider
@Produces({"application/json", "text/json", "*/*"})
@Consumes({"application/json", "text/json", "*/*"})
public class JsonBindingProvider extends AbstractMessageReaderWriterProvider<Object> {
   private static final String JSON = "json";
   private static final String PLUS_JSON = "+json";
   private final Providers providers;

   @Inject
   public JsonBindingProvider(@Context Providers providers) {
      this.providers = providers;
   }

   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return supportsMediaType(mediaType);
   }

   public Object readFrom(
      Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream
   ) throws IOException, WebApplicationException {
      EntityInputStream entityInputStream = new EntityInputStream(entityStream);
      InputStream var11 = entityInputStream;
      if (entityInputStream.isEmpty()) {
         throw new NoContentException(LocalizationMessages.ERROR_JSONB_EMPTYSTREAM());
      } else {
         Jsonb jsonb = this.getJsonb(type);

         try {
            return jsonb.fromJson(var11, genericType);
         } catch (JsonbException var10) {
            throw new ProcessingException(LocalizationMessages.ERROR_JSONB_DESERIALIZATION(), var10);
         }
      }
   }

   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return supportsMediaType(mediaType);
   }

   public void writeTo(
      Object o,
      Class<?> type,
      Type genericType,
      Annotation[] annotations,
      MediaType mediaType,
      MultivaluedMap<String, Object> httpHeaders,
      OutputStream entityStream
   ) throws IOException, WebApplicationException {
      Jsonb jsonb = this.getJsonb(type);

      try {
         entityStream.write(jsonb.toJson(o).getBytes(ReaderWriter.getCharset(mediaType)));
         entityStream.flush();
      } catch (IOException var10) {
         throw new ProcessingException(LocalizationMessages.ERROR_JSONB_SERIALIZATION(), var10);
      }
   }

   private Jsonb getJsonb(Class<?> type) {
      ContextResolver<Jsonb> contextResolver = this.providers.getContextResolver(Jsonb.class, MediaType.APPLICATION_JSON_TYPE);
      return contextResolver != null ? (Jsonb)contextResolver.getContext(type) : JsonBindingProvider.JsonbSingleton.INSTANCE.getInstance();
   }

   private static boolean supportsMediaType(MediaType mediaType) {
      return mediaType.getSubtype().equals("json") || mediaType.getSubtype().endsWith("+json");
   }

   private static enum JsonbSingleton {
      INSTANCE;

      private Jsonb jsonbInstance = JsonbBuilder.create();

      Jsonb getInstance() {
         return this.jsonbInstance;
      }
   }
}
