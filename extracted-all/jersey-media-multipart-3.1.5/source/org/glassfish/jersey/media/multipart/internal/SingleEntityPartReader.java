package org.glassfish.jersey.media.multipart.internal;

import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Providers;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.JerseyEntityPartBuilderProvider;
import org.glassfish.jersey.media.multipart.MultiPart;

public class SingleEntityPartReader implements MessageBodyReader<EntityPart> {
   private MultiPartReaderClientSide multiPartReaderClientSide;
   private final Providers providers;

   @Inject
   public SingleEntityPartReader(@Context Providers providers) {
      this.providers = providers;
   }

   public boolean isReadable(Class<?> type, Type generic, Annotation[] annotations, MediaType mediaType) {
      return EntityPart.class.isAssignableFrom(type);
   }

   public EntityPart readFrom(
      Class<EntityPart> type,
      Type genericType,
      Annotation[] annotations,
      MediaType mediaType,
      MultivaluedMap<String, String> httpHeaders,
      InputStream entityStream
   ) throws IOException, WebApplicationException {
      if (this.multiPartReaderClientSide == null) {
         this.multiPartReaderClientSide = (MultiPartReaderClientSide)this.providers
            .getMessageBodyReader(MultiPart.class, MultiPart.class, new Annotation[0], MediaType.MULTIPART_FORM_DATA_TYPE);
      }

      MultiPart multiPart = this.multiPartReaderClientSide.readFrom(MultiPart.class, MultiPart.class, annotations, mediaType, httpHeaders, entityStream);
      List<BodyPart> bodyParts = multiPart.getBodyParts();
      List<EntityPart> entityParts = new LinkedList<>();

      for (BodyPart bp : bodyParts) {
         if (FormDataBodyPart.class.isInstance(bp)) {
            entityParts.add((EntityPart)bp);
         } else {
            EntityPart ep = new JerseyEntityPartBuilderProvider()
               .withName("")
               .mediaType(bp.getMediaType())
               .content(bp.getEntity())
               .headers(bp.getHeaders())
               .build();
            entityParts.add(ep);
         }
      }

      return entityParts.get(0);
   }
}
