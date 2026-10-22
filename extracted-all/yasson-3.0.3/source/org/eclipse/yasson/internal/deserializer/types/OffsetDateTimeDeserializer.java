package org.eclipse.yasson.internal.deserializer.types;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.logging.Logger;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

class OffsetDateTimeDeserializer extends AbstractDateDeserializer<OffsetDateTime> {
   private static final Logger LOGGER = Logger.getLogger(OffsetDateTimeDeserializer.class.getName());

   OffsetDateTimeDeserializer(TypeDeserializerBuilder builder) {
      super(builder);
   }

   protected OffsetDateTime fromInstant(Instant instant) {
      LOGGER.warning(Messages.getMessage(MessageKeys.OFFSET_DATE_TIME_FROM_MILLIS, OffsetDateTime.class.getSimpleName(), UTC));
      return OffsetDateTime.ofInstant(instant, UTC);
   }

   protected OffsetDateTime parseDefault(String jsonValue, Locale locale) {
      return OffsetDateTime.parse(jsonValue, DateTimeFormatter.ISO_OFFSET_DATE_TIME.withLocale(locale));
   }

   protected OffsetDateTime parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
      return OffsetDateTime.parse(jsonValue, formatter);
   }
}
