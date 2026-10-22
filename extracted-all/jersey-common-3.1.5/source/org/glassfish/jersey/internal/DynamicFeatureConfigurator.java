package org.glassfish.jersey.internal;

import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.container.DynamicFeature;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.glassfish.jersey.AbstractFeatureConfigurator;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.util.PropertiesHelper;

public class DynamicFeatureConfigurator extends AbstractFeatureConfigurator<DynamicFeature> {
   public DynamicFeatureConfigurator() {
      super(DynamicFeature.class, RuntimeType.SERVER);
   }

   @Override
   public void init(InjectionManager injectionManager, BootstrapBag bootstrapBag) {
      Map<String, Object> properties = bootstrapBag.getConfiguration().getProperties();
      if (PropertiesHelper.isJaxRsServiceLoadingEnabled(properties)) {
         Set<Class<DynamicFeature>> dynamicFeatures = new HashSet<>();
         dynamicFeatures.addAll(this.loadImplementations(properties));
         dynamicFeatures.addAll(this.loadImplementations(properties, DynamicFeature.class.getClassLoader()));
         this.registerFeatures(dynamicFeatures, bootstrapBag);
      }
   }
}
