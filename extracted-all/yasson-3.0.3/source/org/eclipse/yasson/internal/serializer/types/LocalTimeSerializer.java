package org.eclipse.yasson.internal.serializer.types;

import jakarta.json.bind.JsonbException;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

class LocalTimeSerializer extends AbstractDateSerializer<LocalTime> {
   LocalTimeSerializer(TypeSerializerBuilder serializerBuilder) {
      super(serializerBuilder);
   }

   protected Instant toInstant(LocalTime value) {
      throw new JsonbException(Messages.getMessage(MessageKeys.TIME_TO_EPOCH_MILLIS_ERROR, LocalTime.class.getSimpleName()));
   }

   protected String formatDefault(LocalTime value, Locale locale) {
      return DateTimeFormatter.ISO_LOCAL_TIME.withLocale(locale).format(value);
   }
}
