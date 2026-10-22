package org.glassfish.jersey.model.internal;

import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.FeatureContext;
import java.util.Map;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.InjectionManagerSupplier;

public class FeatureContextWrapper implements FeatureContext, InjectionManagerSupplier {
   private final FeatureContext context;
   private final InjectionManager injectionManager;

   public FeatureContextWrapper(FeatureContext context, InjectionManager injectionManager) {
      this.context = context;
      this.injectionManager = injectionManager;
   }

   public Configuration getConfiguration() {
      return this.context.getConfiguration();
   }

   public FeatureContext property(String name, Object value) {
      return (FeatureContext)this.context.property(name, value);
   }

   public FeatureContext register(Class<?> componentClass) {
      return (FeatureContext)this.context.register(componentClass);
   }

   public FeatureContext register(Class<?> componentClass, int priority) {
      return (FeatureContext)this.context.register(componentClass, priority);
   }

   public FeatureContext register(Class<?> componentClass, Class<?>... contracts) {
      return (FeatureContext)this.context.register(componentClass, contracts);
   }

   public FeatureContext register(Class<?> componentClass, Map<Class<?>, Integer> contracts) {
      return (FeatureContext)this.context.register(componentClass, contracts);
   }

   public FeatureContext register(Object component) {
      return (FeatureContext)this.context.register(component);
   }

   public FeatureContext register(Object component, int priority) {
      return (FeatureContext)this.context.register(component, priority);
   }

   public FeatureContext register(Object component, Class<?>... contracts) {
      return (FeatureContext)this.context.register(component, contracts);
   }

   public FeatureContext register(Object component, Map<Class<?>, Integer> contracts) {
      return (FeatureContext)this.context.register(component, contracts);
   }

   @Override
   public InjectionManager getInjectionManager() {
      return this.injectionManager;
   }
}
