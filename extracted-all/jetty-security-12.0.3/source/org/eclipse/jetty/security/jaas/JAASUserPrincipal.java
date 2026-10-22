package org.eclipse.jetty.security.jaas;

import java.security.Principal;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;

public class JAASUserPrincipal implements Principal {
   private final String _name;
   private final Subject _subject;
   private final LoginContext _loginContext;

   public JAASUserPrincipal(String name, Subject subject, LoginContext loginContext) {
      this._name = name;
      this._subject = subject;
      this._loginContext = loginContext;
   }

   @Override
   public String getName() {
      return this._name;
   }

   public Subject getSubject() {
      return this._subject;
   }

   LoginContext getLoginContext() {
      return this._loginContext;
   }

   @Override
   public String toString() {
      return this.getName();
   }
}
