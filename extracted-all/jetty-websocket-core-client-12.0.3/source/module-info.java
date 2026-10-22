module org.eclipse.jetty.websocket.core.client {
   requires org.slf4j;
   requires transitive org.eclipse.jetty.client;
   requires transitive org.eclipse.jetty.websocket.core.common;
   requires static org.eclipse.jetty.xml;

   exports org.eclipse.jetty.websocket.core.client;
}
