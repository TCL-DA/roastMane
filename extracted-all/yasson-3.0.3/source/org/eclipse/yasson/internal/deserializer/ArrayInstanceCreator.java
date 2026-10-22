package org.eclipse.yasson.internal.deserializer;

import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonParser;
import java.lang.reflect.Array;
import java.util.Base64;
import java.util.Collection;
import java.util.Map;
import java.util.Base64.Decoder;
import java.util.function.Function;
import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

abstract class ArrayInstanceCreator implements ModelDeserializer<JsonParser> {
   private static final Map<Class<?>, Function<ModelDeserializer<JsonParser>, ArrayInstanceCreator>> CACHE = Map.of(
      boolean[].class,
      x$0 -> new ArrayInstanceCreator.BooleanArrayCreator(x$0),
      byte[].class,
      x$0 -> new ArrayInstanceCreator.ByteArrayCreator(x$0),
      char[].class,
      x$0 -> new ArrayInstanceCreator.CharArrayCreator(x$0),
      double[].class,
      x$0 -> new ArrayInstanceCreator.DoubleArrayCreator(x$0),
      float[].class,
      x$0 -> new ArrayInstanceCreator.FloatArrayCreator(x$0),
      int[].class,
      x$0 -> new ArrayInstanceCreator.IntegerArrayCreator(x$0),
      long[].class,
      x$0 -> new ArrayInstanceCreator.LongArrayCreator(x$0),
      short[].class,
      x$0 -> new ArrayInstanceCreator.ShortArrayCreator(x$0)
   );
   private final ModelDeserializer<JsonParser> delegate;

   private ArrayInstanceCreator(ModelDeserializer<JsonParser> delegate) {
      this.delegate = delegate;
   }

   static ArrayInstanceCreator create(Class<?> arrayType, Class<?> componentClass, ModelDeserializer<JsonParser> delegate) {
      return (ArrayInstanceCreator)(CACHE.containsKey(arrayType)
         ? CACHE.get(arrayType).apply(delegate)
         : new ArrayInstanceCreator.ObjectArrayCreator(delegate, componentClass));
   }

   static ModelDeserializer<JsonParser> createBase64Deserializer(String strategy, ModelDeserializer<JsonParser> delegate) {
      return new ArrayInstanceCreator.Base64ByteArray(strategy, delegate);
   }

   public Object deserialize(JsonParser value, DeserializationContextImpl context) {
      Collection<Object> collection = (Collection<Object>)this.delegate.deserialize(value, context);
      return this.resolveArrayInstance(collection);
   }

   protected abstract Object resolveArrayInstance(Collection<Object> var1);

   private static final class Base64ByteArray implements ModelDeserializer<JsonParser> {
      private final Decoder decoder;
      private final ModelDeserializer<JsonParser> delegate;

      private Base64ByteArray(String strategy, ModelDeserializer<JsonParser> delegate) {
         this.decoder = this.getDecoder(strategy);
         this.delegate = delegate;
      }

