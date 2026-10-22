package org.glassfish.jersey.jackson.internal;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import jakarta.inject.Singleton;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;
import jakarta.ws.rs.core.GenericType;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.internal.inject.ClassBinding;
import org.glassfish.jersey.message.filtering.spi.ObjectGraphTransformer;
import org.glassfish.jersey.message.filtering.spi.ObjectProvider;

public final class JacksonFilteringFeature implements Feature {
   public boolean configure(FeatureContext context) {
      Configuration config = context.getConfiguration();
      if (!config.isRegistered(JacksonFilteringFeature.Binder.class)) {
         context.register(new JacksonFilteringFeature.Binder());
         return true;
      } else {
         return false;
      }
   }

   private static final class Binder extends AbstractBinder {
      private Binder() {
      }

      protected void configure() {
         ((ClassBinding)((ClassBinding)this.bindAsContract(JacksonObjectProvider.class).to(new GenericType<ObjectProvider<FilterProvider>>() {
         })).to(new GenericType<ObjectGraphTransformer<FilterProvider>>() {
         })).in(Singleton.class);
      }
   }
}
