package org.eclipse.jetty.websocket.common;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.eclipse.jetty.websocket.api.UpgradeResponse;
import org.eclipse.jetty.websocket.api.WebSocketContainer;
import org.eclipse.jetty.websocket.api.Callback.Completable;
import org.eclipse.jetty.websocket.api.exceptions.InvalidWebSocketException;
import org.eclipse.jetty.websocket.core.CloseStatus;
import org.eclipse.jetty.websocket.core.CoreSession;
import org.eclipse.jetty.websocket.core.Frame;
import org.eclipse.jetty.websocket.core.FrameHandler;
import org.eclipse.jetty.websocket.core.exception.BadPayloadException;
import org.eclipse.jetty.websocket.core.exception.CloseException;
import org.eclipse.jetty.websocket.core.exception.InvalidSignatureException;
import org.eclipse.jetty.websocket.core.exception.MessageTooLargeException;
import org.eclipse.jetty.websocket.core.exception.ProtocolException;
import org.eclipse.jetty.websocket.core.exception.UpgradeException;
import org.eclipse.jetty.websocket.core.exception.WebSocketException;
import org.eclipse.jetty.websocket.core.exception.WebSocketTimeoutException;
import org.eclipse.jetty.websocket.core.messages.MessageSink;
import org.eclipse.jetty.websocket.core.util.InvokerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JettyWebSocketFrameHandler implements FrameHandler {
   private final AtomicBoolean closeNotified = new AtomicBoolean();
   private final Logger log;
   private final WebSocketContainer container;
   private final Object endpointInstance;
   private final JettyWebSocketFrameHandlerMetadata metadata;
   private MethodHandle openHandle;
   private MethodHandle closeHandle;
   private MethodHandle errorHandle;
   private MethodHandle textHandle;
   private MethodHandle binaryHandle;
   private final Class<? extends MessageSink> textSinkClass;
   private final Class<? extends MessageSink> binarySinkClass;
   private MethodHandle frameHandle;
   private MethodHandle pingHandle;
   private MethodHandle pongHandle;
   private UpgradeRequest upgradeRequest;
   private UpgradeResponse upgradeResponse;
   private MessageSink textSink;
   private MessageSink binarySink;
   private MessageSink activeMessageSink;
   private WebSocketSession session;

   public JettyWebSocketFrameHandler(WebSocketContainer container, Object endpointInstance, JettyWebSocketFrameHandlerMetadata metadata) {
      this.log = LoggerFactory.getLogger(endpointInstance.getClass());
      this.container = container;
      this.endpointInstance = endpointInstance;
      this.metadata = metadata;
      this.openHandle = InvokerUtils.bindTo(metadata.getOpenHandle(), new Object[]{endpointInstance});
      this.closeHandle = InvokerUtils.bindTo(metadata.getCloseHandle(), new Object[]{endpointInstance});
      this.errorHandle = InvokerUtils.bindTo(metadata.getErrorHandle(), new Object[]{endpointInstance});
      this.textHandle = InvokerUtils.bindTo(metadata.getTextHandle(), new Object[]{endpointInstance});
      this.binaryHandle = InvokerUtils.bindTo(metadata.getBinaryHandle(), new Object[]{endpointInstance});
      this.textSinkClass = metadata.getTextSink();
      this.binarySinkClass = metadata.getBinarySink();
      this.frameHandle = InvokerUtils.bindTo(metadata.getFrameHandle(), new Object[]{endpointInstance});
      this.pingHandle = InvokerUtils.bindTo(metadata.getPingHandle(), new Object[]{endpointInstance});
      this.pongHandle = InvokerUtils.bindTo(metadata.getPongHandle(), new Object[]{endpointInstance});
   }

   public void setUpgradeRequest(UpgradeRequest upgradeRequest) {
      this.upgradeRequest = upgradeRequest;
   }

   public void setUpgradeResponse(UpgradeResponse upgradeResponse) {
      this.upgradeResponse = upgradeResponse;
   }

   public UpgradeRequest getUpgradeRequest() {
      return this.upgradeRequest;
   }

   public UpgradeResponse getUpgradeResponse() {
      return this.upgradeResponse;
   }

   public WebSocketSession getSession() {
      return this.session;
   }

   public void onOpen(CoreSession coreSession, Callback callback) {
      try {
         this.metadata.customize(coreSession);
         this.session = new WebSocketSession(this.container, coreSession, this);
         if (!this.session.isOpen()) {
            throw new IllegalStateException("Session is not open");
         }

         this.frameHandle = InvokerUtils.bindTo(this.frameHandle, new Object[]{this.session});
         this.openHandle = InvokerUtils.bindTo(this.openHandle, new Object[]{this.session});
         this.closeHandle = InvokerUtils.bindTo(this.closeHandle, new Object[]{this.session});
         this.errorHandle = InvokerUtils.bindTo(this.errorHandle, new Object[]{this.session});
         this.textHandle = InvokerUtils.bindTo(this.textHandle, new Object[]{this.session});
         this.binaryHandle = InvokerUtils.bindTo(this.binaryHandle, new Object[]{this.session});
         this.pingHandle = InvokerUtils.bindTo(this.pingHandle, new Object[]{this.session});
         this.pongHandle = InvokerUtils.bindTo(this.pongHandle, new Object[]{this.session});
         if (this.textHandle != null) {
            this.textSink = createMessageSink(this.textSinkClass, this.session, this.textHandle, this.isAutoDemand());
         }

         if (this.binaryHandle != null) {
            this.binarySink = createMessageSink(this.binarySinkClass, this.session, this.binaryHandle, this.isAutoDemand());
         }

         if (this.openHandle != null) {
            this.openHandle.invoke();
         }

         if (this.session.isOpen()) {
            this.container.notifySessionListeners(listener -> listener.onWebSocketSessionOpened(this.session));
         }

         callback.succeeded();
      } catch (Throwable var7) {
         callback.failed(new WebSocketException(this.endpointInstance.getClass().getSimpleName() + " OPEN method error: " + var7.getMessage(), var7));
      } finally {
         this.autoDemand();
      }
   }

   private static MessageSink createMessageSink(Class<? extends MessageSink> sinkClass, WebSocketSession session, MethodHandle msgHandle, boolean autoDemanding) {
      if (msgHandle == null) {
         return null;
      } else if (sinkClass == null) {
         return null;
      } else {
         try {
            Lookup lookup = JettyWebSocketFrameHandlerFactory.getServerMethodHandleLookup();
            MethodHandle ctorHandle = lookup.findConstructor(sinkClass, MethodType.methodType(void.class, CoreSession.class, MethodHandle.class, boolean.class));
            return (MessageSink)ctorHandle.invoke((CoreSession)session.getCoreSession(), (MethodHandle)msgHandle, (boolean)autoDemanding);
         } catch (NoSuchMethodException var6) {
            throw new RuntimeException("Missing expected MessageSink constructor found at: " + sinkClass.getName(), var6);
         } catch (InstantiationException | InvocationTargetException | IllegalAccessException var7) {
            throw new RuntimeException("Unable to create MessageSink: " + sinkClass.getName(), var7);
         } catch (RuntimeException var8) {
            throw var8;
         } catch (Throwable var9) {
            throw new RuntimeException(var9);
         }
      }
   }

   public void onFrame(Frame frame, Callback coreCallback) {
      CompletableFuture<Void> frameCallback = null;
      if (this.frameHandle != null) {
         try {
            frameCallback = new Completable();
            this.frameHandle.invoke((JettyWebSocketFrame)(new JettyWebSocketFrame(frame)), (CompletableFuture)frameCallback);
         } catch (Throwable var6) {
            coreCallback.failed(new WebSocketException(this.endpointInstance.getClass().getSimpleName() + " FRAME method error: " + var6.getMessage(), var6));
            return;
         }
      }

      org.eclipse.jetty.util.Callback.Completable eventCallback = new org.eclipse.jetty.util.Callback.Completable();
      switch (frame.getOpCode()) {
         case 0:
            this.onContinuationFrame(frame, eventCallback);
            break;
         case 1:
            this.onTextFrame(frame, eventCallback);
            break;
         case 2:
            this.onBinaryFrame(frame, eventCallback);
            break;
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         default:
            coreCallback.failed(new IllegalStateException());
            break;
         case 8:
            this.onCloseFrame(frame, eventCallback);
            break;
         case 9:
            this.onPingFrame(frame, eventCallback);
            break;
         case 10:
            this.onPongFrame(frame, eventCallback);
      }

      CompletableFuture<Void> callback = eventCallback;
      if (frameCallback != null) {
         callback = frameCallback.thenCompose(ignored -> eventCallback);
      }

      callback.whenComplete((r, x) -> {
         if (x == null) {
            coreCallback.succeeded();
         } else {
            coreCallback.failed(x);
         }
      });
   }

   public void onError(Throwable cause, Callback callback) {
      try {
         cause = convertCause(cause);
         if (this.errorHandle != null) {
            this.errorHandle.invoke((Throwable)cause);
         } else if (this.log.isDebugEnabled()) {
            this.log.debug("Unhandled Error: Endpoint {}", this.endpointInstance.getClass().getName(), cause);
         } else {
            this.log.warn("Unhandled Error: Endpoint {} : {}", this.endpointInstance.getClass().getName(), cause.toString());
         }

         callback.succeeded();
      } catch (Throwable var5) {
         WebSocketException wsError = new WebSocketException(
            this.endpointInstance.getClass().getSimpleName() + " ERROR method error: " + cause.getMessage(), var5
         );
         wsError.addSuppressed(cause);
         callback.failed(wsError);
      }
   }

   public void onClosed(CloseStatus closeStatus, Callback callback) {
      this.notifyOnClose(closeStatus, callback);
      this.container.notifySessionListeners(listener -> listener.onWebSocketSessionClosed(this.session));
   }

   private void onCloseFrame(Frame frame, Callback callback) {
      this.notifyOnClose(CloseStatus.getCloseStatus(frame), callback);
   }

   private void notifyOnClose(CloseStatus closeStatus, Callback callback) {
      if (!this.closeNotified.compareAndSet(false, true)) {
         callback.failed(new ClosedChannelException());
      } else {
         try {
            if (this.closeHandle != null) {
               this.closeHandle.invoke((int)closeStatus.getCode(), (String)closeStatus.getReason());
            }

            callback.succeeded();
         } catch (Throwable var4) {
            callback.failed(new WebSocketException(this.endpointInstance.getClass().getSimpleName() + " CLOSE method error: " + var4.getMessage(), var4));
         }
      }
   }

   private void onPingFrame(Frame frame, final Callback callback) {
      if (this.pingHandle != null) {
         try {
            ByteBuffer payload = frame.getPayload();
            if (payload == null) {
               payload = BufferUtil.EMPTY_BUFFER;
            } else {
               payload = BufferUtil.copy(payload);
            }

            this.pingHandle.invoke((ByteBuffer)payload);
            callback.succeeded();
            this.autoDemand();
         } catch (Throwable var4) {
            callback.failed(new WebSocketException(this.endpointInstance.getClass().getSimpleName() + " PING method error: " + var4.getMessage(), var4));
         }
      } else {
         this.getSession().sendPong(frame.getPayload(), new org.eclipse.jetty.websocket.api.Callback() {
            public void succeed() {
               callback.succeeded();
               JettyWebSocketFrameHandler.this.autoDemand();
            }

            public void fail(Throwable x) {
               callback.succeeded();
            }
         });
      }
   }

   private void onPongFrame(Frame frame, Callback callback) {
      if (this.pongHandle != null) {
         try {
            ByteBuffer payload = frame.getPayload();
            if (payload == null) {
               payload = BufferUtil.EMPTY_BUFFER;
            } else {
               payload = BufferUtil.copy(payload);
            }

            this.pongHandle.invoke((ByteBuffer)payload);
            callback.succeeded();
            this.autoDemand();
         } catch (Throwable var4) {
            callback.failed(new WebSocketException(this.endpointInstance.getClass().getSimpleName() + " PONG method error: " + var4.getMessage(), var4));
         }
      } else {
         this.autoDemand();
      }
   }

   private void onTextFrame(Frame frame, Callback callback) {
      if (this.activeMessageSink == null) {
         this.activeMessageSink = this.textSink;
      }

      this.acceptFrame(frame, callback);
   }

   private void onBinaryFrame(Frame frame, Callback callback) {
      if (this.activeMessageSink == null) {
         this.activeMessageSink = this.binarySink;
      }

      this.acceptFrame(frame, callback);
   }

   private void onContinuationFrame(Frame frame, Callback callback) {
      this.acceptFrame(frame, callback);
   }

   private void acceptFrame(Frame frame, Callback callback) {
      if (this.activeMessageSink == null) {
         callback.succeeded();
         this.autoDemand();
      } else {
         this.activeMessageSink.accept(frame, callback);
         if (frame.isFin()) {
            this.activeMessageSink = null;
         }
      }
   }

   boolean isAutoDemand() {
      return this.metadata.isAutoDemand();
   }

   private void autoDemand() {
      if (this.isAutoDemand()) {
         this.session.getCoreSession().demand();
      }
   }

   @Override
   public String toString() {
      return String.format("%s@%x[%s]", this.getClass().getSimpleName(), this.hashCode(), this.endpointInstance.getClass().getName());
   }

   public static Throwable convertCause(Throwable cause) {
      if (cause instanceof MessageTooLargeException) {
         return new org.eclipse.jetty.websocket.api.exceptions.MessageTooLargeException(cause.getMessage(), cause);
      } else if (cause instanceof ProtocolException) {
         return new org.eclipse.jetty.websocket.api.exceptions.ProtocolException(cause.getMessage(), cause);
      } else if (cause instanceof BadPayloadException) {
         return new org.eclipse.jetty.websocket.api.exceptions.BadPayloadException(cause.getMessage(), cause);
      } else if (cause instanceof CloseException) {
         return new org.eclipse.jetty.websocket.api.exceptions.CloseException(((CloseException)cause).getStatusCode(), cause.getMessage(), cause);
      } else if (cause instanceof WebSocketTimeoutException) {
         return new org.eclipse.jetty.websocket.api.exceptions.WebSocketTimeoutException(cause.getMessage(), cause);
      } else if (cause instanceof InvalidSignatureException) {
         return new InvalidWebSocketException(cause.getMessage(), cause);
      } else {
         return (Throwable)(cause instanceof UpgradeException ue
            ? new org.eclipse.jetty.websocket.api.exceptions.UpgradeException(ue.getRequestURI(), ue.getResponseStatusCode(), cause)
            : cause);
      }
   }
}