      public Decoder getDecoder(String strategy) {
         switch (strategy) {
            case "BASE_64":
               return Base64.getDecoder();
            case "BASE_64_URL":
               return Base64.getUrlDecoder();
            default:
               throw new JsonbException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, "Invalid strategy: " + strategy));
         }
      }

      public Object deserialize(JsonParser value, DeserializationContextImpl context) {
         return this.decoder.decode((String)this.delegate.deserialize(value, context));
      }
   }

   private static final class BooleanArrayCreator extends ArrayInstanceCreator {
      private BooleanArrayCreator(ModelDeserializer<JsonParser> delegate) {
         super(delegate);
      }

      @Override
      protected Object resolveArrayInstance(Collection<Object> collection) {
         boolean[] booleanArray = new boolean[collection.size()];
         int i = 0;

         for (Object obj : collection) {
            booleanArray[i] = (Boolean)obj;
            i++;
         }

         return booleanArray;
      }
   }

   private static final class ByteArrayCreator extends ArrayInstanceCreator {
      private ByteArrayCreator(ModelDeserializer<JsonParser> delegate) {
         super(delegate);
      }

      @Override
      protected Object resolveArrayInstance(Collection<Object> collection) {
         byte[] byteArray = new byte[collection.size()];
         int i = 0;

         for (Object obj : collection) {
            byteArray[i] = (Byte)obj;
            i++;
         }

         return byteArray;
      }
   }

   private static final class CharArrayCreator extends ArrayInstanceCreator {
      private CharArrayCreator(ModelDeserializer<JsonParser> delegate) {
         super(delegate);
      }

      @Override
      protected Object resolveArrayInstance(Collection<Object> collection) {
         char[] charArray = new char[collection.size()];
         int i = 0;

         for (Object obj : collection) {
            charArray[i] = (Character)obj;
            i++;
         }

         return charArray;
      }
   }

   private static final class DoubleArrayCreator extends ArrayInstanceCreator {
      private DoubleArrayCreator(ModelDeserializer<JsonParser> delegate) {
         super(delegate);
      }

      @Override
      protected Object resolveArrayInstance(Collection<Object> collection) {
         double[] doubleArray = new double[collection.size()];
         int i = 0;

         for (Object obj : collection) {
            doubleArray[i] = (Double)obj;
            i++;
         }

         return doubleArray;
      }
   }

   private static final class FloatArrayCreator extends ArrayInstanceCreator {
      private FloatArrayCreator(ModelDeserializer<JsonParser> delegate) {
         super(delegate);
      }

      @Override
      protected Object resolveArrayInstance(Collection<Object> collection) {
         float[] floatArray = new float[collection.size()];
         int i = 0;

         for (Object obj : collection) {
            floatArray[i] = (Float)obj;
            i++;
         }

         return floatArray;
      }
   }

   private static final class IntegerArrayCreator extends ArrayInstanceCreator {
      private IntegerArrayCreator(ModelDeserializer<JsonParser> delegate) {
         super(delegate);
      }

      @Override
      protected Object resolveArrayInstance(Collection<Object> collection) {
         int[] intArray = new int[collection.size()];
         int i = 0;

         for (Object obj : collection) {
            intArray[i] = (Integer)obj;
            i++;
         }

         return intArray;
      }
   }

   private static final class LongArrayCreator extends ArrayInstanceCreator {
      private LongArrayCreator(ModelDeserializer<JsonParser> delegate) {
         super(delegate);
      }

      @Override
      protected Object resolveArrayInstance(Collection<Object> collection) {
         long[] longArray = new long[collection.size()];
         int i = 0;

         for (Object obj : collection) {
            longArray[i] = (Long)obj;
            i++;
         }

         return longArray;
      }
   }

   private static final class ObjectArrayCreator extends ArrayInstanceCreator {
      private final Class<?> componentClass;

      private ObjectArrayCreator(ModelDeserializer<JsonParser> delegate, Class<?> componentClass) {
         super(delegate);
         this.componentClass = componentClass;
      }

      @Override
      protected Object resolveArrayInstance(Collection<Object> collection) {
         Object[] objectArray = (Object[])Array.newInstance(this.componentClass, collection.size());
         int i = 0;

         for (Object obj : collection) {
            objectArray[i] = obj;
            i++;
         }

         return objectArray;
      }
   }

   private static final class ShortArrayCreator extends ArrayInstanceCreator {
      private ShortArrayCreator(ModelDeserializer<JsonParser> delegate) {
         super(delegate);
      }

      @Override
      protected Object resolveArrayInstance(Collection<Object> collection) {
         short[] shortArray = new short[collection.size()];
         int i = 0;

         for (Object obj : collection) {
            shortArray[i] = (Short)obj;
            i++;
         }

         return shortArray;
      }
   }
}
