package org.eclipse.jetty.util;

import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Index<V> {
   V get(String var1);

   V get(ByteBuffer var1);

   V get(String var1, int var2, int var3);

   V get(ByteBuffer var1, int var2, int var3);

   default boolean contains(String s) {
      return this.get(s) != null;
   }

   V getBest(String var1, int var2, int var3);

   V getBest(String var1);

   V getBest(ByteBuffer var1, int var2, int var3);

   default V getBest(ByteBuffer b) {
      return this.getBest(b, 0, b.remaining());
   }

   V getBest(byte[] var1, int var2, int var3);

   default V getBest(byte[] b) {
      return this.getBest(b, 0, b.length);
   }

   boolean isEmpty();

   int size();

   Set<String> keySet();

   static <V> Index.Mutable<V> buildMutableVisibleAsciiAlphabet(boolean caseSensitive, int maxCapacity) {
      if (maxCapacity < 0 || maxCapacity > ArrayTrie.MAX_CAPACITY) {
         return new TreeTrie<>(caseSensitive);
      } else {
         return (Index.Mutable<V>)(maxCapacity == 0 ? EmptyTrie.instance(caseSensitive) : new ArrayTrie<>(caseSensitive, maxCapacity));
      }
   }

   static <V> Index<V> empty(boolean caseSensitive) {
      return EmptyTrie.instance(caseSensitive);
   }

   public static class Builder<V> {
      Map<String, V> contents;
      boolean caseSensitive;

      public Builder() {
         this.caseSensitive = false;
         this.contents = null;
      }

      Builder(boolean caseSensitive, Map<String, V> contents) {
         this.caseSensitive = caseSensitive;
         this.contents = contents;
      }

      private Map<String, V> contents() {
         if (this.contents == null) {
            this.contents = new LinkedHashMap<>();
         }

         return this.contents;
      }

      public Index.Builder<V> caseSensitive(boolean caseSensitive) {
         this.caseSensitive = caseSensitive;
         return this;
      }

      public Index.Builder<V> withAll(V[] values, Function<V, String> keyFunction) {
         for (V value : values) {
            String key = keyFunction.apply(value);
            this.contents().put(key, value);
         }

         return this;
      }

      public Index.Builder<V> withAll(Supplier<Map<String, V>> entriesSupplier) {
         Map<String, V> map = entriesSupplier.get();
         this.contents().putAll(map);
         return this;
      }

      public Index.Builder<V> with(V value) {
         this.contents().put(value.toString(), value);
         return this;
      }

      public Index.Builder<V> with(String key, V value) {
         this.contents().put(key, value);
         return this;
      }

      public Index.Mutable.Builder<V> mutable() {
         return new Index.Mutable.Builder<>(this.caseSensitive, this.contents);
      }

      public Index<V> build() {
         if (this.contents == null) {
            return EmptyTrie.instance(this.caseSensitive);
         } else {
            int capacity = AbstractTrie.requiredCapacity(this.contents.keySet(), this.caseSensitive);
            AbstractTrie<V> trie = ArrayTrie.from(capacity, this.caseSensitive, this.contents);
            if (trie != null) {
               return trie;
            } else {
               trie = TreeTrie.from(this.caseSensitive, this.contents);
               if (trie != null) {
                  return trie;
               } else {
                  throw new IllegalStateException("No suitable Trie implementation : " + this);
               }
            }
         }
      }

      @Override
      public String toString() {
         return String.format("%s{c=%d,cs=%b}", super.toString(), this.contents == null ? 0 : this.contents.size(), this.caseSensitive);
      }
   }

   public interface Mutable<V> extends Index<V> {
      boolean put(String var1, V var2);

      boolean put(V var1);

      V remove(String var1);

      void clear();

      public static class Builder<V> extends Index.Builder<V> {
         private int maxCapacity = -1;

         Builder(boolean caseSensitive, Map<String, V> contents) {
            super(caseSensitive, contents);
         }

         public Index.Mutable.Builder<V> maxCapacity(int capacity) {
            this.maxCapacity = capacity;
            return this;
         }

         @Override
         public Index.Mutable.Builder<V> mutable() {
            return this;
         }

         public Index.Mutable<V> build() {
            if (this.maxCapacity == 0) {
               return EmptyTrie.instance(this.caseSensitive);
            } else {
               int capacity = this.contents == null ? 0 : AbstractTrie.requiredCapacity(this.contents.keySet(), this.caseSensitive);
               if (this.maxCapacity >= 0 && capacity > this.maxCapacity) {
                  throw new IllegalStateException("Insufficient maxCapacity for contents");
               } else {
                  AbstractTrie<V> trie = ArrayTrie.from(this.maxCapacity, this.caseSensitive, this.contents);
                  if (trie != null) {
                     return trie;
                  } else {
                     trie = TreeTrie.from(this.caseSensitive, this.contents);
                     if (trie != null) {
                        return trie;
                     } else {
                        throw new IllegalStateException("No suitable Trie implementation: " + this);
                     }
                  }
               }
            }
         }
      }
   }
}
