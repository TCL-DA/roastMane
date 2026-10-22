package org.glassfish.jersey.server.internal.routing;

import jakarta.ws.rs.core.Configuration;
import java.util.Collection;
import java.util.function.Function;
import org.glassfish.jersey.message.MessageBodyWorkers;
import org.glassfish.jersey.process.internal.ChainableStage;
import org.glassfish.jersey.process.internal.Stage;
import org.glassfish.jersey.server.internal.JerseyResourceContext;
import org.glassfish.jersey.server.internal.ProcessingProviders;
import org.glassfish.jersey.server.internal.process.RequestProcessingContext;
import org.glassfish.jersey.server.model.ModelProcessor;
import org.glassfish.jersey.server.model.ResourceMethodInvoker;
import org.glassfish.jersey.server.model.RuntimeResourceModel;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;

public final class Routing {
   private Routing() {
      throw new AssertionError("No instances allowed.");
   }

   public static Stage<RequestProcessingContext> matchedEndpointExtractor() {
      return new MatchedEndpointExtractorStage();
   }

   public static Routing.Builder forModel(RuntimeResourceModel resourceModel) {
      return new Routing.Builder(resourceModel);
   }

   public static final class Builder {
      private final RuntimeResourceModel resourceModel;
      private JerseyResourceContext resourceContext;
      private Configuration config;
      private MessageBodyWorkers entityProviders;
      private Collection<ValueParamProvider> valueSuppliers;
      private Iterable<ModelProcessor> modelProcessors;
      private Function<Class<?>, ?> createServiceFunction;
      private ProcessingProviders processingProviders;
      private ResourceMethodInvoker.Builder resourceMethodInvokerBuilder;

      private Builder(RuntimeResourceModel resourceModel) {
         if (resourceModel == null) {
            throw new NullPointerException("Resource model must not be null.");
         } else {
            this.resourceModel = resourceModel;
         }
      }

      public Routing.Builder resourceContext(JerseyResourceContext resourceContext) {
         this.resourceContext = resourceContext;
         return this;
      }

      public Routing.Builder configuration(Configuration config) {
         this.config = config;
         return this;
      }

      public Routing.Builder entityProviders(MessageBodyWorkers workers) {
         this.entityProviders = workers;
         return this;
      }

      public Routing.Builder valueSupplierProviders(Collection<ValueParamProvider> valueSuppliers) {
         this.valueSuppliers = valueSuppliers;
         return this;
      }

      public Routing.Builder processingProviders(ProcessingProviders processingProviders) {
         this.processingProviders = processingProviders;
         return this;
      }

      public Routing.Builder modelProcessors(Iterable<ModelProcessor> modelProcessors) {
         this.modelProcessors = modelProcessors;
         return this;
      }

      public Routing.Builder createService(Function<Class<?>, ?> createServiceFunction) {
         this.createServiceFunction = createServiceFunction;
         return this;
      }

      public Routing.Builder resourceMethodInvokerBuilder(ResourceMethodInvoker.Builder resourceMethodInvokerBuilder) {
         this.resourceMethodInvokerBuilder = resourceMethodInvokerBuilder;
         return this;
      }

      public ChainableStage<RequestProcessingContext> buildStage() {
         if (this.resourceContext == null) {
            throw new NullPointerException("Resource context is not set.");
         } else if (this.config == null) {
            throw new NullPointerException("Runtime configuration is not set.");
         } else if (this.entityProviders == null) {
            throw new NullPointerException("Entity providers are not set.");
         } else if (this.valueSuppliers == null) {
            throw new NullPointerException("Value supplier providers are not set.");
         } else if (this.modelProcessors == null) {
            throw new NullPointerException("Model processors are not set.");
         } else if (this.createServiceFunction == null) {
            throw new NullPointerException("Create function is not set.");
         } else if (this.processingProviders == null) {
            throw new NullPointerException("Processing providers are not set.");
         } else if (this.resourceMethodInvokerBuilder == null) {
            throw new NullPointerException("ResourceMethodInvokerBuilder is not set.");
         } else {
            RuntimeModelBuilder runtimeModelBuilder = new RuntimeModelBuilder(
               this.resourceContext,
               this.config,
               this.entityProviders,
               this.valueSuppliers,
               this.processingProviders,
               this.resourceMethodInvokerBuilder,
               this.modelProcessors,
               this.createServiceFunction
            );
            return new RoutingStage(runtimeModelBuilder.buildModel(this.resourceModel, false));
         }
      }
   }
}
