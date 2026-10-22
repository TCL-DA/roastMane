package kotlin.text

import java.util.ArrayList
import java.util.Comparator
import java.util.HashSet
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.NoSuchElementException
import kotlin.internal.InlineOnly
import kotlin.jvm.functions.Function0
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.random.Random
import org.jetbrains.annotations.NotNull

@SourceDebugExtension(["SMAP\n_Strings.kt\nKotlin\n*S Kotlin\n*F\n+ 1 _Strings.kt\nkotlin/text/StringsKt___StringsKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n*L\n1#1,2486:1\n126#1,2:2487\n214#1,5:2489\n502#1,5:2495\n502#1,5:2500\n462#1:2505\n1183#1,2:2506\n463#1,2:2508\n1185#1:2510\n465#1:2511\n462#1:2512\n1183#1,2:2513\n463#1,2:2515\n1185#1:2517\n465#1:2518\n1183#1,3:2519\n492#1,2:2522\n492#1,2:2524\n750#1,4:2526\n719#1,4:2530\n735#1,4:2534\n782#1,4:2538\n882#1,5:2542\n923#1,3:2547\n926#1,3:2557\n941#1,3:2560\n944#1,3:2570\n1041#1,3:2587\n1011#1,4:2590\n1000#1:2594\n1183#1,2:2595\n1185#1:2598\n1001#1:2599\n1183#1,3:2600\n1032#1:2603\n1174#1:2604\n1175#1:2606\n1033#1:2607\n1174#1,2:2608\n1183#1,3:2610\n1982#1,2:2613\n1984#1,6:2616\n2006#1,2:2622\n2008#1,6:2625\n2431#1,6:2631\n2461#1,7:2637\n1#2:2494\n1#2:2597\n1#2:2605\n1#2:2615\n1#2:2624\n372#3,7:2550\n372#3,7:2563\n372#3,7:2573\n372#3,7:2580\n*S KotlinDebug\n*F\n+ 1 _Strings.kt\nkotlin/text/StringsKt___StringsKt\n*L\n52#1:2487,2\n62#1:2489,5\n420#1:2495,5\n429#1:2500,5\n440#1:2505\n440#1:2506,2\n440#1:2508,2\n440#1:2510\n440#1:2511\n451#1:2512\n451#1:2513,2\n451#1:2515,2\n451#1:2517\n451#1:2518\n462#1:2519,3\n474#1:2522,2\n483#1:2524,2\n677#1:2526,4\n692#1:2530,4\n706#1:2534,4\n769#1:2538,4\n842#1:2542,5\n898#1:2547,3\n898#1:2557,3\n911#1:2560,3\n911#1:2570,3\n970#1:2587,3\n980#1:2590,4\n990#1:2594\n990#1:2595,2\n990#1:2598\n990#1:2599\n1000#1:2600,3\n1024#1:2603\n1024#1:2604\n1024#1:2606\n1024#1:2607\n1032#1:2608,2\n1786#1:2610,3\n2077#1:2613,2\n2077#1:2616,6\n2095#1:2622,2\n2095#1:2625,6\n2420#1:2631,6\n2448#1:2637,7\n990#1:2597\n1024#1:2605\n2077#1:2615\n2095#1:2624\n898#1:2550,7\n911#1:2563,7\n925#1:2573,7\n943#1:2580,7\n*E\n"])
// $VF: Class flags could not be determined
internal class StringsKt___StringsKt : StringsKt___StringsJvmKt {
   @InlineOnly
   @JvmStatic
   public inline fun CharSequence.elementAtOrElse(index: Int, defaultValue: (Int) -> Char): Char {
      return if (index >= 0 && index <= StringsKt.getLastIndex(`$this$elementAtOrElse`))
         `$this$elementAtOrElse`.charAt(index)
         else
         defaultValue.invoke(index) as Character;
   }

   @InlineOnly
   @JvmStatic
   public inline fun CharSequence.elementAtOrNull(index: Int): Char? {
      return StringsKt.getOrNull(`$this$elementAtOrNull`, index);
   }

