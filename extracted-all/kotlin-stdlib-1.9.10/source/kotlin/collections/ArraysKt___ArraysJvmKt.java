package kotlin.collections

import java.math.BigDecimal
import java.math.BigInteger
import java.util.ArrayList
import java.util.Arrays
import java.util.Comparator
import java.util.RandomAccess
import java.util.SortedSet
import java.util.TreeSet
import kotlin.internal.InlineOnly
import kotlin.internal.LowPriorityInOverloadResolution
import kotlin.internal.PlatformImplementationsKt
import kotlin.jvm.internal.SourceDebugExtension
import org.jetbrains.annotations.NotNull

@SourceDebugExtension(["SMAP\n_ArraysJvm.kt\nKotlin\n*S Kotlin\n*F\n+ 1 _ArraysJvm.kt\nkotlin/collections/ArraysKt___ArraysJvmKt\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,3042:1\n13896#2,14:3043\n13919#2,14:3057\n13942#2,14:3071\n13965#2,14:3085\n13988#2,14:3099\n14011#2,14:3113\n14034#2,14:3127\n14057#2,14:3141\n14080#2,14:3155\n16482#2,14:3169\n16505#2,14:3183\n16528#2,14:3197\n16551#2,14:3211\n16574#2,14:3225\n16597#2,14:3239\n16620#2,14:3253\n16643#2,14:3267\n16666#2,14:3281\n*S KotlinDebug\n*F\n+ 1 _ArraysJvm.kt\nkotlin/collections/ArraysKt___ArraysJvmKt\n*L\n2434#1:3043,14\n2441#1:3057,14\n2448#1:3071,14\n2455#1:3085,14\n2462#1:3099,14\n2469#1:3113,14\n2476#1:3127,14\n2483#1:3141,14\n2490#1:3155,14\n2632#1:3169,14\n2639#1:3183,14\n2646#1:3197,14\n2653#1:3211,14\n2660#1:3225,14\n2667#1:3239,14\n2674#1:3253,14\n2681#1:3267,14\n2688#1:3281,14\n*E\n"])
// $VF: Class flags could not be determined
internal class ArraysKt___ArraysJvmKt : ArraysKt__ArraysKt {
   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<out T>.elementAt(index: Int): T {
      return (T)`$this$elementAt`[index];
   }

   @InlineOnly
   @JvmStatic
   public inline fun ByteArray.elementAt(index: Int): Byte {
      return `$this$elementAt`[index];
   }

   @InlineOnly
   @JvmStatic
   public inline fun ShortArray.elementAt(index: Int): Short {
      return `$this$elementAt`[index];
   }

   @InlineOnly
   @JvmStatic
   public inline fun IntArray.elementAt(index: Int): Int {
      return `$this$elementAt`[index];
   }

   @InlineOnly
   @JvmStatic
   public inline fun LongArray.elementAt(index: Int): Long {
      return `$this$elementAt`[index];
   }

   @InlineOnly
   @JvmStatic
   public inline fun FloatArray.elementAt(index: Int): Float {
      return `$this$elementAt`[index];
   }

   @InlineOnly
   @JvmStatic
   public inline fun DoubleArray.elementAt(index: Int): Double {
      return `$this$elementAt`[index];
   }

   @InlineOnly
   @JvmStatic
   public inline fun BooleanArray.elementAt(index: Int): Boolean {
      return `$this$elementAt`[index];
   }

   @InlineOnly
   @JvmStatic
   public inline fun CharArray.elementAt(index: Int): Char {
      return `$this$elementAt`[index];
   }

   @JvmStatic
   public fun <R> Array<*>.filterIsInstance(klass: Class<R>): List<R> {
      return ArraysKt.filterIsInstanceTo(`$this$filterIsInstance`, new ArrayList(), klass) as java.util.List<R>;
   }

