package org.eclipse.jetty.security.authentication;

import java.io.Serializable;
import org.eclipse.jetty.security.AuthenticationState;
import org.eclipse.jetty.security.Authenticator;
import org.eclipse.jetty.security.IdentityService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.UserIdentity;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Session;
import org.eclipse.jetty.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LoginAuthenticator implements Authenticator {
   private static final Logger LOG = LoggerFactory.getLogger(LoginAuthenticator.class);
   protected LoginService _loginService;
   protected IdentityService _identityService;
   private boolean _sessionRenewedOnAuthentication;
   private int _sessionMaxInactiveIntervalOnAuthentication;

   protected LoginAuthenticator() {
   }

   public UserIdentity login(String username, Object password, Request request, Response response) {
      UserIdentity user = this._loginService.login(username, password, request, request::getSession);
      if (LOG.isDebugEnabled()) {
         LOG.debug("{}.login {}", this, user);
      }

      if (user != null) {
         this.updateSession(request, response);
         return user;
      } else {
         return null;
      }
   }

   public void logout(Request request, Response response) {
      Session session = request.getSession(false);
      if (LOG.isDebugEnabled()) {
         LOG.debug("{}.logout {}", this, session);
      }

      if (session != null) {
         session.removeAttribute(SecurityHandler.SESSION_AUTHENTICATED_ATTRIBUTE);
      }
   }

   @Override
   public void setConfiguration(Authenticator.Configuration configuration) {
      this._loginService = configuration.getLoginService();
      if (this._loginService == null) {
         throw new IllegalStateException("No LoginService for " + this + " in " + configuration);
      } else {
         this._identityService = configuration.getIdentityService();
         if (this._identityService == null) {
            throw new IllegalStateException("No IdentityService for " + this + " in " + configuration);
         } else {
            this._sessionRenewedOnAuthentication = configuration.isSessionRenewedOnAuthentication();
            this._sessionMaxInactiveIntervalOnAuthentication = configuration.getSessionMaxInactiveIntervalOnAuthentication();
         }
      }
   }

   public LoginService getLoginService() {
      return this._loginService;
   }

   protected void updateSession(Request httpRequest, Response httpResponse) {
      Session session = httpRequest.getSession(false);
      if (session != null && (this._sessionRenewedOnAuthentication || this._sessionMaxInactiveIntervalOnAuthentication != 0)) {
         synchronized (session) {
            if (this._sessionMaxInactiveIntervalOnAuthentication != 0) {
               session.setMaxInactiveInterval(this._sessionMaxInactiveIntervalOnAuthentication < 0 ? -1 : this._sessionMaxInactiveIntervalOnAuthentication);
            }

            if (this._sessionRenewedOnAuthentication && session.getAttribute(SecurityHandler.SESSION_AUTHENTICATED_ATTRIBUTE) != Boolean.TRUE) {
               session.setAttribute(SecurityHandler.SESSION_AUTHENTICATED_ATTRIBUTE, Boolean.TRUE);
               session.renewId(httpRequest, httpResponse);
            }
         }
      }
   }

   public static class LoggedOutAuthentication implements AuthenticationState.Deferred {
      private final AuthenticationState.Deferred _delegate;

      @Override
      public AuthenticationState.Succeeded login(String username, Object password, Request request, Response response) {
         return this._delegate.login(username, password, request, response);
      }

      @Override
      public void logout(Request request, Response response) {
         this._delegate.logout(request, response);
      }

      @Override
      public IdentityService.Association getAssociation() {
         return this._delegate.getAssociation();
      }

      public LoggedOutAuthentication(LoginAuthenticator authenticator) {
         this._delegate = AuthenticationState.defer(authenticator);
      }

      @Override
      public AuthenticationState.Succeeded authenticate(Request request) {
         return null;
      }

      @Override
      public AuthenticationState authenticate(Request request, Response response, Callback callback) {
         return null;
      }
   }

   public static class UserAuthenticationSent extends LoginAuthenticator.UserAuthenticationSucceeded implements AuthenticationState.ResponseSent {
      public UserAuthenticationSent(String method, UserIdentity userIdentity) {
         super(method, userIdentity);
      }
   }

   public static class UserAuthenticationSucceeded implements AuthenticationState.Succeeded, Serializable {
      private static final long serialVersionUID = -6290411814232723403L;
      protected String _authenticationType;
      protected transient UserIdentity _userIdentity;

      public UserAuthenticationSucceeded(String authenticationType, UserIdentity userIdentity) {
         this._authenticationType = authenticationType;
         this._userIdentity = userIdentity;
      }

      @Override
      public String getAuthenticationType() {
         return this._authenticationType;
      }

      @Override
      public UserIdentity getUserIdentity() {
         return this._userIdentity;
      }

      @Override
      public boolean isUserInRole(String role) {
         return this._userIdentity.isUserInRole(role);
      }

      @Override
      public void logout(Request request, Response response) {
         SecurityHandler security = SecurityHandler.getCurrentSecurityHandler();
         if (security != null) {
            LoginService loginService = security.getLoginService();
            if (loginService != null) {
               loginService.logout(this.getUserIdentity());
            }

            IdentityService identityService = security.getIdentityService();
            if (identityService != null) {
               identityService.onLogout(this.getUserIdentity());
            }

            Authenticator authenticator = security.getAuthenticator();
            AuthenticationState authenticationState = null;
            if (authenticator instanceof LoginAuthenticator loginAuthenticator) {
               ((LoginAuthenticator)authenticator).logout(request, response);
               authenticationState = new LoginAuthenticator.LoggedOutAuthentication(loginAuthenticator);
            }

            AuthenticationState.setAuthenticationState(request, authenticationState);
         }
      }

      @Override
      public String toString() {
         return "%s@%x{%s,%s}".formatted(this.getClass().getSimpleName(), this.hashCode(), this.getAuthenticationType(), this.getUserIdentity());
      }
   }
}
