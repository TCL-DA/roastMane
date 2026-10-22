package org.eclipse.jetty.security.jaas.spi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import javax.naming.AuthenticationException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import org.eclipse.jetty.security.UserPrincipal;
import org.eclipse.jetty.security.jaas.callback.ObjectCallback;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.TypeUtil;
import org.eclipse.jetty.util.security.Credential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdapLoginModule extends AbstractLoginModule {
   private static final Logger LOG = LoggerFactory.getLogger(LdapLoginModule.class);
   private String _hostname;
   private int _port;
   private String _authenticationMethod;
   private String _contextFactory;
   private String _bindDn;
   private String _bindPassword;
   private String _userObjectClass = "inetOrgPerson";
   private String _userRdnAttribute = "uid";
   private String _userIdAttribute = "cn";
   private String _userPasswordAttribute = "userPassword";
   private String _userBaseDn;
   private String _roleBaseDn;
   private String _roleObjectClass = "groupOfUniqueNames";
   private String _roleMemberAttribute = "uniqueMember";
   private String _roleNameAttribute = "roleName";
   private boolean _debug;
   private boolean _forceBindingLogin = false;
   private boolean _useLdaps = false;
   private DirContext _rootContext;

   @Override
   public AbstractLoginModule.JAASUser getUser(String username) throws Exception {
      Attributes attributes = this.getUserAttributes(username);
      String pwdCredential = this.getUserCredentials(attributes);
      if (pwdCredential == null) {
         return null;
      } else {
         pwdCredential = convertCredentialLdapToJetty(pwdCredential);
         Credential credential = Credential.getCredential(pwdCredential);
         return new LdapLoginModule.LDAPUser(new UserPrincipal(username, credential), attributes);
      }
   }

   protected String doRFC2254Encoding(String inputString) {
      StringBuffer buf = new StringBuffer(inputString.length());

      for (int i = 0; i < inputString.length(); i++) {
         char c = inputString.charAt(i);
         switch (c) {
            case '\u0000':
               buf.append("\\00");
               break;
            case '(':
               buf.append("\\28");
               break;
            case ')':
               buf.append("\\29");
               break;
            case '*':
               buf.append("\\2a");
               break;
            case '\\':
               buf.append("\\5c");
               break;
            default:
               buf.append(c);
         }
      }

      return buf.toString();
   }

   private Attributes getUserAttributes(String username) throws LoginException {
      SearchResult result = this.findUser(username);
      return result.getAttributes();
   }

   private String getUserCredentials(Attributes attributes) throws LoginException {
      String ldapCredential = null;
      Attribute attribute = attributes.get(this._userPasswordAttribute);
      if (attribute != null) {
         try {
            byte[] value = (byte[])attribute.get();
            ldapCredential = new String(value);
         } catch (NamingException var5) {
            LOG.debug("no password available under attribute: {}", this._userPasswordAttribute);
         }
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("user cred is: {}", ldapCredential);
      }

      return ldapCredential;
   }

   private List<String> getUserRoles(DirContext dirContext, String username, Attributes attributes) throws LoginException, NamingException {
      String rdnValue = username;
      Attribute attribute = attributes.get(this._userRdnAttribute);
      if (attribute != null) {
         try {
            rdnValue = (String)attribute.get();
         } catch (NamingException var9) {
         }
      }

      String filter = "({0}={1})";
      Object[] filterArguments = new Object[]{this._userRdnAttribute, rdnValue};
      SearchResult searchResult = this.findUser(dirContext, filter, filterArguments);
      return this.getUserRolesByDn(dirContext, searchResult.getNameInNamespace());
   }

   private List<String> getUserRolesByDn(DirContext dirContext, String userDn) throws NamingException {
      List<String> roleList = new ArrayList<>();
      if (dirContext != null && this._roleBaseDn != null && this._roleMemberAttribute != null && this._roleObjectClass != null) {
         SearchControls ctls = new SearchControls();
         ctls.setDerefLinkFlag(true);
         ctls.setSearchScope(2);
         ctls.setReturningAttributes(new String[]{this._roleNameAttribute});
         String filter = "(&(objectClass={0})({1}={2}))";
         Object[] filterArguments = new Object[]{this._roleObjectClass, this._roleMemberAttribute, userDn};
         NamingEnumeration<SearchResult> results = dirContext.search(this._roleBaseDn, filter, filterArguments, ctls);
         if (LOG.isDebugEnabled()) {
            LOG.debug("Found user roles?: {}", results.hasMoreElements());
         }

         while (results.hasMoreElements()) {
            SearchResult result = results.nextElement();
            Attributes attributes = result.getAttributes();
            if (attributes != null) {
               Attribute roleAttribute = attributes.get(this._roleNameAttribute);
               if (roleAttribute != null) {
                  NamingEnumeration<?> roles = roleAttribute.getAll();

                  while (roles.hasMore()) {
                     roleList.add(roles.next().toString());
                  }
               }
            }
         }

         return roleList;
      } else {
         return roleList;
      }
   }

   @Override
   public boolean login() throws LoginException {
      try {
         if (this.getCallbackHandler() == null) {
            throw new LoginException("No callback handler");
         } else {
            Callback[] callbacks = this.configureCallbacks();
            this.getCallbackHandler().handle(callbacks);
            String webUserName = ((NameCallback)callbacks[0]).getName();
            Object webCredential = ((ObjectCallback)callbacks[1]).getObject();
            if (webUserName != null && webCredential != null) {
               boolean authed = false;
               if (this._forceBindingLogin) {
                  authed = this.bindingLogin(webUserName, webCredential);
               } else {
                  AbstractLoginModule.JAASUser userInfo = this.getUser(webUserName);
                  if (userInfo == null) {
                     this.setAuthenticated(false);
                     return false;
                  }

                  this.setCurrentUser(userInfo);
                  if (webCredential instanceof String) {
                     authed = this.credentialLogin(Credential.getCredential((String)webCredential));
                  } else {
                     authed = this.credentialLogin(webCredential);
                  }
               }

               if (authed) {
                  this.getCurrentUser().fetchRoles();
               }

               return authed;
            } else {
               this.setAuthenticated(false);
               return this.isAuthenticated();
            }
         }
      } catch (UnsupportedCallbackException var6) {
         throw new LoginException("Error obtaining callback information.");
      } catch (IOException var7) {
         if (this._debug) {
            LOG.info("Login failure", var7);
         }

         throw new LoginException("IO Error performing login.");
      } catch (AuthenticationException var8) {
         if (this._debug) {
            LOG.info("Login failure", var8);
         }

         return false;
      } catch (LoginException var9) {
         throw var9;
      } catch (Exception var10) {
         if (this._debug) {
            LOG.info("Login failure", var10);
         }

         throw new LoginException("Error obtaining user info");
      }
   }

   protected boolean credentialLogin(Object webCredential) throws LoginException {
      this.setAuthenticated(this.getCurrentUser().checkCredential(webCredential));
      return this.isAuthenticated();
   }

   public boolean bindingLogin(String username, Object password) throws LoginException {
      SearchResult searchResult = this.findUser(username);
      String userDn = searchResult.getNameInNamespace();
      LOG.info("Attempting authentication: {}", userDn);
      Hashtable<Object, Object> environment = this.getEnvironment();
      if (userDn != null && !"".equals(userDn)) {
         environment.put("java.naming.security.principal", userDn);
         if (password != null && !"".equals(password)) {
            environment.put("java.naming.security.credentials", password);

            try {
               DirContext dirContext = new InitialDirContext(environment);
               this.setCurrentUser(new LdapLoginModule.LDAPBindingUser(new UserPrincipal(username, null), dirContext, userDn));
               this.setAuthenticated(true);
               return true;
            } catch (AuthenticationException var7) {
               throw new FailedLoginException(var7.getMessage());
            } catch (NamingException var8) {
               throw new FailedLoginException(var8.getMessage());
            }
         } else {
            throw new FailedLoginException("password may not be empty");
         }
      } else {
         throw new FailedLoginException("username may not be empty");
      }
   }

   private SearchResult findUser(String username) throws LoginException {
      String filter = "(&(objectClass={0})({1}={2}))";
      if (LOG.isDebugEnabled()) {
         LOG.debug("Searching for user {} with filter: '{}' from base dn: {}", new Object[]{username, filter, this._userBaseDn});
      }

      Object[] filterArguments = new Object[]{this._userObjectClass, this._userIdAttribute, username};
      return this.findUser(this._rootContext, filter, filterArguments);
   }

   private SearchResult findUser(DirContext dirContext, String filter, Object[] filterArguments) throws LoginException {
      SearchControls ctls = new SearchControls();
      ctls.setDerefLinkFlag(true);
      ctls.setSearchScope(2);

      NamingEnumeration<SearchResult> results;
      try {
         results = this._rootContext.search(this._userBaseDn, filter, filterArguments, ctls);
      } catch (NamingException var7) {
         throw new FailedLoginException(var7.getMessage());
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("Found user?: {}", results.hasMoreElements());
      }

      if (!results.hasMoreElements()) {
         throw new FailedLoginException("User not found.");
      } else {
         SearchResult searchResult = results.nextElement();
         if (results.hasMoreElements()) {
            throw new FailedLoginException("Search result contains ambiguous entries");
         } else {
            return searchResult;
         }
      }
   }

   @Override
   public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
      super.initialize(subject, callbackHandler, sharedState, options);
      this._hostname = (String)options.get("hostname");
      this._port = Integer.parseInt((String)options.get("port"));
      this._contextFactory = (String)options.get("contextFactory");
      this._bindDn = (String)options.get("bindDn");
      this._bindPassword = (String)options.get("bindPassword");
      this._authenticationMethod = (String)options.get("authenticationMethod");
      this._userBaseDn = (String)options.get("userBaseDn");
      this._roleBaseDn = (String)options.get("roleBaseDn");
      if (options.containsKey("forceBindingLogin")) {
         this._forceBindingLogin = Boolean.parseBoolean((String)options.get("forceBindingLogin"));
      }

      if (options.containsKey("useLdaps")) {
         this._useLdaps = Boolean.parseBoolean((String)options.get("useLdaps"));
      }

      this._userObjectClass = this.getOption(options, "userObjectClass", this._userObjectClass);
      this._userRdnAttribute = this.getOption(options, "userRdnAttribute", this._userRdnAttribute);
      this._userIdAttribute = this.getOption(options, "userIdAttribute", this._userIdAttribute);
      this._userPasswordAttribute = this.getOption(options, "userPasswordAttribute", this._userPasswordAttribute);
      this._roleObjectClass = this.getOption(options, "roleObjectClass", this._roleObjectClass);
      this._roleMemberAttribute = this.getOption(options, "roleMemberAttribute", this._roleMemberAttribute);
      this._roleNameAttribute = this.getOption(options, "roleNameAttribute", this._roleNameAttribute);
      this._debug = Boolean.parseBoolean(String.valueOf(this.getOption(options, "debug", Boolean.toString(this._debug))));

      try {
         this._rootContext = new InitialDirContext(this.getEnvironment());
      } catch (NamingException var6) {
         throw new IllegalStateException("Unable to establish root context", var6);
      }
   }

   @Override
   public boolean commit() throws LoginException {
      try {
         this._rootContext.close();
      } catch (NamingException var2) {
         throw new LoginException("error closing root context: " + var2.getMessage());
      }

      return super.commit();
   }

   @Override
   public boolean abort() throws LoginException {
      try {
         this._rootContext.close();
      } catch (NamingException var2) {
         throw new LoginException("error closing root context: " + var2.getMessage());
      }

      return super.abort();
   }

   private String getOption(Map<String, ?> options, String key, String defaultValue) {
      Object value = options.get(key);
      return value == null ? defaultValue : (String)value;
   }

   public Hashtable<Object, Object> getEnvironment() {
      Properties env = new Properties();
      env.put("java.naming.factory.initial", this._contextFactory);
      if (this._hostname != null) {
         env.put("java.naming.provider.url", (this._useLdaps ? "ldaps://" : "ldap://") + this._hostname + (this._port == 0 ? "" : ":" + this._port) + "/");
      }

      if (this._authenticationMethod != null) {
         env.put("java.naming.security.authentication", this._authenticationMethod);
      }

      if (this._bindDn != null) {
         env.put("java.naming.security.principal", this._bindDn);
      }

      if (this._bindPassword != null) {
         env.put("java.naming.security.credentials", this._bindPassword);
      }

      return env;
   }

   public static String convertCredentialLdapToJetty(String encryptedPassword) {
      if (encryptedPassword == null) {
         return null;
      } else if (encryptedPassword.toUpperCase(Locale.ENGLISH).startsWith("{MD5}")) {
         String src = encryptedPassword.substring("{MD5}".length(), encryptedPassword.length());
         return "MD5:" + base64ToHex(src);
      } else {
         return encryptedPassword.toUpperCase(Locale.ENGLISH).startsWith("{CRYPT}")
            ? "CRYPT:" + encryptedPassword.substring("{CRYPT}".length(), encryptedPassword.length())
            : encryptedPassword;
      }
   }

   private static String base64ToHex(String src) {
      byte[] bytes = Base64.getDecoder().decode(src);
      return TypeUtil.toString(bytes, 16);
   }

   private static String hexToBase64(String src) {
      byte[] bytes = StringUtil.fromHexString(src);
      return Base64.getEncoder().encodeToString(bytes);
   }

   public class LDAPBindingUser extends AbstractLoginModule.JAASUser {
      DirContext _context;
      String _userDn;

      public LDAPBindingUser(UserPrincipal user, DirContext context, String userDn) {
         super(user);
         this._context = context;
         this._userDn = userDn;
      }

      @Override
      public List<String> doFetchRoles() throws Exception {
         return LdapLoginModule.this.getUserRolesByDn(this._context, this._userDn);
      }
   }

   public class LDAPUser extends AbstractLoginModule.JAASUser {
      Attributes attributes;

      public LDAPUser(UserPrincipal user, Attributes attributes) {
         super(user);
         this.attributes = attributes;
      }

      @Override
      public List<String> doFetchRoles() throws Exception {
         return LdapLoginModule.this.getUserRoles(LdapLoginModule.this._rootContext, this.getUserName(), this.attributes);
      }
   }
}
