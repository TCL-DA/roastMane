package org.eclipse.yasson.internal.serializer;

import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonGenerator;
import java.util.Base64;
import java.util.Map;
import java.util.Base64.Encoder;
import java.util.function.Function;
import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.SerializationContextImpl;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

abstract class ArraySerializer implements ModelSerializer {
   private static final Map<Class<?>, Function<ModelSerializer, ArraySerializer>> ARRAY_SERIALIZERS = Map.of(
      boolean[].class,
      ArraySerializer.BooleanArraySerializer::new,
      byte[].class,
      ArraySerializer.ByteArraySerializer::new,
      char[].class,
      ArraySerializer.CharacterArraySerializer::new,
      double[].class,
      ArraySerializer.DoubleArraySerializer::new,
      float[].class,
      ArraySerializer.FloatArraySerializer::new,
      int[].class,
      ArraySerializer.IntegerArraySerializer::new,
      long[].class,
      ArraySerializer.LongArraySerializer::new,
      short[].class,
      ArraySerializer.ShortArraySerializer::new
   );
   private final ModelSerializer valueSerializer;

   protected ArraySerializer(ModelSerializer valueSerializer) {
      this.valueSerializer = valueSerializer;
   }

   public static ModelSerializer create(Class<?> arrayType, JsonbContext jsonbContext, ModelSerializer modelSerializer) {
      String binaryDataStrategy = jsonbContext.getConfigProperties().getBinaryDataStrategy();
      if (byte[].class.equals(arrayType) && !binaryDataStrategy.equals("BYTE")) {
         return new ArraySerializer.Base64ByteArraySerializer(binaryDataStrategy);
      } else {
         return (ModelSerializer)(ARRAY_SERIALIZERS.containsKey(arrayType)
            ? ARRAY_SERIALIZERS.get(arrayType).apply(modelSerializer)
            : new ArraySerializer.ObjectArraySerializer(modelSerializer));
      }
   }

   @Override
   public void serialize(Object value, JsonGenerator generator, SerializationContextImpl context) {
      generator.writeStartArray();
      this.serializeArray(value, generator, context);
      generator.writeEnd();
   }

   abstract void serializeArray(Object var1, JsonGenerator var2, SerializationContextImpl var3);

   protected ModelSerializer getValueSerializer() {
      return this.valueSerializer;
   }

   private static final class Base64ByteArraySerializer implements ModelSerializer {
      private final Encoder encoder;

      Base64ByteArraySerializer(String strategy) {
         this.encoder = this.getEncoder(strategy);
      }

      @Override
      public void serialize(Object value, JsonGenerator generator, SerializationContextImpl context) {
         byte[] array = (byte[])value;
         generator.write(this.encoder.encodeToString(array));
      }

      private Encoder getEncoder(String strategy) {
         switch (strategy) {
            case "BASE_64":
               return Base64.getEncoder();
            case "BASE_64_URL":
               return Base64.getUrlEncoder();
            default:
               throw new JsonbException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, "Invalid strategy: " + strategy));
         }
      }
   }

   private static final class BooleanArraySerializer extends ArraySerializer {
      BooleanArraySerializer(ModelSerializer valueSerializer) {
         super(valueSerializer);
      }

      @Override
      public void serializeArray(Object value, JsonGenerator generator, SerializationContextImpl context) {
         boolean[] array = (boolean[])value;

         for (boolean b : array) {
            this.getValueSerializer().serialize(b, generator, context);
         }
      }
   }

   private static final class ByteArraySerializer extends ArraySerializer {
      ByteArraySerializer(ModelSerializer valueSerializer) {
         super(valueSerializer);
      }

      @Override
      public void serializeArray(Object value, JsonGenerator generator, SerializationContextImpl context) {
         byte[] array = (byte[])value;

         for (byte b : array) {
            this.getValueSerializer().serialize(b, generator, context);
         }
      }
   }

   private static final class CharacterArraySerializer extends ArraySerializer {
      CharacterArraySerializer(ModelSerializer valueSerializer) {
         super(valueSerializer);
      }

      @Override
      public void serializeArray(Object value, JsonGenerator generator, SerializationContextImpl context) {
         char[] array = (char[])value;

         for (char c : array) {
            this.getValueSerializer().serialize(c, generator, context);
         }
      }
   }

   private static final class DoubleArraySerializer extends ArraySerializer {
      DoubleArraySerializer(ModelSerializer valueSerializer) {
         super(valueSerializer);
      }

      @Override
      public void serializeArray(Object value, JsonGenerator generator, SerializationContextImpl context) {
         double[] array = (double[])value;

         for (double d : array) {
            this.getValueSerializer().serialize(d, generator, context);
         }
      }
   }

   private static final class FloatArraySerializer extends ArraySerializer {
      FloatArraySerializer(ModelSerializer valueSerializer) {
         super(valueSerializer);
      }

      @Override
      public void serializeArray(Object value, JsonGenerator generator, SerializationContextImpl context) {
         float[] array = (float[])value;

         for (float f : array) {
            this.getValueSerializer().serialize(f, generator, context);
         }
      }
   }

   private static final class IntegerArraySerializer extends ArraySerializer {
      IntegerArraySerializer(ModelSerializer valueSerializer) {
         super(valueSerializer);
      }

      @Override
      public void serializeArray(Object value, JsonGenerator generator, SerializationContextImpl context) {
         int[] array = (int[])value;

         for (int i : array) {
            this.getValueSerializer().serialize(i, generator, context);
         }
      }
   }

   private static final class LongArraySerializer extends ArraySerializer {
      LongArraySerializer(ModelSerializer valueSerializer) {
         super(valueSerializer);
      }

      @Override
      public void serializeArray(Object value, JsonGenerator generator, SerializationContextImpl context) {
         long[] array = (long[])value;

         for (long l : array) {
            this.getValueSerializer().serialize(l, generator, context);
         }
      }
   }

   private static final class ObjectArraySerializer extends ArraySerializer {
      ObjectArraySerializer(ModelSerializer valueSerializer) {
         super(valueSerializer);
      }

      @Override
      public void serializeArray(Object value, JsonGenerator generator, SerializationContextImpl context) {
         Object[] array = (Object[])value;

         for (Object o : array) {
            this.getValueSerializer().serialize(o, generator, context);
         }
      }
   }

   private static final class ShortArraySerializer extends ArraySerializer {
      ShortArraySerializer(ModelSerializer valueSerializer) {
         super(valueSerializer);
      }

      @Override
      public void serializeArray(Object value, JsonGenerator generator, SerializationContextImpl context) {
         short[] array = (short[])value;

         for (short s : array) {
            this.getValueSerializer().serialize(s, generator, context);
         }
      }
   }
}
