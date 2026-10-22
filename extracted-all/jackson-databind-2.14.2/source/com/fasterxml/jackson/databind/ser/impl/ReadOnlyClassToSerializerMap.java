package com.fasterxml.jackson.databind.ser.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.util.LRUMap;
import com.fasterxml.jackson.databind.util.TypeKey;

public final class ReadOnlyClassToSerializerMap {
   private final ReadOnlyClassToSerializerMap.Bucket[] _buckets;
   private final int _size;
   private final int _mask;

   public ReadOnlyClassToSerializerMap(LRUMap<TypeKey, JsonSerializer<Object>> src) {
      this._size = findSize(src.size());
      this._mask = this._size - 1;
      ReadOnlyClassToSerializerMap.Bucket[] buckets = new ReadOnlyClassToSerializerMap.Bucket[this._size];
      src.contents((key, value) -> {
         int index = key.hashCode() & this._mask;
         buckets[index] = new ReadOnlyClassToSerializerMap.Bucket(buckets[index], key, value);
      });
      this._buckets = buckets;
   }

   private static final int findSize(int size) {
      int needed = size <= 64 ? size + size : size + (size >> 2);
      int result = 8;

      while (result < needed) {
         result += result;
      }

      return result;
   }

   public static ReadOnlyClassToSerializerMap from(LRUMap<TypeKey, JsonSerializer<Object>> src) {
      return new ReadOnlyClassToSerializerMap(src);
   }

   public int size() {
      return this._size;
   }

   public JsonSerializer<Object> typedValueSerializer(JavaType type) {
      ReadOnlyClassToSerializerMap.Bucket bucket = this._buckets[TypeKey.typedHash(type) & this._mask];
      if (bucket == null) {
         return null;
      } else if (bucket.matchesTyped(type)) {
         return bucket.value;
      } else {
         while ((bucket = bucket.next) != null) {
            if (bucket.matchesTyped(type)) {
               return bucket.value;
            }
         }

         return null;
      }
   }

   public JsonSerializer<Object> typedValueSerializer(Class<?> type) {
      ReadOnlyClassToSerializerMap.Bucket bucket = this._buckets[TypeKey.typedHash(type) & this._mask];
      if (bucket == null) {
         return null;
      } else if (bucket.matchesTyped(type)) {
         return bucket.value;
      } else {
         while ((bucket = bucket.next) != null) {
            if (bucket.matchesTyped(type)) {
               return bucket.value;
            }
         }

         return null;
      }
   }

   public JsonSerializer<Object> untypedValueSerializer(JavaType type) {
      ReadOnlyClassToSerializerMap.Bucket bucket = this._buckets[TypeKey.untypedHash(type) & this._mask];
      if (bucket == null) {
         return null;
      } else if (bucket.matchesUntyped(type)) {
         return bucket.value;
      } else {
         while ((bucket = bucket.next) != null) {
            if (bucket.matchesUntyped(type)) {
               return bucket.value;
            }
         }

         return null;
      }
   }

   public JsonSerializer<Object> untypedValueSerializer(Class<?> type) {
      ReadOnlyClassToSerializerMap.Bucket bucket = this._buckets[TypeKey.untypedHash(type) & this._mask];
      if (bucket == null) {
         return null;
      } else if (bucket.matchesUntyped(type)) {
         return bucket.value;
      } else {
         while ((bucket = bucket.next) != null) {
            if (bucket.matchesUntyped(type)) {
               return bucket.value;
            }
         }

         return null;
      }
   }

   private static final class Bucket {
      public final JsonSerializer<Object> value;
      public final ReadOnlyClassToSerializerMap.Bucket next;
      protected final Class<?> _class;
      protected final JavaType _type;
      protected final boolean _isTyped;

      public Bucket(ReadOnlyClassToSerializerMap.Bucket next, TypeKey key, JsonSerializer<Object> value) {
         this.next = next;
         this.value = value;
         this._isTyped = key.isTyped();
         this._class = key.getRawType();
         this._type = key.getType();
      }

      public boolean matchesTyped(Class<?> key) {
         return this._class == key && this._isTyped;
      }

      public boolean matchesUntyped(Class<?> key) {
         return this._class == key && !this._isTyped;
      }

      public boolean matchesTyped(JavaType key) {
         return this._isTyped && key.equals(this._type);
      }

      public boolean matchesUntyped(JavaType key) {
         return !this._isTyped && key.equals(this._type);
      }
   }
}
