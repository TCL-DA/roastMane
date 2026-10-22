package org.eclipse.jetty.security;

import java.security.Principal;
import javax.security.auth.Subject;

public class RoleDelegateUserIdentity implements UserIdentity {
   private final Subject _subject;
   private final Principal _principal;
   private final UserIdentity _roleDelegate;

   public RoleDelegateUserIdentity(Subject subject, Principal principal, UserIdentity roleDelegate) {
      this._subject = subject;
      this._principal = principal;
      this._roleDelegate = roleDelegate;
   }

   @Override
   public Subject getSubject() {
      return this._subject;
   }

   @Override
   public Principal getUserPrincipal() {
      return this._principal;
   }

   @Override
   public boolean isUserInRole(String role) {
      return this._roleDelegate != null && this._roleDelegate.isUserInRole(role);
   }

   public boolean isEstablished() {
      return this._roleDelegate != null;
   }
}
