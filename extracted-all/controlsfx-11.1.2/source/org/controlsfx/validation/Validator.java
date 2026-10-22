package org.controlsfx.validation;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.scene.control.Control;

public interface Validator<T> extends BiFunction<Control, T, ValidationResult> {
   @SafeVarargs
   static <T> Validator<T> combine(Validator<T>... validators) {
      return (control, value) -> Stream.of(validators)
            .map(validator -> validator.apply(control, (T)value))
            .collect(Collectors.reducing(new ValidationResult(), ValidationResult::combine));
   }

   static <T> Validator<T> createEmptyValidator(String message, Severity severity) {
      return (c, value) -> {
         boolean condition = value instanceof String ? value.toString().trim().isEmpty() : value == null;
         return ValidationResult.fromMessageIf(c, message, severity, condition);
      };
   }

   static <T> Validator<T> createEmptyValidator(String message) {
      return createEmptyValidator(message, Severity.ERROR);
   }

   static <T> Validator<T> createEqualsValidator(String message, Severity severity, Collection<T> values) {
      return (c, value) -> ValidationResult.fromMessageIf(c, message, severity, !values.contains(value));
   }

   static <T> Validator<T> createEqualsValidator(String message, Collection<T> values) {
      return createEqualsValidator(message, Severity.ERROR, values);
   }

   static <T> Validator<T> createPredicateValidator(Predicate<T> predicate, String message) {
      return createPredicateValidator(predicate, message, Severity.ERROR);
   }

   static <T> Validator<T> createPredicateValidator(Predicate<T> predicate, String message, Severity severity) {
      return (control, value) -> ValidationResult.fromMessageIf(control, message, severity, !predicate.test((T)value));
   }

   static Validator<String> createRegexValidator(String message, String regex, Severity severity) {
      return (c, value) -> {
         boolean condition = value == null ? true : !Pattern.matches(regex, value);
         return ValidationResult.fromMessageIf(c, message, severity, condition);
      };
   }

   static Validator<String> createRegexValidator(String message, Pattern regex, Severity severity) {
      return (c, value) -> {
         boolean condition = value == null ? true : !regex.matcher(value).matches();
         return ValidationResult.fromMessageIf(c, message, severity, condition);
      };
   }
}
