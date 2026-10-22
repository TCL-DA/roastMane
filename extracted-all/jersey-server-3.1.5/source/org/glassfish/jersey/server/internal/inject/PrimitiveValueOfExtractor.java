package org.glassfish.jersey.server.internal.inject;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MultivaluedMap;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.glassfish.jersey.internal.inject.ExtractorException;

final class PrimitiveValueOfExtractor implements MultivaluedParameterExtractor<Object> {
   private final Method valueOf;
   private final String parameter;
   private final String defaultStringValue;
   private final Object defaultValue;
   private final Object defaultPrimitiveTypeValue;

   public PrimitiveValueOfExtractor(Method valueOf, String parameter, String defaultStringValue, Object defaultPrimitiveTypeValue) {
      this.valueOf = valueOf;
      this.parameter = parameter;
      this.defaultStringValue = defaultStringValue;
      this.defaultValue = defaultStringValue != null ? this.getValue(defaultStringValue) : null;
      this.defaultPrimitiveTypeValue = defaultPrimitiveTypeValue;
   }

   @Override
   public String getName() {
      return this.parameter;
   }

   @Override
   public String getDefaultValueString() {
      return this.defaultStringValue;
   }

   private Object getValue(String v) {
      try {
         return this.valueOf.invoke(null, v);
      } catch (InvocationTargetException var4) {
         Throwable target = var4.getTargetException();
         if (target instanceof WebApplicationException) {
            throw (WebApplicationException)target;
         } else {
            throw new ExtractorException(target);
         }
      } catch (Exception var5) {
         throw new ProcessingException(var5);
      }
   }

   @Override
   public Object extract(MultivaluedMap<String, String> parameters) {
      String v = (String)parameters.getFirst(this.parameter);
      if (v != null && !v.trim().isEmpty()) {
         return this.getValue(v);
      } else {
         return this.defaultValue != null ? this.defaultValue : this.defaultPrimitiveTypeValue;
      }
   }
}
