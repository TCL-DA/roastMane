package org.eclipse.yasson.internal;

import jakarta.json.bind.JsonbException;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParser.Event;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.yasson.internal.deserializer.ModelDeserializer;
import org.eclipse.yasson.internal.model.customization.ClassCustomization;
import org.eclipse.yasson.internal.model.customization.Customization;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

public class DeserializationContextImpl extends ProcessingContext implements DeserializationContext {
   private final List<Runnable> delayedSetters = new ArrayList<>();
   private Event lastValueEvent;
   private Customization customization = ClassCustomization.empty();
   private Object instance;

   public DeserializationContextImpl(JsonbContext jsonbContext) {
      super(jsonbContext);
   }

   public DeserializationContextImpl(DeserializationContextImpl context) {
      super(context.getJsonbContext());
      this.lastValueEvent = context.lastValueEvent;
   }

   public Object getInstance() {
      return this.instance;
   }

   public void setInstance(Object instance) {
      this.instance = instance;
   }

   public List<Runnable> getDeferredDeserializers() {
      return this.delayedSetters;
   }

   public Event getLastValueEvent() {
      return this.lastValueEvent;
   }

   public void setLastValueEvent(Event lastValueEvent) {
      this.lastValueEvent = lastValueEvent;
   }

   public Customization getCustomization() {
      return this.customization;
   }

   public void setCustomization(Customization customization) {
      this.customization = customization;
   }

   public <T> T deserialize(Class<T> clazz, JsonParser parser) {
      return this.deserializeItem(clazz, parser);
   }

   public <T> T deserialize(Type type, JsonParser parser) {
      return this.deserializeItem(type, parser);
   }

   private <T> T deserializeItem(Type type, JsonParser parser) {
      try {
         if (this.lastValueEvent == null) {
            this.lastValueEvent = parser.next();
            this.checkState();
         }

         ModelDeserializer<JsonParser> modelDeserializer = this.getJsonbContext().getChainModelCreator().deserializerChain(type);
         return (T)modelDeserializer.deserialize(parser, this);
      } catch (JsonbException var4) {
         throw var4;
      } catch (RuntimeException var5) {
         throw new JsonbException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, var5.getMessage()), var5);
      }
   }

   private void checkState() {
      if (this.lastValueEvent == Event.KEY_NAME) {
         throw new JsonbException("JsonParser has incorrect position as the first event: KEY_NAME");
      }
   }
}
