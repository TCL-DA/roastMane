package org.eclipse.yasson.internal;

import jakarta.json.bind.JsonbException;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerationException;
import jakarta.json.stream.JsonGenerator;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;
import org.eclipse.yasson.internal.serializer.ModelSerializer;

public class SerializationContextImpl extends ProcessingContext implements SerializationContext {
   private static final Logger LOGGER = Logger.getLogger(SerializationContextImpl.class.getName());
   private final Set<Object> currentlyProcessedObjects = new HashSet<>();
   private final Type runtimeType;
   private String key = null;
   private boolean containerWithNulls = true;
   private boolean root = true;

   public SerializationContextImpl(JsonbContext jsonbContext, Type rootRuntimeType) {
      super(jsonbContext);
      this.runtimeType = rootRuntimeType;
   }

   public SerializationContextImpl(JsonbContext jsonbContext) {
      this(jsonbContext, null);
   }

   public void setKey(String key) {
      this.key = key;
   }

   public String getKey() {
      return this.key;
   }

   public boolean isRoot() {
      return this.root;
   }

   public void setRoot(boolean root) {
      this.root = root;
   }

   public boolean isContainerWithNulls() {
      return this.containerWithNulls;
   }

   public void setContainerWithNulls(boolean writeNulls) {
      this.containerWithNulls = writeNulls;
   }

   public void marshall(Object object, JsonGenerator jsonGenerator, boolean close) {
      try {
         this.serializeObject(object, jsonGenerator);
      } catch (JsonbException var13) {
         throw var13;
      } catch (RuntimeException var14) {
         throw new JsonbException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, var14.getMessage()), var14);
      } finally {
         try {
            if (close) {
               jsonGenerator.close();
            } else {
               jsonGenerator.flush();
            }
         } catch (JsonGenerationException var12) {
            LOGGER.severe(var12.getMessage());
         }
      }
   }

   public void marshall(Object object, JsonGenerator jsonGenerator) {
      this.marshall(object, jsonGenerator, true);
   }

   public void marshallWithoutClose(Object object, JsonGenerator jsonGenerator) {
      this.marshall(object, jsonGenerator, false);
   }

   public <T> void serialize(String key, T object, JsonGenerator generator) {
      Objects.requireNonNull(key);
      Objects.requireNonNull(object);
      this.setKey(key);
      this.serializeObject(object, generator);
   }

   public <T> void serialize(T object, JsonGenerator generator) {
      Objects.requireNonNull(object);
      this.serializeObject(object, generator);
   }

   public <T> void serializeObject(T root, JsonGenerator generator) {
      Type type = this.determineSerializationType(root);
      ModelSerializer rootSerializer = this.getRootSerializer(type);
      rootSerializer.serialize(root, generator, this);
   }

   private <T> Type determineSerializationType(T root) {
      if (this.isRoot() && this.runtimeType != null) {
         return this.runtimeType;
      } else {
         return root == null ? Object.class : root.getClass();
      }
   }

   public ModelSerializer getRootSerializer(Type type) {
      return this.getJsonbContext().getSerializationModelCreator().serializerChain(type, true, true);
   }

   @Override
   public boolean addProcessedObject(Object object) {
      return this.currentlyProcessedObjects.add(object);
   }

   @Override
   public boolean removeProcessedObject(Object object) {
      return this.currentlyProcessedObjects.remove(object);
   }
}
