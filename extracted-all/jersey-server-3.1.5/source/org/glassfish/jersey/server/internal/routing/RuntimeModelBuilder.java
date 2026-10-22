package org.glassfish.jersey.server.internal.routing;

import jakarta.ws.rs.core.Configuration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import org.glassfish.jersey.internal.util.collection.Value;
import org.glassfish.jersey.internal.util.collection.Values;
import org.glassfish.jersey.message.MessageBodyWorkers;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.internal.JerseyResourceContext;
import org.glassfish.jersey.server.internal.ProcessingProviders;
import org.glassfish.jersey.server.internal.process.Endpoint;
import org.glassfish.jersey.server.model.ModelProcessor;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.model.ResourceMethodInvoker;
import org.glassfish.jersey.server.model.RuntimeResource;
import org.glassfish.jersey.server.model.RuntimeResourceModel;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;
import org.glassfish.jersey.uri.PathPattern;
import org.glassfish.jersey.uri.UriTemplate;

final class RuntimeModelBuilder {
   private final ResourceMethodInvoker.Builder resourceMethodInvokerBuilder;
   private final MessageBodyWorkers messageBodyWorkers;
   private final ProcessingProviders processingProviders;
   private final Value<RuntimeLocatorModelBuilder> locatorBuilder;
   private final boolean isWildcardMethodSelectingRouter;

   public RuntimeModelBuilder(
      JerseyResourceContext resourceContext,
      Configuration config,
      MessageBodyWorkers messageBodyWorkers,
      Collection<ValueParamProvider> valueSuppliers,
      ProcessingProviders processingProviders,
      ResourceMethodInvoker.Builder resourceMethodInvokerBuilder,
      Iterable<ModelProcessor> modelProcessors,
      Function<Class<?>, ?> createServiceFunction
   ) {
      this.resourceMethodInvokerBuilder = resourceMethodInvokerBuilder;
      this.messageBodyWorkers = messageBodyWorkers;
      this.processingProviders = processingProviders;
      this.locatorBuilder = Values.lazy(
         () -> new RuntimeLocatorModelBuilder(config, messageBodyWorkers, valueSuppliers, resourceContext, this, modelProcessors, createServiceFunction)
      );
      this.isWildcardMethodSelectingRouter = ServerProperties.getValue(
         config.getProperties(), "jersey.config.server.empty.request.media.matches.any.consumes", true
      );
   }

   private Router createMethodRouter(ResourceMethod resourceMethod) {
      Router methodAcceptor = null;
      switch (resourceMethod.getType()) {
         case RESOURCE_METHOD:
         case SUB_RESOURCE_METHOD:
            methodAcceptor = Routers.endpoint(this.createInflector(resourceMethod));
            break;
         case SUB_RESOURCE_LOCATOR:
            methodAcceptor = ((RuntimeLocatorModelBuilder)this.locatorBuilder.get()).getRouter(resourceMethod);
      }

      return new PushMethodHandlerRouter(resourceMethod.getInvocable().getHandler(), methodAcceptor);
   }

   private Endpoint createInflector(ResourceMethod method) {
      return this.resourceMethodInvokerBuilder.build(method, this.processingProviders);
   }

   private Router createRootRouter(PathMatchingRouterBuilder lastRoutedBuilder, boolean subResourceMode) {
      Router routingRoot;
      if (lastRoutedBuilder != null) {
         routingRoot = lastRoutedBuilder.build();
      } else {
         routingRoot = Routers.noop();
      }

      return (Router)(subResourceMode ? routingRoot : new MatchResultInitializerRouter(routingRoot));
   }

