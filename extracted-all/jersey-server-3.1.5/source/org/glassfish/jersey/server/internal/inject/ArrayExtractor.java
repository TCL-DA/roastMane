package org.glassfish.jersey.server.internal.inject;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.ParamConverter;
import java.lang.reflect.Array;
import java.util.List;
import java.util.function.Function;

class ArrayExtractor implements MultivaluedParameterExtractor<Object> {
   private final String parameterName;
   private final String defaultValueString;
   private final Function<String, Object> typeExtractor;
   private final Class<?> type;

   private ArrayExtractor(Class<?> type, Function<String, Object> typeExtractor, String parameterName, String defaultValueString) {
      this.type = type;
      this.typeExtractor = typeExtractor;
      this.parameterName = parameterName;
      this.defaultValueString = defaultValueString;
   }

   @Override
   public String getName() {
      return this.parameterName;
   }

   @Override
   public String getDefaultValueString() {
      return this.defaultValueString;
   }

   @Override
   public Object extract(MultivaluedMap<String, String> parameters) {
      List<String> stringList = (List<String>)parameters.get(this.getName());
      Object array = null;
      if (stringList != null) {
         array = Array.newInstance(this.type, stringList.size());

         for (int i = 0; i < stringList.size(); i++) {
            Array.set(array, i, this.typeExtractor.apply(stringList.get(i)));
         }
      } else if (this.defaultValueString != null) {
         array = Array.newInstance(this.type, 1);
         Array.set(array, 0, this.typeExtractor.apply(this.defaultValueString));
      } else {
         array = Array.newInstance(this.type, 0);
      }

      return array;
   }

   public static MultivaluedParameterExtractor<Object> getInstance(Class<?> type, ParamConverter<?> converter, String parameterName, String defaultValueString) {
      Function<String, Object> typeExtractor = value -> converter.fromString(value);
      return new ArrayExtractor(type, typeExtractor, parameterName, defaultValueString);
   }

   public static MultivaluedParameterExtractor<Object> getInstance(
      Class<?> type, MultivaluedParameterExtractor<?> extractor, String parameterName, String defaultValueString
   ) {
      Function<String, Object> typeExtractor = value -> {
         MultivaluedMap<String, String> pair = new MultivaluedHashMap();
         pair.putSingle(parameterName, value);
         return extractor.extract(pair);
      };
      return new ArrayExtractor(type, typeExtractor, parameterName, defaultValueString);
   }
}
