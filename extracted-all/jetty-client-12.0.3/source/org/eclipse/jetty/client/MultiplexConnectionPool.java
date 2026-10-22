package org.eclipse.jetty.client;

import java.util.function.ToIntFunction;
import org.eclipse.jetty.util.ConcurrentPool;
import org.eclipse.jetty.util.ConcurrentPool.StrategyType;
import org.eclipse.jetty.util.Pool.Factory;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;

@ManagedObject
public class MultiplexConnectionPool extends AbstractConnectionPool {
   public static ToIntFunction<Connection> newMaxMultiplexer(int defaultMaxMultiplex) {
      return connection -> {
         int maxMultiplex = defaultMaxMultiplex;
         if (connection instanceof ConnectionPool.MaxMultiplexable maxMultiplexable) {
            maxMultiplex = maxMultiplexable.getMaxMultiplex();
         }

         return maxMultiplex;
      };
   }

   public MultiplexConnectionPool(Destination destination, int maxConnections, int initialMaxMultiplex) {
      this(destination, () -> new ConcurrentPool(StrategyType.FIRST, maxConnections, false, newMaxMultiplexer(initialMaxMultiplex)), initialMaxMultiplex);
   }

   protected MultiplexConnectionPool(Destination destination, org.eclipse.jetty.util.Pool.Factory<Connection> poolFactory, int initialMaxMultiplex) {
      super(destination, poolFactory, initialMaxMultiplex);
   }

   @ManagedAttribute("The initial multiplexing factor of connections")
   @Override
   public int getInitialMaxMultiplex() {
      return super.getInitialMaxMultiplex();
   }

   @Override
   public void setInitialMaxMultiplex(int initialMaxMultiplex) {
      super.setInitialMaxMultiplex(initialMaxMultiplex);
   }
}
