package org.eclipse.yasson.internal.deserializer.types;

import jakarta.json.bind.JsonbException;
import java.time.Instant;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

class OffsetTimeDeserializer extends AbstractDateDeserializer<OffsetTime> {
   OffsetTimeDeserializer(TypeDeserializerBuilder builder) {
      super(builder);
   }

   protected OffsetTime fromInstant(Instant instant) {
      throw new JsonbException(Messages.getMessage(MessageKeys.TIME_TO_EPOCH_MILLIS_ERROR, OffsetTime.class.getSimpleName()));
   }

   protected OffsetTime parseDefault(String jsonValue, Locale locale) {
      return OffsetTime.parse(jsonValue, DateTimeFormatter.ISO_OFFSET_TIME.withLocale(locale));
   }

   protected OffsetTime parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
      return OffsetTime.parse(jsonValue, formatter);
   }
}
