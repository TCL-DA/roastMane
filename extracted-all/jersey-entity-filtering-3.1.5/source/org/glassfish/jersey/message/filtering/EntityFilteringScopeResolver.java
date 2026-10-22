package org.glassfish.jersey.message.filtering;

import jakarta.inject.Singleton;
import java.lang.annotation.Annotation;
import java.util.Set;
import org.glassfish.jersey.message.filtering.spi.ScopeResolver;

@Singleton
final class EntityFilteringScopeResolver implements ScopeResolver {
   @Override
   public Set<String> resolve(Annotation[] annotations) {
      return EntityFilteringHelper.getFilteringScopes(annotations);
   }
}
