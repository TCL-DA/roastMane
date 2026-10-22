package org.glassfish.jersey.message.filtering;

import jakarta.annotation.Priority;
import jakarta.inject.Singleton;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import org.glassfish.jersey.message.filtering.spi.AbstractEntityProcessor;
import org.glassfish.jersey.message.filtering.spi.EntityGraph;
import org.glassfish.jersey.message.filtering.spi.EntityProcessor;

@Singleton
@Priority(2147478647)
public class SelectableEntityProcessor extends AbstractEntityProcessor {
   @Override
   protected EntityProcessor.Result process(String fieldName, Class<?> fieldClass, Annotation[] fieldAnnotations, Annotation[] annotations, EntityGraph graph) {
      if (fieldName != null) {
         Set<String> scopes = new HashSet<>();
         scopes.add(SelectableScopeResolver.DEFAULT_SCOPE);
         scopes.add(SelectableScopeResolver.PREFIX + fieldName);
         this.addFilteringScopes(fieldName, fieldClass, scopes, graph);
      }

      return EntityProcessor.Result.APPLY;
   }
}
