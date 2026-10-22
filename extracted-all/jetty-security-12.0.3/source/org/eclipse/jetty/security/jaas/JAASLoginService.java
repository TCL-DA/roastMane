package org.eclipse.jetty.security.jaas;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import org.eclipse.jetty.security.DefaultIdentityService;
import org.eclipse.jetty.security.IdentityService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.security.UserIdentity;
import org.eclipse.jetty.security.jaas.callback.DefaultCallbackHandler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Session;
import org.eclipse.jetty.util.ArrayUtil;
import org.eclipse.jetty.util.Loader;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JAASLoginService extends ContainerLifeCycle implements LoginService {
   private static final Logger LOG = LoggerFactory.getLogger(JAASLoginService.class);
   public static final String DEFAULT_ROLE_CLASS_NAME = "org.eclipse.jetty.security.jaas.JAASRole";
   public static final String[] DEFAULT_ROLE_CLASS_NAMES = new String[]{"org.eclipse.jetty.security.jaas.JAASRole"};
   public static final ThreadLocal<JAASLoginService> INSTANCE = new ThreadLocal<>();
   protected String[] _roleClassNames = DEFAULT_ROLE_CLASS_NAMES;
   protected String _callbackHandlerClass;
   protected String _realmName;
   protected String _loginModuleName;
   protected JAASUserPrincipal _defaultUser = new JAASUserPrincipal(null, null, null);
   protected IdentityService _identityService;
   protected Configuration _configuration;

   public JAASLoginService() {
   }

   public JAASLoginService(String name) {
      this();
      this._realmName = name;
      this._loginModuleName = name;
   }

   @Override
   public String getName() {
      return this._realmName;
   }

   public void setName(String name) {
      this._realmName = name;
   }

   public Configuration getConfiguration() {
      return this._configuration;
   }

   public void setConfiguration(Configuration configuration) {
      this._configuration = configuration;
   }

   @Override
   public IdentityService getIdentityService() {
      return this._identityService;
   }

   @Override
   public void setIdentityService(IdentityService identityService) {
      this._identityService = identityService;
   }

   public void setLoginModuleName(String name) {
      this._loginModuleName = name;
   }

   public void setCallbackHandlerClass(String classname) {
      this._callbackHandlerClass = classname;
   }

   public void setRoleClassNames(String[] classnames) {
      if (classnames != null && classnames.length != 0) {
         this._roleClassNames = (String[])ArrayUtil.addToArray(classnames, "org.eclipse.jetty.security.jaas.JAASRole", String.class);
      } else {
         this._roleClassNames = DEFAULT_ROLE_CLASS_NAMES;
      }
   }

   public String[] getRoleClassNames() {
      return this._roleClassNames;
   }

   protected void doStart() throws Exception {
      if (this._identityService == null) {
         this._identityService = new DefaultIdentityService();
      }

      this.addBean(new PropertyUserStoreManager());
      super.doStart();
   }

   @Override
   public UserIdentity login(String username, Object credentials, Request request, Function<Boolean, Session> getOrCreateSession) {
      try {
         CallbackHandler callbackHandler;
         if (this._callbackHandlerClass == null) {
            callbackHandler = new DefaultCallbackHandler();
         } else {
            Class<?> clazz = Loader.loadClass(this._callbackHandlerClass);
            callbackHandler = (CallbackHandler)clazz.getDeclaredConstructor().newInstance();
         }

         if (callbackHandler instanceof DefaultCallbackHandler dch) {
            dch.setRequest(request);
            dch.setCredential(credentials);
            dch.setUserName(username);
         }

         Subject subject = new Subject();
         INSTANCE.set(this);
         LoginContext loginContext = this._configuration == null
            ? new LoginContext(this._loginModuleName, subject, callbackHandler)
            : new LoginContext(this._loginModuleName, subject, callbackHandler, this._configuration);
         loginContext.login();
         JAASUserPrincipal userPrincipal = new JAASUserPrincipal(this.getUserName(callbackHandler), subject, loginContext);
         subject.getPrincipals().add(userPrincipal);
         return this._identityService.newUserIdentity(subject, userPrincipal, this.getGroups(subject));
      } catch (FailedLoginException var14) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Login failed", var14);
         }
      } catch (Exception var15) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Login error", var15);
         }
      } finally {
         INSTANCE.remove();
      }

      return null;
   }

   @Override
   public boolean validate(UserIdentity user) {
      return true;
   }

   private String getUserName(CallbackHandler callbackHandler) throws IOException, UnsupportedCallbackException {
      NameCallback nameCallback = new NameCallback("foo");
      callbackHandler.handle(new Callback[]{nameCallback});
      return nameCallback.getName();
   }

   @Override
   public void logout(UserIdentity user) {
      Set<JAASUserPrincipal> userPrincipals = user.getSubject().getPrincipals(JAASUserPrincipal.class);
      LoginContext loginContext = userPrincipals.iterator().next().getLoginContext();

      try {
         loginContext.logout();
      } catch (LoginException var5) {
         LOG.warn("Failed to logout {}", user, var5);
      }
   }

   protected String[] getGroups(Subject subject) {
      Collection<String> groups = new LinkedHashSet<>();

      for (Principal principal : subject.getPrincipals()) {
         if (isRoleClass(principal.getClass(), Arrays.asList(this.getRoleClassNames()))) {
            groups.add(principal.getName());
         }
      }

      return groups.toArray(new String[0]);
   }

   private static boolean isRoleClass(Class<?> clazz, List<String> roleClassNames) {
      Class<?> c = clazz;

      List<String> classnames;
      for (classnames = new ArrayList<>(); c != null; c = c.getSuperclass()) {
         classnames.add(c.getName());
         Arrays.stream(c.getInterfaces()).map(Class::getName).forEach(classnames::add);
      }

      return roleClassNames.stream().anyMatch(classnames::contains);
   }
}
