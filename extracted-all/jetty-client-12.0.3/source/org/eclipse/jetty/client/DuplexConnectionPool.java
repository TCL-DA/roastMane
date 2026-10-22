package org.eclipse.jetty.client;

import org.eclipse.jetty.util.ConcurrentPool;
import org.eclipse.jetty.util.ConcurrentPool.StrategyType;
import org.eclipse.jetty.util.annotation.ManagedObject;

@ManagedObject
public class DuplexConnectionPool extends AbstractConnectionPool {
   public DuplexConnectionPool(Destination destination, int maxConnections) {
      super(destination, () -> new ConcurrentPool(StrategyType.FIRST, maxConnections, false), 1);
   }
}
