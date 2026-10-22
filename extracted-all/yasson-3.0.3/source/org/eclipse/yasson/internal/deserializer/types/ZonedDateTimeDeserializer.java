package org.eclipse.yasson.internal.deserializer.types;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.logging.Logger;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

class ZonedDateTimeDeserializer extends AbstractDateDeserializer<ZonedDateTime> {
   private static final Logger LOGGER = Logger.getLogger(ZonedDateTimeDeserializer.class.getName());

   ZonedDateTimeDeserializer(TypeDeserializerBuilder builder) {
      super(builder);
   }

   protected ZonedDateTime fromInstant(Instant instant) {
      LOGGER.warning(Messages.getMessage(MessageKeys.OFFSET_DATE_TIME_FROM_MILLIS, ZonedDateTime.class.getSimpleName(), UTC));
      return ZonedDateTime.ofInstant(instant, UTC);
   }

   protected ZonedDateTime parseDefault(String jsonValue, Locale locale) {
      return ZonedDateTime.parse(jsonValue, DateTimeFormatter.ISO_ZONED_DATE_TIME.withLocale(locale));
   }

   protected ZonedDateTime parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
      return ZonedDateTime.parse(jsonValue, this.getZonedFormatter(formatter));
   }
}
