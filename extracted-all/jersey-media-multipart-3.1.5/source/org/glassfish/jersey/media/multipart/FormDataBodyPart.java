package org.glassfish.jersey.media.multipart;

import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import org.glassfish.jersey.media.multipart.internal.LocalizationMessages;
import org.glassfish.jersey.message.internal.MediaTypes;

public class FormDataBodyPart extends BodyPart implements EntityPart {
   private final boolean fileNameFix;
   protected final AtomicBoolean contentRead = new AtomicBoolean(false);

   public FormDataBodyPart() {
      this(false);
   }

   public FormDataBodyPart(boolean fileNameFix) {
      this.fileNameFix = fileNameFix;
   }

   public FormDataBodyPart(MediaType mediaType) {
      super(mediaType);
      this.fileNameFix = false;
   }

   public FormDataBodyPart(Object entity, MediaType mediaType) {
      super(entity, mediaType);
      this.fileNameFix = false;
   }

   public FormDataBodyPart(String name, String value) {
      super(value, MediaType.TEXT_PLAIN_TYPE);
      this.fileNameFix = false;
      this.setName(name);
   }

   public FormDataBodyPart(String name, Object entity, MediaType mediaType) {
      super(entity, mediaType);
      this.fileNameFix = false;
      this.setName(name);
   }

   public FormDataBodyPart(FormDataContentDisposition formDataContentDisposition, String value) {
      super(value, MediaType.TEXT_PLAIN_TYPE);
      this.fileNameFix = false;
      this.setFormDataContentDisposition(formDataContentDisposition);
   }

   public FormDataBodyPart(FormDataContentDisposition formDataContentDisposition, Object entity, MediaType mediaType) {
      super(entity, mediaType);
      this.fileNameFix = false;
      this.setFormDataContentDisposition(formDataContentDisposition);
   }

   public FormDataContentDisposition getFormDataContentDisposition() {
      return (FormDataContentDisposition)this.getContentDisposition();
   }

   public void setFormDataContentDisposition(FormDataContentDisposition formDataContentDisposition) {
      super.setContentDisposition(formDataContentDisposition);
   }

   @Override
   public ContentDisposition getContentDisposition() {
      if (this.contentDisposition == null) {
         String scd = (String)this.getHeaders().getFirst("Content-Disposition");
         if (scd != null) {
            try {
               this.contentDisposition = new FormDataContentDisposition(scd, this.fileNameFix);
            } catch (ParseException var3) {
               throw new IllegalArgumentException(LocalizationMessages.ERROR_PARSING_CONTENT_DISPOSITION(scd), var3);
            }
         }
      }

      return this.contentDisposition;
   }

   @Override
   public void setContentDisposition(ContentDisposition contentDisposition) {
      if (contentDisposition instanceof FormDataContentDisposition) {
         super.setContentDisposition(contentDisposition);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public String getName() {
      FormDataContentDisposition formDataContentDisposition = this.getFormDataContentDisposition();
      return formDataContentDisposition == null ? null : formDataContentDisposition.getName();
   }

   public Optional<String> getFileName() {
      return Optional.ofNullable(this.getFormDataContentDisposition().getFileName());
   }

   public InputStream getContent() {
      return this.getContent(InputStream.class);
   }

   public <T> T getContent(Class<T> type) {
      if (this.contentRead.compareAndExchange(false, true)) {
         throw new IllegalStateException(LocalizationMessages.CONTENT_HAS_BEEN_ALREADY_READ());
      } else {
         Object entity = this.getEntity();
         return type.isInstance(entity) ? type.cast(entity) : this.getEntityAs(type);
      }
   }

   public <T> T getContent(GenericType<T> type) {
      if (this.contentRead.compareAndExchange(false, true)) {
         throw new IllegalStateException(LocalizationMessages.CONTENT_HAS_BEEN_ALREADY_READ());
      } else {
         Object entity = this.getEntity();
         return (T)(type.getRawType().isInstance(entity) ? entity : this.getEntityAs(type));
      }
   }

   public void setName(String name) {
      if (name == null) {
         throw new IllegalArgumentException(LocalizationMessages.CONTROL_NAME_CANNOT_BE_NULL());
      } else {
         if (this.getFormDataContentDisposition() == null) {
            FormDataContentDisposition contentDisposition = FormDataContentDisposition.name(name).build();
            super.setContentDisposition(contentDisposition);
         } else {
            FormDataContentDisposition formDataContentDisposition = FormDataContentDisposition.name(name)
               .fileName(this.contentDisposition.getFileName())
               .creationDate(this.contentDisposition.getCreationDate())
               .modificationDate(this.contentDisposition.getModificationDate())
               .readDate(this.contentDisposition.getReadDate())
               .size(this.contentDisposition.getSize())
               .build();
            super.setContentDisposition(formDataContentDisposition);
         }
      }
   }

   public String getValue() {
      if (!MediaTypes.typeEqual(MediaType.TEXT_PLAIN_TYPE, this.getMediaType())) {
         throw new IllegalStateException(LocalizationMessages.MEDIA_TYPE_NOT_TEXT_PLAIN());
      } else {
         return this.getEntity() instanceof BodyPartEntity ? this.getValueAs(String.class) : (String)this.getEntity();
      }
   }

   public <T> T getValueAs(Class<T> clazz) {
      return this.getEntityAs(clazz);
   }

   public void setValue(String value) {
      if (!MediaType.TEXT_PLAIN_TYPE.equals(this.getMediaType())) {
         throw new IllegalStateException(LocalizationMessages.MEDIA_TYPE_NOT_TEXT_PLAIN());
      } else {
         this.setEntity(value);
      }
   }

   public void setValue(MediaType mediaType, Object value) {
      this.setMediaType(mediaType);
      this.setEntity(value);
   }

   public boolean isSimple() {
      return MediaType.TEXT_PLAIN_TYPE.equals(this.getMediaType());
   }
}
