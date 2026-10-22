package javafx.util.converter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import javafx.util.StringConverter;

public class LocalTimeStringConverter extends StringConverter<LocalTime> {
   LocalDateTimeStringConverter.LdtConverter<LocalTime> ldtConverter;

   public LocalTimeStringConverter() {
      this.ldtConverter = new LocalDateTimeStringConverter.LdtConverter<>(LocalTime.class, null, null, null, null, null, null);
   }

   public LocalTimeStringConverter(FormatStyle var1) {
      this.ldtConverter = new LocalDateTimeStringConverter.LdtConverter<>(LocalTime.class, null, null, null, var1, null, null);
   }

   public LocalTimeStringConverter(FormatStyle var1, Locale var2) {
      this.ldtConverter = new LocalDateTimeStringConverter.LdtConverter<>(LocalTime.class, null, null, null, var1, var2, null);
   }

   public LocalTimeStringConverter(DateTimeFormatter var1, DateTimeFormatter var2) {
      this.ldtConverter = new LocalDateTimeStringConverter.LdtConverter<>(LocalTime.class, var1, var2, null, null, null, null);
   }

   public LocalTime fromString(String var1) {
      return this.ldtConverter.fromString(var1);
   }

   public String toString(LocalTime var1) {
      return this.ldtConverter.toString(var1);
   }
}
