package org.eclipse.yasson.internal;

import java.util.Objects;

public class JsonbNumberFormatter {
   private final String format;
   private final String locale;

   public JsonbNumberFormatter(String format, String locale) {
      this.format = format;
      this.locale = locale;
   }

   public String getFormat() {
      return this.format;
   }

   public String getLocale() {
      return this.locale;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         JsonbNumberFormatter that = (JsonbNumberFormatter)o;
         return Objects.equals(this.format, that.format) && Objects.equals(this.locale, that.locale);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.format, this.locale);
   }

   @Override
   public String toString() {
      return "JsonbNumberFormatter{format='" + this.format + "', locale='" + this.locale + "'}";
   }
}
