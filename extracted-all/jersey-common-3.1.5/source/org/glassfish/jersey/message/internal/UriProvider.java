package org.glassfish.jersey.message.internal;

import jakarta.inject.Singleton;
import java.net.URI;
import java.net.URISyntaxException;
import org.glassfish.jersey.internal.LocalizationMessages;
import org.glassfish.jersey.spi.HeaderDelegateProvider;

@Singleton
public class UriProvider implements HeaderDelegateProvider<URI> {
   @Override
   public boolean supports(Class<?> type) {
      return type == URI.class;
   }

   public String toString(URI header) {
      Utils.throwIllegalArgumentExceptionIfNull(header, LocalizationMessages.URI_IS_NULL());
      return header.toASCIIString();
   }

   public URI fromString(String header) {
      Utils.throwIllegalArgumentExceptionIfNull(header, LocalizationMessages.URI_IS_NULL());

      try {
         return new URI(header);
      } catch (URISyntaxException var3) {
         throw new IllegalArgumentException("Error parsing uri '" + header + "'", var3);
      }
   }
}
