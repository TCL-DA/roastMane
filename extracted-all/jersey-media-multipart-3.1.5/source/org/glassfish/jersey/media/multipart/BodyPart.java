package org.glassfish.jersey.media.multipart;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Providers;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.ParseException;
import org.glassfish.jersey.internal.util.collection.ImmutableMultivaluedMap;
import org.glassfish.jersey.media.multipart.internal.LocalizationMessages;
import org.glassfish.jersey.message.MessageBodyWorkers;
import org.glassfish.jersey.message.internal.HeaderUtils;
import org.glassfish.jersey.message.internal.ParameterizedHeader;

public class BodyPart {
   protected ContentDisposition contentDisposition = null;
   private Object entity;
   private final MultivaluedMap<String, String> headers = HeaderUtils.createInbound();
   private MediaType mediaType = null;
   public MessageBodyWorkers messageBodyWorkers;
   private MultiPart parent = null;
   private Providers providers = null;

   public BodyPart() {
      this(MediaType.TEXT_PLAIN_TYPE);
   }

   public BodyPart(MediaType mediaType) {
      this.setMediaType(mediaType);
   }

   public BodyPart(Object entity, MediaType mediaType) {
      this.setEntity(entity);
      this.setMediaType(mediaType);
   }

   public Object getEntity() {
      return this.entity;
   }

   public void setEntity(Object entity) {
      this.entity = entity;
   }

   public MultivaluedMap<String, String> getHeaders() {
      return this.headers;
   }

   public MultivaluedMap<String, ParameterizedHeader> getParameterizedHeaders() throws ParseException {
      return new ImmutableMultivaluedMap(new ParameterizedHeadersMap(this.headers));
   }

   public ContentDisposition getContentDisposition() {
      if (this.contentDisposition == null) {
         String scd = (String)this.headers.getFirst("Content-Disposition");
         if (scd != null) {
            try {
               this.contentDisposition = new ContentDisposition(scd);
            } catch (ParseException var3) {
               throw new IllegalArgumentException("Error parsing content disposition: " + scd, var3);
            }
         }
      }

      return this.contentDisposition;
   }

   public void setContentDisposition(ContentDisposition contentDisposition) {
      this.contentDisposition = contentDisposition;
      this.headers.remove("Content-Disposition");
   }

   public MediaType getMediaType() {
      return this.mediaType;
   }

   public void setMediaType(MediaType mediaType) {
      if (mediaType == null) {
         throw new IllegalArgumentException("mediaType cannot be null");
      } else {
         this.mediaType = mediaType;
      }
   }

   public MultiPart getParent() {
      return this.parent;
   }

   public void setParent(MultiPart parent) {
      this.parent = parent;
   }

   public Providers getProviders() {
      return this.providers;
   }

   public void setProviders(Providers providers) {
      this.providers = providers;
   }

   public void cleanup() {
      if (this.getEntity() != null && this.getEntity() instanceof BodyPartEntity) {
         ((BodyPartEntity)this.getEntity()).cleanup();
      }
   }

   public BodyPart entity(Object entity) {
      this.setEntity(entity);
      return this;
   }

   public <T> T getEntityAs(Class<T> clazz) {
      return this.getEntityAs(clazz, clazz);
   }

   <T> T getEntityAs(GenericType<T> genericEntity) {
      return this.getEntityAs(genericEntity.getRawType(), genericEntity.getType());
   }

   <T> T getEntityAs(Class<T> type, Type genericType) {
      if (this.entity == null || !(this.entity instanceof BodyPartEntity)) {
         throw new IllegalStateException(LocalizationMessages.ENTITY_HAS_WRONG_TYPE());
      } else if (type == BodyPartEntity.class) {
         return type.cast(this.entity);
      } else {
         Annotation[] annotations = new Annotation[0];
         MessageBodyReader<T> reader = this.messageBodyWorkers.getMessageBodyReader(type, genericType, annotations, this.mediaType);
         if (reader == null) {
            throw new IllegalArgumentException(LocalizationMessages.NO_AVAILABLE_MBR(type, this.mediaType));
         } else {
            try {
               return (T)reader.readFrom(type, genericType, annotations, this.mediaType, this.headers, ((BodyPartEntity)this.entity).getInputStream());
            } catch (IOException var6) {
               throw new ProcessingException(LocalizationMessages.ERROR_READING_ENTITY(String.class), var6);
            }
         }
      }
   }

   public BodyPart type(MediaType type) {
      this.setMediaType(type);
      return this;
   }

   public BodyPart contentDisposition(ContentDisposition contentDisposition) {
      this.setContentDisposition(contentDisposition);
      return this;
   }

   public void setMessageBodyWorkers(MessageBodyWorkers messageBodyWorkers) {
      this.messageBodyWorkers = messageBodyWorkers;
   }
}
