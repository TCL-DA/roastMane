module org.eclipse.jetty.server {
   requires transitive org.eclipse.jetty.http;
   requires transitive org.slf4j;
   requires static org.eclipse.jetty.jmx;

   exports org.eclipse.jetty.server;
   exports org.eclipse.jetty.server.handler;
   exports org.eclipse.jetty.server.handler.gzip;
   exports org.eclipse.jetty.server.handler.jmx to
      org.eclipse.jetty.jmx;
   exports org.eclipse.jetty.server.jmx to
      org.eclipse.jetty.jmx;
}
