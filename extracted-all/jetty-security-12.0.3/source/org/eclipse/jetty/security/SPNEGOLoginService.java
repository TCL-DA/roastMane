package org.eclipse.jetty.security;

import java.io.Serializable;
import java.net.InetAddress;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Function;
import javax.security.auth.Subject;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Session;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.security.SecurityUtils;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SPNEGOLoginService extends ContainerLifeCycle implements LoginService {
   private static final Logger LOG = LoggerFactory.getLogger(SPNEGOLoginService.class);
   private final GSSManager _gssManager = GSSManager.getInstance();
   private final String _realm;
   private final LoginService _loginService;
   private IdentityService _identityService = new DefaultIdentityService();
   private String _serviceName;
   private Path _keyTabPath;
   private String _hostName;
   private SPNEGOLoginService.SPNEGOContext _context;

   public SPNEGOLoginService(String realm, LoginService loginService) {
      this._realm = realm;
      this._loginService = loginService;
      this.addBean(this._loginService);
   }

   @Override
   public String getName() {
      return this._realm;
   }

   public Path getKeyTabPath() {
      return this._keyTabPath;
   }

   public void setKeyTabPath(Path keyTabFile) {
      this._keyTabPath = keyTabFile;
   }

   public String getServiceName() {
      return this._serviceName;
   }

   public void setServiceName(String serviceName) {
      this._serviceName = serviceName;
   }

   public String getHostName() {
      return this._hostName;
   }

   public void setHostName(String hostName) {
      this._hostName = hostName;
   }

   protected void doStart() throws Exception {
      if (this._hostName == null) {
         this._hostName = InetAddress.getLocalHost().getCanonicalHostName();
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("Retrieving credentials for service {}/{}", this.getServiceName(), this.getHostName());
      }

      LoginContext loginContext = new LoginContext("", null, null, new SPNEGOLoginService.SPNEGOConfiguration());
      loginContext.login();
      Subject subject = loginContext.getSubject();
      this._context = (SPNEGOLoginService.SPNEGOContext)SecurityUtils.doAs(subject, this.newSpnegoContext(subject));
      super.doStart();
   }

   private Callable<SPNEGOLoginService.SPNEGOContext> newSpnegoContext(Subject subject) {
      return () -> {
         try {
            GSSName serviceName = this._gssManager.createName(this.getServiceName() + "@" + this.getHostName(), GSSName.NT_HOSTBASED_SERVICE);
            Oid kerberosOid = new Oid("1.2.840.113554.1.2.2");
            Oid spnegoOid = new Oid("1.3.6.1.5.5.2");
            Oid[] mechanisms = new Oid[]{kerberosOid, spnegoOid};
            GSSCredential serviceCredential = this._gssManager.createCredential(serviceName, 0, mechanisms, 2);
            SPNEGOLoginService.SPNEGOContext context = new SPNEGOLoginService.SPNEGOContext();
            context._subject = subject;
            context._serviceCredential = serviceCredential;
            return context;
         } catch (GSSException var8) {
            throw new RuntimeException(var8);
         }
      };
   }

   @Override
   public UserIdentity login(String username, Object credentials, Request request, Function<Boolean, Session> getOrCreateSession) {
      Subject subject = this._context._subject;
      Session httpSession = getOrCreateSession.apply(false);
      GSSContext gssContext = null;
      if (httpSession != null) {
         SPNEGOLoginService.GSSContextHolder holder = (SPNEGOLoginService.GSSContextHolder)httpSession.getAttribute(
            SPNEGOLoginService.GSSContextHolder.ATTRIBUTE
         );
         gssContext = holder == null ? null : holder.gssContext;
      }

      if (gssContext == null) {
         gssContext = (GSSContext)SecurityUtils.doAs(subject, this.newGSSContext());
      }

      byte[] input = Base64.getDecoder().decode((String)credentials);
      byte[] output = (byte[])SecurityUtils.doAs(this._context._subject, this.acceptGSSContext(gssContext, input));
      String token = Base64.getEncoder().encodeToString(output);
      String userName = this.toUserName(gssContext);
      SPNEGOUserPrincipal principal = new SPNEGOUserPrincipal(userName, token);
      if (gssContext.isEstablished()) {
         if (httpSession != null) {
            httpSession.removeAttribute(SPNEGOLoginService.GSSContextHolder.ATTRIBUTE);
         }

         return this._loginService.getUserIdentity(subject, principal, false);
      } else {
         if (httpSession == null) {
            httpSession = getOrCreateSession.apply(true);
         }

         SPNEGOLoginService.GSSContextHolder holder = new SPNEGOLoginService.GSSContextHolder(gssContext);
         httpSession.setAttribute(SPNEGOLoginService.GSSContextHolder.ATTRIBUTE, holder);
         return new RoleDelegateUserIdentity(subject, principal, null);
      }
   }

   private Callable<GSSContext> newGSSContext() {
      return () -> {
         try {
            return this._gssManager.createContext(this._context._serviceCredential);
         } catch (GSSException var2) {
            throw new RuntimeException(var2);
         }
      };
   }

   private Callable<byte[]> acceptGSSContext(GSSContext gssContext, byte[] token) {
      return () -> {
         try {
            return gssContext.acceptSecContext(token, 0, token.length);
         } catch (GSSException var3) {
            throw new RuntimeException(var3);
         }
      };
   }

   private String toUserName(GSSContext gssContext) {
      try {
         String name = gssContext.getSrcName().toString();
         int at = name.indexOf(64);
         return at < 0 ? name : name.substring(0, at);
      } catch (GSSException var4) {
         throw new RuntimeException(var4);
      }
   }

   @Override
   public boolean validate(UserIdentity user) {
      return false;
   }

   @Override
   public IdentityService getIdentityService() {
      return this._identityService;
   }

   @Override
   public void setIdentityService(IdentityService identityService) {
      this._identityService = identityService;
   }

   @Override
   public void logout(UserIdentity user) {
   }

   private static class GSSContextHolder implements Serializable {
      public static final String ATTRIBUTE = SPNEGOLoginService.GSSContextHolder.class.getName();
      private final transient GSSContext gssContext;

      private GSSContextHolder(GSSContext gssContext) {
         this.gssContext = gssContext;
      }
   }

   private class SPNEGOConfiguration extends Configuration {
      @Override
      public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
         String principal = SPNEGOLoginService.this.getServiceName() + "/" + SPNEGOLoginService.this.getHostName();
         Map<String, Object> options = new HashMap<>();
         if (SPNEGOLoginService.LOG.isDebugEnabled()) {
            options.put("debug", "true");
         }

         options.put("doNotPrompt", "true");
         options.put("refreshKrb5Config", "true");
         options.put("principal", principal);
         options.put("useKeyTab", "true");
         Path keyTabPath = SPNEGOLoginService.this.getKeyTabPath();
         if (keyTabPath != null) {
            options.put("keyTab", keyTabPath.toAbsolutePath().toString());
         }

         options.put("storeKey", "true");
         options.put("isInitiator", "false");
         String moduleClass = "com.sun.security.auth.module.Krb5LoginModule";
         AppConfigurationEntry config = new AppConfigurationEntry(moduleClass, LoginModuleControlFlag.REQUIRED, options);
         return new AppConfigurationEntry[]{config};
      }
   }

   private static class SPNEGOContext {
      private Subject _subject;
      private GSSCredential _serviceCredential;
   }
}
