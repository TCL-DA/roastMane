package org.glassfish.jersey.internal;

import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.core.Feature;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.glassfish.jersey.AbstractFeatureConfigurator;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.util.PropertiesHelper;

public class FeatureConfigurator extends AbstractFeatureConfigurator<Feature> {
   public FeatureConfigurator(RuntimeType runtimeType) {
      super(Feature.class, runtimeType);
   }

   @Override
   public void init(InjectionManager injectionManager, BootstrapBag bootstrapBag) {
      Map<String, Object> properties = bootstrapBag.getConfiguration().getProperties();
      if (PropertiesHelper.isJaxRsServiceLoadingEnabled(properties)) {
         Set<Class<Feature>> features = new HashSet<>();
         features.addAll(this.loadImplementations(properties));
         features.addAll(this.loadImplementations(properties, Feature.class.getClassLoader()));
         this.registerFeatures(features, bootstrapBag);
      }
   }
}
