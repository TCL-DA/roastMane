package org.eclipse.jetty.security.internal;

import org.eclipse.jetty.security.IdentityService;

public class RoleRunAsToken implements IdentityService.RunAsToken {
   private final String _runAsRole;

   public RoleRunAsToken(String runAsRole) {
      this._runAsRole = runAsRole;
   }

   public String getRunAsRole() {
      return this._runAsRole;
   }

   @Override
   public String toString() {
      return "RoleRunAsToken(" + this._runAsRole + ")";
   }
}
