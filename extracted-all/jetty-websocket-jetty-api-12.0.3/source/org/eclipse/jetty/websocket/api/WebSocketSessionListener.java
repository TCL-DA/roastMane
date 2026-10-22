package org.eclipse.jetty.websocket.api;

public interface WebSocketSessionListener {
   default void onWebSocketSessionCreated(Session session) {
   }

   default void onWebSocketSessionOpened(Session session) {
   }

   default void onWebSocketSessionClosed(Session session) {
   }
}
