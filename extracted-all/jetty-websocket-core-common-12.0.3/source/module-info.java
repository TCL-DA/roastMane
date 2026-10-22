module org.eclipse.jetty.websocket.core.common {
   requires org.eclipse.jetty.http;
   requires org.slf4j;
   requires transitive org.eclipse.jetty.io;
   requires transitive org.eclipse.jetty.util;

   exports org.eclipse.jetty.websocket.core;
   exports org.eclipse.jetty.websocket.core.exception;
   exports org.eclipse.jetty.websocket.core.messages;
   exports org.eclipse.jetty.websocket.core.util;
   exports org.eclipse.jetty.websocket.core.internal to
      org.eclipse.jetty.util;

   uses org.eclipse.jetty.websocket.core.Extension;

   provides org.eclipse.jetty.websocket.core.Extension with
      org.eclipse.jetty.websocket.core.internal.FragmentExtension,
      org.eclipse.jetty.websocket.core.internal.IdentityExtension,
      org.eclipse.jetty.websocket.core.internal.PerMessageDeflateExtension,
      org.eclipse.jetty.websocket.core.internal.ValidationExtension;
}
