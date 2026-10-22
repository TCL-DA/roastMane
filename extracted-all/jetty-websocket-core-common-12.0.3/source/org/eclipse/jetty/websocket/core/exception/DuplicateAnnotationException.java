package org.eclipse.jetty.websocket.core.exception;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.eclipse.jetty.websocket.core.util.ReflectUtils;

public class DuplicateAnnotationException extends InvalidWebSocketException {
   public static DuplicateAnnotationException build(Class<?> pojo, Class<? extends Annotation> annoClass, Method... methods) {
      StringBuilder err = new StringBuilder();
      err.append("Duplicate @");
      err.append(annoClass.getSimpleName());
      err.append(" declarations in: ");
      err.append(pojo.getName());

      for (Method method : methods) {
         err.append(System.lineSeparator());
         ReflectUtils.append(err, method);
      }

      return new DuplicateAnnotationException(err.toString());
   }

   public DuplicateAnnotationException(String message) {
      super(message);
   }

   public DuplicateAnnotationException(String message, Throwable cause) {
      super(message, cause);
   }
}
