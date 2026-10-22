package org.eclipse.jetty.security.authentication;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.security.AuthenticationState;
import org.eclipse.jetty.security.RoleDelegateUserIdentity;
import org.eclipse.jetty.security.SPNEGOUserPrincipal;
import org.eclipse.jetty.security.ServerAuthException;
import org.eclipse.jetty.security.UserIdentity;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Session;
import org.eclipse.jetty.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SPNEGOAuthenticator extends LoginAuthenticator {
   private static final Logger LOG = LoggerFactory.getLogger(SPNEGOAuthenticator.class);
   private final String _type;
   private Duration _authenticationDuration = Duration.ofNanos(-1L);

   public SPNEGOAuthenticator() {
      this("SPNEGO");
   }

   public SPNEGOAuthenticator(String type) {
      this._type = type;
   }

   @Override
   public String getAuthenticationType() {
      return this._type;
   }

   public Duration getAuthenticationDuration() {
      return this._authenticationDuration;
   }

   public void setAuthenticationDuration(Duration authenticationDuration) {
      this._authenticationDuration = authenticationDuration;
   }

   @Override
   public UserIdentity login(String username, Object password, Request request, Response response) {
      RoleDelegateUserIdentity user = (RoleDelegateUserIdentity)this._loginService.login(username, password, request, request::getSession);
      if (user != null && user.isEstablished()) {
         this.updateSession(request, response);
      }

      return user;
   }

   @Override
   public AuthenticationState validateRequest(Request req, Response res, Callback callback) throws ServerAuthException {
      String header = req.getHeaders().get(HttpHeader.AUTHORIZATION);
      String spnegoToken = this.getSpnegoToken(header);
      Session httpSession = req.getSession(false);
      if (header != null && spnegoToken != null) {
         RoleDelegateUserIdentity identity = (RoleDelegateUserIdentity)this.login(null, spnegoToken, req, res);
         if (identity.isEstablished()) {
            if (!AuthenticationState.Deferred.isDeferred(res)) {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Sending final token");
               }

               SPNEGOUserPrincipal principal = (SPNEGOUserPrincipal)identity.getUserPrincipal();
               this.setSpnegoToken(res, principal.getEncodedToken());
            }

            Duration authnDuration = this.getAuthenticationDuration();
            if (!authnDuration.isNegative()) {
               if (httpSession == null) {
                  httpSession = req.getSession(true);
               }

               httpSession.setAttribute(SPNEGOAuthenticator.UserIdentityHolder.ATTRIBUTE, new SPNEGOAuthenticator.UserIdentityHolder(identity));
            }

            return new LoginAuthenticator.UserAuthenticationSucceeded(this.getAuthenticationType(), identity);
         } else if (AuthenticationState.Deferred.isDeferred(res)) {
            return null;
         } else {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Sending intermediate challenge");
            }

            SPNEGOUserPrincipal principal = (SPNEGOUserPrincipal)identity.getUserPrincipal();
            this.sendChallenge(req, res, callback, principal.getEncodedToken());
            return AuthenticationState.CHALLENGE;
         }
      } else {
         if (httpSession != null) {
            SPNEGOAuthenticator.UserIdentityHolder holder = (SPNEGOAuthenticator.UserIdentityHolder)httpSession.getAttribute(
               SPNEGOAuthenticator.UserIdentityHolder.ATTRIBUTE
            );
            if (holder != null) {
               UserIdentity identity = holder._userIdentity;
               if (identity != null) {
                  Duration authnDuration = this.getAuthenticationDuration();
                  if (!authnDuration.isNegative()) {
                     boolean expired = !authnDuration.isZero() && Instant.now().isAfter(holder._validFrom.plus(authnDuration));
                     if (!expired || !HttpMethod.GET.is(req.getMethod())) {
                        return new LoginAuthenticator.UserAuthenticationSucceeded(this.getAuthenticationType(), identity);
                     }
                  }
               }
            }
         }

         if (AuthenticationState.Deferred.isDeferred(res)) {
            return null;
         } else {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Sending initial challenge");
            }

            this.sendChallenge(req, res, callback, null);
            return AuthenticationState.CHALLENGE;
         }
      }
   }

   private void sendChallenge(Request req, Response res, Callback callback, String token) {
      this.setSpnegoToken(res, token);
      Response.writeError(req, res, callback, 401);
   }

   private void setSpnegoToken(Response response, String token) {
      String value = HttpHeader.NEGOTIATE.asString();
      if (token != null) {
         value = value + " " + token;
      }

      response.getHeaders().put(HttpHeader.WWW_AUTHENTICATE.asString(), value);
   }

   private String getSpnegoToken(String header) {
      if (header == null) {
         return null;
      } else {
         String scheme = HttpHeader.NEGOTIATE.asString() + " ";
         return header.regionMatches(true, 0, scheme, 0, scheme.length()) ? header.substring(scheme.length()).trim() : null;
      }
   }

   private static class UserIdentityHolder implements Serializable {
      private static final String ATTRIBUTE = SPNEGOAuthenticator.UserIdentityHolder.class.getName();
      private final transient Instant _validFrom = Instant.now();
      private final transient UserIdentity _userIdentity;

      private UserIdentityHolder(UserIdentity userIdentity) {
         this._userIdentity = userIdentity;
      }
   }
}
