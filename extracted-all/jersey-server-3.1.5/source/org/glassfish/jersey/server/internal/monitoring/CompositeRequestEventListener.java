package org.glassfish.jersey.server.internal.monitoring;

import java.util.List;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

public class CompositeRequestEventListener implements RequestEventListener {
   private final List<RequestEventListener> requestEventListeners;

   public CompositeRequestEventListener(List<RequestEventListener> requestEventListeners) {
      this.requestEventListeners = requestEventListeners;
   }

   @Override
   public void onEvent(RequestEvent event) {
      for (RequestEventListener requestEventListener : this.requestEventListeners) {
         requestEventListener.onEvent(event);
      }
   }
}
