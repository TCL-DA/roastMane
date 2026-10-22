package org.glassfish.jersey.internal;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Providers;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.PerLookup;
import org.glassfish.jersey.message.MessageBodyWorkers;
import org.glassfish.jersey.spi.ContextResolvers;
import org.glassfish.jersey.spi.ExceptionMappers;

public class JaxrsProviders implements Providers {
   private final Provider<MessageBodyWorkers> workers;
   private final Provider<ContextResolvers> resolvers;
   private final Provider<ExceptionMappers> mappers;

   @Inject
   public JaxrsProviders(
      @Context Provider<MessageBodyWorkers> workers, @Context Provider<ContextResolvers> resolvers, @Context Provider<ExceptionMappers> mappers
   ) {
      this.workers = workers;
      this.resolvers = resolvers;
      this.mappers = mappers;
   }

   public <T> MessageBodyReader<T> getMessageBodyReader(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return ((MessageBodyWorkers)this.workers.get()).getMessageBodyReader(type, genericType, annotations, mediaType);
   }

   public <T> MessageBodyWriter<T> getMessageBodyWriter(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return ((MessageBodyWorkers)this.workers.get()).getMessageBodyWriter(type, genericType, annotations, mediaType);
   }

   public <T extends Throwable> ExceptionMapper<T> getExceptionMapper(Class<T> type) {
      ExceptionMappers actualMappers = (ExceptionMappers)this.mappers.get();
      return actualMappers != null ? actualMappers.find(type) : null;
   }

   public <T> ContextResolver<T> getContextResolver(Class<T> contextType, MediaType mediaType) {
      return ((ContextResolvers)this.resolvers.get()).resolve(contextType, mediaType);
   }

   public static class ProvidersConfigurator implements BootstrapConfigurator {
      @Override
      public void init(InjectionManager injectionManager, BootstrapBag bootstrapBag) {
         injectionManager.register(Bindings.service(JaxrsProviders.class).to(Providers.class).in(PerLookup.class));
      }
   }
}
