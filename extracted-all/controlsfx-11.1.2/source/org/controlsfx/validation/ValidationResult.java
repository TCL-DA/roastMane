package org.controlsfx.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.control.Control;

public class ValidationResult {
   private List<ValidationMessage> invalidMessages = new ArrayList<>();

   public static final ValidationResult fromMessageIf(Control target, String text, Severity severity, boolean condition) {
      return new ValidationResult().addMessageIf(target, text, severity, condition);
   }

   public static final ValidationResult fromErrorIf(Control target, String text, boolean condition) {
      return new ValidationResult().addErrorIf(target, text, condition);
   }

   public static final ValidationResult fromWarningIf(Control target, String text, boolean condition) {
      return new ValidationResult().addWarningIf(target, text, condition);
   }

   public static final ValidationResult fromInfoIf(Control target, String text, boolean condition) {
      return new ValidationResult().addInfoIf(target, text, condition);
   }

   public static final ValidationResult fromError(Control target, String text) {
      return fromMessages(ValidationMessage.error(target, text));
   }

   public static final ValidationResult fromWarning(Control target, String text) {
      return fromMessages(ValidationMessage.warning(target, text));
   }

   public static final ValidationResult fromInfo(Control target, String text) {
      return fromMessages(ValidationMessage.info(target, text));
   }

   public static final ValidationResult fromMessages(ValidationMessage... messages) {
      return new ValidationResult().addAll(messages);
   }

   public static final ValidationResult fromMessages(Collection<? extends ValidationMessage> messages) {
      return new ValidationResult().addAll(messages);
   }

   public static final ValidationResult fromResults(ValidationResult... results) {
      return new ValidationResult().combineAll(results);
   }

   public static final ValidationResult fromResults(Collection<ValidationResult> results) {
      return new ValidationResult().combineAll(results);
   }

   public ValidationResult copy() {
      return fromMessages(this.getMessages());
   }

   public ValidationResult add(ValidationMessage message) {
      if (message != null && message.getSeverity() != Severity.OK) {
         this.invalidMessages.add(message);
      }

      return this;
   }

   public ValidationResult addMessageIf(Control target, String text, Severity severity, boolean condition) {
      return condition ? this.add(new SimpleValidationMessage(target, text, severity)) : this;
   }

   public ValidationResult addErrorIf(Control target, String text, boolean condition) {
      return this.addMessageIf(target, text, Severity.ERROR, condition);
   }

   public ValidationResult addWarningIf(Control target, String text, boolean condition) {
      return this.addMessageIf(target, text, Severity.WARNING, condition);
   }

   public ValidationResult addInfoIf(Control target, String text, boolean condition) {
      return this.addMessageIf(target, text, Severity.INFO, condition);
   }

   public ValidationResult addAll(Collection<? extends ValidationMessage> messages) {
      messages.stream().forEach(msg -> this.add(msg));
      return this;
   }

   public ValidationResult addAll(ValidationMessage... messages) {
      return this.addAll(Arrays.asList(messages));
   }

   public ValidationResult combine(ValidationResult validationResult) {
      return validationResult == null ? this.copy() : this.copy().addAll(validationResult.getMessages());
   }

   public ValidationResult combineAll(Collection<ValidationResult> validationResults) {
      return validationResults.stream().reduce(this.copy(), (x, r) -> r == null ? x : x.addAll(r.getMessages()));
   }

   public ValidationResult combineAll(ValidationResult... validationResults) {
      return this.combineAll(Arrays.asList(validationResults));
   }

   public Collection<ValidationMessage> getErrors() {
      return this.getMessages(Severity.ERROR);
   }

   public Collection<ValidationMessage> getWarnings() {
      return this.getMessages(Severity.WARNING);
   }

   public Collection<ValidationMessage> getInfos() {
      return this.getMessages(Severity.INFO);
   }

   public Collection<ValidationMessage> getMessages() {
      return this.getMessages(null);
   }

   private Collection<ValidationMessage> getMessages(Severity severity) {
      List<ValidationMessage> messages = severity == null
         ? this.invalidMessages
         : this.invalidMessages.parallelStream().filter(msg -> msg.getSeverity() == severity).collect(Collectors.toList());
      return Collections.unmodifiableList(messages);
   }
}
