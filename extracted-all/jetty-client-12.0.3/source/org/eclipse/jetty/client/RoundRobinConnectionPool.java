package org.eclipse.jetty.client;

import org.eclipse.jetty.util.ConcurrentPool;
import org.eclipse.jetty.util.ConcurrentPool.StrategyType;
import org.eclipse.jetty.util.annotation.ManagedObject;

@ManagedObject
public class RoundRobinConnectionPool extends MultiplexConnectionPool {
   public RoundRobinConnectionPool(Destination destination, int maxConnections) {
      this(destination, maxConnections, 1);
   }

   public RoundRobinConnectionPool(Destination destination, int maxConnections, int initialMaxMultiplex) {
      super(destination, () -> new ConcurrentPool(StrategyType.ROUND_ROBIN, maxConnections, false, newMaxMultiplexer(initialMaxMultiplex)), initialMaxMultiplex);
      this.setMaximizeConnections(true);
   }
}
