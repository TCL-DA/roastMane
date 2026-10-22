package org.eclipse.jetty.client;

import java.io.Closeable;

public interface Connection extends Closeable {
   void send(Request var1, Response.CompleteListener var2);

   @Override
   void close();

   boolean isClosed();
}
