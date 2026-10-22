package org.glassfish.jersey.media.multipart;

import jakarta.ws.rs.core.MediaType;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;

public class MultiPart extends BodyPart implements Closeable {
   private BodyPartsList bodyParts = new BodyPartsList(this);

   public MultiPart() {
      super(new MediaType("multipart", "mixed"));
   }

   public MultiPart(MediaType mediaType) {
      super(mediaType);
   }

   public List<BodyPart> getBodyParts() {
      return this.bodyParts;
   }

   @Override
   public Object getEntity() {
      throw new IllegalStateException("Cannot get entity on a MultiPart instance");
   }

   @Override
   public void setEntity(Object entity) {
      throw new IllegalStateException("Cannot set entity on a MultiPart instance");
   }

   @Override
   public void setMediaType(MediaType mediaType) {
      if (!"multipart".equals(mediaType.getType())) {
         throw new IllegalArgumentException(mediaType.toString());
      } else {
         super.setMediaType(mediaType);
      }
   }

   public MultiPart bodyPart(BodyPart bodyPart) {
      this.getBodyParts().add(bodyPart);
      return this;
   }

   public MultiPart bodyPart(Object entity, MediaType mediaType) {
      BodyPart bodyPart = new BodyPart(entity, mediaType);
      return this.bodyPart(bodyPart);
   }

   @Override
   public BodyPart entity(Object entity) {
      this.setEntity(entity);
      return this;
   }

   public MultiPart type(MediaType type) {
      this.setMediaType(type);
      return this;
   }

   @Override
   public void cleanup() {
      for (BodyPart bp : this.getBodyParts()) {
         bp.cleanup();
      }
   }

   @Override
   public void close() throws IOException {
      this.cleanup();
   }
}
