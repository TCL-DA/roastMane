package org.glassfish.jersey.server.internal.inject;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.ParamConverter;
import org.glassfish.jersey.internal.inject.ExtractorException;

final class SingleValueExtractor<T> extends AbstractParamValueExtractor<T> implements MultivaluedParameterExtractor<T> {
   public SingleValueExtractor(ParamConverter<T> converter, String parameterName, String defaultStringValue) {
      super(converter, parameterName, defaultStringValue);
   }

   @Override
   public T extract(MultivaluedMap<String, String> parameters) {
      String value = (String)parameters.getFirst(this.getName());

      try {
         return this.fromString(value == null && this.isDefaultValueRegistered() ? this.getDefaultValueString() : value);
      } catch (ProcessingException | WebApplicationException var4) {
         throw var4;
      } catch (IllegalArgumentException var5) {
         if (value == null) {
            return this.defaultValue();
         } else {
            throw new ExtractorException(var5);
         }
      } catch (Exception var6) {
         throw new ExtractorException(var6);
      }
   }
}
