module org.eclipse.jetty.http {
   requires org.slf4j;
   requires transitive org.eclipse.jetty.io;

   exports org.eclipse.jetty.http;
   exports org.eclipse.jetty.http.compression;
   exports org.eclipse.jetty.http.pathmap;
   exports org.eclipse.jetty.http.content;

   uses org.eclipse.jetty.http.HttpFieldPreEncoder;

   provides org.eclipse.jetty.http.HttpFieldPreEncoder with
      org.eclipse.jetty.http.Http10FieldPreEncoder,
      org.eclipse.jetty.http.Http11FieldPreEncoder;
}
