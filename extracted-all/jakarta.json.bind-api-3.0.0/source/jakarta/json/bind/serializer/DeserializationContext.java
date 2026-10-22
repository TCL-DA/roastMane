package jakarta.json.bind.serializer;

import jakarta.json.stream.JsonParser;
import java.lang.reflect.Type;

public interface DeserializationContext {
   <T> T deserialize(Class<T> var1, JsonParser var2);

   <T> T deserialize(Type var1, JsonParser var2);
}
