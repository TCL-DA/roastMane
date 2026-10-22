package org.eclipse.jetty.security.authentication;

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Objects;
import org.eclipse.jetty.security.AuthenticationState;
import org.eclipse.jetty.security.ServerAuthException;
import org.eclipse.jetty.security.UserIdentity;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.SecureRequestCustomizer.SslSessionData;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class SslClientCertAuthenticator extends LoginAuthenticator {
   private final SslContextFactory sslContextFactory;
   private boolean validateCerts = true;

   public SslClientCertAuthenticator(SslContextFactory sslContextFactory) {
      this.sslContextFactory = Objects.requireNonNull(sslContextFactory);
   }

   @Override
   public String getAuthenticationType() {
      return "CLIENT_CERT";
   }

   @Override
   public AuthenticationState validateRequest(Request req, Response res, Callback callback) throws ServerAuthException {
      SslSessionData sslSessionData = (SslSessionData)req.getAttribute(SecureRequestCustomizer.DEFAULT_SSL_SESSION_DATA_ATTRIBUTE);
      if (sslSessionData == null) {
         Response.writeError(req, res, callback, 403);
         return AuthenticationState.SEND_FAILURE;
      } else {
         X509Certificate[] certs = sslSessionData.peerCertificates();

         try {
            if (certs != null && certs.length > 0) {
               if (this.validateCerts) {
                  this.sslContextFactory.validateCerts(certs);
               }

               for (X509Certificate cert : certs) {
                  if (cert != null) {
                     Principal principal = cert.getSubjectDN();
                     if (principal == null) {
                        principal = cert.getIssuerDN();
                     }

                     String username = principal == null ? "clientcert" : principal.getName();
                     UserIdentity user = this.login(username, "", req, res);
                     if (user != null) {
                        return new LoginAuthenticator.UserAuthenticationSucceeded(this.getAuthenticationType(), user);
                     }

                     user = this.login(username, null, req, res);
                     if (user != null) {
                        return new LoginAuthenticator.UserAuthenticationSucceeded(this.getAuthenticationType(), user);
                     }

                     char[] credential = Base64.getEncoder().encodeToString(cert.getSignature()).toCharArray();
                     user = this.login(username, credential, req, res);
                     if (user != null) {
                        return new LoginAuthenticator.UserAuthenticationSucceeded(this.getAuthenticationType(), user);
                     }
                  }
               }
            }

            if (!AuthenticationState.Deferred.isDeferred(res)) {
               Response.writeError(req, res, callback, 403);
               return AuthenticationState.SEND_FAILURE;
            } else {
               return null;
            }
         } catch (Exception var14) {
            throw new ServerAuthException(var14.getMessage());
         }
      }
   }

   public boolean isValidateCerts() {
      return this.validateCerts;
   }

   public void setValidateCerts(boolean validateCerts) {
      this.validateCerts = validateCerts;
   }
}
