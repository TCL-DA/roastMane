package org.glassfish.jersey.message.filtering;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.core.SecurityContext;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.glassfish.jersey.message.filtering.spi.FilteringHelper;

final class SecurityHelper {
   private static final Set<String> roles = new HashSet<>();

   static Set<String> getFilteringScopes(Annotation[] annotations) {
      return getFilteringScopes(null, annotations);
   }

   static Set<String> getFilteringScopes(SecurityContext securityContext, Annotation[] annotations) {
      if (annotations.length == 0) {
         return Collections.emptySet();
      } else {
         for (Annotation annotation : annotations) {
            if (annotation instanceof RolesAllowed) {
               Set<String> bindings = new HashSet<>();

               for (String role : ((RolesAllowed)annotation).value()) {
                  if (securityContext == null || securityContext.isUserInRole(role)) {
                     bindings.add(getRolesAllowedScope(role));
                  }
               }

               return bindings;
            }

            if (annotation instanceof PermitAll) {
               return FilteringHelper.getDefaultFilteringScope();
            }

            if (annotation instanceof DenyAll) {
               return null;
            }
         }

         return Collections.emptySet();
      }
   }

   static String getRolesAllowedScope(String role) {
      roles.add(role);
      return RolesAllowed.class.getName() + "_" + role;
   }

   static Set<String> getProcessedRoles() {
      return roles;
   }

   private SecurityHelper() {
   }
}
