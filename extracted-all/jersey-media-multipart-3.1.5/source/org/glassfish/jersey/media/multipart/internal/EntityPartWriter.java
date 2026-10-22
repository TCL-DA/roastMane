package org.glassfish.jersey.media.multipart.internal;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Produces;
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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.MultiPart;

@Produces({"multipart/form-data"})
@Singleton
public class EntityPartWriter implements MessageBodyWriter<List<EntityPart>> {
   private MultiPartWriter multiPartWriter;
   private final Providers providers;

   @Inject
   public EntityPartWriter(@Context Providers providers) {
      this.providers = providers;
   }

   public boolean isWriteable(Class<?> type, Type generic, Annotation[] annotations, MediaType mediaType) {
      return List.class.isAssignableFrom(type)
         && ParameterizedType.class.isInstance(generic)
         && EntityPart.class.isAssignableFrom((Class<?>)ReflectionHelper.getGenericTypeArgumentClasses(generic).get(0));
   }

   public void writeTo(
      List<EntityPart> entityParts,
      Class<?> type,
      Type genericType,
      Annotation[] annotations,
      MediaType mediaType,
      MultivaluedMap<String, Object> httpHeaders,
      OutputStream entityStream
   ) throws IOException, WebApplicationException {
      MultiPart multiPart = new MultiPart();
      multiPart.setMediaType(mediaType);

      for (EntityPart ep : entityParts) {
         multiPart.bodyPart((BodyPart)ep);
      }

      if (this.multiPartWriter == null) {
         this.multiPartWriter = (MultiPartWriter)this.providers
            .getMessageBodyWriter(MultiPart.class, MultiPart.class, new Annotation[0], MediaType.MULTIPART_FORM_DATA_TYPE);
      }

      this.multiPartWriter.writeTo(multiPart, MultiPart.class, MultiPart.class, annotations, multiPart.getMediaType(), httpHeaders, entityStream);
   }
}
