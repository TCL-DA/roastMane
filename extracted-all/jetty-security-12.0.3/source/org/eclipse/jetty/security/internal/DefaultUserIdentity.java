package org.eclipse.jetty.security.internal;

import java.security.Principal;
import javax.security.auth.Subject;
import org.eclipse.jetty.security.DefaultIdentityService;
import org.eclipse.jetty.security.UserIdentity;

public class DefaultUserIdentity implements UserIdentity {
   private final Subject _subject;
   private final Principal _userPrincipal;
   private final String[] _roles;

   public DefaultUserIdentity(Subject subject, Principal userPrincipal, String[] roles) {
      this._subject = subject;
      this._userPrincipal = userPrincipal;
      this._roles = roles;
   }

   @Override
   public Subject getSubject() {
      return this._subject;
   }

   @Override
   public Principal getUserPrincipal() {
      return this._userPrincipal;
   }

   @Override
   public boolean isUserInRole(String role) {
      if (role == null) {
         return false;
      } else if (DefaultIdentityService.isRoleAssociated(role)) {
         return true;
      } else {
         for (String r : this._roles) {
            if (r.equals(role)) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public String toString() {
      return DefaultUserIdentity.class.getSimpleName() + "('" + this._userPrincipal + "')";
   }
}
