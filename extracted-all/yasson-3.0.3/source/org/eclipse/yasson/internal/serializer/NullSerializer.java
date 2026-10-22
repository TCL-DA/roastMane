package org.eclipse.yasson.internal.serializer;

import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.stream.JsonGenerator;
import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.SerializationContextImpl;
import org.eclipse.yasson.internal.model.customization.Customization;

public class NullSerializer implements ModelSerializer {
   private final ModelSerializer delegate;
   private final ModelSerializer nullSerializer;
   private final ModelSerializer rootNullSerializer;

   public NullSerializer(ModelSerializer delegate, Customization customization, JsonbContext jsonbContext) {
      this.delegate = delegate;
      if (customization.isNillable()) {
         this.nullSerializer = new NullSerializer.NullWritingEnabled();
      } else {
         this.nullSerializer = new NullSerializer.NullWritingDisabled();
      }

      JsonbSerializer<?> userDefinedNullSerializer = jsonbContext.getConfigProperties().getNullSerializer();
      if (userDefinedNullSerializer != null) {
         this.rootNullSerializer = (value, generator, context) -> userDefinedNullSerializer.serialize(null, generator, context);
      } else {
         this.rootNullSerializer = this.nullSerializer;
      }
   }

   @Override
   public void serialize(Object value, JsonGenerator generator, SerializationContextImpl context) {
      if (value == null) {
         if (context.isRoot()) {
            context.setRoot(false);
            this.rootNullSerializer.serialize(null, generator, context);
         } else {
            this.nullSerializer.serialize(null, generator, context);
         }

         context.setKey(null);
      } else {
         context.setRoot(false);
         this.delegate.serialize(value, generator, context);
      }
   }

   private static class NullWritingDisabled implements ModelSerializer {
      @Override
      public void serialize(Object value, JsonGenerator generator, SerializationContextImpl context) {
         if (context.isContainerWithNulls()) {
            if (context.getKey() == null) {
               generator.writeNull();
            } else {
               generator.writeNull(context.getKey());
            }
         }

         context.setKey(null);
      }
   }

   private static final class NullWritingEnabled implements ModelSerializer {
      @Override
      public void serialize(Object value, JsonGenerator generator, SerializationContextImpl context) {
         if (context.getKey() == null) {
            generator.writeNull();
         } else {
            generator.writeNull(context.getKey());
         }
      }
   }
}
