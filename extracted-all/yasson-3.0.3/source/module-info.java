module org.eclipse.yasson {
   requires jakarta.json;
   requires jakarta.json.bind;
   requires java.logging;
   requires static java.xml;
   requires static java.naming;
   requires static java.sql;
   requires static java.desktop;
   requires static jakarta.cdi;

   exports org.eclipse.yasson;
   exports org.eclipse.yasson.spi;

   uses org.eclipse.yasson.spi.JsonbComponentInstanceCreator;

   provides jakarta.json.bind.spi.JsonbProvider with
      org.eclipse.yasson.JsonBindingProvider;
}
