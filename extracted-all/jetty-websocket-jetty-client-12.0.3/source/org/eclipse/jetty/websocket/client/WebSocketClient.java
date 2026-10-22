package org.eclipse.jetty.websocket.client;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.URI;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.Request;
import org.eclipse.jetty.client.Response;
import org.eclipse.jetty.io.Connection.Listener;
import org.eclipse.jetty.util.DecoratedObjectFactory;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.component.Graceful;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.ShutdownThread;
import org.eclipse.jetty.websocket.api.Configurable;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketContainer;
import org.eclipse.jetty.websocket.api.WebSocketSessionListener;
import org.eclipse.jetty.websocket.client.internal.JettyClientUpgradeRequest;
import org.eclipse.jetty.websocket.common.JettyWebSocketFrameHandler;
import org.eclipse.jetty.websocket.common.JettyWebSocketFrameHandlerFactory;
import org.eclipse.jetty.websocket.common.SessionTracker;
import org.eclipse.jetty.websocket.core.CoreSession;
import org.eclipse.jetty.websocket.core.Configuration.ConfigurationCustomizer;
import org.eclipse.jetty.websocket.core.client.UpgradeListener;
import org.eclipse.jetty.websocket.core.client.WebSocketCoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketClient extends ContainerLifeCycle implements Configurable, WebSocketContainer {
   private static final Logger LOG = LoggerFactory.getLogger(WebSocketClient.class);
   private final WebSocketCoreClient coreClient;
   private final int id = ThreadLocalRandom.current().nextInt();
   private final JettyWebSocketFrameHandlerFactory frameHandlerFactory;
   private final List<WebSocketSessionListener> sessionListeners = new CopyOnWriteArrayList<>();
   private final SessionTracker sessionTracker = new SessionTracker();
   private final ConfigurationCustomizer configurationCustomizer = new ConfigurationCustomizer();
   private boolean stopAtShutdown = false;
   private long _stopTimeout = Long.MAX_VALUE;

   public WebSocketClient() {
      this(null);
   }

   public WebSocketClient(HttpClient httpClient) {
      this.coreClient = new WebSocketCoreClient(httpClient, null);
      this.addManaged(this.coreClient);
      this.frameHandlerFactory = new JettyWebSocketFrameHandlerFactory(this, this.coreClient.getWebSocketComponents());
      this.sessionListeners.add(this.sessionTracker);
      this.addBean(this.sessionTracker);
   }

   public CompletableFuture<Session> connect(Object websocket, URI toUri) throws IOException {
      return this.connect(websocket, toUri, null);
   }

   public CompletableFuture<Session> connect(Object websocket, URI toUri, ClientUpgradeRequest request) throws IOException {
      return this.connect(websocket, toUri, request, null);
   }

   public CompletableFuture<Session> connect(Object websocket, URI toUri, ClientUpgradeRequest request, final JettyUpgradeListener upgradeListener) throws IOException {
      for (Listener listener : this.getBeans(Listener.class)) {
         this.coreClient.addBean(listener);
      }

      JettyClientUpgradeRequest upgradeRequest = new JettyClientUpgradeRequest(this.coreClient, request, toUri, this.frameHandlerFactory, websocket);
      upgradeRequest.setConfiguration(this.configurationCustomizer);

      for (org.eclipse.jetty.client.Request.Listener l : this.getBeans(org.eclipse.jetty.client.Request.Listener.class)) {
         upgradeRequest.listener(l);
      }

      if (upgradeListener != null) {
         upgradeRequest.addListener(new UpgradeListener() {
            public void onHandshakeRequest(Request request) {
               upgradeListener.onHandshakeRequest(request);
            }

            public void onHandshakeResponse(Request request, Response response) {
               upgradeListener.onHandshakeResponse(request, response);
            }
         });
      }

      CompletableFuture<Session> futureSession = new CompletableFuture<>();
      CompletableFuture<CoreSession> coreConnect = this.coreClient.connect(upgradeRequest);
      coreConnect.whenComplete((coreSession, error) -> {
         if (error != null) {
            futureSession.completeExceptionally(JettyWebSocketFrameHandler.convertCause(error));
         } else {
            JettyWebSocketFrameHandler frameHandler = (JettyWebSocketFrameHandler)upgradeRequest.getFrameHandler();
            futureSession.complete(frameHandler.getSession());
         }
      });
      futureSession.whenComplete((session, throwable) -> {
         if (throwable != null) {
            coreConnect.completeExceptionally(throwable);
         }
      });
      return futureSession;
   }

   public void dump(Appendable out, String indent) throws IOException {
      this.dumpObjects(out, indent, new Object[]{this.getOpenSessions()});
   }

   public void addSessionListener(WebSocketSessionListener listener) {
      this.sessionListeners.add(listener);
   }

   public boolean removeSessionListener(WebSocketSessionListener listener) {
      return this.sessionListeners.remove(listener);
   }

   public void notifySessionListeners(Consumer<WebSocketSessionListener> consumer) {
      for (WebSocketSessionListener listener : this.sessionListeners) {
         try {
            consumer.accept(listener);
         } catch (Throwable var5) {
            LOG.info("Exception while invoking listener {}", listener, var5);
         }
      }
   }

   public Duration getIdleTimeout() {
      return this.configurationCustomizer.getIdleTimeout();
   }

   public void setIdleTimeout(Duration duration) {
      this.configurationCustomizer.setIdleTimeout(duration);
      this.getHttpClient().setIdleTimeout(duration.toMillis());
   }

   public int getInputBufferSize() {
      return this.configurationCustomizer.getInputBufferSize();
   }

   public void setInputBufferSize(int size) {
      this.configurationCustomizer.setInputBufferSize(size);
   }

   public int getOutputBufferSize() {
      return this.configurationCustomizer.getOutputBufferSize();
   }

   public void setOutputBufferSize(int size) {
      this.configurationCustomizer.setOutputBufferSize(size);
   }

   public long getMaxBinaryMessageSize() {
      return this.configurationCustomizer.getMaxBinaryMessageSize();
   }

   public void setMaxBinaryMessageSize(long size) {
      this.configurationCustomizer.setMaxBinaryMessageSize(size);
   }

   public long getMaxTextMessageSize() {
      return this.configurationCustomizer.getMaxTextMessageSize();
   }

   public void setMaxTextMessageSize(long size) {
      this.configurationCustomizer.setMaxTextMessageSize(size);
   }

   public long getMaxFrameSize() {
      return this.configurationCustomizer.getMaxFrameSize();
   }

   public void setMaxFrameSize(long maxFrameSize) {
      this.configurationCustomizer.setMaxFrameSize(maxFrameSize);
   }

   public boolean isAutoFragment() {
      return this.configurationCustomizer.isAutoFragment();
   }

   public void setAutoFragment(boolean autoFragment) {
      this.configurationCustomizer.setAutoFragment(autoFragment);
   }

   public int getMaxOutgoingFrames() {
      return this.configurationCustomizer.getMaxOutgoingFrames();
   }

   public void setMaxOutgoingFrames(int maxOutgoingFrames) {
      this.configurationCustomizer.setMaxOutgoingFrames(maxOutgoingFrames);
   }

   public SocketAddress getBindAddress() {
      return this.getHttpClient().getBindAddress();
   }

   public void setBindAddress(SocketAddress bindAddress) {
      this.getHttpClient().setBindAddress(bindAddress);
   }

   public long getConnectTimeout() {
      return this.getHttpClient().getConnectTimeout();
   }

   public void setConnectTimeout(long ms) {
      this.getHttpClient().setConnectTimeout(ms);
   }

   public Executor getExecutor() {
      return this.getHttpClient().getExecutor();
   }

   public HttpClient getHttpClient() {
      return this.coreClient.getHttpClient();
   }

   public DecoratedObjectFactory getObjectFactory() {
      return this.coreClient.getObjectFactory();
   }

   public Collection<Session> getOpenSessions() {
      return this.sessionTracker.getSessions();
   }

   public SslContextFactory getSslContextFactory() {
      return this.getHttpClient().getSslContextFactory();
   }

   public void setStopAtShutdown(boolean stop) {
      if (stop) {
         if (!this.stopAtShutdown && !ShutdownThread.isRegistered(this)) {
            ShutdownThread.register(new LifeCycle[]{this});
         }
      } else {
         ShutdownThread.deregister(this);
      }

      this.stopAtShutdown = stop;
   }

   public void setStopTimeout(long stopTimeout) {
      this._stopTimeout = stopTimeout;
   }

   public long getStopTimeout() {
      return this._stopTimeout;
   }

   public boolean isStopAtShutdown() {
      return this.stopAtShutdown;
   }

   protected void doStop() throws Exception {
      if (this.getStopTimeout() > 0L) {
         Graceful.shutdown(this).get(this.getStopTimeout(), TimeUnit.MILLISECONDS);
      }

      super.doStop();
   }

   public String toString() {
      StringBuilder sb = new StringBuilder("WebSocketClient@");
      sb.append(Integer.toHexString(this.id));
      sb.append("[coreClient=").append(this.coreClient);
      sb.append(",openSessions.size=");
      sb.append(this.getOpenSessions().size());
      sb.append(']');
      return sb.toString();
   }
}
