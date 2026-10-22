package org.glassfish.jersey.server.internal.inject;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverter.Lazy;
import org.glassfish.jersey.internal.inject.ExtractorException;
import org.glassfish.jersey.internal.util.collection.UnsafeValue;
import org.glassfish.jersey.internal.util.collection.Values;

abstract class AbstractParamValueExtractor<T> {
   private final ParamConverter<T> paramConverter;
   private final String parameterName;
   private final String defaultValueString;
   private final UnsafeValue<T, RuntimeException> convertedDefaultValue;

   protected AbstractParamValueExtractor(ParamConverter<T> converter, String parameterName, final String defaultValueString) {
      this.paramConverter = converter;
      this.parameterName = parameterName;
      this.defaultValueString = defaultValueString;
      if (defaultValueString != null) {
         this.convertedDefaultValue = Values.lazy(new UnsafeValue<T, RuntimeException>() {
            public T get() throws RuntimeException {
               return (T)AbstractParamValueExtractor.this.convert(defaultValueString);
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
      return this.defaultValueString;
   }

   protected final T fromString(String value) {
      T result = this.convert(value);
      return result == null ? this.defaultValue() : result;
   }

   private T convert(String value) {
      try {
         return (T)this.paramConverter.fromString(value);
      } catch (WebApplicationException var3) {
         throw var3;
      } catch (IllegalArgumentException var4) {
         throw var4;
      } catch (Exception var5) {
         throw new ExtractorException(var5);
      }
   }

   protected final boolean isDefaultValueRegistered() {
      return this.defaultValueString != null;
   }

   protected final T defaultValue() {
      return (T)(!this.isDefaultValueRegistered() ? null : this.convertedDefaultValue.get());
   }
}
