@file:SourceDebugExtension(["SMAP\nRegex.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Regex.kt\nkotlin/text/RegexKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,396:1\n1789#2,3:397\n*S KotlinDebug\n*F\n+ 1 Regex.kt\nkotlin/text/RegexKt\n*L\n19#1:397,3\n*E\n"])

package kotlin.text

import java.util.Collections
import java.util.EnumSet
import java.util.regex.Matcher
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension
import org.jetbrains.annotations.NotNull

private fun Iterable<FlagEnum>.toInt(): Int {
   var `accumulator$iv`: Int = 0;

   for (Object element$iv : $this$toInt) {
      `accumulator$iv` |= (`element$iv` as FlagEnum).getValue();
   }

   return `accumulator$iv`;
}

@JvmSynthetic
private inline fun <reified T> fromInt(value: Int): Set<T> where T : FlagEnum, T : Enum<T> {
   Intrinsics.reifiedOperationMarker(4, "T");
   var var2: EnumSet = EnumSet.allOf(java.lang.Enum::class.java);
   var `$this$fromInt_u24lambda_u241`: EnumSet = var2;
   var var10000: java.lang.Iterable = `$this$fromInt_u24lambda_u241`;
   Intrinsics.needClassReification();
   CollectionsKt.retainAll(var10000, (new Function1<T, java.lang.Boolean>(value) {
      {
         super(1);
         this.$value = `$value`;
      }

      @NotNull
      public final java.lang.Boolean invoke(T it) {
         return (this.$value and (it as FlagEnum).getMask()) == (it as FlagEnum).getValue();
      }
   }) as Function1);
   var var5: java.util.Set = Collections.unmodifiableSet(var2);
   return var5;
}

private fun Matcher.findNext(from: Int, input: CharSequence): MatchResult? {
   return if (!`$this$findNext`.find(from)) null else new MatcherMatchResult(`$this$findNext`, input);
}

private fun Matcher.matchEntire(input: CharSequence): MatchResult? {
   return if (!`$this$matchEntire`.matches()) null else new MatcherMatchResult(`$this$matchEntire`, input);
}

private fun java.util.regex.MatchResult.range(): IntRange {
   return RangesKt.until(`$this$range`.start(), `$this$range`.end());
}

private fun java.util.regex.MatchResult.range(groupIndex: Int): IntRange {
   return RangesKt.until(`$this$range`.start(groupIndex), `$this$range`.end(groupIndex));
}

@JvmSynthetic
fun `access$toInt`(`$receiver`: java.lang.Iterable): Int {
   return toInt(`$receiver`);
}

@JvmSynthetic
fun `access$findNext`(`$receiver`: Matcher, from: Int, input: java.lang.CharSequence): MatchResult {
   return findNext(`$receiver`, from, input);
}

@JvmSynthetic
fun `access$matchEntire`(`$receiver`: Matcher, input: java.lang.CharSequence): MatchResult {
   return matchEntire(`$receiver`, input);
}

@JvmSynthetic
fun `access$range`(`$receiver`: java.util.regex.MatchResult): IntRange {
   return range(`$receiver`);
}

@JvmSynthetic
fun `access$range`(`$receiver`: java.util.regex.MatchResult, groupIndex: Int): IntRange {
   return range(`$receiver`, groupIndex);
}

