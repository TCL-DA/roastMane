package org.glassfish.jersey.server;

import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.jersey.internal.BootstrapBag;
import org.glassfish.jersey.internal.BootstrapConfigurator;
import org.glassfish.jersey.internal.ServiceFinder;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.spi.ComponentProvider;
import org.glassfish.jersey.server.spi.ExternalRequestContext;
import org.glassfish.jersey.server.spi.ExternalRequestScope;

class ExternalRequestScopeConfigurator implements BootstrapConfigurator {
   private static final Logger LOGGER = Logger.getLogger(ExternalRequestScopeConfigurator.class.getName());
   private static final ExternalRequestScope<Object> NOOP_EXTERNAL_REQ_SCOPE = new ExternalRequestScope<Object>() {
      @Override
      public ExternalRequestContext<Object> open(InjectionManager injectionManager) {
         return null;
      }

      @Override
      public void close() {
      }

      @Override
      public void suspend(ExternalRequestContext<Object> o, InjectionManager injectionManager) {
      }

      @Override
      public void resume(ExternalRequestContext<Object> o, InjectionManager injectionManager) {
      }
   };

   public void init(InjectionManager injectionManager, BootstrapBag bootstrapBag) {
      ServerBootstrapBag serverBag = (ServerBootstrapBag)bootstrapBag;
      Class<ExternalRequestScope>[] extScopes = ServiceFinder.find(ExternalRequestScope.class, true).toClassArray();
      boolean extScopeBound = false;
      if (extScopes.length == 1) {
         for (ComponentProvider p : (Collection)serverBag.getComponentProviders().get()) {
            if (p.bind(extScopes[0], Collections.singleton(ExternalRequestScope.class))) {
               extScopeBound = true;
               break;
            }
         }
      } else if (extScopes.length > 1 && LOGGER.isLoggable(Level.WARNING)) {
         StringBuilder scopeList = new StringBuilder("\n");

         for (Class<ExternalRequestScope> ers : extScopes) {
            scopeList.append("   ").append(ers.getTypeParameters()[0]).append('\n');
         }

         LOGGER.warning(LocalizationMessages.WARNING_TOO_MANY_EXTERNAL_REQ_SCOPES(scopeList.toString()));
      }

      if (!extScopeBound) {
         injectionManager.register(new ExternalRequestScopeConfigurator.NoopExternalRequestScopeBinder());
      }
   }

   private static class NoopExternalRequestScopeBinder extends AbstractBinder {
      private NoopExternalRequestScopeBinder() {
      }

      protected void configure() {
         this.bind(ExternalRequestScopeConfigurator.NOOP_EXTERNAL_REQ_SCOPE).to(ExternalRequestScope.class);
      }
   }
}
