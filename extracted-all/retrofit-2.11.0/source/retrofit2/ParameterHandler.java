package retrofit2;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import okhttp3.RequestBody;

abstract class ParameterHandler<T> {
   abstract void apply(RequestBuilder var1, @Nullable T var2) throws IOException;

   final ParameterHandler<Iterable<T>> iterable() {
      return new ParameterHandler<Iterable<T>>() {
         void apply(RequestBuilder builder, @Nullable Iterable<T> values) throws IOException {
            if (values != null) {
               for (T value : values) {
                  ParameterHandler.this.apply(builder, value);
               }
            }
         }
      };
   }

   final ParameterHandler<Object> array() {
      return new ParameterHandler<Object>() {
         @Override
         void apply(RequestBuilder builder, @Nullable Object values) throws IOException {
            if (values != null) {
               int i = 0;

               for (int size = Array.getLength(values); i < size; i++) {
                  ParameterHandler.this.apply(builder, Array.get(values, i));
               }
            }
         }
      };
   }

   static final class Body<T> extends ParameterHandler<T> {
      private final Method method;
      private final int p;
      private final Converter<T, RequestBody> converter;

      Body(Method method, int p, Converter<T, RequestBody> converter) {
         this.method = method;
         this.p = p;
         this.converter = converter;
      }

      @Override
      void apply(RequestBuilder builder, @Nullable T value) {
         if (value == null) {
            throw Utils.parameterError(this.method, this.p, "Body parameter value must not be null.");
         } else {
            RequestBody body;
            try {
               body = this.converter.convert(value);
            } catch (IOException var5) {
               throw Utils.parameterError(this.method, var5, this.p, "Unable to convert " + value + " to RequestBody");
            }

            builder.setBody(body);
         }
      }
   }

   static final class Field<T> extends ParameterHandler<T> {
      private final String name;
      private final Converter<T, String> valueConverter;
      private final boolean encoded;

      Field(String name, Converter<T, String> valueConverter, boolean encoded) {
         this.name = Objects.requireNonNull(name, "name == null");
         this.valueConverter = valueConverter;
         this.encoded = encoded;
      }

      @Override
      void apply(RequestBuilder builder, @Nullable T value) throws IOException {
         if (value != null) {
            String fieldValue = this.valueConverter.convert(value);
            if (fieldValue != null) {
               builder.addFormField(this.name, fieldValue, this.encoded);
            }
         }
      }
   }

   static final class FieldMap<T> extends ParameterHandler<Map<String, T>> {
      private final Method method;
      private final int p;
      private final Converter<T, String> valueConverter;
      private final boolean encoded;

      FieldMap(Method method, int p, Converter<T, String> valueConverter, boolean encoded) {
         this.method = method;
         this.p = p;
         this.valueConverter = valueConverter;
         this.encoded = encoded;
      }

      void apply(RequestBuilder builder, @Nullable Map<String, T> value) throws IOException {
         if (value == null) {
            throw Utils.parameterError(this.method, this.p, "Field map was null.");
         } else {
            for (Entry<String, T> entry : value.entrySet()) {
               String entryKey = entry.getKey();
               if (entryKey == null) {
                  throw Utils.parameterError(this.method, this.p, "Field map contained null key.");
               }

               T entryValue = entry.getValue();
               if (entryValue == null) {
                  throw Utils.parameterError(this.method, this.p, "Field map contained null value for key '" + entryKey + "'.");
               }

               String fieldEntry = this.valueConverter.convert(entryValue);
               if (fieldEntry == null) {
                  throw Utils.parameterError(
                     this.method,
                     this.p,
                     "Field map value '" + entryValue + "' converted to null by " + this.valueConverter.getClass().getName() + " for key '" + entryKey + "'."
                  );
               }

               builder.addFormField(entryKey, fieldEntry, this.encoded);
            }
         }
      }
   }

   static final class Header<T> extends ParameterHandler<T> {
      private final String name;
      private final Converter<T, String> valueConverter;
      private final boolean allowUnsafeNonAsciiValues;

      Header(String name, Converter<T, String> valueConverter, boolean allowUnsafeNonAsciiValues) {
         this.name = Objects.requireNonNull(name, "name == null");
         this.valueConverter = valueConverter;
         this.allowUnsafeNonAsciiValues = allowUnsafeNonAsciiValues;
      }

