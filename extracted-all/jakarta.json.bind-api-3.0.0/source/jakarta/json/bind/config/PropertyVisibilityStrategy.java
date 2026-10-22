package jakarta.json.bind.config;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface PropertyVisibilityStrategy {
   boolean isVisible(Field var1);

   boolean isVisible(Method var1);
}
