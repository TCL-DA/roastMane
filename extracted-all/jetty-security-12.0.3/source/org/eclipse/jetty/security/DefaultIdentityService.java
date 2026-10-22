package org.eclipse.jetty.security;

import java.security.Principal;
import javax.security.auth.Subject;
import org.eclipse.jetty.security.internal.DefaultUserIdentity;
import org.eclipse.jetty.security.internal.RoleRunAsToken;

public class DefaultIdentityService implements IdentityService {
   private static final ThreadLocal<String> runAsRole = new ThreadLocal<>();
   private static final IdentityService.Association NOOP = () -> {
   };
   private static final IdentityService.Association CLEAR_RUN_AS = () -> runAsRole.set(null);

   public static boolean isRoleAssociated(String role) {
      return role != null && role.equals(runAsRole.get());
   }

   @Override
   public IdentityService.Association associate(UserIdentity user, IdentityService.RunAsToken runAsToken) {
      if (runAsToken instanceof RoleRunAsToken roleRunAsToken) {
         String oldAssociate = runAsRole.get();
         runAsRole.set(roleRunAsToken.getRunAsRole());
         return oldAssociate == null ? CLEAR_RUN_AS : () -> runAsRole.set(oldAssociate);
      } else {
         return NOOP;
      }
   }

   @Override
   public void onLogout(UserIdentity user) {
      runAsRole.set(null);
   }

   @Override
   public IdentityService.RunAsToken newRunAsToken(String roleName) {
      return new RoleRunAsToken(roleName);
   }

   @Override
   public UserIdentity getSystemUserIdentity() {
      return null;
   }

   @Override
   public UserIdentity newUserIdentity(Subject subject, Principal userPrincipal, String[] roles) {
      return new DefaultUserIdentity(subject, userPrincipal, roles);
   }
}
