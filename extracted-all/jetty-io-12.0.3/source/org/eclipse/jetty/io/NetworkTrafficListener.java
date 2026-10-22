package org.eclipse.jetty.io;

import java.net.Socket;
import java.nio.ByteBuffer;

public interface NetworkTrafficListener {
   default void opened(Socket socket) {
   }

   default void incoming(Socket socket, ByteBuffer bytes) {
   }

   default void outgoing(Socket socket, ByteBuffer bytes) {
   }

   default void closed(Socket socket) {
   }
}
