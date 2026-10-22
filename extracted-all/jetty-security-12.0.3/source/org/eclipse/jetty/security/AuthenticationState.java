package org.eclipse.jetty.security;

import java.security.Principal;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.http.HttpException.RuntimeException;
import org.eclipse.jetty.security.authentication.LoginAuthenticator;
import org.eclipse.jetty.security.internal.DeferredAuthenticationState;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;

public interface AuthenticationState extends org.eclipse.jetty.server.Request.AuthenticationState {
   AuthenticationState CHALLENGE = new AuthenticationState.ResponseSent() {
      @Override
      public String toString() {
         return "CHALLENGE";
      }
   };
   AuthenticationState SEND_FAILURE = new AuthenticationState.ResponseSent() {
      @Override
      public String toString() {
         return "FAILURE";
      }
   };
   AuthenticationState SEND_SUCCESS = new AuthenticationState.ResponseSent() {
      @Override
      public String toString() {
         return "SEND_SUCCESS";
      }
   };

   static AuthenticationState getAuthenticationState(Request request) {
      return Request.getAuthenticationState(request) instanceof AuthenticationState authenticationState ? authenticationState : null;
   }

   static void setAuthenticationState(Request request, AuthenticationState authenticationState) {
      Request.setAuthenticationState(request, authenticationState);
   }

   static Principal getUserPrincipal(Request request) {
      AuthenticationState.Succeeded succeeded = authenticate(request);
      return succeeded == null ? null : succeeded.getUserIdentity().getUserPrincipal();
   }

   static AuthenticationState.Succeeded authenticate(Request request) {
      AuthenticationState authenticationState = getAuthenticationState(request);
      if (authenticationState instanceof AuthenticationState.Deferred deferred) {
         authenticationState = deferred.authenticate(request);
      }

      return authenticationState instanceof AuthenticationState.Succeeded ? (AuthenticationState.Succeeded)authenticationState : null;
   }

   static AuthenticationState.Succeeded authenticate(Request request, Response response, Callback callback) {
      AuthenticationState authenticationState = getAuthenticationState(request);
      if (authenticationState instanceof AuthenticationState.Deferred deferred) {
         authenticationState = deferred.authenticate(request, response, callback);
         if (authenticationState instanceof AuthenticationState.ResponseSent) {
            return null;
         }
      }

      if (authenticationState instanceof AuthenticationState.Succeeded) {
         return (AuthenticationState.Succeeded)authenticationState;
      } else {
         Response.writeError(request, response, callback, 403);
         return null;
      }
   }

   static AuthenticationState.Succeeded login(String username, String password, Request request, Response response) {
      AuthenticationState authenticationState = getAuthenticationState(request);
      if (authenticationState instanceof AuthenticationState.Succeeded) {
         throw new RuntimeException(500, "Already authenticated");
      } else {
         if (authenticationState instanceof AuthenticationState.Deferred deferred) {
            AuthenticationState.Succeeded undeferred = deferred.login(username, password, request, response);
            if (undeferred != null) {
               setAuthenticationState(request, undeferred);
               return undeferred;
            }
         }

         return null;
      }
   }

   static boolean logout(Request request, Response response) {
      AuthenticationState authenticationState = getAuthenticationState(request);
      if (authenticationState instanceof AuthenticationState.Succeeded succeededAuthentication) {
         succeededAuthentication.logout(request, response);
         return true;
      } else if (authenticationState instanceof AuthenticationState.Deferred deferred) {
         deferred.logout(request, response);
         return true;
      } else {
         return false;
      }
   }

   static AuthenticationState.Deferred defer(LoginAuthenticator loginAuthenticator) {
      return new DeferredAuthenticationState(loginAuthenticator);
   }

   public interface Deferred extends AuthenticationState {
      static boolean isDeferred(Response response) {
         return response instanceof AuthenticationState.Deferred.DeferredResponse;
      }

      AuthenticationState.Succeeded authenticate(Request var1);

      AuthenticationState authenticate(Request var1, Response var2, Callback var3);

      AuthenticationState.Succeeded login(String var1, Object var2, Request var3, Response var4);

      void logout(Request var1, Response var2);

      IdentityService.Association getAssociation();

      public interface DeferredResponse extends Response {
      }
   }

   public interface ResponseSent extends AuthenticationState {
   }

   public static class ServeAs implements AuthenticationState {
      private final HttpURI _uri;

      public ServeAs(HttpURI uri) {
         this._uri = uri;
      }

      public Request wrap(Request request) {
         return Request.serveAs(request, this._uri);
      }
   }

   public interface Succeeded extends AuthenticationState {
      String getAuthenticationType();

      UserIdentity getUserIdentity();

      default Principal getUserPrincipal() {
         UserIdentity user = this.getUserIdentity();
         return user != null ? user.getUserPrincipal() : null;
      }

      boolean isUserInRole(String var1);

      void logout(Request var1, Response var2);
   }
}
