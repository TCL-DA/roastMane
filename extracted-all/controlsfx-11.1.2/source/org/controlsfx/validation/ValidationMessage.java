package org.controlsfx.validation;

import java.util.Comparator;
import javafx.scene.control.Control;

public interface ValidationMessage extends Comparable<ValidationMessage> {
   Comparator<ValidationMessage> COMPARATOR = new Comparator<ValidationMessage>() {
      public int compare(ValidationMessage vm1, ValidationMessage vm2) {
         if (vm1 == vm2) {
            return 0;
         } else if (vm1 == null) {
            return 1;
         } else {
            return vm2 == null ? -1 : vm1.compareTo(vm2);
         }
      }
   };

   String getText();

   Severity getSeverity();

   Control getTarget();

   static ValidationMessage error(Control target, String text) {
      return new SimpleValidationMessage(target, text, Severity.ERROR);
   }

   static ValidationMessage warning(Control target, String text) {
      return new SimpleValidationMessage(target, text, Severity.WARNING);
   }

   static ValidationMessage info(Control target, String text) {
      return new SimpleValidationMessage(target, text, Severity.INFO);
   }

   static ValidationMessage ok(Control target, String text) {
      return new SimpleValidationMessage(target, text, Severity.OK);
   }

   static ValidationMessage ok(Control target) {
      return new SimpleValidationMessage(target, "", Severity.OK);
   }

   default int compareTo(ValidationMessage msg) {
      return msg != null && this.getTarget() == msg.getTarget() ? this.getSeverity().compareTo(msg.getSeverity()) : -1;
   }
}
