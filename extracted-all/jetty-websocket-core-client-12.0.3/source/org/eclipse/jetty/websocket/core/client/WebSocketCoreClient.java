package org.eclipse.jetty.websocket.core.client;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.Request.Listener;
import org.eclipse.jetty.util.DecoratedObjectFactory;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.websocket.core.CoreSession;
import org.eclipse.jetty.websocket.core.ExtensionConfig;
import org.eclipse.jetty.websocket.core.FrameHandler;
import org.eclipse.jetty.websocket.core.WebSocketComponents;
import org.eclipse.jetty.websocket.core.WebSocketExtensionRegistry;
import org.eclipse.jetty.websocket.core.client.internal.HttpClientProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketCoreClient extends ContainerLifeCycle {
   public static final String WEBSOCKET_CORECLIENT_ATTRIBUTE = WebSocketCoreClient.class.getName();
   private static final Logger LOG = LoggerFactory.getLogger(WebSocketCoreClient.class);
   private final HttpClient client;
   private final WebSocketComponents components;
   private ClassLoader classLoader;

   public WebSocketCoreClient() {
      this(null, new WebSocketComponents());
   }

   public WebSocketCoreClient(WebSocketComponents webSocketComponents) {
      this(null, webSocketComponents);
   }

   public WebSocketCoreClient(HttpClient httpClient, WebSocketComponents webSocketComponents) {
      this.client = Objects.requireNonNullElse(httpClient, HttpClientProvider.get());
      this.addBean(this.client);
      if (webSocketComponents == null) {
         if (this.client.isStarted()) {
            webSocketComponents = new WebSocketComponents(null, null, this.client.getByteBufferPool(), null, null, this.client.getExecutor());
         } else {
            webSocketComponents = new WebSocketComponents();
         }
      }

      this.components = webSocketComponents;
      this.addBean(this.components);
      if (!this.client.isStarted()) {
         if (this.client.getByteBufferPool() == null) {
            this.client.setByteBufferPool(this.components.getByteBufferPool());
         }

         if (this.client.getExecutor() == null) {
            this.client.setExecutor(this.components.getExecutor());
         }
      }

      this.classLoader = Thread.currentThread().getContextClassLoader();
   }

   public ClassLoader getClassLoader() {
      return this.classLoader;
   }

   public void setClassLoader(ClassLoader classLoader) {
      this.classLoader = Objects.requireNonNull(classLoader);
   }

   public CompletableFuture<CoreSession> connect(FrameHandler frameHandler, URI wsUri) throws IOException {
      CoreClientUpgradeRequest request = CoreClientUpgradeRequest.from(this, wsUri, frameHandler);
      return this.connect(request);
   }

   public CompletableFuture<CoreSession> connect(CoreClientUpgradeRequest request) throws IOException {
      if (!this.isStarted()) {
         throw new IllegalStateException(WebSocketCoreClient.class.getSimpleName() + "@" + this.hashCode() + " is not started");
      } else {
         for (ExtensionConfig reqExt : request.getExtensions()) {
            if (!this.components.getExtensionRegistry().isAvailable(reqExt.getName())) {
               throw new IllegalArgumentException("Requested extension [" + reqExt.getName() + "] is not installed");
            }
         }

         for (Listener l : this.getBeans(Listener.class)) {
            request.listener(l);
         }

         if (LOG.isDebugEnabled()) {
            LOG.debug("connect to websocket {}", request.getURI());
         }

         return request.sendAsync();
      }
   }

   public WebSocketExtensionRegistry getExtensionRegistry() {
      return this.components.getExtensionRegistry();
   }

   public HttpClient getHttpClient() {
      return this.client;
   }

   public DecoratedObjectFactory getObjectFactory() {
      return this.components.getObjectFactory();
   }

   public WebSocketComponents getWebSocketComponents() {
      return this.components;
   }
}
