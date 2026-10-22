package jakarta.ws.rs.ext;

import jakarta.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;

public interface InterceptorContext {
   Object getProperty(String var1);

   default boolean hasProperty(String name) {
      return this.getProperty(name) != null;
   }

   Collection<String> getPropertyNames();

   void setProperty(String var1, Object var2);

   void removeProperty(String var1);

   Annotation[] getAnnotations();

   void setAnnotations(Annotation[] var1);

   Class<?> getType();

   void setType(Class<?> var1);

   Type getGenericType();

   void setGenericType(Type var1);

   MediaType getMediaType();

   void setMediaType(MediaType var1);
}
