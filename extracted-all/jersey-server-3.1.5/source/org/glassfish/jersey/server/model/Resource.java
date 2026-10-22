package org.glassfish.jersey.server.model;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.glassfish.jersey.Severity;
import org.glassfish.jersey.internal.Errors;
import org.glassfish.jersey.internal.guava.Preconditions;
import org.glassfish.jersey.internal.util.collection.Value;
import org.glassfish.jersey.internal.util.collection.Values;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.model.internal.ModelHelper;
import org.glassfish.jersey.uri.PathPattern;
import org.glassfish.jersey.uri.PathPattern.RightHandPath;

public final class Resource implements Routed, ResourceModelComponent {
   private final Resource parent;
   private final Resource.Data data;
   private final Value<String> name;
   private final List<ResourceMethod> resourceMethods;
   private final ResourceMethod locator;
   private final List<Resource> childResources;

   public static Resource.Builder builder() {
      return new Resource.Builder();
   }

   public static Resource.Builder builder(String path) {
      return new Resource.Builder(path);
   }

   public static Resource.Builder builder(List<Resource> resources) {
      if (resources != null && !resources.isEmpty()) {
         Iterator<Resource> it = resources.iterator();
         Resource.Data resourceData = it.next().data;
         Resource.Builder builder = builder(resourceData);
         String path = resourceData.path;

         while (it.hasNext()) {
            resourceData = it.next().data;
            if ((resourceData.path != null || path != null) && (path == null || !path.equals(resourceData.path))) {
               throw new IllegalArgumentException(LocalizationMessages.ERROR_RESOURCES_CANNOT_MERGE());
            }

            builder.mergeWith(resourceData);
         }

         return builder;
      } else {
         return builder();
      }
   }

   public static Resource.Builder builder(Class<?> resourceClass) {
      return builder(resourceClass, false);
   }

   public static Resource.Builder builder(Class<?> resourceClass, boolean disableValidation) {
      Resource.Builder builder = new IntrospectionModeller(resourceClass, disableValidation).createResourceBuilder();
      return builder.isEmpty() ? null : builder;
   }

   public static Resource from(Class<?> resourceClass) {
      return from(resourceClass, false);
   }

   public static Resource from(Class<?> resourceClass, boolean disableValidation) {
      Resource.Builder builder = new IntrospectionModeller(resourceClass, disableValidation).createResourceBuilder();
      return builder.isEmpty() ? null : builder.build();
   }

   public static boolean isAcceptable(Class<?> c) {
      return (c.getModifiers() & 1024) == 0
         && !c.isPrimitive()
         && !c.isAnnotation()
         && !c.isInterface()
         && !c.isLocalClass()
         && (!c.isMemberClass() || (c.getModifiers() & 8) != 0);
   }

   public static Path getPath(Class<?> resourceClass) {
      return ModelHelper.getAnnotatedResourceClass(resourceClass).getAnnotation(Path.class);
   }

   public static Resource.Builder builder(Resource resource) {
      return builder(resource.data);
   }

   private static Resource.Builder builder(Resource.Data resourceData) {
      Resource.Builder b;
      if (resourceData.path == null) {
         b = new Resource.Builder();
      } else {
         b = new Resource.Builder(resourceData.path);
      }

      b.resourceMethods.addAll(resourceData.resourceMethods);
      b.childResources.addAll(resourceData.childResources);
      b.subResourceLocator = resourceData.subResourceLocator;
      b.handlerClasses.addAll(resourceData.handlerClasses);
      b.handlerInstances.addAll(resourceData.handlerInstances);
      b.names.addAll(resourceData.names);
      return b;
   }

   private static List<Resource> transform(Resource parent, List<Resource.Data> list) {
      return list.stream().map(data -> new Resource(parent, data)).collect(Collectors.toList());
   }

   private static <T> List<T> immutableCopy(List<T> list) {
      return list.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(list);
   }

