package jakarta.validation.valueextraction;

import jakarta.validation.ValidationException;

public class ValueExtractorDeclarationException extends ValidationException {
   public ValueExtractorDeclarationException() {
   }

   public ValueExtractorDeclarationException(String message) {
      super(message);
   }

   public ValueExtractorDeclarationException(Throwable cause) {
      super(cause);
   }

   public ValueExtractorDeclarationException(String message, Throwable cause) {
      super(message, cause);
   }
}
