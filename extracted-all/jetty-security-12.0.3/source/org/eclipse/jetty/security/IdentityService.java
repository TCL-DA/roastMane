package org.eclipse.jetty.security;

import java.security.Principal;
import javax.security.auth.Subject;

public interface IdentityService {
   IdentityService.Association associate(UserIdentity var1, IdentityService.RunAsToken var2);

   void onLogout(UserIdentity var1);

   UserIdentity newUserIdentity(Subject var1, Principal var2, String[] var3);

   IdentityService.RunAsToken newRunAsToken(String var1);

   UserIdentity getSystemUserIdentity();

   public interface Association extends AutoCloseable {
      @Override
      void close();
   }

   public interface RunAsToken {
   }
}
