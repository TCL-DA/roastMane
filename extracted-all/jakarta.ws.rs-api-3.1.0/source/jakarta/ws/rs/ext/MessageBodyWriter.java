package jakarta.ws.rs.ext;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface MessageBodyWriter<T> {
   boolean isWriteable(Class<?> var1, Type var2, Annotation[] var3, MediaType var4);

   default long getSize(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return -1L;
   }

   void writeTo(T var1, Class<?> var2, Type var3, Annotation[] var4, MediaType var5, MultivaluedMap<String, Object> var6, OutputStream var7) throws IOException, WebApplicationException;
}