   @InlineOnly
   @JvmStatic
   public inline fun CharSequence.find(predicate: (Char) -> Boolean): Char? {
      var `$this$firstOrNull$iv`: java.lang.CharSequence = `$this$find`;
      var var4: Int = 0;

      var var10000: Character;
      while (true) {
         if (var4 >= `$this$firstOrNull$iv`.length()) {
            var10000 = null;
            break;
         }

         var `element$iv`: Char = `$this$firstOrNull$iv`.charAt(var4);
         if (predicate.invoke(`element$iv`) as java.lang.Boolean) {
            var10000 = `element$iv`;
            break;
         }

         var4++;
      }

      return var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline fun CharSequence.findLast(predicate: (Char) -> Boolean): Char? {
      var `$this$lastOrNull$iv`: java.lang.CharSequence = `$this$findLast`;
      var var4: Int = `$this$findLast`.length() + -1;
      if (0 <= var4) {
         do {
            var `element$iv`: Char = `$this$lastOrNull$iv`.charAt(var4--);
            if (predicate.invoke(`element$iv`) as java.lang.Boolean) {
               return `element$iv`;
            }
         } while (0 <= var4);
      }

      return null;
   }

   @JvmStatic
   public fun CharSequence.first(): Char {
      if (`$this$first`.length() == 0) {
         throw new NoSuchElementException("Char sequence is empty.");
      } else {
         return `$this$first`.charAt(0);
      }
   }

   @JvmStatic
   public inline fun CharSequence.first(predicate: (Char) -> Boolean): Char {
      for (int var3 = 0; var3 < $this$first.length(); var3++) {
         var element: Char = `$this$first`.charAt(var3);
         if (predicate.invoke(element) as java.lang.Boolean) {
            return element;
         }
      }

      throw new NoSuchElementException("Char sequence contains no character matching the predicate.");
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @JvmStatic
   public inline fun <R : Any> CharSequence.firstNotNullOf(transform: (Char) -> R?): R {
      var var2: java.lang.CharSequence = `$this$firstNotNullOf`;
      var var3: Int = 0;

      var var10000: Any;
      while (true) {
         if (var3 >= var2.length()) {
            var10000 = null;
            break;
         }

         var10000 = transform.invoke(var2.charAt(var3));
         if (var10000 != null) {
            break;
         }

         var3++;
      }

      if (var10000 == null) {
         throw new NoSuchElementException("No element of the char sequence was transformed to a non-null value.");
      } else {
         return (R)var10000;
      }
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @JvmStatic
   public inline fun <R : Any> CharSequence.firstNotNullOfOrNull(transform: (Char) -> R?): R? {
      for (int var2 = 0; var2 < $this$firstNotNullOfOrNull.length(); var2++) {
         var result: Any = transform.invoke(`$this$firstNotNullOfOrNull`.charAt(var2));
         if (result != null) {
            return (R)result;
         }
      }

      return null;
   }

   @JvmStatic
   public fun CharSequence.firstOrNull(): Char? {
      return if (`$this$firstOrNull`.length() == 0) null else `$this$firstOrNull`.charAt(0);
   }

   @JvmStatic
   public inline fun CharSequence.firstOrNull(predicate: (Char) -> Boolean): Char? {
      for (int var3 = 0; var3 < $this$firstOrNull.length(); var3++) {
         var element: Char = `$this$firstOrNull`.charAt(var3);
         if (predicate.invoke(element) as java.lang.Boolean) {
            return element;
         }
      }

      return null;
   }

   @InlineOnly
   @JvmStatic
   public inline fun CharSequence.getOrElse(index: Int, defaultValue: (Int) -> Char): Char {
      return if (index >= 0 && index <= StringsKt.getLastIndex(`$this$getOrElse`)) `$this$getOrElse`.charAt(index) else defaultValue.invoke(index) as Character;
   }

   @JvmStatic
   public fun CharSequence.getOrNull(index: Int): Char? {
      return if (index >= 0 && index <= StringsKt.getLastIndex(`$this$getOrNull`)) `$this$getOrNull`.charAt(index) else null;
   }

   @JvmStatic
   public inline fun CharSequence.indexOfFirst(predicate: (Char) -> Boolean): Int {
      var index: Int = 0;

      for (int var4 = $this$indexOfFirst.length(); index < var4; index++) {
         if (predicate.invoke(`$this$indexOfFirst`.charAt(index)) as java.lang.Boolean) {
            return index;
         }
      }

      return -1;
   }

   @JvmStatic
   public inline fun CharSequence.indexOfLast(predicate: (Char) -> Boolean): Int {
      var var3: Int = `$this$indexOfLast`.length() + -1;
      if (0 <= var3) {
         do {
            var index: Int = var3--;
            if (predicate.invoke(`$this$indexOfLast`.charAt(index)) as java.lang.Boolean) {
               return index;
            }
         } while (0 <= var3);
      }

      return -1;
   }

   @JvmStatic
   public fun CharSequence.last(): Char {
      if (`$this$last`.length() == 0) {
         throw new NoSuchElementException("Char sequence is empty.");
      } else {
         return `$this$last`.charAt(StringsKt.getLastIndex(`$this$last`));
      }
   }

   @JvmStatic
   public inline fun CharSequence.last(predicate: (Char) -> Boolean): Char {
      var var3: Int = `$this$last`.length() + -1;
      if (0 <= var3) {
         do {
            var element: Char = `$this$last`.charAt(var3--);
            if (predicate.invoke(element) as java.lang.Boolean) {
               return element;
            }
         } while (0 <= var3);
      }

      throw new NoSuchElementException("Char sequence contains no character matching the predicate.");
   }

   @JvmStatic
   public fun CharSequence.lastOrNull(): Char? {
      return if (`$this$lastOrNull`.length() == 0) null else `$this$lastOrNull`.charAt(`$this$lastOrNull`.length() - 1);
   }

   @JvmStatic
   public inline fun CharSequence.lastOrNull(predicate: (Char) -> Boolean): Char? {
      var var3: Int = `$this$lastOrNull`.length() + -1;
      if (0 <= var3) {
         do {
            var element: Char = `$this$lastOrNull`.charAt(var3--);
            if (predicate.invoke(element) as java.lang.Boolean) {
               return element;
            }
         } while (0 <= var3);
      }

      return null;
   }

   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   public inline fun CharSequence.random(): Char {
      return StringsKt.random(`$this$random`, Random.Default);
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   public fun CharSequence.random(random: Random): Char {
      if (`$this$random`.length() == 0) {
         throw new NoSuchElementException("Char sequence is empty.");
      } else {
         return `$this$random`.charAt(random.nextInt(`$this$random`.length()));
      }
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun CharSequence.randomOrNull(): Char? {
      return StringsKt.randomOrNull(`$this$randomOrNull`, Random.Default);
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun CharSequence.randomOrNull(random: Random): Char? {
      return if (`$this$randomOrNull`.length() == 0) null else `$this$randomOrNull`.charAt(random.nextInt(`$this$randomOrNull`.length()));
   }

   @JvmStatic
   public fun CharSequence.single(): Char {
      switch ($this$single.length()) {
         case 0:
            throw new NoSuchElementException("Char sequence is empty.");
         case 1:
            return `$this$single`.charAt(0);
         default:
            throw new IllegalArgumentException("Char sequence has more than one element.");
      }
   }

   @JvmStatic
   public inline fun CharSequence.single(predicate: (Char) -> Boolean): Char {
      var single: Character = null;
      var found: Boolean = false;

      for (int var5 = 0; var5 < $this$single.length(); var5++) {
         var element: Char = `$this$single`.charAt(var5);
         if (predicate.invoke(element) as java.lang.Boolean) {
            if (found) {
               throw new IllegalArgumentException("Char sequence contains more than one matching element.");
            }

            single = element;
            found = true;
         }
      }

      if (!found) {
         throw new NoSuchElementException("Char sequence contains no character matching the predicate.");
      } else {
         return single;
      }
   }

   @JvmStatic
   public fun CharSequence.singleOrNull(): Char? {
      return if (`$this$singleOrNull`.length() == 1) `$this$singleOrNull`.charAt(0) else null;
   }

   @JvmStatic
   public inline fun CharSequence.singleOrNull(predicate: (Char) -> Boolean): Char? {
      var single: Character = null;
      var found: Boolean = false;

      for (int var5 = 0; var5 < $this$singleOrNull.length(); var5++) {
         var element: Char = `$this$singleOrNull`.charAt(var5);
         if (predicate.invoke(element) as java.lang.Boolean) {
            if (found) {
               return null;
            }

            single = element;
            found = true;
         }
      }

      return if (!found) null else single;
   }

   @JvmStatic
   public fun CharSequence.drop(n: Int): CharSequence {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested character count " + n + " is less than zero.").toString());
      } else {
         return `$this$drop`.subSequence(RangesKt.coerceAtMost(n, `$this$drop`.length()), `$this$drop`.length());
      }
   }

   @JvmStatic
   public fun String.drop(n: Int): String {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested character count " + n + " is less than zero.").toString());
      } else {
         var var10000: java.lang.String = `$this$drop`.substring(RangesKt.coerceAtMost(n, `$this$drop`.length()));
         return var10000;
      }
   }

   @JvmStatic
   public fun CharSequence.dropLast(n: Int): CharSequence {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested character count " + n + " is less than zero.").toString());
      } else {
         return StringsKt.take(`$this$dropLast`, RangesKt.coerceAtLeast(`$this$dropLast`.length() - n, 0));
      }
   }

   @JvmStatic
   public fun String.dropLast(n: Int): String {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested character count " + n + " is less than zero.").toString());
      } else {
         return StringsKt.take(`$this$dropLast`, RangesKt.coerceAtLeast(`$this$dropLast`.length() - n, 0));
      }
   }

   @JvmStatic
   public inline fun CharSequence.dropLastWhile(predicate: (Char) -> Boolean): CharSequence {
      for (int index = StringsKt.getLastIndex($this$dropLastWhile); -1 < index; index--) {
         if (!predicate.invoke(`$this$dropLastWhile`.charAt(index)) as java.lang.Boolean) {
            return `$this$dropLastWhile`.subSequence(0, index + 1);
         }
      }

      return "";
   }

   @JvmStatic
   public inline fun String.dropLastWhile(predicate: (Char) -> Boolean): String {
      for (int index = StringsKt.getLastIndex($this$dropLastWhile); -1 < index; index--) {
         if (!predicate.invoke(`$this$dropLastWhile`.charAt(index)) as java.lang.Boolean) {
            var var10000: java.lang.String = `$this$dropLastWhile`.substring(0, index + 1);
            return var10000;
         }
      }

      return "";
   }

   @JvmStatic
   public inline fun CharSequence.dropWhile(predicate: (Char) -> Boolean): CharSequence {
      var index: Int = 0;

      for (int var4 = $this$dropWhile.length(); index < var4; index++) {
         if (!predicate.invoke(`$this$dropWhile`.charAt(index)) as java.lang.Boolean) {
            return `$this$dropWhile`.subSequence(index, `$this$dropWhile`.length());
         }
      }

      return "";
   }

   @JvmStatic
   public inline fun String.dropWhile(predicate: (Char) -> Boolean): String {
      var index: Int = 0;

      for (int var4 = $this$dropWhile.length(); index < var4; index++) {
         if (!predicate.invoke(`$this$dropWhile`.charAt(index)) as java.lang.Boolean) {
            var var10000: java.lang.String = `$this$dropWhile`.substring(index);
            return var10000;
         }
      }

      return "";
   }

   @JvmStatic
   public inline fun CharSequence.filter(predicate: (Char) -> Boolean): CharSequence {
      var `$this$filterTo$iv`: java.lang.CharSequence = `$this$filter`;
      var `destination$iv`: Appendable = new StringBuilder();
      var `index$iv`: Int = 0;

      for (int var7 = $this$filter.length(); index$iv < var7; index$iv++) {
         var `element$iv`: Char = `$this$filterTo$iv`.charAt(`index$iv`);
         if (predicate.invoke(`element$iv`) as java.lang.Boolean) {
            `destination$iv`.append(`element$iv`);
         }
      }

      return `destination$iv` as java.lang.CharSequence;
   }

   @JvmStatic
   public inline fun String.filter(predicate: (Char) -> Boolean): String {
      var `$this$filterTo$iv`: java.lang.CharSequence = `$this$filter`;
      var `destination$iv`: Appendable = new StringBuilder();
      var `index$iv`: Int = 0;

      for (int var7 = $this$filterTo$iv.length(); index$iv < var7; index$iv++) {
         var `element$iv`: Char = `$this$filterTo$iv`.charAt(`index$iv`);
         if (predicate.invoke(`element$iv`) as java.lang.Boolean) {
            `destination$iv`.append(`element$iv`);
         }
      }

      var var10000: java.lang.String = (`destination$iv` as StringBuilder).toString();
      return var10000;
   }

   @JvmStatic
   public inline fun CharSequence.filterIndexed(predicate: (Int, Char) -> Boolean): CharSequence {
      var `destination$iv`: Appendable = new StringBuilder();
      var `$this$forEachIndexed$iv$iv`: java.lang.CharSequence = `$this$filterIndexed`;
      var `index$iv$iv`: Int = 0;

      for (int var9 = 0; var9 < $this$forEachIndexed$iv$iv.length(); var9++) {
         var `item$iv$iv`: Char = `$this$forEachIndexed$iv$iv`.charAt(var9);
         if (predicate.invoke(`index$iv$iv`++, `item$iv$iv`) as java.lang.Boolean) {
            `destination$iv`.append(`item$iv$iv`);
         }
      }

      return `destination$iv` as java.lang.CharSequence;
   }

   @JvmStatic
   public inline fun String.filterIndexed(predicate: (Int, Char) -> Boolean): String {
      var `$this$filterIndexedTo$iv`: java.lang.CharSequence = `$this$filterIndexed`;
      var `destination$iv`: Appendable = new StringBuilder();
      var `$this$forEachIndexed$iv$iv`: java.lang.CharSequence = `$this$filterIndexedTo$iv`;
      var `index$iv$iv`: Int = 0;

      for (int var9 = 0; var9 < $this$forEachIndexed$iv$iv.length(); var9++) {
         var `item$iv$iv`: Char = `$this$forEachIndexed$iv$iv`.charAt(var9);
         if (predicate.invoke(`index$iv$iv`++, `item$iv$iv`) as java.lang.Boolean) {
            `destination$iv`.append(`item$iv$iv`);
         }
      }

      var var14: java.lang.String = (`destination$iv` as StringBuilder).toString();
      return var14;
   }

   @JvmStatic
   public inline fun <C : Appendable> CharSequence.filterIndexedTo(destination: C, predicate: (Int, Char) -> Boolean): C {
      var `$this$forEachIndexed$iv`: java.lang.CharSequence = `$this$filterIndexedTo`;
      var `index$iv`: Int = 0;

      for (int var7 = 0; var7 < $this$forEachIndexed$iv.length(); var7++) {
         var `item$iv`: Char = `$this$forEachIndexed$iv`.charAt(var7);
         if (predicate.invoke(`index$iv`++, `item$iv`) as java.lang.Boolean) {
            destination.append(`item$iv`);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun CharSequence.filterNot(predicate: (Char) -> Boolean): CharSequence {
      var `$this$filterNotTo$iv`: java.lang.CharSequence = `$this$filterNot`;
      var `destination$iv`: Appendable = new StringBuilder();

      for (int var6 = 0; var6 < $this$filterNotTo$iv.length(); var6++) {
         var `element$iv`: Char = `$this$filterNotTo$iv`.charAt(var6);
         if (!predicate.invoke(`element$iv`) as java.lang.Boolean) {
            `destination$iv`.append(`element$iv`);
         }
      }

      return `destination$iv` as java.lang.CharSequence;
   }

   @JvmStatic
   public inline fun String.filterNot(predicate: (Char) -> Boolean): String {
      var `$this$filterNotTo$iv`: java.lang.CharSequence = `$this$filterNot`;
      var `destination$iv`: Appendable = new StringBuilder();

      for (int var6 = 0; var6 < $this$filterNotTo$iv.length(); var6++) {
         var `element$iv`: Char = `$this$filterNotTo$iv`.charAt(var6);
         if (!predicate.invoke(`element$iv`) as java.lang.Boolean) {
            `destination$iv`.append(`element$iv`);
         }
      }

      var var10000: java.lang.String = (`destination$iv` as StringBuilder).toString();
      return var10000;
   }

   @JvmStatic
   public inline fun <C : Appendable> CharSequence.filterNotTo(destination: C, predicate: (Char) -> Boolean): C {
      for (int var4 = 0; var4 < $this$filterNotTo.length(); var4++) {
         var element: Char = `$this$filterNotTo`.charAt(var4);
         if (!predicate.invoke(element) as java.lang.Boolean) {
            destination.append(element);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <C : Appendable> CharSequence.filterTo(destination: C, predicate: (Char) -> Boolean): C {
      var index: Int = 0;

      for (int var5 = $this$filterTo.length(); index < var5; index++) {
         var element: Char = `$this$filterTo`.charAt(index);
         if (predicate.invoke(element) as java.lang.Boolean) {
            destination.append(element);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public fun CharSequence.slice(indices: IntRange): CharSequence {
      return if (indices.isEmpty()) "" else StringsKt.subSequence(`$this$slice`, indices);
   }

   @JvmStatic
   public fun String.slice(indices: IntRange): String {
      return if (indices.isEmpty()) "" else StringsKt.substring(`$this$slice`, indices);
   }

   @JvmStatic
   public fun CharSequence.slice(indices: Iterable<Int>): CharSequence {
      var size: Int = CollectionsKt.collectionSizeOrDefault(indices, 10);
      if (size == 0) {
         return "";
      } else {
         var result: StringBuilder = new StringBuilder(size);
         var var4: java.util.Iterator = indices.iterator();

         while (var4.hasNext()) {
            result.append(`$this$slice`.charAt((var4.next() as java.lang.Number).intValue()));
         }

         return result;
      }
   }

   @InlineOnly
   @JvmStatic
   public inline fun String.slice(indices: Iterable<Int>): String {
      return StringsKt.slice(`$this$slice`, indices).toString();
   }

   @JvmStatic
   public fun CharSequence.take(n: Int): CharSequence {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested character count " + n + " is less than zero.").toString());
      } else {
         return `$this$take`.subSequence(0, RangesKt.coerceAtMost(n, `$this$take`.length()));
      }
   }

   @JvmStatic
   public fun String.take(n: Int): String {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested character count " + n + " is less than zero.").toString());
      } else {
         var var10000: java.lang.String = `$this$take`.substring(0, RangesKt.coerceAtMost(n, `$this$take`.length()));
         return var10000;
      }
   }

   @JvmStatic
   public fun CharSequence.takeLast(n: Int): CharSequence {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested character count " + n + " is less than zero.").toString());
      } else {
         var var4: Int = `$this$takeLast`.length();
         return `$this$takeLast`.subSequence(var4 - RangesKt.coerceAtMost(n, var4), var4);
      }
   }

   @JvmStatic
   public fun String.takeLast(n: Int): String {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested character count " + n + " is less than zero.").toString());
      } else {
         var var5: Int = `$this$takeLast`.length();
         var var10000: java.lang.String = `$this$takeLast`.substring(var5 - RangesKt.coerceAtMost(n, var5));
         return var10000;
      }
   }

   @JvmStatic
   public inline fun CharSequence.takeLastWhile(predicate: (Char) -> Boolean): CharSequence {
      for (int index = StringsKt.getLastIndex($this$takeLastWhile); -1 < index; index--) {
         if (!predicate.invoke(`$this$takeLastWhile`.charAt(index)) as java.lang.Boolean) {
            return `$this$takeLastWhile`.subSequence(index + 1, `$this$takeLastWhile`.length());
         }
      }

      return `$this$takeLastWhile`.subSequence(0, `$this$takeLastWhile`.length());
   }

   @JvmStatic
   public inline fun String.takeLastWhile(predicate: (Char) -> Boolean): String {
      for (int index = StringsKt.getLastIndex($this$takeLastWhile); -1 < index; index--) {
         if (!predicate.invoke(`$this$takeLastWhile`.charAt(index)) as java.lang.Boolean) {
            var var10000: java.lang.String = `$this$takeLastWhile`.substring(index + 1);
            return var10000;
         }
      }

      return `$this$takeLastWhile`;
   }

   @JvmStatic
   public inline fun CharSequence.takeWhile(predicate: (Char) -> Boolean): CharSequence {
      var index: Int = 0;

      for (int var4 = $this$takeWhile.length(); index < var4; index++) {
         if (!predicate.invoke(`$this$takeWhile`.charAt(index)) as java.lang.Boolean) {
            return `$this$takeWhile`.subSequence(0, index);
         }
      }

      return `$this$takeWhile`.subSequence(0, `$this$takeWhile`.length());
   }

   @JvmStatic
   public inline fun String.takeWhile(predicate: (Char) -> Boolean): String {
      var index: Int = 0;

      for (int var4 = $this$takeWhile.length(); index < var4; index++) {
         if (!predicate.invoke(`$this$takeWhile`.charAt(index)) as java.lang.Boolean) {
            var var10000: java.lang.String = `$this$takeWhile`.substring(0, index);
            return var10000;
         }
      }

      return `$this$takeWhile`;
   }

   @JvmStatic
   public fun CharSequence.reversed(): CharSequence {
      var var10000: StringBuilder = new StringBuilder(`$this$reversed`).reverse();
      return var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline fun String.reversed(): String {
      return StringsKt.reversed(`$this$reversed`).toString();
   }

   @JvmStatic
   public inline fun <K, V> CharSequence.associate(transform: (Char) -> Pair<K, V>): Map<K, V> {
      var capacity: Int = RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associate`.length()), 16);
      var `$this$associateTo$iv`: java.lang.CharSequence = `$this$associate`;
      var `destination$iv`: java.util.Map = new LinkedHashMap(capacity);

      for (int var7 = 0; var7 < $this$associateTo$iv.length(); var7++) {
         var var10: Pair = transform.invoke(`$this$associateTo$iv`.charAt(var7)) as Pair;
         `destination$iv`.put(var10.getFirst(), var10.getSecond());
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K> CharSequence.associateBy(keySelector: (Char) -> K): Map<K, Char> {
      var capacity: Int = RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associateBy`.length()), 16);
      var `$this$associateByTo$iv`: java.lang.CharSequence = `$this$associateBy`;
      var `destination$iv`: java.util.Map = new LinkedHashMap(capacity);

      for (int var7 = 0; var7 < $this$associateByTo$iv.length(); var7++) {
         var `element$iv`: Char = `$this$associateByTo$iv`.charAt(var7);
         `destination$iv`.put(keySelector.invoke(`element$iv`), `element$iv`);
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K, V> CharSequence.associateBy(keySelector: (Char) -> K, valueTransform: (Char) -> V): Map<K, V> {
      var capacity: Int = RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associateBy`.length()), 16);
      var `$this$associateByTo$iv`: java.lang.CharSequence = `$this$associateBy`;
      var `destination$iv`: java.util.Map = new LinkedHashMap(capacity);

      for (int var8 = 0; var8 < $this$associateByTo$iv.length(); var8++) {
         var `element$iv`: Char = `$this$associateByTo$iv`.charAt(var8);
         `destination$iv`.put(keySelector.invoke(`element$iv`), valueTransform.invoke(`element$iv`));
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K, M : MutableMap<in K, in Char>> CharSequence.associateByTo(destination: M, keySelector: (Char) -> K): M {
      for (int var4 = 0; var4 < $this$associateByTo.length(); var4++) {
         var element: Char = `$this$associateByTo`.charAt(var4);
         destination.put(keySelector.invoke(element), element);
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, V, M : MutableMap<in K, in V>> CharSequence.associateByTo(destination: M, keySelector: (Char) -> K, valueTransform: (Char) -> V): M {
      for (int var5 = 0; var5 < $this$associateByTo.length(); var5++) {
         var element: Char = `$this$associateByTo`.charAt(var5);
         destination.put(keySelector.invoke(element), valueTransform.invoke(element));
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, V, M : MutableMap<in K, in V>> CharSequence.associateTo(destination: M, transform: (Char) -> Pair<K, V>): M {
      for (int var4 = 0; var4 < $this$associateTo.length(); var4++) {
         var var7: Pair = transform.invoke(`$this$associateTo`.charAt(var4)) as Pair;
         destination.put(var7.getFirst(), var7.getSecond());
      }

      return (M)destination;
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   public inline fun <V> CharSequence.associateWith(valueSelector: (Char) -> V): Map<Char, V> {
      var result: LinkedHashMap = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(RangesKt.coerceAtMost(`$this$associateWith`.length(), 128)), 16));
      var `$this$associateWithTo$iv`: java.lang.CharSequence = `$this$associateWith`;

      for (int var6 = 0; var6 < $this$associateWithTo$iv.length(); var6++) {
         var `element$iv`: Char = `$this$associateWithTo$iv`.charAt(var6);
         result.put(`element$iv`, valueSelector.invoke(`element$iv`));
      }

      return result;
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   public inline fun <V, M : MutableMap<in Char, in V>> CharSequence.associateWithTo(destination: M, valueSelector: (Char) -> V): M {
      for (int var4 = 0; var4 < $this$associateWithTo.length(); var4++) {
         var element: Char = `$this$associateWithTo`.charAt(var4);
         destination.put(element, valueSelector.invoke(element));
      }

      return (M)destination;
   }

   @JvmStatic
   public fun <C : MutableCollection<in Char>> CharSequence.toCollection(destination: C): C {
      for (int var2 = 0; var2 < $this$toCollection.length(); var2++) {
         destination.add(`$this$toCollection`.charAt(var2));
      }

      return (C)destination;
   }

   @JvmStatic
   public fun CharSequence.toHashSet(): HashSet<Char> {
      return StringsKt.toCollection(`$this$toHashSet`, new HashSet<>(MapsKt.mapCapacity(RangesKt.coerceAtMost(`$this$toHashSet`.length(), 128))));
   }

   @JvmStatic
   public fun CharSequence.toList(): List<Char> {
      var var10000: java.util.List;
      switch ($this$toList.length()) {
         case 0:
            var10000 = CollectionsKt.emptyList();
            break;
         case 1:
            var10000 = CollectionsKt.listOf(Character.valueOf(`$this$toList`.charAt(0)));
            break;
         default:
            var10000 = StringsKt.toMutableList(`$this$toList`);
      }

      return var10000;
   }

   @JvmStatic
   public fun CharSequence.toMutableList(): MutableList<Char> {
      return StringsKt.toCollection(`$this$toMutableList`, new ArrayList<>(`$this$toMutableList`.length()));
   }

   @JvmStatic
   public fun CharSequence.toSet(): Set<Char> {
      var var10000: java.util.Set;
      switch ($this$toSet.length()) {
         case 0:
            var10000 = SetsKt.emptySet();
            break;
         case 1:
            var10000 = SetsKt.setOf(Character.valueOf(`$this$toSet`.charAt(0)));
            break;
         default:
            var10000 = StringsKt.toCollection(`$this$toSet`, new LinkedHashSet(MapsKt.mapCapacity(RangesKt.coerceAtMost(`$this$toSet`.length(), 128))));
      }

      return var10000;
   }

   @JvmStatic
   public inline fun <R> CharSequence.flatMap(transform: (Char) -> Iterable<R>): List<R> {
      var `$this$flatMapTo$iv`: java.lang.CharSequence = `$this$flatMap`;
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (int var6 = 0; var6 < $this$flatMapTo$iv.length(); var6++) {
         CollectionsKt.addAll(`destination$iv`, transform.invoke(`$this$flatMapTo$iv`.charAt(var6)) as java.lang.Iterable);
      }

      return `destination$iv` as java.util.List<R>;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "flatMapIndexedIterable")
   @InlineOnly
   @JvmStatic
   public inline fun <R> CharSequence.flatMapIndexed(transform: (Int, Char) -> Iterable<R>): List<R> {
      var var2: java.lang.CharSequence = `$this$flatMapIndexed`;
      var var3: java.util.Collection = new ArrayList();
      var var4: Int = 0;

      for (int var5 = 0; var5 < var2.length(); var5++) {
         CollectionsKt.addAll(var3, transform.invoke(var4++, var2.charAt(var5)) as java.lang.Iterable);
      }

      return var3 as java.util.List<R>;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "flatMapIndexedIterableTo")
   @InlineOnly
   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> CharSequence.flatMapIndexedTo(destination: C, transform: (Int, Char) -> Iterable<R>): C {
      var index: Int = 0;

      for (int var4 = 0; var4 < $this$flatMapIndexedTo.length(); var4++) {
         CollectionsKt.addAll(destination, transform.invoke(index++, `$this$flatMapIndexedTo`.charAt(var4)) as java.lang.Iterable);
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> CharSequence.flatMapTo(destination: C, transform: (Char) -> Iterable<R>): C {
      for (int var4 = 0; var4 < $this$flatMapTo.length(); var4++) {
         CollectionsKt.addAll(destination, transform.invoke(`$this$flatMapTo`.charAt(var4)) as java.lang.Iterable);
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <K> CharSequence.groupBy(keySelector: (Char) -> K): Map<K, List<Char>> {
      var `$this$groupByTo$iv`: java.lang.CharSequence = `$this$groupBy`;
      var `destination$iv`: java.util.Map = new LinkedHashMap();

      for (int var6 = 0; var6 < $this$groupByTo$iv.length(); var6++) {
         var `element$iv`: Char = `$this$groupByTo$iv`.charAt(var6);
         var `key$iv`: Any = keySelector.invoke(`element$iv`);
         var `value$iv$iv`: Any = `destination$iv`.get(`key$iv`);
         var var10000: Any;
         if (`value$iv$iv` == null) {
            var var14: Any = new ArrayList();
            `destination$iv`.put(`key$iv`, var14);
            var10000 = var14;
         } else {
            var10000 = `value$iv$iv`;
         }

         (var10000 as java.util.List).add(`element$iv`);
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K, V> CharSequence.groupBy(keySelector: (Char) -> K, valueTransform: (Char) -> V): Map<K, List<V>> {
      var `$this$groupByTo$iv`: java.lang.CharSequence = `$this$groupBy`;
      var `destination$iv`: java.util.Map = new LinkedHashMap();

      for (int var7 = 0; var7 < $this$groupByTo$iv.length(); var7++) {
         var `element$iv`: Char = `$this$groupByTo$iv`.charAt(var7);
         var `key$iv`: Any = keySelector.invoke(`element$iv`);
         var `value$iv$iv`: Any = `destination$iv`.get(`key$iv`);
         var var10000: Any;
         if (`value$iv$iv` == null) {
            var var15: Any = new ArrayList();
            `destination$iv`.put(`key$iv`, var15);
            var10000 = var15;
         } else {
            var10000 = `value$iv$iv`;
         }

         (var10000 as java.util.List).add(valueTransform.invoke(`element$iv`));
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K, M : MutableMap<in K, MutableList<Char>>> CharSequence.groupByTo(destination: M, keySelector: (Char) -> K): M {
      for (int var4 = 0; var4 < $this$groupByTo.length(); var4++) {
         var element: Char = `$this$groupByTo`.charAt(var4);
         var key: Any = keySelector.invoke(element);
         var `value$iv`: Any = destination.get(key);
         var var10000: Any;
         if (`value$iv` == null) {
            var var12: Any = new ArrayList();
            destination.put(key, var12);
            var10000 = var12;
         } else {
            var10000 = `value$iv`;
         }

         (var10000 as java.util.List).add(element);
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, V, M : MutableMap<in K, MutableList<V>>> CharSequence.groupByTo(destination: M, keySelector: (Char) -> K, valueTransform: (Char) -> V): M {
      for (int var5 = 0; var5 < $this$groupByTo.length(); var5++) {
         var element: Char = `$this$groupByTo`.charAt(var5);
         var key: Any = keySelector.invoke(element);
         var `value$iv`: Any = destination.get(key);
         var var10000: Any;
         if (`value$iv` == null) {
            var var13: Any = new ArrayList();
            destination.put(key, var13);
            var10000 = var13;
         } else {
            var10000 = `value$iv`;
         }

         (var10000 as java.util.List).add(valueTransform.invoke(element));
      }

      return (M)destination;
   }

   @SinceKotlin(version = "1.1")
   @JvmStatic
   public inline fun <K> CharSequence.groupingBy(crossinline keySelector: (Char) -> K): Grouping<Char, K> {
      return new Grouping<Character, K>(`$this$groupingBy`, keySelector) {
         {
            this.$this_groupingBy = `$receiver`;
            this.$keySelector = `$keySelector`;
         }

         @NotNull
         @Override
         public java.util.Iterator<Character> sourceIterator() {
            return StringsKt.iterator(this.$this_groupingBy);
         }

         public K keyOf(char element) {
            return (K)this.$keySelector.invoke(element);
         }
      };
   }

   @JvmStatic
   public inline fun <R> CharSequence.map(transform: (Char) -> R): List<R> {
      var `$this$mapTo$iv`: java.lang.CharSequence = `$this$map`;
      var `destination$iv`: java.util.Collection = new ArrayList(`$this$map`.length());

      for (int var6 = 0; var6 < $this$mapTo$iv.length(); var6++) {
         `destination$iv`.add(transform.invoke(`$this$mapTo$iv`.charAt(var6)));
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <R> CharSequence.mapIndexed(transform: (Int, Char) -> R): List<R> {
      var `$this$mapIndexedTo$iv`: java.lang.CharSequence = `$this$mapIndexed`;
      var `destination$iv`: java.util.Collection = new ArrayList(`$this$mapIndexed`.length());
      var `index$iv`: Int = 0;

      for (int var7 = 0; var7 < $this$mapIndexedTo$iv.length(); var7++) {
         `destination$iv`.add(transform.invoke(`index$iv`++, `$this$mapIndexedTo$iv`.charAt(var7)));
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <R : Any> CharSequence.mapIndexedNotNull(transform: (Int, Char) -> R?): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList();
      var `$this$forEachIndexed$iv$iv`: java.lang.CharSequence = `$this$mapIndexedNotNull`;
      var `index$iv$iv`: Int = 0;

      for (int var9 = 0; var9 < $this$forEachIndexed$iv$iv.length(); var9++) {
         var var17: Any = transform.invoke(`index$iv$iv`++, `$this$forEachIndexed$iv$iv`.charAt(var9));
         if (var17 != null) {
            `destination$iv`.add(var17);
         }
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <R : Any, C : MutableCollection<in R>> CharSequence.mapIndexedNotNullTo(destination: C, transform: (Int, Char) -> R?): C {
      var `$this$forEachIndexed$iv`: java.lang.CharSequence = `$this$mapIndexedNotNullTo`;
      var `index$iv`: Int = 0;

      for (int var7 = 0; var7 < $this$forEachIndexed$iv.length(); var7++) {
         var var15: Any = transform.invoke(`index$iv`++, `$this$forEachIndexed$iv`.charAt(var7));
         if (var15 != null) {
            destination.add(var15);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> CharSequence.mapIndexedTo(destination: C, transform: (Int, Char) -> R): C {
      var index: Int = 0;

      for (int var5 = 0; var5 < $this$mapIndexedTo.length(); var5++) {
         destination.add(transform.invoke(index++, `$this$mapIndexedTo`.charAt(var5)));
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <R : Any> CharSequence.mapNotNull(transform: (Char) -> R?): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList();
      var `$this$forEach$iv$iv`: java.lang.CharSequence = `$this$mapNotNull`;

      for (int var8 = 0; var8 < $this$forEach$iv$iv.length(); var8++) {
         var var10000: Any = transform.invoke(`$this$forEach$iv$iv`.charAt(var8));
         if (var10000 != null) {
            `destination$iv`.add(var10000);
         }
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <R : Any, C : MutableCollection<in R>> CharSequence.mapNotNullTo(destination: C, transform: (Char) -> R?): C {
      var `$this$forEach$iv`: java.lang.CharSequence = `$this$mapNotNullTo`;

      for (int var6 = 0; var6 < $this$forEach$iv.length(); var6++) {
         var var10000: Any = transform.invoke(`$this$forEach$iv`.charAt(var6));
         if (var10000 != null) {
            destination.add(var10000);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> CharSequence.mapTo(destination: C, transform: (Char) -> R): C {
      for (int var4 = 0; var4 < $this$mapTo.length(); var4++) {
         destination.add(transform.invoke(`$this$mapTo`.charAt(var4)));
      }

      return (C)destination;
   }

   @JvmStatic
   public fun CharSequence.withIndex(): Iterable<IndexedValue<Char>> {
      return new IndexingIterable<>((new Function0<java.util.Iterator<? extends Character>>(`$this$withIndex`) {
         {
            super(0);
            this.$this_withIndex = `$receiver`;
         }

         @NotNull
         public final java.util.Iterator<Character> invoke() {
            return StringsKt.iterator(this.$this_withIndex);
         }
      }) as Function0<? extends java.util.Iterator<? extends Character>>);
   }

   @JvmStatic
   public inline fun CharSequence.all(predicate: (Char) -> Boolean): Boolean {
      for (int var3 = 0; var3 < $this$all.length(); var3++) {
         if (!predicate.invoke(`$this$all`.charAt(var3)) as java.lang.Boolean) {
            return false;
         }
      }

      return true;
   }

   @JvmStatic
   public fun CharSequence.any(): Boolean {
      return `$this$any`.length() != 0;
   }

   @JvmStatic
   public inline fun CharSequence.any(predicate: (Char) -> Boolean): Boolean {
      for (int var3 = 0; var3 < $this$any.length(); var3++) {
         if (predicate.invoke(`$this$any`.charAt(var3)) as java.lang.Boolean) {
            return true;
         }
      }

      return false;
   }

   @InlineOnly
   @JvmStatic
   public inline fun CharSequence.count(): Int {
      return `$this$count`.length();
   }

   @JvmStatic
   public inline fun CharSequence.count(predicate: (Char) -> Boolean): Int {
      var count: Int = 0;

      for (int var4 = 0; var4 < $this$count.length(); var4++) {
         if (predicate.invoke(`$this$count`.charAt(var4)) as java.lang.Boolean) {
            count++;
         }
      }

      return count;
   }

   @JvmStatic
   public inline fun <R> CharSequence.fold(initial: R, operation: (R, Char) -> R): R {
      var accumulator: Any = initial;

      for (int var5 = 0; var5 < $this$fold.length(); var5++) {
         accumulator = operation.invoke(accumulator, `$this$fold`.charAt(var5));
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <R> CharSequence.foldIndexed(initial: R, operation: (Int, R, Char) -> R): R {
      var index: Int = 0;
      var accumulator: Any = initial;

      for (int var6 = 0; var6 < $this$foldIndexed.length(); var6++) {
         accumulator = operation.invoke(index++, accumulator, `$this$foldIndexed`.charAt(var6));
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <R> CharSequence.foldRight(initial: R, operation: (Char, R) -> R): R {
      var index: Int = StringsKt.getLastIndex(`$this$foldRight`);
      var accumulator: Any = initial;

      while (index >= 0) {
         accumulator = operation.invoke(`$this$foldRight`.charAt(index--), accumulator);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <R> CharSequence.foldRightIndexed(initial: R, operation: (Int, Char, R) -> R): R {
      var index: Int = StringsKt.getLastIndex(`$this$foldRightIndexed`);

      var accumulator: Any;
      for (accumulator = initial; index >= 0; index--) {
         accumulator = operation.invoke(index, `$this$foldRightIndexed`.charAt(index), accumulator);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun CharSequence.forEach(action: (Char) -> Unit) {
      for (int var3 = 0; var3 < $this$forEach.length(); var3++) {
         action.invoke(`$this$forEach`.charAt(var3));
      }
   }

   @JvmStatic
   public inline fun CharSequence.forEachIndexed(action: (Int, Char) -> Unit) {
      var index: Int = 0;

      for (int var4 = 0; var4 < $this$forEachIndexed.length(); var4++) {
         action.invoke(index++, `$this$forEachIndexed`.charAt(var4));
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxOrThrow")
   @JvmStatic
   public fun CharSequence.max(): Char {
      if (`$this$max`.length() == 0) {
         throw new NoSuchElementException();
      } else {
         var max: Char = `$this$max`.charAt(0);
         var var2: IntIterator = new IntRange(1, StringsKt.getLastIndex(`$this$max`)).iterator();

         while (var2.hasNext()) {
            var e: Char = `$this$max`.charAt(var2.nextInt());
            if (Intrinsics.compare(max, e) < 0) {
               max = e;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxByOrThrow")
   @JvmStatic
   public inline fun <R : Comparable<R>> CharSequence.maxBy(selector: (Char) -> R): Char {
      if (`$this$maxBy`.length() == 0) {
         throw new NoSuchElementException();
      } else {
         var maxElem: Char = `$this$maxBy`.charAt(0);
         var lastIndex: Int = StringsKt.getLastIndex(`$this$maxBy`);
         if (lastIndex == 0) {
            return maxElem;
         } else {
            var maxValue: java.lang.Comparable = selector.invoke(maxElem) as java.lang.Comparable;
            var var6: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var6.hasNext()) {
               var e: Char = `$this$maxBy`.charAt(var6.nextInt());
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
               if (maxValue.compareTo(v) < 0) {
                  maxElem = e;
                  maxValue = v;
               }
            }

            return maxElem;
         }
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun <R : Comparable<R>> CharSequence.maxByOrNull(selector: (Char) -> R): Char? {
      if (`$this$maxByOrNull`.length() == 0) {
         return null;
      } else {
         var maxElem: Char = `$this$maxByOrNull`.charAt(0);
         var lastIndex: Int = StringsKt.getLastIndex(`$this$maxByOrNull`);
         if (lastIndex == 0) {
            return maxElem;
         } else {
            var maxValue: java.lang.Comparable = selector.invoke(maxElem) as java.lang.Comparable;
            var var6: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var6.hasNext()) {
               var e: Char = `$this$maxByOrNull`.charAt(var6.nextInt());
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
               if (maxValue.compareTo(v) < 0) {
                  maxElem = e;
                  maxValue = v;
               }
            }

            return maxElem;
         }
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun CharSequence.maxOf(selector: (Char) -> Double): Double {
      if (`$this$maxOf`.length() == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Double = (selector.invoke(`$this$maxOf`.charAt(0)) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, StringsKt.getLastIndex(`$this$maxOf`)).iterator();

         while (var4.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOf`.charAt(var4.nextInt())) as java.lang.Number).doubleValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun CharSequence.maxOf(selector: (Char) -> Float): Float {
      if (`$this$maxOf`.length() == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Float = (selector.invoke(`$this$maxOf`.charAt(0)) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, StringsKt.getLastIndex(`$this$maxOf`)).iterator();

         while (var3.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOf`.charAt(var3.nextInt())) as java.lang.Number).floatValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> CharSequence.maxOf(selector: (Char) -> R): R {
      if (`$this$maxOf`.length() == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: java.lang.Comparable = selector.invoke(`$this$maxOf`.charAt(0)) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, StringsKt.getLastIndex(`$this$maxOf`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$maxOf`.charAt(var3.nextInt())) as java.lang.Comparable;
            if (maxValue.compareTo(v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun CharSequence.maxOfOrNull(selector: (Char) -> Double): Double? {
      if (`$this$maxOfOrNull`.length() == 0) {
         return null;
      } else {
         var maxValue: Double = (selector.invoke(`$this$maxOfOrNull`.charAt(0)) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, StringsKt.getLastIndex(`$this$maxOfOrNull`)).iterator();

         while (var4.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOfOrNull`.charAt(var4.nextInt())) as java.lang.Number).doubleValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun CharSequence.maxOfOrNull(selector: (Char) -> Float): Float? {
      if (`$this$maxOfOrNull`.length() == 0) {
         return null;
      } else {
         var maxValue: Float = (selector.invoke(`$this$maxOfOrNull`.charAt(0)) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, StringsKt.getLastIndex(`$this$maxOfOrNull`)).iterator();

         while (var3.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOfOrNull`.charAt(var3.nextInt())) as java.lang.Number).floatValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> CharSequence.maxOfOrNull(selector: (Char) -> R): R? {
      if (`$this$maxOfOrNull`.length() == 0) {
         return null;
      } else {
         var maxValue: java.lang.Comparable = selector.invoke(`$this$maxOfOrNull`.charAt(0)) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, StringsKt.getLastIndex(`$this$maxOfOrNull`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$maxOfOrNull`.charAt(var3.nextInt())) as java.lang.Comparable;
            if (maxValue.compareTo(v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R> CharSequence.maxOfWith(comparator: Comparator<in R>, selector: (Char) -> R): R {
      if (`$this$maxOfWith`.length() == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Any = selector.invoke(`$this$maxOfWith`.charAt(0));
         var var4: IntIterator = new IntRange(1, StringsKt.getLastIndex(`$this$maxOfWith`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$maxOfWith`.charAt(var4.nextInt()));
            if (comparator.compare(maxValue, v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R> CharSequence.maxOfWithOrNull(comparator: Comparator<in R>, selector: (Char) -> R): R? {
      if (`$this$maxOfWithOrNull`.length() == 0) {
         return null;
      } else {
         var maxValue: Any = selector.invoke(`$this$maxOfWithOrNull`.charAt(0));
         var var4: IntIterator = new IntRange(1, StringsKt.getLastIndex(`$this$maxOfWithOrNull`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$maxOfWithOrNull`.charAt(var4.nextInt()));
            if (comparator.compare(maxValue, v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun CharSequence.maxOrNull(): Char? {
      if (`$this$maxOrNull`.length() == 0) {
         return null;
      } else {
         var max: Char = `$this$maxOrNull`.charAt(0);
         var var2: IntIterator = new IntRange(1, StringsKt.getLastIndex(`$this$maxOrNull`)).iterator();

         while (var2.hasNext()) {
            var e: Char = `$this$maxOrNull`.charAt(var2.nextInt());
            if (Intrinsics.compare(max, e) < 0) {
               max = e;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxWithOrThrow")
   @JvmStatic
   public fun CharSequence.maxWith(comparator: Comparator<in Char>): Char {
      if (`$this$maxWith`.length() == 0) {
         throw new NoSuchElementException();
      } else {
         var max: Char = `$this$maxWith`.charAt(0);
         var var3: IntIterator = new IntRange(1, StringsKt.getLastIndex(`$this$maxWith`)).iterator();

         while (var3.hasNext()) {
            var e: Char = `$this$maxWith`.charAt(var3.nextInt());
            if (comparator.compare(max, e) < 0) {
               max = e;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun CharSequence.maxWithOrNull(comparator: Comparator<in Char>): Char? {
      if (`$this$maxWithOrNull`.length() == 0) {
         return null;
      } else {
         var max: Char = `$this$maxWithOrNull`.charAt(0);
         var var3: IntIterator = new IntRange(1, StringsKt.getLastIndex(`$this$maxWithOrNull`)).iterator();

         while (var3.hasNext()) {
            var e: Char = `$this$maxWithOrNull`.charAt(var3.nextInt());
            if (comparator.compare(max, e) < 0) {
               max = e;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minOrThrow")
   @JvmStatic
   public fun CharSequence.min(): Char {
      if (`$this$min`.length() == 0) {
         throw new NoSuchElementException();
      } else {
         var min: Char = `$this$min`.charAt(0);
         var var2: IntIterator = new IntRange(1, StringsKt.getLastIndex(`$this$min`)).iterator();

         while (var2.hasNext()) {
            var e: Char = `$this$min`.charAt(var2.nextInt());
            if (Intrinsics.compare(min, e) > 0) {
               min = e;
            }
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minByOrThrow")
   @JvmStatic
   public inline fun <R : Comparable<R>> CharSequence.minBy(selector: (Char) -> R): Char {
      if (`$this$minBy`.length() == 0) {
         throw new NoSuchElementException();
      } else {
         var minElem: Char = `$this$minBy`.charAt(0);
         var lastIndex: Int = StringsKt.getLastIndex(`$this$minBy`);
         if (lastIndex == 0) {
            return minElem;
         } else {
            var minValue: java.lang.Comparable = selector.invoke(minElem) as java.lang.Comparable;
            var var6: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var6.hasNext()) {
               var e: Char = `$this$minBy`.charAt(var6.nextInt());
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
               if (minValue.compareTo(v) > 0) {
                  minElem = e;
                  minValue = v;
               }
            }

            return minElem;
         }
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun <R : Comparable<R>> CharSequence.minByOrNull(selector: (Char) -> R): Char? {
      if (`$this$minByOrNull`.length() == 0) {
         return null;
      } else {
         var minElem: Char = `$this$minByOrNull`.charAt(0);
         var lastIndex: Int = StringsKt.getLastIndex(`$this$minByOrNull`);
         if (lastIndex == 0) {
            return minElem;
         } else {
            var minValue: java.lang.Comparable = selector.invoke(minElem) as java.lang.Comparable;
            var var6: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var6.hasNext()) {
               var e: Char = `$this$minByOrNull`.charAt(var6.nextInt());
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
               if (minValue.compareTo(v) > 0) {
                  minElem = e;
                  minValue = v;
               }
            }

            return minElem;
         }
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun CharSequence.minOf(selector: (Char) -> Double): Double {
      if (`$this$minOf`.length() == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: Double = (selector.invoke(`$this$minOf`.charAt(0)) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, StringsKt.getLastIndex(`$this$minOf`)).iterator();

         while (var4.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOf`.charAt(var4.nextInt())) as java.lang.Number).doubleValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun CharSequence.minOf(selector: (Char) -> Float): Float {
      if (`$this$minOf`.length() == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: Float = (selector.invoke(`$this$minOf`.charAt(0)) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, StringsKt.getLastIndex(`$this$minOf`)).iterator();

         while (var3.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOf`.charAt(var3.nextInt())) as java.lang.Number).floatValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> CharSequence.minOf(selector: (Char) -> R): R {
      if (`$this$minOf`.length() == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: java.lang.Comparable = selector.invoke(`$this$minOf`.charAt(0)) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, StringsKt.getLastIndex(`$this$minOf`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$minOf`.charAt(var3.nextInt())) as java.lang.Comparable;
            if (minValue.compareTo(v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun CharSequence.minOfOrNull(selector: (Char) -> Double): Double? {
      if (`$this$minOfOrNull`.length() == 0) {
         return null;
      } else {
         var minValue: Double = (selector.invoke(`$this$minOfOrNull`.charAt(0)) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, StringsKt.getLastIndex(`$this$minOfOrNull`)).iterator();

         while (var4.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOfOrNull`.charAt(var4.nextInt())) as java.lang.Number).doubleValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun CharSequence.minOfOrNull(selector: (Char) -> Float): Float? {
      if (`$this$minOfOrNull`.length() == 0) {
         return null;
      } else {
         var minValue: Float = (selector.invoke(`$this$minOfOrNull`.charAt(0)) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, StringsKt.getLastIndex(`$this$minOfOrNull`)).iterator();

         while (var3.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOfOrNull`.charAt(var3.nextInt())) as java.lang.Number).floatValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> CharSequence.minOfOrNull(selector: (Char) -> R): R? {
      if (`$this$minOfOrNull`.length() == 0) {
         return null;
      } else {
         var minValue: java.lang.Comparable = selector.invoke(`$this$minOfOrNull`.charAt(0)) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, StringsKt.getLastIndex(`$this$minOfOrNull`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$minOfOrNull`.charAt(var3.nextInt())) as java.lang.Comparable;
            if (minValue.compareTo(v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R> CharSequence.minOfWith(comparator: Comparator<in R>, selector: (Char) -> R): R {
      if (`$this$minOfWith`.length() == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: Any = selector.invoke(`$this$minOfWith`.charAt(0));
         var var4: IntIterator = new IntRange(1, StringsKt.getLastIndex(`$this$minOfWith`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$minOfWith`.charAt(var4.nextInt()));
            if (comparator.compare(minValue, v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R> CharSequence.minOfWithOrNull(comparator: Comparator<in R>, selector: (Char) -> R): R? {
      if (`$this$minOfWithOrNull`.length() == 0) {
         return null;
      } else {
         var minValue: Any = selector.invoke(`$this$minOfWithOrNull`.charAt(0));
         var var4: IntIterator = new IntRange(1, StringsKt.getLastIndex(`$this$minOfWithOrNull`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$minOfWithOrNull`.charAt(var4.nextInt()));
            if (comparator.compare(minValue, v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun CharSequence.minOrNull(): Char? {
      if (`$this$minOrNull`.length() == 0) {
         return null;
      } else {
         var min: Char = `$this$minOrNull`.charAt(0);
         var var2: IntIterator = new IntRange(1, StringsKt.getLastIndex(`$this$minOrNull`)).iterator();

         while (var2.hasNext()) {
            var e: Char = `$this$minOrNull`.charAt(var2.nextInt());
            if (Intrinsics.compare(min, e) > 0) {
               min = e;
            }
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minWithOrThrow")
   @JvmStatic
   public fun CharSequence.minWith(comparator: Comparator<in Char>): Char {
      if (`$this$minWith`.length() == 0) {
         throw new NoSuchElementException();
      } else {
         var min: Char = `$this$minWith`.charAt(0);
         var var3: IntIterator = new IntRange(1, StringsKt.getLastIndex(`$this$minWith`)).iterator();

         while (var3.hasNext()) {
            var e: Char = `$this$minWith`.charAt(var3.nextInt());
            if (comparator.compare(min, e) > 0) {
               min = e;
            }
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun CharSequence.minWithOrNull(comparator: Comparator<in Char>): Char? {
      if (`$this$minWithOrNull`.length() == 0) {
         return null;
      } else {
         var min: Char = `$this$minWithOrNull`.charAt(0);
         var var3: IntIterator = new IntRange(1, StringsKt.getLastIndex(`$this$minWithOrNull`)).iterator();

         while (var3.hasNext()) {
            var e: Char = `$this$minWithOrNull`.charAt(var3.nextInt());
            if (comparator.compare(min, e) > 0) {
               min = e;
            }
         }

         return min;
      }
   }

   @JvmStatic
   public fun CharSequence.none(): Boolean {
      return `$this$none`.length() == 0;
   }

   @JvmStatic
   public inline fun CharSequence.none(predicate: (Char) -> Boolean): Boolean {
      for (int var3 = 0; var3 < $this$none.length(); var3++) {
         if (predicate.invoke(`$this$none`.charAt(var3)) as java.lang.Boolean) {
            return false;
         }
      }

      return true;
   }

   @SinceKotlin(version = "1.1")
   @JvmStatic
   public inline fun <S : CharSequence> S.onEach(action: (Char) -> Unit): S {
      var `$this$onEach_u24lambda_u2415`: java.lang.CharSequence = `$this$onEach`;

      for (int var6 = 0; var6 < $this$onEach_u24lambda_u2415.length(); var6++) {
         action.invoke(`$this$onEach_u24lambda_u2415`.charAt(var6));
      }

      return (S)`$this$onEach`;
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun <S : CharSequence> S.onEachIndexed(action: (Int, Char) -> Unit): S {
      var `$this$forEachIndexed$iv`: java.lang.CharSequence = `$this$onEachIndexed`;
      var `index$iv`: Int = 0;

      for (int var9 = 0; var9 < $this$forEachIndexed$iv.length(); var9++) {
         action.invoke(`index$iv`++, `$this$forEachIndexed$iv`.charAt(var9));
      }

      return (S)`$this$onEachIndexed`;
   }

   @JvmStatic
   public inline fun CharSequence.reduce(operation: (Char, Char) -> Char): Char {
      if (`$this$reduce`.length() == 0) {
         throw new UnsupportedOperationException("Empty char sequence can't be reduced.");
      } else {
         var accumulator: Char = `$this$reduce`.charAt(0);
         var var4: IntIterator = new IntRange(1, StringsKt.getLastIndex(`$this$reduce`)).iterator();

         while (var4.hasNext()) {
            accumulator = operation.invoke(accumulator, `$this$reduce`.charAt(var4.nextInt())) as Character;
         }

         return accumulator;
      }
   }

   @JvmStatic
   public inline fun CharSequence.reduceIndexed(operation: (Int, Char, Char) -> Char): Char {
      if (`$this$reduceIndexed`.length() == 0) {
         throw new UnsupportedOperationException("Empty char sequence can't be reduced.");
      } else {
         var accumulator: Char = `$this$reduceIndexed`.charAt(0);
         var var4: IntIterator = new IntRange(1, StringsKt.getLastIndex(`$this$reduceIndexed`)).iterator();

         while (var4.hasNext()) {
            var index: Int = var4.nextInt();
            accumulator = operation.invoke(index, accumulator, `$this$reduceIndexed`.charAt(index)) as Character;
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun CharSequence.reduceIndexedOrNull(operation: (Int, Char, Char) -> Char): Char? {
      if (`$this$reduceIndexedOrNull`.length() == 0) {
         return null;
      } else {
         var accumulator: Char = `$this$reduceIndexedOrNull`.charAt(0);
         var var4: IntIterator = new IntRange(1, StringsKt.getLastIndex(`$this$reduceIndexedOrNull`)).iterator();

         while (var4.hasNext()) {
            var index: Int = var4.nextInt();
            accumulator = operation.invoke(index, accumulator, `$this$reduceIndexedOrNull`.charAt(index)) as Character;
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public inline fun CharSequence.reduceOrNull(operation: (Char, Char) -> Char): Char? {
      if (`$this$reduceOrNull`.length() == 0) {
         return null;
      } else {
         var accumulator: Char = `$this$reduceOrNull`.charAt(0);
         var var4: IntIterator = new IntRange(1, StringsKt.getLastIndex(`$this$reduceOrNull`)).iterator();

         while (var4.hasNext()) {
            accumulator = operation.invoke(accumulator, `$this$reduceOrNull`.charAt(var4.nextInt())) as Character;
         }

         return accumulator;
      }
   }

   @JvmStatic
   public inline fun CharSequence.reduceRight(operation: (Char, Char) -> Char): Char {
      var index: Int = StringsKt.getLastIndex(`$this$reduceRight`);
      if (index < 0) {
         throw new UnsupportedOperationException("Empty char sequence can't be reduced.");
      } else {
         var accumulator: Char = `$this$reduceRight`.charAt(index--);

         while (index >= 0) {
            accumulator = operation.invoke(`$this$reduceRight`.charAt(index--), accumulator) as Character;
         }

         return accumulator;
      }
   }

   @JvmStatic
   public inline fun CharSequence.reduceRightIndexed(operation: (Int, Char, Char) -> Char): Char {
      var index: Int = StringsKt.getLastIndex(`$this$reduceRightIndexed`);
      if (index < 0) {
         throw new UnsupportedOperationException("Empty char sequence can't be reduced.");
      } else {
         var accumulator: Char;
         for (accumulator = $this$reduceRightIndexed.charAt(index--); index >= 0; index--) {
            accumulator = operation.invoke(index, `$this$reduceRightIndexed`.charAt(index), accumulator) as Character;
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun CharSequence.reduceRightIndexedOrNull(operation: (Int, Char, Char) -> Char): Char? {
      var index: Int = StringsKt.getLastIndex(`$this$reduceRightIndexedOrNull`);
      if (index < 0) {
         return null;
      } else {
         var accumulator: Char;
         for (accumulator = $this$reduceRightIndexedOrNull.charAt(index--); index >= 0; index--) {
            accumulator = operation.invoke(index, `$this$reduceRightIndexedOrNull`.charAt(index), accumulator) as Character;
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public inline fun CharSequence.reduceRightOrNull(operation: (Char, Char) -> Char): Char? {
      var index: Int = StringsKt.getLastIndex(`$this$reduceRightOrNull`);
      if (index < 0) {
         return null;
      } else {
         var accumulator: Char = `$this$reduceRightOrNull`.charAt(index--);

         while (index >= 0) {
            accumulator = operation.invoke(`$this$reduceRightOrNull`.charAt(index--), accumulator) as Character;
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun <R> CharSequence.runningFold(initial: R, operation: (R, Char) -> R): List<R> {
      if (`$this$runningFold`.length() == 0) {
         return (java.util.List<R>)CollectionsKt.listOf(initial);
      } else {
         var accumulator: ArrayList = new ArrayList(`$this$runningFold`.length() + 1);
         accumulator.add(initial);
         var result: ArrayList = accumulator;
         var var8: Any = initial;

         for (int $this$runningFold_u24lambda_u2417 = 0; $this$runningFold_u24lambda_u2417 < $this$runningFold.length(); $this$runningFold_u24lambda_u2417++) {
            var8 = operation.invoke(var8, `$this$runningFold`.charAt(`$this$runningFold_u24lambda_u2417`));
            result.add(var8);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun <R> CharSequence.runningFoldIndexed(initial: R, operation: (Int, R, Char) -> R): List<R> {
      if (`$this$runningFoldIndexed`.length() == 0) {
         return (java.util.List<R>)CollectionsKt.listOf(initial);
      } else {
         var accumulator: ArrayList = new ArrayList(`$this$runningFoldIndexed`.length() + 1);
         accumulator.add(initial);
         var result: ArrayList = accumulator;
         var var8: Any = initial;
         var index: Int = 0;

         for (int var9 = $this$runningFoldIndexed.length(); index < var9; index++) {
            var8 = operation.invoke(index, var8, `$this$runningFoldIndexed`.charAt(index));
            result.add(var8);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun CharSequence.runningReduce(operation: (Char, Char) -> Char): List<Char> {
      if (`$this$runningReduce`.length() == 0) {
         return CollectionsKt.emptyList();
      } else {
         var var8: Char = `$this$runningReduce`.charAt(0);
         var index: ArrayList = new ArrayList(`$this$runningReduce`.length());
         index.add(var8);
         var result: ArrayList = index;
         var var9: Int = 1;

         for (int $this$runningReduce_u24lambda_u2419 = $this$runningReduce.length(); index < $this$runningReduce_u24lambda_u2419; index++) {
            var8 = operation.invoke(var8, `$this$runningReduce`.charAt(var9)) as Character;
            result.add(var8);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun CharSequence.runningReduceIndexed(operation: (Int, Char, Char) -> Char): List<Char> {
      if (`$this$runningReduceIndexed`.length() == 0) {
         return CollectionsKt.emptyList();
      } else {
         var var8: Char = `$this$runningReduceIndexed`.charAt(0);
         var index: ArrayList = new ArrayList(`$this$runningReduceIndexed`.length());
         index.add(var8);
         var result: ArrayList = index;
         var var9: Int = 1;

         for (int $this$runningReduceIndexed_u24lambda_u2420 = $this$runningReduceIndexed.length(); index < $this$runningReduceIndexed_u24lambda_u2420; index++) {
            var8 = operation.invoke(var9, var8, `$this$runningReduceIndexed`.charAt(var9)) as Character;
            result.add(var8);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public inline fun <R> CharSequence.scan(initial: R, operation: (R, Char) -> R): List<R> {
      var `$this$runningFold$iv`: java.lang.CharSequence = `$this$scan`;
      var var10000: java.util.List;
      if (`$this$scan`.length() == 0) {
         var10000 = CollectionsKt.listOf(initial);
      } else {
         var `accumulator$iv`: ArrayList = new ArrayList(`$this$scan`.length() + 1);
         `accumulator$iv`.add(initial);
         var `result$iv`: ArrayList = `accumulator$iv`;
         var var10: Any = initial;

         for (int $this$runningFold_u24lambda_u2417$iv = 0;
            $this$runningFold_u24lambda_u2417$iv < $this$runningFold$iv.length();
            $this$runningFold_u24lambda_u2417$iv++
         ) {
            var10 = operation.invoke(var10, `$this$runningFold$iv`.charAt(`$this$runningFold_u24lambda_u2417$iv`));
            `result$iv`.add(var10);
         }

         var10000 = `result$iv`;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public inline fun <R> CharSequence.scanIndexed(initial: R, operation: (Int, R, Char) -> R): List<R> {
      var `$this$runningFoldIndexed$iv`: java.lang.CharSequence = `$this$scanIndexed`;
      var var10000: java.util.List;
      if (`$this$scanIndexed`.length() == 0) {
         var10000 = CollectionsKt.listOf(initial);
      } else {
         var `accumulator$iv`: ArrayList = new ArrayList(`$this$scanIndexed`.length() + 1);
         `accumulator$iv`.add(initial);
         var `result$iv`: ArrayList = `accumulator$iv`;
         var var10: Any = initial;
         var `index$iv`: Int = 0;

         for (int var11 = $this$scanIndexed.length(); index$iv < var11; index$iv++) {
            var10 = operation.invoke(`index$iv`, var10, `$this$runningFoldIndexed$iv`.charAt(`index$iv`));
            `result$iv`.add(var10);
         }

         var10000 = `result$iv`;
      }

      return var10000;
   }

   @Deprecated(message = "Use sumOf instead.", replaceWith = @ReplaceWith(expression = "this.sumOf(selector)", imports = []))
   @DeprecatedSinceKotlin(warningSince = "1.5")
   @JvmStatic
   public inline fun CharSequence.sumBy(selector: (Char) -> Int): Int {
      var sum: Int = 0;

      for (int var4 = 0; var4 < $this$sumBy.length(); var4++) {
         sum += (selector.invoke(`$this$sumBy`.charAt(var4)) as java.lang.Number).intValue();
      }

      return sum;
   }

   @Deprecated(message = "Use sumOf instead.", replaceWith = @ReplaceWith(expression = "this.sumOf(selector)", imports = []))
   @DeprecatedSinceKotlin(warningSince = "1.5")
   @JvmStatic
   public inline fun CharSequence.sumByDouble(selector: (Char) -> Double): Double {
      var sum: Double = 0.0;

      for (int var5 = 0; var5 < $this$sumByDouble.length(); var5++) {
         sum += (selector.invoke(`$this$sumByDouble`.charAt(var5)) as java.lang.Number).doubleValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfDouble")
   @InlineOnly
   @JvmStatic
   public inline fun CharSequence.sumOf(selector: (Char) -> Double): Double {
      var sum: Double = 0.0;

      for (int var4 = 0; var4 < $this$sumOf.length(); var4++) {
         sum += (selector.invoke(`$this$sumOf`.charAt(var4)) as java.lang.Number).doubleValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfInt")
   @InlineOnly
   @JvmStatic
   public inline fun CharSequence.sumOf(selector: (Char) -> Int): Int {
      var sum: Int = 0;

      for (int var3 = 0; var3 < $this$sumOf.length(); var3++) {
         sum += (selector.invoke(`$this$sumOf`.charAt(var3)) as java.lang.Number).intValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfLong")
   @InlineOnly
   @JvmStatic
   public inline fun CharSequence.sumOf(selector: (Char) -> Long): Long {
      var sum: Long = 0L;

      for (int var4 = 0; var4 < $this$sumOf.length(); var4++) {
         sum += (selector.invoke(`$this$sumOf`.charAt(var4)) as java.lang.Number).longValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.5")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfUInt")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun CharSequence.sumOf(selector: (Char) -> UInt): UInt {
      var sum: Int = UInt.constructor-impl(0);

      for (int var3 = 0; var3 < $this$sumOf.length(); var3++) {
         sum = UInt.constructor-impl(sum + (selector.invoke(`$this$sumOf`.charAt(var3)) as UInt).unbox-impl());
      }

      return sum;
   }

   @SinceKotlin(version = "1.5")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfULong")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun CharSequence.sumOf(selector: (Char) -> ULong): ULong {
      var sum: Long = ULong.constructor-impl(0L);

      for (int var4 = 0; var4 < $this$sumOf.length(); var4++) {
         sum = ULong.constructor-impl(sum + (selector.invoke(`$this$sumOf`.charAt(var4)) as ULong).unbox-impl());
      }

      return sum;
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun CharSequence.chunked(size: Int): List<String> {
      return StringsKt.windowed(`$this$chunked`, size, size, true);
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun <R> CharSequence.chunked(size: Int, transform: (CharSequence) -> R): List<R> {
      return StringsKt.windowed(`$this$chunked`, size, size, true, transform);
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun CharSequence.chunkedSequence(size: Int): Sequence<String> {
      return StringsKt.chunkedSequence(`$this$chunkedSequence`, size, <unrepresentable>.INSTANCE);
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun <R> CharSequence.chunkedSequence(size: Int, transform: (CharSequence) -> R): Sequence<R> {
      return StringsKt.windowedSequence(`$this$chunkedSequence`, size, size, true, transform);
   }

   @JvmStatic
   public inline fun CharSequence.partition(predicate: (Char) -> Boolean): Pair<CharSequence, CharSequence> {
      var first: StringBuilder = new StringBuilder();
      var second: StringBuilder = new StringBuilder();

      for (int var5 = 0; var5 < $this$partition.length(); var5++) {
         var element: Char = `$this$partition`.charAt(var5);
         if (predicate.invoke(element) as java.lang.Boolean) {
            first.append(element);
         } else {
            second.append(element);
         }
      }

      return new Pair<>(first, second);
   }

   @JvmStatic
   public inline fun String.partition(predicate: (Char) -> Boolean): Pair<String, String> {
      var first: StringBuilder = new StringBuilder();
      var second: StringBuilder = new StringBuilder();
      var var5: Int = 0;

      for (int var6 = $this$partition.length(); var5 < var6; var5++) {
         var element: Char = `$this$partition`.charAt(var5);
         if (predicate.invoke(element) as java.lang.Boolean) {
            first.append(element);
         } else {
            second.append(element);
         }
      }

      var var10002: java.lang.String = first.toString();
      var var10003: java.lang.String = second.toString();
      return new Pair<>(var10002, var10003);
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun CharSequence.windowed(size: Int, step: Int, partialWindows: Boolean): List<String> {
      return StringsKt.windowed(`$this$windowed`, size, step, partialWindows, <unrepresentable>.INSTANCE);
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun <R> CharSequence.windowed(size: Int, step: Int, partialWindows: Boolean, transform: (CharSequence) -> R): List<R> {
      SlidingWindowKt.checkWindowSizeStep(size, step);
      var thisSize: Int = `$this$windowed`.length();
      var result: ArrayList = new ArrayList(thisSize / step + (if (thisSize % step == 0) 0 else 1));

      for (int index = 0; 0 <= index && index < thisSize; index += step) {
         var end: Int = index + size;
         var var10000: Int;
         if (index + size >= 0 && index + size <= thisSize) {
            var10000 = end;
         } else {
            if (!partialWindows) {
               break;
            }

            var10000 = thisSize;
         }

         result.add(transform.invoke(`$this$windowed`.subSequence(index, var10000)));
      }

      return result;
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun CharSequence.windowedSequence(size: Int, step: Int, partialWindows: Boolean): Sequence<String> {
      return StringsKt.windowedSequence(`$this$windowedSequence`, size, step, partialWindows, <unrepresentable>.INSTANCE);
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun <R> CharSequence.windowedSequence(size: Int, step: Int, partialWindows: Boolean, transform: (CharSequence) -> R): Sequence<R> {
      SlidingWindowKt.checkWindowSizeStep(size, step);
      return SequencesKt.map(
         CollectionsKt.asSequence(
            RangesKt.step(
               if (partialWindows) StringsKt.getIndices(`$this$windowedSequence`) else RangesKt.until(0, `$this$windowedSequence`.length() - size + 1), step
            )
         ),
         (
            new Function1<Integer, R>(size, `$this$windowedSequence`, transform) {
               {
                  super(1);
                  this.$size = `$size`;
                  this.$this_windowedSequence = `$receiver`;
                  this.$transform = `$transform`;
               }

               public final R invoke(int index) {
                  var end: Int = index + this.$size;
                  return (R)this.$transform
                     .invoke(
                        this.$this_windowedSequence
                           .subSequence(
                              index,
                              if (index + this.$size >= 0 && index + this.$size <= this.$this_windowedSequence.length())
                                 end
                                 else
                                 this.$this_windowedSequence.length()
                           )
                     );
               }
            }
         ) as Function1
      );
   }

   @JvmStatic
   public infix fun CharSequence.zip(other: CharSequence): List<Pair<Char, Char>> {
      var `$this$zip$iv`: java.lang.CharSequence = `$this$zip`;
      var `length$iv`: Int = Math.min(`$this$zip`.length(), other.length());
      var `list$iv`: ArrayList = new ArrayList(`length$iv`);

      for (int i$iv = 0; i$iv < length$iv; i$iv++) {
         `list$iv`.add(TuplesKt.to(`$this$zip$iv`.charAt(`i$iv`), other.charAt(`i$iv`)));
      }

      return `list$iv`;
   }

   @JvmStatic
   public inline fun <V> CharSequence.zip(other: CharSequence, transform: (Char, Char) -> V): List<V> {
      var length: Int = Math.min(`$this$zip`.length(), other.length());
      var list: ArrayList = new ArrayList(length);

      for (int i = 0; i < length; i++) {
         list.add(transform.invoke(`$this$zip`.charAt(i), other.charAt(i)));
      }

      return list;
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun CharSequence.zipWithNext(): List<Pair<Char, Char>> {
      var `$this$zipWithNext$iv`: java.lang.CharSequence = `$this$zipWithNext`;
      var `size$iv`: Int = `$this$zipWithNext`.length() - 1;
      var var10000: java.util.List;
      if (`size$iv` < 1) {
         var10000 = CollectionsKt.emptyList();
      } else {
         var `result$iv`: ArrayList = new ArrayList(`size$iv`);

         for (int index$iv = 0; index$iv < size$iv; index$iv++) {
            `result$iv`.add(TuplesKt.to(`$this$zipWithNext$iv`.charAt(`index$iv`), `$this$zipWithNext$iv`.charAt(`index$iv` + 1)));
         }

         var10000 = `result$iv`;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public inline fun <R> CharSequence.zipWithNext(transform: (Char, Char) -> R): List<R> {
      var size: Int = `$this$zipWithNext`.length() - 1;
      if (size < 1) {
         return CollectionsKt.emptyList();
      } else {
         var result: ArrayList = new ArrayList(size);

         for (int index = 0; index < size; index++) {
            result.add(transform.invoke(`$this$zipWithNext`.charAt(index), `$this$zipWithNext`.charAt(index + 1)));
         }

         return result;
      }
   }

   @JvmStatic
   public fun CharSequence.asIterable(): Iterable<Char> {
      return (java.lang.Iterable<Character>)(if (`$this$asIterable` is java.lang.String && `$this$asIterable`.length() == 0)
         CollectionsKt.emptyList()
         else
         new StringsKt___StringsKt$asIterable$$inlined$Iterable$1(`$this$asIterable`));
   }

   @JvmStatic
   public fun CharSequence.asSequence(): Sequence<Char> {
      return (Sequence<Character>)(if (`$this$asSequence` is java.lang.String && `$this$asSequence`.length() == 0)
         SequencesKt.emptySequence()
         else
         new StringsKt___StringsKt$asSequence$$inlined$Sequence$1(`$this$asSequence`));
   }

   open fun StringsKt___StringsKt() {
   }
}
