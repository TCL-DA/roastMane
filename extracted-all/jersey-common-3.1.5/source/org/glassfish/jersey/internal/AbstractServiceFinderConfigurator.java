package org.glassfish.jersey.internal;

import jakarta.ws.rs.RuntimeType;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.glassfish.jersey.internal.util.PropertiesHelper;

public abstract class AbstractServiceFinderConfigurator<T> implements BootstrapConfigurator {
   private final Class<T> contract;
   private final RuntimeType runtimeType;

   protected AbstractServiceFinderConfigurator(Class<T> contract, RuntimeType runtimeType) {
      this.contract = contract;
      this.runtimeType = runtimeType;
   }

   protected List<Class<T>> loadImplementations(Map<String, Object> applicationProperties) {
      return PropertiesHelper.isMetaInfServicesEnabled(applicationProperties, this.runtimeType)
         ? Stream.of(ServiceFinder.find(this.contract, true).toClassArray()).collect(Collectors.toList())
         : Collections.emptyList();
   }

   protected RuntimeType getRuntimeType() {
      return this.runtimeType;
   }

   protected Class<T> getContract() {
      return this.contract;
   }
}
