package jakarta.json.bind;

import jakarta.json.bind.spi.JsonbProvider;
import jakarta.json.spi.JsonProvider;

public interface JsonbBuilder {
   JsonbBuilder withConfig(JsonbConfig var1);

   JsonbBuilder withProvider(JsonProvider var1);

   Jsonb build();

   static Jsonb create() {
      return JsonbProvider.provider().create().build();
   }

   static Jsonb create(JsonbConfig config) {
      return JsonbProvider.provider().create().withConfig(config).build();
   }

   static JsonbBuilder newBuilder() {
      return JsonbProvider.provider().create();
   }

   static JsonbBuilder newBuilder(String providerName) {
      return JsonbProvider.provider(providerName).create();
   }

   static JsonbBuilder newBuilder(JsonbProvider provider) {
      return provider.create();
   }
}
