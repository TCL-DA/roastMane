package org.glassfish.jersey.message.filtering;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import org.glassfish.jersey.internal.inject.InjectionManager;

@Singleton
@ConstrainedTo(RuntimeType.SERVER)
final class SecurityServerScopeProvider extends ServerScopeProvider {
   @Context
   private SecurityContext securityContext;

   @Inject
   public SecurityServerScopeProvider(Configuration config, InjectionManager injectionManager) {
      super(config, injectionManager);
   }

   @Override
   public Set<String> getFilteringScopes(Annotation[] entityAnnotations, boolean defaultIfNotFound) {
      Set<String> filteringScope = super.getFilteringScopes(entityAnnotations, false);
      if (filteringScope.isEmpty()) {
         filteringScope = new HashSet<>();

         for (String role : SecurityHelper.getProcessedRoles()) {
            if (this.securityContext.isUserInRole(role)) {
               filteringScope.add(SecurityHelper.getRolesAllowedScope(role));
            }
         }
      }

      return this.returnFilteringScopes(filteringScope, defaultIfNotFound);
   }
}
