module org.eclipse.jetty.client {
   requires org.eclipse.jetty.alpn.client;
   requires org.slf4j;
   requires transitive org.eclipse.jetty.http;
   requires static java.management;
   requires static java.security.jgss;
   requires static org.eclipse.jetty.jmx;

   exports org.eclipse.jetty.client;
   exports org.eclipse.jetty.client.transport;
   exports org.eclipse.jetty.client.jmx to
      org.eclipse.jetty.jmx;
}
