package org.eclipse.yasson.internal.deserializer.types;

import jakarta.json.bind.JsonbException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.zone.ZoneRulesException;
import java.util.SimpleTimeZone;
import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

class TimeZoneDeserializer extends TypeDeserializer {
   TimeZoneDeserializer(TypeDeserializerBuilder builder) {
      super(builder);
   }

   @Override
   Object deserializeStringValue(String value, DeserializationContextImpl context, Type rType) {
      try {
         ZoneId zoneId = ZoneId.of(value);
         ZonedDateTime zonedDateTime = LocalDateTime.now().atZone(zoneId);
         return new SimpleTimeZone(zonedDateTime.getOffset().getTotalSeconds() * 1000, zoneId.getId());
      } catch (ZoneRulesException var6) {
         throw new JsonbException(Messages.getMessage(MessageKeys.ZONE_PARSE_ERROR, value), var6);
      }
   }
}