   public Router buildModel(RuntimeResourceModel resourceModel, boolean subResourceMode) {
      List<RuntimeResource> runtimeResources = resourceModel.getRuntimeResources();
      PushMatchedUriRouter uriPushingRouter = new PushMatchedUriRouter();
      PathMatchingRouterBuilder currentRouterBuilder = null;

      for (RuntimeResource resource : runtimeResources) {
         PushMatchedRuntimeResourceRouter resourcePushingRouter = new PushMatchedRuntimeResourceRouter(resource);
         if (!resource.getResourceMethods().isEmpty()) {
            List<MethodRouting> methodRoutings = this.createResourceMethodRouters(resource, subResourceMode);
            Router methodSelectingRouter = (Router)(this.isWildcardMethodSelectingRouter
               ? new WildcardMethodSelectingRouter(this.messageBodyWorkers, methodRoutings)
               : new OctetStreamMethodSelectingRouter(this.messageBodyWorkers, methodRoutings));
            if (subResourceMode) {
               currentRouterBuilder = this.startNextRoute(currentRouterBuilder, PathPattern.END_OF_PATH_PATTERN)
                  .to(resourcePushingRouter)
                  .to(methodSelectingRouter);
            } else {
               currentRouterBuilder = this.startNextRoute(currentRouterBuilder, PathPattern.asClosed(resource.getPathPattern()))
                  .to(uriPushingRouter)
                  .to(resourcePushingRouter)
                  .to(methodSelectingRouter);
            }
         }

         PathMatchingRouterBuilder srRoutedBuilder = null;
         if (!resource.getChildRuntimeResources().isEmpty()) {
            for (RuntimeResource childResource : resource.getChildRuntimeResources()) {
               PathPattern childOpenPattern = childResource.getPathPattern();
               PathPattern childClosedPattern = PathPattern.asClosed(childOpenPattern);
               PushMatchedRuntimeResourceRouter childResourcePushingRouter = new PushMatchedRuntimeResourceRouter(childResource);
               if (!childResource.getResourceMethods().isEmpty()) {
                  List<MethodRouting> childMethodRoutings = this.createResourceMethodRouters(childResource, subResourceMode);
                  srRoutedBuilder = this.startNextRoute(srRoutedBuilder, childClosedPattern)
                     .to(uriPushingRouter)
                     .to(childResourcePushingRouter)
                     .to(
                        (Router)(this.isWildcardMethodSelectingRouter
                           ? new WildcardMethodSelectingRouter(this.messageBodyWorkers, childMethodRoutings)
                           : new OctetStreamMethodSelectingRouter(this.messageBodyWorkers, childMethodRoutings))
                     );
               }

               if (childResource.getResourceLocator() != null) {
                  PushMatchedTemplateRouter locTemplateRouter = this.getTemplateRouterForChildLocator(subResourceMode, childResource);
                  srRoutedBuilder = this.startNextRoute(srRoutedBuilder, childOpenPattern)
                     .to(uriPushingRouter)
                     .to(locTemplateRouter)
                     .to(childResourcePushingRouter)
                     .to(new PushMatchedMethodRouter(childResource.getResourceLocator()))
                     .to(this.createMethodRouter(childResource.getResourceLocator()));
               }
            }
         }

         if (resource.getResourceLocator() != null) {
            PushMatchedTemplateRouter resourceTemplateRouter = this.getTemplateRouter(
               subResourceMode, this.getLocatorResource(resource).getPathPattern().getTemplate(), PathPattern.OPEN_ROOT_PATH_PATTERN.getTemplate()
            );
            srRoutedBuilder = this.startNextRoute(srRoutedBuilder, PathPattern.OPEN_ROOT_PATH_PATTERN)
               .to(uriPushingRouter)
               .to(resourceTemplateRouter)
               .to(new PushMatchedMethodRouter(resource.getResourceLocator()))
               .to(this.createMethodRouter(resource.getResourceLocator()));
         }

         if (srRoutedBuilder != null) {
            Router methodRouter = srRoutedBuilder.build();
            if (subResourceMode) {
               currentRouterBuilder = this.startNextRoute(currentRouterBuilder, PathPattern.OPEN_ROOT_PATH_PATTERN).to(resourcePushingRouter).to(methodRouter);
            } else {
               currentRouterBuilder = this.startNextRoute(currentRouterBuilder, resource.getPathPattern())
                  .to(uriPushingRouter)
                  .to(resourcePushingRouter)
                  .to(methodRouter);
            }
         }
      }

      return this.createRootRouter(currentRouterBuilder, subResourceMode);
   }

   private PushMatchedTemplateRouter getTemplateRouterForChildLocator(boolean subResourceMode, RuntimeResource child) {
      int i = 0;

      for (Resource res : child.getResources()) {
         if (res.getResourceLocator() != null) {
            return this.getTemplateRouter(subResourceMode, child.getParentResources().get(i).getPathPattern().getTemplate(), res.getPathPattern().getTemplate());
         }

         i++;
      }

      return null;
   }

   private PushMatchedTemplateRouter getTemplateRouter(boolean subResourceMode, UriTemplate parentTemplate, UriTemplate childTemplate) {
      return childTemplate != null
         ? new PushMatchedTemplateRouter(subResourceMode ? PathPattern.OPEN_ROOT_PATH_PATTERN.getTemplate() : parentTemplate, childTemplate)
         : new PushMatchedTemplateRouter(subResourceMode ? PathPattern.END_OF_PATH_PATTERN.getTemplate() : parentTemplate);
   }

   private Resource getLocatorResource(RuntimeResource resource) {
      for (Resource res : resource.getResources()) {
         if (res.getResourceLocator() != null) {
            return res;
         }
      }

      return null;
   }

   private List<MethodRouting> createResourceMethodRouters(RuntimeResource runtimeResource, boolean subResourceMode) {
      List<MethodRouting> methodRoutings = new ArrayList<>();
      int i = 0;

      for (Resource resource : runtimeResource.getResources()) {
         Resource parentResource = runtimeResource.getParent() == null ? null : runtimeResource.getParentResources().get(i++);
         UriTemplate template = resource.getPathPattern().getTemplate();
         PushMatchedTemplateRouter templateRouter = parentResource == null
            ? this.getTemplateRouter(subResourceMode, template, null)
            : this.getTemplateRouter(subResourceMode, parentResource.getPathPattern().getTemplate(), template);

         for (ResourceMethod resourceMethod : resource.getResourceMethods()) {
            methodRoutings.add(
               new MethodRouting(resourceMethod, templateRouter, new PushMatchedMethodRouter(resourceMethod), this.createMethodRouter(resourceMethod))
            );
         }
      }

      return methodRoutings.isEmpty() ? Collections.emptyList() : methodRoutings;
   }

   private PathToRouterBuilder startNextRoute(PathMatchingRouterBuilder currentRouterBuilder, PathPattern routingPattern) {
      return currentRouterBuilder == null ? PathMatchingRouterBuilder.newRoute(routingPattern) : currentRouterBuilder.route(routingPattern);
   }
}
