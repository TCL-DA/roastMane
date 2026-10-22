package org.eclipse.jetty.client;

import org.eclipse.jetty.util.ConcurrentPool;
import org.eclipse.jetty.util.ConcurrentPool.StrategyType;
import org.eclipse.jetty.util.annotation.ManagedObject;

@ManagedObject
public class RandomConnectionPool extends MultiplexConnectionPool {
   public RandomConnectionPool(Destination destination, int maxConnections, int initialMaxMultiplex) {
      super(
         destination,
         () -> new ConcurrentPool(StrategyType.RANDOM, maxConnections, false, MultiplexConnectionPool.newMaxMultiplexer(initialMaxMultiplex)),
         initialMaxMultiplex
      );
   }
}
