package com.segment.analytics.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

public final class AutoValueAdapterFactory implements TypeAdapterFactory {
   public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
      Class<? super T> rawType = type.getRawType();
      if (!rawType.isAnnotationPresent(AutoGson.class)) {
         return null;
      } else {
         String packageName = rawType.getPackage().getName();
         String className = rawType.getName().substring(packageName.length() + 1).replace('$', '_');
         String autoValueName = packageName + ".AutoValue_" + className;

         try {
            Class<?> autoValueType = Class.forName(autoValueName);
            return gson.getAdapter(autoValueType);
         } catch (ClassNotFoundException var8) {
            throw new RuntimeException("Could not load AutoValue type " + autoValueName, var8);
         }
      }
   }
}
