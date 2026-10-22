package org.glassfish.jersey.message.internal;

import jakarta.inject.Singleton;
import java.text.ParseException;
import java.util.Date;
import org.glassfish.jersey.internal.LocalizationMessages;
import org.glassfish.jersey.spi.HeaderDelegateProvider;

@Singleton
public class DateProvider implements HeaderDelegateProvider<Date> {
   @Override
   public boolean supports(Class<?> type) {
      return Date.class.isAssignableFrom(type);
   }

   public String toString(Date header) {
      Utils.throwIllegalArgumentExceptionIfNull(header, LocalizationMessages.DATE_IS_NULL());
      return HttpDateFormat.getPreferredDateFormat().format(header);
   }

   public Date fromString(String header) {
      Utils.throwIllegalArgumentExceptionIfNull(header, LocalizationMessages.DATE_IS_NULL());

      try {
         return HttpHeaderReader.readDate(header);
      } catch (ParseException var3) {
         throw new IllegalArgumentException("Error parsing date '" + header + "'", var3);
      }
   }
}
