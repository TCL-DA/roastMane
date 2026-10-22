package org.glassfish.jersey.media.multipart;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.EntityPart.Builder;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map.Entry;
import org.glassfish.jersey.innate.spi.EntityPartBuilderProvider;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;
import org.glassfish.jersey.media.multipart.internal.LocalizationMessages;

public class JerseyEntityPartBuilderProvider implements EntityPartBuilderProvider {
   public Builder withName(String partName) {
      return new JerseyEntityPartBuilderProvider.EnityPartBuilder().withName(partName);
   }

   private static class EnityPartBuilder implements Builder {
      private String partName;
      private String fileName = null;
      private MultivaluedMap<String, String> headers = new MultivaluedHashMap();
      private MediaType mediaType = null;
      private JerseyEntityPartBuilderProvider.EnityPartBuilder.MethodData methodData;

      private EnityPartBuilder() {
      }

      private Builder withName(String partName) {
         this.partName = partName;
         return this;
      }

      public Builder mediaType(MediaType mediaType) throws IllegalArgumentException {
         this.mediaType = mediaType;
         return this;
      }

      public Builder mediaType(String mediaTypeString) throws IllegalArgumentException {
         this.mediaType = MediaType.valueOf(mediaTypeString);
         return this;
      }

      public Builder header(String headerName, String... headerValues) throws IllegalArgumentException {
         this.headers.addAll(headerName, headerValues);
         return this;
      }

      public Builder headers(MultivaluedMap<String, String> newHeaders) throws IllegalArgumentException {
         for (Entry<String, List<String>> entry : newHeaders.entrySet()) {
            this.header(entry.getKey(), entry.getValue().toArray(new String[0]));
         }

         return this;
      }

      public Builder fileName(String fileName) throws IllegalArgumentException {
         this.fileName = fileName;
         return this;
      }

      public Builder content(InputStream content) throws IllegalArgumentException {
         this.methodData = new JerseyEntityPartBuilderProvider.EnityPartBuilder.InputStreamMethodData(content);
         return this;
      }

      public <T> Builder content(T content, Class<? extends T> type) throws IllegalArgumentException {
         if (File.class.equals(type)) {
            this.methodData = new JerseyEntityPartBuilderProvider.EnityPartBuilder.FileMethodData((File)content);
         } else if (InputStream.class.equals(type)) {
            this.methodData = new JerseyEntityPartBuilderProvider.EnityPartBuilder.InputStreamMethodData((InputStream)content);
         } else {
            this.methodData = new JerseyEntityPartBuilderProvider.EnityPartBuilder.GenericData(content, null);
         }

         return this;
      }

      public <T> Builder content(T content, GenericType<T> type) throws IllegalArgumentException {
         if (File.class.equals(type.getRawType())) {
            this.methodData = new JerseyEntityPartBuilderProvider.EnityPartBuilder.FileMethodData((File)content);
         } else if (InputStream.class.equals(type.getRawType())) {
            this.methodData = new JerseyEntityPartBuilderProvider.EnityPartBuilder.InputStreamMethodData((InputStream)content);
         } else {
            this.methodData = new JerseyEntityPartBuilderProvider.EnityPartBuilder.GenericData(content, type);
         }

         return this;
      }

      public EntityPart build() throws IllegalStateException, IOException, WebApplicationException {
         if (this.methodData == null) {
            throw new IllegalStateException(LocalizationMessages.ENTITY_CONTENT_NOT_SET());
         } else {
            return this.methodData.build();
         }
      }

      private class FileMethodData extends JerseyEntityPartBuilderProvider.EnityPartBuilder.MethodData<File> {
         protected FileMethodData(File content) {
            super(content);
         }

         @Override
         protected FormDataBodyPart build() {
            FileDataBodyPart fileDataBodyPart = new FileDataBodyPart();
            this.fillFormData(fileDataBodyPart);
            if (EnityPartBuilder.this.mediaType != null) {
               fileDataBodyPart.setFileEntity(this.content, EnityPartBuilder.this.mediaType);
            } else {
               fileDataBodyPart.setFileEntity(this.content);
            }

            return fileDataBodyPart;
         }
      }

      private class GenericData extends JerseyEntityPartBuilderProvider.EnityPartBuilder.MethodData<Object> {
         private final GenericType<?> genericEntity;

         protected GenericData(Object content, GenericType<?> genericEntity) {
            super(content);
            this.genericEntity = genericEntity;
         }

         @Override
         protected FormDataBodyPart build() {
            FormDataBodyPart formDataBodyPart = new FormDataBodyPart();
            this.fillFormData(formDataBodyPart);
            if (this.genericEntity != null && !GenericEntity.class.isInstance(this.content)) {
               GenericEntity entity = new GenericEntity(this.content, this.genericEntity.getType());
               formDataBodyPart.setEntity(entity);
            } else {
               formDataBodyPart.setEntity(this.content);
            }

            return formDataBodyPart;
         }
      }

      private class InputStreamMethodData extends JerseyEntityPartBuilderProvider.EnityPartBuilder.MethodData<InputStream> {
         protected InputStreamMethodData(InputStream content) {
            super(content);
         }

         @Override
         protected FormDataBodyPart build() {
            StreamDataBodyPart streamDataBodyPart = new StreamDataBodyPart();
            streamDataBodyPart.setFilename(EnityPartBuilder.this.fileName);
            this.fillFormData(streamDataBodyPart);
            streamDataBodyPart.setStreamEntity(this.content, EnityPartBuilder.this.mediaType);
            return streamDataBodyPart;
         }
      }

      private abstract class MethodData<T> {
         protected final T content;

         protected MethodData(T content) {
            this.content = content;
         }

         protected abstract FormDataBodyPart build();

         protected void fillFormData(FormDataBodyPart bodyPart) {
            FormDataContentDisposition contentDisposition = FormDataContentDisposition.name(EnityPartBuilder.this.partName)
               .fileName(EnityPartBuilder.this.fileName)
               .build();
            bodyPart.setContentDisposition(contentDisposition);
            if (EnityPartBuilder.this.mediaType != null) {
               bodyPart.setMediaType(EnityPartBuilder.this.mediaType);
            }

            for (Entry<String, List<String>> entry : EnityPartBuilder.this.headers.entrySet()) {
               bodyPart.getHeaders().addAll(entry.getKey(), (String[])entry.getValue().toArray(new String[0]));
            }
         }
      }
   }
}
