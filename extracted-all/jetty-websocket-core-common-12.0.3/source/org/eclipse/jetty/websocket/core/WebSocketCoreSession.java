package org.eclipse.jetty.websocket.core;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.WritePendingException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.websocket.core.exception.CloseException;
import org.eclipse.jetty.websocket.core.exception.ProtocolException;
import org.eclipse.jetty.websocket.core.exception.WebSocketTimeoutException;
import org.eclipse.jetty.websocket.core.exception.WebSocketWriteTimeoutException;
import org.eclipse.jetty.websocket.core.internal.FrameFlusher;
import org.eclipse.jetty.websocket.core.internal.WebSocketSessionState;
import org.eclipse.jetty.websocket.core.util.FragmentingFlusher;
import org.eclipse.jetty.websocket.core.util.FrameValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketCoreSession implements CoreSession, Dumpable {
   private static final Logger LOG = LoggerFactory.getLogger(WebSocketCoreSession.class);
   private static final CloseStatus NO_CODE = new CloseStatus(1005);
   private final WebSocketComponents components;
   private final Behavior behavior;
   private final WebSocketSessionState sessionState = new WebSocketSessionState();
   private final FrameHandler handler;
   private final Negotiated negotiated;
   private final WebSocketCoreSession.Flusher flusher = new WebSocketCoreSession.Flusher(this);
   private final ExtensionStack extensionStack;
   private int maxOutgoingFrames = -1;
   private final AtomicInteger numOutgoingFrames = new AtomicInteger();
   private WebSocketConnection connection;
   private boolean autoFragment = true;
   private long maxFrameSize = 65536L;
   private int inputBufferSize = 4096;
   private int outputBufferSize = 4096;
   private long maxBinaryMessageSize = 65536L;
   private long maxTextMessageSize = 65536L;
   private Duration idleTimeout = WebSocketConstants.DEFAULT_IDLE_TIMEOUT;
   private Duration writeTimeout = WebSocketConstants.DEFAULT_WRITE_TIMEOUT;
   private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

   public WebSocketCoreSession(FrameHandler handler, Behavior behavior, Negotiated negotiated, WebSocketComponents components) {
      this.components = components;
      this.handler = handler;
      this.behavior = behavior;
      this.negotiated = negotiated;
      this.extensionStack = negotiated.getExtensions();
      this.extensionStack.initialize(new WebSocketCoreSession.IncomingAdaptor(), new WebSocketCoreSession.OutgoingAdaptor(), this);
   }

   public ClassLoader getClassLoader() {
      return this.classLoader;
   }

   public void setClassLoader(ClassLoader classLoader) {
      this.classLoader = classLoader;
   }

   protected void handle(Runnable runnable) {
      ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();

      try {
         Thread.currentThread().setContextClassLoader(this.classLoader);
         runnable.run();
      } finally {
         Thread.currentThread().setContextClassLoader(oldClassLoader);
      }
   }

   public ExtensionStack getExtensionStack() {
      return this.negotiated.getExtensions();
   }

   public FrameHandler getHandler() {
      return this.handler;
   }

   @Override
   public String getNegotiatedSubProtocol() {
      return this.negotiated.getSubProtocol();
   }

   @Override
   public Duration getIdleTimeout() {
      return this.idleTimeout;
   }

   @Override
   public void setIdleTimeout(Duration timeout) {
      this.idleTimeout = timeout;
      if (this.connection != null) {
         this.connection.getEndPoint().setIdleTimeout(timeout.toMillis());
      }
   }

   @Override
   public Duration getWriteTimeout() {
      return this.writeTimeout;
   }

   @Override
   public void setWriteTimeout(Duration timeout) {
      this.writeTimeout = timeout;
      if (this.getConnection() != null) {
         this.getConnection().setWriteTimeout(timeout.toMillis());
      }
   }

   @Override
   public SocketAddress getLocalAddress() {
      return this.getConnection().getEndPoint().getLocalSocketAddress();
   }

   @Override
   public SocketAddress getRemoteAddress() {
      return this.getConnection().getEndPoint().getRemoteSocketAddress();
   }

   @Override
   public boolean isInputOpen() {
      return this.sessionState.isInputOpen();
   }

   @Override
   public boolean isOutputOpen() {
      return this.sessionState.isOutputOpen();
   }

   @Override
   public boolean isClosed() {
      return this.sessionState.isClosed();
   }

   public void setWebSocketConnection(WebSocketConnection connection) {
      connection.getEndPoint().setIdleTimeout(this.idleTimeout.toMillis());
      connection.setWriteTimeout(this.writeTimeout.toMillis());
      this.extensionStack.setLastDemand(connection::demand);
      this.connection = connection;
   }

   @Override
   public void close(Callback callback) {
      this.close(NO_CODE, callback);
   }

   @Override
   public void close(int statusCode, String reason, Callback callback) {
      this.close(new CloseStatus(statusCode, reason), callback);
   }

   private void close(CloseStatus closeStatus, Callback callback) {
      this.sendFrame(closeStatus.toFrame(), callback, false);
   }

   @Override
   public ByteBufferPool getByteBufferPool() {
      return this.components.getByteBufferPool();
   }

   public void onEof() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("onEof() {}", this);
      }

      if (this.sessionState.onEof()) {
         this.closeConnection(this.sessionState.getCloseStatus(), Callback.NOOP);
      }
   }

   private void closeConnection(CloseStatus closeStatus, Callback callback) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("closeConnection() {} {}", closeStatus, this);
      }

      this.abort();
      this.extensionStack.close();
      if (closeStatus.isAbnormal() && closeStatus.getCause() != null) {
         Callback errorCallback = Callback.from(() -> {
            try {
               this.handle(() -> this.handler.onClosed(closeStatus, callback));
            } catch (Throwable var4x) {
               LOG.warn("Failure from onClosed on handler {}", this.handler, var4x);
               callback.failed(var4x);
            }
         });
         Throwable cause = closeStatus.getCause();

         try {
            this.handle(() -> this.handler.onError(cause, errorCallback));
         } catch (Throwable var7) {
            if (var7 != cause) {
               cause.addSuppressed(var7);
            }

            LOG.warn("Failure from onError on handler {}", this.handler, cause);
            errorCallback.failed(cause);
         }
      } else {
         try {
            this.handle(() -> this.handler.onClosed(closeStatus, callback));
         } catch (Throwable var6) {
            LOG.warn("Failure from onClosed on handler {}", this.handler, var6);
            callback.failed(var6);
         }
      }
   }

   public void processConnectionError(Throwable cause, Callback callback) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("processConnectionError {}", this, cause);
      }

      int code;
      if (cause instanceof CloseException) {
         code = ((CloseException)cause).getStatusCode();
      } else if (cause instanceof WebSocketWriteTimeoutException) {
         code = 1006;
      } else if (!(cause instanceof WebSocketTimeoutException) && !(cause instanceof TimeoutException) && !(cause instanceof SocketTimeoutException)) {
         code = 1006;
      } else {
         code = 1001;
      }

      CloseStatus closeStatus = new CloseStatus(code, cause);
      if (CloseStatus.isTransmittableStatusCode(code)) {
         this.close(closeStatus, callback);
      } else if (this.sessionState.onClosed(closeStatus)) {
         this.closeConnection(closeStatus, callback);
      }
   }

   public void processHandlerError(Throwable cause, Callback callback) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("processHandlerError {}", this, cause);
      }

      int code;
      if (cause instanceof CloseException) {
         code = ((CloseException)cause).getStatusCode();
      } else if (cause instanceof ClosedChannelException) {
         code = 1006;
      } else if (cause instanceof WebSocketTimeoutException || cause instanceof TimeoutException || cause instanceof SocketTimeoutException) {
         code = 1001;
      } else if (this.behavior == Behavior.CLIENT) {
         code = 1008;
      } else {
         code = 1011;
      }

      CloseStatus closeStatus = new CloseStatus(code, cause);
      if (CloseStatus.isTransmittableStatusCode(code)) {
         this.close(closeStatus, callback);
      } else if (this.sessionState.onClosed(closeStatus)) {
         this.closeConnection(closeStatus, callback);
      }
   }

   public void onOpen() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("onOpen() {}", this);
      }

      this.sessionState.onConnected();
      if (LOG.isDebugEnabled()) {
         LOG.debug("ConnectionState: Transition to CONNECTED");
      }

      Callback openCallback = Callback.from(() -> {
         this.sessionState.onOpen();
         if (LOG.isDebugEnabled()) {
            LOG.debug("ConnectionState: Transition to OPEN");
         }
      }, x -> {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Error during OPEN", x);
         }

         this.processHandlerError(new CloseException(1011, x), Callback.NOOP);
      });

      try {
         this.handle(() -> this.handler.onOpen(this, openCallback));
      } catch (Throwable var3) {
         openCallback.failed(var3);
         throw new RuntimeException(var3);
      }
   }

   @Override
   public void demand() {
      this.getExtensionStack().demand();
   }

   @Override
   public boolean isRsv1Used() {
      return this.getExtensionStack().isRsv1Used();
   }

   @Override
   public boolean isRsv2Used() {
      return this.getExtensionStack().isRsv2Used();
   }

   @Override
   public boolean isRsv3Used() {
      return this.getExtensionStack().isRsv3Used();
   }

   public WebSocketConnection getConnection() {
      return this.connection;
   }

   public Executor getExecutor() {
      return this.connection.getExecutor();
   }

   @Override
   public void onFrame(Frame frame, Callback callback) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("onFrame({})", frame);
      }

      try {
         FrameValidation.assertValidIncoming(frame, this);
      } catch (Throwable var4) {
         callback.failed(var4);
         return;
      }

      this.negotiated.getExtensions().onFrame(frame, callback);
   }

   @Override
   public void sendFrame(Frame frame, Callback callback, boolean batch) {
      if (this.maxOutgoingFrames > 0 && frame.isDataFrame()) {
         callback = Callback.from(callback, this.numOutgoingFrames::decrementAndGet);
         if (this.numOutgoingFrames.incrementAndGet() > this.maxOutgoingFrames) {
            callback.failed(new WritePendingException());
            return;
         }
      }

      try {
         FrameValidation.assertValidOutgoing(frame, this);
      } catch (Throwable var7) {
         LOG.warn("Invalid outgoing frame: {}", frame, var7);
         callback.failed(var7);
         return;
      }

      try {
         if (LOG.isDebugEnabled()) {
            LOG.debug("sendFrame({}, {}, {})", new Object[]{frame, callback, batch});
         }

         boolean closeConnection = this.sessionState.onOutgoingFrame(frame);
         if (closeConnection) {
            Callback c = callback;
            Callback closeConnectionCallback = Callback.from(
               () -> this.closeConnection(this.sessionState.getCloseStatus(), c),
               tx -> this.closeConnection(this.sessionState.getCloseStatus(), Callback.from(c, tx))
            );
            this.flusher.sendFrame(frame, closeConnectionCallback, false);
         } else {
            this.flusher.sendFrame(frame, callback, batch);
         }
      } catch (Throwable var8) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Failed sendFrame() {}", var8.toString());
         }

         if (frame.getOpCode() == 8) {
            CloseStatus closeStatus = CloseStatus.getCloseStatus(frame);
            if (closeStatus.isAbnormal() && this.sessionState.onClosed(closeStatus)) {
               this.closeConnection(closeStatus, Callback.from(callback, var8));
            } else {
               callback.failed(var8);
            }
         } else {
            callback.failed(var8);
         }
      }
   }

   @Override
   public void flush(Callback callback) {
      this.flusher.sendFrame(FrameFlusher.FLUSH_FRAME, callback, false);
   }

   @Override
   public void abort() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("abort(): {}", this);
      }

      this.connection.cancelDemand();
      this.connection.getEndPoint().close();
   }

   @Override
   public boolean isAutoFragment() {
      return this.autoFragment;
   }

   @Override
   public void setAutoFragment(boolean autoFragment) {
      this.autoFragment = autoFragment;
   }

   @Override
   public long getMaxFrameSize() {
      return this.maxFrameSize;
   }

   @Override
   public void setMaxFrameSize(long maxFrameSize) {
      this.maxFrameSize = maxFrameSize;
   }

   @Override
   public int getOutputBufferSize() {
      return this.outputBufferSize;
   }

   @Override
   public void setOutputBufferSize(int outputBufferSize) {
      this.outputBufferSize = outputBufferSize;
   }

   @Override
   public int getInputBufferSize() {
      return this.inputBufferSize;
   }

   @Override
   public void setInputBufferSize(int inputBufferSize) {
      this.inputBufferSize = inputBufferSize;
      if (this.connection != null) {
         this.connection.setInputBufferSize(inputBufferSize);
      }
   }

   @Override
   public long getMaxBinaryMessageSize() {
      return this.maxBinaryMessageSize;
   }

   @Override
   public void setMaxBinaryMessageSize(long maxSize) {
      this.maxBinaryMessageSize = maxSize;
   }

   @Override
   public long getMaxTextMessageSize() {
      return this.maxTextMessageSize;
   }

   @Override
   public void setMaxTextMessageSize(long maxSize) {
      this.maxTextMessageSize = maxSize;
   }

   @Override
   public int getMaxOutgoingFrames() {
      return this.maxOutgoingFrames;
   }

   @Override
   public void setMaxOutgoingFrames(int maxOutgoingFrames) {
      this.maxOutgoingFrames = maxOutgoingFrames;
   }

   public String dump() {
      return Dumpable.dump(this);
   }

   public void dump(Appendable out, String indent) throws IOException {
      Dumpable.dumpObjects(out, indent, this, new Object[]{"subprotocol=" + this.negotiated.getSubProtocol(), this.negotiated.getExtensions(), this.handler});
   }

   @Override
   public List<ExtensionConfig> getNegotiatedExtensions() {
      return this.negotiated.getExtensions().getNegotiatedExtensions();
   }

   @Override
   public Map<String, List<String>> getParameterMap() {
      return this.negotiated.getParameterMap();
   }

   @Override
   public String getProtocolVersion() {
      return this.negotiated.getProtocolVersion();
   }

   @Override
   public URI getRequestURI() {
      return this.negotiated.getRequestURI();
   }

   @Override
   public boolean isSecure() {
      return this.negotiated.isSecure();
   }

   @Override
   public Behavior getBehavior() {
      return this.behavior;
   }

   @Override
   public WebSocketComponents getWebSocketComponents() {
      return this.components;
   }

   @Override
   public String toString() {
      return String.format(
         "WebSocketCoreSession@%x{%s,%s,%s,af=%b,i/o=%d/%d,fs=%d}->%s",
         this.hashCode(),
         this.behavior,
         this.sessionState,
         this.negotiated,
         this.autoFragment,
         this.inputBufferSize,
         this.outputBufferSize,
         this.maxFrameSize,
         this.handler
      );
   }

   private class Flusher extends FragmentingFlusher {
      public Flusher(Configuration configuration) {
         super(configuration);
      }

      @Override
      protected void forwardFrame(Frame frame, Callback callback, boolean batch) {
         WebSocketCoreSession.this.negotiated.getExtensions().sendFrame(frame, callback, batch);
      }
   }

   private class IncomingAdaptor implements IncomingFrames {
      @Override
      public void onFrame(Frame frame, Callback callback) {
         Callback closeCallback = null;

         try {
            if (WebSocketCoreSession.LOG.isDebugEnabled()) {
               WebSocketCoreSession.LOG
                  .debug(
                     "receiveFrame({}, {}) - connectionState={}, handler={}",
                     new Object[]{frame, callback, WebSocketCoreSession.this.sessionState, WebSocketCoreSession.this.handler}
                  );
            }

            boolean closeConnection = WebSocketCoreSession.this.sessionState.onIncomingFrame(frame);
            if (frame.getOpCode() != 8) {
               WebSocketCoreSession.this.handle(() -> WebSocketCoreSession.this.handler.onFrame(frame, callback));
               return;
            }

            WebSocketCoreSession.this.connection.cancelDemand();
            if (closeConnection) {
               closeCallback = Callback.from(
                  () -> WebSocketCoreSession.this.closeConnection(WebSocketCoreSession.this.sessionState.getCloseStatus(), callback), tx -> {
                     WebSocketCoreSession.this.sessionState.onError(tx);
                     WebSocketCoreSession.this.closeConnection(WebSocketCoreSession.this.sessionState.getCloseStatus(), callback);
                  }
               );
            } else {
               closeCallback = Callback.from(() -> {
                  if (WebSocketCoreSession.this.sessionState.isOutputOpen()) {
                     CloseStatus closeStatus = CloseStatus.getCloseStatus(frame);
                     if (WebSocketCoreSession.LOG.isDebugEnabled()) {
                        WebSocketCoreSession.LOG.debug("ConnectionState: sending close response {}", closeStatus);
                     }

                     WebSocketCoreSession.this.close(closeStatus == null ? CloseStatus.NO_CODE_STATUS : closeStatus, callback);
                  } else {
                     callback.succeeded();
                  }
               }, x -> WebSocketCoreSession.this.processHandlerError(x, callback));
            }

            WebSocketCoreSession.this.handler.onFrame(frame, closeCallback);
         } catch (Throwable var5) {
            if (closeCallback != null) {
               closeCallback.failed(var5);
            } else {
               callback.failed(var5);
            }
         }
      }
   }

   private class OutgoingAdaptor implements OutgoingFrames {
      @Override
      public void sendFrame(Frame frame, Callback callback, boolean batch) {
         try {
            WebSocketCoreSession.this.connection.enqueueFrame(frame, callback, batch);
         } catch (ProtocolException var5) {
            callback.failed(var5);
         }
      }
   }
}
