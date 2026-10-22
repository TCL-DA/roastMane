package javafx.util.converter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateStringConverter extends DateTimeStringConverter {
   public DateStringConverter() {
      this(null, null, null, 2);
   }

   public DateStringConverter(int var1) {
      this(null, null, null, var1);
   }

   public DateStringConverter(Locale var1) {
      this(var1, null, null, 2);
   }

   public DateStringConverter(Locale var1, int var2) {
      this(var1, null, null, var2);
   }

   public DateStringConverter(String var1) {
      this(null, var1, null, 2);
   }

   public DateStringConverter(Locale var1, String var2) {
      this(var1, var2, null, 2);
   }

   public DateStringConverter(DateFormat var1) {
      this(null, null, var1, 2);
   }

   private DateStringConverter(Locale var1, String var2, DateFormat var3, int var4) {
      super(var1, var2, var3, var4, 2);
   }

   @Override
   protected DateFormat getDateFormat() {
      Object var1 = null;
      if (this.dateFormat != null) {
         return this.dateFormat;
      } else {
         if (this.pattern != null) {
            var1 = new SimpleDateFormat(this.pattern, this.locale);
         } else {
            var1 = DateFormat.getDateInstance(this.dateStyle, this.locale);
         }

         var1.setLenient(false);
         return (DateFormat)var1;
      }
   }
}
