package org.eclipse.jetty.security;

import java.io.Serializable;
import java.security.Principal;
import javax.security.auth.Subject;

public class RolePrincipal implements Principal, Serializable {
   private static final long serialVersionUID = 2998397924051854402L;
   private final String _roleName;

   public RolePrincipal(String name) {
      this._roleName = name;
   }

   @Override
   public String getName() {
      return this._roleName;
   }

   public void configureForSubject(Subject subject) {
      subject.getPrincipals().add(this);
   }
}
