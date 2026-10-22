package org.eclipse.yasson.internal.deserializer.types;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.yasson.internal.DeserializationContextImpl;

class UrlDeserializer extends TypeDeserializer {
   UrlDeserializer(TypeDeserializerBuilder builder) {
      super(builder);
   }

   @Override
   Object deserializeStringValue(String value, DeserializationContextImpl context, Type rType) {
      URL url = null;

      try {
         url = new URL(value);
      } catch (MalformedURLException var6) {
         var6.printStackTrace();
      }

      return url;
   }
}
