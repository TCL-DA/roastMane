package org.controlsfx.validation.decoration;

import java.util.Collection;
import java.util.List;
import javafx.scene.control.Control;
import org.controlsfx.control.decoration.Decoration;
import org.controlsfx.control.decoration.Decorator;
import org.controlsfx.validation.ValidationMessage;
import org.controlsfx.validation.ValidationSupport;

public abstract class AbstractValidationDecoration implements ValidationDecoration {
   private static final String VALIDATION_DECORATION = "$org.controlsfx.decoration.vaidation$";

   private static boolean isValidationDecoration(Decoration decoration) {
      return decoration != null && decoration.getProperties().get("$org.controlsfx.decoration.vaidation$") == Boolean.TRUE;
   }

   private static void setValidationDecoration(Decoration decoration) {
      if (decoration != null) {
         decoration.getProperties().put("$org.controlsfx.decoration.vaidation$", Boolean.TRUE);
      }
   }

   protected abstract Collection<Decoration> createValidationDecorations(ValidationMessage var1);

   protected abstract Collection<Decoration> createRequiredDecorations(Control var1);

   @Override
   public void removeDecorations(Control target) {
      List<Decoration> decorations = Decorator.getDecorations(target);
      if (decorations != null) {
         for (Decoration d : (Decoration[])Decorator.getDecorations(target).toArray(new Decoration[0])) {
            if (isValidationDecoration(d)) {
               Decorator.removeDecoration(target, d);
            }
         }
      }
   }

   @Override
   public void applyValidationDecoration(ValidationMessage message) {
      this.createValidationDecorations(message).stream().forEach(d -> this.decorate(message.getTarget(), d));
   }

   @Override
   public void applyRequiredDecoration(Control target) {
      if (ValidationSupport.isRequired(target)) {
         this.createRequiredDecorations(target).stream().forEach(d -> this.decorate(target, d));
      }
   }

   private void decorate(Control target, Decoration d) {
      setValidationDecoration(d);
      Decorator.addDecoration(target, d);
   }
}
