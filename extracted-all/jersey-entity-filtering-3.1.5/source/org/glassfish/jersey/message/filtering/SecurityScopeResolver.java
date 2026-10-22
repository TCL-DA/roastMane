package org.glassfish.jersey.message.filtering;

import jakarta.inject.Singleton;
import java.lang.annotation.Annotation;
import java.util.Set;
import org.glassfish.jersey.message.filtering.spi.ScopeResolver;

@Singleton
final class SecurityScopeResolver implements ScopeResolver {
   @Override
   public Set<String> resolve(Annotation[] annotations) {
      return SecurityHelper.getFilteringScopes(annotations);
   }
}
