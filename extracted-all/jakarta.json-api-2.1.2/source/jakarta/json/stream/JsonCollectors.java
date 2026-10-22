package jakarta.json.stream;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;

public final class JsonCollectors {
   private JsonCollectors() {
   }

   public static Collector<JsonValue, JsonArrayBuilder, JsonArray> toJsonArray() {
      return Collector.of(Json::createArrayBuilder, JsonArrayBuilder::add, JsonArrayBuilder::addAll, JsonArrayBuilder::build);
   }

   public static Collector<Entry<String, JsonValue>, JsonObjectBuilder, JsonObject> toJsonObject() {
      return Collector.of(Json::createObjectBuilder, (b, v) -> b.add(v.getKey(), v.getValue()), JsonObjectBuilder::addAll, JsonObjectBuilder::build);
   }

   public static Collector<JsonValue, JsonObjectBuilder, JsonObject> toJsonObject(
      Function<JsonValue, String> keyMapper, Function<JsonValue, JsonValue> valueMapper
   ) {
      return Collector.of(
         Json::createObjectBuilder,
         (b, v) -> b.add(keyMapper.apply(v), valueMapper.apply(v)),
         JsonObjectBuilder::addAll,
         JsonObjectBuilder::build,
         Characteristics.UNORDERED
      );
   }

   public static <T extends JsonArrayBuilder> Collector<JsonValue, Map<String, T>, JsonObject> groupingBy(
      Function<JsonValue, String> classifier, Collector<JsonValue, T, JsonArray> downstream
   ) {
      BiConsumer<Map<String, T>, JsonValue> accumulator = (map, value) -> {
         String key = classifier.apply(value);
         if (key == null) {
            throw new JsonException("element cannot be mapped to a null key");
         } else {
            T arrayBuilder = map.computeIfAbsent(key, v -> downstream.supplier().get());
            downstream.accumulator().accept(arrayBuilder, value);
         }
      };
      Function<Map<String, T>, JsonObject> finisher = map -> {
         JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
         map.forEach((k, v) -> {
            JsonArray array = downstream.finisher().apply((T)v);
            objectBuilder.add(k, array);
         });
         return objectBuilder.build();
      };
      BinaryOperator<Map<String, T>> combiner = (map1, map2) -> {
         map1.putAll(map2);
         return map1;
      };
      return Collector.of(HashMap::new, accumulator, combiner, finisher, Characteristics.UNORDERED);
   }

   public static Collector<JsonValue, Map<String, JsonArrayBuilder>, JsonObject> groupingBy(Function<JsonValue, String> classifier) {
      return groupingBy(classifier, toJsonArray());
   }
}
