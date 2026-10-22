package org.glassfish.jersey.message.internal;

import jakarta.inject.Singleton;
import jakarta.ws.rs.core.MediaType;
import java.text.ParseException;
import java.util.Map;
import java.util.Map.Entry;
import org.glassfish.jersey.internal.LocalizationMessages;
import org.glassfish.jersey.spi.HeaderDelegateProvider;

@Singleton
public class MediaTypeProvider implements HeaderDelegateProvider<MediaType> {
   private static final String MEDIA_TYPE_IS_NULL = LocalizationMessages.MEDIA_TYPE_IS_NULL();

   @Override
   public boolean supports(Class<?> type) {
      return MediaType.class.isAssignableFrom(type);
   }

   public String toString(MediaType header) {
      Utils.throwIllegalArgumentExceptionIfNull(header, MEDIA_TYPE_IS_NULL);
      StringBuilder b = new StringBuilder();
      b.append(header.getType()).append('/').append(header.getSubtype());

      for (Entry<String, String> e : header.getParameters().entrySet()) {
         b.append(";").append(e.getKey()).append('=');
         StringBuilderUtils.appendQuotedIfNonToken(b, e.getValue());
      }

      return b.toString();
   }

   public MediaType fromString(String header) {
      Utils.throwIllegalArgumentExceptionIfNull(header, MEDIA_TYPE_IS_NULL);

      try {
         return valueOf(HttpHeaderReader.newInstance(header));
      } catch (ParseException var3) {
         throw new IllegalArgumentException("Error parsing media type '" + header + "'", var3);
      }
   }

   public static MediaType valueOf(HttpHeaderReader reader) throws ParseException {
      reader.hasNext();
      String type = reader.nextToken().toString();
      reader.nextSeparator('/');
      String subType = reader.nextToken().toString();
      Map<String, String> params = null;
      if (reader.hasNext()) {
         params = HttpHeaderReader.readParameters(reader);
      }

      return new MediaType(type, subType, params);
   }
}
