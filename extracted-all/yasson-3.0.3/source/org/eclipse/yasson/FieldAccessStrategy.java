package org.eclipse.yasson;

import jakarta.json.bind.config.PropertyVisibilityStrategy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class FieldAccessStrategy implements PropertyVisibilityStrategy {
   public boolean isVisible(Field field) {
      return true;
   }

   public boolean isVisible(Method method) {
      return false;
   }
}
