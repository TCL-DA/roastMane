package org.eclipse.jetty.websocket.api;

import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public interface WebSocketContainer {
   Executor getExecutor();

   Collection<Session> getOpenSessions();

   void addSessionListener(WebSocketSessionListener var1);

   boolean removeSessionListener(WebSocketSessionListener var1);

   void notifySessionListeners(Consumer<WebSocketSessionListener> var1);
}
