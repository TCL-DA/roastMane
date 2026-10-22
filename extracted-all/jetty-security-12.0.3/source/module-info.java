module org.eclipse.jetty.security {
   requires transitive org.eclipse.jetty.server;
   requires transitive org.eclipse.jetty.util;
   requires transitive org.slf4j;
   requires static java.security.jgss;
   requires static java.sql;

   exports org.eclipse.jetty.security;
   exports org.eclipse.jetty.security.authentication;
   exports org.eclipse.jetty.security.jaas;

   uses org.eclipse.jetty.security.Authenticator$Factory;
}
