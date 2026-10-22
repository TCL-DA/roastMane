package org.glassfish.jersey.message.filtering;

import jakarta.annotation.Priority;
import jakarta.inject.Singleton;
import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import java.lang.annotation.Annotation;
import java.util.Set;
import org.glassfish.jersey.message.filtering.spi.ScopeResolver;

@Singleton
@Priority(4100)
@ConstrainedTo(RuntimeType.SERVER)
final class SecurityServerScopeResolver implements ScopeResolver {
   @Context
   private SecurityContext securityContext;

   @Override
   public Set<String> resolve(Annotation[] annotations) {
      return SecurityHelper.getFilteringScopes(this.securityContext, annotations);
   }
}
