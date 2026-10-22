package org.glassfish.jersey.server.internal.process;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.SecurityContext;
import java.security.Principal;
import org.glassfish.jersey.server.internal.LocalizationMessages;

class SecurityContextInjectee implements SecurityContext {
   private final ContainerRequestContext requestContext;

   @Inject
   public SecurityContextInjectee(ContainerRequestContext requestContext) {
      this.requestContext = requestContext;
   }

   public Principal getUserPrincipal() {
      this.checkState();
      return this.requestContext.getSecurityContext().getUserPrincipal();
   }

   public boolean isUserInRole(String role) {
      this.checkState();
      return this.requestContext.getSecurityContext().isUserInRole(role);
   }

   public boolean isSecure() {
      this.checkState();
      return this.requestContext.getSecurityContext().isSecure();
   }

   public String getAuthenticationScheme() {
      this.checkState();
      return this.requestContext.getSecurityContext().getAuthenticationScheme();
   }

   @Override
   public int hashCode() {
      this.checkState();
      return 7 * this.requestContext.getSecurityContext().hashCode();
   }

   @Override
   public boolean equals(Object that) {
      this.checkState();
      return that instanceof SecurityContext && that.equals(this.requestContext.getSecurityContext());
   }

   private void checkState() {
      if (this.requestContext == null) {
         throw new IllegalStateException(LocalizationMessages.SECURITY_CONTEXT_WAS_NOT_SET());
      }
   }
}