   private static <T> Set<T> immutableCopy(Set<T> set) {
      if (set.isEmpty()) {
         return Collections.emptySet();
      } else {
         Set<T> result = Collections.newSetFromMap(new IdentityHashMap<>());
         result.addAll(set);
         return set;
      }
   }

   private Resource(Resource parent, final Resource.Data data) {
      this.parent = parent;
      this.data = data;
      this.name = Values.lazy(new Value<String>() {
         public String get() {
            return data.names.size() == 1 ? data.names.get(0) : "Merge of " + data.names.toString();
         }
      });
      this.resourceMethods = immutableCopy(ResourceMethod.transform(this, data.resourceMethods));
      this.locator = data.subResourceLocator == null ? null : new ResourceMethod(this, data.subResourceLocator);
      this.childResources = immutableCopy(transform(this, data.childResources));
   }

   @Override
   public String getPath() {
      return this.data.path;
   }

   @Override
   public PathPattern getPathPattern() {
      return this.data.pathPattern;
   }

   public Resource getParent() {
      return this.parent;
   }

   public String getName() {
      return (String)this.name.get();
   }

   public List<String> getNames() {
      return this.data.names;
   }

   public List<ResourceMethod> getResourceMethods() {
      return this.resourceMethods;
   }

   public ResourceMethod getResourceLocator() {
      return this.locator;
   }

   public List<ResourceMethod> getAllMethods() {
      LinkedList<ResourceMethod> methodsAndLocators = new LinkedList<>(this.getResourceMethods());
      ResourceMethod loc = this.getResourceLocator();
      if (loc != null) {
         methodsAndLocators.add(loc);
      }

      return methodsAndLocators;
   }

   public List<Resource> getChildResources() {
      return this.childResources;
   }

   public Set<Class<?>> getHandlerClasses() {
      return this.data.handlerClasses;
   }

   public Set<Object> getHandlerInstances() {
      return this.data.handlerInstances;
   }

   @Override
   public void accept(ResourceModelVisitor visitor) {
      if (this.getParent() == null) {
         visitor.visitResource(this);
      } else {
         visitor.visitChildResource(this);
      }
   }

   public boolean isExtended() {
      return this.data.extended;
   }

   @Override
   public String toString() {
      return this.data.toString();
   }

   @Override
   public List<? extends ResourceModelComponent> getComponents() {
      List<ResourceModelComponent> components = new LinkedList<>();
      components.addAll(this.getChildResources());
      components.addAll(this.getResourceMethods());
      ResourceMethod resourceLocator = this.getResourceLocator();
      if (resourceLocator != null) {
         components.add(resourceLocator);
      }

      return components;
   }

   public static final class Builder {
      private List<String> names;
      private String path;
      private final Set<ResourceMethod.Builder> methodBuilders = new LinkedHashSet<>();
      private final Set<Resource.Builder> childResourceBuilders = new LinkedHashSet<>();
      private final List<Resource.Data> childResources = new LinkedList<>();
      private final List<ResourceMethod.Data> resourceMethods = new LinkedList<>();
      private ResourceMethod.Data subResourceLocator;
      private final Set<Class<?>> handlerClasses = Collections.newSetFromMap(new IdentityHashMap<>());
      private final Set<Object> handlerInstances = Collections.newSetFromMap(new IdentityHashMap<>());
      private final Resource.Builder parentResource;
      private boolean extended;

      private Builder(Resource.Builder parentResource) {
         this.parentResource = parentResource;
         this.name("[unnamed]");
      }

      private Builder(String path) {
         this((Resource.Builder)null);
         this.path(path);
      }

      private Builder(String path, Resource.Builder parentResource) {
         this(parentResource);
         this.path = path;
      }

      private Builder() {
         this((Resource.Builder)null);
      }

      private boolean isEmpty() {
         return this.path == null
            && this.methodBuilders.isEmpty()
            && this.childResourceBuilders.isEmpty()
            && this.resourceMethods.isEmpty()
            && this.childResources.isEmpty()
            && this.subResourceLocator == null;
      }

