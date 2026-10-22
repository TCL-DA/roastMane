package kotlin.collections

import java.util.Map.Entry
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.markers.KMappedMarker
import org.jetbrains.annotations.NotNull

@SinceKotlin(version = "1.1")
@SourceDebugExtension(["SMAP\nAbstractMap.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AbstractMap.kt\nkotlin/collections/AbstractMap\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,153:1\n1747#2,3:154\n1726#2,3:157\n288#2,2:160\n*S KotlinDebug\n*F\n+ 1 AbstractMap.kt\nkotlin/collections/AbstractMap\n*L\n28#1:154,3\n60#1:157,3\n141#1:160,2\n*E\n"])
abstract class AbstractMap<K, V> : java.util.Map<K, V>, KMappedMarker {
   @JvmStatic
   public AbstractMap.Companion Companion = new AbstractMap.Companion(null);

   private final var _keys: Set<Any>?
   private final var _values: Collection<Any>?
      private set
   public open val keys: Set<Any>
      public open get() {
         if (this._keys == null) {
            this._keys = new AbstractSet<K>(this) {
               {
                  this.this$0 = `$receiver`;
               }

               @Override
               public boolean contains(Object element) {
                  return this.this$0.containsKey(element);
               }

               @NotNull
               @Override
               public java.util.Iterator<K> iterator() {
                  var entryIterator: java.util.Iterator = this.this$0.entrySet().iterator();
                  return new java.util.Iterator<K>(entryIterator) {
                     {
                        this.$entryIterator = `$entryIterator`;
                     }

                     @Override
                     public boolean hasNext() {
                        return this.$entryIterator.hasNext();
                     }

                     @Override
                     public K next() {
                        return this.$entryIterator.next().getKey();
                     }

                     @Override
                     public void remove() {
                        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
                     }
                  };
               }

               @Override
               public int getSize() {
                  return this.this$0.size();
               }
            };
         }

         var var10000: java.util.Set = this._keys;
         return var10000;
      }

   public open val size: Int
      public open get() {
         return this.entrySet().size();
      }

   public open val values: Collection<Any>
      public open get() {
         if (this._values == null) {
            this._values = new AbstractCollection<V>(this) {
               {
                  this.this$0 = `$receiver`;
               }

               @Override
               public boolean contains(Object element) {
                  return this.this$0.containsValue(element);
               }

               @NotNull
               @Override
               public java.util.Iterator<V> iterator() {
                  var entryIterator: java.util.Iterator = this.this$0.entrySet().iterator();
                  return new java.util.Iterator<V>(entryIterator) {
                     {
                        this.$entryIterator = `$entryIterator`;
                     }

                     @Override
                     public boolean hasNext() {
                        return this.$entryIterator.hasNext();
                     }

                     @Override
                     public V next() {
                        return this.$entryIterator.next().getValue();
                     }

                     @Override
                     public void remove() {
                        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
                     }
                  };
               }

               @Override
               public int getSize() {
                  return this.this$0.size();
               }
            };
         }

         var var10000: java.util.Collection = this._values;
         return var10000;
      }


   open fun AbstractMap() {
   }

   public override fun containsKey(key: Any): Boolean {
      return this.implFindEntry((K)key) != null;
   }

   public override fun containsValue(value: Any): Boolean {
      var `$this$any$iv`: java.lang.Iterable = this.entrySet();
      var var10000: Boolean;
      if (`$this$any$iv` is java.util.Collection && (`$this$any$iv` as java.util.Collection).isEmpty()) {
         var10000 = false;
      } else {
         var var4: java.util.Iterator = `$this$any$iv`.iterator();

         while (true) {
            if (!var4.hasNext()) {
               var10000 = false;
               break;
            }

            if ((var4.next() as Entry).getValue() == value) {
               var10000 = true;
               break;
            }
         }
      }

      return var10000;
   }

