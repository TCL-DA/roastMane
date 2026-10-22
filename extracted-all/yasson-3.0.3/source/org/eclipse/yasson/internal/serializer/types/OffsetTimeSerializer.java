package org.eclipse.yasson.internal.serializer.types;

import jakarta.json.bind.JsonbException;
import java.time.Instant;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

class OffsetTimeSerializer extends AbstractDateSerializer<OffsetTime> {
   OffsetTimeSerializer(TypeSerializerBuilder serializerBuilder) {
      super(serializerBuilder);
   }

   protected Instant toInstant(OffsetTime value) {
      throw new JsonbException(Messages.getMessage(MessageKeys.TIME_TO_EPOCH_MILLIS_ERROR, OffsetTime.class.getSimpleName()));
   }

   protected String formatDefault(OffsetTime value, Locale locale) {
      return DateTimeFormatter.ISO_OFFSET_TIME.withLocale(locale).format(value);
   }
}
