package jakarta.json;

public interface JsonMergePatch {
   JsonValue apply(JsonValue var1);

   JsonValue toJsonValue();
}
