package org.glassfish.jersey.message.filtering;

import jakarta.annotation.Priority;
import jakarta.inject.Singleton;
import java.lang.annotation.Annotation;
import java.util.Set;
import org.glassfish.jersey.message.filtering.spi.AbstractEntityProcessor;
import org.glassfish.jersey.message.filtering.spi.EntityGraph;
import org.glassfish.jersey.message.filtering.spi.EntityProcessor;

@Singleton
@Priority(2147480647)
final class SecurityEntityProcessor extends AbstractEntityProcessor {
   @Override
   protected EntityProcessor.Result process(String fieldName, Class<?> fieldClass, Annotation[] fieldAnnotations, Annotation[] annotations, EntityGraph graph) {
      if (annotations.length > 0) {
         Set<String> filteringScopes = SecurityHelper.getFilteringScopes(annotations);
         if (filteringScopes == null) {
            return EntityProcessor.Result.ROLLBACK;
         }

         if (!filteringScopes.isEmpty()) {
            if (fieldName != null) {
               this.addFilteringScopes(fieldName, fieldClass, filteringScopes, graph);
            } else {
               this.addGlobalScopes(filteringScopes, graph);
            }
         }
      }

      return EntityProcessor.Result.APPLY;
   }
}
