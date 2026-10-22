package org.glassfish.jersey.inject.hk2;

import jakarta.inject.Singleton;
import java.lang.annotation.Annotation;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.jersey.internal.inject.ForeignDescriptorImpl;
import org.glassfish.jersey.internal.inject.InjecteeImpl;
import org.glassfish.jersey.internal.inject.InjectionResolver;
import org.glassfish.jersey.internal.util.ReflectionHelper;

@Singleton
public class InjectionResolverWrapper<T extends Annotation> implements org.glassfish.hk2.api.InjectionResolver<T> {
   private final InjectionResolver jerseyResolver;

   InjectionResolverWrapper(InjectionResolver<T> jerseyResolver) {
      this.jerseyResolver = jerseyResolver;
   }

   public Object resolve(Injectee injectee, ServiceHandle root) {
      InjecteeImpl injecteeWrapper = new InjecteeImpl();
      injecteeWrapper.setRequiredType(injectee.getRequiredType());
      injecteeWrapper.setParent(injectee.getParent());
      injecteeWrapper.setRequiredQualifiers(injectee.getRequiredQualifiers());
      injecteeWrapper.setOptional(injectee.isOptional());
      injecteeWrapper.setPosition(injectee.getPosition());
      injecteeWrapper.setFactory(ReflectionHelper.isSubClassOf(injectee.getRequiredType(), Factory.class));
      injecteeWrapper.setInjecteeDescriptor(new ForeignDescriptorImpl(injectee.getInjecteeDescriptor()));
      Object instance = this.jerseyResolver.resolve(injecteeWrapper);
      return injecteeWrapper.isFactory() ? this.asFactory(instance) : instance;
   }

   private Factory asFactory(final Object instance) {
      return new Factory() {
         public Object provide() {
            return instance;
         }

         public void dispose(Object instancex) {
         }
      };
   }

   public boolean isConstructorParameterIndicator() {
      return this.jerseyResolver.isConstructorParameterIndicator();
   }

   public boolean isMethodParameterIndicator() {
      return this.jerseyResolver.isMethodParameterIndicator();
   }
}
