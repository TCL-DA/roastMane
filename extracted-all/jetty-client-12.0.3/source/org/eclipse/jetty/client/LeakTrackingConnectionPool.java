package org.eclipse.jetty.client;

import org.eclipse.jetty.util.LeakDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LeakTrackingConnectionPool extends DuplexConnectionPool {
   private static final Logger LOG = LoggerFactory.getLogger(LeakTrackingConnectionPool.class);
   private final LeakDetector<Connection> leakDetector = new LeakDetector<Connection>() {
      protected void leaked(LeakDetector<Connection>.LeakInfo leakInfo) {
         LeakTrackingConnectionPool.this.leaked(leakInfo);
      }
   };

   public LeakTrackingConnectionPool(Destination destination, int maxConnections) {
      super(destination, maxConnections);
      this.addBean(this.leakDetector);
   }

   @Override
   protected void acquired(Connection connection) {
      if (!this.leakDetector.acquired(connection)) {
         LOG.info("Connection {}@{} not tracked", connection, this.leakDetector.id(connection));
      }
   }

   @Override
   protected void released(Connection connection) {
      if (!this.leakDetector.released(connection)) {
         LOG.info("Connection {}@{} released but not acquired", connection, this.leakDetector.id(connection));
      }
   }

   protected void leaked(LeakDetector<Connection>.LeakInfo leakInfo) {
      LOG.info("Connection {} leaked at:", leakInfo.getResourceDescription(), leakInfo.getStackFrames());
   }
}
