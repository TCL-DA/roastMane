package jakarta.json.bind.serializer;

import jakarta.json.stream.JsonParser;
import java.lang.reflect.Type;

public interface JsonbDeserializer<T> {
   T deserialize(JsonParser var1, DeserializationContext var2, Type var3);
}
