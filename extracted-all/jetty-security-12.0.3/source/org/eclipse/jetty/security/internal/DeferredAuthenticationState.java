package org.eclipse.jetty.security.internal;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpFields.Mutable;
import org.eclipse.jetty.security.AuthenticationState;
import org.eclipse.jetty.security.IdentityService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.security.ServerAuthException;
import org.eclipse.jetty.security.UserIdentity;
import org.eclipse.jetty.security.authentication.LoginAuthenticator;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeferredAuthenticationState implements AuthenticationState.Deferred {
   private static final Logger LOG = LoggerFactory.getLogger(DeferredAuthenticationState.class);
   protected final LoginAuthenticator _authenticator;
   private IdentityService.Association _association;
   static final Response __deferredResponse = new AuthenticationState.Deferred.DeferredResponse() {
      public Request getRequest() {
         return null;
      }

      public int getStatus() {
         return 0;
      }

      public void setStatus(int code) {
      }

      public Mutable getHeaders() {
         return null;
      }

      public Supplier<HttpFields> getTrailersSupplier() {
         return null;
      }

      public void setTrailersSupplier(Supplier<HttpFields> trailers) {
      }

      public void write(boolean last, ByteBuffer content, Callback callback) {
         callback.succeeded();
      }

      public boolean isCommitted() {
         return true;
      }

      public boolean hasLastWrite() {
         return false;
      }

      public boolean isCompletedSuccessfully() {
         return false;
      }

      public void reset() {
      }

      public CompletableFuture<Void> writeInterim(int status, HttpFields headers) {
         return null;
      }
   };

   public DeferredAuthenticationState(LoginAuthenticator authenticator) {
      if (authenticator == null) {
         throw new NullPointerException("No Authenticator");
      } else {
         this._authenticator = authenticator;
      }
   }

   @Override
   public AuthenticationState.Succeeded authenticate(Request request) {
      try {
         AuthenticationState authenticationState = this._authenticator.validateRequest(request, __deferredResponse, Callback.NOOP);
         if (authenticationState != null) {
            AuthenticationState.setAuthenticationState(request, authenticationState);
            if (authenticationState instanceof AuthenticationState.Succeeded succeeded) {
               LoginService loginService = this._authenticator.getLoginService();
               IdentityService identityService = loginService.getIdentityService();
               if (identityService != null) {
                  UserIdentity user = succeeded.getUserIdentity();
                  this._association = identityService.associate(user, null);
               }

               return succeeded;
            }
         }
      } catch (ServerAuthException var7) {
         LOG.debug("Unable to authenticate {}", request, var7);
      }

      return null;
   }

   @Override
   public AuthenticationState authenticate(Request request, Response response, Callback callback) {
      try {
         LoginService loginService = this._authenticator.getLoginService();
         IdentityService identityService = loginService.getIdentityService();
         AuthenticationState authenticationState = this._authenticator.validateRequest(request, response, callback);
         if (authenticationState != null) {
            AuthenticationState.setAuthenticationState(request, authenticationState);
            if (authenticationState instanceof AuthenticationState.Succeeded && identityService != null) {
               UserIdentity user = ((AuthenticationState.Succeeded)authenticationState).getUserIdentity();
               this._association = identityService.associate(user, null);
            }
         }

         return authenticationState;
      } catch (ServerAuthException var8) {
         LOG.debug("Unable to authenticate {}", request, var8);
         return null;
      }
   }

   @Override
   public AuthenticationState.Succeeded login(String username, Object password, Request request, Response response) {
      if (username == null) {
         return null;
      } else {
         UserIdentity identity = this._authenticator.login(username, password, request, response);
         if (identity != null) {
            IdentityService identityService = this._authenticator.getLoginService().getIdentityService();
            AuthenticationState.Succeeded authentication = new LoginAuthenticator.UserAuthenticationSucceeded("API", identity);
            if (identityService != null) {
               this._association = identityService.associate(identity, null);
            }

            return authentication;
         } else {
            return null;
         }
      }
   }

   @Override
   public void logout(Request request, Response response) {
      this._authenticator.logout(request, response);
   }

   @Override
   public IdentityService.Association getAssociation() {
      return this._association;
   }
}
