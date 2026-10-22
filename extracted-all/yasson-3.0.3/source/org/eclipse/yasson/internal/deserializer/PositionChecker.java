package org.eclipse.yasson.internal.deserializer;

import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParser.Event;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import org.eclipse.yasson.internal.DeserializationContextImpl;

public class PositionChecker implements ModelDeserializer<JsonParser> {
   private static final Map<Event, Event> CLOSING_EVENTS = Map.of(Event.START_ARRAY, Event.END_ARRAY, Event.START_OBJECT, Event.END_OBJECT);
   private final Set<Event> expectedEvents;
   private final ModelDeserializer<JsonParser> delegate;
   private final Type rType;

   public PositionChecker(ModelDeserializer<JsonParser> delegate, Type rType, PositionChecker.Checker checker) {
      this(checker.events, delegate, rType);
   }

   public PositionChecker(ModelDeserializer<JsonParser> delegate, Type rType, Event... events) {
      this(Set.copyOf(Arrays.asList(events)), delegate, rType);
   }

   private PositionChecker(Set<Event> expectedEvents, ModelDeserializer<JsonParser> delegate, Type rType) {
      this.expectedEvents = expectedEvents;
      this.delegate = delegate;
      this.rType = rType;
   }

   public Object deserialize(JsonParser value, DeserializationContextImpl context) {
      Event original = context.getLastValueEvent();
      Event startEvent = original;
      if (!this.expectedEvents.contains(original)) {
         startEvent = value.next();
         context.setLastValueEvent(startEvent);
         if (!this.expectedEvents.contains(startEvent)) {
            throw new JsonbException(
               "Incorrect position for processing type: " + this.rType + ". Received event: " + original + " Allowed: " + this.expectedEvents
            );
         }
      }

      Object o = this.delegate.deserialize(value, context);
      if (CLOSING_EVENTS.containsKey(startEvent) && CLOSING_EVENTS.get(startEvent) != context.getLastValueEvent()) {
         throw new JsonbException(
            "Incorrect parser position after processing of the type: "
               + this.rType
               + ". Start event: "
               + startEvent
               + " After processing event: "
               + context.getLastValueEvent()
         );
      } else {
         return o;
      }
   }

   @Override
   public String toString() {
      return "PositionChecker{expectedEvents=" + this.expectedEvents + ", runtimeType=" + this.rType + "}";
   }

   public static enum Checker {
      VALUES(Event.VALUE_FALSE, Event.VALUE_TRUE, Event.VALUE_STRING, Event.VALUE_NUMBER, Event.VALUE_NULL),
      CONTAINER(Event.START_OBJECT, Event.START_ARRAY);

      private final Set<Event> events;

      private Checker(Event... events) {
         this.events = Set.of(events);
      }

      public Set<Event> getEvents() {
         return this.events;
      }
   }
}
