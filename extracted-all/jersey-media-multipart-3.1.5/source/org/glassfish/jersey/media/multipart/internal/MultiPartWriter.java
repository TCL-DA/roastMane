package org.glassfish.jersey.media.multipart.internal;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Providers;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map.Entry;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.Boundary;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.message.MessageUtils;

@Singleton
@Produces({"multipart/*"})
public class MultiPartWriter implements MessageBodyWriter<MultiPart> {
   private static final Annotation[] EMPTY_ANNOTATIONS = new Annotation[0];
   private final Providers providers;

   @Inject
   public MultiPartWriter(@Context Providers providers) {
      this.providers = providers;
   }

   public long getSize(MultiPart entity, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return -1L;
   }

   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return MultiPart.class.isAssignableFrom(type);
   }

   public void writeTo(
      MultiPart entity,
      Class<?> type,
      Type genericType,
      Annotation[] annotations,
      MediaType mediaType,
      MultivaluedMap<String, Object> headers,
      OutputStream stream
   ) throws IOException, WebApplicationException {
      if (entity.getBodyParts() != null && entity.getBodyParts().size() >= 1) {
         if (entity.getParent() == null) {
            Object value = headers.getFirst("MIME-Version");
            if (value == null) {
               headers.putSingle("MIME-Version", "1.0");
            }
         }

         Writer writer = new BufferedWriter(new OutputStreamWriter(stream, MessageUtils.getCharset(mediaType)));
         MediaType boundaryMediaType = Boundary.addBoundary(mediaType);
         if (boundaryMediaType != mediaType) {
            headers.putSingle("Content-Type", boundaryMediaType.toString());
         }

         String boundaryString = (String)boundaryMediaType.getParameters().get("boundary");
         boolean isFirst = true;

         for (BodyPart bodyPart : entity.getBodyParts()) {
            if (isFirst) {
               isFirst = false;
               writer.write("--");
            } else {
               writer.write("\r\n--");
            }

            writer.write(boundaryString);
            writer.write("\r\n");
            MediaType bodyMediaType = bodyPart.getMediaType();
            if (bodyMediaType == null) {
               throw new IllegalArgumentException(LocalizationMessages.MISSING_MEDIA_TYPE_OF_BODY_PART());
            }

            MultivaluedMap<String, String> bodyHeaders = bodyPart.getHeaders();
            bodyHeaders.putSingle("Content-Type", bodyMediaType.toString());
            if (bodyHeaders.getFirst("Content-Disposition") == null && bodyPart.getContentDisposition() != null) {
               bodyHeaders.putSingle("Content-Disposition", bodyPart.getContentDisposition().toString());
            }

            for (Entry<String, List<String>> entry : bodyHeaders.entrySet()) {
               writer.write(entry.getKey());
               writer.write(58);
               boolean first = true;

               for (String value : entry.getValue()) {
                  if (first) {
                     writer.write(32);
                     first = false;
                  } else {
                     writer.write(44);
                  }

                  writer.write(value);
               }

               writer.write("\r\n");
            }

            writer.write("\r\n");
            writer.flush();
            Object bodyEntity = bodyPart.getEntity();
            if (bodyEntity == null) {
               throw new IllegalArgumentException(LocalizationMessages.MISSING_ENTITY_OF_BODY_PART(bodyMediaType));
            }

            Class bodyClass = bodyEntity.getClass();
            if (bodyEntity instanceof BodyPartEntity) {
               bodyClass = InputStream.class;
               bodyEntity = ((BodyPartEntity)bodyEntity).getInputStream();
            }

            Type bodyType = bodyClass;
            if (GenericEntity.class.isInstance(bodyEntity)) {
               bodyClass = ((GenericEntity)bodyEntity).getRawType();
               bodyType = ((GenericEntity)bodyEntity).getType();
               bodyEntity = ((GenericEntity)bodyEntity).getEntity();
            }

            MessageBodyWriter bodyWriter = this.providers.getMessageBodyWriter(bodyClass, bodyType, EMPTY_ANNOTATIONS, bodyMediaType);
            if (bodyWriter == null) {
               throw new IllegalArgumentException(LocalizationMessages.NO_AVAILABLE_MBW(bodyClass, bodyMediaType));
            }

            bodyWriter.writeTo(bodyEntity, bodyClass, bodyClass, EMPTY_ANNOTATIONS, bodyMediaType, bodyHeaders, stream);
         }

         writer.write("\r\n--");
         writer.write(boundaryString);
         writer.write("--\r\n");
         writer.flush();
      } else {
         throw new IllegalArgumentException(LocalizationMessages.MUST_SPECIFY_BODY_PART());
      }
   }
}
