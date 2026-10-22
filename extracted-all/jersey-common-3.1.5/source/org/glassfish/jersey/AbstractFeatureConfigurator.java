package org.glassfish.jersey;

import jakarta.ws.rs.RuntimeType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.glassfish.jersey.internal.AbstractServiceFinderConfigurator;
import org.glassfish.jersey.internal.BootstrapBag;
import org.glassfish.jersey.internal.ServiceFinder;
import org.glassfish.jersey.internal.spi.AutoDiscoverable;
import org.glassfish.jersey.internal.util.PropertiesHelper;

public abstract class AbstractFeatureConfigurator<T> extends AbstractServiceFinderConfigurator<T> {
   protected AbstractFeatureConfigurator(Class contract, RuntimeType runtimeType) {
      super(contract, runtimeType);
   }

   protected List<Class<T>> loadImplementations(Map<String, Object> applicationProperties, ClassLoader loader) {
      return PropertiesHelper.isMetaInfServicesEnabled(applicationProperties, this.getRuntimeType())
         ? Stream.of(ServiceFinder.find(this.getContract(), loader, true).toClassArray()).collect(Collectors.toList())
         : Collections.emptyList();
   }

   protected void registerFeatures(Collection<Class<T>> features, BootstrapBag bootstrapBag) {
      List<AutoDiscoverable> autoDiscoverables = new ArrayList<>();
      features.forEach(feature -> autoDiscoverables.add(registerClass((Class<T>)feature)));
      bootstrapBag.getAutoDiscoverables().addAll(autoDiscoverables);
   }

   private static <T> AutoDiscoverable registerClass(Class<T> classToRegister) {
      return context -> {
         if (!context.getConfiguration().isRegistered(classToRegister)) {
            context.register(classToRegister, 2000);
         }
      };
   }
}
