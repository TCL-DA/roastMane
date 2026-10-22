package io.netty.handler.ssl;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.util.LinkedHashSet;
import java.util.List;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import org.eclipse.jetty.alpn.ALPN;
import org.eclipse.jetty.alpn.ALPN.ClientProvider;
import org.eclipse.jetty.alpn.ALPN.ServerProvider;

abstract class JettyAlpnSslEngine extends JdkSslEngine {
   private static final boolean available = initAvailable();

   static boolean isAvailable() {
      return available;
   }

   private static boolean initAvailable() {
      if (PlatformDependent.javaVersion() <= 8) {
         try {
            Class.forName("sun.security.ssl.ALPNExtension", true, null);
            return true;
         } catch (Throwable var1) {
         }
      }

      return false;
   }

   static JettyAlpnSslEngine newClientEngine(SSLEngine engine, JdkApplicationProtocolNegotiator applicationNegotiator) {
      return new JettyAlpnSslEngine.ClientEngine(engine, applicationNegotiator);
   }

   static JettyAlpnSslEngine newServerEngine(SSLEngine engine, JdkApplicationProtocolNegotiator applicationNegotiator) {
      return new JettyAlpnSslEngine.ServerEngine(engine, applicationNegotiator);
   }

   private JettyAlpnSslEngine(SSLEngine engine) {
      super(engine);
   }

   private static final class ClientEngine extends JettyAlpnSslEngine {
      ClientEngine(SSLEngine engine, final JdkApplicationProtocolNegotiator applicationNegotiator) {
         super(engine);
         ObjectUtil.checkNotNull(applicationNegotiator, "applicationNegotiator");
         final JdkApplicationProtocolNegotiator.ProtocolSelectionListener protocolListener = (JdkApplicationProtocolNegotiator.ProtocolSelectionListener)ObjectUtil.checkNotNull(
            applicationNegotiator.protocolListenerFactory().newListener(this, applicationNegotiator.protocols()), "protocolListener"
         );
         ALPN.put(engine, new ClientProvider() {
            public List<String> protocols() {
               return applicationNegotiator.protocols();
            }

            public void selected(String protocol) throws SSLException {
               try {
                  protocolListener.selected(protocol);
               } catch (Throwable var3) {
                  throw SslUtils.toSSLHandshakeException(var3);
               }
            }

            public void unsupported() {
               protocolListener.unsupported();
            }
         });
      }

      @Override
      public void closeInbound() throws SSLException {
         try {
            ALPN.remove(this.getWrappedEngine());
         } finally {
            super.closeInbound();
         }
      }

      @Override
      public void closeOutbound() {
         try {
            ALPN.remove(this.getWrappedEngine());
         } finally {
            super.closeOutbound();
         }
      }
   }

   private static final class ServerEngine extends JettyAlpnSslEngine {
      ServerEngine(SSLEngine engine, JdkApplicationProtocolNegotiator applicationNegotiator) {
         super(engine);
         ObjectUtil.checkNotNull(applicationNegotiator, "applicationNegotiator");
         final JdkApplicationProtocolNegotiator.ProtocolSelector protocolSelector = (JdkApplicationProtocolNegotiator.ProtocolSelector)ObjectUtil.checkNotNull(
            applicationNegotiator.protocolSelectorFactory().newSelector(this, new LinkedHashSet<>(applicationNegotiator.protocols())), "protocolSelector"
         );
         ALPN.put(engine, new ServerProvider() {
            public String select(List<String> protocols) throws SSLException {
               try {
                  return protocolSelector.select(protocols);
               } catch (Throwable var3) {
                  throw SslUtils.toSSLHandshakeException(var3);
               }
            }

            public void unsupported() {
               protocolSelector.unsupported();
            }
         });
      }

      @Override
      public void closeInbound() throws SSLException {
         try {
            ALPN.remove(this.getWrappedEngine());
         } finally {
            super.closeInbound();
         }
      }

      @Override
      public void closeOutbound() {
         try {
            ALPN.remove(this.getWrappedEngine());
         } finally {
            super.closeOutbound();
         }
      }
   }
}
