package org.eclipse.jetty.server;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.Container;
import org.eclipse.jetty.util.component.Graceful;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.thread.Scheduler;

@ManagedObject("Connector Interface")
public interface Connector extends LifeCycle, Container, Graceful {
   Server getServer();

   Executor getExecutor();

   Scheduler getScheduler();

   ByteBufferPool getByteBufferPool();

   ConnectionFactory getConnectionFactory(String var1);

   <T> T getConnectionFactory(Class<T> var1);

   ConnectionFactory getDefaultConnectionFactory();

   Collection<ConnectionFactory> getConnectionFactories();

   List<String> getProtocols();

   @ManagedAttribute("maximum time a connection can be idle before being closed (in ms)")
   long getIdleTimeout();

   Object getTransport();

   Collection<EndPoint> getConnectedEndPoints();

   String getName();
}
