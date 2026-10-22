package org.glassfish.jersey.message.internal;

import jakarta.ws.rs.core.EntityTag;
import java.text.ParseException;
import java.util.Collections;
import java.util.Set;
import org.glassfish.jersey.internal.LocalizationMessages;

public class MatchingEntityTag extends EntityTag {
   public static final Set<MatchingEntityTag> ANY_MATCH = Collections.emptySet();

   public MatchingEntityTag(String value) {
      super(value, false);
   }

   public MatchingEntityTag(String value, boolean weak) {
      super(value, weak);
   }

   public static MatchingEntityTag valueOf(HttpHeaderReader reader) throws ParseException {
      CharSequence tagString = reader.getRemainder();
      HttpHeaderReader.Event e = reader.next(false);
      if (e == HttpHeaderReader.Event.QuotedString) {
         return new MatchingEntityTag(reader.getEventValue().toString());
      } else {
         if (e == HttpHeaderReader.Event.Token) {
            CharSequence ev = reader.getEventValue();
            if (ev != null && ev.length() == 1 && 'W' == ev.charAt(0)) {
               reader.nextSeparator('/');
               return new MatchingEntityTag(reader.nextQuotedString().toString(), true);
            }
         }

         throw new ParseException(LocalizationMessages.ERROR_PARSING_ENTITY_TAG(tagString), reader.getIndex());
      }
   }
}
