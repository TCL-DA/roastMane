package jakarta.ws.rs.ext;

import jakarta.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface Providers {
   <T> MessageBodyReader<T> getMessageBodyReader(Class<T> var1, Type var2, Annotation[] var3, MediaType var4);

   <T> MessageBodyWriter<T> getMessageBodyWriter(Class<T> var1, Type var2, Annotation[] var3, MediaType var4);

   <T extends Throwable> ExceptionMapper<T> getExceptionMapper(Class<T> var1);

   <T> ContextResolver<T> getContextResolver(Class<T> var1, MediaType var2);
}
