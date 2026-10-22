package org.eclipse.jetty.server;

import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.util.Attributes;
import org.eclipse.jetty.util.thread.Scheduler;
import org.eclipse.jetty.util.thread.ThreadPool;

public interface Components {
   ByteBufferPool getByteBufferPool();

   Scheduler getScheduler();

   ThreadPool getThreadPool();

   Attributes getCache();
}
