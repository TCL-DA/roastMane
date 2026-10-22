package jakarta.validation;

import java.lang.annotation.ElementType;

public interface TraversableResolver {
   boolean isReachable(Object var1, Path.Node var2, Class<?> var3, Path var4, ElementType var5);

   boolean isCascadable(Object var1, Path.Node var2, Class<?> var3, Path var4, ElementType var5);
}
