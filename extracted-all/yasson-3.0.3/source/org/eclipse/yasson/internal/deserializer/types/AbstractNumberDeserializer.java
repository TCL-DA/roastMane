package org.eclipse.yasson.internal.deserializer.types;

import jakarta.json.bind.JsonbException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.function.Function;
import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.JsonbNumberFormatter;
import org.eclipse.yasson.internal.deserializer.ModelDeserializer;
import org.eclipse.yasson.internal.model.customization.Customization;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

abstract class AbstractNumberDeserializer<T extends Number> extends TypeDeserializer {
   private final ModelDeserializer<String> actualDeserializer;
   private final boolean integerOnly;

   AbstractNumberDeserializer(TypeDeserializerBuilder builder, boolean integerOnly) {
      super(builder);
      this.actualDeserializer = this.actualDeserializer(builder);
      this.integerOnly = integerOnly;
   }

   private ModelDeserializer<String> actualDeserializer(TypeDeserializerBuilder builder) {
      Customization customization = builder.getCustomization();
      if (customization.getDeserializeNumberFormatter() == null) {
         return (value, context) -> {
            try {
               return this.parseNumberValue(value);
            } catch (NumberFormatException var4x) {
               throw new JsonbException(Messages.getMessage(MessageKeys.DESERIALIZE_VALUE_ERROR, this.getType()), var4x);
            }
         };
      } else {
         JsonbNumberFormatter numberFormat = customization.getDeserializeNumberFormatter();
         Locale locale = builder.getConfigProperties().getLocale(numberFormat.getLocale());
         NumberFormat format = NumberFormat.getInstance(locale);
         ((DecimalFormat)format).applyPattern(numberFormat.getFormat());
         format.setParseIntegerOnly(this.integerOnly);
         Function<String, String> valueChanger = this.createCompatibilityValueChanger(locale);
         return (value, context) -> {
            try {
               String updated = valueChanger.apply(value);
               return this.parseNumberValue(String.valueOf(format.parse(updated)));
            } catch (ParseException var7) {
               throw new JsonbException(Messages.getMessage(MessageKeys.PARSING_NUMBER, value, numberFormat.getFormat()), var7);
            }
         };
      }
   }

   private Function<String, String> createCompatibilityValueChanger(Locale locale) {
      char beforeJdk13GroupSeparator = 160;
      char frenchGroupingSeparator = DecimalFormatSymbols.getInstance(Locale.FRENCH).getGroupingSeparator();
      return locale.getLanguage().equals(Locale.FRENCH.getLanguage()) && beforeJdk13GroupSeparator != frenchGroupingSeparator
         ? value -> value.replace(beforeJdk13GroupSeparator, frenchGroupingSeparator)
         : value -> value;
   }

   abstract T parseNumberValue(String var1);

   @Override
   Object deserializeStringValue(String value, DeserializationContextImpl context, Type rType) {
      return this.actualDeserializer.deserialize(value, context);
   }
}
