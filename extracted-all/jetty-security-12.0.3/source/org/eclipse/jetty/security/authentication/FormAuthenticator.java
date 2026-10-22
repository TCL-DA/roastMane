package org.eclipse.jetty.security.authentication;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.http.HttpURI.Mutable;
import org.eclipse.jetty.security.AuthenticationState;
import org.eclipse.jetty.security.Authenticator;
import org.eclipse.jetty.security.Constraint;
import org.eclipse.jetty.security.ServerAuthException;
import org.eclipse.jetty.security.UserIdentity;
import org.eclipse.jetty.server.FormFields;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Session;
import org.eclipse.jetty.server.Request.Wrapper;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.Fields;
import org.eclipse.jetty.util.URIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FormAuthenticator extends LoginAuthenticator {
   private static final Logger LOG = LoggerFactory.getLogger(FormAuthenticator.class);
   public static final String __FORM_LOGIN_PAGE = "org.eclipse.jetty.security.form_login_page";
   public static final String __FORM_ERROR_PAGE = "org.eclipse.jetty.security.form_error_page";
   public static final String __FORM_DISPATCH = "org.eclipse.jetty.security.dispatch";
   public static final String __J_URI = "org.eclipse.jetty.security.form_URI";
   public static final String __J_POST = "org.eclipse.jetty.security.form_POST";
   public static final String __J_METHOD = "org.eclipse.jetty.security.form_METHOD";
   public static final String __J_SECURITY_CHECK = "/j_security_check";
   public static final String __J_USERNAME = "j_username";
   public static final String __J_PASSWORD = "j_password";
   private String _formErrorPage;
   private String _formErrorPath;
   private String _formLoginPage;
   private String _formLoginPath;
   private boolean _dispatch;
   private boolean _alwaysSaveUri;

   public FormAuthenticator() {
   }

   public FormAuthenticator(String login, String error, boolean dispatch) {
      this();
      if (login != null) {
         this.setLoginPage(login);
      }

      if (error != null) {
         this.setErrorPage(error);
      }

      this._dispatch = dispatch;
   }

   public void setAlwaysSaveUri(boolean alwaysSave) {
      this._alwaysSaveUri = alwaysSave;
   }

   public boolean getAlwaysSaveUri() {
      return this._alwaysSaveUri;
   }

   @Override
   public void setConfiguration(Authenticator.Configuration configuration) {
      super.setConfiguration(configuration);
      String login = configuration.getParameter("org.eclipse.jetty.security.form_login_page");
      if (login != null) {
         this.setLoginPage(login);
      }

      String error = configuration.getParameter("org.eclipse.jetty.security.form_error_page");
      if (error != null) {
         this.setErrorPage(error);
      }

      String dispatch = configuration.getParameter("org.eclipse.jetty.security.dispatch");
      this._dispatch = dispatch == null ? this._dispatch : Boolean.parseBoolean(dispatch);
   }

   @Override
   public String getAuthenticationType() {
      return "FORM";
   }

   private void setLoginPage(String path) {
      if (!path.startsWith("/")) {
         LOG.warn("form-login-page must start with /");
         path = "/" + path;
      }

      this._formLoginPage = path;
      this._formLoginPath = path;
      if (this._formLoginPath.indexOf(63) > 0) {
         this._formLoginPath = this._formLoginPath.substring(0, this._formLoginPath.indexOf(63));
      }
   }

   private void setErrorPage(String path) {
      if (path != null && path.trim().length() != 0) {
         if (!path.startsWith("/")) {
            LOG.warn("form-error-page must start with /");
            path = "/" + path;
         }

         this._formErrorPage = path;
         this._formErrorPath = path;
         if (this._formErrorPath.indexOf(63) > 0) {
            this._formErrorPath = this._formErrorPath.substring(0, this._formErrorPath.indexOf(63));
         }
      } else {
         this._formErrorPath = null;
         this._formErrorPage = null;
      }
   }

   @Override
   public UserIdentity login(String username, Object password, Request request, Response response) {
      UserIdentity user = super.login(username, password, request, response);
      if (user != null) {
         Session session = request.getSession(true);
         AuthenticationState cached = new SessionAuthentication(this.getAuthenticationType(), user, password);
         session.setAttribute("org.eclipse.jetty.security.UserIdentity", cached);
      }

      return user;
   }

   @Override
   public void logout(Request request, Response response) {
      super.logout(request, response);
      Session session = request.getSession(false);
      if (session != null) {
         session.removeAttribute("org.eclipse.jetty.security.UserIdentity");
      }
   }

   @Override
   public Request prepareRequest(Request request, AuthenticationState authenticationState) {
      if (authenticationState instanceof AuthenticationState.Succeeded) {
         Session session = request.getSession(false);
         HttpURI juri = (HttpURI)session.getAttribute("org.eclipse.jetty.security.form_URI");
         HttpURI uri = request.getHttpURI();
         if (uri.equals(juri)) {
            session.removeAttribute("org.eclipse.jetty.security.form_URI");
            if (session.removeAttribute("org.eclipse.jetty.security.form_POST") instanceof CompletableFuture<?> futureFields) {
               FormFields.set(request, futureFields);
            }

            final String method = (String)session.removeAttribute("org.eclipse.jetty.security.form_METHOD");
            if (method != null && request.getMethod().equals(method)) {
               return new Wrapper(request) {
                  public String getMethod() {
                     return method;
                  }
               };
            }
         }
      }

      return request;
   }

   protected Fields getParameters(Request request) {
      try {
         Fields queryFields = Request.extractQueryParameters(request);
         Fields formFields = (Fields)FormFields.from(request).get();
         return Fields.combine(queryFields, formFields);
      } catch (ExecutionException | InterruptedException var4) {
         throw new RuntimeException(var4);
      }
   }

   protected String encodeURL(String url, Request request) {
      Session session = request.getSession(false);
      return session == null ? url : session.encodeURI(request, url, request.getHeaders().contains(HttpHeader.COOKIE));
   }

   @Override
   public Constraint.Authorization getConstraintAuthentication(String pathInContext, Constraint.Authorization existing, Function<Boolean, Session> getSession) {
      if (this.isJSecurityCheck(pathInContext)) {
         return Constraint.Authorization.ANY_USER;
      } else {
         return this.isLoginOrErrorPage(pathInContext) ? Constraint.Authorization.ALLOWED : existing;
      }
   }

   @Override
   public AuthenticationState validateRequest(Request request, Response response, Callback callback) throws ServerAuthException {
      String pathInContext = Request.getPathInContext(request);
      boolean jSecurityCheck = this.isJSecurityCheck(pathInContext);
      if (jSecurityCheck) {
         Fields parameters = this.getParameters(request);
         String username = parameters.getValue("j_username");
         String password = parameters.getValue("j_password");
         UserIdentity user = this.login(username, password, request, response);
         if (LOG.isDebugEnabled()) {
            LOG.debug("jsecuritycheck {} {}", username, user);
         }

         if (user != null) {
            Session session = request.getSession(false);
            HttpURI savedURI = (HttpURI)session.getAttribute("org.eclipse.jetty.security.form_URI");
            String originalURI = savedURI != null ? savedURI.asString() : Request.getContextPath(request);
            if (originalURI == null) {
               originalURI = "/";
            }

            LoginAuthenticator.UserAuthenticationSent formAuth = new LoginAuthenticator.UserAuthenticationSent(this.getAuthenticationType(), user);
            Response.sendRedirect(request, response, callback, this.encodeURL(originalURI, request), true);
            return formAuth;
         } else {
            if (LOG.isDebugEnabled()) {
               LOG.debug("auth failed {}=={}", username, this._formErrorPage);
            }

            return this.sendError(request, response, callback);
         }
      } else {
         Session session = request.getSession(false);
         AuthenticationState authenticationState = session == null
            ? null
            : (AuthenticationState)session.getAttribute("org.eclipse.jetty.security.UserIdentity");
         if (LOG.isDebugEnabled()) {
            LOG.debug("auth {}", authenticationState);
         }

         if (authenticationState instanceof AuthenticationState.Succeeded succeeded
            && this._loginService != null
            && !this._loginService.validate(succeeded.getUserIdentity())) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("auth revoked {}", authenticationState);
            }

            session.removeAttribute("org.eclipse.jetty.security.UserIdentity");
            authenticationState = null;
         }

         if (authenticationState != null) {
            return authenticationState;
         } else if (response.isCommitted()) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("auth deferred {}", session == null ? null : session.getId());
            }

            return null;
         } else {
            session = session != null ? session : request.getSession(true);
            synchronized (session) {
               if (session.getAttribute("org.eclipse.jetty.security.form_URI") == null || this._alwaysSaveUri) {
                  HttpURI juri = request.getHttpURI();
                  session.setAttribute("org.eclipse.jetty.security.form_URI", juri.asImmutable());
                  if (!HttpMethod.GET.is(request.getMethod())) {
                     session.setAttribute("org.eclipse.jetty.security.form_METHOD", request.getMethod());
                  }

                  if (HttpMethod.POST.is(request.getMethod())) {
                     try {
                        CompletableFuture<Fields> futureFields = FormFields.from(request);
                        futureFields.get();
                        session.setAttribute("org.eclipse.jetty.security.form_POST", futureFields);
                     } catch (ExecutionException var15) {
                        throw new ServerAuthException(var15.getCause());
                     } catch (InterruptedException var16) {
                        throw new ServerAuthException(var16);
                     }
                  }
               }
            }

            if (LOG.isDebugEnabled()) {
               LOG.debug("challenge {}->{}", session.getId(), this._formLoginPage);
            }

            return this.sendChallenge(request, response, callback);
         }
      }
   }

   private AuthenticationState dispatch(String path, Request request, Response response, Callback callback) {
      try {
         String newPath = URIUtil.addPaths(request.getContext().getContextPath(), path);
         Mutable newUri = HttpURI.build(request.getHttpURI()).pathQuery(newPath);
         return new AuthenticationState.ServeAs(newUri);
      } catch (Throwable var7) {
         Response.writeError(request, response, callback, var7);
         return AuthenticationState.SEND_FAILURE;
      }
   }

   private AuthenticationState sendError(Request request, Response response, Callback callback) {
      if (this._formErrorPage == null) {
         Response.writeError(request, response, callback, 403);
      } else {
         if (this._dispatch) {
            return this.dispatch(this._formErrorPage, request, response, callback);
         }

         Response.sendRedirect(
            request, response, callback, this.encodeURL(URIUtil.addPaths(request.getContext().getContextPath(), this._formErrorPage), request), true
         );
      }

      return AuthenticationState.SEND_FAILURE;
   }

   private AuthenticationState sendChallenge(Request request, Response response, Callback callback) {
      if (this._dispatch) {
         return this.dispatch(this._formLoginPage, request, response, callback);
      } else {
         Response.sendRedirect(
            request, response, callback, this.encodeURL(URIUtil.addPaths(request.getContext().getContextPath(), this._formLoginPage), request), true
         );
         return AuthenticationState.SEND_FAILURE;
      }
   }

   public boolean isJSecurityCheck(String uri) {
      int jsc = uri.indexOf("/j_security_check");
      if (jsc < 0) {
         return false;
      } else {
         int e = jsc + "/j_security_check".length();
         if (e == uri.length()) {
            return true;
         } else {
            char c = uri.charAt(e);
            return c == ';' || c == '#' || c == '/' || c == '?';
         }
      }
   }

   public boolean isLoginOrErrorPage(String pathInContext) {
      return pathInContext != null && (pathInContext.equals(this._formErrorPath) || pathInContext.equals(this._formLoginPath));
   }
}
