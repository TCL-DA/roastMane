package org.eclipse.jetty.security;

import java.util.Set;
import java.util.function.Function;
import org.eclipse.jetty.server.Context;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Session;
import org.eclipse.jetty.util.Callback;

public interface Authenticator {
   String BASIC_AUTH = "BASIC";
   String FORM_AUTH = "FORM";
   String DIGEST_AUTH = "DIGEST";
   String CERT_AUTH = "CLIENT_CERT";
   String CERT_AUTH2 = "CLIENT-CERT";
   String SPNEGO_AUTH = "SPNEGO";
   String NEGOTIATE_AUTH = "NEGOTIATE";
   String OPENID_AUTH = "OPENID";

   void setConfiguration(Authenticator.Configuration var1);

   String getAuthenticationType();

   default Request prepareRequest(Request request, AuthenticationState authenticationState) {
      return request;
   }

   default Constraint.Authorization getConstraintAuthentication(String pathInContext, Constraint.Authorization existing, Function<Boolean, Session> getSession) {
      return existing == null ? Constraint.Authorization.ALLOWED : existing;
   }

   AuthenticationState validateRequest(Request var1, Response var2, Callback var3) throws ServerAuthException;

   public interface Configuration {
      String getAuthenticationType();

      String getRealmName();

      String getParameter(String var1);

      Set<String> getParameterNames();

      LoginService getLoginService();

      IdentityService getIdentityService();

      boolean isSessionRenewedOnAuthentication();

      int getSessionMaxInactiveIntervalOnAuthentication();

      public static class Wrapper implements Authenticator.Configuration {
         private final Authenticator.Configuration _configuration;

         public Wrapper(Authenticator.Configuration configuration) {
            this._configuration = configuration;
         }

         @Override
         public String getAuthenticationType() {
            return this._configuration.getAuthenticationType();
         }

         @Override
         public String getRealmName() {
            return this._configuration.getRealmName();
         }

         @Override
         public String getParameter(String param) {
            return this._configuration.getParameter(param);
         }

         @Override
         public Set<String> getParameterNames() {
            return this._configuration.getParameterNames();
         }

         @Override
         public LoginService getLoginService() {
            return this._configuration.getLoginService();
         }

         @Override
         public IdentityService getIdentityService() {
            return this._configuration.getIdentityService();
         }

         @Override
         public boolean isSessionRenewedOnAuthentication() {
            return this._configuration.isSessionRenewedOnAuthentication();
         }

         @Override
         public int getSessionMaxInactiveIntervalOnAuthentication() {
            return this._configuration.getSessionMaxInactiveIntervalOnAuthentication();
         }
      }
   }

   public interface Factory {
      Authenticator getAuthenticator(Server var1, Context var2, Authenticator.Configuration var3);
   }

   public static class NoOp implements Authenticator {
      @Override
      public void setConfiguration(Authenticator.Configuration configuration) {
      }

      @Override
      public String getAuthenticationType() {
         return null;
      }

      @Override
      public AuthenticationState validateRequest(Request request, Response response, Callback callback) throws ServerAuthException {
         return null;
      }
   }
}
