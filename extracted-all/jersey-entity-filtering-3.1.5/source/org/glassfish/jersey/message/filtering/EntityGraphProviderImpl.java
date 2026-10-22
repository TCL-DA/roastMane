package org.glassfish.jersey.message.filtering;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.glassfish.jersey.message.filtering.spi.EntityGraph;
import org.glassfish.jersey.message.filtering.spi.EntityGraphProvider;
import org.glassfish.jersey.message.filtering.spi.ObjectGraph;

final class EntityGraphProviderImpl implements EntityGraphProvider {
   private final ConcurrentMap<Class<?>, EntityGraph> writerClassToGraph = new ConcurrentHashMap<>();
   private final ConcurrentMap<Class<?>, EntityGraph> readerClassToGraph = new ConcurrentHashMap<>();

   @Override
   public EntityGraph getOrCreateEntityGraph(Class<?> entityClass, boolean forWriter) {
      ConcurrentMap<Class<?>, EntityGraph> classToGraph = forWriter ? this.writerClassToGraph : this.readerClassToGraph;
      if (!classToGraph.containsKey(entityClass)) {
         classToGraph.putIfAbsent(entityClass, new EntityGraphImpl(entityClass));
      }

      return classToGraph.get(entityClass);
   }

   @Override
   public EntityGraph getOrCreateEmptyEntityGraph(Class<?> entityClass, boolean forWriter) {
      ConcurrentMap<Class<?>, EntityGraph> classToGraph = forWriter ? this.writerClassToGraph : this.readerClassToGraph;
      if (!classToGraph.containsKey(entityClass) || !(classToGraph.get(entityClass) instanceof EmptyEntityGraphImpl)) {
         classToGraph.put(entityClass, new EmptyEntityGraphImpl(entityClass));
      }

      return classToGraph.get(entityClass);
   }

   public Map<Class<?>, EntityGraph> asMap(boolean forWriter) {
      return Collections.unmodifiableMap(forWriter ? this.writerClassToGraph : this.readerClassToGraph);
   }

   @Override
   public boolean containsEntityGraph(Class<?> entityClass, boolean forWriter) {
      return forWriter ? this.writerClassToGraph.containsKey(entityClass) : this.readerClassToGraph.containsKey(entityClass);
   }

   @Override
   public ObjectGraph createObjectGraph(Class<?> entityClass, Set<String> filteringScopes, boolean forWriter) {
      Map<Class<?>, EntityGraph> classToGraph = forWriter ? this.writerClassToGraph : this.readerClassToGraph;
      EntityGraph entityGraph = classToGraph.get(entityClass);
      return (ObjectGraph)(entityGraph == null ? new EmptyObjectGraph(entityClass) : new ObjectGraphImpl(classToGraph, entityGraph, filteringScopes));
   }

   @Override
   public EntityGraph putIfAbsent(Class<?> entityClass, EntityGraph entityGraph, boolean forWriter) {
      ConcurrentMap<Class<?>, EntityGraph> classToGraph = forWriter ? this.writerClassToGraph : this.readerClassToGraph;
      return classToGraph.putIfAbsent(entityClass, entityGraph);
   }
}
