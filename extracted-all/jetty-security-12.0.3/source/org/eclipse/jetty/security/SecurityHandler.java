package org.eclipse.jetty.security;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpHeaderValue;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.http.HttpFields.Mutable;
import org.eclipse.jetty.http.pathmap.MappedResource;
import org.eclipse.jetty.http.pathmap.PathMappings;
import org.eclipse.jetty.http.pathmap.PathSpec;
import org.eclipse.jetty.http.pathmap.PathSpecGroup;
import org.eclipse.jetty.security.authentication.LoginAuthenticator;
import org.eclipse.jetty.server.Context;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.TypeUtil;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.component.DumpableCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SecurityHandler extends org.eclipse.jetty.server.Handler.Wrapper implements Authenticator.Configuration {
   public static String SESSION_AUTHENTICATED_ATTRIBUTE = "org.eclipse.jetty.security.sessionAuthenticated";
   private static final Logger LOG = LoggerFactory.getLogger(SecurityHandler.class);
   private static final List<Authenticator.Factory> __knownAuthenticatorFactories = new ArrayList<>();
   private Authenticator _authenticator;
   private Authenticator.Factory _authenticatorFactory;
   private String _realmName;
   private String _authenticationType;
   private final Map<String, String> _parameters = new HashMap<>();
   private LoginService _loginService;
   private IdentityService _identityService;
   private boolean _renewSessionOnAuthentication = true;
   private int _sessionMaxInactiveIntervalOnAuthentication = 0;
   private AuthenticationState.Deferred _deferred;

   protected SecurityHandler() {
      this(null);
   }

   protected SecurityHandler(Handler handler) {
      super(handler);
      this.addBean(new DumpableCollection("knownAuthenticatorFactories", __knownAuthenticatorFactories));
   }

   @Override
   public IdentityService getIdentityService() {
      return this._identityService;
   }

   public void setIdentityService(IdentityService identityService) {
      if (this.isStarted()) {
         throw new IllegalStateException("Started");
      } else {
         this.updateBean(this._identityService, identityService);
         this._identityService = identityService;
      }
   }

   @Override
   public LoginService getLoginService() {
      return this._loginService;
   }

   public void setLoginService(LoginService loginService) {
      if (this.isStarted()) {
         throw new IllegalStateException("Started");
      } else {
         this.updateBean(this._loginService, loginService);
         this._loginService = loginService;
      }
   }

   public Authenticator getAuthenticator() {
      return this._authenticator;
   }

   public void setAuthenticator(Authenticator authenticator) {
      if (this.isStarted()) {
         throw new IllegalStateException("Started");
      } else {
         this.updateBean(this._authenticator, authenticator);
         this._authenticator = authenticator;
         if (this._authenticator != null) {
            this._authenticationType = this._authenticator.getAuthenticationType();
         }
      }
   }

   public Authenticator.Factory getAuthenticatorFactory() {
      return this._authenticatorFactory;
   }

   public void setAuthenticatorFactory(Authenticator.Factory authenticatorFactory) {
      if (this.isRunning()) {
         throw new IllegalStateException("running");
      } else {
         this.updateBean(this._authenticatorFactory, authenticatorFactory);
         this._authenticatorFactory = authenticatorFactory;
      }
   }

   public List<Authenticator.Factory> getKnownAuthenticatorFactories() {
      return __knownAuthenticatorFactories;
   }

   @Override
   public String getRealmName() {
      return this._realmName;
   }

   public void setRealmName(String realmName) {
      if (this.isRunning()) {
         throw new IllegalStateException("running");
      } else {
         this._realmName = realmName;
      }
   }

   @Override
   public String getAuthenticationType() {
      return this._authenticationType;
   }

   public void setAuthenticationType(String authenticationType) {
      if (this.isRunning()) {
         throw new IllegalStateException("running");
      } else {
         this._authenticationType = authenticationType;
         if (this._authenticator != null && !this._authenticator.getAuthenticationType().equals(this._authenticationType)) {
            this._authenticator = null;
         }
      }
   }

   @Override
   public String getParameter(String key) {
      return this._parameters.get(key);
   }

   @Override
   public Set<String> getParameterNames() {
      return this._parameters.keySet();
   }

   public String setParameter(String key, String value) {
      if (this.isStarted()) {
         throw new IllegalStateException("started");
      } else {
         return this._parameters.put(key, value);
      }
   }

   protected LoginService findLoginService() {
      Collection<LoginService> list = this.getServer().getBeans(LoginService.class);
      LoginService service = null;
      String realm = this.getRealmName();
      if (realm != null) {
         for (LoginService s : list) {
            if (s.getName() != null && s.getName().equals(realm)) {
               service = s;
               break;
            }
         }
      } else if (list.size() == 1) {
         service = list.iterator().next();
      }

      return service;
   }

   protected IdentityService findIdentityService() {
      return (IdentityService)this.getServer().getBean(IdentityService.class);
   }

   protected void doStart() throws Exception {
      if (this._loginService == null) {
         this.setLoginService(this.findLoginService());
         if (this._loginService != null) {
            this.unmanage(this._loginService);
         }
      }

      if (this._identityService == null) {
         if (this._loginService != null) {
            this.setIdentityService(this._loginService.getIdentityService());
         }

         if (this._identityService == null) {
            this.setIdentityService(this.findIdentityService());
         }

         if (this._identityService == null) {
            this.setIdentityService(new DefaultIdentityService());
            this.manage(this._identityService);
         } else {
            this.unmanage(this._identityService);
         }
      }

      if (this._loginService != null) {
         if (this._loginService.getIdentityService() == null) {
            this._loginService.setIdentityService(this._identityService);
         } else if (this._loginService.getIdentityService() != this._identityService) {
            throw new IllegalStateException("LoginService has different IdentityService to " + this);
         }
      }

      Context context = ContextHandler.getCurrentContext();
      if (this._authenticator == null) {
         if (this._authenticatorFactory != null) {
            Authenticator authenticator = this._authenticatorFactory.getAuthenticator(this.getServer(), context, this);
            if (authenticator != null) {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Created authenticator {} with {}", authenticator, this._authenticatorFactory);
               }

               this.setAuthenticator(authenticator);
            }
         } else {
            for (Authenticator.Factory factory : this.getKnownAuthenticatorFactories()) {
               Authenticator authenticator = factory.getAuthenticator(this.getServer(), context, this);
               if (authenticator != null) {
                  if (LOG.isDebugEnabled()) {
                     LOG.debug("Created authenticator {} with {}", authenticator, factory);
                  }

                  this.setAuthenticator(authenticator);
                  break;
               }
            }
         }
      }

      if (this._authenticator == null) {
         this.setAuthenticator(new Authenticator.NoOp());
      }

      if (this._authenticator != null) {
         this._authenticator.setConfiguration(this);
      } else if (this._realmName != null) {
         LOG.warn("No Authenticator for {}", this);
         throw new IllegalStateException("No Authenticator");
      }

      if (this._authenticator instanceof LoginAuthenticator loginAuthenticator) {
         this._deferred = AuthenticationState.defer(loginAuthenticator);
         this.addBean(this._deferred);
      }

      super.doStart();
   }

   protected void doStop() throws Exception {
      if (!this.isManaged(this._identityService)) {
         this.removeBean(this._identityService);
         this._identityService = null;
      }

      if (!this.isManaged(this._loginService)) {
         this.removeBean(this._loginService);
         this._loginService = null;
      }

      if (this._deferred != null) {
         this.removeBean(this._deferred);
         this._deferred = null;
      }

      super.doStop();
   }

   @Override
   public boolean isSessionRenewedOnAuthentication() {
      return this._renewSessionOnAuthentication;
   }

   public void setSessionRenewedOnAuthentication(boolean renew) {
      this._renewSessionOnAuthentication = renew;
   }

   @Override
   public int getSessionMaxInactiveIntervalOnAuthentication() {
      return this._sessionMaxInactiveIntervalOnAuthentication;
   }

   public void setSessionMaxInactiveIntervalOnAuthentication(int seconds) {
      this._sessionMaxInactiveIntervalOnAuthentication = seconds;
   }

   public boolean handle(Request request, Response response, Callback callback) throws Exception {
      Handler next = this.getHandler();
      if (next == null) {
         return false;
      } else {
         String pathInContext = Request.getPathInContext(request);
         Constraint constraint = this.getConstraint(pathInContext, request);
         if (LOG.isDebugEnabled()) {
            LOG.debug("getConstraint({}) -> {}", pathInContext, constraint);
         }

         if (constraint == null) {
            constraint = Constraint.ALLOWED;
         }

         if (constraint.getAuthorization() == Constraint.Authorization.FORBIDDEN) {
            Response.writeError(request, response, callback, 403);
            return true;
         } else if (Constraint.Transport.SECURE.equals(constraint.getTransport()) && !request.isSecure()) {
            this.redirectToSecure(request, response, callback);
            return true;
         } else {
            Constraint.Authorization constraintAuthorization = constraint.getAuthorization();
            constraintAuthorization = this._authenticator.getConstraintAuthentication(pathInContext, constraintAuthorization, request::getSession);
            if (constraintAuthorization == Constraint.Authorization.INHERIT) {
               constraintAuthorization = Constraint.Authorization.ALLOWED;
            }

            if (LOG.isDebugEnabled()) {
               LOG.debug("constraintAuthorization {}", constraintAuthorization);
            }

            boolean mustValidate = constraintAuthorization != Constraint.Authorization.ALLOWED;

            try {
               AuthenticationState authenticationState = mustValidate ? this._authenticator.validateRequest(request, response, callback) : null;
               if (LOG.isDebugEnabled()) {
                  LOG.debug("AuthenticationState {}", authenticationState);
               }

               if (authenticationState instanceof AuthenticationState.ResponseSent) {
                  return true;
               } else {
                  if (authenticationState instanceof AuthenticationState.ServeAs serveAs) {
                     HttpURI uri = request.getHttpURI();
                     request = serveAs.wrap(request);
                     if (!uri.equals(request.getHttpURI())) {
                        response.getHeaders().put(HttpHeader.CACHE_CONTROL.asString(), HttpHeaderValue.NO_CACHE.asString());
                        response.getHeaders().putDate(HttpHeader.EXPIRES.asString(), 1L);
                        final Mutable headers = new org.eclipse.jetty.http.HttpFields.Mutable.Wrapper(response.getHeaders()) {
                           public HttpField onAddField(HttpField field) {
                              if (field.getHeader() == null) {
                                 return field;
                              } else {
                                 return switch (field.getHeader()) {
                                    case CACHE_CONTROL, PRAGMA, ETAG, EXPIRES, LAST_MODIFIED, AGE -> null;
                                    default -> field;
                                 };
                              }
                           }
                        };
                        response = new org.eclipse.jetty.server.Response.Wrapper(request, response) {
                           public Mutable getHeaders() {
                              return headers;
                           }
                        };
                     }

                     authenticationState = this._deferred;
                  } else {
                     if (mustValidate && !this.isAuthorized(constraint, authenticationState)) {
                        Response.writeError(request, response, callback, 403, "!authorized");
                        return true;
                     }

                     if (authenticationState == null) {
                        authenticationState = this._deferred;
                     }
                  }

                  AuthenticationState.setAuthenticationState(request, authenticationState);
                  IdentityService.Association association = authenticationState instanceof AuthenticationState.Succeeded user
                     ? this._identityService.associate(user.getUserIdentity(), null)
                     : null;

                  boolean var22;
                  try {
                     var22 = next.handle(this._authenticator.prepareRequest(request, authenticationState), response, callback);
                  } finally {
                     if (association == null && authenticationState instanceof AuthenticationState.Deferred deferred) {
                        association = deferred.getAssociation();
                     }

                     if (association != null) {
                        association.close();
                     }
                  }

                  return var22;
               }
            } catch (ServerAuthException var18) {
               Response.writeError(request, response, callback, 500, var18.getMessage());
               return true;
            }
         }
      }
   }

   public static SecurityHandler getCurrentSecurityHandler() {
      ContextHandler contextHandler = ContextHandler.getCurrentContextHandler();
      return contextHandler != null ? (SecurityHandler)contextHandler.getDescendant(SecurityHandler.class) : null;
   }

   protected abstract Constraint getConstraint(String var1, Request var2);

   protected void redirectToSecure(Request request, Response response, Callback callback) {
      HttpConfiguration httpConfig = request.getConnectionMetaData().getHttpConfiguration();
      if (httpConfig.getSecurePort() > 0) {
         String scheme = httpConfig.getSecureScheme();
         int port = httpConfig.getSecurePort();
         String url = URIUtil.newURI(scheme, Request.getServerName(request), port, request.getHttpURI().getPath(), request.getHttpURI().getQuery());
         response.getHeaders().put(HttpFields.CONTENT_LENGTH_0);
         Response.sendRedirect(request, response, callback, 302, url, true);
      } else {
         Response.writeError(request, response, callback, 403, "!Secure");
      }
   }

   protected boolean isAuthorized(Constraint constraint, AuthenticationState authenticationState) {
      UserIdentity userIdentity = authenticationState instanceof AuthenticationState.Succeeded user ? user.getUserIdentity() : null;

      return switch (constraint.getAuthorization()) {
         case FORBIDDEN, ALLOWED, INHERIT -> true;
         case ANY_USER -> userIdentity != null && userIdentity.getUserPrincipal() != null;
         case KNOWN_ROLE -> {
            if (userIdentity != null && userIdentity.getUserPrincipal() != null) {
               for (String role : this.getKnownRoles()) {
                  if (userIdentity.isUserInRole(role)) {
                     yield true;
                  }
               }
            }

            yield false;
         }
         case SPECIFIC_ROLE -> {
            if (userIdentity != null && userIdentity.getUserPrincipal() != null) {
               for (String role : constraint.getRoles()) {
                  if (userIdentity.isUserInRole(role)) {
                     yield true;
                  }
               }
            }

            yield false;
         }
      };
   }

   protected Set<String> getKnownRoles() {
      return Collections.emptySet();
   }

   static {
      TypeUtil.serviceStream(ServiceLoader.load(Authenticator.Factory.class)).forEach(__knownAuthenticatorFactories::add);
      __knownAuthenticatorFactories.add(new DefaultAuthenticatorFactory());
   }

   public class NotChecked implements Principal {
      @Override
      public String getName() {
         return null;
      }

      @Override
      public String toString() {
         return "NOT CHECKED";
      }

      public SecurityHandler getSecurityHandler() {
         return SecurityHandler.this;
      }
   }

   public static class PathMapped extends SecurityHandler implements Comparator<PathSpec> {
      private final PathMappings<Constraint> _mappings = new PathMappings();
      private final Set<String> _knownRoles = new HashSet<>();

      public PathMapped() {
         this(null);
      }

      public PathMapped(Handler handler) {
         super(handler);
      }

      public Constraint put(String pathSpec, Constraint constraint) {
         return this.put(PathSpec.from(pathSpec), constraint);
      }

      public Constraint put(PathSpec pathSpec, Constraint constraint) {
         Set<String> roles = constraint.getRoles();
         if (roles != null) {
            this._knownRoles.addAll(roles);
         }

         return (Constraint)this._mappings.put(pathSpec, constraint);
      }

      public Constraint get(PathSpec pathSpec) {
         return (Constraint)this._mappings.get(pathSpec);
      }

      public Constraint remove(PathSpec pathSpec) {
         Constraint removed = (Constraint)this._mappings.remove(pathSpec);
         this._knownRoles.clear();
         this._mappings.values().forEach(c -> {
            Set<String> roles = c.getRoles();
            if (roles != null) {
               this._knownRoles.addAll(roles);
            }
         });
         return removed;
      }

      @Override
      protected Constraint getConstraint(String pathInContext, Request request) {
         List<MappedResource<Constraint>> matches = this._mappings.getMatches(pathInContext);
         if (matches != null && !matches.isEmpty()) {
            if (matches.size() == 1) {
               return (Constraint)matches.get(0).getResource();
            } else {
               matches.sort(this::compare);
               if (SecurityHandler.LOG.isDebugEnabled()) {
                  SecurityHandler.LOG.debug("getConstraint {} -> {}", pathInContext, matches);
               }

               Constraint constraint = null;

               for (MappedResource<Constraint> c : matches) {
                  constraint = Constraint.combine(constraint, (Constraint)c.getResource());
               }

               return constraint;
            }
         } else {
            return null;
         }
      }

      public int compare(PathSpec ps1, PathSpec ps2) {
         PathSpecGroup g1 = ps1.getGroup();
         PathSpecGroup g2 = ps2.getGroup();
         return g1.equals(g2)
            ? Integer.compare(ps1.getSpecLength(), ps2.getSpecLength())
            : Integer.compare(this.pathSpecGroupPrecedence(g1), this.pathSpecGroupPrecedence(g2));
      }

      int compare(MappedResource<Constraint> c1, MappedResource<Constraint> c2) {
         PathSpecGroup g1 = c1.getPathSpec().getGroup();
         PathSpecGroup g2 = c2.getPathSpec().getGroup();
         int l1 = c1.getPathSpec().getSpecLength();
         int l2 = c2.getPathSpec().getSpecLength();
         return g1.equals(g2) ? Integer.compare(l1, l2) : Integer.compare(this.pathSpecGroupPrecedence(g1), this.pathSpecGroupPrecedence(g2));
      }

      protected int pathSpecGroupPrecedence(PathSpecGroup group) {
         return switch (group) {
            case EXACT -> 5;
            case ROOT -> 4;
            case SUFFIX_GLOB -> 3;
            case MIDDLE_GLOB -> 2;
            case PREFIX_GLOB -> 1;
            case DEFAULT -> 0;
            default -> throw new IncompatibleClassChangeError();
         };
      }

      @Override
      protected Set<String> getKnownRoles() {
         return this._knownRoles;
      }
   }
}
