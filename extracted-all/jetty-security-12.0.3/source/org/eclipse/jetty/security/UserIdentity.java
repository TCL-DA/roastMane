package org.eclipse.jetty.security;

import java.security.Principal;
import javax.security.auth.Subject;
import org.eclipse.jetty.security.internal.DefaultUserIdentity;

public interface UserIdentity {
   Subject getSubject();

   Principal getUserPrincipal();

   boolean isUserInRole(String var1);

   static UserIdentity from(Subject subject, Principal userPrincipal, String... roles) {
      return new DefaultUserIdentity(subject, userPrincipal, roles);
   }
}
