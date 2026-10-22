package org.eclipse.jetty.io;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ReadPendingException;
import java.nio.channels.WritePendingException;
import org.eclipse.jetty.util.Callback;

public interface EndPoint extends Closeable {
   @Deprecated
   InetSocketAddress getLocalAddress();

   default SocketAddress getLocalSocketAddress() {
      return this.getLocalAddress();
   }

   @Deprecated
   InetSocketAddress getRemoteAddress();

   default SocketAddress getRemoteSocketAddress() {
      return this.getRemoteAddress();
   }

   boolean isOpen();

   long getCreatedTimeStamp();

   void shutdownOutput();

   boolean isOutputShutdown();

   boolean isInputShutdown();

   @Override
   default void close() {
      this.close(null);
   }

   void close(Throwable var1);

   default int fill(ByteBuffer buffer) throws IOException {
      throw new UnsupportedOperationException();
   }

   default boolean flush(ByteBuffer... buffer) throws IOException {
      throw new UnsupportedOperationException();
   }

   Object getTransport();

   long getIdleTimeout();

   void setIdleTimeout(long var1);

   void fillInterested(Callback var1) throws ReadPendingException;

   boolean tryFillInterested(Callback var1);

   boolean isFillInterested();

   default void write(Callback callback, ByteBuffer... buffers) throws WritePendingException {
      throw new UnsupportedOperationException();
   }

   Connection getConnection();

   void setConnection(Connection var1);

   void onOpen();

   void onClose(Throwable var1);

   void upgrade(Connection var1);

   public interface Wrapper {
      EndPoint unwrap();
   }
}
