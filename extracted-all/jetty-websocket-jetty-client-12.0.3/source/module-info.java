module org.eclipse.jetty.websocket.client {
   requires org.eclipse.jetty.websocket.core.client;
   requires org.eclipse.jetty.websocket.common;
   requires org.slf4j;
   requires transitive org.eclipse.jetty.client;
   requires transitive org.eclipse.jetty.websocket.api;

   exports org.eclipse.jetty.websocket.client;
}
