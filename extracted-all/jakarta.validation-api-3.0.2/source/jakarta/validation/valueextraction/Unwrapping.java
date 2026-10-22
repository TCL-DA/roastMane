package jakarta.validation.valueextraction;

import jakarta.validation.Payload;

public interface Unwrapping {
   public interface Skip extends Payload {
   }

   public interface Unwrap extends Payload {
   }
}
