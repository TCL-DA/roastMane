package org.eclipse.yasson.internal;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.spi.JsonProvider;
import java.util.Optional;

public class JsonBindingBuilder implements JsonbBuilder {
   private JsonbConfig config = new JsonbConfig();
   private JsonProvider provider = null;

   public JsonbBuilder withConfig(JsonbConfig config) {
      this.config = config;
      return this;
   }

   public JsonbBuilder withProvider(JsonProvider jsonpProvider) {
      this.provider = jsonpProvider;
      return this;
   }

   public JsonbConfig getConfig() {
      return this.config;
   }

   public Optional<JsonProvider> getProvider() {
      return Optional.ofNullable(this.provider);
   }

   public Jsonb build() {
      return new JsonBinding(this);
   }
}
