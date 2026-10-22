package org.glassfish.jersey.message.filtering;

import jakarta.annotation.Priority;
import jakarta.inject.Singleton;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.message.filtering.spi.AbstractEntityProcessor;
import org.glassfish.jersey.message.filtering.spi.EntityGraph;
import org.glassfish.jersey.message.filtering.spi.EntityProcessor;
import org.glassfish.jersey.message.filtering.spi.EntityProcessorContext;
import org.glassfish.jersey.message.filtering.spi.FilteringHelper;

@Singleton
@Priority(2147482647)
final class DefaultEntityProcessor extends AbstractEntityProcessor {
   @Override
   public EntityProcessor.Result process(EntityProcessorContext context) {
      switch (context.getType()) {
         case CLASS_READER:
         case CLASS_WRITER:
            EntityGraph graph = context.getEntityGraph();
            if (graph.getFilteringScopes().isEmpty()) {
               graph.addFilteringScopes(FilteringHelper.getDefaultFilteringScope());
            }

            return EntityProcessor.Result.APPLY;
         case PROPERTY_READER:
         case PROPERTY_WRITER:
            Field field = context.getField();
            this.process(context.getEntityGraph(), field.getName(), field.getGenericType());
            return EntityProcessor.Result.APPLY;
         case METHOD_READER:
         case METHOD_WRITER:
            Method method = context.getMethod();
            this.process(context.getEntityGraph(), ReflectionHelper.getPropertyName(method), method.getGenericReturnType());
            return EntityProcessor.Result.APPLY;
         default:
            return EntityProcessor.Result.SKIP;
      }
   }

   private void process(EntityGraph graph, String fieldName, Type fieldType) {
      if (!graph.presentInScopes(fieldName)) {
         this.addFilteringScopes(fieldName, FilteringHelper.getEntityClass(fieldType), graph.getClassFilteringScopes(), graph);
      }
   }
}
