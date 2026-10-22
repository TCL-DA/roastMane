package org.eclipse.jetty.websocket.api;

import java.io.Closeable;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

public interface Session extends Configurable, Closeable {
   void demand();

   void sendBinary(ByteBuffer var1, Callback var2);

   void sendPartialBinary(ByteBuffer var1, boolean var2, Callback var3);

   void sendText(String var1, Callback var2);

   void sendPartialText(String var1, boolean var2, Callback var3);

   void sendPing(ByteBuffer var1, Callback var2);

   void sendPong(ByteBuffer var1, Callback var2);

   @Override
   default void close() {
      this.close(1000, null, Callback.NOOP);
   }

   void close(int var1, String var2, Callback var3);

   void disconnect();

   SocketAddress getLocalSocketAddress();

   SocketAddress getRemoteSocketAddress();

   String getProtocolVersion();

   UpgradeRequest getUpgradeRequest();

   UpgradeResponse getUpgradeResponse();

   boolean isOpen();

   boolean isSecure();

   public interface Listener {
      default void onWebSocketOpen(Session session) {
      }

      default void onWebSocketFrame(Frame frame, Callback callback) {
      }

      default void onWebSocketPing(ByteBuffer payload) {
      }

      default void onWebSocketPong(ByteBuffer payload) {
      }

      default void onWebSocketPartialBinary(ByteBuffer payload, boolean last, Callback callback) {
      }

      default void onWebSocketPartialText(String payload, boolean last) {
      }

      default void onWebSocketBinary(ByteBuffer payload, Callback callback) {
      }

      default void onWebSocketText(String message) {
      }

      default void onWebSocketError(Throwable cause) {
      }

      default void onWebSocketClose(int statusCode, String reason) {
      }

      public abstract static class Abstract implements Session.Listener {
         private volatile Session session;

         @Override
         public void onWebSocketOpen(Session session) {
            this.session = session;
         }

         public Session getSession() {
            return this.session;
         }

         public boolean isOpen() {
            Session session = this.session;
            return session != null && session.isOpen();
         }
      }

      public abstract static class AbstractAutoDemanding extends Session.Listener.Abstract implements Session.Listener.AutoDemanding {
      }

      public interface AutoDemanding extends Session.Listener {
      }
   }
}
