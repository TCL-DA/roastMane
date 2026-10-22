package jakarta.json.bind.serializer;

import jakarta.json.stream.JsonGenerator;

public interface SerializationContext {
   <T> void serialize(String var1, T var2, JsonGenerator var3);

   <T> void serialize(T var1, JsonGenerator var2);
}
