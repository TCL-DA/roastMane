package org.controlsfx.validation.decoration;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javafx.scene.control.Control;
import org.controlsfx.control.decoration.Decoration;
import org.controlsfx.validation.ValidationMessage;

public class CompoundValidationDecoration extends AbstractValidationDecoration {
   private final Set<ValidationDecoration> decorators = new HashSet<>();

   public CompoundValidationDecoration(Collection<ValidationDecoration> decorators) {
      this.decorators.addAll(decorators);
   }

   public CompoundValidationDecoration(ValidationDecoration... decorators) {
      this(Arrays.asList(decorators));
   }

   @Override
   public void applyRequiredDecoration(Control target) {
      this.decorators.stream().forEach(d -> d.applyRequiredDecoration(target));
   }

   @Override
   public void applyValidationDecoration(ValidationMessage message) {
      this.decorators.stream().forEach(d -> d.applyValidationDecoration(message));
   }

   @Override
   protected Collection<Decoration> createValidationDecorations(ValidationMessage message) {
      return Collections.emptyList();
   }

   @Override
   protected Collection<Decoration> createRequiredDecorations(Control target) {
      return Collections.emptyList();
   }
}
