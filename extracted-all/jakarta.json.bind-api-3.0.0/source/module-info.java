module jakarta.json.bind {
   requires jakarta.json;
   requires java.logging;

   exports jakarta.json.bind;
   exports jakarta.json.bind.adapter;
   exports jakarta.json.bind.annotation;
   exports jakarta.json.bind.config;
   exports jakarta.json.bind.serializer;
   exports jakarta.json.bind.spi;

   uses jakarta.json.bind.spi.JsonbProvider;
}
