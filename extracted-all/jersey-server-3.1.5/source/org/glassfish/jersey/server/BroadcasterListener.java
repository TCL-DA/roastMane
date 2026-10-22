package org.glassfish.jersey.server;

public interface BroadcasterListener<T> {
   void onException(ChunkedOutput<T> var1, Exception var2);

   void onClose(ChunkedOutput<T> var1);
}
