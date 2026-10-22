package kotlin.text

import java.math.BigDecimal
import java.math.BigInteger
import java.util.SortedSet
import java.util.TreeSet
import kotlin.internal.InlineOnly
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\n_StringsJvm.kt\nKotlin\n*S Kotlin\n*F\n+ 1 _StringsJvm.kt\nkotlin/text/StringsKt___StringsJvmKt\n+ 2 _Strings.kt\nkotlin/text/StringsKt___StringsKt\n*L\n1#1,108:1\n1239#2,14:109\n1521#2,14:123\n*S KotlinDebug\n*F\n+ 1 _StringsJvm.kt\nkotlin/text/StringsKt___StringsJvmKt\n*L\n45#1:109,14\n66#1:123,14\n*E\n"])
// $VF: Class flags could not be determined
internal class StringsKt___StringsJvmKt : StringsKt__StringsKt {
   @InlineOnly
   @JvmStatic
   public inline fun CharSequence.elementAt(index: Int): Char {
      return `$this$elementAt`.charAt(index);
   }

   @JvmStatic
   public fun CharSequence.toSortedSet(): SortedSet<Char> {
      return StringsKt.toCollection(`$this$toSortedSet`, new TreeSet<>());
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfBigDecimal")
   @InlineOnly
   @JvmStatic
   public inline fun CharSequence.sumOf(selector: (Char) -> BigDecimal): BigDecimal {
      var var10000: BigDecimal = BigDecimal.valueOf(0L);
      var sum: BigDecimal = var10000;

      for (int var3 = 0; var3 < $this$sumOf.length(); var3++) {
         var10000 = sum.add(selector.invoke(`$this$sumOf`.charAt(var3)) as BigDecimal);
         sum = var10000;
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfBigInteger")
   @InlineOnly
   @JvmStatic
   public inline fun CharSequence.sumOf(selector: (Char) -> BigInteger): BigInteger {
      var var10000: BigInteger = BigInteger.valueOf(0L);
      var sum: BigInteger = var10000;

      for (int var3 = 0; var3 < $this$sumOf.length(); var3++) {
         var10000 = sum.add(selector.invoke(`$this$sumOf`.charAt(var3)) as BigInteger);
         sum = var10000;
      }

      return sum;
   }

   open fun StringsKt___StringsJvmKt() {
   }
}
