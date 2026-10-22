package org.eclipse.jetty.websocket.core.exception;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import org.eclipse.jetty.websocket.core.util.ReflectUtils;

public class InvalidSignatureException extends InvalidWebSocketException {
   public static InvalidSignatureException build(Class<?> pojo, Class<? extends Annotation> methodAnnotationClass, Method method) {
      StringBuilder err = new StringBuilder();
      err.append("Invalid ");
      if (methodAnnotationClass != null) {
         err.append("@");
         err.append(methodAnnotationClass.getSimpleName());
         err.append(' ');
      }

      if (pojo == null) {
         ReflectUtils.append(err, method);
      } else {
         ReflectUtils.append(err, pojo, method);
      }

      return new InvalidSignatureException(err.toString());
   }

   public static InvalidSignatureException build(MethodType expectedType, MethodType actualType) {
      StringBuilder err = new StringBuilder();
      err.append("Invalid MethodHandle ");
      ReflectUtils.append(err, actualType);
      err.append(" - expected ");
      ReflectUtils.append(err, expectedType);
      return new InvalidSignatureException(err.toString());
   }

   public InvalidSignatureException(String message) {
      super(message);
   }

   public InvalidSignatureException(String message, Throwable cause) {
      super(message, cause);
   }
}
