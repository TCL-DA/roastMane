package jakarta.json;

public interface JsonStructure extends JsonValue {
   default JsonValue getValue(String jsonPointer) {
      return Json.createPointer(jsonPointer).getValue(this);
   }
}
