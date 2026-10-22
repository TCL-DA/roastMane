module org.eclipse.parsson {
   requires transitive jakarta.json;

   exports org.eclipse.parsson.api;

   provides jakarta.json.spi.JsonProvider with
      org.eclipse.parsson.JsonProviderImpl;
}
