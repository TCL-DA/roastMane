package org.eclipse.jetty.security.authentication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import org.eclipse.jetty.security.Authenticator;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.UserIdentity;
import org.eclipse.jetty.server.Session;
import org.eclipse.jetty.server.Session.ValueListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionAuthentication extends LoginAuthenticator.UserAuthenticationSucceeded implements Serializable, ValueListener {
   private static final Logger LOG = LoggerFactory.getLogger(SessionAuthentication.class);
   private static final long serialVersionUID = -4643200685888258706L;
   public static final String AUTHENTICATED_ATTRIBUTE = "org.eclipse.jetty.security.UserIdentity";
   private final String _name;
   private final Object _credentials;
   private Session _session;

   public SessionAuthentication(String method, UserIdentity userIdentity, Object credentials) {
      super(method, userIdentity);
      this._name = userIdentity.getUserPrincipal().getName();
      this._credentials = credentials;
   }

   @Override
   public UserIdentity getUserIdentity() {
      if (this._userIdentity == null) {
         throw new IllegalStateException("!UserIdentity");
      } else {
         return super.getUserIdentity();
      }
   }

   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      stream.defaultReadObject();
      SecurityHandler security = SecurityHandler.getCurrentSecurityHandler();
      if (security == null) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("!SecurityHandler");
         }
      } else {
         Authenticator authenticator = security.getAuthenticator();
         LoginService loginService;
         if (authenticator instanceof LoginAuthenticator) {
            loginService = ((LoginAuthenticator)authenticator).getLoginService();
         } else {
            loginService = security.getLoginService();
         }

         if (loginService == null) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("!LoginService");
            }
         } else {
            this._userIdentity = loginService.login(this._name, this._credentials, null, null);
            LOG.debug("Deserialized and relogged in {}", this);
         }
      }
   }

   @Override
   public String toString() {
      return String.format(
         "%s@%x{%s,%s}", this.getClass().getSimpleName(), this.hashCode(), this._session == null ? "-" : this._session.getId(), this._userIdentity
      );
   }

   public void onSessionActivation(Session session) {
      this._session = session;
   }

   public void onSessionPassivation(Session session) {
   }
}
