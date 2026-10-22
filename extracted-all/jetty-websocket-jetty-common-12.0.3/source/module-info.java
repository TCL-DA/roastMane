module org.eclipse.jetty.websocket.common {
   requires org.eclipse.jetty.util;
   requires org.slf4j;
   requires transitive org.eclipse.jetty.websocket.core.common;
   requires transitive org.eclipse.jetty.websocket.api;

   exports org.eclipse.jetty.websocket.common;

   provides org.eclipse.jetty.websocket.api.ExtensionConfig.Parser with
      org.eclipse.jetty.websocket.common.ExtensionConfigParser;
}
