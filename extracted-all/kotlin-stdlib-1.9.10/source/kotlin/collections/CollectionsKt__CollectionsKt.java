package kotlin.collections

import java.util.ArrayList
import java.util.Comparator
import kotlin.contracts.InvocationKind
import kotlin.internal.InlineOnly
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.random.Random
import org.jetbrains.annotations.NotNull

@SourceDebugExtension(["SMAP\nCollections.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Collections.kt\nkotlin/collections/CollectionsKt__CollectionsKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,481:1\n404#1:483\n1#2:482\n*S KotlinDebug\n*F\n+ 1 Collections.kt\nkotlin/collections/CollectionsKt__CollectionsKt\n*L\n398#1:483\n*E\n"])
// $VF: Class flags could not be determined
internal class CollectionsKt__CollectionsKt : CollectionsKt__CollectionsJVMKt {
   public final val indices: IntRange
      public final get() {
         return new IntRange(0, `$this$indices`.size() - 1);
      }

   public final val lastIndex: Int
      public final get() {
         return `$this$lastIndex`.size() - 1;
      }


   @JvmStatic
   internal fun <T> Array<out T>.asCollection(): Collection<T> {
      return (java.util.Collection<T>)(new ArrayAsCollection<>(`$this$asCollection`, false));
   }

   @JvmStatic
   public fun <T> emptyList(): List<T> {
      return EmptyList.INSTANCE;
   }

   @JvmStatic
   public fun <T> listOf(vararg elements: T): List<T> {
      return (java.util.List<T>)(if (elements.length > 0) ArraysKt.asList(elements) else CollectionsKt.emptyList());
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T> listOf(): List<T> {
      return CollectionsKt.emptyList();
   }

   @SinceKotlin(version = "1.1")
   @InlineOnly
   @JvmStatic
   public inline fun <T> mutableListOf(): MutableList<T> {
      return new ArrayList();
   }

   @SinceKotlin(version = "1.1")
   @InlineOnly
   @JvmStatic
   public inline fun <T> arrayListOf(): ArrayList<T> {
      return new ArrayList();
   }

   @JvmStatic
   public fun <T> mutableListOf(vararg elements: T): MutableList<T> {
      return if (elements.length == 0) new ArrayList() else new ArrayList<>(new ArrayAsCollection<>(elements, true));
   }

   @JvmStatic
   public fun <T> arrayListOf(vararg elements: T): ArrayList<T> {
      return if (elements.length == 0) new ArrayList() else new ArrayList<>(new ArrayAsCollection<>(elements, true));
   }

   @JvmStatic
   public fun <T : Any> listOfNotNull(element: T?): List<T> {
      return (java.util.List<T>)(if (element != null) CollectionsKt.listOf(element) else CollectionsKt.emptyList());
   }

   @JvmStatic
   public fun <T : Any> listOfNotNull(vararg elements: T?): List<T> {
      return (java.util.List<T>)ArraysKt.filterNotNull(elements);
   }

   @SinceKotlin(version = "1.1")
   @InlineOnly
   @JvmStatic
   public inline fun <T> List(size: Int, init: (Int) -> T): List<T> {
      var var2: ArrayList = new ArrayList(size);

      for (int var3 = 0; var3 < size; var3++) {
         var2.add(init.invoke(var3));
      }

      return var2;
   }

   @SinceKotlin(version = "1.1")
   @InlineOnly
   @JvmStatic
   public inline fun <T> MutableList(size: Int, init: (Int) -> T): MutableList<T> {
      var list: ArrayList = new ArrayList(size);

      for (int var3 = 0; var3 < size; var3++) {
         list.add(init.invoke(var3));
      }

      return list;
   }

   @SinceKotlin(version = "1.6")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun <E> buildList(builderAction: (MutableList<E>) -> Unit): List<E> {
      contract {
         callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE)
      }

      var var1: java.util.List = CollectionsKt.createListBuilder();
      builderAction.invoke(var1);
      return CollectionsKt.build(var1);
   }

   @SinceKotlin(version = "1.6")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun <E> buildList(capacity: Int, builderAction: (MutableList<E>) -> Unit): List<E> {
      contract {
         callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE)
      }

