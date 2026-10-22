package org.eclipse.yasson.internal;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;
import java.util.Objects;

public class JsonbDateFormatter {
   private static final JsonbDateFormatter DEFAULT = new JsonbDateFormatter("##default", Locale.getDefault().toLanguageTag());
   public static final DateTimeFormatter IJSON_DATE_FORMATTER = new DateTimeFormatterBuilder()
      .parseCaseInsensitive()
      .append(DateTimeFormatter.ISO_LOCAL_DATE)
      .appendLiteral('T')
      .appendValue(ChronoField.HOUR_OF_DAY, 2)
      .appendLiteral(':')
      .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
      .appendLiteral(':')
      .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
      .appendLiteral('Z')
      .appendOffset("+HH:MM", "+00:00")
      .toFormatter();
   private final DateTimeFormatter dateTimeFormatter;
   private final String format;
   private final String locale;

   public JsonbDateFormatter(DateTimeFormatter dateTimeFormatter, String format, String locale) {
      this.dateTimeFormatter = dateTimeFormatter;
      this.format = format;
      this.locale = locale;
   }

   public JsonbDateFormatter(String format, String locale) {
      this.format = format;
      this.locale = locale;
      this.dateTimeFormatter = null;
   }

   public DateTimeFormatter getDateTimeFormatter() {
      return this.dateTimeFormatter;
   }

   public String getFormat() {
      return this.format;
   }

   public String getLocale() {
      return this.locale;
   }

   public static JsonbDateFormatter getDefault() {
      return DEFAULT;
   }

   public boolean isDefault() {
      return "##default".equals(this.format);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         JsonbDateFormatter that = (JsonbDateFormatter)o;
         return Objects.equals(this.format, that.format)
            && Objects.equals(this.locale, that.locale)
            && Objects.equals(this.dateTimeFormatter, that.dateTimeFormatter);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.dateTimeFormatter, this.format, this.locale);
   }

   @Override
   public String toString() {
      return "JsonbDateFormatter{dateTimeFormatter=" + this.dateTimeFormatter + ", format='" + this.format + "', locale='" + this.locale + "'}";
   }
}
