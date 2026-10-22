package kotlin.collections.builders

import java.io.NotSerializableException
import java.io.Serializable
import java.util.Arrays
import java.util.NoSuchElementException
import kotlin.collections.MutableMap.MutableEntry
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.markers.KMutableIterator
import kotlin.jvm.internal.markers.KMutableMap

@SourceDebugExtension(["SMAP\nMapBuilder.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MapBuilder.kt\nkotlin/collections/builders/MapBuilder\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,697:1\n1#2:698\n*E\n"])
internal class MapBuilder<K, V> private constructor(vararg keysArray: Any,
      vararg valuesArray: Any,
      presenceArray: IntArray,
      hashArray: IntArray,
      maxProbeDistance: Int,
      length: Int
   ) :
   java.util.Map<K, V>,
   Serializable,
   KMutableMap {
   @JvmStatic
   public MapBuilder.Companion Companion = new MapBuilder.Companion(null);
   private K[] keysArray;
   private V[] valuesArray;
   @JvmStatic
   private int MAGIC = -1640531527;
   @JvmStatic
   private int INITIAL_CAPACITY = 8;
   @JvmStatic
   private int INITIAL_MAX_PROBE_DISTANCE = 2;
   @JvmStatic
   private int TOMBSTONE = -1;
   @JvmStatic
   private MapBuilder Empty;

   internal final val capacity: Int
      internal final get() {
         return this.keysArray.length;
      }

   public open val entries: MutableSet<MutableEntry<Any, Any>>
      public open get() {
         if (this.entriesView == null) {
            var var2: MapBuilderEntries = new MapBuilderEntries<>(this);
            this.entriesView = var2;
            return var2;
         } else {
            return this.entriesView as java.util.Set<java.util.Map.Entry<K, V>>;
         }
      }

   private final var entriesView: MapBuilderEntries<Any, Any>?
   private final var hashArray: IntArray
   private final var hashShift: Int
   private final val hashSize: Int
      private final get() {
         return this.hashArray.length;
      }

   internal final var isReadOnly: Boolean
      private set
   public open val keys: MutableSet<Any>
      public open get() {
         var var10000: java.util.Set;
         if (this.keysView == null) {
            var var2: MapBuilderKeys = new MapBuilderKeys<>(this);
            this.keysView = var2;
            var10000 = var2;
         } else {
            var10000 = this.keysView;
         }

         return var10000;
      }

   private final var keysArray: Array<Any>
   private final var keysView: MapBuilderKeys<Any>?
   private final var length: Int
   private final var maxProbeDistance: Int
   private final var presenceArray: IntArray
   public open var size: Int
      private set
   public open val values: MutableCollection<Any>
      public open get() {
         var var10000: java.util.Collection;
         if (this.valuesView == null) {
            var var2: MapBuilderValues = new MapBuilderValues<>(this);
            this.valuesView = var2;
            var10000 = var2;
         } else {
            var10000 = this.valuesView;
         }

         return var10000;
      }

   private final var valuesArray: Array<Any>?
   private final var valuesView: MapBuilderValues<Any>?

   init {
      this.keysArray = (K[])keysArray;
      this.valuesArray = (V[])valuesArray;
      this.presenceArray = presenceArray;
      this.hashArray = hashArray;
      this.maxProbeDistance = maxProbeDistance;
      this.length = length;
      this.hashShift = MapBuilder.Companion.access$computeShift(Companion, this.getHashSize());
   }

   public constructor() : this(8)
   public constructor(initialCapacity: Int) : this(
         (K[])ListBuilderKt.arrayOfUninitializedElements(initialCapacity),
         null,
         new int[initialCapacity],
         new int[MapBuilder.Companion.access$computeHashSize(Companion, initialCapacity)],
         2,
         0
      )
   public fun build(): Map<Any, Any> {
      this.checkIsMutable$kotlin_stdlib();
      this.isReadOnly = true;
      var var10000: java.util.Map;
      if (this.size() > 0) {
         var10000 = this;
      } else {
         var var1: MapBuilder = Empty;
         var10000 = var1;
      }

      return var10000;
   }

   private fun writeReplace(): Any {
      if (this.isReadOnly) {
         return new SerializedMap(this);
      } else {
         throw new NotSerializableException("The map cannot be serialized while it is being built.");
      }
   }

   public override fun isEmpty(): Boolean {
      return this.size() == 0;
   }

   public override fun containsKey(key: Any): Boolean {
      return this.findKey((K)key) >= 0;
   }

   public override fun containsValue(value: Any): Boolean {
      return this.findValue((V)value) >= 0;
   }

   public override operator fun get(key: Any): Any? {
      var index: Int = this.findKey((K)key);
      if (index < 0) {
         return null;
      } else {
         var var10000: Array<Any> = this.valuesArray;
         return (V)var10000[index];
      }
   }

   public override fun put(key: Any, value: Any): Any? {
      this.checkIsMutable$kotlin_stdlib();
      var index: Int = this.addKey$kotlin_stdlib((K)key);
      var valuesArray: Array<Any> = this.allocateValuesArray();
      if (index < 0) {
         var oldValue: Any = valuesArray[-index - 1];
         valuesArray[-index - 1] = value;
         return (V)oldValue;
      } else {
         valuesArray[index] = value;
         return null;
      }
   }

   public override fun putAll(from: Map<out Any, Any>) {
      this.checkIsMutable$kotlin_stdlib();
      this.putAllEntries(from.entrySet());
   }

   public override fun remove(key: Any): Any? {
      var index: Int = this.removeKey$kotlin_stdlib((K)key);
      if (index < 0) {
         return null;
      } else {
         var var10000: Array<Any> = this.valuesArray;
         var oldValue: Any = var10000[index];
         ListBuilderKt.resetAt(var10000, index);
         return (V)oldValue;
      }
   }

   public override fun clear() {
      this.checkIsMutable$kotlin_stdlib();
      var var1: IntIterator = new IntRange(0, this.length - 1).iterator();

      while (var1.hasNext()) {
         var i: Int = var1.nextInt();
         var hash: Int = this.presenceArray[i];
         if (this.presenceArray[i] >= 0) {
            this.hashArray[hash] = 0;
            this.presenceArray[i] = -1;
         }
      }

      ListBuilderKt.resetRange(this.keysArray, 0, this.length);
      if (this.valuesArray != null) {
         ListBuilderKt.resetRange(this.valuesArray, 0, this.length);
      }

      this.size = 0;
      this.length = 0;
   }

   public override operator fun equals(other: Any?): Boolean {
      return other === this || other is java.util.Map && this.contentEquals(other as java.util.Map<?, ?>);
   }

   public override fun hashCode(): Int {
      var result: Int = 0;
      var it: MapBuilder.EntriesItr = this.entriesIterator$kotlin_stdlib();

      while (it.hasNext()) {
         result += it.nextHashCode$kotlin_stdlib();
      }

      return result;
   }

   public override fun toString(): String {
      var sb: StringBuilder = new StringBuilder(2 + this.size() * 3);
      sb.append("{");
      var i: Int = 0;

      for (MapBuilder.EntriesItr it = this.entriesIterator$kotlin_stdlib(); it.hasNext(); i++) {
         if (i > 0) {
            sb.append(", ");
         }

         it.nextAppendString(sb);
      }

      sb.append("}");
      var var10000: java.lang.String = sb.toString();
      return var10000;
   }

   internal fun checkIsMutable() {
      if (this.isReadOnly) {
         throw new UnsupportedOperationException();
      }
   }

   private fun ensureExtraCapacity(n: Int) {
      if (this.shouldCompact(n)) {
         this.rehash(this.getHashSize());
      } else {
         this.ensureCapacity(this.length + n);
      }
   }

   private fun shouldCompact(extraCapacity: Int): Boolean {
      var spareCapacity: Int = this.getCapacity$kotlin_stdlib() - this.length;
      var gaps: Int = this.length - this.size();
      return spareCapacity < extraCapacity && gaps + spareCapacity >= extraCapacity && gaps >= this.getCapacity$kotlin_stdlib() / 4;
   }

   private fun ensureCapacity(capacity: Int) {
      if (capacity < 0) {
         throw new OutOfMemoryError();
      } else {
         if (capacity > this.getCapacity$kotlin_stdlib()) {
            var newSize: Int = this.getCapacity$kotlin_stdlib() * 3 / 2;
            if (capacity > newSize) {
               newSize = capacity;
            }

            this.keysArray = ListBuilderKt.copyOfUninitializedElements(this.keysArray, newSize);
            this.valuesArray = if (this.valuesArray != null) ListBuilderKt.copyOfUninitializedElements(this.valuesArray, newSize) else null;
            var var10001: IntArray = Arrays.copyOf(this.presenceArray, newSize);
            this.presenceArray = var10001;
            var newHashSize: Int = MapBuilder.Companion.access$computeHashSize(Companion, newSize);
            if (newHashSize > this.getHashSize()) {
               this.rehash(newHashSize);
            }
         }
      }
   }

   private fun allocateValuesArray(): Array<Any> {
      if (this.valuesArray != null) {
         return this.valuesArray;
      } else {
         var newValuesArray: Array<Any> = ListBuilderKt.arrayOfUninitializedElements(this.getCapacity$kotlin_stdlib());
         this.valuesArray = (V[])newValuesArray;
         return (V[])newValuesArray;
      }
   }

   private fun hash(key: Any): Int {
      return (if (key != null) key.hashCode() else 0) * -1640531527 ushr this.hashShift;
   }

   private fun compact() {
      var i: Int = 0;
      var j: Int = 0;

      var valuesArray: Array<Any>;
      for (valuesArray = this.valuesArray; i < this.length; i++) {
         if (this.presenceArray[i] >= 0) {
            this.keysArray[j] = this.keysArray[i];
            if (valuesArray != null) {
               valuesArray[j] = valuesArray[i];
            }

            j++;
         }
      }

      ListBuilderKt.resetRange(this.keysArray, j, this.length);
      if (valuesArray != null) {
         ListBuilderKt.resetRange(valuesArray, j, this.length);
      }

      this.length = j;
   }

   private fun rehash(newHashSize: Int) {
      if (this.length > this.size()) {
         this.compact();
      }

      if (newHashSize != this.getHashSize()) {
         this.hashArray = new int[newHashSize];
         this.hashShift = MapBuilder.Companion.access$computeShift(Companion, newHashSize);
      } else {
         ArraysKt.fill(this.hashArray, 0, 0, this.getHashSize());
      }

      var i: Int = 0;

      while (i < this.length) {
         if (!this.putRehash(i++)) {
            throw new IllegalStateException("This cannot happen with fixed magic multiplier and grow-only hash array. Have object hashCodes changed?");
         }
      }
   }

   private fun putRehash(i: Int): Boolean {
      var hash: Int = this.hash(this.keysArray[i]);
      var probesLeft: Int = this.maxProbeDistance;

      while (true) {
         if (this.hashArray[hash] == 0) {
            this.hashArray[hash] = i + 1;
            this.presenceArray[i] = hash;
            return true;
         }

         if (--probesLeft < 0) {
            return false;
         }

         if (hash-- == 0) {
            hash = this.getHashSize() - 1;
         }
      }
   }

   private fun findKey(key: Any): Int {
      var hash: Int = this.hash((K)key);
      var probesLeft: Int = this.maxProbeDistance;

      while (true) {
         var index: Int = this.hashArray[hash];
         if (this.hashArray[hash] == 0) {
            return -1;
         }

         if (index > 0 && this.keysArray[index - 1] == key) {
            return index - 1;
         }

         if (--probesLeft < 0) {
            return -1;
         }

         if (hash-- == 0) {
            hash = this.getHashSize() - 1;
         }
      }
   }

   private fun findValue(value: Any): Int {
      var i: Int = this.length;

      while (--i >= 0) {
         if (this.presenceArray[i] >= 0) {
            var var10000: Array<Any> = this.valuesArray;
            if (var10000[i] == value) {
               return i;
            }
         }
      }

      return -1;
   }

   internal fun addKey(key: Any): Int {
      this.checkIsMutable$kotlin_stdlib();

      while (true) {
         var hash: Int = this.hash((K)key);
         var tentativeMaxProbeDistance: Int = RangesKt.coerceAtMost(this.maxProbeDistance * 2, this.getHashSize() / 2);
         var probeDistance: Int = 0;

         while (true) {
            var index: Int = this.hashArray[hash];
            if (this.hashArray[hash] <= 0) {
               if (this.length < this.getCapacity$kotlin_stdlib()) {
                  var var7: Int = this.length++;
                  this.keysArray[var7] = (K)key;
                  this.presenceArray[var7] = hash;
                  this.hashArray[hash] = var7 + 1;
                  this.size = this.size() + 1;
                  if (probeDistance > this.maxProbeDistance) {
                     this.maxProbeDistance = probeDistance;
                  }

                  return var7;
               }

               this.ensureExtraCapacity(1);
               break;
            }

            if (this.keysArray[index - 1] == key) {
               return -index;
            }

            if (++probeDistance > tentativeMaxProbeDistance) {
               this.rehash(this.getHashSize() * 2);
               break;
            }

            if (hash-- == 0) {
               hash = this.getHashSize() - 1;
            }
         }
      }
   }

   internal fun removeKey(key: Any): Int {
      this.checkIsMutable$kotlin_stdlib();
      var index: Int = this.findKey((K)key);
      if (index < 0) {
         return -1;
      } else {
         this.removeKeyAt(index);
         return index;
      }
   }

   private fun removeKeyAt(index: Int) {
      ListBuilderKt.resetAt(this.keysArray, index);
      this.removeHashAt(this.presenceArray[index]);
      this.presenceArray[index] = -1;
      this.size = this.size() + -1;
   }

   private fun removeHashAt(removedHash: Int) {
      var hash: Int = removedHash;
      var hole: Int = removedHash;
      var probeDistance: Int = 0;
      var patchAttemptsLeft: Int = RangesKt.coerceAtMost(this.maxProbeDistance * 2, this.getHashSize() / 2);

      do {
         if (hash-- == 0) {
            hash = this.getHashSize() - 1;
         }

         if (++probeDistance > this.maxProbeDistance) {
            this.hashArray[hole] = 0;
            return;
         }

         var index: Int = this.hashArray[hash];
         if (this.hashArray[hash] == 0) {
            this.hashArray[hole] = 0;
            return;
         }

         if (index < 0) {
            this.hashArray[hole] = -1;
            hole = hash;
            probeDistance = 0;
         } else if ((this.hash(this.keysArray[index - 1]) - hash and this.getHashSize() - 1) >= probeDistance) {
            this.hashArray[hole] = index;
            this.presenceArray[index - 1] = hole;
            hole = hash;
            probeDistance = 0;
         }
      } while (--patchAttemptsLeft >= 0);

      this.hashArray[hole] = -1;
   }

   internal fun containsEntry(entry: kotlin.collections.Map.Entry<Any, Any>): Boolean {
      var index: Int = this.findKey((K)entry.getKey());
      if (index < 0) {
         return false;
      } else {
         var var10000: Array<Any> = this.valuesArray;
         return var10000[index] == entry.getValue();
      }
   }

   private fun contentEquals(other: Map<*, *>): Boolean {
      return this.size() == other.size() && this.containsAllEntries$kotlin_stdlib(other.entrySet());
   }

   internal fun containsAllEntries(m: Collection<*>): Boolean {
      for (Object entry : m) {
         try {
            if (entry == null || !this.containsEntry$kotlin_stdlib(entry as java.util.Map.Entry<? extends K, ? extends V>)) {
               return false;
            }
         } catch (ClassCastException var5) {
            return false;
         }
      }

      return true;
   }

   private fun putEntry(entry: kotlin.collections.Map.Entry<Any, Any>): Boolean {
      var index: Int = this.addKey$kotlin_stdlib((K)entry.getKey());
      var valuesArray: Array<Any> = this.allocateValuesArray();
      if (index >= 0) {
         valuesArray[index] = entry.getValue();
         return true;
      } else if (!(entry.getValue() == valuesArray[-index - 1])) {
         valuesArray[-index - 1] = entry.getValue();
         return true;
      } else {
         return false;
      }
   }

   private fun putAllEntries(from: Collection<kotlin.collections.Map.Entry<Any, Any>>): Boolean {
      if (from.isEmpty()) {
         return false;
      } else {
         this.ensureExtraCapacity(from.size());
         var it: java.util.Iterator = from.iterator();
         var updated: Boolean = false;

         while (it.hasNext()) {
            if (this.putEntry(it.next() as java.util.Map.Entry<? extends K, ? extends V>)) {
               updated = true;
            }
         }

         return updated;
      }
   }

   internal fun removeEntry(entry: kotlin.collections.Map.Entry<Any, Any>): Boolean {
      this.checkIsMutable$kotlin_stdlib();
      var index: Int = this.findKey((K)entry.getKey());
      if (index < 0) {
         return false;
      } else {
         var var10000: Array<Any> = this.valuesArray;
         if (!(var10000[index] == entry.getValue())) {
            return false;
         } else {
            this.removeKeyAt(index);
            return true;
         }
      }
   }

   internal fun removeValue(element: Any): Boolean {
      this.checkIsMutable$kotlin_stdlib();
      var index: Int = this.findValue((V)element);
      if (index < 0) {
         return false;
      } else {
         this.removeKeyAt(index);
         return true;
      }
   }

   internal fun keysIterator(): kotlin.collections.builders.MapBuilder.KeysItr<Any, Any> {
      return new MapBuilder.KeysItr<>(this);
   }

   internal fun valuesIterator(): kotlin.collections.builders.MapBuilder.ValuesItr<Any, Any> {
      return new MapBuilder.ValuesItr<>(this);
   }

   internal fun entriesIterator(): kotlin.collections.builders.MapBuilder.EntriesItr<Any, Any> {
      return new MapBuilder.EntriesItr<>(this);
   }

   @JvmStatic
   fun {
      var var0: MapBuilder = new MapBuilder(0);
      var0.isReadOnly = true;
      Empty = var0;
   }

   internal companion object Companion private constructor() {
      internal final val Empty: MapBuilder<Nothing, Nothing>
      private const val INITIAL_CAPACITY: Int
      private const val INITIAL_MAX_PROBE_DISTANCE: Int
      private const val MAGIC: Int
      private const val TOMBSTONE: Int


      private fun computeHashSize(capacity: Int): Int {
         return Integer.highestOneBit(RangesKt.coerceAtLeast(capacity, 1) * 3);
      }

      private fun computeShift(hashSize: Int): Int {
         return Integer.numberOfLeadingZeros(hashSize) + 1;
      }
   }

   internal class EntriesItr<K, V>(map: MapBuilder<Any, Any>) : MapBuilder.Itr(map), java.util.Iterator<java.util.Map.Entry<K, V>>, KMutableIterator {
      public open operator fun next(): kotlin.collections.builders.MapBuilder.EntryRef<Any, Any> {
         if (this.getIndex$kotlin_stdlib() >= MapBuilder.access$getLength$p(this.getMap$kotlin_stdlib())) {
            throw new NoSuchElementException();
         } else {
            var result: Int = this.getIndex$kotlin_stdlib();
            this.setIndex$kotlin_stdlib(result + 1);
            this.setLastIndex$kotlin_stdlib(result);
            var var2: MapBuilder.EntryRef = new MapBuilder.EntryRef<>(this.getMap$kotlin_stdlib(), this.getLastIndex$kotlin_stdlib());
            this.initNext$kotlin_stdlib();
            return var2;
         }
      }

      internal fun nextHashCode(): Int {
         if (this.getIndex$kotlin_stdlib() >= MapBuilder.access$getLength$p(this.getMap$kotlin_stdlib())) {
            throw new NoSuchElementException();
         } else {
            var result: Int = this.getIndex$kotlin_stdlib();
            this.setIndex$kotlin_stdlib(result + 1);
            this.setLastIndex$kotlin_stdlib(result);
            var var10000: Any = MapBuilder.access$getKeysArray$p(this.getMap$kotlin_stdlib())[this.getLastIndex$kotlin_stdlib()];
            var var3: Int = if (var10000 != null) var10000.hashCode() else 0;
            var var10001: Array<Any> = MapBuilder.access$getValuesArray$p(this.getMap$kotlin_stdlib());
            var10001 = (Object[])var10001[this.getLastIndex$kotlin_stdlib()];
            result = var3 xor (if (var10001 != null) var10001.hashCode() else 0);
            this.initNext$kotlin_stdlib();
            return result;
         }
      }

      public fun nextAppendString(sb: StringBuilder) {
         if (this.getIndex$kotlin_stdlib() >= MapBuilder.access$getLength$p(this.getMap$kotlin_stdlib())) {
            throw new NoSuchElementException();
         } else {
            var key: Int = this.getIndex$kotlin_stdlib();
            this.setIndex$kotlin_stdlib(key + 1);
            this.setLastIndex$kotlin_stdlib(key);
            var var4: Any = MapBuilder.access$getKeysArray$p(this.getMap$kotlin_stdlib())[this.getLastIndex$kotlin_stdlib()];
            if (var4 == this.getMap$kotlin_stdlib()) {
               sb.append("(this Map)");
            } else {
               sb.append(var4);
            }

            sb.append('=');
            var var10000: Array<Any> = MapBuilder.access$getValuesArray$p(this.getMap$kotlin_stdlib());
            var value: Any = var10000[this.getLastIndex$kotlin_stdlib()];
            if (value == this.getMap$kotlin_stdlib()) {
               sb.append("(this Map)");
            } else {
               sb.append(value);
            }

            this.initNext$kotlin_stdlib();
         }
      }
   }

   internal class EntryRef<K, V>(map: MapBuilder<Any, Any>, index: Int) : java.util.Map.Entry<K, V>, KMutableMap.Entry {
      private final val index: Int
      public open val key: Any
         public open get() {
            return (K)MapBuilder.access$getKeysArray$p(this.map)[this.index];
         }

      private final val map: MapBuilder<Any, Any>
      public open val value: Any
         public open get() {
            var var10000: Array<Any> = MapBuilder.access$getValuesArray$p(this.map);
            return (V)var10000[this.index];
         }


      init {
         this.map = map;
         this.index = index;
      }

      public override fun setValue(newValue: Any): Any {
         this.map.checkIsMutable$kotlin_stdlib();
         var valuesArray: Array<Any> = MapBuilder.access$allocateValuesArray(this.map);
         var oldValue: Any = valuesArray[this.index];
         valuesArray[this.index] = newValue;
         return (V)oldValue;
      }

      public override operator fun equals(other: Any?): Boolean {
         return other is java.util.Map.Entry
            && (other as java.util.Map.Entry).getKey() == this.getKey()
            && (other as java.util.Map.Entry).getValue() == this.getValue();
      }

      public override fun hashCode(): Int {
         var var10000: Any = this.getKey();
         var var1: Int = if (var10000 != null) var10000.hashCode() else 0;
         var var10001: Any = this.getValue();
         return var1 xor (if (var10001 != null) var10001.hashCode() else 0);
      }

      public override fun toString(): String {
         return "" + this.getKey() + '=' + this.getValue();
      }
   }

   @SourceDebugExtension(["SMAP\nMapBuilder.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MapBuilder.kt\nkotlin/collections/builders/MapBuilder$Itr\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,697:1\n1#2:698\n*E\n"])
   internal open class Itr<K, V>(map: MapBuilder<Any, Any>) {
      internal final var index: Int
      internal final var lastIndex: Int
      internal final val map: MapBuilder<Any, Any>

      init {
         this.map = map;
         this.lastIndex = -1;
         this.initNext$kotlin_stdlib();
      }

      internal fun initNext() {
         while (this.index < MapBuilder.access$getLength$p(this.map) && MapBuilder.access$getPresenceArray$p(this.map)[this.index] < 0) {
            var var1: Int = this.index++;
         }
      }

      public fun hasNext(): Boolean {
         return this.index < MapBuilder.access$getLength$p(this.map);
      }

      public fun remove() {
         if (this.lastIndex == -1) {
            throw new IllegalStateException("Call next() before removing element from the iterator.".toString());
         } else {
            this.map.checkIsMutable$kotlin_stdlib();
            MapBuilder.access$removeKeyAt(this.map, this.lastIndex);
            this.lastIndex = -1;
         }
      }
   }

   internal class KeysItr<K, V>(map: MapBuilder<Any, Any>) : MapBuilder.Itr(map), java.util.Iterator<K>, KMutableIterator {
      public override operator fun next(): Any {
         if (this.getIndex$kotlin_stdlib() >= MapBuilder.access$getLength$p(this.getMap$kotlin_stdlib())) {
            throw new NoSuchElementException();
         } else {
            var result: Int = this.getIndex$kotlin_stdlib();
            this.setIndex$kotlin_stdlib(result + 1);
            this.setLastIndex$kotlin_stdlib(result);
            var var2: Any = MapBuilder.access$getKeysArray$p(this.getMap$kotlin_stdlib())[this.getLastIndex$kotlin_stdlib()];
            this.initNext$kotlin_stdlib();
            return (K)var2;
         }
      }
   }

   internal class ValuesItr<K, V>(map: MapBuilder<Any, Any>) : MapBuilder.Itr(map), java.util.Iterator<V>, KMutableIterator {
      public override operator fun next(): Any {
         if (this.getIndex$kotlin_stdlib() >= MapBuilder.access$getLength$p(this.getMap$kotlin_stdlib())) {
            throw new NoSuchElementException();
         } else {
            var result: Int = this.getIndex$kotlin_stdlib();
            this.setIndex$kotlin_stdlib(result + 1);
            this.setLastIndex$kotlin_stdlib(result);
            var var10000: Array<Any> = MapBuilder.access$getValuesArray$p(this.getMap$kotlin_stdlib());
            var var2: Any = var10000[this.getLastIndex$kotlin_stdlib()];
            this.initNext$kotlin_stdlib();
            return (V)var2;
         }
      }
   }
}
