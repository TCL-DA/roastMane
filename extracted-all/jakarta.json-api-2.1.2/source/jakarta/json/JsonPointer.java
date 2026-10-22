package jakarta.json;

public interface JsonPointer {
   <T extends JsonStructure> T add(T var1, JsonValue var2);

   <T extends JsonStructure> T remove(T var1);

   <T extends JsonStructure> T replace(T var1, JsonValue var2);

   boolean containsValue(JsonStructure var1);

   JsonValue getValue(JsonStructure var1);

   @Override
   String toString();
}
