package org.glassfish.jersey.server.model;

import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.WriterInterceptor;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.glassfish.jersey.model.ContractProvider;
import org.glassfish.jersey.model.ContractProvider.Builder;
import org.glassfish.jersey.model.internal.CommonConfig;
import org.glassfish.jersey.model.internal.ComponentBag;
import org.glassfish.jersey.process.Inflector;
import org.glassfish.jersey.server.internal.LocalizationMessages;

class ResourceMethodConfig extends CommonConfig {
   private static final Logger LOGGER = Logger.getLogger(ResourceMethodConfig.class.getName());
   private static final Set<Class<?>> allowedContracts;

   ResourceMethodConfig(Map<String, Object> properties) {
      super(RuntimeType.SERVER, ComponentBag.EXCLUDE_EMPTY);
      this.setProperties(properties);
   }

   protected Inflector<Builder, ContractProvider> getModelEnhancer(final Class<?> providerClass) {
      return new Inflector<Builder, ContractProvider>() {
         public ContractProvider apply(Builder builder) {
            Iterator<Class<?>> it = builder.getContracts().keySet().iterator();

            while (it.hasNext()) {
               Class<?> contract = it.next();
               if (!ResourceMethodConfig.allowedContracts.contains(contract)) {
                  ResourceMethodConfig.LOGGER.warning(LocalizationMessages.CONTRACT_CANNOT_BE_BOUND_TO_RESOURCE_METHOD(contract, providerClass));
                  it.remove();
               }
            }

            return builder.build();
         }
      };
   }

   static {
      Set<Class<?>> tempSet = Collections.newSetFromMap(new IdentityHashMap<>());
      tempSet.add(ContainerRequestFilter.class);
      tempSet.add(ContainerResponseFilter.class);
      tempSet.add(ReaderInterceptor.class);
      tempSet.add(WriterInterceptor.class);
      allowedContracts = Collections.unmodifiableSet(tempSet);
   }
}