      var var2: java.util.List = CollectionsKt.createListBuilder(capacity);
      builderAction.invoke(var2);
      return CollectionsKt.build(var2);
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T> Collection<T>.isNotEmpty(): Boolean {
      return !`$this$isNotEmpty`.isEmpty();
   }

   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   public inline fun <T> Collection<T>?.isNullOrEmpty(): Boolean {
      contract {
         returns(false) implies (this != null)
      }

      return `$this$isNullOrEmpty` == null || `$this$isNullOrEmpty`.isEmpty();
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T> Collection<T>?.orEmpty(): Collection<T> {
      var var10000: java.util.Collection = `$this$orEmpty`;
      if (`$this$orEmpty` == null) {
         var10000 = CollectionsKt.emptyList();
      }

      return var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T> List<T>?.orEmpty(): List<T> {
      var var10000: java.util.List = `$this$orEmpty`;
      if (`$this$orEmpty` == null) {
         var10000 = CollectionsKt.emptyList();
      }

      return var10000;
   }

   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   public inline fun <C, R> C.ifEmpty(defaultValue: () -> R): R where C : Collection<*>, C : R {
      return (R)(if (`$this$ifEmpty`.isEmpty()) defaultValue.invoke() else `$this$ifEmpty`);
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T> Collection<T>.containsAll(elements: Collection<T>): Boolean {
      return `$this$containsAll`.containsAll(elements);
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   public fun <T> Iterable<T>.shuffled(random: Random): List<T> {
      var var2: java.util.List = CollectionsKt.toMutableList(`$this$shuffled`);
      CollectionsKt.shuffle(var2, random);
      return var2;
   }

   @JvmStatic
   internal fun <T> List<T>.optimizeReadOnlyList(): List<T> {
      var var10000: java.util.List;
      switch ($this$optimizeReadOnlyList.size()) {
         case 0:
            var10000 = CollectionsKt.emptyList();
            break;
         case 1:
            var10000 = CollectionsKt.listOf(`$this$optimizeReadOnlyList`.get(0));
            break;
         default:
            var10000 = `$this$optimizeReadOnlyList`;
      }

      return var10000;
   }

   @JvmStatic
   public fun <T : Comparable<T>> List<T?>.binarySearch(element: T?, fromIndex: Int, toIndex: Int): Int {
      rangeCheck$CollectionsKt__CollectionsKt(`$this$binarySearch`.size(), fromIndex, toIndex);
      var low: Int = fromIndex;
      var high: Int = toIndex - 1;

      while (low <= high) {
         var mid: Int = low + high ushr 1;
         var cmp: Int = ComparisonsKt.compareValues(`$this$binarySearch`.get(low + high ushr 1) as java.lang.Comparable, element);
         if (cmp < 0) {
            low = mid + 1;
         } else {
            if (cmp <= 0) {
               return mid;
            }

            high = mid - 1;
         }
      }

      return -(low + 1);
   }

   @JvmStatic
   public fun <T> List<T>.binarySearch(element: T, comparator: Comparator<in T>, fromIndex: Int, toIndex: Int): Int {
      rangeCheck$CollectionsKt__CollectionsKt(`$this$binarySearch`.size(), fromIndex, toIndex);
      var low: Int = fromIndex;
      var high: Int = toIndex - 1;

      while (low <= high) {
         var mid: Int = low + high ushr 1;
         var cmp: Int = comparator.compare(`$this$binarySearch`.get(low + high ushr 1), element);
         if (cmp < 0) {
            low = mid + 1;
         } else {
            if (cmp <= 0) {
               return mid;
            }

            high = mid - 1;
         }
      }

      return -(low + 1);
   }

   @JvmStatic
   public inline fun <T, K : Comparable<K>> List<T>.binarySearchBy(key: K?, fromIndex: Int, toIndex: Int, crossinline selector: (T) -> K?): Int {
      return CollectionsKt.binarySearch(`$this$binarySearchBy`, fromIndex, toIndex, (new Function1<T, Integer>(selector, key) {
         {
            super(1);
            this.$selector = `$selector`;
            this.$key = (K)`$key`;
         }

         @NotNull
         public final Integer invoke(T it) {
            return ComparisonsKt.compareValues(this.$selector.invoke(it) as java.lang.Comparable, this.$key);
         }
      }) as Function1);
   }

   @JvmStatic
   public fun <T> List<T>.binarySearch(fromIndex: Int, toIndex: Int, comparison: (T) -> Int): Int {
      rangeCheck$CollectionsKt__CollectionsKt(`$this$binarySearch`.size(), fromIndex, toIndex);
      var low: Int = fromIndex;
      var high: Int = toIndex - 1;

      while (low <= high) {
         var mid: Int = low + high ushr 1;
         var cmp: Int = (comparison.invoke(`$this$binarySearch`.get(low + high ushr 1)) as java.lang.Number).intValue();
         if (cmp < 0) {
            low = mid + 1;
         } else {
            if (cmp <= 0) {
               return mid;
            }

            high = mid - 1;
         }
      }

      return -(low + 1);
   }

   @JvmStatic
   private fun rangeCheck(size: Int, fromIndex: Int, toIndex: Int) {
      if (fromIndex > toIndex) {
         throw new IllegalArgumentException("fromIndex (" + fromIndex + ") is greater than toIndex (" + toIndex + ").");
      } else if (fromIndex < 0) {
         throw new IndexOutOfBoundsException("fromIndex (" + fromIndex + ") is less than zero.");
      } else if (toIndex > size) {
         throw new IndexOutOfBoundsException("toIndex (" + toIndex + ") is greater than size (" + size + ").");
      }
   }

   @PublishedApi
   @SinceKotlin(version = "1.3")
   @JvmStatic
   internal fun throwIndexOverflow() {
      throw new ArithmeticException("Index overflow has happened.");
   }

   @PublishedApi
   @SinceKotlin(version = "1.3")
   @JvmStatic
   internal fun throwCountOverflow() {
      throw new ArithmeticException("Count overflow has happened.");
   }

   open fun CollectionsKt__CollectionsKt() {
   }
}