   internal fun containsEntry(entry: kotlin.collections.Map.Entry<*, *>?): Boolean {
      if (entry == null) {
         return false;
      } else {
         var key: Any = entry.getKey();
         var value: Any = entry.getValue();
         var var10000: java.util.Map = this;
         var ourValue: Any = var10000.get(key);
         if (!(value == ourValue)) {
            return false;
         } else {
            if (ourValue == null) {
               var10000 = this;
               if (!var10000.containsKey(key)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public override operator fun equals(other: Any?): Boolean {
      if (other === this) {
         return true;
      } else if (other !is java.util.Map) {
         return false;
      } else if (this.size() != (other as java.util.Map).size()) {
         return false;
      } else {
         var `$this$all$iv`: java.lang.Iterable = (other as java.util.Map).entrySet();
         var var10000: Boolean;
         if (`$this$all$iv` is java.util.Collection && (`$this$all$iv` as java.util.Collection).isEmpty()) {
            var10000 = true;
         } else {
            var var4: java.util.Iterator = `$this$all$iv`.iterator();

            while (true) {
               if (!var4.hasNext()) {
                  var10000 = true;
                  break;
               }

               if (!this.containsEntry$kotlin_stdlib(var4.next() as Entry<?, ?>)) {
                  var10000 = false;
                  break;
               }
            }
         }

         return var10000;
      }
   }

   public override operator fun get(key: Any): Any? {
      var var10000: Entry = this.implFindEntry((K)key);
      return (V)(if (var10000 != null) var10000.getValue() else null);
   }

   public override fun hashCode(): Int {
      return this.entrySet().hashCode();
   }

   public override fun isEmpty(): Boolean {
      return this.size() == 0;
   }

   public override fun toString(): String {
      return CollectionsKt.joinToString$default(
         this.entrySet(), ", ", "{", "}", 0, null, (new Function1<Entry<? extends K, ? extends V>, java.lang.CharSequence>(this) {
            {
               super(1);
               this.this$0 = `$receiver`;
            }

            @NotNull
            public final java.lang.CharSequence invoke(@NotNull Entry<? extends K, ? extends V> it) {
               return AbstractMap.access$toString(this.this$0, it);
            }
         }) as Function1, 24, null
      );
   }

   private fun toString(entry: kotlin.collections.Map.Entry<Any, Any>): String {
      return this.toString(entry.getKey()) + '=' + this.toString(entry.getValue());
   }

   private fun toString(o: Any?): String {
      return if (o === this) "(this Map)" else java.lang.String.valueOf(o);
   }

   private fun implFindEntry(key: Any): kotlin.collections.Map.Entry<Any, Any>? {
      var var4: java.util.Iterator = this.entrySet().iterator();

      var var10000: Any;
      while (true) {
         if (var4.hasNext()) {
            var `element$iv`: Any = var4.next();
            if (!((`element$iv` as Entry).getKey() == key)) {
               continue;
            }

            var10000 = `element$iv`;
            break;
         }

         var10000 = null;
         break;
      }

      return var10000 as Entry<K, V>;
   }

   override fun clear() {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   override fun put(key: K, value: V): V {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   override fun putAll(from: MutableMap<K, V>) {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   override fun remove(key: Any): V {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   abstract fun getEntries(): java.util.Set

   @SourceDebugExtension(["SMAP\nAbstractMap.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AbstractMap.kt\nkotlin/collections/AbstractMap$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,153:1\n1#2:154\n*E\n"])
   internal companion object Companion private constructor() {
      internal fun entryHashCode(e: kotlin.collections.Map.Entry<*, *>): Int {
         var var10000: Any = e.getKey();
         var var4: Int = if (var10000 != null) var10000.hashCode() else 0;
         var var10001: Any = e.getValue();
         return var4 xor (if (var10001 != null) var10001.hashCode() else 0);
      }

      internal fun entryToString(e: kotlin.collections.Map.Entry<*, *>): String {
         return "" + e.getKey() + '=' + e.getValue();
      }

      internal fun entryEquals(e: kotlin.collections.Map.Entry<*, *>, other: Any?): Boolean {
         if (other !is Entry) {
            return false;
         } else {
            return e.getKey() == (other as Entry).getKey() && e.getValue() == (other as Entry).getValue();
         }
      }
   }
}
