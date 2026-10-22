package org.glassfish.jersey.inject.hk2;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.lang.annotation.Annotation;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Context;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.internal.inject.ForeignDescriptor;
import org.glassfish.jersey.process.internal.RequestScope;
import org.glassfish.jersey.process.internal.RequestScoped;

@Singleton
public class RequestContext implements Context<RequestScoped> {
   private final RequestScope requestScope;

   @Inject
   public RequestContext(RequestScope requestScope) {
      this.requestScope = requestScope;
   }

   public Class<? extends Annotation> getScope() {
      return RequestScoped.class;
   }

   public <U> U findOrCreate(ActiveDescriptor<U> activeDescriptor, ServiceHandle<?> root) {
      Hk2RequestScope.Instance instance = (Hk2RequestScope.Instance)this.requestScope.current();
      U retVal = instance.get(ForeignDescriptor.wrap(activeDescriptor));
      if (retVal == null) {
         retVal = (U)activeDescriptor.create(root);
         instance.put(ForeignDescriptor.wrap(activeDescriptor, obj -> activeDescriptor.dispose(obj)), retVal);
      }

      return retVal;
   }

   public boolean containsKey(ActiveDescriptor<?> descriptor) {
      Hk2RequestScope.Instance instance = (Hk2RequestScope.Instance)this.requestScope.current();
      return instance.contains(ForeignDescriptor.wrap(descriptor));
   }

   public boolean supportsNullCreation() {
      return true;
   }

   public boolean isActive() {
      return this.requestScope.isActive();
   }

   public void destroyOne(ActiveDescriptor<?> descriptor) {
      Hk2RequestScope.Instance instance = (Hk2RequestScope.Instance)this.requestScope.current();
      instance.remove(ForeignDescriptor.wrap(descriptor));
   }

   public void shutdown() {
      this.requestScope.shutdown();
   }

   public static class Binder extends AbstractBinder {
      protected void configure() {
         this.bindAsContract(RequestContext.class).to((new TypeLiteral<Context<RequestScoped>>() {
         }).getType()).in(Singleton.class);
      }
   }
}
