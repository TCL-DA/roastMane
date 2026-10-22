package org.eclipse.jetty.client;

import java.util.concurrent.CompletableFuture;

public interface ConnectionPool {
   default CompletableFuture<Void> preCreateConnections(int connectionCount) {
      return CompletableFuture.completedFuture(null);
   }

   boolean isActive(Connection var1);

   boolean isEmpty();

   Connection acquire(boolean var1);

   boolean accept(Connection var1);

   boolean release(Connection var1);

   boolean remove(Connection var1);

   public interface Factory {
      ConnectionPool newConnectionPool(Destination var1);
   }

   public interface MaxMultiplexable {
      int getMaxMultiplex();
   }

   public interface MaxUsable {
      int getMaxUsage();
   }
}
