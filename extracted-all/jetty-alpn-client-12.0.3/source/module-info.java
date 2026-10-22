module org.eclipse.jetty.alpn.client {
   requires org.slf4j;
   requires transitive org.eclipse.jetty.io;

   exports org.eclipse.jetty.alpn.client;

   uses org.eclipse.jetty.io.ssl.ALPNProcessor.Client;
}
