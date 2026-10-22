package org.glassfish.jersey.media.multipart.file;

import jakarta.ws.rs.core.MediaType;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Optional;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

public class StreamDataBodyPart extends FormDataBodyPart {
   private InputStream streamEntity;
   private String filename;

   public StreamDataBodyPart() {
   }

   public StreamDataBodyPart(String name, InputStream streamEntity) {
      this(name, streamEntity, null, null);
   }

   public StreamDataBodyPart(String name, InputStream streamEntity, String filename) {
      this(name, streamEntity, filename, null);
   }

   public StreamDataBodyPart(String name, InputStream streamEntity, String filename, MediaType mediaType) {
      if (name != null && streamEntity != null) {
         this.setFilename(filename);
         super.setName(name);
         if (mediaType != null) {
            this.setStreamEntity(streamEntity, mediaType);
         } else {
            this.setStreamEntity(streamEntity, getDefaultMediaType());
         }
      } else {
         MessageFormat msg = new MessageFormat("Neither the \"name\" nor \"streamEntity\" can be null. Passed values: \"{0}\" \"{1}\"");
         throw new IllegalArgumentException(msg.format(new Object[]{name, streamEntity}));
      }
   }

   @Override
   public void setValue(MediaType mediaType, Object value) throws UnsupportedOperationException {
      throw new UnsupportedOperationException("It is unsupported, please use setStreamEntity(-) instead!");
   }

   @Override
   public void setValue(String value) {
      throw new UnsupportedOperationException("It is unsupported, please use setStreamEntity(-) instead!");
   }

   @Override
   public void setEntity(Object entity) throws UnsupportedOperationException {
      throw new UnsupportedOperationException("It is unsupported, please use setStreamEntity(-) instead!");
   }

   public void setStreamEntity(InputStream streamEntity) {
      this.setStreamEntity(streamEntity, getDefaultMediaType());
   }

   public void setStreamEntity(InputStream streamEntity, MediaType mediaType) {
      if (streamEntity == null) {
         throw new IllegalArgumentException("Stream body part entity cannot be null.");
      } else {
         if (mediaType == null) {
            mediaType = getDefaultMediaType();
         }

         this.streamEntity = streamEntity;
         super.setMediaType(mediaType);
         super.setEntity(streamEntity);
         this.setFormDataContentDisposition(this.buildContentDisposition());
      }
   }

   protected FormDataContentDisposition buildContentDisposition() {
      FormDataContentDisposition.FormDataContentDispositionBuilder builder = FormDataContentDisposition.name(this.getName());
      if (this.filename != null) {
         builder.fileName(this.filename);
      } else {
         builder.fileName(this.getName());
      }

      return builder.build();
   }

   protected static MediaType getDefaultMediaType() {
      return MediaType.APPLICATION_OCTET_STREAM_TYPE;
   }

   public void setFilename(String filename) {
      this.filename = filename;
   }

   public InputStream getStreamEntity() {
      return this.streamEntity;
   }

   public String getFilename() {
      return this.filename;
   }

   @Override
   public Optional<String> getFileName() {
      return Optional.ofNullable(this.getFilename());
   }
}
