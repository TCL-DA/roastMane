package org.glassfish.jersey.message.filtering;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.core.Context;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.Providers;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.message.filtering.spi.EntityGraph;
import org.glassfish.jersey.message.filtering.spi.EntityGraphProvider;
import org.glassfish.jersey.message.filtering.spi.EntityInspector;
import org.glassfish.jersey.message.filtering.spi.EntityProcessor;
import org.glassfish.jersey.message.filtering.spi.EntityProcessorContext;
import org.glassfish.jersey.message.filtering.spi.FilteringHelper;
import org.glassfish.jersey.model.internal.RankedComparator;

@Singleton
final class EntityInspectorImpl implements EntityInspector {
   private final List<EntityProcessor> entityProcessors;
   private final EntityGraphProvider graphProvider;

   @Inject
   public EntityInspectorImpl(@Context InjectionManager injectionManager, @Context EntityGraphProvider graphProvider) {
      Spliterator<EntityProcessor> entities = Providers.getAllProviders(injectionManager, EntityProcessor.class, new RankedComparator()).spliterator();
      this.entityProcessors = StreamSupport.stream(entities, false).collect(Collectors.toList());
      this.graphProvider = graphProvider;
   }

   @Override
   public void inspect(Class<?> entityClass, boolean forWriter) {
      if (!this.graphProvider.containsEntityGraph(entityClass, forWriter)) {
         EntityGraph graph = new EntityGraphImpl(entityClass);
         Set<Class<?>> inspect = new HashSet<>();
         if (!this.inspectEntityClass(entityClass, graph, forWriter)) {
            Map<String, Method> unmatchedAccessors = this.inspectEntityProperties(entityClass, graph, inspect, forWriter);
            this.inspectStandaloneAccessors(unmatchedAccessors, graph, forWriter);
            this.graphProvider.putIfAbsent(entityClass, graph, forWriter);

            for (Class<?> clazz : inspect) {
               this.inspect(clazz, forWriter);
            }
         } else {
            this.graphProvider.putIfAbsent(entityClass, graph, forWriter);
         }
      }
   }

   private boolean inspectEntityClass(Class<?> entityClass, EntityGraph graph, boolean forWriter) {
      EntityProcessorContextImpl context = new EntityProcessorContextImpl(
         forWriter ? EntityProcessorContext.Type.CLASS_WRITER : EntityProcessorContext.Type.CLASS_READER, entityClass, graph
      );

      for (EntityProcessor processor : this.entityProcessors) {
         EntityProcessor.Result result = processor.process(context);
         if (EntityProcessor.Result.ROLLBACK == result) {
            this.graphProvider.getOrCreateEmptyEntityGraph(entityClass, false);
            return true;
         }
      }

      return false;
   }

   private Map<String, Method> inspectEntityProperties(Class<?> entityClass, EntityGraph graph, Set<Class<?>> inspect, boolean forWriter) {
      Field[] fields = AccessController.doPrivileged(ReflectionHelper.getAllFieldsPA(entityClass));
      Map<String, Method> methods = FilteringHelper.getPropertyMethods(entityClass, forWriter);

      for (Field field : fields) {
         if (!Modifier.isStatic(field.getModifiers())) {
            String name = field.getName();
            Class<?> clazz = FilteringHelper.getEntityClass(field.getGenericType());
            Method method = methods.remove(name);
            EntityProcessorContextImpl context = new EntityProcessorContextImpl(
               forWriter ? EntityProcessorContext.Type.PROPERTY_WRITER : EntityProcessorContext.Type.PROPERTY_READER, field, method, graph
            );
            boolean rollback = false;

            for (EntityProcessor processor : this.entityProcessors) {
               EntityProcessor.Result result = processor.process(context);
               if (EntityProcessor.Result.ROLLBACK == result) {
                  rollback = true;
                  graph.remove(name);
                  break;
               }
            }

            if (!rollback && FilteringHelper.filterableEntityClass(clazz)) {
               inspect.add(clazz);
            }
         }
      }

      return methods;
   }

   private void inspectStandaloneAccessors(Map<String, Method> unprocessedAccessors, EntityGraph graph, boolean forWriter) {
      for (Entry<String, Method> entry : unprocessedAccessors.entrySet()) {
         EntityProcessorContextImpl context = new EntityProcessorContextImpl(
            forWriter ? EntityProcessorContext.Type.METHOD_WRITER : EntityProcessorContext.Type.METHOD_READER, entry.getValue(), graph
         );

         for (EntityProcessor processor : this.entityProcessors) {
            EntityProcessor.Result result = processor.process(context);
            if (EntityProcessor.Result.ROLLBACK == result) {
               graph.remove(entry.getKey());
               break;
            }
         }
      }
   }
}
