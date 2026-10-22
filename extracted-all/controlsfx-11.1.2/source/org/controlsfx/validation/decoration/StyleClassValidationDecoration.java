package org.controlsfx.validation.decoration;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import javafx.scene.control.Control;
import org.controlsfx.control.decoration.Decoration;
import org.controlsfx.control.decoration.StyleClassDecoration;
import org.controlsfx.validation.ValidationMessage;

public class StyleClassValidationDecoration extends AbstractValidationDecoration {
   private final String errorClass;
   private final String warningClass;
   private final String infoClass;
   private final String okClass;

   public StyleClassValidationDecoration() {
      this(null, null);
   }

   public StyleClassValidationDecoration(String errorClass, String warningClass) {
      this(errorClass, warningClass, null, null);
   }

   public StyleClassValidationDecoration(String errorClass, String warningClass, String infoClass, String okClass) {
      this.errorClass = errorClass != null ? errorClass : "error";
      this.warningClass = warningClass != null ? warningClass : "warning";
      this.infoClass = infoClass != null ? infoClass : "info";
      this.okClass = okClass != null ? okClass : "ok";
   }

   @Override
   protected Collection<Decoration> createValidationDecorations(ValidationMessage message) {
      String validationClass = this.infoClass;
      switch (message.getSeverity()) {
         case ERROR:
            validationClass = this.errorClass;
            break;
         case WARNING:
            validationClass = this.warningClass;
            break;
         case OK:
            validationClass = this.okClass;
            break;
         default:
            validationClass = this.infoClass;
      }

      return Arrays.asList(new StyleClassDecoration(validationClass));
   }

   @Override
   protected Collection<Decoration> createRequiredDecorations(Control target) {
      return Collections.emptyList();
   }
}
