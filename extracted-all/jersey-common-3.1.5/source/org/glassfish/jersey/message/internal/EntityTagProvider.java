package org.glassfish.jersey.message.internal;

import jakarta.inject.Singleton;
import jakarta.ws.rs.core.EntityTag;
import java.text.ParseException;
import org.glassfish.jersey.internal.LocalizationMessages;
import org.glassfish.jersey.spi.HeaderDelegateProvider;

@Singleton
public class EntityTagProvider implements HeaderDelegateProvider<EntityTag> {
   @Override
   public boolean supports(Class<?> type) {
      return type == EntityTag.class;
   }

   public String toString(EntityTag header) {
      Utils.throwIllegalArgumentExceptionIfNull(header, LocalizationMessages.ENTITY_TAG_IS_NULL());
      StringBuilder b = new StringBuilder();
      if (header.isWeak()) {
         b.append("W/");
      }

      StringBuilderUtils.appendQuoted(b, header.getValue());
      return b.toString();
   }

   public EntityTag fromString(String header) {
      Utils.throwIllegalArgumentExceptionIfNull(header, LocalizationMessages.ENTITY_TAG_IS_NULL());

      try {
         HttpHeaderReader reader = HttpHeaderReader.newInstance(header);
         HttpHeaderReader.Event e = reader.next(false);
         if (e == HttpHeaderReader.Event.QuotedString) {
            return new EntityTag(reader.getEventValue().toString());
         }

         if (e == HttpHeaderReader.Event.Token) {
            CharSequence ev = reader.getEventValue();
            if (ev != null && ev.length() > 0 && ev.charAt(0) == 'W') {
               reader.nextSeparator('/');
               return new EntityTag(reader.nextQuotedString().toString(), true);
            }
         }
      } catch (ParseException var5) {
         throw new IllegalArgumentException("Error parsing entity tag '" + header + "'", var5);
      }

      throw new IllegalArgumentException("Error parsing entity tag '" + header + "'");
   }
}
