package org.glassfish.jersey.message.internal;

import jakarta.inject.Singleton;
import org.glassfish.jersey.internal.LocalizationMessages;
import org.glassfish.jersey.spi.HeaderDelegateProvider;

@Singleton
public class StringHeaderProvider implements HeaderDelegateProvider<String> {
   @Override
   public boolean supports(Class<?> type) {
      return type == String.class;
   }

   public String toString(String header) {
      Utils.throwIllegalArgumentExceptionIfNull(header, LocalizationMessages.STRING_IS_NULL());
      return header;
   }

   public String fromString(String header) {
      Utils.throwIllegalArgumentExceptionIfNull(header, LocalizationMessages.STRING_IS_NULL());
      return header;
   }
}
