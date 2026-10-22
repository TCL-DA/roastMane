package org.eclipse.jetty.security.jaas;

public class JAASRole extends JAASPrincipal {
   private static final long serialVersionUID = 3465114254970134526L;

   public JAASRole(String name) {
      super(name);
   }

   @Override
   public boolean equals(Object o) {
      return !(o instanceof JAASRole) ? false : this.getName().equals(((JAASRole)o).getName());
   }
}
