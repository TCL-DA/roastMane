package org.controlsfx.validation.decoration;

import javafx.scene.control.Control;
import org.controlsfx.validation.ValidationMessage;

public interface ValidationDecoration {
   void removeDecorations(Control var1);

   void applyValidationDecoration(ValidationMessage var1);

   void applyRequiredDecoration(Control var1);
}
