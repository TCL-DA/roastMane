package org.eclipse.jetty.client;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.util.Attributes;
import org.eclipse.jetty.util.security.SecurityUtils;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SPNEGOAuthentication extends AbstractAuthentication {
   private static final Logger LOG = LoggerFactory.getLogger(SPNEGOAuthentication.class);
   private static final String NEGOTIATE = HttpHeader.NEGOTIATE.asString();
   private final GSSManager gssManager = GSSManager.getInstance();
   private String userName;
   private String userPassword;
   private Path userKeyTabPath;
   private String serviceName;
   private boolean useTicketCache;
   private Path ticketCachePath;
   private boolean renewTGT;

   public SPNEGOAuthentication(URI uri) {
      super(uri, "<<ANY_REALM>>");
   }

   @Override
   public String getType() {
      return NEGOTIATE;
   }

   public String getUserName() {
      return this.userName;
   }

   public void setUserName(String userName) {
      this.userName = userName;
   }

   public String getUserPassword() {
      return this.userPassword;
   }

   public void setUserPassword(String userPassword) {
      this.userPassword = userPassword;
   }

   public Path getUserKeyTabPath() {
      return this.userKeyTabPath;
   }

   public void setUserKeyTabPath(Path userKeyTabPath) {
      this.userKeyTabPath = userKeyTabPath;
   }

   public String getServiceName() {
      return this.serviceName;
   }

   public void setServiceName(String serviceName) {
      this.serviceName = serviceName;
   }

   public boolean isUseTicketCache() {
      return this.useTicketCache;
   }

   public void setUseTicketCache(boolean useTicketCache) {
      this.useTicketCache = useTicketCache;
   }

   public Path getTicketCachePath() {
      return this.ticketCachePath;
   }

   public void setTicketCachePath(Path ticketCachePath) {
      this.ticketCachePath = ticketCachePath;
   }

   public boolean isRenewTGT() {
      return this.renewTGT;
   }

   public void setRenewTGT(boolean renewTGT) {
      this.renewTGT = renewTGT;
   }

   @Override
   public Authentication.Result authenticate(Request request, ContentResponse response, Authentication.HeaderInfo headerInfo, Attributes context) {
      SPNEGOAuthentication.SPNEGOContext spnegoContext = (SPNEGOAuthentication.SPNEGOContext)context.getAttribute(SPNEGOAuthentication.SPNEGOContext.ATTRIBUTE);
      if (LOG.isDebugEnabled()) {
         LOG.debug("Authenticate with context {}", spnegoContext);
      }

      if (spnegoContext == null) {
         spnegoContext = this.login();
         context.setAttribute(SPNEGOAuthentication.SPNEGOContext.ATTRIBUTE, spnegoContext);
      }

      String b64Input = headerInfo.getBase64();
      byte[] input = b64Input == null ? new byte[0] : Base64.getDecoder().decode(b64Input);
      byte[] output = (byte[])SecurityUtils.doAs(spnegoContext.subject, this.initGSSContext(spnegoContext, request.getHost(), input));
      String b64Output = output == null ? null : new String(Base64.getEncoder().encode(output));
      return new SPNEGOAuthentication.SPNEGOResult(null, b64Output);
   }

   private SPNEGOAuthentication.SPNEGOContext login() {
      try {
         String user = this.getUserName();
         if (LOG.isDebugEnabled()) {
            LOG.debug("Logging in user {}", user);
         }

         CallbackHandler callbackHandler = new SPNEGOAuthentication.PasswordCallbackHandler();
         LoginContext loginContext = new LoginContext("", null, callbackHandler, new SPNEGOAuthentication.SPNEGOConfiguration());
         loginContext.login();
         Subject subject = loginContext.getSubject();
         SPNEGOAuthentication.SPNEGOContext spnegoContext = new SPNEGOAuthentication.SPNEGOContext();
         spnegoContext.subject = subject;
         if (LOG.isDebugEnabled()) {
            LOG.debug("Initialized {}", spnegoContext);
         }

         return spnegoContext;
      } catch (LoginException var6) {
         throw new RuntimeException(var6);
      }
   }

   private Callable<byte[]> initGSSContext(SPNEGOAuthentication.SPNEGOContext spnegoContext, String host, byte[] bytes) {
      return () -> {
         try {
            GSSContext gssContext = spnegoContext.gssContext;
            if (gssContext == null) {
               String principal = this.getServiceName() + "@" + host;
               GSSName serviceName = this.gssManager.createName(principal, GSSName.NT_HOSTBASED_SERVICE);
               Oid spnegoOid = new Oid("1.3.6.1.5.5.2");
               gssContext = this.gssManager.createContext(serviceName, spnegoOid, null, Integer.MAX_VALUE);
               spnegoContext.gssContext = gssContext;
               gssContext.requestMutualAuth(true);
            }

            byte[] result = gssContext.initSecContext(bytes, 0, bytes.length);
            if (LOG.isDebugEnabled()) {
               LOG.debug("{} {}", gssContext.isEstablished() ? "Initialized" : "Initializing", gssContext);
            }

            return result;
         } catch (GSSException var8) {
            throw new RuntimeException(var8);
         }
      };
   }

   private class PasswordCallbackHandler implements CallbackHandler {
      @Override
      public void handle(Callback[] callbacks) throws IOException {
         PasswordCallback callback = Arrays.stream(callbacks)
            .filter(PasswordCallback.class::isInstance)
            .map(PasswordCallback.class::cast)
            .findAny()
            .filter(c -> c.getPrompt().contains(SPNEGOAuthentication.this.getUserName()))
            .orElseThrow(IOException::new);
         callback.setPassword(SPNEGOAuthentication.this.getUserPassword().toCharArray());
      }
   }

   private class SPNEGOConfiguration extends Configuration {
      @Override
      public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
         Map<String, Object> options = new HashMap<>();
         if (SPNEGOAuthentication.LOG.isDebugEnabled()) {
            options.put("debug", "true");
         }

         options.put("refreshKrb5Config", "true");
         options.put("principal", SPNEGOAuthentication.this.getUserName());
         options.put("isInitiator", "true");
         Path keyTabPath = SPNEGOAuthentication.this.getUserKeyTabPath();
         if (keyTabPath != null) {
            options.put("doNotPrompt", "true");
            options.put("useKeyTab", "true");
            options.put("keyTab", keyTabPath.toAbsolutePath().toString());
            options.put("storeKey", "true");
         }

         boolean useTicketCache = SPNEGOAuthentication.this.isUseTicketCache();
         if (useTicketCache) {
            options.put("useTicketCache", "true");
            Path ticketCachePath = SPNEGOAuthentication.this.getTicketCachePath();
            if (ticketCachePath != null) {
               options.put("ticketCache", ticketCachePath.toAbsolutePath().toString());
            }

            options.put("renewTGT", String.valueOf(SPNEGOAuthentication.this.isRenewTGT()));
         }

         String moduleClass = "com.sun.security.auth.module.Krb5LoginModule";
         AppConfigurationEntry config = new AppConfigurationEntry(moduleClass, LoginModuleControlFlag.REQUIRED, options);
         return new AppConfigurationEntry[]{config};
      }
   }

   private static class SPNEGOContext {
      private static final String ATTRIBUTE = SPNEGOAuthentication.SPNEGOContext.class.getName();
      private Subject subject;
      private GSSContext gssContext;

      @Override
      public String toString() {
         return String.format("%s@%x[context=%s]", this.getClass().getSimpleName(), this.hashCode(), this.gssContext);
      }
   }

   public static class SPNEGOResult implements Authentication.Result {
      private final URI uri;
      private final HttpHeader header;
      private final String value;

      public SPNEGOResult(URI uri, String token) {
         this(uri, HttpHeader.AUTHORIZATION, token);
      }

      public SPNEGOResult(URI uri, HttpHeader header, String token) {
         this.uri = uri;
         this.header = header;
         this.value = SPNEGOAuthentication.NEGOTIATE + (token == null ? "" : " " + token);
      }

      @Override
      public URI getURI() {
         return this.uri;
      }

      @Override
      public void apply(Request request) {
         request.headers(headers -> headers.add(this.header, this.value));
      }
   }
}
