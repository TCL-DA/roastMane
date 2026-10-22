package org.eclipse.jetty.websocket.core.util;

public interface DemandChain {
   void demand();

   default void setNextDemand(DemandChain nextDemand) {
   }
}