   @JvmStatic
   public fun <C : MutableCollection<in R>, R> Array<*>.filterIsInstanceTo(destination: C, klass: Class<R>): C {
      for (Object element : $this$filterIsInstanceTo) {
         if (klass.isInstance(element)) {
            destination.add(element);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public fun <T> Array<out T>.asList(): List<T> {
      var var10000: java.util.List = ArraysUtilJVM.asList(`$this$asList`);
      return var10000;
   }

   @JvmStatic
   public fun ByteArray.asList(): List<Byte> {
      return new RandomAccess(`$this$asList`) {
         {
            this.$this_asList = `$receiver`;
         }

         @Override
         public int getSize() {
            return this.$this_asList.length;
         }

         @Override
         public boolean isEmpty() {
            return this.$this_asList.length == 0;
         }

         public boolean contains(byte element) {
            return ArraysKt.contains(this.$this_asList, element);
         }

         @NotNull
         public java.lang.Byte get(int index) {
            return this.$this_asList[index];
         }

         public int indexOf(byte element) {
            return ArraysKt.indexOf(this.$this_asList, element);
         }

         public int lastIndexOf(byte element) {
            return ArraysKt.lastIndexOf(this.$this_asList, element);
         }
      };
   }

   @JvmStatic
   public fun ShortArray.asList(): List<Short> {
      return new RandomAccess(`$this$asList`) {
         {
            this.$this_asList = `$receiver`;
         }

         @Override
         public int getSize() {
            return this.$this_asList.length;
         }

         @Override
         public boolean isEmpty() {
            return this.$this_asList.length == 0;
         }

         public boolean contains(short element) {
            return ArraysKt.contains(this.$this_asList, element);
         }

         @NotNull
         public java.lang.Short get(int index) {
            return this.$this_asList[index];
         }

         public int indexOf(short element) {
            return ArraysKt.indexOf(this.$this_asList, element);
         }

         public int lastIndexOf(short element) {
            return ArraysKt.lastIndexOf(this.$this_asList, element);
         }
      };
   }

   @JvmStatic
   public fun IntArray.asList(): List<Int> {
      return new RandomAccess(`$this$asList`) {
         {
            this.$this_asList = `$receiver`;
         }

         @Override
         public int getSize() {
            return this.$this_asList.length;
         }

         @Override
         public boolean isEmpty() {
            return this.$this_asList.length == 0;
         }

         public boolean contains(int element) {
            return ArraysKt.contains(this.$this_asList, element);
         }

         @NotNull
         public Integer get(int index) {
            return this.$this_asList[index];
         }

         public int indexOf(int element) {
            return ArraysKt.indexOf(this.$this_asList, element);
         }

         public int lastIndexOf(int element) {
            return ArraysKt.lastIndexOf(this.$this_asList, element);
         }
      };
   }

   @JvmStatic
   public fun LongArray.asList(): List<Long> {
      return new RandomAccess(`$this$asList`) {
         {
            this.$this_asList = `$receiver`;
         }

         @Override
         public int getSize() {
            return this.$this_asList.length;
         }

         @Override
         public boolean isEmpty() {
            return this.$this_asList.length == 0;
         }

         public boolean contains(long element) {
            return ArraysKt.contains(this.$this_asList, element);
         }

         @NotNull
         public java.lang.Long get(int index) {
            return this.$this_asList[index];
         }

         public int indexOf(long element) {
            return ArraysKt.indexOf(this.$this_asList, element);
         }

         public int lastIndexOf(long element) {
            return ArraysKt.lastIndexOf(this.$this_asList, element);
         }
      };
   }

   @JvmStatic
   public fun FloatArray.asList(): List<Float> {
      return new RandomAccess(`$this$asList`) {
         {
            this.$this_asList = `$receiver`;
         }

         @Override
         public int getSize() {
            return this.$this_asList.length;
         }

         @Override
         public boolean isEmpty() {
            return this.$this_asList.length == 0;
         }

         public boolean contains(float element) {
            var `$this$any$iv`: FloatArray = this.$this_asList;
            var var4: Int = 0;
            var var5: Int = this.$this_asList.length;

            var var10000: Boolean;
            while (true) {
               if (var4 >= var5) {
                  var10000 = false;
                  break;
               }

               if (java.lang.Float.floatToIntBits(`$this$any$iv`[var4]) == java.lang.Float.floatToIntBits(element)) {
                  var10000 = true;
                  break;
               }

               var4++;
            }

            return var10000;
         }

         @NotNull
         public java.lang.Float get(int index) {
            return this.$this_asList[index];
         }

         public int indexOf(float element) {
            var `$this$indexOfFirst$iv`: FloatArray = this.$this_asList;
            var `index$iv`: Int = 0;
            var var5: Int = this.$this_asList.length;

            var var10000: Int;
            while (true) {
               if (`index$iv` >= var5) {
                  var10000 = -1;
                  break;
               }

               if (java.lang.Float.floatToIntBits(`$this$indexOfFirst$iv`[`index$iv`]) == java.lang.Float.floatToIntBits(element)) {
                  var10000 = `index$iv`;
                  break;
               }

               `index$iv`++;
            }

            return var10000;
         }

         public int lastIndexOf(float element) {
            var `$this$indexOfLast$iv`: FloatArray = this.$this_asList;
            var var4: Int = this.$this_asList.length + -1;
            if (0 <= this.$this_asList.length + -1) {
               do {
                  var `index$iv`: Int = var4--;
                  if (java.lang.Float.floatToIntBits(`$this$indexOfLast$iv`[`index$iv`]) == java.lang.Float.floatToIntBits(element)) {
                     return `index$iv`;
                  }
               } while (0 <= var4);
            }

            return -1;
         }
      };
   }

   @JvmStatic
   public fun DoubleArray.asList(): List<Double> {
      return new RandomAccess(`$this$asList`) {
         {
            this.$this_asList = `$receiver`;
         }

         @Override
         public int getSize() {
            return this.$this_asList.length;
         }

         @Override
         public boolean isEmpty() {
            return this.$this_asList.length == 0;
         }

         public boolean contains(double element) {
            var `$this$any$iv`: DoubleArray = this.$this_asList;
            var var5: Int = 0;
            var var6: Int = this.$this_asList.length;

            var var10000: Boolean;
            while (true) {
               if (var5 >= var6) {
                  var10000 = false;
                  break;
               }

               if (java.lang.Double.doubleToLongBits(`$this$any$iv`[var5]) == java.lang.Double.doubleToLongBits(element)) {
                  var10000 = true;
                  break;
               }

               var5++;
            }

            return var10000;
         }

         @NotNull
         public java.lang.Double get(int index) {
            return this.$this_asList[index];
         }

         public int indexOf(double element) {
            var `$this$indexOfFirst$iv`: DoubleArray = this.$this_asList;
            var `index$iv`: Int = 0;
            var var6: Int = this.$this_asList.length;

            var var10000: Int;
            while (true) {
               if (`index$iv` >= var6) {
                  var10000 = -1;
                  break;
               }

               if (java.lang.Double.doubleToLongBits(`$this$indexOfFirst$iv`[`index$iv`]) == java.lang.Double.doubleToLongBits(element)) {
                  var10000 = `index$iv`;
                  break;
               }

               `index$iv`++;
            }

            return var10000;
         }

         public int lastIndexOf(double element) {
            var `$this$indexOfLast$iv`: DoubleArray = this.$this_asList;
            var var5: Int = this.$this_asList.length + -1;
            if (0 <= this.$this_asList.length + -1) {
               do {
                  var `index$iv`: Int = var5--;
                  if (java.lang.Double.doubleToLongBits(`$this$indexOfLast$iv`[`index$iv`]) == java.lang.Double.doubleToLongBits(element)) {
                     return `index$iv`;
                  }
               } while (0 <= var5);
            }

            return -1;
         }
      };
   }

   @JvmStatic
   public fun BooleanArray.asList(): List<Boolean> {
      return new RandomAccess(`$this$asList`) {
         {
            this.$this_asList = `$receiver`;
         }

         @Override
         public int getSize() {
            return this.$this_asList.length;
         }

         @Override
         public boolean isEmpty() {
            return this.$this_asList.length == 0;
         }

         public boolean contains(boolean element) {
            return ArraysKt.contains(this.$this_asList, element);
         }

         @NotNull
         public java.lang.Boolean get(int index) {
            return this.$this_asList[index];
         }

         public int indexOf(boolean element) {
            return ArraysKt.indexOf(this.$this_asList, element);
         }

         public int lastIndexOf(boolean element) {
            return ArraysKt.lastIndexOf(this.$this_asList, element);
         }
      };
   }

   @JvmStatic
   public fun CharArray.asList(): List<Char> {
      return new RandomAccess(`$this$asList`) {
         {
            this.$this_asList = `$receiver`;
         }

         @Override
         public int getSize() {
            return this.$this_asList.length;
         }

         @Override
         public boolean isEmpty() {
            return this.$this_asList.length == 0;
         }

         public boolean contains(char element) {
            return ArraysKt.contains(this.$this_asList, element);
         }

         @NotNull
         public Character get(int index) {
            return this.$this_asList[index];
         }

         public int indexOf(char element) {
            return ArraysKt.indexOf(this.$this_asList, element);
         }

         public int lastIndexOf(char element) {
            return ArraysKt.lastIndexOf(this.$this_asList, element);
         }
      };
   }

   @JvmStatic
   public fun <T> Array<out T>.binarySearch(element: T, comparator: Comparator<in T>, fromIndex: Int, toIndex: Int): Int {
      return Arrays.binarySearch(`$this$binarySearch`, fromIndex, toIndex, element, comparator);
   }

   @JvmStatic
   public fun <T> Array<out T>.binarySearch(element: T, fromIndex: Int, toIndex: Int): Int {
      return Arrays.binarySearch(`$this$binarySearch`, fromIndex, toIndex, element);
   }

   @JvmStatic
   public fun ByteArray.binarySearch(element: Byte, fromIndex: Int, toIndex: Int): Int {
      return Arrays.binarySearch(`$this$binarySearch`, fromIndex, toIndex, element);
   }

   @JvmStatic
   public fun ShortArray.binarySearch(element: Short, fromIndex: Int, toIndex: Int): Int {
      return Arrays.binarySearch(`$this$binarySearch`, fromIndex, toIndex, element);
   }

   @JvmStatic
   public fun IntArray.binarySearch(element: Int, fromIndex: Int, toIndex: Int): Int {
      return Arrays.binarySearch(`$this$binarySearch`, fromIndex, toIndex, element);
   }

   @JvmStatic
   public fun LongArray.binarySearch(element: Long, fromIndex: Int, toIndex: Int): Int {
      return Arrays.binarySearch(`$this$binarySearch`, fromIndex, toIndex, element);
   }

   @JvmStatic
   public fun FloatArray.binarySearch(element: Float, fromIndex: Int, toIndex: Int): Int {
      return Arrays.binarySearch(`$this$binarySearch`, fromIndex, toIndex, element);
   }

   @JvmStatic
   public fun DoubleArray.binarySearch(element: Double, fromIndex: Int, toIndex: Int): Int {
      return Arrays.binarySearch(`$this$binarySearch`, fromIndex, toIndex, element);
   }

   @JvmStatic
   public fun CharArray.binarySearch(element: Char, fromIndex: Int, toIndex: Int): Int {
      return Arrays.binarySearch(`$this$binarySearch`, fromIndex, toIndex, element);
   }

   @SinceKotlin(version = "1.1")
   @LowPriorityInOverloadResolution
   @JvmName(name = "contentDeepEqualsInline")
   @InlineOnly
   @JvmStatic
   public inline infix fun <T> Array<out T>.contentDeepEquals(other: Array<out T>): Boolean {
      return ArraysKt.contentDeepEquals(`$this$contentDeepEquals`, other);
   }

   @SinceKotlin(version = "1.4")
   @JvmName(name = "contentDeepEqualsNullable")
   @InlineOnly
   @JvmStatic
   public inline infix fun <T> Array<out T>?.contentDeepEquals(other: Array<out T>?): Boolean {
      return if (PlatformImplementationsKt.apiVersionIsAtLeast(1, 3, 0))
         ArraysKt.contentDeepEquals(`$this$contentDeepEquals`, other)
         else
         Arrays.deepEquals(`$this$contentDeepEquals`, other);
   }

   @SinceKotlin(version = "1.1")
   @LowPriorityInOverloadResolution
   @JvmName(name = "contentDeepHashCodeInline")
   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<out T>.contentDeepHashCode(): Int {
      return ArraysKt.contentDeepHashCode(`$this$contentDeepHashCode`);
   }

   @SinceKotlin(version = "1.4")
   @JvmName(name = "contentDeepHashCodeNullable")
   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<out T>?.contentDeepHashCode(): Int {
      return if (PlatformImplementationsKt.apiVersionIsAtLeast(1, 3, 0))
         ArraysKt.contentDeepHashCode(`$this$contentDeepHashCode`)
         else
         Arrays.deepHashCode(`$this$contentDeepHashCode`);
   }

   @SinceKotlin(version = "1.1")
   @LowPriorityInOverloadResolution
   @JvmName(name = "contentDeepToStringInline")
   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<out T>.contentDeepToString(): String {
      return ArraysKt.contentDeepToString(`$this$contentDeepToString`);
   }

   @SinceKotlin(version = "1.4")
   @JvmName(name = "contentDeepToStringNullable")
   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<out T>?.contentDeepToString(): String {
      if (PlatformImplementationsKt.apiVersionIsAtLeast(1, 3, 0)) {
         return ArraysKt.contentDeepToString(`$this$contentDeepToString`);
      } else {
         var var10000: java.lang.String = Arrays.deepToString(`$this$contentDeepToString`);
         return var10000;
      }
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline infix fun <T> Array<out T>?.contentEquals(other: Array<out T>?): Boolean {
      return Arrays.equals(`$this$contentEquals`, other);
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline infix fun ByteArray?.contentEquals(other: ByteArray?): Boolean {
      return Arrays.equals(`$this$contentEquals`, other);
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline infix fun ShortArray?.contentEquals(other: ShortArray?): Boolean {
      return Arrays.equals(`$this$contentEquals`, other);
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline infix fun IntArray?.contentEquals(other: IntArray?): Boolean {
      return Arrays.equals(`$this$contentEquals`, other);
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline infix fun LongArray?.contentEquals(other: LongArray?): Boolean {
      return Arrays.equals(`$this$contentEquals`, other);
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline infix fun FloatArray?.contentEquals(other: FloatArray?): Boolean {
      return Arrays.equals(`$this$contentEquals`, other);
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline infix fun DoubleArray?.contentEquals(other: DoubleArray?): Boolean {
      return Arrays.equals(`$this$contentEquals`, other);
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline infix fun BooleanArray?.contentEquals(other: BooleanArray?): Boolean {
      return Arrays.equals(`$this$contentEquals`, other);
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline infix fun CharArray?.contentEquals(other: CharArray?): Boolean {
      return Arrays.equals(`$this$contentEquals`, other);
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<out T>?.contentHashCode(): Int {
      return Arrays.hashCode(`$this$contentHashCode`);
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun ByteArray?.contentHashCode(): Int {
      return Arrays.hashCode(`$this$contentHashCode`);
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun ShortArray?.contentHashCode(): Int {
      return Arrays.hashCode(`$this$contentHashCode`);
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun IntArray?.contentHashCode(): Int {
      return Arrays.hashCode(`$this$contentHashCode`);
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun LongArray?.contentHashCode(): Int {
      return Arrays.hashCode(`$this$contentHashCode`);
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun FloatArray?.contentHashCode(): Int {
      return Arrays.hashCode(`$this$contentHashCode`);
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun DoubleArray?.contentHashCode(): Int {
      return Arrays.hashCode(`$this$contentHashCode`);
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun BooleanArray?.contentHashCode(): Int {
      return Arrays.hashCode(`$this$contentHashCode`);
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun CharArray?.contentHashCode(): Int {
      return Arrays.hashCode(`$this$contentHashCode`);
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<out T>?.contentToString(): String {
      var var10000: java.lang.String = Arrays.toString(`$this$contentToString`);
      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun ByteArray?.contentToString(): String {
      var var10000: java.lang.String = Arrays.toString(`$this$contentToString`);
      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun ShortArray?.contentToString(): String {
      var var10000: java.lang.String = Arrays.toString(`$this$contentToString`);
      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun IntArray?.contentToString(): String {
      var var10000: java.lang.String = Arrays.toString(`$this$contentToString`);
      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun LongArray?.contentToString(): String {
      var var10000: java.lang.String = Arrays.toString(`$this$contentToString`);
      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun FloatArray?.contentToString(): String {
      var var10000: java.lang.String = Arrays.toString(`$this$contentToString`);
      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun DoubleArray?.contentToString(): String {
      var var10000: java.lang.String = Arrays.toString(`$this$contentToString`);
      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun BooleanArray?.contentToString(): String {
      var var10000: java.lang.String = Arrays.toString(`$this$contentToString`);
      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun CharArray?.contentToString(): String {
      var var10000: java.lang.String = Arrays.toString(`$this$contentToString`);
      return var10000;
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   public fun <T> Array<out T>.copyInto(destination: Array<T>, destinationOffset: Int, startIndex: Int, endIndex: Int): Array<T> {
      System.arraycopy(`$this$copyInto`, startIndex, destination, destinationOffset, endIndex - startIndex);
      return (T[])destination;
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   public fun ByteArray.copyInto(destination: ByteArray, destinationOffset: Int, startIndex: Int, endIndex: Int): ByteArray {
      System.arraycopy(`$this$copyInto`, startIndex, destination, destinationOffset, endIndex - startIndex);
      return destination;
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   public fun ShortArray.copyInto(destination: ShortArray, destinationOffset: Int, startIndex: Int, endIndex: Int): ShortArray {
      System.arraycopy(`$this$copyInto`, startIndex, destination, destinationOffset, endIndex - startIndex);
      return destination;
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   public fun IntArray.copyInto(destination: IntArray, destinationOffset: Int, startIndex: Int, endIndex: Int): IntArray {
      System.arraycopy(`$this$copyInto`, startIndex, destination, destinationOffset, endIndex - startIndex);
      return destination;
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   public fun LongArray.copyInto(destination: LongArray, destinationOffset: Int, startIndex: Int, endIndex: Int): LongArray {
      System.arraycopy(`$this$copyInto`, startIndex, destination, destinationOffset, endIndex - startIndex);
      return destination;
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   public fun FloatArray.copyInto(destination: FloatArray, destinationOffset: Int, startIndex: Int, endIndex: Int): FloatArray {
      System.arraycopy(`$this$copyInto`, startIndex, destination, destinationOffset, endIndex - startIndex);
      return destination;
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   public fun DoubleArray.copyInto(destination: DoubleArray, destinationOffset: Int, startIndex: Int, endIndex: Int): DoubleArray {
      System.arraycopy(`$this$copyInto`, startIndex, destination, destinationOffset, endIndex - startIndex);
      return destination;
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   public fun BooleanArray.copyInto(destination: BooleanArray, destinationOffset: Int, startIndex: Int, endIndex: Int): BooleanArray {
      System.arraycopy(`$this$copyInto`, startIndex, destination, destinationOffset, endIndex - startIndex);
      return destination;
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   public fun CharArray.copyInto(destination: CharArray, destinationOffset: Int, startIndex: Int, endIndex: Int): CharArray {
      System.arraycopy(`$this$copyInto`, startIndex, destination, destinationOffset, endIndex - startIndex);
      return destination;
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<T>.copyOf(): Array<T> {
      var var10000: Array<Any> = Arrays.copyOf(`$this$copyOf`, `$this$copyOf`.length);
      return (T[])var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline fun ByteArray.copyOf(): ByteArray {
      var var10000: ByteArray = Arrays.copyOf(`$this$copyOf`, `$this$copyOf`.length);
      return var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline fun ShortArray.copyOf(): ShortArray {
      var var10000: ShortArray = Arrays.copyOf(`$this$copyOf`, `$this$copyOf`.length);
      return var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline fun IntArray.copyOf(): IntArray {
      var var10000: IntArray = Arrays.copyOf(`$this$copyOf`, `$this$copyOf`.length);
      return var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline fun LongArray.copyOf(): LongArray {
      var var10000: LongArray = Arrays.copyOf(`$this$copyOf`, `$this$copyOf`.length);
      return var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline fun FloatArray.copyOf(): FloatArray {
      var var10000: FloatArray = Arrays.copyOf(`$this$copyOf`, `$this$copyOf`.length);
      return var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline fun DoubleArray.copyOf(): DoubleArray {
      var var10000: DoubleArray = Arrays.copyOf(`$this$copyOf`, `$this$copyOf`.length);
      return var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline fun BooleanArray.copyOf(): BooleanArray {
      var var10000: BooleanArray = Arrays.copyOf(`$this$copyOf`, `$this$copyOf`.length);
      return var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline fun CharArray.copyOf(): CharArray {
      var var10000: CharArray = Arrays.copyOf(`$this$copyOf`, `$this$copyOf`.length);
      return var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline fun ByteArray.copyOf(newSize: Int): ByteArray {
      var var10000: ByteArray = Arrays.copyOf(`$this$copyOf`, newSize);
      return var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline fun ShortArray.copyOf(newSize: Int): ShortArray {
      var var10000: ShortArray = Arrays.copyOf(`$this$copyOf`, newSize);
      return var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline fun IntArray.copyOf(newSize: Int): IntArray {
      var var10000: IntArray = Arrays.copyOf(`$this$copyOf`, newSize);
      return var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline fun LongArray.copyOf(newSize: Int): LongArray {
      var var10000: LongArray = Arrays.copyOf(`$this$copyOf`, newSize);
      return var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline fun FloatArray.copyOf(newSize: Int): FloatArray {
      var var10000: FloatArray = Arrays.copyOf(`$this$copyOf`, newSize);
      return var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline fun DoubleArray.copyOf(newSize: Int): DoubleArray {
      var var10000: DoubleArray = Arrays.copyOf(`$this$copyOf`, newSize);
      return var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline fun BooleanArray.copyOf(newSize: Int): BooleanArray {
      var var10000: BooleanArray = Arrays.copyOf(`$this$copyOf`, newSize);
      return var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline fun CharArray.copyOf(newSize: Int): CharArray {
      var var10000: CharArray = Arrays.copyOf(`$this$copyOf`, newSize);
      return var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<T>.copyOf(newSize: Int): Array<T?> {
      var var10000: Array<Any> = Arrays.copyOf(`$this$copyOf`, newSize);
      return (T[])var10000;
   }

   @JvmName(name = "copyOfRangeInline")
   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<T>.copyOfRange(fromIndex: Int, toIndex: Int): Array<T> {
      var var10000: Array<Any>;
      if (PlatformImplementationsKt.apiVersionIsAtLeast(1, 3, 0)) {
         var10000 = ArraysKt.copyOfRange(`$this$copyOfRange`, fromIndex, toIndex);
      } else {
         if (toIndex > `$this$copyOfRange`.length) {
            throw new IndexOutOfBoundsException("toIndex: " + toIndex + ", size: " + `$this$copyOfRange`.length);
         }

         var10000 = Arrays.copyOfRange(`$this$copyOfRange`, fromIndex, toIndex);
      }

      return (T[])var10000;
   }

   @JvmName(name = "copyOfRangeInline")
   @InlineOnly
   @JvmStatic
   public inline fun ByteArray.copyOfRange(fromIndex: Int, toIndex: Int): ByteArray {
      var var10000: ByteArray;
      if (PlatformImplementationsKt.apiVersionIsAtLeast(1, 3, 0)) {
         var10000 = ArraysKt.copyOfRange(`$this$copyOfRange`, fromIndex, toIndex);
      } else {
         if (toIndex > `$this$copyOfRange`.length) {
            throw new IndexOutOfBoundsException("toIndex: " + toIndex + ", size: " + `$this$copyOfRange`.length);
         }

         var10000 = Arrays.copyOfRange(`$this$copyOfRange`, fromIndex, toIndex);
      }

      return var10000;
   }

   @JvmName(name = "copyOfRangeInline")
   @InlineOnly
   @JvmStatic
   public inline fun ShortArray.copyOfRange(fromIndex: Int, toIndex: Int): ShortArray {
      var var10000: ShortArray;
      if (PlatformImplementationsKt.apiVersionIsAtLeast(1, 3, 0)) {
         var10000 = ArraysKt.copyOfRange(`$this$copyOfRange`, fromIndex, toIndex);
      } else {
         if (toIndex > `$this$copyOfRange`.length) {
            throw new IndexOutOfBoundsException("toIndex: " + toIndex + ", size: " + `$this$copyOfRange`.length);
         }

         var10000 = Arrays.copyOfRange(`$this$copyOfRange`, fromIndex, toIndex);
      }

      return var10000;
   }

   @JvmName(name = "copyOfRangeInline")
   @InlineOnly
   @JvmStatic
   public inline fun IntArray.copyOfRange(fromIndex: Int, toIndex: Int): IntArray {
      var var10000: IntArray;
      if (PlatformImplementationsKt.apiVersionIsAtLeast(1, 3, 0)) {
         var10000 = ArraysKt.copyOfRange(`$this$copyOfRange`, fromIndex, toIndex);
      } else {
         if (toIndex > `$this$copyOfRange`.length) {
            throw new IndexOutOfBoundsException("toIndex: " + toIndex + ", size: " + `$this$copyOfRange`.length);
         }

         var10000 = Arrays.copyOfRange(`$this$copyOfRange`, fromIndex, toIndex);
      }

      return var10000;
   }

   @JvmName(name = "copyOfRangeInline")
   @InlineOnly
   @JvmStatic
   public inline fun LongArray.copyOfRange(fromIndex: Int, toIndex: Int): LongArray {
      var var10000: LongArray;
      if (PlatformImplementationsKt.apiVersionIsAtLeast(1, 3, 0)) {
         var10000 = ArraysKt.copyOfRange(`$this$copyOfRange`, fromIndex, toIndex);
      } else {
         if (toIndex > `$this$copyOfRange`.length) {
            throw new IndexOutOfBoundsException("toIndex: " + toIndex + ", size: " + `$this$copyOfRange`.length);
         }

         var10000 = Arrays.copyOfRange(`$this$copyOfRange`, fromIndex, toIndex);
      }

      return var10000;
   }

   @JvmName(name = "copyOfRangeInline")
   @InlineOnly
   @JvmStatic
   public inline fun FloatArray.copyOfRange(fromIndex: Int, toIndex: Int): FloatArray {
      var var10000: FloatArray;
      if (PlatformImplementationsKt.apiVersionIsAtLeast(1, 3, 0)) {
         var10000 = ArraysKt.copyOfRange(`$this$copyOfRange`, fromIndex, toIndex);
      } else {
         if (toIndex > `$this$copyOfRange`.length) {
            throw new IndexOutOfBoundsException("toIndex: " + toIndex + ", size: " + `$this$copyOfRange`.length);
         }

         var10000 = Arrays.copyOfRange(`$this$copyOfRange`, fromIndex, toIndex);
      }

      return var10000;
   }

   @JvmName(name = "copyOfRangeInline")
   @InlineOnly
   @JvmStatic
   public inline fun DoubleArray.copyOfRange(fromIndex: Int, toIndex: Int): DoubleArray {
      var var10000: DoubleArray;
      if (PlatformImplementationsKt.apiVersionIsAtLeast(1, 3, 0)) {
         var10000 = ArraysKt.copyOfRange(`$this$copyOfRange`, fromIndex, toIndex);
      } else {
         if (toIndex > `$this$copyOfRange`.length) {
            throw new IndexOutOfBoundsException("toIndex: " + toIndex + ", size: " + `$this$copyOfRange`.length);
         }

         var10000 = Arrays.copyOfRange(`$this$copyOfRange`, fromIndex, toIndex);
      }

      return var10000;
   }

   @JvmName(name = "copyOfRangeInline")
   @InlineOnly
   @JvmStatic
   public inline fun BooleanArray.copyOfRange(fromIndex: Int, toIndex: Int): BooleanArray {
      var var10000: BooleanArray;
      if (PlatformImplementationsKt.apiVersionIsAtLeast(1, 3, 0)) {
         var10000 = ArraysKt.copyOfRange(`$this$copyOfRange`, fromIndex, toIndex);
      } else {
         if (toIndex > `$this$copyOfRange`.length) {
            throw new IndexOutOfBoundsException("toIndex: " + toIndex + ", size: " + `$this$copyOfRange`.length);
         }

         var10000 = Arrays.copyOfRange(`$this$copyOfRange`, fromIndex, toIndex);
      }

      return var10000;
   }

   @JvmName(name = "copyOfRangeInline")
   @InlineOnly
   @JvmStatic
   public inline fun CharArray.copyOfRange(fromIndex: Int, toIndex: Int): CharArray {
      var var10000: CharArray;
      if (PlatformImplementationsKt.apiVersionIsAtLeast(1, 3, 0)) {
         var10000 = ArraysKt.copyOfRange(`$this$copyOfRange`, fromIndex, toIndex);
      } else {
         if (toIndex > `$this$copyOfRange`.length) {
            throw new IndexOutOfBoundsException("toIndex: " + toIndex + ", size: " + `$this$copyOfRange`.length);
         }

         var10000 = Arrays.copyOfRange(`$this$copyOfRange`, fromIndex, toIndex);
      }

      return var10000;
   }

   @SinceKotlin(version = "1.3")
   @PublishedApi
   @JvmName(name = "copyOfRange")
   @JvmStatic
   internal fun <T> Array<T>.copyOfRangeImpl(fromIndex: Int, toIndex: Int): Array<T> {
      ArraysKt.copyOfRangeToIndexCheck(toIndex, `$this$copyOfRangeImpl`.length);
      var var10000: Array<Any> = Arrays.copyOfRange(`$this$copyOfRangeImpl`, fromIndex, toIndex);
      return (T[])var10000;
   }

   @SinceKotlin(version = "1.3")
   @PublishedApi
   @JvmName(name = "copyOfRange")
   @JvmStatic
   internal fun ByteArray.copyOfRangeImpl(fromIndex: Int, toIndex: Int): ByteArray {
      ArraysKt.copyOfRangeToIndexCheck(toIndex, `$this$copyOfRangeImpl`.length);
      var var10000: ByteArray = Arrays.copyOfRange(`$this$copyOfRangeImpl`, fromIndex, toIndex);
      return var10000;
   }

   @SinceKotlin(version = "1.3")
   @PublishedApi
   @JvmName(name = "copyOfRange")
   @JvmStatic
   internal fun ShortArray.copyOfRangeImpl(fromIndex: Int, toIndex: Int): ShortArray {
      ArraysKt.copyOfRangeToIndexCheck(toIndex, `$this$copyOfRangeImpl`.length);
      var var10000: ShortArray = Arrays.copyOfRange(`$this$copyOfRangeImpl`, fromIndex, toIndex);
      return var10000;
   }

   @SinceKotlin(version = "1.3")
   @PublishedApi
   @JvmName(name = "copyOfRange")
   @JvmStatic
   internal fun IntArray.copyOfRangeImpl(fromIndex: Int, toIndex: Int): IntArray {
      ArraysKt.copyOfRangeToIndexCheck(toIndex, `$this$copyOfRangeImpl`.length);
      var var10000: IntArray = Arrays.copyOfRange(`$this$copyOfRangeImpl`, fromIndex, toIndex);
      return var10000;
   }

   @SinceKotlin(version = "1.3")
   @PublishedApi
   @JvmName(name = "copyOfRange")
   @JvmStatic
   internal fun LongArray.copyOfRangeImpl(fromIndex: Int, toIndex: Int): LongArray {
      ArraysKt.copyOfRangeToIndexCheck(toIndex, `$this$copyOfRangeImpl`.length);
      var var10000: LongArray = Arrays.copyOfRange(`$this$copyOfRangeImpl`, fromIndex, toIndex);
      return var10000;
   }

   @SinceKotlin(version = "1.3")
   @PublishedApi
   @JvmName(name = "copyOfRange")
   @JvmStatic
   internal fun FloatArray.copyOfRangeImpl(fromIndex: Int, toIndex: Int): FloatArray {
      ArraysKt.copyOfRangeToIndexCheck(toIndex, `$this$copyOfRangeImpl`.length);
      var var10000: FloatArray = Arrays.copyOfRange(`$this$copyOfRangeImpl`, fromIndex, toIndex);
      return var10000;
   }

   @SinceKotlin(version = "1.3")
   @PublishedApi
   @JvmName(name = "copyOfRange")
   @JvmStatic
   internal fun DoubleArray.copyOfRangeImpl(fromIndex: Int, toIndex: Int): DoubleArray {
      ArraysKt.copyOfRangeToIndexCheck(toIndex, `$this$copyOfRangeImpl`.length);
      var var10000: DoubleArray = Arrays.copyOfRange(`$this$copyOfRangeImpl`, fromIndex, toIndex);
      return var10000;
   }

   @SinceKotlin(version = "1.3")
   @PublishedApi
   @JvmName(name = "copyOfRange")
   @JvmStatic
   internal fun BooleanArray.copyOfRangeImpl(fromIndex: Int, toIndex: Int): BooleanArray {
      ArraysKt.copyOfRangeToIndexCheck(toIndex, `$this$copyOfRangeImpl`.length);
      var var10000: BooleanArray = Arrays.copyOfRange(`$this$copyOfRangeImpl`, fromIndex, toIndex);
      return var10000;
   }

   @SinceKotlin(version = "1.3")
   @PublishedApi
   @JvmName(name = "copyOfRange")
   @JvmStatic
   internal fun CharArray.copyOfRangeImpl(fromIndex: Int, toIndex: Int): CharArray {
      ArraysKt.copyOfRangeToIndexCheck(toIndex, `$this$copyOfRangeImpl`.length);
      var var10000: CharArray = Arrays.copyOfRange(`$this$copyOfRangeImpl`, fromIndex, toIndex);
      return var10000;
   }

   @JvmStatic
   public fun <T> Array<T>.fill(element: T, fromIndex: Int, toIndex: Int) {
      Arrays.fill(`$this$fill`, fromIndex, toIndex, element);
   }

   @JvmStatic
   public fun ByteArray.fill(element: Byte, fromIndex: Int, toIndex: Int) {
      Arrays.fill(`$this$fill`, fromIndex, toIndex, element);
   }

   @JvmStatic
   public fun ShortArray.fill(element: Short, fromIndex: Int, toIndex: Int) {
      Arrays.fill(`$this$fill`, fromIndex, toIndex, element);
   }

   @JvmStatic
   public fun IntArray.fill(element: Int, fromIndex: Int, toIndex: Int) {
      Arrays.fill(`$this$fill`, fromIndex, toIndex, element);
   }

   @JvmStatic
   public fun LongArray.fill(element: Long, fromIndex: Int, toIndex: Int) {
      Arrays.fill(`$this$fill`, fromIndex, toIndex, element);
   }

   @JvmStatic
   public fun FloatArray.fill(element: Float, fromIndex: Int, toIndex: Int) {
      Arrays.fill(`$this$fill`, fromIndex, toIndex, element);
   }

   @JvmStatic
   public fun DoubleArray.fill(element: Double, fromIndex: Int, toIndex: Int) {
      Arrays.fill(`$this$fill`, fromIndex, toIndex, element);
   }

   @JvmStatic
   public fun BooleanArray.fill(element: Boolean, fromIndex: Int, toIndex: Int) {
      Arrays.fill(`$this$fill`, fromIndex, toIndex, element);
   }

   @JvmStatic
   public fun CharArray.fill(element: Char, fromIndex: Int, toIndex: Int) {
      Arrays.fill(`$this$fill`, fromIndex, toIndex, element);
   }

   @JvmStatic
   public operator fun <T> Array<T>.plus(element: T): Array<T> {
      var result: Array<Any> = Arrays.copyOf(`$this$plus`, `$this$plus`.length + 1);
      result[`$this$plus`.length] = element;
      return (T[])result;
   }

   @JvmStatic
   public operator fun ByteArray.plus(element: Byte): ByteArray {
      var result: ByteArray = Arrays.copyOf(`$this$plus`, `$this$plus`.length + 1);
      result[`$this$plus`.length] = element;
      return result;
   }

   @JvmStatic
   public operator fun ShortArray.plus(element: Short): ShortArray {
      var result: ShortArray = Arrays.copyOf(`$this$plus`, `$this$plus`.length + 1);
      result[`$this$plus`.length] = element;
      return result;
   }

   @JvmStatic
   public operator fun IntArray.plus(element: Int): IntArray {
      var result: IntArray = Arrays.copyOf(`$this$plus`, `$this$plus`.length + 1);
      result[`$this$plus`.length] = element;
      return result;
   }

   @JvmStatic
   public operator fun LongArray.plus(element: Long): LongArray {
      var result: LongArray = Arrays.copyOf(`$this$plus`, `$this$plus`.length + 1);
      result[`$this$plus`.length] = element;
      return result;
   }

   @JvmStatic
   public operator fun FloatArray.plus(element: Float): FloatArray {
      var result: FloatArray = Arrays.copyOf(`$this$plus`, `$this$plus`.length + 1);
      result[`$this$plus`.length] = element;
      return result;
   }

   @JvmStatic
   public operator fun DoubleArray.plus(element: Double): DoubleArray {
      var result: DoubleArray = Arrays.copyOf(`$this$plus`, `$this$plus`.length + 1);
      result[`$this$plus`.length] = element;
      return result;
   }

   @JvmStatic
   public operator fun BooleanArray.plus(element: Boolean): BooleanArray {
      var result: BooleanArray = Arrays.copyOf(`$this$plus`, `$this$plus`.length + 1);
      result[`$this$plus`.length] = element;
      return result;
   }

   @JvmStatic
   public operator fun CharArray.plus(element: Char): CharArray {
      var result: CharArray = Arrays.copyOf(`$this$plus`, `$this$plus`.length + 1);
      result[`$this$plus`.length] = element;
      return result;
   }

   @JvmStatic
   public operator fun <T> Array<T>.plus(elements: Collection<T>): Array<T> {
      var index: Int = `$this$plus`.length;
      var result: Array<Any> = Arrays.copyOf(`$this$plus`, `$this$plus`.length + elements.size());

      for (Object element : elements) {
         result[index++] = element;
      }

      return (T[])result;
   }

   @JvmStatic
   public operator fun ByteArray.plus(elements: Collection<Byte>): ByteArray {
      var index: Int = `$this$plus`.length;
      var result: ByteArray = Arrays.copyOf(`$this$plus`, `$this$plus`.length + elements.size());
      var var4: java.util.Iterator = elements.iterator();

      while (var4.hasNext()) {
         result[index++] = (var4.next() as java.lang.Number).byteValue();
      }

      return result;
   }

   @JvmStatic
   public operator fun ShortArray.plus(elements: Collection<Short>): ShortArray {
      var index: Int = `$this$plus`.length;
      var result: ShortArray = Arrays.copyOf(`$this$plus`, `$this$plus`.length + elements.size());
      var var4: java.util.Iterator = elements.iterator();

      while (var4.hasNext()) {
         result[index++] = (var4.next() as java.lang.Number).shortValue();
      }

      return result;
   }

   @JvmStatic
   public operator fun IntArray.plus(elements: Collection<Int>): IntArray {
      var index: Int = `$this$plus`.length;
      var result: IntArray = Arrays.copyOf(`$this$plus`, `$this$plus`.length + elements.size());
      var var4: java.util.Iterator = elements.iterator();

      while (var4.hasNext()) {
         result[index++] = (var4.next() as java.lang.Number).intValue();
      }

      return result;
   }

   @JvmStatic
   public operator fun LongArray.plus(elements: Collection<Long>): LongArray {
      var index: Int = `$this$plus`.length;
      var result: LongArray = Arrays.copyOf(`$this$plus`, `$this$plus`.length + elements.size());
      var var4: java.util.Iterator = elements.iterator();

      while (var4.hasNext()) {
         result[index++] = (var4.next() as java.lang.Number).longValue();
      }

      return result;
   }

   @JvmStatic
   public operator fun FloatArray.plus(elements: Collection<Float>): FloatArray {
      var index: Int = `$this$plus`.length;
      var result: FloatArray = Arrays.copyOf(`$this$plus`, `$this$plus`.length + elements.size());
      var var4: java.util.Iterator = elements.iterator();

      while (var4.hasNext()) {
         result[index++] = (var4.next() as java.lang.Number).floatValue();
      }

      return result;
   }

   @JvmStatic
   public operator fun DoubleArray.plus(elements: Collection<Double>): DoubleArray {
      var index: Int = `$this$plus`.length;
      var result: DoubleArray = Arrays.copyOf(`$this$plus`, `$this$plus`.length + elements.size());
      var var4: java.util.Iterator = elements.iterator();

      while (var4.hasNext()) {
         result[index++] = (var4.next() as java.lang.Number).doubleValue();
      }

      return result;
   }

   @JvmStatic
   public operator fun BooleanArray.plus(elements: Collection<Boolean>): BooleanArray {
      var index: Int = `$this$plus`.length;
      var result: BooleanArray = Arrays.copyOf(`$this$plus`, `$this$plus`.length + elements.size());

      for (boolean element : elements) {
         result[index++] = element;
      }

      return result;
   }

   @JvmStatic
   public operator fun CharArray.plus(elements: Collection<Char>): CharArray {
      var index: Int = `$this$plus`.length;
      var result: CharArray = Arrays.copyOf(`$this$plus`, `$this$plus`.length + elements.size());

      for (char element : elements) {
         result[index++] = element;
      }

      return result;
   }

   @JvmStatic
   public operator fun <T> Array<T>.plus(elements: Array<out T>): Array<T> {
      var result: Array<Any> = Arrays.copyOf(`$this$plus`, `$this$plus`.length + elements.length);
      System.arraycopy(elements, 0, result, `$this$plus`.length, elements.length);
      return (T[])result;
   }

   @JvmStatic
   public operator fun ByteArray.plus(elements: ByteArray): ByteArray {
      var result: ByteArray = Arrays.copyOf(`$this$plus`, `$this$plus`.length + elements.length);
      System.arraycopy(elements, 0, result, `$this$plus`.length, elements.length);
      return result;
   }

   @JvmStatic
   public operator fun ShortArray.plus(elements: ShortArray): ShortArray {
      var result: ShortArray = Arrays.copyOf(`$this$plus`, `$this$plus`.length + elements.length);
      System.arraycopy(elements, 0, result, `$this$plus`.length, elements.length);
      return result;
   }

   @JvmStatic
   public operator fun IntArray.plus(elements: IntArray): IntArray {
      var result: IntArray = Arrays.copyOf(`$this$plus`, `$this$plus`.length + elements.length);
      System.arraycopy(elements, 0, result, `$this$plus`.length, elements.length);
      return result;
   }

   @JvmStatic
   public operator fun LongArray.plus(elements: LongArray): LongArray {
      var result: LongArray = Arrays.copyOf(`$this$plus`, `$this$plus`.length + elements.length);
      System.arraycopy(elements, 0, result, `$this$plus`.length, elements.length);
      return result;
   }

   @JvmStatic
   public operator fun FloatArray.plus(elements: FloatArray): FloatArray {
      var result: FloatArray = Arrays.copyOf(`$this$plus`, `$this$plus`.length + elements.length);
      System.arraycopy(elements, 0, result, `$this$plus`.length, elements.length);
      return result;
   }

   @JvmStatic
   public operator fun DoubleArray.plus(elements: DoubleArray): DoubleArray {
      var result: DoubleArray = Arrays.copyOf(`$this$plus`, `$this$plus`.length + elements.length);
      System.arraycopy(elements, 0, result, `$this$plus`.length, elements.length);
      return result;
   }

   @JvmStatic
   public operator fun BooleanArray.plus(elements: BooleanArray): BooleanArray {
      var result: BooleanArray = Arrays.copyOf(`$this$plus`, `$this$plus`.length + elements.length);
      System.arraycopy(elements, 0, result, `$this$plus`.length, elements.length);
      return result;
   }

   @JvmStatic
   public operator fun CharArray.plus(elements: CharArray): CharArray {
      var result: CharArray = Arrays.copyOf(`$this$plus`, `$this$plus`.length + elements.length);
      System.arraycopy(elements, 0, result, `$this$plus`.length, elements.length);
      return result;
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<T>.plusElement(element: T): Array<T> {
      return (T[])ArraysKt.plus(`$this$plusElement`, element);
   }

   @JvmStatic
   public fun IntArray.sort() {
      if (`$this$sort`.length > 1) {
         Arrays.sort(`$this$sort`);
      }
   }

   @JvmStatic
   public fun LongArray.sort() {
      if (`$this$sort`.length > 1) {
         Arrays.sort(`$this$sort`);
      }
   }

   @JvmStatic
   public fun ByteArray.sort() {
      if (`$this$sort`.length > 1) {
         Arrays.sort(`$this$sort`);
      }
   }

   @JvmStatic
   public fun ShortArray.sort() {
      if (`$this$sort`.length > 1) {
         Arrays.sort(`$this$sort`);
      }
   }

   @JvmStatic
   public fun DoubleArray.sort() {
      if (`$this$sort`.length > 1) {
         Arrays.sort(`$this$sort`);
      }
   }

   @JvmStatic
   public fun FloatArray.sort() {
      if (`$this$sort`.length > 1) {
         Arrays.sort(`$this$sort`);
      }
   }

   @JvmStatic
   public fun CharArray.sort() {
      if (`$this$sort`.length > 1) {
         Arrays.sort(`$this$sort`);
      }
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T : Comparable<T>> Array<out T>.sort() {
      ArraysKt.sort(`$this$sort`);
   }

   @JvmStatic
   public fun <T> Array<out T>.sort() {
      if (`$this$sort`.length > 1) {
         Arrays.sort(`$this$sort`);
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun <T : Comparable<T>> Array<out T>.sort(fromIndex: Int, toIndex: Int) {
      Arrays.sort((Object[])`$this$sort`, fromIndex, toIndex);
   }

   @JvmStatic
   public fun ByteArray.sort(fromIndex: Int, toIndex: Int) {
      Arrays.sort(`$this$sort`, fromIndex, toIndex);
   }

   @JvmStatic
   public fun ShortArray.sort(fromIndex: Int, toIndex: Int) {
      Arrays.sort(`$this$sort`, fromIndex, toIndex);
   }

   @JvmStatic
   public fun IntArray.sort(fromIndex: Int, toIndex: Int) {
      Arrays.sort(`$this$sort`, fromIndex, toIndex);
   }

   @JvmStatic
   public fun LongArray.sort(fromIndex: Int, toIndex: Int) {
      Arrays.sort(`$this$sort`, fromIndex, toIndex);
   }

   @JvmStatic
   public fun FloatArray.sort(fromIndex: Int, toIndex: Int) {
      Arrays.sort(`$this$sort`, fromIndex, toIndex);
   }

   @JvmStatic
   public fun DoubleArray.sort(fromIndex: Int, toIndex: Int) {
      Arrays.sort(`$this$sort`, fromIndex, toIndex);
   }

   @JvmStatic
   public fun CharArray.sort(fromIndex: Int, toIndex: Int) {
      Arrays.sort(`$this$sort`, fromIndex, toIndex);
   }

   @JvmStatic
   public fun <T> Array<out T>.sort(fromIndex: Int, toIndex: Int) {
      Arrays.sort(`$this$sort`, fromIndex, toIndex);
   }

   @JvmStatic
   public fun <T> Array<out T>.sortWith(comparator: Comparator<in T>) {
      if (`$this$sortWith`.length > 1) {
         Arrays.sort(`$this$sortWith`, comparator);
      }
   }

   @JvmStatic
   public fun <T> Array<out T>.sortWith(comparator: Comparator<in T>, fromIndex: Int, toIndex: Int) {
      Arrays.sort(`$this$sortWith`, fromIndex, toIndex, comparator);
   }

   @JvmStatic
   public fun ByteArray.toTypedArray(): Array<Byte> {
      var result: Array<java.lang.Byte> = new java.lang.Byte[`$this$toTypedArray`.length];
      var index: Int = 0;

      for (int var3 = $this$toTypedArray.length; index < var3; index++) {
         result[index] = `$this$toTypedArray`[index];
      }

      return result;
   }

   @JvmStatic
   public fun ShortArray.toTypedArray(): Array<Short> {
      var result: Array<java.lang.Short> = new java.lang.Short[`$this$toTypedArray`.length];
      var index: Int = 0;

      for (int var3 = $this$toTypedArray.length; index < var3; index++) {
         result[index] = `$this$toTypedArray`[index];
      }

      return result;
   }

   @JvmStatic
   public fun IntArray.toTypedArray(): Array<Int> {
      var result: Array<Int> = new Integer[`$this$toTypedArray`.length];
      var index: Int = 0;

      for (int var3 = $this$toTypedArray.length; index < var3; index++) {
         result[index] = `$this$toTypedArray`[index];
      }

      return result;
   }

   @JvmStatic
   public fun LongArray.toTypedArray(): Array<Long> {
      var result: Array<java.lang.Long> = new java.lang.Long[`$this$toTypedArray`.length];
      var index: Int = 0;

      for (int var3 = $this$toTypedArray.length; index < var3; index++) {
         result[index] = `$this$toTypedArray`[index];
      }

      return result;
   }

   @JvmStatic
   public fun FloatArray.toTypedArray(): Array<Float> {
      var result: Array<java.lang.Float> = new java.lang.Float[`$this$toTypedArray`.length];
      var index: Int = 0;

      for (int var3 = $this$toTypedArray.length; index < var3; index++) {
         result[index] = `$this$toTypedArray`[index];
      }

      return result;
   }

   @JvmStatic
   public fun DoubleArray.toTypedArray(): Array<Double> {
      var result: Array<java.lang.Double> = new java.lang.Double[`$this$toTypedArray`.length];
      var index: Int = 0;

      for (int var3 = $this$toTypedArray.length; index < var3; index++) {
         result[index] = `$this$toTypedArray`[index];
      }

      return result;
   }

   @JvmStatic
   public fun BooleanArray.toTypedArray(): Array<Boolean> {
      var result: Array<java.lang.Boolean> = new java.lang.Boolean[`$this$toTypedArray`.length];
      var index: Int = 0;

      for (int var3 = $this$toTypedArray.length; index < var3; index++) {
         result[index] = `$this$toTypedArray`[index];
      }

      return result;
   }

   @JvmStatic
   public fun CharArray.toTypedArray(): Array<Char> {
      var result: Array<Character> = new Character[`$this$toTypedArray`.length];
      var index: Int = 0;

      for (int var3 = $this$toTypedArray.length; index < var3; index++) {
         result[index] = `$this$toTypedArray`[index];
      }

      return result;
   }

   @JvmStatic
   public fun <T : Comparable<T>> Array<out T>.toSortedSet(): SortedSet<T> {
      return ArraysKt.toCollection(`$this$toSortedSet`, new TreeSet()) as SortedSet<T>;
   }

   @JvmStatic
   public fun ByteArray.toSortedSet(): SortedSet<Byte> {
      return ArraysKt.toCollection(`$this$toSortedSet`, new TreeSet<>());
   }

   @JvmStatic
   public fun ShortArray.toSortedSet(): SortedSet<Short> {
      return ArraysKt.toCollection(`$this$toSortedSet`, new TreeSet<>());
   }

   @JvmStatic
   public fun IntArray.toSortedSet(): SortedSet<Int> {
      return ArraysKt.toCollection(`$this$toSortedSet`, new TreeSet<>());
   }

   @JvmStatic
   public fun LongArray.toSortedSet(): SortedSet<Long> {
      return ArraysKt.toCollection(`$this$toSortedSet`, new TreeSet<>());
   }

   @JvmStatic
   public fun FloatArray.toSortedSet(): SortedSet<Float> {
      return ArraysKt.toCollection(`$this$toSortedSet`, new TreeSet<>());
   }

   @JvmStatic
   public fun DoubleArray.toSortedSet(): SortedSet<Double> {
      return ArraysKt.toCollection(`$this$toSortedSet`, new TreeSet<>());
   }

   @JvmStatic
   public fun BooleanArray.toSortedSet(): SortedSet<Boolean> {
      return ArraysKt.toCollection(`$this$toSortedSet`, new TreeSet<>());
   }

   @JvmStatic
   public fun CharArray.toSortedSet(): SortedSet<Char> {
      return ArraysKt.toCollection(`$this$toSortedSet`, new TreeSet<>());
   }

   @JvmStatic
   public fun <T> Array<out T>.toSortedSet(comparator: Comparator<in T>): SortedSet<T> {
      return ArraysKt.toCollection(`$this$toSortedSet`, new TreeSet(comparator)) as SortedSet<T>;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfBigDecimal")
   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<out T>.sumOf(selector: (T) -> BigDecimal): BigDecimal {
      var var10000: BigDecimal = BigDecimal.valueOf(0L);
      var sum: BigDecimal = var10000;

      for (Object element : $this$sumOf) {
         var10000 = sum.add(selector.invoke(element) as BigDecimal);
         sum = var10000;
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfBigDecimal")
   @InlineOnly
   @JvmStatic
   public inline fun ByteArray.sumOf(selector: (Byte) -> BigDecimal): BigDecimal {
      var var10000: BigDecimal = BigDecimal.valueOf(0L);
      var sum: BigDecimal = var10000;

      for (byte element : $this$sumOf) {
         var10000 = sum.add(selector.invoke(element) as BigDecimal);
         sum = var10000;
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfBigDecimal")
   @InlineOnly
   @JvmStatic
   public inline fun ShortArray.sumOf(selector: (Short) -> BigDecimal): BigDecimal {
      var var10000: BigDecimal = BigDecimal.valueOf(0L);
      var sum: BigDecimal = var10000;

      for (short element : $this$sumOf) {
         var10000 = sum.add(selector.invoke(element) as BigDecimal);
         sum = var10000;
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfBigDecimal")
   @InlineOnly
   @JvmStatic
   public inline fun IntArray.sumOf(selector: (Int) -> BigDecimal): BigDecimal {
      var var10000: BigDecimal = BigDecimal.valueOf(0L);
      var sum: BigDecimal = var10000;

      for (int element : $this$sumOf) {
         var10000 = sum.add(selector.invoke(element) as BigDecimal);
         sum = var10000;
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfBigDecimal")
   @InlineOnly
   @JvmStatic
   public inline fun LongArray.sumOf(selector: (Long) -> BigDecimal): BigDecimal {
      var var10000: BigDecimal = BigDecimal.valueOf(0L);
      var sum: BigDecimal = var10000;

      for (long element : $this$sumOf) {
         var10000 = sum.add(selector.invoke(element) as BigDecimal);
         sum = var10000;
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfBigDecimal")
   @InlineOnly
   @JvmStatic
   public inline fun FloatArray.sumOf(selector: (Float) -> BigDecimal): BigDecimal {
      var var10000: BigDecimal = BigDecimal.valueOf(0L);
      var sum: BigDecimal = var10000;

      for (float element : $this$sumOf) {
         var10000 = sum.add(selector.invoke(element) as BigDecimal);
         sum = var10000;
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfBigDecimal")
   @InlineOnly
   @JvmStatic
   public inline fun DoubleArray.sumOf(selector: (Double) -> BigDecimal): BigDecimal {
      var var10000: BigDecimal = BigDecimal.valueOf(0L);
      var sum: BigDecimal = var10000;

      for (double element : $this$sumOf) {
         var10000 = sum.add(selector.invoke(element) as BigDecimal);
         sum = var10000;
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfBigDecimal")
   @InlineOnly
   @JvmStatic
   public inline fun BooleanArray.sumOf(selector: (Boolean) -> BigDecimal): BigDecimal {
      var var10000: BigDecimal = BigDecimal.valueOf(0L);
      var sum: BigDecimal = var10000;

      for (boolean element : $this$sumOf) {
         var10000 = sum.add(selector.invoke(element) as BigDecimal);
         sum = var10000;
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfBigDecimal")
   @InlineOnly
   @JvmStatic
   public inline fun CharArray.sumOf(selector: (Char) -> BigDecimal): BigDecimal {
      var var10000: BigDecimal = BigDecimal.valueOf(0L);
      var sum: BigDecimal = var10000;

      for (char element : $this$sumOf) {
         var10000 = sum.add(selector.invoke(element) as BigDecimal);
         sum = var10000;
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfBigInteger")
   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<out T>.sumOf(selector: (T) -> BigInteger): BigInteger {
      var var10000: BigInteger = BigInteger.valueOf(0L);
      var sum: BigInteger = var10000;

      for (Object element : $this$sumOf) {
         var10000 = sum.add(selector.invoke(element) as BigInteger);
         sum = var10000;
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfBigInteger")
   @InlineOnly
   @JvmStatic
   public inline fun ByteArray.sumOf(selector: (Byte) -> BigInteger): BigInteger {
      var var10000: BigInteger = BigInteger.valueOf(0L);
      var sum: BigInteger = var10000;

      for (byte element : $this$sumOf) {
         var10000 = sum.add(selector.invoke(element) as BigInteger);
         sum = var10000;
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfBigInteger")
   @InlineOnly
   @JvmStatic
   public inline fun ShortArray.sumOf(selector: (Short) -> BigInteger): BigInteger {
      var var10000: BigInteger = BigInteger.valueOf(0L);
      var sum: BigInteger = var10000;

      for (short element : $this$sumOf) {
         var10000 = sum.add(selector.invoke(element) as BigInteger);
         sum = var10000;
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfBigInteger")
   @InlineOnly
   @JvmStatic
   public inline fun IntArray.sumOf(selector: (Int) -> BigInteger): BigInteger {
      var var10000: BigInteger = BigInteger.valueOf(0L);
      var sum: BigInteger = var10000;

      for (int element : $this$sumOf) {
         var10000 = sum.add(selector.invoke(element) as BigInteger);
         sum = var10000;
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfBigInteger")
   @InlineOnly
   @JvmStatic
   public inline fun LongArray.sumOf(selector: (Long) -> BigInteger): BigInteger {
      var var10000: BigInteger = BigInteger.valueOf(0L);
      var sum: BigInteger = var10000;

      for (long element : $this$sumOf) {
         var10000 = sum.add(selector.invoke(element) as BigInteger);
         sum = var10000;
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfBigInteger")
   @InlineOnly
   @JvmStatic
   public inline fun FloatArray.sumOf(selector: (Float) -> BigInteger): BigInteger {
      var var10000: BigInteger = BigInteger.valueOf(0L);
      var sum: BigInteger = var10000;

      for (float element : $this$sumOf) {
         var10000 = sum.add(selector.invoke(element) as BigInteger);
         sum = var10000;
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfBigInteger")
   @InlineOnly
   @JvmStatic
   public inline fun DoubleArray.sumOf(selector: (Double) -> BigInteger): BigInteger {
      var var10000: BigInteger = BigInteger.valueOf(0L);
      var sum: BigInteger = var10000;

      for (double element : $this$sumOf) {
         var10000 = sum.add(selector.invoke(element) as BigInteger);
         sum = var10000;
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfBigInteger")
   @InlineOnly
   @JvmStatic
   public inline fun BooleanArray.sumOf(selector: (Boolean) -> BigInteger): BigInteger {
      var var10000: BigInteger = BigInteger.valueOf(0L);
      var sum: BigInteger = var10000;

      for (boolean element : $this$sumOf) {
         var10000 = sum.add(selector.invoke(element) as BigInteger);
         sum = var10000;
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfBigInteger")
   @InlineOnly
   @JvmStatic
   public inline fun CharArray.sumOf(selector: (Char) -> BigInteger): BigInteger {
      var var10000: BigInteger = BigInteger.valueOf(0L);
      var sum: BigInteger = var10000;

      for (char element : $this$sumOf) {
         var10000 = sum.add(selector.invoke(element) as BigInteger);
         sum = var10000;
      }

      return sum;
   }

   open fun ArraysKt___ArraysJvmKt() {
   }
}