      public Resource.Builder name(String name) {
         this.names = new ArrayList<>();
         this.names.add(name);
         return this;
      }

      public Resource.Builder path(String path) {
         this.path = path;
         return this;
      }

      public ResourceMethod.Builder addMethod(String httpMethod) {
         ResourceMethod.Builder builder = new ResourceMethod.Builder(this);
         this.methodBuilders.add(builder);
         return builder.httpMethod(httpMethod);
      }

      public ResourceMethod.Builder addMethod() {
         ResourceMethod.Builder builder = new ResourceMethod.Builder(this);
         this.methodBuilders.add(builder);
         return builder;
      }

      public ResourceMethod.Builder addMethod(ResourceMethod resourceMethod) {
         ResourceMethod.Builder builder = new ResourceMethod.Builder(this, resourceMethod);
         this.methodBuilders.add(builder);
         return builder;
      }

      public ResourceMethod.Builder updateMethod(ResourceMethod resourceMethod) {
         boolean removed = this.resourceMethods.remove(resourceMethod.getData());
         if (!removed) {
            throw new IllegalArgumentException(LocalizationMessages.RESOURCE_UPDATED_METHOD_DOES_NOT_EXIST(resourceMethod.toString()));
         } else {
            ResourceMethod.Builder builder = new ResourceMethod.Builder(this, resourceMethod);
            this.methodBuilders.add(builder);
            return builder;
         }
      }

      public Resource.Builder addChildResource(String relativePath) {
         if (this.parentResource != null) {
            throw new IllegalStateException(LocalizationMessages.RESOURCE_ADD_CHILD_ALREADY_CHILD());
         } else {
            Resource.Builder resourceBuilder = new Resource.Builder(relativePath, this);
            this.childResourceBuilders.add(resourceBuilder);
            return resourceBuilder;
         }
      }

      public void addChildResource(Resource resource) {
         this.childResources.add(resource.data);
      }

      public void replaceChildResource(Resource replacedResource, Resource newResource) {
         boolean removed = this.childResources.remove(replacedResource.data);
         if (!removed) {
            throw new IllegalArgumentException(LocalizationMessages.RESOURCE_REPLACED_CHILD_DOES_NOT_EXIST(replacedResource.toString()));
         } else {
            this.addChildResource(newResource);
         }
      }

      public Resource.Builder mergeWith(Resource resource) {
         this.mergeWith(resource.data);
         return this;
      }

      public Resource.Builder extended(boolean extended) {
         this.extended = extended;
         return this;
      }

      boolean isExtended() {
         return this.extended;
      }

      private Resource.Builder mergeWith(final Resource.Data resourceData) {
         this.resourceMethods.addAll(resourceData.resourceMethods);
         this.childResources.addAll(resourceData.childResources);
         if (this.subResourceLocator != null && resourceData.subResourceLocator != null) {
            Errors.processWithException(new Runnable() {
               @Override
               public void run() {
                  Errors.error(this, LocalizationMessages.RESOURCE_MERGE_CONFLICT_LOCATORS(Builder.this, resourceData, Builder.this.path), Severity.FATAL);
               }
            });
         } else if (resourceData.subResourceLocator != null) {
            this.subResourceLocator = resourceData.subResourceLocator;
         }

         this.handlerClasses.addAll(resourceData.handlerClasses);
         this.handlerInstances.addAll(resourceData.handlerInstances);
         this.names.addAll(resourceData.names);
         return this;
      }

