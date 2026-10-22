package org.glassfish.jersey.client.internal.inject;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverter.Lazy;
import org.glassfish.jersey.internal.inject.UpdaterException;
import org.glassfish.jersey.internal.util.collection.UnsafeValue;
import org.glassfish.jersey.internal.util.collection.Values;

abstract class AbstractParamValueUpdater<T> {
   private final ParamConverter<T> paramConverter;
   private final String parameterName;
   private final String defaultValue;
   private final UnsafeValue<String, RuntimeException> convertedDefaultValue;

   protected AbstractParamValueUpdater(ParamConverter<T> converter, String parameterName, final String defaultValue) {
      this.paramConverter = converter;
      this.parameterName = parameterName;
      this.defaultValue = defaultValue;
      if (defaultValue != null) {
         this.convertedDefaultValue = Values.lazy(new UnsafeValue<String, RuntimeException>() {
            public String get() throws RuntimeException {
               return defaultValue;
            }
         });
         if (!converter.getClass().isAnnotationPresent(Lazy.class)) {
            this.convertedDefaultValue.get();
         }
      } else {
         this.convertedDefaultValue = null;
      }
   }

   public String getName() {
      return this.parameterName;
   }

   public String getDefaultValueString() {
      return this.defaultValue;
   }

   protected final String toString(T value) {
      String result = this.convert(value);
      return result == null ? this.defaultValue() : result;
   }

   private String convert(T value) {
      try {
         return this.paramConverter.toString(value);
      } catch (IllegalArgumentException | WebApplicationException var3) {
         throw var3;
      } catch (Exception var4) {
         throw new UpdaterException(var4);
      }
   }

   protected final boolean isDefaultValueRegistered() {
      return this.defaultValue != null;
   }

   protected final String defaultValue() {
      return !this.isDefaultValueRegistered() ? null : (String)this.convertedDefaultValue.get();
   }
}
