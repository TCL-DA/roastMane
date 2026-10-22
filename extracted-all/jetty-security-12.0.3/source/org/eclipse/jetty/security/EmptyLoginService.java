package org.eclipse.jetty.security;

import java.util.function.Function;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Session;

public class EmptyLoginService implements LoginService {
   @Override
   public String getName() {
      return null;
   }

   @Override
   public UserIdentity login(String username, Object credentials, Request request, Function<Boolean, Session> getOrCreateSession) {
      return null;
   }

   @Override
   public boolean validate(UserIdentity user) {
      return false;
   }

   @Override
   public IdentityService getIdentityService() {
      return null;
   }

   @Override
   public void setIdentityService(IdentityService service) {
   }

   @Override
   public void logout(UserIdentity user) {
   }
}
