package org.glassfish.jersey.server.wadl;

import jakarta.inject.Singleton;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;
import java.util.logging.Logger;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.internal.inject.ClassBinding;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.wadl.internal.WadlApplicationContextImpl;
import org.glassfish.jersey.server.wadl.processor.WadlModelProcessor;

public class WadlFeature implements Feature {
   private static final Logger LOGGER = Logger.getLogger(WadlFeature.class.getName());

   public boolean configure(FeatureContext context) {
      boolean disabled = PropertiesHelper.isProperty(context.getConfiguration().getProperty("jersey.config.server.wadl.disableWadl"));
      if (disabled) {
         return false;
      } else if (!ReflectionHelper.isJaxbAvailable()) {
         LOGGER.warning(LocalizationMessages.WADL_FEATURE_DISABLED_NOJAXB());
         return false;
      } else if (!ReflectionHelper.isXmlTransformAvailable()) {
         LOGGER.warning(LocalizationMessages.WADL_FEATURE_DISABLED_NOTRANSFORM());
         return false;
      } else if (!WadlApplicationContextImpl.isJaxbImplAvailable()) {
         LOGGER.warning(LocalizationMessages.WADL_FEATURE_DISABLED());
         return false;
      } else {
         context.register(WadlModelProcessor.class);
         context.register(new AbstractBinder() {
            protected void configure() {
               ((ClassBinding)this.bind(WadlApplicationContextImpl.class).to(WadlApplicationContext.class)).in(Singleton.class);
            }
         });
         return true;
      }
   }
}
