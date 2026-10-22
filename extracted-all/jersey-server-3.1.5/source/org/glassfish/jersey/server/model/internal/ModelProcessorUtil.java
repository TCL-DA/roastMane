package org.glassfish.jersey.server.model.internal;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.glassfish.jersey.process.Inflector;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.model.ResourceModel;
import org.glassfish.jersey.server.model.RuntimeResource;

public final class ModelProcessorUtil {
   private ModelProcessorUtil() {
      throw new AssertionError("Instantiation not allowed.");
   }

   public static Set<String> getAllowedMethods(RuntimeResource resource) {
      boolean getFound = false;
      Set<String> allowedMethods = new HashSet<>();

      for (ResourceMethod resourceMethod : resource.getResourceMethods()) {
         String httpMethod = resourceMethod.getHttpMethod();
         allowedMethods.add(httpMethod);
         if (!getFound && httpMethod.equals("GET")) {
            getFound = true;
         }
      }

      allowedMethods.add("OPTIONS");
      if (getFound) {
         allowedMethods.add("HEAD");
      }

      return allowedMethods;
   }

   private static boolean isMethodOverridden(ResourceMethod resourceMethod, String httpMethod, MediaType consumes, MediaType produces) {
      if (!resourceMethod.getHttpMethod().equals(httpMethod)) {
         return false;
      } else {
         boolean consumesMatch = overrides(resourceMethod.getConsumedTypes(), consumes);
         boolean producesMatch = overrides(resourceMethod.getProducedTypes(), produces);
         return consumesMatch && producesMatch;
      }
   }

   private static boolean overrides(List<MediaType> mediaTypes, MediaType mediaType) {
      if (mediaTypes.isEmpty()) {
         return true;
      } else {
         for (MediaType mt : mediaTypes) {
            if (overrides(mt, mediaType)) {
               return true;
            }
         }

         return false;
      }
   }

   private static boolean overrides(MediaType mt1, MediaType mt2) {
      return mt1.isWildcardType() || mt1.getType().equals(mt2.getType()) && (mt1.isWildcardSubtype() || mt1.getSubtype().equals(mt2.getSubtype()));
   }

   public static ResourceModel.Builder enhanceResourceModel(
      ResourceModel resourceModel, boolean subResourceModel, List<ModelProcessorUtil.Method> methods, boolean extendedFlag
   ) {
      ResourceModel.Builder newModelBuilder = new ResourceModel.Builder(resourceModel, subResourceModel);

      for (RuntimeResource resource : resourceModel.getRuntimeResourceModel().getRuntimeResources()) {
         enhanceResource(resource, newModelBuilder, methods, extendedFlag);
      }

      return newModelBuilder;
   }

   public static void enhanceResource(
      RuntimeResource resource, ResourceModel.Builder enhancedModelBuilder, List<ModelProcessorUtil.Method> methods, boolean extended
   ) {
      Resource firstResource = resource.getResources().get(0);
      if (methodsSuitableForResource(firstResource, methods)) {
         for (ModelProcessorUtil.Method method : methods) {
            Set<MediaType> produces = new HashSet<>(method.produces);

            for (ResourceMethod resourceMethod : resource.getResourceMethods()) {
               for (MediaType produce : method.produces) {
                  if (isMethodOverridden(resourceMethod, method.httpMethod, method.consumes.get(0), produce)) {
                     produces.remove(produce);
                  }
               }
            }

            if (!produces.isEmpty()) {
               Resource parentResource = resource.getParentResources().get(0);
               if (parentResource == null || method.path == null) {
                  Resource.Builder resourceBuilder = Resource.builder(firstResource.getPath());
                  Resource.Builder builder = method.path != null ? resourceBuilder.addChildResource(method.path) : resourceBuilder;
                  ResourceMethod.Builder methodBuilder = builder.addMethod(method.httpMethod).consumes(method.consumes).produces(produces);
                  if (method.inflector != null) {
                     methodBuilder.handledBy(method.inflector);
                  } else {
                     methodBuilder.handledBy(method.inflectorClass);
                  }

                  methodBuilder.extended(extended);
                  Resource newResource = resourceBuilder.build();
                  if (parentResource != null) {
                     Resource.Builder parentBuilder = Resource.builder(parentResource.getPath());
                     parentBuilder.addChildResource(newResource);
                     enhancedModelBuilder.addResource(parentBuilder.build());
                  } else {
                     enhancedModelBuilder.addResource(newResource);
                  }
               }
            }
         }
      }

      for (RuntimeResource child : resource.getChildRuntimeResources()) {
         enhanceResource(child, enhancedModelBuilder, methods, extended);
      }
   }

   private static boolean methodsSuitableForResource(Resource resource, List<ModelProcessorUtil.Method> methods) {
      if (!resource.getResourceMethods().isEmpty()) {
         return true;
      } else {
         if (resource.getHandlerInstances().isEmpty() && resource.getHandlerClasses().isEmpty()) {
            for (ModelProcessorUtil.Method method : methods) {
               if (!"HEAD".equals(method.httpMethod) && !"OPTIONS".equals(method.httpMethod)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public static class Method {
      private final String httpMethod;
      private final String path;
      private final List<MediaType> consumes;
      private final List<MediaType> produces;
      private final Class<? extends Inflector<ContainerRequestContext, Response>> inflectorClass;
      private final Inflector<ContainerRequestContext, Response> inflector;

      public Method(
         String path, String httpMethod, MediaType consumes, MediaType produces, Class<? extends Inflector<ContainerRequestContext, Response>> inflector
      ) {
         this(path, httpMethod, Collections.singletonList(consumes), Collections.singletonList(produces), inflector);
      }

      public Method(
         String path,
         String httpMethod,
         List<MediaType> consumes,
         List<MediaType> produces,
         Class<? extends Inflector<ContainerRequestContext, Response>> inflectorClass
      ) {
         this.path = path;
         this.httpMethod = httpMethod;
         this.consumes = consumes;
         this.produces = produces;
         this.inflectorClass = inflectorClass;
         this.inflector = null;
      }

      public Method(String httpMethod, MediaType consumes, MediaType produces, Class<? extends Inflector<ContainerRequestContext, Response>> inflector) {
         this(null, httpMethod, consumes, produces, inflector);
      }

      public Method(
         String httpMethod, List<MediaType> consumes, List<MediaType> produces, Class<? extends Inflector<ContainerRequestContext, Response>> inflector
      ) {
         this(null, httpMethod, consumes, produces, inflector);
      }

      public Method(String path, String httpMethod, List<MediaType> consumes, List<MediaType> produces, Inflector<ContainerRequestContext, Response> inflector) {
         this.path = path;
         this.httpMethod = httpMethod;
         this.consumes = consumes;
         this.produces = produces;
         this.inflectorClass = null;
         this.inflector = inflector;
      }

      public Method(String path, String httpMethod, MediaType consumes, MediaType produces, Inflector<ContainerRequestContext, Response> inflector) {
         this(path, httpMethod, Collections.singletonList(consumes), Collections.singletonList(produces), inflector);
      }

      public Method(String httpMethod, MediaType consumes, MediaType produces, Inflector<ContainerRequestContext, Response> inflector) {
         this(null, httpMethod, consumes, produces, inflector);
      }

      public Method(String httpMethod, List<MediaType> consumes, List<MediaType> produces, Inflector<ContainerRequestContext, Response> inflector) {
         this(null, httpMethod, consumes, produces, inflector);
      }
   }
}
