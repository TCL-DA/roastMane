package org.eclipse.jetty.websocket.common;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.websocket.api.Callback;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.eclipse.jetty.websocket.api.UpgradeResponse;
import org.eclipse.jetty.websocket.api.WebSocketContainer;
import org.eclipse.jetty.websocket.core.CoreSession;
import org.eclipse.jetty.websocket.core.Frame;
import org.eclipse.jetty.websocket.core.OpCode;
import org.eclipse.jetty.websocket.core.exception.ProtocolException;

public class WebSocketSession implements Session, Dumpable {
   private final CoreSession coreSession;
   private final JettyWebSocketFrameHandler frameHandler;
   private final UpgradeRequest upgradeRequest;
   private final UpgradeResponse upgradeResponse;
   private byte messageType = -1;

   public WebSocketSession(WebSocketContainer container, CoreSession coreSession, JettyWebSocketFrameHandler frameHandler) {
      this.frameHandler = Objects.requireNonNull(frameHandler);
      this.coreSession = Objects.requireNonNull(coreSession);
      this.upgradeRequest = frameHandler.getUpgradeRequest();
      this.upgradeResponse = frameHandler.getUpgradeResponse();
      container.notifySessionListeners(listener -> listener.onWebSocketSessionCreated(this));
   }

   public void demand() {
      if (this.frameHandler.isAutoDemand()) {
         throw new IllegalStateException("auto-demanding endpoint cannot explicitly demand");
      } else {
         this.coreSession.demand();
      }
   }

   public void sendBinary(ByteBuffer buffer, Callback callback) {
      callback = Objects.requireNonNullElse(callback, Callback.NOOP);
      this.coreSession.sendFrame(new Frame((byte)2).setPayload(buffer), org.eclipse.jetty.util.Callback.from(callback::succeed, callback::fail), false);
   }

   public void sendPartialBinary(ByteBuffer buffer, boolean last, Callback callback) {
      callback = Objects.requireNonNullElse(callback, Callback.NOOP);

      Frame frame = switch (this.messageType) {
         case -1 -> {
            this.messageType = 2;
            yield new Frame((byte)2);
         }
         case 2 -> new Frame((byte)0);
         default -> {
            callback.fail(new ProtocolException("Attempt to send partial BINARY during " + OpCode.name(this.messageType)));
            yield null;
         }
      };
      if (frame != null) {
         frame.setPayload(buffer);
         frame.setFin(last);
         org.eclipse.jetty.util.Callback cb = org.eclipse.jetty.util.Callback.from(callback::succeed, callback::fail);
         this.coreSession.sendFrame(frame, cb, false);
         if (last) {
            this.messageType = -1;
         }
      }
   }

   public void sendText(String text, Callback callback) {
      callback = Objects.requireNonNullElse(callback, Callback.NOOP);
      org.eclipse.jetty.util.Callback cb = org.eclipse.jetty.util.Callback.from(callback::succeed, callback::fail);
      this.coreSession.sendFrame(new Frame((byte)1).setPayload(text), cb, false);
   }

   public void sendPartialText(String text, boolean last, Callback callback) {
      Frame frame = switch (this.messageType) {
         case -1 -> {
            this.messageType = 1;
            yield new Frame((byte)1);
         }
         case 1 -> new Frame((byte)0);
         default -> {
            callback.fail(new ProtocolException("Attempt to send partial TEXT during " + OpCode.name(this.messageType)));
            yield null;
         }
      };
      if (frame != null) {
         frame.setPayload(BufferUtil.toBuffer(text, StandardCharsets.UTF_8));
         frame.setFin(last);
         org.eclipse.jetty.util.Callback cb = org.eclipse.jetty.util.Callback.from(callback::succeed, callback::fail);
         this.coreSession.sendFrame(frame, cb, false);
         if (last) {
            this.messageType = -1;
         }
      }
   }

   public void sendPing(ByteBuffer applicationData, Callback callback) {
      this.coreSession
         .sendFrame(new Frame((byte)9).setPayload(applicationData), org.eclipse.jetty.util.Callback.from(callback::succeed, callback::fail), false);
   }

   public void sendPong(ByteBuffer applicationData, Callback callback) {
      this.coreSession
         .sendFrame(new Frame((byte)10).setPayload(applicationData), org.eclipse.jetty.util.Callback.from(callback::succeed, callback::fail), false);
   }

   public void close(int statusCode, String reason, Callback callback) {
      this.coreSession.close(statusCode, reason, org.eclipse.jetty.util.Callback.from(callback::succeed, callback::fail));
   }

   public Duration getIdleTimeout() {
      return this.coreSession.getIdleTimeout();
   }

   public void setIdleTimeout(Duration duration) {
      this.coreSession.setIdleTimeout(duration);
   }

   public int getInputBufferSize() {
      return this.coreSession.getInputBufferSize();
   }

   public void setInputBufferSize(int size) {
      this.coreSession.setInputBufferSize(size);
   }

   public int getOutputBufferSize() {
      return this.coreSession.getOutputBufferSize();
   }

   public void setOutputBufferSize(int size) {
      this.coreSession.setOutputBufferSize(size);
   }

   public long getMaxBinaryMessageSize() {
      return this.coreSession.getMaxBinaryMessageSize();
   }

   public void setMaxBinaryMessageSize(long size) {
      this.coreSession.setMaxBinaryMessageSize(size);
   }

   public long getMaxTextMessageSize() {
      return this.coreSession.getMaxTextMessageSize();
   }

   public void setMaxTextMessageSize(long size) {
      this.coreSession.setMaxTextMessageSize(size);
   }

   public long getMaxFrameSize() {
      return this.coreSession.getMaxFrameSize();
   }

   public void setMaxFrameSize(long maxFrameSize) {
      this.coreSession.setMaxFrameSize(maxFrameSize);
   }

   public boolean isAutoFragment() {
      return this.coreSession.isAutoFragment();
   }

   public void setAutoFragment(boolean autoFragment) {
      this.coreSession.setAutoFragment(autoFragment);
   }

   public int getMaxOutgoingFrames() {
      return this.coreSession.getMaxOutgoingFrames();
   }

   public void setMaxOutgoingFrames(int maxOutgoingFrames) {
      this.coreSession.setMaxOutgoingFrames(maxOutgoingFrames);
   }

   public String getProtocolVersion() {
      return this.upgradeRequest.getProtocolVersion();
   }

   public boolean isOpen() {
      return this.coreSession.isOutputOpen();
   }

   public boolean isSecure() {
      return this.upgradeRequest.isSecure();
   }

   public void disconnect() {
      this.coreSession.abort();
   }

   public SocketAddress getLocalSocketAddress() {
      return this.coreSession.getLocalAddress();
   }

   public SocketAddress getRemoteSocketAddress() {
      return this.coreSession.getRemoteAddress();
   }

   public UpgradeRequest getUpgradeRequest() {
      return this.upgradeRequest;
   }

   public UpgradeResponse getUpgradeResponse() {
      return this.upgradeResponse;
   }

   public CoreSession getCoreSession() {
      return this.coreSession;
   }

   public void dump(Appendable out, String indent) throws IOException {
      Dumpable.dumpObjects(out, indent, this, new Object[]{this.upgradeRequest, this.coreSession, this.frameHandler});
   }

   public String dumpSelf() {
      return String.format("%s@%x[idleTimeout=%dms]", this.getClass().getSimpleName(), this.hashCode(), this.getIdleTimeout().toMillis());
   }

   @Override
   public String toString() {
      return String.format("WebSocketSession[to=%s,%s,%s]", this.getIdleTimeout(), this.coreSession, this.frameHandler);
   }
}
