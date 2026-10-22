package org.glassfish.jersey.internal;

import jakarta.ws.rs.SeBootstrap.Configuration;
import jakarta.ws.rs.SeBootstrap.Instance;
import jakarta.ws.rs.SeBootstrap.Configuration.Builder;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.ext.RuntimeDelegate;
import java.util.concurrent.CompletionStage;
import org.glassfish.jersey.message.internal.MessagingBinders;

public class RuntimeDelegateImpl extends AbstractRuntimeDelegate {
   public RuntimeDelegateImpl() {
      super(new MessagingBinders.HeaderDelegateProviders().getHeaderDelegateProviders());
   }

   public <T> T createEndpoint(Application application, Class<T> endpointType) throws IllegalArgumentException, UnsupportedOperationException {
      RuntimeDelegate runtimeDelegate = this.findServerDelegate();
      if (runtimeDelegate != null) {
         return (T)runtimeDelegate.createEndpoint(application, endpointType);
      } else {
         throw new UnsupportedOperationException(LocalizationMessages.NO_CONTAINER_AVAILABLE());
      }
   }

   public Builder createConfigurationBuilder() {
      RuntimeDelegate runtimeDelegate = this.findServerDelegate();
      if (runtimeDelegate != null) {
         return runtimeDelegate.createConfigurationBuilder();
      } else {
         throw new UnsupportedOperationException(LocalizationMessages.NO_CONTAINER_AVAILABLE());
      }
   }

   public CompletionStage<Instance> bootstrap(Application application, Configuration configuration) {
      RuntimeDelegate runtimeDelegate = this.findServerDelegate();
      if (runtimeDelegate != null) {
         return runtimeDelegate.bootstrap(application, configuration);
      } else {
         throw new UnsupportedOperationException(LocalizationMessages.NO_CONTAINER_AVAILABLE());
      }
   }

   public CompletionStage<Instance> bootstrap(Class<? extends Application> applicationClass, Configuration configuration) {
      RuntimeDelegate runtimeDelegate = this.findServerDelegate();
      if (runtimeDelegate != null) {
         return runtimeDelegate.bootstrap(applicationClass, configuration);
      } else {
         throw new UnsupportedOperationException(LocalizationMessages.NO_CONTAINER_AVAILABLE());
      }
   }

   private RuntimeDelegate findServerDelegate() {
      for (RuntimeDelegate delegate : ServiceFinder.find(RuntimeDelegate.class)) {
         if (delegate.getClass() != RuntimeDelegateImpl.class) {
            RuntimeDelegate.setInstance(delegate);
            return delegate;
         }
      }

      return null;
   }
}
