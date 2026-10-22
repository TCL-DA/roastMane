package org.eclipse.jetty.websocket.common;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.component.Graceful;
import org.eclipse.jetty.websocket.api.Callback;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketSessionListener;

public class SessionTracker extends AbstractLifeCycle implements WebSocketSessionListener, Graceful, Dumpable {
   private final Set<Session> sessions = Collections.newSetFromMap(new ConcurrentHashMap<>());
   private boolean isShutdown = false;

   public Collection<Session> getSessions() {
      return Set.copyOf(this.sessions);
   }

   public void onWebSocketSessionOpened(Session session) {
      this.sessions.add(session);
   }

   public void onWebSocketSessionClosed(Session session) {
      this.sessions.remove(session);
   }

   protected void doStart() throws Exception {
      this.isShutdown = false;
      super.doStart();
   }

   protected void doStop() throws Exception {
      this.sessions.clear();
      super.doStop();
   }

   public CompletableFuture<Void> shutdown() {
      this.isShutdown = true;
      return Graceful.shutdown(() -> {
         for (Session session : this.sessions) {
            if (Thread.interrupted()) {
               break;
            }

            session.close(1001, "Container being shut down", Callback.NOOP);
         }
      });
   }

   public boolean isShutdown() {
      return this.isShutdown;
   }

   @ManagedAttribute("Total number of active WebSocket Sessions")
   public int getNumSessions() {
      return this.sessions.size();
   }

   public void dump(Appendable out, String indent) throws IOException {
      Dumpable.dumpObjects(out, indent, this, new Object[]{this.sessions});
   }
}
