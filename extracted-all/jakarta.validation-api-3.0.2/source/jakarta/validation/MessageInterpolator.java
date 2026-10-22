package jakarta.validation;

import jakarta.validation.metadata.ConstraintDescriptor;
import java.util.Locale;

public interface MessageInterpolator {
   String interpolate(String var1, MessageInterpolator.Context var2);

   String interpolate(String var1, MessageInterpolator.Context var2, Locale var3);

   public interface Context {
      ConstraintDescriptor<?> getConstraintDescriptor();

      Object getValidatedValue();

      <T> T unwrap(Class<T> var1);
   }
}
