package org.eclipse.yasson;

import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.spi.JsonbProvider;
import org.eclipse.yasson.internal.JsonBindingBuilder;

public class JsonBindingProvider extends JsonbProvider {
   public JsonbBuilder create() {
      return new JsonBindingBuilder();
   }
}
