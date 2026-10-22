package jakarta.json;

import java.util.Collection;
import java.util.Map;

public interface JsonBuilderFactory {
   JsonObjectBuilder createObjectBuilder();

   default JsonObjectBuilder createObjectBuilder(JsonObject object) {
      throw new UnsupportedOperationException();
   }

   default JsonObjectBuilder createObjectBuilder(Map<String, Object> object) {
      throw new UnsupportedOperationException();
   }

   JsonArrayBuilder createArrayBuilder();

   default JsonArrayBuilder createArrayBuilder(JsonArray array) {
      throw new UnsupportedOperationException();
   }

   default JsonArrayBuilder createArrayBuilder(Collection<?> collection) {
      throw new UnsupportedOperationException();
   }

   Map<String, ?> getConfigInUse();
}
