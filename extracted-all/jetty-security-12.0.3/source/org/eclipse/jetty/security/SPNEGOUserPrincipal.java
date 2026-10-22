package org.eclipse.jetty.security;

import java.security.Principal;

public class SPNEGOUserPrincipal implements Principal {
   private final String _name;
   private final String _encodedToken;

   public SPNEGOUserPrincipal(String name, String encodedToken) {
      this._name = name;
      this._encodedToken = encodedToken;
   }

   @Override
   public String getName() {
      return this._name;
   }

   public String getEncodedToken() {
      return this._encodedToken;
   }
}
