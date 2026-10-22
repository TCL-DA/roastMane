package org.glassfish.jersey.server.filter;

import jakarta.annotation.Priority;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.FeatureContext;
import java.io.IOException;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.model.AnnotatedMethod;

public class RolesAllowedDynamicFeature implements DynamicFeature {
   public void configure(ResourceInfo resourceInfo, FeatureContext configuration) {
      AnnotatedMethod am = new AnnotatedMethod(resourceInfo.getResourceMethod());
      if (am.isAnnotationPresent(DenyAll.class)) {
         configuration.register(new RolesAllowedDynamicFeature.RolesAllowedRequestFilter());
      } else {
         RolesAllowed ra = (RolesAllowed)am.getAnnotation(RolesAllowed.class);
         if (ra != null) {
            configuration.register(new RolesAllowedDynamicFeature.RolesAllowedRequestFilter(ra.value()));
         } else if (!am.isAnnotationPresent(PermitAll.class)) {
            ra = resourceInfo.getResourceClass().getAnnotation(RolesAllowed.class);
            if (ra != null) {
               configuration.register(new RolesAllowedDynamicFeature.RolesAllowedRequestFilter(ra.value()));
            }
         }
      }
   }

   @Priority(2000)
   private static class RolesAllowedRequestFilter implements ContainerRequestFilter {
      private final boolean denyAll;
      private final String[] rolesAllowed;

      RolesAllowedRequestFilter() {
         this.denyAll = true;
         this.rolesAllowed = null;
      }

      RolesAllowedRequestFilter(String[] rolesAllowed) {
         this.denyAll = false;
         this.rolesAllowed = rolesAllowed != null ? rolesAllowed : new String[0];
      }

      public void filter(ContainerRequestContext requestContext) throws IOException {
         if (!this.denyAll) {
            if (this.rolesAllowed.length > 0 && !isAuthenticated(requestContext)) {
               throw new ForbiddenException(LocalizationMessages.USER_NOT_AUTHORIZED());
            }

            for (String role : this.rolesAllowed) {
               if (requestContext.getSecurityContext().isUserInRole(role)) {
                  return;
               }
            }
         }

         throw new ForbiddenException(LocalizationMessages.USER_NOT_AUTHORIZED());
      }

      private static boolean isAuthenticated(ContainerRequestContext requestContext) {
         return requestContext.getSecurityContext().getUserPrincipal() != null;
      }
   }
}
