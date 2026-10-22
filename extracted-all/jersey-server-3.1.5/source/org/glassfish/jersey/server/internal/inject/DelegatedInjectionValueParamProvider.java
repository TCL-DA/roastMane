package org.glassfish.jersey.server.internal.inject;

import jakarta.inject.Singleton;
import java.lang.reflect.ParameterizedType;
import java.util.function.Function;
import org.glassfish.jersey.internal.inject.Binding;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.internal.inject.ContextInjectionResolver;
import org.glassfish.jersey.internal.inject.ForeignDescriptor;
import org.glassfish.jersey.internal.inject.Injectee;
import org.glassfish.jersey.internal.inject.InjecteeImpl;
import org.glassfish.jersey.internal.util.collection.Cache;
import org.glassfish.jersey.internal.util.collection.LazyValue;
import org.glassfish.jersey.model.Parameter.Source;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;

@Singleton
class DelegatedInjectionValueParamProvider implements ValueParamProvider {
   private final LazyValue<ContextInjectionResolver> resolver;
   private final Function<Binding, ForeignDescriptor> foreignDescriptorFactory;
   private final Cache<Parameter, ForeignDescriptor> descriptorCache = new Cache(parameter -> {
      Class<?> rawType = parameter.getRawType();
      return rawType.isInterface() && !(parameter.getType() instanceof ParameterizedType) ? this.createDescriptor(rawType) : null;
   });

   public DelegatedInjectionValueParamProvider(LazyValue<ContextInjectionResolver> resolver, Function<Binding, ForeignDescriptor> foreignDescriptorFactory) {
      this.resolver = resolver;
      this.foreignDescriptorFactory = foreignDescriptorFactory;
   }

   @Override
   public Function<ContainerRequest, ?> getValueProvider(Parameter parameter) {
      Source paramSource = parameter.getSource();
      return paramSource == Source.CONTEXT ? containerRequest -> ((ContextInjectionResolver)this.resolver.get()).resolve(this.getInjectee(parameter)) : null;
   }

   @Override
   public ValueParamProvider.PriorityType getPriority() {
      return ValueParamProvider.Priority.LOW;
   }

   private Injectee getInjectee(Parameter parameter) {
      InjecteeImpl injectee = new InjecteeImpl();
      injectee.setRequiredType(parameter.getType());
      injectee.setInjecteeClass(parameter.getRawType());
      ForeignDescriptor proxyDescriptor = (ForeignDescriptor)this.descriptorCache.apply(parameter);
      if (proxyDescriptor != null) {
         injectee.setInjecteeDescriptor(proxyDescriptor);
      }

      return injectee;
   }

   private ForeignDescriptor createDescriptor(Class<?> clazz) {
      return this.foreignDescriptorFactory.apply(Bindings.serviceAsContract(clazz).in(RequestScoped.class));
   }
}
