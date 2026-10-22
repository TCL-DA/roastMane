package org.apache.logging.log4j.core.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;
import org.apache.logging.log4j.core.util.Integers;

public final class Log4jStackTraceElementDeserializer extends StdScalarDeserializer<StackTraceElement> {
   private static final long serialVersionUID = 1L;

   public Log4jStackTraceElementDeserializer() {
      super(StackTraceElement.class);
   }

   public StackTraceElement deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
      JsonToken t = jp.getCurrentToken();
      if (t == JsonToken.START_OBJECT) {
         String className = null;
         String methodName = null;
         String fileName = null;
         int lineNumber = -1;

         while ((t = jp.nextValue()) != JsonToken.END_OBJECT) {
            String propName = jp.getCurrentName();
            switch (propName) {
               case "class":
                  className = jp.getText();
                  break;
               case "file":
                  fileName = jp.getText();
                  break;
               case "line":
                  if (t.isNumeric()) {
                     lineNumber = jp.getIntValue();
                  } else {
                     try {
                        lineNumber = Integers.parseInt(jp.getText());
                     } catch (NumberFormatException var12) {
                        throw JsonMappingException.from(jp, "Non-numeric token (" + t + ") for property 'line'", var12);
                     }
                  }
                  break;
               case "method":
                  methodName = jp.getText();
                  break;
               case "nativeMethod":
               default:
                  this.handleUnknownProperty(jp, ctxt, this._valueClass, propName);
            }
         }

         return new StackTraceElement(className, methodName, fileName, lineNumber);
      } else {
         throw JsonMappingException.from(jp, String.format("Cannot deserialize instance of %s out of %s token", ClassUtil.nameOf(this._valueClass), t));
      }
   }
}