      public Resource.Builder mergeWith(final Resource.Builder resourceBuilder) {
         resourceBuilder.processMethodBuilders();
         this.resourceMethods.addAll(resourceBuilder.resourceMethods);
         this.childResources.addAll(resourceBuilder.childResources);
         if (this.subResourceLocator != null && resourceBuilder.subResourceLocator != null) {
            Errors.processWithException(new Runnable() {
               @Override
               public void run() {
                  Errors.warning(this, LocalizationMessages.RESOURCE_MERGE_CONFLICT_LOCATORS(Builder.this, resourceBuilder, Builder.this.path));
               }
            });
         } else if (resourceBuilder.subResourceLocator != null) {
            this.subResourceLocator = resourceBuilder.subResourceLocator;
         }

         this.handlerClasses.addAll(resourceBuilder.handlerClasses);
         this.handlerInstances.addAll(resourceBuilder.handlerInstances);
         this.names.addAll(resourceBuilder.names);
         return this;
      }

      void onBuildMethod(ResourceMethod.Builder builder, ResourceMethod.Data methodData) {
         Preconditions.checkState(
            this.methodBuilders.remove(builder),
            "Resource.Builder.onBuildMethod() invoked from a resource method builder that is not registered in the resource builder instance."
         );
         switch (methodData.getType()) {
            case RESOURCE_METHOD:
               this.resourceMethods.add(methodData);
               break;
            case SUB_RESOURCE_LOCATOR:
               if (this.subResourceLocator != null) {
                  Errors.processWithException(new Runnable() {
                     @Override
                     public void run() {
                        Errors.error(this, LocalizationMessages.AMBIGUOUS_SRLS(this, Builder.this.path), Severity.FATAL);
                     }
                  });
               }

               this.subResourceLocator = methodData;
         }

         MethodHandler methodHandler = methodData.getInvocable().getHandler();
         if (methodHandler.isClassBased()) {
            this.handlerClasses.add(methodHandler.getHandlerClass());
         } else {
            this.handlerInstances.add(methodHandler.getHandlerInstance());
         }
      }

      private void onBuildChildResource(Resource.Builder childResourceBuilder, Resource.Data childResourceData) {
         Preconditions.checkState(
            this.childResourceBuilders.remove(childResourceBuilder),
            "Resource.Builder.onBuildChildResource() invoked from a resource builder that is not registered in the resource builder instance as a child resource builder."
         );
         this.childResources.add(childResourceData);
      }

      private static List<Resource.Data> mergeResources(List<Resource.Data> resources) {
         MultivaluedMap<String, Resource.Data> resourceData = new MultivaluedHashMap();
         List<Resource.Data> mergedResources = new ArrayList<>();
         Iterator<Resource.Data> resourcesIterator = resources.iterator();

         while (resourcesIterator.hasNext()) {
            Resource.Data data = resourcesIterator.next();
            if (resourceData.containsKey(data.path)) {
               resourcesIterator.remove();
            }

            resourceData.add(data.path, data);
         }

         for (Entry<String, List<Resource.Data>> entry : resourceData.entrySet()) {
            if (entry.getValue().size() == 1) {
               mergedResources.add(entry.getValue().get(0));
            } else {
               Resource.Builder builder = null;

               for (Resource.Data data : entry.getValue()) {
                  if (builder != null) {
                     builder.mergeWith(data);
                  } else {
                     builder = Resource.builder(data);
                  }
               }

               mergedResources.add(builder.buildResourceData());
            }
         }

         return mergedResources;
      }

      private Resource.Data buildResourceData() {
         if (this.parentResource != null && this.parentResource.isExtended()) {
            this.extended = true;
         }

         this.processMethodBuilders();
         this.processChildResourceBuilders();
         List<Resource.Data> mergedChildResources = mergeResources(this.childResources);
         Set<Class<?>> classes = new HashSet<>(this.handlerClasses);
         Set<Object> instances = new HashSet<>(this.handlerInstances);

         for (Resource.Data childResource : mergedChildResources) {
            classes.addAll(childResource.handlerClasses);
            instances.addAll(childResource.handlerInstances);
         }

         if (this.areAllMembersExtended(mergedChildResources)) {
            this.extended = true;
         }

         Resource.Data resourceData = new Resource.Data(
            this.names, this.path, this.resourceMethods, this.subResourceLocator, mergedChildResources, classes, instances, this.extended
         );
         if (this.parentResource != null) {
            this.parentResource.onBuildChildResource(this, resourceData);
         }

         return resourceData;
      }

