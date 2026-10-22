module org.apache.logging.log4j.slf4j2.impl {
   requires static biz.aQute.bnd.annotation;
   requires static com.google.errorprone.annotations;
   requires java.base;
   requires transitive org.apache.logging.log4j;
   requires static org.osgi.annotation.bundle;
   requires static org.osgi.annotation.versioning;
   requires transitive org.slf4j;

   exports org.apache.logging.slf4j;
   exports org.apache.logging.slf4j.message;

   provides org.slf4j.spi.SLF4JServiceProvider with
      org.apache.logging.slf4j.SLF4JServiceProvider;
}
