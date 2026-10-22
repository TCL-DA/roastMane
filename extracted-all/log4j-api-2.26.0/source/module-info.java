module org.apache.logging.log4j {
   requires static biz.aQute.bnd.annotation;
   requires static com.google.errorprone.annotations;
   requires java.base;
   requires static java.management;
   requires static java.sql;
   requires static org.jspecify;
   requires static org.osgi.annotation.bundle;
   requires static org.osgi.annotation.versioning;
   requires static org.osgi.core;

   exports org.apache.logging.log4j;
   exports org.apache.logging.log4j.message;
   exports org.apache.logging.log4j.simple;
   exports org.apache.logging.log4j.spi;
   exports org.apache.logging.log4j.status;
   exports org.apache.logging.log4j.util;

   uses org.apache.logging.log4j.message.ThreadDumpMessage$ThreadInfoFactory;
   uses org.apache.logging.log4j.spi.Provider;
   uses org.apache.logging.log4j.util.PropertySource;

   provides org.apache.logging.log4j.util.PropertySource with
      org.apache.logging.log4j.util.EnvironmentPropertySource,
      org.apache.logging.log4j.util.SystemPropertiesPropertySource;
}
