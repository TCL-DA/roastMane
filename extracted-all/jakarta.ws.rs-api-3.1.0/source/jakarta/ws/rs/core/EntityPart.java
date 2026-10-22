package jakarta.ws.rs.core;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.RuntimeDelegate;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public interface EntityPart {
   static EntityPart.Builder withName(String partName) {
      return RuntimeDelegate.getInstance().createEntityPartBuilder(partName);
   }

   static EntityPart.Builder withFileName(String partAndFileName) {
      return RuntimeDelegate.getInstance().createEntityPartBuilder(partAndFileName).fileName(partAndFileName);
   }

   String getName();

   Optional<String> getFileName();

   InputStream getContent();

   <T> T getContent(Class<T> var1) throws IllegalArgumentException, IllegalStateException, IOException, WebApplicationException;

   <T> T getContent(GenericType<T> var1) throws IllegalArgumentException, IllegalStateException, IOException, WebApplicationException;

   MultivaluedMap<String, String> getHeaders();

   MediaType getMediaType();

   public interface Builder {
      EntityPart.Builder mediaType(MediaType var1) throws IllegalArgumentException;

      EntityPart.Builder mediaType(String var1) throws IllegalArgumentException;

      EntityPart.Builder header(String var1, String... var2) throws IllegalArgumentException;

      EntityPart.Builder headers(MultivaluedMap<String, String> var1) throws IllegalArgumentException;

      EntityPart.Builder fileName(String var1) throws IllegalArgumentException;

      EntityPart.Builder content(InputStream var1) throws IllegalArgumentException;

      default EntityPart.Builder content(String fileName, InputStream content) throws IllegalArgumentException {
         return this.fileName(fileName).content(content);
      }

      <T> EntityPart.Builder content(T var1, Class<? extends T> var2) throws IllegalArgumentException;

      default EntityPart.Builder content(Object content) throws IllegalArgumentException {
         return this.content(content, (Class<? extends Object>)content.getClass());
      }

      <T> EntityPart.Builder content(T var1, GenericType<T> var2) throws IllegalArgumentException;

      EntityPart build() throws IllegalStateException, IOException, WebApplicationException;
   }
}
