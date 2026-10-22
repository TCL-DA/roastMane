package org.eclipse.yasson.internal.deserializer;

import org.eclipse.yasson.internal.DeserializationContextImpl;

public final class JustReturn implements ModelDeserializer<Object> {
   private static final JustReturn INSTANCE = new JustReturn();

   private JustReturn() {
   }

   public static JustReturn instance() {
      return INSTANCE;
   }

   @Override
   public Object deserialize(Object value, DeserializationContextImpl context) {
      return value;
   }

   @Override
   public String toString() {
      return "No other operations will be performed";
   }
}
