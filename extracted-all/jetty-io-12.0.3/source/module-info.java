module org.eclipse.jetty.io {
   requires org.slf4j;
   requires transitive org.eclipse.jetty.util;
   requires static org.eclipse.jetty.jmx;

   exports org.eclipse.jetty.io;
   exports org.eclipse.jetty.io.content;
   exports org.eclipse.jetty.io.ssl;
}
