package javafx.util.converter;

import java.time.LocalDate;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import javafx.util.StringConverter;

public class LocalDateStringConverter extends StringConverter<LocalDate> {
   LocalDateTimeStringConverter.LdtConverter<LocalDate> ldtConverter;

   public LocalDateStringConverter() {
      this.ldtConverter = new LocalDateTimeStringConverter.LdtConverter<>(LocalDate.class, null, null, null, null, null, null);
   }

   public LocalDateStringConverter(FormatStyle var1) {
      this.ldtConverter = new LocalDateTimeStringConverter.LdtConverter<>(LocalDate.class, null, null, var1, null, null, null);
   }

   public LocalDateStringConverter(DateTimeFormatter var1, DateTimeFormatter var2) {
      this.ldtConverter = new LocalDateTimeStringConverter.LdtConverter<>(LocalDate.class, var1, var2, null, null, null, null);
   }

   public LocalDateStringConverter(FormatStyle var1, Locale var2, Chronology var3) {
      this.ldtConverter = new LocalDateTimeStringConverter.LdtConverter<>(LocalDate.class, null, null, var1, null, var2, var3);
   }

   public LocalDate fromString(String var1) {
      return this.ldtConverter.fromString(var1);
   }

   public String toString(LocalDate var1) {
      return this.ldtConverter.toString(var1);
   }
}
