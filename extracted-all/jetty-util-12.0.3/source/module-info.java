module org.eclipse.jetty.util {
   requires org.slf4j;
   requires transitive java.naming;
   requires static java.desktop;
   requires static java.sql;

   exports org.eclipse.jetty.util;
   exports org.eclipse.jetty.util.annotation;
   exports org.eclipse.jetty.util.component;
   exports org.eclipse.jetty.util.compression;
   exports org.eclipse.jetty.util.preventers;
   exports org.eclipse.jetty.util.resource;
   exports org.eclipse.jetty.util.security;
   exports org.eclipse.jetty.util.ssl;
   exports org.eclipse.jetty.util.statistic;
   exports org.eclipse.jetty.util.thread;
   exports org.eclipse.jetty.util.thread.strategy;

   uses org.eclipse.jetty.util.security.CredentialProvider;
}
