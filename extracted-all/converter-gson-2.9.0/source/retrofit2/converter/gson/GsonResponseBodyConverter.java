package retrofit2.converter.gson;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Converter;

final class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
   private final Gson gson;
   private final TypeAdapter<T> adapter;

   GsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
      this.gson = gson;
      this.adapter = adapter;
   }

   public T convert(ResponseBody value) throws IOException {
      JsonReader jsonReader = this.gson.newJsonReader(value.charStream());

      Object var4;
      try {
         T result = (T)this.adapter.read(jsonReader);
         if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
            throw new JsonIOException("JSON document was not fully consumed.");
         }

         var4 = result;
      } finally {
         value.close();
      }

      return (T)var4;
   }
}
