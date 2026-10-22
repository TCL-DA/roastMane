package org.glassfish.jersey.client.internal.inject;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.ParamConverter;
import org.glassfish.jersey.client.inject.ParameterUpdater;
import org.glassfish.jersey.internal.inject.UpdaterException;

final class SingleValueUpdater<T> extends AbstractParamValueUpdater<T> implements ParameterUpdater<T, String> {
   public SingleValueUpdater(ParamConverter<T> converter, String parameterName, String defaultValue) {
      super(converter, parameterName, defaultValue);
   }

   public String update(T value) {
      try {
         return value == null && this.isDefaultValueRegistered() ? this.getDefaultValueString() : this.toString(value);
      } catch (ProcessingException | WebApplicationException var3) {
         throw var3;
      } catch (IllegalArgumentException var4) {
         return this.defaultValue();
      } catch (Exception var5) {
         throw new UpdaterException(var5);
      }
   }
}
