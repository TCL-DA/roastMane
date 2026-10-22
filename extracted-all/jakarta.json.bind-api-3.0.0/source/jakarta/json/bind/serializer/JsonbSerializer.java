package jakarta.json.bind.serializer;

import jakarta.json.stream.JsonGenerator;

public interface JsonbSerializer<T> {
   void serialize(T var1, JsonGenerator var2, SerializationContext var3);
}
