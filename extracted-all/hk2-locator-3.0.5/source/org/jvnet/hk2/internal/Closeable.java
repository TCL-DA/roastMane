package org.jvnet.hk2.internal;

public interface Closeable {
   boolean close();

   boolean isClosed();
}
