package retrofit2;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import kotlin.Unit;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Streaming;

final class BuiltInConverters extends Converter.Factory {
   @Nullable
   @Override
   public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
      if (type == ResponseBody.class) {
         return (Converter<ResponseBody, ?>)(Utils.isAnnotationPresent(annotations, Streaming.class)
            ? BuiltInConverters.StreamingResponseBodyConverter.INSTANCE
            : BuiltInConverters.BufferingResponseBodyConverter.INSTANCE);
      } else if (type == Void.class) {
         return BuiltInConverters.VoidResponseBodyConverter.INSTANCE;
      } else {
         return Utils.isUnit(type) ? BuiltInConverters.UnitResponseBodyConverter.INSTANCE : null;
      }
   }

   @Nullable
   @Override
   public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
      return RequestBody.class.isAssignableFrom(Utils.getRawType(type)) ? BuiltInConverters.RequestBodyConverter.INSTANCE : null;
   }

   static final class BufferingResponseBodyConverter implements Converter<ResponseBody, ResponseBody> {
      static final BuiltInConverters.BufferingResponseBodyConverter INSTANCE = new BuiltInConverters.BufferingResponseBodyConverter();

      public ResponseBody convert(ResponseBody value) throws IOException {
         ResponseBody var2;
         try {
            var2 = Utils.buffer(value);
         } finally {
            value.close();
         }

         return var2;
      }
   }

   static final class RequestBodyConverter implements Converter<RequestBody, RequestBody> {
      static final BuiltInConverters.RequestBodyConverter INSTANCE = new BuiltInConverters.RequestBodyConverter();

      public RequestBody convert(RequestBody value) {
         return value;
      }
   }

   static final class StreamingResponseBodyConverter implements Converter<ResponseBody, ResponseBody> {
      static final BuiltInConverters.StreamingResponseBodyConverter INSTANCE = new BuiltInConverters.StreamingResponseBodyConverter();

      public ResponseBody convert(ResponseBody value) {
         return value;
      }
   }

   static final class ToStringConverter implements Converter<Object, String> {
      static final BuiltInConverters.ToStringConverter INSTANCE = new BuiltInConverters.ToStringConverter();

      public String convert(Object value) {
         return value.toString();
      }
   }

   static final class UnitResponseBodyConverter implements Converter<ResponseBody, Unit> {
      static final BuiltInConverters.UnitResponseBodyConverter INSTANCE = new BuiltInConverters.UnitResponseBodyConverter();

      public Unit convert(ResponseBody value) {
         value.close();
         return Unit.INSTANCE;
      }
   }

   static final class VoidResponseBodyConverter implements Converter<ResponseBody, Void> {
      static final BuiltInConverters.VoidResponseBodyConverter INSTANCE = new BuiltInConverters.VoidResponseBodyConverter();

      public Void convert(ResponseBody value) {
         value.close();
         return null;
      }
   }
}
