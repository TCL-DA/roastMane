package org.glassfish.jersey.internal;

import jakarta.ws.rs.RuntimeType;
import java.util.Map;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.internal.util.PropertiesHelper;

public class ServiceFinderBinder<T> extends AbstractBinder {
   private final Class<T> contract;
   private final Map<String, Object> applicationProperties;
   private final RuntimeType runtimeType;

   public ServiceFinderBinder(Class<T> contract, Map<String, Object> applicationProperties, RuntimeType runtimeType) {
      this.contract = contract;
      this.applicationProperties = applicationProperties;
      this.runtimeType = runtimeType;
   }

   @Override
   protected void configure() {
      if (PropertiesHelper.isMetaInfServicesEnabled(this.applicationProperties, this.runtimeType)) {
         for (Class<T> t : ServiceFinder.find(this.contract, true).toClassArray()) {
            this.bind(t).to(this.contract);
         }
      }
   }
}
