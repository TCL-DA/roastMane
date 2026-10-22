package org.eclipse.jetty.security;

import java.util.Collection;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.security.authentication.DigestAuthenticator;
import org.eclipse.jetty.security.authentication.FormAuthenticator;
import org.eclipse.jetty.security.authentication.SPNEGOAuthenticator;
import org.eclipse.jetty.security.authentication.SslClientCertAuthenticator;
import org.eclipse.jetty.server.Context;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class DefaultAuthenticatorFactory implements Authenticator.Factory {
   @Override
   public Authenticator getAuthenticator(Server server, Context context, Authenticator.Configuration configuration) {
      String auth = configuration.getAuthenticationType();
      Authenticator authenticator = null;
      if ("BASIC".equalsIgnoreCase(auth)) {
         authenticator = new BasicAuthenticator();
      } else if ("DIGEST".equalsIgnoreCase(auth)) {
         authenticator = new DigestAuthenticator();
      } else if ("FORM".equalsIgnoreCase(auth)) {
         authenticator = new FormAuthenticator();
      } else if ("SPNEGO".equalsIgnoreCase(auth)) {
         authenticator = new SPNEGOAuthenticator();
      } else if ("NEGOTIATE".equalsIgnoreCase(auth)) {
         authenticator = new SPNEGOAuthenticator("NEGOTIATE");
      }

      if ("CLIENT-CERT".equalsIgnoreCase(auth)) {
         Collection<SslContextFactory> sslContextFactories = server.getBeans(SslContextFactory.class);
         if (sslContextFactories.size() != 1) {
            throw new IllegalStateException("SslClientCertAuthenticator requires a single SslContextFactory instances.");
         }

         authenticator = new SslClientCertAuthenticator(sslContextFactories.iterator().next());
      }

      return authenticator;
   }
}
