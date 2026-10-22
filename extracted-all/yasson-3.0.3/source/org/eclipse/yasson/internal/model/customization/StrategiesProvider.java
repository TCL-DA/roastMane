package org.eclipse.yasson.internal.model.customization;

import jakarta.json.bind.JsonbException;
import jakarta.json.bind.config.PropertyNamingStrategy;
import java.nio.CharBuffer;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import org.eclipse.yasson.internal.model.PropertyModel;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

public final class StrategiesProvider {
   public static final PropertyNamingStrategy CASE_INSENSITIVE_STRATEGY = Objects::requireNonNull;

   private StrategiesProvider() {
   }

   public static Consumer<List<PropertyModel>> getOrderingFunction(String strategy) {
      switch (strategy) {
         case "LEXICOGRAPHICAL":
            return props -> props.sort(Comparator.comparing(PropertyModel::getWriteName));
         case "ANY":
            return props -> {
            };
         case "REVERSE":
            return props -> props.sort(Comparator.comparing(PropertyModel::getWriteName).reversed());
         default:
            throw new JsonbException(Messages.getMessage(MessageKeys.PROPERTY_ORDER, strategy));
      }
   }

   public static PropertyNamingStrategy getPropertyNamingStrategy(String strategy) {
      switch (strategy) {
         case "LOWER_CASE_WITH_UNDERSCORES":
            return createLowerCaseStrategyWithSeparator('_');
         case "LOWER_CASE_WITH_DASHES":
            return createLowerCaseStrategyWithSeparator('-');
         case "UPPER_CAMEL_CASE":
            return createUpperCamelCaseStrategy();
         case "UPPER_CAMEL_CASE_WITH_SPACES":
            return createUpperCamelCaseWithSpaceStrategy();
         case "IDENTITY":
            return Objects::requireNonNull;
         case "CASE_INSENSITIVE":
            return CASE_INSENSITIVE_STRATEGY;
         default:
            throw new JsonbException("No property naming strategy was found for: " + strategy);
      }
   }

   private static PropertyNamingStrategy createUpperCamelCaseStrategy() {
      return propertyName -> {
         Objects.requireNonNull(propertyName);
         char first = Character.toUpperCase(propertyName.charAt(0));
         return first + propertyName.substring(1);
      };
   }

   private static PropertyNamingStrategy createUpperCamelCaseWithSpaceStrategy() {
      return propertyName -> {
         String upperCased = createUpperCamelCaseStrategy().translateName(propertyName);
         CharBuffer buffer = CharBuffer.allocate(upperCased.length() * 2);
         char last = 0;

         for (int i = 0; i < upperCased.length(); i++) {
            char current = upperCased.charAt(i);
            if (i > 0 && Character.isUpperCase(current) && isLowerCaseCharacter(last)) {
               buffer.append(' ');
            }

            last = current;
            buffer.append(current);
         }

         return new String(buffer.array(), 0, buffer.position());
      };
   }

   private static PropertyNamingStrategy createLowerCaseStrategyWithSeparator(char separator) {
      return propertyName -> {
         Objects.requireNonNull(propertyName);
         CharBuffer charBuffer = CharBuffer.allocate(propertyName.length() * 2);
         char last = 0;

         for (int i = 0; i < propertyName.length(); i++) {
            char current = propertyName.charAt(i);
            if (i > 0 && Character.isUpperCase(current) && isLowerCaseCharacter(last)) {
               charBuffer.append(separator);
            }

            last = current;
            charBuffer.append(Character.toLowerCase(current));
         }

         return new String(charBuffer.array(), 0, charBuffer.position());
      };
   }

   private static boolean isLowerCaseCharacter(char character) {
      return Character.isAlphabetic(character) && Character.isLowerCase(character);
   }
}
