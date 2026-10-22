package org.glassfish.jersey.internal;

import jakarta.ws.rs.core.Configuration;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import org.glassfish.jersey.internal.spi.AutoDiscoverable;
import org.glassfish.jersey.message.MessageBodyWorkers;
import org.glassfish.jersey.model.internal.ManagedObjectsFinalizer;
import org.glassfish.jersey.process.internal.RequestScope;
import org.glassfish.jersey.spi.ContextResolvers;
import org.glassfish.jersey.spi.ExceptionMappers;

public class BootstrapBag {
   private Configuration configuration;
   private RequestScope requestScope;
   private MessageBodyWorkers messageBodyWorkers;
   private ExceptionMappers exceptionMappers;
   private ContextResolvers contextResolvers;
   private ManagedObjectsFinalizer managedObjectsFinalizer;
   private List<AutoDiscoverable> autoDiscoverables;

   public List<AutoDiscoverable> getAutoDiscoverables() {
      return this.autoDiscoverables;
   }

   public void setAutoDiscoverables(List<AutoDiscoverable> autoDiscoverables) {
      this.autoDiscoverables = autoDiscoverables;
   }

   public ManagedObjectsFinalizer getManagedObjectsFinalizer() {
      return this.managedObjectsFinalizer;
   }

   public void setManagedObjectsFinalizer(ManagedObjectsFinalizer managedObjectsFinalizer) {
      this.managedObjectsFinalizer = managedObjectsFinalizer;
   }

   public RequestScope getRequestScope() {
      requireNonNull(this.requestScope, RequestScope.class);
      return this.requestScope;
   }

   public void setRequestScope(RequestScope requestScope) {
      this.requestScope = requestScope;
   }

   public MessageBodyWorkers getMessageBodyWorkers() {
      requireNonNull(this.messageBodyWorkers, MessageBodyWorkers.class);
      return this.messageBodyWorkers;
   }

   public void setMessageBodyWorkers(MessageBodyWorkers messageBodyWorkers) {
      this.messageBodyWorkers = messageBodyWorkers;
   }

   public Configuration getConfiguration() {
      requireNonNull(this.configuration, Configuration.class);
      return this.configuration;
   }

   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }

   public ExceptionMappers getExceptionMappers() {
      requireNonNull(this.exceptionMappers, ExceptionMappers.class);
      return this.exceptionMappers;
   }

   public void setExceptionMappers(ExceptionMappers exceptionMappers) {
      this.exceptionMappers = exceptionMappers;
   }

   public ContextResolvers getContextResolvers() {
      requireNonNull(this.contextResolvers, ContextResolvers.class);
      return this.contextResolvers;
   }

   public void setContextResolvers(ContextResolvers contextResolvers) {
      this.contextResolvers = contextResolvers;
   }

   protected static void requireNonNull(Object object, Type type) {
      Objects.requireNonNull(object, type + " has not been added into BootstrapBag yet");
   }
}
