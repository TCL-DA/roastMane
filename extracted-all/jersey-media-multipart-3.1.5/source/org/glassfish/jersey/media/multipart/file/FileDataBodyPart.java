package org.glassfish.jersey.media.multipart.file;

import jakarta.ws.rs.core.MediaType;
import java.io.File;
import java.util.Date;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

public class FileDataBodyPart extends FormDataBodyPart {
   private File fileEntity;
   private MediaTypePredictor predictor = DefaultMediaTypePredictor.getInstance();

   public FileDataBodyPart() {
   }

   public FileDataBodyPart(String name, File fileEntity) {
      this(name, fileEntity, null);
   }

   public FileDataBodyPart(String name, File fileEntity, MediaType mediaType) throws IllegalArgumentException {
      super.setName(name);
      if (mediaType != null) {
         this.setFileEntity(fileEntity, mediaType);
      } else {
         this.setFileEntity(fileEntity, this.predictMediaType(fileEntity));
      }
   }

   public File getFileEntity() {
      return this.fileEntity;
   }

   @Override
   public void setValue(MediaType mediaType, Object value) throws UnsupportedOperationException {
      throw new UnsupportedOperationException("It is unsupported, please use setFileEntity instead!");
   }

   @Override
   public void setEntity(Object entity) throws UnsupportedOperationException {
      throw new UnsupportedOperationException("It is unsupported, please use setFileEntity instead!");
   }

   public void setFileEntity(File fileEntity) {
      this.setFileEntity(fileEntity, this.predictMediaType());
   }

   public void setFileEntity(File fileEntity, MediaType mediaType) {
      super.setMediaType(mediaType);
      super.setEntity(fileEntity);
      this.fileEntity = fileEntity;
      if (fileEntity != null) {
         FormDataContentDisposition.FormDataContentDispositionBuilder builder = FormDataContentDisposition.name(this.getName());
         builder.fileName(fileEntity.getName());
         if (fileEntity.exists()) {
            builder.size(fileEntity.length());
            builder.modificationDate(new Date(fileEntity.lastModified()));
         }

         this.setFormDataContentDisposition(builder.build());
      }
   }

   protected MediaType predictMediaType() {
      return this.predictMediaType(this.getFileEntity());
   }

   protected MediaType predictMediaType(File file) {
      return this.getPredictor().getMediaTypeFromFile(file);
   }

   public MediaTypePredictor getPredictor() {
      return this.predictor;
   }

   public void setPredictor(MediaTypePredictor predictor) {
      if (predictor == null) {
         throw new IllegalArgumentException();
      } else {
         this.predictor = predictor;
      }
   }
}
