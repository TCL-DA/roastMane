package org.eclipse.jetty.client;

import java.net.URI;

public interface AuthenticationStore {
   void addAuthentication(Authentication var1);

   void removeAuthentication(Authentication var1);

   void clearAuthentications();

   Authentication findAuthentication(String var1, URI var2, String var3);

   void addAuthenticationResult(Authentication.Result var1);

   void removeAuthenticationResult(Authentication.Result var1);

   void clearAuthenticationResults();

   Authentication.Result findAuthenticationResult(URI var1);

   default boolean hasAuthenticationResults() {
      return true;
   }
}