      private boolean areAllMembersExtended(List<Resource.Data> mergedChildResources) {
         boolean allExtended = true;

         for (ResourceMethod.Data resourceMethod : this.resourceMethods) {
            if (!resourceMethod.isExtended()) {
               allExtended = false;
            }
         }

         if (this.subResourceLocator != null && !this.subResourceLocator.isExtended()) {
            allExtended = false;
         }

         for (Resource.Data childResource : mergedChildResources) {
            if (!childResource.extended) {
               allExtended = false;
            }
         }

         return allExtended;
      }

      public Resource build() {
         Resource.Data resourceData = this.buildResourceData();
         return new Resource(null, resourceData);
      }

      private void processMethodBuilders() {
         while (!this.methodBuilders.isEmpty()) {
            this.methodBuilders.iterator().next().build();
         }
      }

      private void processChildResourceBuilders() {
         while (!this.childResourceBuilders.isEmpty()) {
            this.childResourceBuilders.iterator().next().build();
         }
      }

      @Override
      public String toString() {
         return "Builder{names="
            + this.names
            + ", path='"
            + this.path
            + '\''
            + ", methodBuilders="
            + this.methodBuilders
            + ", childResourceBuilders="
            + this.childResourceBuilders
            + ", childResources="
            + this.childResources
            + ", resourceMethods="
            + this.resourceMethods
            + ", subResourceLocator="
            + this.subResourceLocator
            + ", handlerClasses="
            + this.handlerClasses
            + ", handlerInstances="
            + this.handlerInstances
            + ", parentResource="
            + (this.parentResource == null ? "<no parent>" : this.parentResource.shortToString())
            + ", extended="
            + this.extended
            + '}';
      }

      private String shortToString() {
         return "Builder{names=" + this.names + ", path='" + this.path + "'}";
      }
   }

   private static class Data {
      private final List<String> names;
      private final String path;
      private final PathPattern pathPattern;
      private final List<ResourceMethod.Data> resourceMethods;
      private final ResourceMethod.Data subResourceLocator;
      private final List<Resource.Data> childResources;
      private final Set<Class<?>> handlerClasses;
      private final Set<Object> handlerInstances;
      private final boolean extended;

      private Data(
         List<String> names,
         String path,
         List<ResourceMethod.Data> resourceMethods,
         ResourceMethod.Data subResourceLocator,
         List<Resource.Data> childResources,
         Set<Class<?>> handlerClasses,
         Set<Object> handlerInstances,
         boolean extended
      ) {
         this.extended = extended;
         this.names = Resource.immutableCopy(names);
         this.path = path;
         this.pathPattern = path != null && !path.isEmpty()
            ? new PathPattern(path, RightHandPath.capturingZeroOrMoreSegments)
            : PathPattern.OPEN_ROOT_PATH_PATTERN;
         this.resourceMethods = Resource.immutableCopy(resourceMethods);
         this.subResourceLocator = subResourceLocator;
         this.childResources = Collections.unmodifiableList(childResources);
         this.handlerClasses = Resource.immutableCopy(handlerClasses);
         this.handlerInstances = Resource.immutableCopy(handlerInstances);
      }

      @Override
      public String toString() {
         return "Resource{"
            + (this.path == null ? "[unbound], " : "\"" + this.path + "\", ")
            + this.childResources.size()
            + " child resources, "
            + this.resourceMethods.size()
            + " resource methods, "
            + (this.subResourceLocator == null ? "0" : "1")
            + " sub-resource locator, "
            + this.handlerClasses.size()
            + " method handler classes, "
            + this.handlerInstances.size()
            + " method handler instances"
            + '}';
      }
   }
}
