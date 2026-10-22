package org.glassfish.jersey.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.glassfish.jersey.server.model.Resource;

final class ResourceBag {
   final Set<Class<?>> classes;
   final Set<Object> instances;
   final List<Resource> models;

   private ResourceBag(Set<Class<?>> classes, Set<Object> instances, List<Resource> models) {
      this.classes = classes;
      this.instances = instances;
      this.models = models;
   }

   List<Resource> getRootResources() {
      List<Resource> rootResources = new ArrayList<>();

      for (Resource resource : this.models) {
         if (resource.getPath() != null) {
            rootResources.add(resource);
         }
      }

      return rootResources;
   }

   public static final class Builder {
      private final Set<Class<?>> classes = Collections.newSetFromMap(new IdentityHashMap<>());
      private final Set<Object> instances = Collections.newSetFromMap(new IdentityHashMap<>());
      private final List<Resource> models = new LinkedList<>();
      private final Map<String, Resource> rootResourceMap = new HashMap<>();

      void registerResource(Class<?> resourceClass, Resource resourceModel) {
         this.registerModel(resourceModel);
         this.classes.add(resourceClass);
      }

      void registerResource(Object resourceInstance, Resource resourceModel) {
         this.registerModel(resourceModel);
         this.instances.add(resourceInstance);
      }

      void registerProgrammaticResource(Resource resourceModel) {
         this.registerModel(resourceModel);
         this.classes.addAll(resourceModel.getHandlerClasses());
         this.instances.addAll(resourceModel.getHandlerInstances());
      }

      private void registerModel(Resource resourceModel) {
         String path = resourceModel.getPath();
         if (path != null) {
            Resource existing = this.rootResourceMap.get(path);
            if (existing != null) {
               existing = Resource.builder(existing).mergeWith(resourceModel).build();
               this.rootResourceMap.put(path, existing);
            } else {
               this.rootResourceMap.put(path, resourceModel);
            }
         } else {
            this.models.add(resourceModel);
         }
      }

      ResourceBag build() {
         this.models.addAll(this.rootResourceMap.values());
         return new ResourceBag(this.classes, this.instances, this.models);
      }
   }
}
