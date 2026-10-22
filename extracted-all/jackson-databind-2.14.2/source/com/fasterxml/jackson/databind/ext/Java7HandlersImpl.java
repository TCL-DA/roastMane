package com.fasterxml.jackson.databind.ext;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import java.nio.file.Path;

public class Java7HandlersImpl extends Java7Handlers {
   private final Class<?> _pathClass = Path.class;

   @Override
   public Class<?> getClassJavaNioFilePath() {
      return this._pathClass;
   }

   @Override
   public JsonDeserializer<?> getDeserializerForJavaNioFilePath(Class<?> rawType) {
      return rawType == this._pathClass ? new NioPathDeserializer() : null;
   }

   @Override
   public JsonSerializer<?> getSerializerForJavaNioFilePath(Class<?> rawType) {
      return this._pathClass.isAssignableFrom(rawType) ? new NioPathSerializer() : null;
   }
}