      @Override
      void apply(RequestBuilder builder, @Nullable T value) throws IOException {
         if (value != null) {
            String headerValue = this.valueConverter.convert(value);
            if (headerValue != null) {
               builder.addHeader(this.name, headerValue, this.allowUnsafeNonAsciiValues);
            }
         }
      }
   }

   static final class HeaderMap<T> extends ParameterHandler<Map<String, T>> {
      private final Method method;
      private final int p;
      private final Converter<T, String> valueConverter;
      private final boolean allowUnsafeNonAsciiValues;

      HeaderMap(Method method, int p, Converter<T, String> valueConverter, boolean allowUnsafeNonAsciiValues) {
         this.method = method;
         this.p = p;
         this.valueConverter = valueConverter;
         this.allowUnsafeNonAsciiValues = allowUnsafeNonAsciiValues;
      }

      void apply(RequestBuilder builder, @Nullable Map<String, T> value) throws IOException {
         if (value == null) {
            throw Utils.parameterError(this.method, this.p, "Header map was null.");
         } else {
            for (Entry<String, T> entry : value.entrySet()) {
               String headerName = entry.getKey();
               if (headerName == null) {
                  throw Utils.parameterError(this.method, this.p, "Header map contained null key.");
               }

               T headerValue = entry.getValue();
               if (headerValue == null) {
                  throw Utils.parameterError(this.method, this.p, "Header map contained null value for key '" + headerName + "'.");
               }

               builder.addHeader(headerName, this.valueConverter.convert(headerValue), this.allowUnsafeNonAsciiValues);
            }
         }
      }
   }

   static final class Headers extends ParameterHandler<okhttp3.Headers> {
      private final Method method;
      private final int p;

      Headers(Method method, int p) {
         this.method = method;
         this.p = p;
      }

      void apply(RequestBuilder builder, @Nullable okhttp3.Headers headers) {
         if (headers == null) {
            throw Utils.parameterError(this.method, this.p, "Headers parameter must not be null.");
         } else {
            builder.addHeaders(headers);
         }
      }
   }

   static final class Part<T> extends ParameterHandler<T> {
      private final Method method;
      private final int p;
      private final okhttp3.Headers headers;
      private final Converter<T, RequestBody> converter;

      Part(Method method, int p, okhttp3.Headers headers, Converter<T, RequestBody> converter) {
         this.method = method;
         this.p = p;
         this.headers = headers;
         this.converter = converter;
      }

      @Override
      void apply(RequestBuilder builder, @Nullable T value) {
         if (value != null) {
            RequestBody body;
            try {
               body = this.converter.convert(value);
            } catch (IOException var5) {
               throw Utils.parameterError(this.method, this.p, "Unable to convert " + value + " to RequestBody", var5);
            }

            builder.addPart(this.headers, body);
         }
      }
   }

   static final class PartMap<T> extends ParameterHandler<Map<String, T>> {
      private final Method method;
      private final int p;
      private final Converter<T, RequestBody> valueConverter;
      private final String transferEncoding;

      PartMap(Method method, int p, Converter<T, RequestBody> valueConverter, String transferEncoding) {
         this.method = method;
         this.p = p;
         this.valueConverter = valueConverter;
         this.transferEncoding = transferEncoding;
      }

      void apply(RequestBuilder builder, @Nullable Map<String, T> value) throws IOException {
         if (value == null) {
            throw Utils.parameterError(this.method, this.p, "Part map was null.");
         } else {
            for (Entry<String, T> entry : value.entrySet()) {
               String entryKey = entry.getKey();
               if (entryKey == null) {
                  throw Utils.parameterError(this.method, this.p, "Part map contained null key.");
               }

               T entryValue = entry.getValue();
               if (entryValue == null) {
                  throw Utils.parameterError(this.method, this.p, "Part map contained null value for key '" + entryKey + "'.");
               }

               okhttp3.Headers headers = okhttp3.Headers.of(
                  new String[]{"Content-Disposition", "form-data; name=\"" + entryKey + "\"", "Content-Transfer-Encoding", this.transferEncoding}
               );
               builder.addPart(headers, this.valueConverter.convert(entryValue));
            }
         }
      }
   }

   static final class Path<T> extends ParameterHandler<T> {
      private final Method method;
      private final int p;
      private final String name;
      private final Converter<T, String> valueConverter;
      private final boolean encoded;

