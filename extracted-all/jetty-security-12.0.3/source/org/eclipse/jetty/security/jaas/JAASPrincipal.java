package org.eclipse.jetty.security.jaas;

import java.io.Serializable;
import java.security.Principal;

public class JAASPrincipal implements Principal, Serializable {
   private static final long serialVersionUID = -5538962177019315479L;
   private final String _name;

   public JAASPrincipal(String userName) {
      this._name = userName;
   }

   @Override
   public boolean equals(Object p) {
      return !(p instanceof JAASPrincipal) ? false : this.getName().equals(((JAASPrincipal)p).getName());
   }

   @Override
   public int hashCode() {
      return this.getName().hashCode();
   }

   @Override
   public String getName() {
      return this._name;
   }

   @Override
   public String toString() {
      return this.getName();
   }
}
