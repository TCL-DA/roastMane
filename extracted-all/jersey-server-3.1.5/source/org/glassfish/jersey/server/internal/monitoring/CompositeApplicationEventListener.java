package org.glassfish.jersey.server.internal.monitoring;

import java.util.ArrayList;
import java.util.List;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

public class CompositeApplicationEventListener implements ApplicationEventListener {
   private final Iterable<ApplicationEventListener> applicationEventListeners;

   public CompositeApplicationEventListener(Iterable<ApplicationEventListener> applicationEventListeners) {
      this.applicationEventListeners = applicationEventListeners;
   }

   @Override
   public void onEvent(ApplicationEvent event) {
      for (ApplicationEventListener applicationEventListener : this.applicationEventListeners) {
         applicationEventListener.onEvent(event);
      }
   }

   @Override
   public RequestEventListener onRequest(RequestEvent requestEvent) {
      List<RequestEventListener> requestEventListeners = new ArrayList<>();

      for (ApplicationEventListener applicationEventListener : this.applicationEventListeners) {
         RequestEventListener requestEventListener = applicationEventListener.onRequest(requestEvent);
         if (requestEventListener != null) {
            requestEventListeners.add(requestEventListener);
         }
      }

      return requestEventListeners.isEmpty() ? null : new CompositeRequestEventListener(requestEventListeners);
   }
}
