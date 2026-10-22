module jakarta.json {
   requires java.logging;

   exports jakarta.json;
   exports jakarta.json.spi;
   exports jakarta.json.stream;

   uses jakarta.json.spi.JsonProvider;
}
