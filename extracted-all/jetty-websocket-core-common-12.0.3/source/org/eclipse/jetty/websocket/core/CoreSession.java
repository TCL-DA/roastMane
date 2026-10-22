package org.eclipse.jetty.websocket.core;

import java.net.SocketAddress;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.util.Callback;

public interface CoreSession extends OutgoingFrames, IncomingFrames, Configuration {
   String getNegotiatedSubProtocol();

   List<ExtensionConfig> getNegotiatedExtensions();

   Map<String, List<String>> getParameterMap();

   String getProtocolVersion();

   URI getRequestURI();

   boolean isSecure();

   Behavior getBehavior();

   WebSocketComponents getWebSocketComponents();

   ByteBufferPool getByteBufferPool();

   SocketAddress getLocalAddress();

   SocketAddress getRemoteAddress();

   boolean isInputOpen();

   boolean isOutputOpen();

   boolean isClosed();

   void flush(Callback var1);

   void close(Callback var1);

   void close(int var1, String var2, Callback var3);

   void abort();

   void demand();

   boolean isRsv1Used();

   boolean isRsv2Used();

   boolean isRsv3Used();

   public static class Empty extends Configuration.ConfigurationCustomizer implements CoreSession {
      @Override
      public String getNegotiatedSubProtocol() {
         return null;
      }

      @Override
      public List<ExtensionConfig> getNegotiatedExtensions() {
         return null;
      }

      @Override
      public Map<String, List<String>> getParameterMap() {
         return null;
      }

      @Override
      public String getProtocolVersion() {
         return null;
      }

      @Override
      public URI getRequestURI() {
         return null;
      }

      @Override
      public boolean isSecure() {
         return false;
      }

      @Override
      public void abort() {
      }

      @Override
      public Behavior getBehavior() {
         return null;
      }

      @Override
      public WebSocketComponents getWebSocketComponents() {
         return null;
      }

      @Override
      public ByteBufferPool getByteBufferPool() {
         WebSocketComponents components = this.getWebSocketComponents();
         return components != null ? components.getByteBufferPool() : null;
      }

      @Override
      public SocketAddress getLocalAddress() {
         return null;
      }

      @Override
      public SocketAddress getRemoteAddress() {
         return null;
      }

      @Override
      public boolean isInputOpen() {
         return true;
      }

      @Override
      public boolean isOutputOpen() {
         return true;
      }

      @Override
      public boolean isClosed() {
         return false;
      }

      @Override
      public void flush(Callback callback) {
         callback.succeeded();
      }

      @Override
      public void close(Callback callback) {
         callback.succeeded();
      }

      @Override
      public void close(int statusCode, String reason, Callback callback) {
         callback.succeeded();
      }

      @Override
      public void demand() {
      }

      @Override
      public void onFrame(Frame frame, Callback callback) {
         callback.succeeded();
      }

      @Override
      public void sendFrame(Frame frame, Callback callback, boolean batch) {
         callback.succeeded();
      }

      @Override
      public boolean isRsv1Used() {
         return false;
      }

      @Override
      public boolean isRsv2Used() {
         return false;
      }

      @Override
      public boolean isRsv3Used() {
         return false;
      }
   }
}
