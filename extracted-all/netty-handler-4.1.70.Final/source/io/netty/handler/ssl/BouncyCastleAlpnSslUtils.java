package io.netty.handler.ssl;

import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.SuppressJava6Requirement;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.List;
import java.util.function.BiFunction;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;

@SuppressJava6Requirement(
   reason = "Usage guarded by java version check"
)
final class BouncyCastleAlpnSslUtils {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(BouncyCastleAlpnSslUtils.class);
   private static final Class BC_SSL_PARAMETERS;
   private static final Method SET_PARAMETERS;
   private static final Method SET_APPLICATION_PROTOCOLS;
   private static final Method GET_APPLICATION_PROTOCOL;
   private static final Method GET_HANDSHAKE_APPLICATION_PROTOCOL;
   private static final Method SET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR;
   private static final Method GET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR;
   private static final Class BC_APPLICATION_PROTOCOL_SELECTOR;
   private static final Method BC_APPLICATION_PROTOCOL_SELECTOR_SELECT;

   private BouncyCastleAlpnSslUtils() {
   }

   static String getApplicationProtocol(SSLEngine sslEngine) {
      try {
         return (String)GET_APPLICATION_PROTOCOL.invoke(sslEngine);
      } catch (UnsupportedOperationException var2) {
         throw var2;
      } catch (Exception var3) {
         throw new IllegalStateException(var3);
      }
   }

   static void setApplicationProtocols(SSLEngine engine, List<String> supportedProtocols) {
      SSLParameters parameters = engine.getSSLParameters();
      String[] protocolArray = supportedProtocols.toArray(EmptyArrays.EMPTY_STRINGS);

      try {
         Object bcSslParameters = BC_SSL_PARAMETERS.newInstance();
         SET_APPLICATION_PROTOCOLS.invoke(bcSslParameters, protocolArray);
         SET_PARAMETERS.invoke(engine, bcSslParameters);
      } catch (UnsupportedOperationException var5) {
         throw var5;
      } catch (Exception var6) {
         throw new IllegalStateException(var6);
      }

      engine.setSSLParameters(parameters);
   }

   static String getHandshakeApplicationProtocol(SSLEngine sslEngine) {
      try {
         return (String)GET_HANDSHAKE_APPLICATION_PROTOCOL.invoke(sslEngine);
      } catch (UnsupportedOperationException var2) {
         throw var2;
      } catch (Exception var3) {
         throw new IllegalStateException(var3);
      }
   }

