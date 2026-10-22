package org.glassfish.jersey.message.filtering;

import jakarta.annotation.Priority;
import jakarta.inject.Singleton;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import org.glassfish.jersey.message.filtering.spi.AbstractEntityProcessor;
import org.glassfish.jersey.message.filtering.spi.EntityGraph;
import org.glassfish.jersey.message.filtering.spi.EntityProcessor;
import org.glassfish.jersey.message.filtering.spi.EntityProcessorContext;

@Singleton
@Priority(2147481647)
final class EntityFilteringProcessor extends AbstractEntityProcessor {
   @Override
   public EntityProcessor.Result process(EntityProcessorContext context) {
      switch (context.getType()) {
         case CLASS_READER:
         case CLASS_WRITER:
            this.addGlobalScopes(EntityFilteringHelper.getFilteringScopes(context.getEntityClass().getDeclaredAnnotations()), context.getEntityGraph());
         default:
            return super.process(context);
      }
   }

   @Override
   protected EntityProcessor.Result process(String field, Class<?> fieldClass, Annotation[] fieldAnnotations, Annotation[] annotations, EntityGraph graph) {
      Set<String> filteringScopes = new HashSet<>();
      if (fieldAnnotations.length > 0) {
         filteringScopes.addAll(EntityFilteringHelper.getFilteringScopes(fieldAnnotations));
      }

      if (annotations.length > 0) {
         filteringScopes.addAll(EntityFilteringHelper.getFilteringScopes(annotations));
      }

      if (!filteringScopes.isEmpty()) {
         if (field != null) {
            this.addFilteringScopes(field, fieldClass, filteringScopes, graph);
         } else {
            this.addGlobalScopes(filteringScopes, graph);
         }

         return EntityProcessor.Result.APPLY;
      } else {
         return EntityProcessor.Result.SKIP;
      }
   }
}
