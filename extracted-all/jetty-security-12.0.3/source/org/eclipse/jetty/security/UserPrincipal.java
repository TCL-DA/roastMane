package org.eclipse.jetty.security;

import java.io.Serializable;
import java.security.Principal;
import javax.security.auth.Subject;
import org.eclipse.jetty.util.security.Credential;

public class UserPrincipal implements Principal, Serializable {
   private static final long serialVersionUID = -6226920753748399662L;
   private final String _name;
   protected final Credential _credential;

   public UserPrincipal(String name, Credential credential) {
      this._name = name;
      this._credential = credential;
   }

   public boolean authenticate(Object credentials) {
      return this._credential != null && this._credential.check(credentials);
   }

   public boolean authenticate(Credential c) {
      return this._credential != null && c != null && this._credential.equals(c);
   }

   public boolean authenticate(UserPrincipal u) {
      return u != null && this.authenticate(u._credential);
   }

   public void configureSubject(Subject subject) {
      if (subject != null) {
         subject.getPrincipals().add(this);
         if (this._credential != null) {
            subject.getPrivateCredentials().add(this._credential);
         }
      }
   }

   public void deconfigureSubject(Subject subject) {
      if (subject != null) {
         subject.getPrincipals().remove(this);
         if (this._credential != null) {
            subject.getPrivateCredentials().remove(this._credential);
         }
      }
   }

   @Override
   public String getName() {
      return this._name;
   }

   @Override
   public String toString() {
      return this._name;
   }
}