      Path(Method method, int p, String name, Converter<T, String> valueConverter, boolean encoded) {
         this.method = method;
         this.p = p;
         this.name = Objects.requireNonNull(name, "name == null");
         this.valueConverter = valueConverter;
         this.encoded = encoded;
      }

      @Override
      void apply(RequestBuilder builder, @Nullable T value) throws IOException {
         if (value == null) {
            throw Utils.parameterError(this.method, this.p, "Path parameter \"" + this.name + "\" value must not be null.");
         } else {
            builder.addPathParam(this.name, this.valueConverter.convert(value), this.encoded);
         }
      }
   }

   static final class Query<T> extends ParameterHandler<T> {
      private final String name;
      private final Converter<T, String> valueConverter;
      private final boolean encoded;

      Query(String name, Converter<T, String> valueConverter, boolean encoded) {
         this.name = Objects.requireNonNull(name, "name == null");
         this.valueConverter = valueConverter;
         this.encoded = encoded;
      }

      @Override
      void apply(RequestBuilder builder, @Nullable T value) throws IOException {
         if (value != null) {
            String queryValue = this.valueConverter.convert(value);
            if (queryValue != null) {
               builder.addQueryParam(this.name, queryValue, this.encoded);
            }
         }
      }
   }

   static final class QueryMap<T> extends ParameterHandler<Map<String, T>> {
      private final Method method;
      private final int p;
      private final Converter<T, String> valueConverter;
      private final boolean encoded;

      QueryMap(Method method, int p, Converter<T, String> valueConverter, boolean encoded) {
         this.method = method;
         this.p = p;
         this.valueConverter = valueConverter;
         this.encoded = encoded;
      }

      void apply(RequestBuilder builder, @Nullable Map<String, T> value) throws IOException {
         if (value == null) {
            throw Utils.parameterError(this.method, this.p, "Query map was null");
         } else {
            for (Entry<String, T> entry : value.entrySet()) {
               String entryKey = entry.getKey();
               if (entryKey == null) {
                  throw Utils.parameterError(this.method, this.p, "Query map contained null key.");
               }

               T entryValue = entry.getValue();
               if (entryValue == null) {
                  throw Utils.parameterError(this.method, this.p, "Query map contained null value for key '" + entryKey + "'.");
               }

               String convertedEntryValue = this.valueConverter.convert(entryValue);
               if (convertedEntryValue == null) {
                  throw Utils.parameterError(
                     this.method,
                     this.p,
                     "Query map value '" + entryValue + "' converted to null by " + this.valueConverter.getClass().getName() + " for key '" + entryKey + "'."
                  );
               }

               builder.addQueryParam(entryKey, convertedEntryValue, this.encoded);
            }
         }
      }
   }

   static final class QueryName<T> extends ParameterHandler<T> {
      private final Converter<T, String> nameConverter;
      private final boolean encoded;

      QueryName(Converter<T, String> nameConverter, boolean encoded) {
         this.nameConverter = nameConverter;
         this.encoded = encoded;
      }

      @Override
      void apply(RequestBuilder builder, @Nullable T value) throws IOException {
         if (value != null) {
            builder.addQueryParam(this.nameConverter.convert(value), null, this.encoded);
         }
      }
   }

   static final class RawPart extends ParameterHandler<okhttp3.MultipartBody.Part> {
      static final ParameterHandler.RawPart INSTANCE = new ParameterHandler.RawPart();

      private RawPart() {
      }

      void apply(RequestBuilder builder, @Nullable okhttp3.MultipartBody.Part value) {
         if (value != null) {
            builder.addPart(value);
         }
      }
   }

   static final class RelativeUrl extends ParameterHandler<Object> {
      private final Method method;
      private final int p;

      RelativeUrl(Method method, int p) {
         this.method = method;
         this.p = p;
      }

      @Override
      void apply(RequestBuilder builder, @Nullable Object value) {
         if (value == null) {
            throw Utils.parameterError(this.method, this.p, "@Url parameter is null.");
         } else {
            builder.setRelativeUrl(value);
         }
      }
   }

   static final class Tag<T> extends ParameterHandler<T> {
      final Class<T> cls;

      Tag(Class<T> cls) {
         this.cls = cls;
      }

      @Override
      void apply(RequestBuilder builder, @Nullable T value) {
         builder.addTag(this.cls, value);
      }
   }
}
