package org.glassfish.jersey.message.internal;

import jakarta.inject.Singleton;
import java.text.ParseException;
import java.util.Locale;
import org.glassfish.jersey.internal.LocalizationMessages;
import org.glassfish.jersey.spi.HeaderDelegateProvider;

@Singleton
public class LocaleProvider implements HeaderDelegateProvider<Locale> {
   @Override
   public boolean supports(Class<?> type) {
      return Locale.class.isAssignableFrom(type);
   }

   public String toString(Locale header) {
      Utils.throwIllegalArgumentExceptionIfNull(header, LocalizationMessages.LOCALE_IS_NULL());
      return header.getCountry().length() == 0 ? header.getLanguage() : header.getLanguage() + '-' + header.getCountry();
   }

   public Locale fromString(String header) {
      Utils.throwIllegalArgumentExceptionIfNull(header, LocalizationMessages.LOCALE_IS_NULL());

      try {
         LanguageTag lt = new LanguageTag(header);
         return lt.getAsLocale();
      } catch (ParseException var3) {
         throw new IllegalArgumentException("Error parsing date '" + header + "'", var3);
      }
   }
}
