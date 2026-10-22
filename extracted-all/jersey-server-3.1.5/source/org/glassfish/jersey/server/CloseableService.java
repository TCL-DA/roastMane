package org.glassfish.jersey.server;

import java.io.Closeable;

public interface CloseableService {
   boolean add(Closeable var1);

   void close();
}
