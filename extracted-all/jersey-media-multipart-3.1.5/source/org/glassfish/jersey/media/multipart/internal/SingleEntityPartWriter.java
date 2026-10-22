package org.glassfish.jersey.media.multipart.internal;

import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Providers;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.MultiPart;

public class SingleEntityPartWriter implements MessageBodyWriter<EntityPart> {
   private MultiPartWriter multiPartWriter;
   private final Providers providers;

   @Inject
   public SingleEntityPartWriter(@Context Providers providers) {
      this.providers = providers;
   }

   public boolean isWriteable(Class<?> type, Type generic, Annotation[] annotations, MediaType mediaType) {
      return EntityPart.class.isAssignableFrom(type);
   }

   public void writeTo(
      EntityPart entityParts,
      Class<?> type,
      Type genericType,
      Annotation[] annotations,
      MediaType mediaType,
      MultivaluedMap<String, Object> httpHeaders,
      OutputStream entityStream
   ) throws IOException, WebApplicationException {
      MultiPart multiPart = new MultiPart();
      multiPart.setMediaType(mediaType);
      multiPart.bodyPart((BodyPart)entityParts);
      if (this.multiPartWriter == null) {
         this.multiPartWriter = (MultiPartWriter)this.providers
            .getMessageBodyWriter(MultiPart.class, MultiPart.class, new Annotation[0], MediaType.MULTIPART_FORM_DATA_TYPE);
      }

      this.multiPartWriter.writeTo(multiPart, MultiPart.class, MultiPart.class, annotations, multiPart.getMediaType(), httpHeaders, entityStream);
   }
}