   static void setHandshakeApplicationProtocolSelector(SSLEngine engine, final BiFunction<SSLEngine, List<String>, String> selector) {
      try {
         Object selectorProxyInstance = Proxy.newProxyInstance(
            BouncyCastleAlpnSslUtils.class.getClassLoader(), new Class[]{BC_APPLICATION_PROTOCOL_SELECTOR}, new InvocationHandler() {
               @Override
               public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                  if (method.getName().equals("select")) {
                     try {
                        return selector.apply((SSLEngine)args[0], (List<String>)args[1]);
                     } catch (ClassCastException var5) {
                        throw new RuntimeException("BCApplicationProtocolSelector select method parameter of invalid type.", var5);
                     }
                  } else {
                     throw new UnsupportedOperationException(String.format("Method '%s' not supported.", method.getName()));
                  }
               }
            }
         );
         SET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR.invoke(engine, selectorProxyInstance);
      } catch (UnsupportedOperationException var3) {
         throw var3;
      } catch (Exception var4) {
         throw new IllegalStateException(var4);
      }
   }

   static BiFunction<SSLEngine, List<String>, String> getHandshakeApplicationProtocolSelector(SSLEngine engine) {
      try {
         final Object selector = GET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR.invoke(engine);
         return new BiFunction<SSLEngine, List<String>, String>() {
            public String apply(SSLEngine sslEngine, List<String> strings) {
               try {
                  return (String)BouncyCastleAlpnSslUtils.BC_APPLICATION_PROTOCOL_SELECTOR_SELECT.invoke(selector, sslEngine, strings);
               } catch (Exception var4) {
                  throw new RuntimeException("Could not call getHandshakeApplicationProtocolSelector", var4);
               }
            }
         };
      } catch (UnsupportedOperationException var2) {
         throw var2;
      } catch (Exception var3) {
         throw new IllegalStateException(var3);
      }
   }

   static {
      final Class bcSslParameters;
      Method setParameters;
      Method setApplicationProtocols;
      Method getApplicationProtocol;
      Method getHandshakeApplicationProtocol;
      Method setHandshakeApplicationProtocolSelector;
      Method getHandshakeApplicationProtocolSelector;
      Method bcApplicationProtocolSelectorSelect;
      Class bcApplicationProtocolSelector;
      try {
         final Class bcSslEngine = Class.forName("org.bouncycastle.jsse.BCSSLEngine");
         bcSslParameters = Class.forName("org.bouncycastle.jsse.BCSSLParameters");
         Object bcSslParametersInstance = bcSslParameters.newInstance();
         bcApplicationProtocolSelector = Class.forName("org.bouncycastle.jsse.BCApplicationProtocolSelector");
         final Class testBCApplicationProtocolSelector = bcApplicationProtocolSelector;
         bcApplicationProtocolSelectorSelect = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
               return testBCApplicationProtocolSelector.getMethod("select", Object.class, List.class);
            }
         });
         SSLContext context = SslUtils.getSSLContext("BCJSSE");
         SSLEngine engine = context.createSSLEngine();
         setParameters = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
               return bcSslEngine.getMethod("setParameters", bcSslParameters);
            }
         });
         setParameters.invoke(engine, bcSslParametersInstance);
         setApplicationProtocols = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
               return bcSslParameters.getMethod("setApplicationProtocols", String[].class);
            }
         });
         setApplicationProtocols.invoke(bcSslParametersInstance, EmptyArrays.EMPTY_STRINGS);
         getApplicationProtocol = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
               return bcSslEngine.getMethod("getApplicationProtocol");
            }
         });
         getApplicationProtocol.invoke(engine);
         getHandshakeApplicationProtocol = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
               return bcSslEngine.getMethod("getHandshakeApplicationProtocol");
            }
         });
         getHandshakeApplicationProtocol.invoke(engine);
         setHandshakeApplicationProtocolSelector = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
               return bcSslEngine.getMethod("setBCHandshakeApplicationProtocolSelector", testBCApplicationProtocolSelector);
            }
         });
         getHandshakeApplicationProtocolSelector = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
               return bcSslEngine.getMethod("getBCHandshakeApplicationProtocolSelector");
            }
         });
         getHandshakeApplicationProtocolSelector.invoke(engine);
      } catch (Throwable var16) {
         logger.error("Unable to initialize BouncyCastleAlpnSslUtils.", var16);
         bcSslParameters = null;
         setParameters = null;
         setApplicationProtocols = null;
         getApplicationProtocol = null;
         getHandshakeApplicationProtocol = null;
         setHandshakeApplicationProtocolSelector = null;
         getHandshakeApplicationProtocolSelector = null;
         bcApplicationProtocolSelectorSelect = null;
         bcApplicationProtocolSelector = null;
      }

      BC_SSL_PARAMETERS = bcSslParameters;
      SET_PARAMETERS = setParameters;
      SET_APPLICATION_PROTOCOLS = setApplicationProtocols;
      GET_APPLICATION_PROTOCOL = getApplicationProtocol;
      GET_HANDSHAKE_APPLICATION_PROTOCOL = getHandshakeApplicationProtocol;
      SET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR = setHandshakeApplicationProtocolSelector;
      GET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR = getHandshakeApplicationProtocolSelector;
      BC_APPLICATION_PROTOCOL_SELECTOR_SELECT = bcApplicationProtocolSelectorSelect;
      BC_APPLICATION_PROTOCOL_SELECTOR = bcApplicationProtocolSelector;
   }
}
