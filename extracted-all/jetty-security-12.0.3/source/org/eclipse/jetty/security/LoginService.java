package org.eclipse.jetty.security;

import java.security.Principal;
import java.util.function.Function;
import javax.security.auth.Subject;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Session;

public interface LoginService {
   String getName();

   UserIdentity login(String var1, Object var2, Request var3, Function<Boolean, Session> var4);

   default UserIdentity getUserIdentity(Subject subject, Principal userPrincipal, boolean create) {
      UserIdentity userIdentity = this.login(userPrincipal.getName(), "", null, b -> null);
      if (userIdentity != null) {
         return new RoleDelegateUserIdentity(subject, userPrincipal, userIdentity);
      } else {
         return create && this.getIdentityService() != null ? this.getIdentityService().newUserIdentity(subject, userPrincipal, new String[0]) : null;
      }
   }

   boolean validate(UserIdentity var1);

   IdentityService getIdentityService();

   void setIdentityService(IdentityService var1);

   void logout(UserIdentity var1);
}
