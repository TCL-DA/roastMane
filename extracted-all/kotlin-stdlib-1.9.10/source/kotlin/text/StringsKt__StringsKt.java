package kotlin.text

import java.util.ArrayList
import kotlin.internal.InlineOnly
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

@SourceDebugExtension(["SMAP\nStrings.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Strings.kt\nkotlin/text/StringsKt__StringsKt\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,1486:1\n79#1,22:1487\n113#1,5:1509\n130#1,5:1514\n79#1,22:1519\n107#1:1541\n79#1,22:1542\n113#1,5:1564\n124#1:1569\n113#1,5:1570\n130#1,5:1575\n141#1:1580\n130#1,5:1581\n79#1,22:1586\n113#1,5:1608\n130#1,5:1613\n12554#2,2:1618\n12554#2,2:1620\n288#3,2:1622\n288#3,2:1624\n1549#3:1627\n1620#3,3:1628\n1549#3:1631\n1620#3,3:1632\n1#4:1626\n*S KotlinDebug\n*F\n+ 1 Strings.kt\nkotlin/text/StringsKt__StringsKt\n*L\n107#1:1487,22\n124#1:1509,5\n141#1:1514,5\n146#1:1519,22\n151#1:1541\n151#1:1542,22\n156#1:1564,5\n161#1:1569\n161#1:1570,5\n166#1:1575,5\n171#1:1580\n171#1:1581,5\n176#1:1586,22\n187#1:1608,5\n198#1:1613,5\n940#1:1618,2\n964#1:1620,2\n1003#1:1622,2\n1009#1:1624,2\n1309#1:1627\n1309#1:1628,3\n1334#1:1631\n1334#1:1632,3\n*E\n"])
// $VF: Class flags could not be determined
internal class StringsKt__StringsKt : StringsKt__StringsJVMKt {
   public final val indices: IntRange
      public final get() {
         return new IntRange(0, `$this$indices`.length() - 1);
      }

   public final val lastIndex: Int
      public final get() {
         return `$this$lastIndex`.length() - 1;
      }


   @JvmStatic
   public inline fun CharSequence.trim(predicate: (Char) -> Boolean): CharSequence {
      var startIndex: Int = 0;
      var endIndex: Int = `$this$trim`.length() - 1;
      var startFound: Boolean = false;

      while (startIndex <= endIndex) {
         var match: Boolean = predicate.invoke(`$this$trim`.charAt(if (!startFound) startIndex else endIndex)) as java.lang.Boolean;
         if (!startFound) {
            if (!match) {
               startFound = true;
            } else {
               startIndex++;
            }
         } else {
            if (!match) {
               break;
            }

            endIndex--;
         }
      }

      return `$this$trim`.subSequence(startIndex, endIndex + 1);
   }

   @JvmStatic
   public inline fun String.trim(predicate: (Char) -> Boolean): String {
      var `$this$trim$iv`: java.lang.CharSequence = `$this$trim`;
      var `startIndex$iv`: Int = 0;
      var `endIndex$iv`: Int = `$this$trim$iv`.length() - 1;
      var `startFound$iv`: Boolean = false;

      while (startIndex$iv <= endIndex$iv) {
         var `match$iv`: Boolean = predicate.invoke(`$this$trim$iv`.charAt(if (!`startFound$iv`) `startIndex$iv` else `endIndex$iv`)) as java.lang.Boolean;
         if (!`startFound$iv`) {
            if (!`match$iv`) {
               `startFound$iv` = true;
            } else {
               `startIndex$iv`++;
            }
         } else {
            if (!`match$iv`) {
               break;
            }

            `endIndex$iv`--;
         }
      }

      return `$this$trim$iv`.subSequence(`startIndex$iv`, `endIndex$iv` + 1).toString();
   }

   @JvmStatic
   public inline fun CharSequence.trimStart(predicate: (Char) -> Boolean): CharSequence {
      var index: Int = 0;

      for (int var4 = $this$trimStart.length(); index < var4; index++) {
         if (!predicate.invoke(`$this$trimStart`.charAt(index)) as java.lang.Boolean) {
            return `$this$trimStart`.subSequence(index, `$this$trimStart`.length());
         }
      }

      return "";
   }

   @JvmStatic
   public inline fun String.trimStart(predicate: (Char) -> Boolean): String {
      var `$this$trimStart$iv`: java.lang.CharSequence = `$this$trimStart`;
      var `index$iv`: Int = 0;
      var var6: Int = `$this$trimStart$iv`.length();

      var var10000: java.lang.CharSequence;
      while (true) {
         if (`index$iv` >= var6) {
            var10000 = "";
            break;
         }

         if (!predicate.invoke(`$this$trimStart$iv`.charAt(`index$iv`)) as java.lang.Boolean) {
            var10000 = `$this$trimStart$iv`.subSequence(`index$iv`, `$this$trimStart$iv`.length());
            break;
         }

         `index$iv`++;
      }

      return var10000.toString();
   }

   @JvmStatic
   public inline fun CharSequence.trimEnd(predicate: (Char) -> Boolean): CharSequence {
      var var3: Int = `$this$trimEnd`.length() + -1;
      if (0 <= var3) {
         do {
            var index: Int = var3--;
            if (!predicate.invoke(`$this$trimEnd`.charAt(index)) as java.lang.Boolean) {
               return `$this$trimEnd`.subSequence(0, index + 1);
            }
         } while (0 <= var3);
      }

      return "";
   }

   @JvmStatic
   public inline fun String.trimEnd(predicate: (Char) -> Boolean): String {
      var `$this$trimEnd$iv`: java.lang.CharSequence = `$this$trimEnd`;
      var var5: Int = `$this$trimEnd`.length() + -1;
      if (0 <= var5) {
         do {
            var `index$iv`: Int = var5--;
            if (!predicate.invoke(`$this$trimEnd$iv`.charAt(`index$iv`)) as java.lang.Boolean) {
               return `$this$trimEnd$iv`.subSequence(0, `index$iv` + 1).toString();
            }
         } while (0 <= var5);
      }

      return "".toString();
   }

   @JvmStatic
   public fun CharSequence.trim(chars: CharArray): CharSequence {
      var `$this$trim$iv`: java.lang.CharSequence = `$this$trim`;
      var `startIndex$iv`: Int = 0;
      var `endIndex$iv`: Int = `$this$trim`.length() - 1;
      var `startFound$iv`: Boolean = false;

      while (startIndex$iv <= endIndex$iv) {
         var var10: Boolean = ArraysKt.contains(chars, `$this$trim$iv`.charAt(if (!`startFound$iv`) `startIndex$iv` else `endIndex$iv`));
         if (!`startFound$iv`) {
            if (!var10) {
               `startFound$iv` = true;
            } else {
               `startIndex$iv`++;
            }
         } else {
            if (!var10) {
               break;
            }

            `endIndex$iv`--;
         }
      }

      return `$this$trim$iv`.subSequence(`startIndex$iv`, `endIndex$iv` + 1);
   }

   @JvmStatic
   public fun String.trim(chars: CharArray): String {
      var `$this$trim$iv$iv`: java.lang.CharSequence = `$this$trim`;
      var `startIndex$iv$iv`: Int = 0;
      var `endIndex$iv$iv`: Int = `$this$trim$iv$iv`.length() - 1;
      var `startFound$iv$iv`: Boolean = false;

      while (startIndex$iv$iv <= endIndex$iv$iv) {
         var var12: Boolean = ArraysKt.contains(chars, `$this$trim$iv$iv`.charAt(if (!`startFound$iv$iv`) `startIndex$iv$iv` else `endIndex$iv$iv`));
         if (!`startFound$iv$iv`) {
            if (!var12) {
               `startFound$iv$iv` = true;
            } else {
               `startIndex$iv$iv`++;
            }
         } else {
            if (!var12) {
               break;
            }

            `endIndex$iv$iv`--;
         }
      }

      return `$this$trim$iv$iv`.subSequence(`startIndex$iv$iv`, `endIndex$iv$iv` + 1).toString();
   }

   @JvmStatic
   public fun CharSequence.trimStart(chars: CharArray): CharSequence {
      var `$this$trimStart$iv`: java.lang.CharSequence = `$this$trimStart`;
      var `index$iv`: Int = 0;
      var var5: Int = `$this$trimStart`.length();

      var var10000: java.lang.CharSequence;
      while (true) {
         if (`index$iv` >= var5) {
            var10000 = "";
            break;
         }

         if (!ArraysKt.contains(chars, `$this$trimStart$iv`.charAt(`index$iv`))) {
            var10000 = `$this$trimStart$iv`.subSequence(`index$iv`, `$this$trimStart$iv`.length());
            break;
         }

         `index$iv`++;
      }

      return var10000;
   }

   @JvmStatic
   public fun String.trimStart(chars: CharArray): String {
      var `$this$trimStart$iv$iv`: java.lang.CharSequence = `$this$trimStart`;
      var `index$iv$iv`: Int = 0;
      var var7: Int = `$this$trimStart$iv$iv`.length();

      var var10000: java.lang.CharSequence;
      while (true) {
         if (`index$iv$iv` >= var7) {
            var10000 = "";
            break;
         }

         if (!ArraysKt.contains(chars, `$this$trimStart$iv$iv`.charAt(`index$iv$iv`))) {
            var10000 = `$this$trimStart$iv$iv`.subSequence(`index$iv$iv`, `$this$trimStart$iv$iv`.length());
            break;
         }

         `index$iv$iv`++;
      }

      return var10000.toString();
   }

   @JvmStatic
   public fun CharSequence.trimEnd(chars: CharArray): CharSequence {
      var `$this$trimEnd$iv`: java.lang.CharSequence = `$this$trimEnd`;
      var var4: Int = `$this$trimEnd`.length() + -1;
      if (0 <= var4) {
         do {
            var `index$iv`: Int = var4--;
            if (!ArraysKt.contains(chars, `$this$trimEnd$iv`.charAt(`index$iv`))) {
               return `$this$trimEnd$iv`.subSequence(0, `index$iv` + 1);
            }
         } while (0 <= var4);
      }

      return "";
   }

   @JvmStatic
   public fun String.trimEnd(chars: CharArray): String {
      var `$this$trimEnd$iv$iv`: java.lang.CharSequence = `$this$trimEnd`;
      var var6: Int = `$this$trimEnd`.length() + -1;
      if (0 <= var6) {
         do {
            var `index$iv$iv`: Int = var6--;
            if (!ArraysKt.contains(chars, `$this$trimEnd$iv$iv`.charAt(`index$iv$iv`))) {
               return `$this$trimEnd$iv$iv`.subSequence(0, `index$iv$iv` + 1).toString();
            }
         } while (0 <= var6);
      }

      return "".toString();
   }

   @JvmStatic
   public fun CharSequence.trim(): CharSequence {
      var `$this$trim$iv`: java.lang.CharSequence = `$this$trim`;
      var `startIndex$iv`: Int = 0;
      var `endIndex$iv`: Int = `$this$trim`.length() - 1;
      var `startFound$iv`: Boolean = false;

      while (startIndex$iv <= endIndex$iv) {
         var var9: Boolean = CharsKt.isWhitespace(`$this$trim$iv`.charAt(if (!`startFound$iv`) `startIndex$iv` else `endIndex$iv`));
         if (!`startFound$iv`) {
            if (!var9) {
               `startFound$iv` = true;
            } else {
               `startIndex$iv`++;
            }
         } else {
            if (!var9) {
               break;
            }

            `endIndex$iv`--;
         }
      }

      return `$this$trim$iv`.subSequence(`startIndex$iv`, `endIndex$iv` + 1);
   }

   @InlineOnly
   @JvmStatic
   public inline fun String.trim(): String {
      return StringsKt.trim(`$this$trim`).toString();
   }

   @JvmStatic
   public fun CharSequence.trimStart(): CharSequence {
      var `$this$trimStart$iv`: java.lang.CharSequence = `$this$trimStart`;
      var `index$iv`: Int = 0;
      var var4: Int = `$this$trimStart`.length();

      var var10000: java.lang.CharSequence;
      while (true) {
         if (`index$iv` >= var4) {
            var10000 = "";
            break;
         }

         if (!CharsKt.isWhitespace(`$this$trimStart$iv`.charAt(`index$iv`))) {
            var10000 = `$this$trimStart$iv`.subSequence(`index$iv`, `$this$trimStart$iv`.length());
            break;
         }

         `index$iv`++;
      }

      return var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline fun String.trimStart(): String {
      return StringsKt.trimStart(`$this$trimStart`).toString();
   }

   @JvmStatic
   public fun CharSequence.trimEnd(): CharSequence {
      var `$this$trimEnd$iv`: java.lang.CharSequence = `$this$trimEnd`;
      var var3: Int = `$this$trimEnd`.length() + -1;
      if (0 <= var3) {
         do {
            var `index$iv`: Int = var3--;
            if (!CharsKt.isWhitespace(`$this$trimEnd$iv`.charAt(`index$iv`))) {
               return `$this$trimEnd$iv`.subSequence(0, `index$iv` + 1);
            }
         } while (0 <= var3);
      }

      return "";
   }

   @InlineOnly
   @JvmStatic
   public inline fun String.trimEnd(): String {
      return StringsKt.trimEnd(`$this$trimEnd`).toString();
   }

   @JvmStatic
   public fun CharSequence.padStart(length: Int, padChar: Char): CharSequence {
      if (length < 0) {
         throw new IllegalArgumentException("Desired length " + length + " is less than zero.");
      } else if (length <= `$this$padStart`.length()) {
         return `$this$padStart`.subSequence(0, `$this$padStart`.length());
      } else {
         var sb: StringBuilder = new StringBuilder(length);
         var var4: IntIterator = new IntRange(1, length - `$this$padStart`.length()).iterator();

         while (var4.hasNext()) {
            var i: Int = var4.nextInt();
            sb.append(padChar);
         }

         sb.append(`$this$padStart`);
         return sb;
      }
   }

   @JvmStatic
   public fun String.padStart(length: Int, padChar: Char): String {
      return StringsKt.padStart(`$this$padStart`, length, padChar).toString();
   }

   @JvmStatic
   public fun CharSequence.padEnd(length: Int, padChar: Char): CharSequence {
      if (length < 0) {
         throw new IllegalArgumentException("Desired length " + length + " is less than zero.");
      } else if (length <= `$this$padEnd`.length()) {
         return `$this$padEnd`.subSequence(0, `$this$padEnd`.length());
      } else {
         var sb: StringBuilder = new StringBuilder(length);
         sb.append(`$this$padEnd`);
         var var4: IntIterator = new IntRange(1, length - `$this$padEnd`.length()).iterator();

         while (var4.hasNext()) {
            var i: Int = var4.nextInt();
            sb.append(padChar);
         }

         return sb;
      }
   }

   @JvmStatic
   public fun String.padEnd(length: Int, padChar: Char): String {
      return StringsKt.padEnd(`$this$padEnd`, length, padChar).toString();
   }

   @InlineOnly
   @JvmStatic
   public inline fun CharSequence?.isNullOrEmpty(): Boolean {
      contract {
         returns(false) implies (this != null)
      }

      return `$this$isNullOrEmpty` == null || `$this$isNullOrEmpty`.length() == 0;
   }

   @InlineOnly
   @JvmStatic
   public inline fun CharSequence.isEmpty(): Boolean {
      return `$this$isEmpty`.length() == 0;
   }

   @InlineOnly
   @JvmStatic
   public inline fun CharSequence.isNotEmpty(): Boolean {
      return `$this$isNotEmpty`.length() > 0;
   }

   @InlineOnly
   @JvmStatic
   public inline fun CharSequence.isNotBlank(): Boolean {
      return !StringsKt.isBlank(`$this$isNotBlank`);
   }

   @InlineOnly
   @JvmStatic
   public inline fun CharSequence?.isNullOrBlank(): Boolean {
      contract {
         returns(false) implies (this != null)
      }

      return `$this$isNullOrBlank` == null || StringsKt.isBlank(`$this$isNullOrBlank`);
   }

   @JvmStatic
   public operator fun CharSequence.iterator(): CharIterator {
      return new CharIterator(`$this$iterator`) {
         private int index;

         {
            this.$this_iterator = `$receiver`;
         }

         @Override
         public char nextChar() {
            return this.$this_iterator.charAt(this.index++);
         }

         @Override
         public boolean hasNext() {
            return this.index < this.$this_iterator.length();
         }
      };
   }

   @InlineOnly
   @JvmStatic
   public inline fun String?.orEmpty(): String {
      var var10000: java.lang.String = `$this$orEmpty`;
      if (`$this$orEmpty` == null) {
         var10000 = "";
      }

      return var10000;
   }

   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   public inline fun <C, R> C.ifEmpty(defaultValue: () -> R): R where C : CharSequence, C : R {
      return (R)(if (`$this$ifEmpty`.length() == 0) defaultValue.invoke() else `$this$ifEmpty`);
   }

   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   public inline fun <C, R> C.ifBlank(defaultValue: () -> R): R where C : CharSequence, C : R {
      return (R)(if (StringsKt.isBlank(`$this$ifBlank`)) defaultValue.invoke() else `$this$ifBlank`);
   }

   @JvmStatic
   public fun CharSequence.hasSurrogatePairAt(index: Int): Boolean {
      return new IntRange(0, `$this$hasSurrogatePairAt`.length() - 2).contains(index)
         && Character.isHighSurrogate(`$this$hasSurrogatePairAt`.charAt(index))
         && Character.isLowSurrogate(`$this$hasSurrogatePairAt`.charAt(index + 1));
   }

   @JvmStatic
   public fun String.substring(range: IntRange): String {
      var var10000: java.lang.String = `$this$substring`.substring(range.getStart(), range.getEndInclusive() + 1);
      return var10000;
   }

   @JvmStatic
   public fun CharSequence.subSequence(range: IntRange): CharSequence {
      return `$this$subSequence`.subSequence(range.getStart(), range.getEndInclusive() + 1);
   }

   @Deprecated(message = "Use parameters named startIndex and endIndex.", replaceWith = @ReplaceWith(expression = "subSequence(startIndex = start, endIndex = end)", imports = []))
   @InlineOnly
   @JvmStatic
   public inline fun String.subSequence(start: Int, end: Int): CharSequence {
      return `$this$subSequence`.subSequence(start, end);
   }

   @InlineOnly
   @JvmStatic
   public inline fun CharSequence.substring(startIndex: Int, endIndex: Int): String {
      return `$this$substring`.subSequence(startIndex, endIndex).toString();
   }

   @JvmStatic
   public fun CharSequence.substring(range: IntRange): String {
      return `$this$substring`.subSequence(range.getStart(), range.getEndInclusive() + 1).toString();
   }

   @JvmStatic
   public fun String.substringBefore(delimiter: Char, missingDelimiterValue: String): String {
      var index: Int = StringsKt.indexOf$default(`$this$substringBefore`, delimiter, 0, false, 6, null);
      var var10000: java.lang.String;
      if (index == -1) {
         var10000 = missingDelimiterValue;
      } else {
         var10000 = `$this$substringBefore`.substring(0, index);
      }

      return var10000;
   }

   @JvmStatic
   public fun String.substringBefore(delimiter: String, missingDelimiterValue: String): String {
      var index: Int = StringsKt.indexOf$default(`$this$substringBefore`, delimiter, 0, false, 6, null);
      var var10000: java.lang.String;
      if (index == -1) {
         var10000 = missingDelimiterValue;
      } else {
         var10000 = `$this$substringBefore`.substring(0, index);
      }

      return var10000;
   }

   @JvmStatic
   public fun String.substringAfter(delimiter: Char, missingDelimiterValue: String): String {
      var index: Int = StringsKt.indexOf$default(`$this$substringAfter`, delimiter, 0, false, 6, null);
      var var10000: java.lang.String;
      if (index == -1) {
         var10000 = missingDelimiterValue;
      } else {
         var10000 = `$this$substringAfter`.substring(index + 1, `$this$substringAfter`.length());
      }

      return var10000;
   }

   @JvmStatic
   public fun String.substringAfter(delimiter: String, missingDelimiterValue: String): String {
      var index: Int = StringsKt.indexOf$default(`$this$substringAfter`, delimiter, 0, false, 6, null);
      var var10000: java.lang.String;
      if (index == -1) {
         var10000 = missingDelimiterValue;
      } else {
         var10000 = `$this$substringAfter`.substring(index + delimiter.length(), `$this$substringAfter`.length());
      }

      return var10000;
   }

   @JvmStatic
   public fun String.substringBeforeLast(delimiter: Char, missingDelimiterValue: String): String {
      var index: Int = StringsKt.lastIndexOf$default(`$this$substringBeforeLast`, delimiter, 0, false, 6, null);
      var var10000: java.lang.String;
      if (index == -1) {
         var10000 = missingDelimiterValue;
      } else {
         var10000 = `$this$substringBeforeLast`.substring(0, index);
      }

      return var10000;
   }

   @JvmStatic
   public fun String.substringBeforeLast(delimiter: String, missingDelimiterValue: String): String {
      var index: Int = StringsKt.lastIndexOf$default(`$this$substringBeforeLast`, delimiter, 0, false, 6, null);
      var var10000: java.lang.String;
      if (index == -1) {
         var10000 = missingDelimiterValue;
      } else {
         var10000 = `$this$substringBeforeLast`.substring(0, index);
      }

      return var10000;
   }

   @JvmStatic
   public fun String.substringAfterLast(delimiter: Char, missingDelimiterValue: String): String {
      var index: Int = StringsKt.lastIndexOf$default(`$this$substringAfterLast`, delimiter, 0, false, 6, null);
      var var10000: java.lang.String;
      if (index == -1) {
         var10000 = missingDelimiterValue;
      } else {
         var10000 = `$this$substringAfterLast`.substring(index + 1, `$this$substringAfterLast`.length());
      }

      return var10000;
   }

   @JvmStatic
   public fun String.substringAfterLast(delimiter: String, missingDelimiterValue: String): String {
      var index: Int = StringsKt.lastIndexOf$default(`$this$substringAfterLast`, delimiter, 0, false, 6, null);
      var var10000: java.lang.String;
      if (index == -1) {
         var10000 = missingDelimiterValue;
      } else {
         var10000 = `$this$substringAfterLast`.substring(index + delimiter.length(), `$this$substringAfterLast`.length());
      }

      return var10000;
   }

   @JvmStatic
   public fun CharSequence.replaceRange(startIndex: Int, endIndex: Int, replacement: CharSequence): CharSequence {
      if (endIndex < startIndex) {
         throw new IndexOutOfBoundsException("End index (" + endIndex + ") is less than start index (" + startIndex + ").");
      } else {
         var sb: StringBuilder = new StringBuilder();
         sb.append(replacement);
         return sb;
      }
   }

   @InlineOnly
   @JvmStatic
   public inline fun String.replaceRange(startIndex: Int, endIndex: Int, replacement: CharSequence): String {
      return StringsKt.replaceRange(`$this$replaceRange`, startIndex, endIndex, replacement).toString();
   }

   @JvmStatic
   public fun CharSequence.replaceRange(range: IntRange, replacement: CharSequence): CharSequence {
      return StringsKt.replaceRange(`$this$replaceRange`, range.getStart(), range.getEndInclusive() + 1, replacement);
   }

   @InlineOnly
   @JvmStatic
   public inline fun String.replaceRange(range: IntRange, replacement: CharSequence): String {
      return StringsKt.replaceRange(`$this$replaceRange`, range, replacement).toString();
   }

   @JvmStatic
   public fun CharSequence.removeRange(startIndex: Int, endIndex: Int): CharSequence {
      if (endIndex < startIndex) {
         throw new IndexOutOfBoundsException("End index (" + endIndex + ") is less than start index (" + startIndex + ").");
      } else if (endIndex == startIndex) {
         return `$this$removeRange`.subSequence(0, `$this$removeRange`.length());
      } else {
         var sb: StringBuilder = new StringBuilder(`$this$removeRange`.length() - (endIndex - startIndex));
         return sb;
      }
   }

   @InlineOnly
   @JvmStatic
   public inline fun String.removeRange(startIndex: Int, endIndex: Int): String {
      return StringsKt.removeRange(`$this$removeRange`, startIndex, endIndex).toString();
   }

   @JvmStatic
   public fun CharSequence.removeRange(range: IntRange): CharSequence {
      return StringsKt.removeRange(`$this$removeRange`, range.getStart(), range.getEndInclusive() + 1);
   }

   @InlineOnly
   @JvmStatic
   public inline fun String.removeRange(range: IntRange): String {
      return StringsKt.removeRange(`$this$removeRange`, range).toString();
   }

   @JvmStatic
   public fun CharSequence.removePrefix(prefix: CharSequence): CharSequence {
      return if (StringsKt.startsWith$default(`$this$removePrefix`, prefix, false, 2, null))
         `$this$removePrefix`.subSequence(prefix.length(), `$this$removePrefix`.length())
         else
         `$this$removePrefix`.subSequence(0, `$this$removePrefix`.length());
   }

   @JvmStatic
   public fun String.removePrefix(prefix: CharSequence): String {
      if (StringsKt.startsWith$default(`$this$removePrefix`, prefix, false, 2, null)) {
         var var10000: java.lang.String = `$this$removePrefix`.substring(prefix.length());
         return var10000;
      } else {
         return `$this$removePrefix`;
      }
   }

   @JvmStatic
   public fun CharSequence.removeSuffix(suffix: CharSequence): CharSequence {
      return if (StringsKt.endsWith$default(`$this$removeSuffix`, suffix, false, 2, null))
         `$this$removeSuffix`.subSequence(0, `$this$removeSuffix`.length() - suffix.length())
         else
         `$this$removeSuffix`.subSequence(0, `$this$removeSuffix`.length());
   }

   @JvmStatic
   public fun String.removeSuffix(suffix: CharSequence): String {
      if (StringsKt.endsWith$default(`$this$removeSuffix`, suffix, false, 2, null)) {
         var var10000: java.lang.String = `$this$removeSuffix`.substring(0, `$this$removeSuffix`.length() - suffix.length());
         return var10000;
      } else {
         return `$this$removeSuffix`;
      }
   }

   @JvmStatic
   public fun CharSequence.removeSurrounding(prefix: CharSequence, suffix: CharSequence): CharSequence {
      return if (`$this$removeSurrounding`.length() >= prefix.length() + suffix.length()
            && StringsKt.startsWith$default(`$this$removeSurrounding`, prefix, false, 2, null)
            && StringsKt.endsWith$default(`$this$removeSurrounding`, suffix, false, 2, null))
         `$this$removeSurrounding`.subSequence(prefix.length(), `$this$removeSurrounding`.length() - suffix.length())
         else
         `$this$removeSurrounding`.subSequence(0, `$this$removeSurrounding`.length());
   }

   @JvmStatic
   public fun String.removeSurrounding(prefix: CharSequence, suffix: CharSequence): String {
      if (`$this$removeSurrounding`.length() >= prefix.length() + suffix.length()
         && StringsKt.startsWith$default(`$this$removeSurrounding`, prefix, false, 2, null)
         && StringsKt.endsWith$default(`$this$removeSurrounding`, suffix, false, 2, null)) {
         var var10000: java.lang.String = `$this$removeSurrounding`.substring(prefix.length(), `$this$removeSurrounding`.length() - suffix.length());
         return var10000;
      } else {
         return `$this$removeSurrounding`;
      }
   }

   @JvmStatic
   public fun CharSequence.removeSurrounding(delimiter: CharSequence): CharSequence {
      return StringsKt.removeSurrounding(`$this$removeSurrounding`, delimiter, delimiter);
   }

   @JvmStatic
   public fun String.removeSurrounding(delimiter: CharSequence): String {
      return StringsKt.removeSurrounding(`$this$removeSurrounding`, delimiter, delimiter);
   }

   @JvmStatic
   public fun String.replaceBefore(delimiter: Char, replacement: String, missingDelimiterValue: String): String {
      var index: Int = StringsKt.indexOf$default(`$this$replaceBefore`, delimiter, 0, false, 6, null);
      return if (index == -1) missingDelimiterValue else StringsKt.replaceRange(`$this$replaceBefore`, 0, index, replacement).toString();
   }

   @JvmStatic
   public fun String.replaceBefore(delimiter: String, replacement: String, missingDelimiterValue: String): String {
      var index: Int = StringsKt.indexOf$default(`$this$replaceBefore`, delimiter, 0, false, 6, null);
      return if (index == -1) missingDelimiterValue else StringsKt.replaceRange(`$this$replaceBefore`, 0, index, replacement).toString();
   }

   @JvmStatic
   public fun String.replaceAfter(delimiter: Char, replacement: String, missingDelimiterValue: String): String {
      var index: Int = StringsKt.indexOf$default(`$this$replaceAfter`, delimiter, 0, false, 6, null);
      return if (index == -1)
         missingDelimiterValue
         else
         StringsKt.replaceRange(`$this$replaceAfter`, index + 1, `$this$replaceAfter`.length(), replacement).toString();
   }

   @JvmStatic
   public fun String.replaceAfter(delimiter: String, replacement: String, missingDelimiterValue: String): String {
      var index: Int = StringsKt.indexOf$default(`$this$replaceAfter`, delimiter, 0, false, 6, null);
      return if (index == -1)
         missingDelimiterValue
         else
         StringsKt.replaceRange(`$this$replaceAfter`, index + delimiter.length(), `$this$replaceAfter`.length(), replacement).toString();
   }

   @JvmStatic
   public fun String.replaceAfterLast(delimiter: String, replacement: String, missingDelimiterValue: String): String {
      var index: Int = StringsKt.lastIndexOf$default(`$this$replaceAfterLast`, delimiter, 0, false, 6, null);
      return if (index == -1)
         missingDelimiterValue
         else
         StringsKt.replaceRange(`$this$replaceAfterLast`, index + delimiter.length(), `$this$replaceAfterLast`.length(), replacement).toString();
   }

   @JvmStatic
   public fun String.replaceAfterLast(delimiter: Char, replacement: String, missingDelimiterValue: String): String {
      var index: Int = StringsKt.lastIndexOf$default(`$this$replaceAfterLast`, delimiter, 0, false, 6, null);
      return if (index == -1)
         missingDelimiterValue
         else
         StringsKt.replaceRange(`$this$replaceAfterLast`, index + 1, `$this$replaceAfterLast`.length(), replacement).toString();
   }

   @JvmStatic
   public fun String.replaceBeforeLast(delimiter: Char, replacement: String, missingDelimiterValue: String): String {
      var index: Int = StringsKt.lastIndexOf$default(`$this$replaceBeforeLast`, delimiter, 0, false, 6, null);
      return if (index == -1) missingDelimiterValue else StringsKt.replaceRange(`$this$replaceBeforeLast`, 0, index, replacement).toString();
   }

   @JvmStatic
   public fun String.replaceBeforeLast(delimiter: String, replacement: String, missingDelimiterValue: String): String {
      var index: Int = StringsKt.lastIndexOf$default(`$this$replaceBeforeLast`, delimiter, 0, false, 6, null);
      return if (index == -1) missingDelimiterValue else StringsKt.replaceRange(`$this$replaceBeforeLast`, 0, index, replacement).toString();
   }

   @InlineOnly
   @JvmStatic
   public inline fun CharSequence.replace(regex: Regex, replacement: String): String {
      return regex.replace(`$this$replace`, replacement);
   }

   @InlineOnly
   @JvmStatic
   public inline fun CharSequence.replace(regex: Regex, noinline transform: (MatchResult) -> CharSequence): String {
      return regex.replace(`$this$replace`, transform);
   }

   @InlineOnly
   @JvmStatic
   public inline fun CharSequence.replaceFirst(regex: Regex, replacement: String): String {
      return regex.replaceFirst(`$this$replaceFirst`, replacement);
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "replaceFirstCharWithChar")
   @InlineOnly
   @JvmStatic
   public inline fun String.replaceFirstChar(transform: (Char) -> Char): String {
      var var5: java.lang.String;
      if (`$this$replaceFirstChar`.length() > 0) {
         var var2: Char = transform.invoke(`$this$replaceFirstChar`.charAt(0)) as Character;
         var5 = `$this$replaceFirstChar`.substring(1);
         var5 = var2 + var5;
      } else {
         var5 = `$this$replaceFirstChar`;
      }

      return var5;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "replaceFirstCharWithCharSequence")
   @InlineOnly
   @JvmStatic
   public inline fun String.replaceFirstChar(transform: (Char) -> CharSequence): String {
      var var4: java.lang.String;
      if (`$this$replaceFirstChar`.length() > 0) {
         var var10000: StringBuilder = new StringBuilder().append(transform.invoke(`$this$replaceFirstChar`.charAt(0)));
         var var10001: java.lang.String = `$this$replaceFirstChar`.substring(1);
         var4 = var10000.append(var10001).toString();
      } else {
         var4 = `$this$replaceFirstChar`;
      }

      return var4;
   }

   @InlineOnly
   @JvmStatic
   public inline infix fun CharSequence.matches(regex: Regex): Boolean {
      return regex.matches(`$this$matches`);
   }

   @JvmStatic
   internal fun CharSequence.regionMatchesImpl(thisOffset: Int, other: CharSequence, otherOffset: Int, length: Int, ignoreCase: Boolean): Boolean {
      if (otherOffset >= 0 && thisOffset >= 0 && thisOffset <= `$this$regionMatchesImpl`.length() - length && otherOffset <= other.length() - length) {
         for (int index = 0; index < length; index++) {
            if (!CharsKt.equals(`$this$regionMatchesImpl`.charAt(thisOffset + index), other.charAt(otherOffset + index), ignoreCase)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @JvmStatic
   public fun CharSequence.startsWith(char: Char, ignoreCase: Boolean): Boolean {
      return `$this$startsWith`.length() > 0 && CharsKt.equals(`$this$startsWith`.charAt(0), var1, ignoreCase);
   }

   @JvmStatic
   public fun CharSequence.endsWith(char: Char, ignoreCase: Boolean): Boolean {
      return `$this$endsWith`.length() > 0 && CharsKt.equals(`$this$endsWith`.charAt(StringsKt.getLastIndex(`$this$endsWith`)), var1, ignoreCase);
   }

   @JvmStatic
   public fun CharSequence.startsWith(prefix: CharSequence, ignoreCase: Boolean): Boolean {
      return if (!ignoreCase && `$this$startsWith` is java.lang.String && prefix is java.lang.String)
         StringsKt.startsWith$default(`$this$startsWith` as java.lang.String, prefix as java.lang.String, false, 2, null)
         else
         StringsKt.regionMatchesImpl(`$this$startsWith`, 0, prefix, 0, prefix.length(), ignoreCase);
   }

   @JvmStatic
   public fun CharSequence.startsWith(prefix: CharSequence, startIndex: Int, ignoreCase: Boolean): Boolean {
      return if (!ignoreCase && `$this$startsWith` is java.lang.String && prefix is java.lang.String)
         StringsKt.startsWith$default(`$this$startsWith` as java.lang.String, prefix as java.lang.String, startIndex, false, 4, null)
         else
         StringsKt.regionMatchesImpl(`$this$startsWith`, startIndex, prefix, 0, prefix.length(), ignoreCase);
   }

   @JvmStatic
   public fun CharSequence.endsWith(suffix: CharSequence, ignoreCase: Boolean): Boolean {
      return if (!ignoreCase && `$this$endsWith` is java.lang.String && suffix is java.lang.String)
         StringsKt.endsWith$default(`$this$endsWith` as java.lang.String, suffix as java.lang.String, false, 2, null)
         else
         StringsKt.regionMatchesImpl(`$this$endsWith`, `$this$endsWith`.length() - suffix.length(), suffix, 0, suffix.length(), ignoreCase);
   }

   @JvmStatic
   public fun CharSequence.commonPrefixWith(other: CharSequence, ignoreCase: Boolean): String {
      var shortestLength: Int = Math.min(`$this$commonPrefixWith`.length(), other.length());
      var i: Int = 0;

      while (i < shortestLength && CharsKt.equals($this$commonPrefixWith.charAt(i), other.charAt(i), ignoreCase)) {
         i++;
      }

      if (StringsKt.hasSurrogatePairAt(`$this$commonPrefixWith`, i - 1) || StringsKt.hasSurrogatePairAt(other, i - 1)) {
         i--;
      }

      return `$this$commonPrefixWith`.subSequence(0, i).toString();
   }

   @JvmStatic
   public fun CharSequence.commonSuffixWith(other: CharSequence, ignoreCase: Boolean): String {
      var thisLength: Int = `$this$commonSuffixWith`.length();
      var otherLength: Int = other.length();
      var shortestLength: Int = Math.min(thisLength, otherLength);
      var i: Int = 0;

      while (i < shortestLength && CharsKt.equals($this$commonSuffixWith.charAt(thisLength - i - 1), other.charAt(otherLength - i - 1), ignoreCase)) {
         i++;
      }

      if (StringsKt.hasSurrogatePairAt(`$this$commonSuffixWith`, thisLength - i - 1) || StringsKt.hasSurrogatePairAt(other, otherLength - i - 1)) {
         i--;
      }

      return `$this$commonSuffixWith`.subSequence(thisLength - i, thisLength).toString();
   }

   @JvmStatic
   public fun CharSequence.indexOfAny(chars: CharArray, startIndex: Int, ignoreCase: Boolean): Int {
      if (!ignoreCase && chars.length == 1 && `$this$indexOfAny` is java.lang.String) {
         return (`$this$indexOfAny` as java.lang.String).indexOf(ArraysKt.single(chars), startIndex);
      } else {
         var var4: IntIterator = new IntRange(RangesKt.coerceAtLeast(startIndex, 0), StringsKt.getLastIndex(`$this$indexOfAny`)).iterator();

         while (var4.hasNext()) {
            var index: Int = var4.nextInt();
            var charAtIndex: Char = `$this$indexOfAny`.charAt(index);
            var `$this$any$iv`: CharArray = chars;
            var var9: Int = 0;
            var var10: Int = chars.length;

            var var10000: Boolean;
            while (true) {
               if (var9 >= var10) {
                  var10000 = false;
                  break;
               }

               if (CharsKt.equals(`$this$any$iv`[var9], charAtIndex, ignoreCase)) {
                  var10000 = true;
                  break;
               }

               var9++;
            }

            if (var10000) {
               return index;
            }
         }

         return -1;
      }
   }

   @JvmStatic
   public fun CharSequence.lastIndexOfAny(chars: CharArray, startIndex: Int, ignoreCase: Boolean): Int {
      if (!ignoreCase && chars.length == 1 && `$this$lastIndexOfAny` is java.lang.String) {
         return (`$this$lastIndexOfAny` as java.lang.String).lastIndexOf(ArraysKt.single(chars), startIndex);
      } else {
         for (int index = RangesKt.coerceAtMost(startIndex, StringsKt.getLastIndex($this$lastIndexOfAny)); -1 < index; index--) {
            var charAtIndex: Char = `$this$lastIndexOfAny`.charAt(index);
            var `$this$any$iv`: CharArray = chars;
            var var8: Int = 0;
            var var9: Int = chars.length;

            var var10000: Boolean;
            while (true) {
               if (var8 >= var9) {
                  var10000 = false;
                  break;
               }

               if (CharsKt.equals(`$this$any$iv`[var8], charAtIndex, ignoreCase)) {
                  var10000 = true;
                  break;
               }

               var8++;
            }

            if (var10000) {
               return index;
            }
         }

         return -1;
      }
   }

   @JvmStatic
   private fun CharSequence.indexOf(other: CharSequence, startIndex: Int, endIndex: Int, ignoreCase: Boolean, last: Boolean): Int {
      var indices: IntProgression = if (!last)
         new IntRange(RangesKt.coerceAtLeast(startIndex, 0), RangesKt.coerceAtMost(endIndex, `$this$indexOf`.length()))
         else
         RangesKt.downTo(RangesKt.coerceAtMost(startIndex, StringsKt.getLastIndex(`$this$indexOf`)), RangesKt.coerceAtLeast(endIndex, 0));
      if (`$this$indexOf` is java.lang.String && other is java.lang.String) {
         var var10: Int = indices.getFirst();
         var var11: Int = indices.getLast();
         var var12: Int = indices.getStep();
         if (var12 > 0 && var10 <= var11 || var12 < 0 && var11 <= var10) {
            while (true) {
               if (StringsKt.regionMatches(other as java.lang.String, 0, `$this$indexOf` as java.lang.String, var10, other.length(), ignoreCase)) {
                  return var10;
               }

               if (var10 == var11) {
                  break;
               }

               var10 += var12;
            }
         }
      } else {
         var index: Int = indices.getFirst();
         var var8: Int = indices.getLast();
         var var9: Int = indices.getStep();
         if (var9 > 0 && index <= var8 || var9 < 0 && var8 <= index) {
            while (true) {
               if (StringsKt.regionMatchesImpl(other, 0, `$this$indexOf`, index, other.length(), ignoreCase)) {
                  return index;
               }

               if (index == var8) {
                  break;
               }

               index += var9;
            }
         }
      }

      return -1;
   }

   @JvmStatic
   private fun CharSequence.findAnyOf(strings: Collection<String>, startIndex: Int, ignoreCase: Boolean, last: Boolean): Pair<Int, String>? {
      if (!ignoreCase && strings.size() == 1) {
         var var16: java.lang.String = CollectionsKt.single(strings);
         var var18: Int = if (!last)
            StringsKt.indexOf$default(`$this$findAnyOf`, var16, startIndex, false, 4, null)
            else
            StringsKt.lastIndexOf$default(`$this$findAnyOf`, var16, startIndex, false, 4, null);
         return if (var18 < 0) null else TuplesKt.to(var18, var16);
      } else {
         var indices: IntProgression = if (!last)
            new IntRange(RangesKt.coerceAtLeast(startIndex, 0), `$this$findAnyOf`.length())
            else
            RangesKt.downTo(RangesKt.coerceAtMost(startIndex, StringsKt.getLastIndex(`$this$findAnyOf`)), 0);
         if (`$this$findAnyOf` is java.lang.String) {
            var index: Int = indices.getFirst();
            var var7: Int = indices.getLast();
            var var8: Int = indices.getStep();
            if (var8 > 0 && index <= var7 || var8 < 0 && var7 <= index) {
               while (true) {
                  var var12: java.util.Iterator = strings.iterator();

                  var var10000: Any;
                  while (true) {
                     if (!var12.hasNext()) {
                        var10000 = null;
                        break;
                     }

                     var `element$iv`: Any = var12.next();
                     if (StringsKt.regionMatches(
                        `element$iv` as java.lang.String,
                        0,
                        `$this$findAnyOf` as java.lang.String,
                        index,
                        (`element$iv` as java.lang.String).length(),
                        ignoreCase
                     )) {
                        var10000 = `element$iv`;
                        break;
                     }
                  }

                  var matchingString: java.lang.String = var10000 as java.lang.String;
                  if (var10000 as java.lang.String != null) {
                     return TuplesKt.to(index, matchingString);
                  }

                  if (index == var7) {
                     break;
                  }

                  index += var8;
               }
            }
         } else {
            var var17: Int = indices.getFirst();
            var var19: Int = indices.getLast();
            var var20: Int = indices.getStep();
            if (var20 > 0 && var17 <= var19 || var20 < 0 && var19 <= var17) {
               while (true) {
                  var var24: java.util.Iterator = strings.iterator();

                  var var28: Any;
                  while (true) {
                     if (!var24.hasNext()) {
                        var28 = null;
                        break;
                     }

                     var var25: Any = var24.next();
                     if (StringsKt.regionMatchesImpl(var25 as java.lang.String, 0, `$this$findAnyOf`, var17, (var25 as java.lang.String).length(), ignoreCase)) {
                        var28 = var25;
                        break;
                     }
                  }

                  var matchingStringx: java.lang.String = var28 as java.lang.String;
                  if (var28 as java.lang.String != null) {
                     return TuplesKt.to(var17, matchingStringx);
                  }

                  if (var17 == var19) {
                     break;
                  }

                  var17 += var20;
               }
            }
         }

         return null;
      }
   }

   @JvmStatic
   public fun CharSequence.findAnyOf(strings: Collection<String>, startIndex: Int, ignoreCase: Boolean): Pair<Int, String>? {
      return findAnyOf$StringsKt__StringsKt(`$this$findAnyOf`, strings, startIndex, ignoreCase, false);
   }

   @JvmStatic
   public fun CharSequence.findLastAnyOf(strings: Collection<String>, startIndex: Int, ignoreCase: Boolean): Pair<Int, String>? {
      return findAnyOf$StringsKt__StringsKt(`$this$findLastAnyOf`, strings, startIndex, ignoreCase, true);
   }

   @JvmStatic
   public fun CharSequence.indexOfAny(strings: Collection<String>, startIndex: Int, ignoreCase: Boolean): Int {
      var var10000: Pair = findAnyOf$StringsKt__StringsKt(`$this$indexOfAny`, strings, startIndex, ignoreCase, false);
      return if (var10000 != null) (var10000.getFirst() as java.lang.Number).intValue() else -1;
   }

   @JvmStatic
   public fun CharSequence.lastIndexOfAny(strings: Collection<String>, startIndex: Int, ignoreCase: Boolean): Int {
      var var10000: Pair = findAnyOf$StringsKt__StringsKt(`$this$lastIndexOfAny`, strings, startIndex, ignoreCase, true);
      return if (var10000 != null) (var10000.getFirst() as java.lang.Number).intValue() else -1;
   }

   @JvmStatic
   public fun CharSequence.indexOf(char: Char, startIndex: Int, ignoreCase: Boolean): Int {
      return if (!ignoreCase && `$this$indexOf` is java.lang.String)
         (`$this$indexOf` as java.lang.String).indexOf(var1, startIndex)
         else
         StringsKt.indexOfAny(`$this$indexOf`, new char[]{var1}, startIndex, ignoreCase);
   }

   @JvmStatic
   public fun CharSequence.indexOf(string: String, startIndex: Int, ignoreCase: Boolean): Int {
      return if (!ignoreCase && `$this$indexOf` is java.lang.String)
         (`$this$indexOf` as java.lang.String).indexOf(string, startIndex)
         else
         indexOf$StringsKt__StringsKt$default(`$this$indexOf`, string, startIndex, `$this$indexOf`.length(), ignoreCase, false, 16, null);
   }

   @JvmStatic
   public fun CharSequence.lastIndexOf(char: Char, startIndex: Int, ignoreCase: Boolean): Int {
      return if (!ignoreCase && `$this$lastIndexOf` is java.lang.String)
         (`$this$lastIndexOf` as java.lang.String).lastIndexOf(var1, startIndex)
         else
         StringsKt.lastIndexOfAny(`$this$lastIndexOf`, new char[]{var1}, startIndex, ignoreCase);
   }

   @JvmStatic
   public fun CharSequence.lastIndexOf(string: String, startIndex: Int, ignoreCase: Boolean): Int {
      return if (!ignoreCase && `$this$lastIndexOf` is java.lang.String)
         (`$this$lastIndexOf` as java.lang.String).lastIndexOf(string, startIndex)
         else
         indexOf$StringsKt__StringsKt(`$this$lastIndexOf`, string, startIndex, 0, ignoreCase, true);
   }

   @JvmStatic
   public operator fun CharSequence.contains(other: CharSequence, ignoreCase: Boolean): Boolean {
      return if (other is java.lang.String)
         StringsKt.indexOf$default(`$this$contains`, other as java.lang.String, 0, ignoreCase, 2, null) >= 0
         else
         indexOf$StringsKt__StringsKt$default(`$this$contains`, other, 0, `$this$contains`.length(), ignoreCase, false, 16, null) >= 0;
   }

   @JvmStatic
   public operator fun CharSequence.contains(char: Char, ignoreCase: Boolean): Boolean {
      return StringsKt.indexOf$default(`$this$contains`, var1, 0, ignoreCase, 2, null) >= 0;
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun CharSequence.contains(regex: Regex): Boolean {
      return regex.containsMatchIn(`$this$contains`);
   }

   @JvmStatic
   private fun CharSequence.rangesDelimitedBy(delimiters: CharArray, startIndex: Int, ignoreCase: Boolean, limit: Int): Sequence<IntRange> {
      StringsKt.requireNonNegativeLimit(limit);
      return new DelimitedRangesSequence(
         `$this$rangesDelimitedBy`,
         startIndex,
         limit,
         (new Function2<java.lang.CharSequence, Integer, Pair<? extends Integer, ? extends Integer>>(delimiters, ignoreCase) {
            {
               super(2);
               this.$delimiters = `$delimiters`;
               this.$ignoreCase = `$ignoreCase`;
            }

            @Nullable
            public final Pair<Integer, Integer> invoke(@NotNull java.lang.CharSequence $this$$receiver, int currentIndex) {
               var var3: Int = StringsKt.indexOfAny(`$this$$receiver`, this.$delimiters, currentIndex, this.$ignoreCase);
               return if (var3 < 0) null else TuplesKt.to(var3, 1);
            }
         }) as Function2<? super java.lang.CharSequence, ? super Integer, Pair<Integer, Integer>>
      );
   }

   @JvmStatic
   private fun CharSequence.rangesDelimitedBy(delimiters: Array<out String>, startIndex: Int, ignoreCase: Boolean, limit: Int): Sequence<IntRange> {
      StringsKt.requireNonNegativeLimit(limit);
      var delimitersList: java.util.List = ArraysKt.asList(delimiters);
      return new DelimitedRangesSequence(
         `$this$rangesDelimitedBy`,
         startIndex,
         limit,
         (new Function2<java.lang.CharSequence, Integer, Pair<? extends Integer, ? extends Integer>>(delimitersList, ignoreCase) {
            {
               super(2);
               this.$delimitersList = `$delimitersList`;
               this.$ignoreCase = `$ignoreCase`;
            }

            @Nullable
            public final Pair<Integer, Integer> invoke(@NotNull java.lang.CharSequence $this$$receiver, int currentIndex) {
               var var10000: Pair = StringsKt__StringsKt.access$findAnyOf(`$this$$receiver`, this.$delimitersList, currentIndex, this.$ignoreCase, false);
               return if (var10000 != null) TuplesKt.to((Integer)var10000.getFirst(), (var10000.getSecond() as java.lang.String).length()) else null;
            }
         }) as Function2<? super java.lang.CharSequence, ? super Integer, Pair<Integer, Integer>>
      );
   }

   @JvmStatic
   internal fun requireNonNegativeLimit(limit: Int) {
      if (limit < 0) {
         throw new IllegalArgumentException(("Limit must be non-negative, but was " + limit).toString());
      }
   }

   @JvmStatic
   public fun CharSequence.splitToSequence(vararg delimiters: String, ignoreCase: Boolean, limit: Int): Sequence<String> {
      return SequencesKt.map(
         rangesDelimitedBy$StringsKt__StringsKt$default(`$this$splitToSequence`, delimiters, 0, ignoreCase, limit, 2, null),
         (new Function1<IntRange, java.lang.String>(`$this$splitToSequence`) {
            {
               super(1);
               this.$this_splitToSequence = `$receiver`;
            }

            @NotNull
            public final java.lang.String invoke(@NotNull IntRange it) {
               return StringsKt.substring(this.$this_splitToSequence, it);
            }
         }) as Function1
      );
   }

   @JvmStatic
   public fun CharSequence.split(vararg delimiters: String, ignoreCase: Boolean, limit: Int): List<String> {
      if (delimiters.length == 1) {
         var `$this$map$iv`: java.lang.String = delimiters[0];
         if (delimiters[0].length() != 0) {
            return split$StringsKt__StringsKt(`$this$split`, `$this$map$iv`, ignoreCase, limit);
         }
      }

      var var14: java.lang.Iterable = SequencesKt.asIterable(
         rangesDelimitedBy$StringsKt__StringsKt$default(`$this$split`, delimiters, 0, ignoreCase, limit, 2, null)
      );
      var `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(var14, 10));

      for (Object item$iv$iv : $this$map$iv) {
         `destination$iv$iv`.add(StringsKt.substring(`$this$split`, `item$iv$iv` as IntRange));
      }

      return `destination$iv$iv` as java.util.List<java.lang.String>;
   }

   @JvmStatic
   public fun CharSequence.splitToSequence(delimiters: CharArray, ignoreCase: Boolean, limit: Int): Sequence<String> {
      return SequencesKt.map(
         rangesDelimitedBy$StringsKt__StringsKt$default(`$this$splitToSequence`, delimiters, 0, ignoreCase, limit, 2, null),
         (new Function1<IntRange, java.lang.String>(`$this$splitToSequence`) {
            {
               super(1);
               this.$this_splitToSequence = `$receiver`;
            }

            @NotNull
            public final java.lang.String invoke(@NotNull IntRange it) {
               return StringsKt.substring(this.$this_splitToSequence, it);
            }
         }) as Function1
      );
   }

   @JvmStatic
   public fun CharSequence.split(delimiters: CharArray, ignoreCase: Boolean, limit: Int): List<String> {
      if (delimiters.length == 1) {
         return split$StringsKt__StringsKt(`$this$split`, java.lang.String.valueOf(delimiters[0]), ignoreCase, limit);
      } else {
         var `$this$map$iv`: java.lang.Iterable = SequencesKt.asIterable(
            rangesDelimitedBy$StringsKt__StringsKt$default(`$this$split`, delimiters, 0, ignoreCase, limit, 2, null)
         );
         var `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$map$iv`, 10));

         for (Object item$iv$iv : $this$map$iv) {
            `destination$iv$iv`.add(StringsKt.substring(`$this$split`, `item$iv$iv` as IntRange));
         }

         return `destination$iv$iv` as java.util.List<java.lang.String>;
      }
   }

   @JvmStatic
   private fun CharSequence.split(delimiter: String, ignoreCase: Boolean, limit: Int): List<String> {
      StringsKt.requireNonNegativeLimit(limit);
      var currentOffset: Int = 0;
      var nextIndex: Int = StringsKt.indexOf(`$this$split`, delimiter, 0, ignoreCase);
      if (nextIndex != -1 && limit != 1) {
         var isLimited: Boolean = limit > 0;
         var result: ArrayList = new ArrayList(if (limit > 0) RangesKt.coerceAtMost(limit, 10) else 10);

         do {
            result.add(`$this$split`.subSequence(currentOffset, nextIndex).toString());
            currentOffset = nextIndex + delimiter.length();
            if (isLimited && result.size() == limit - 1) {
               break;
            }

            nextIndex = StringsKt.indexOf(`$this$split`, delimiter, currentOffset, ignoreCase);
         } while (nextIndex != -1);

         result.add(`$this$split`.subSequence(currentOffset, `$this$split`.length()).toString());
         return result;
      } else {
         return CollectionsKt.listOf(`$this$split`.toString());
      }
   }

   @InlineOnly
   @JvmStatic
   public inline fun CharSequence.split(regex: Regex, limit: Int): List<String> {
      return regex.split(`$this$split`, limit);
   }

   @SinceKotlin(version = "1.6")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun CharSequence.splitToSequence(regex: Regex, limit: Int): Sequence<String> {
      return regex.splitToSequence(`$this$splitToSequence`, limit);
   }

   @JvmStatic
   public fun CharSequence.lineSequence(): Sequence<String> {
      return StringsKt.splitToSequence$default(`$this$lineSequence`, new java.lang.String[]{"\r\n", "\n", "\r"}, false, 0, 6, null);
   }

   @JvmStatic
   public fun CharSequence.lines(): List<String> {
      return SequencesKt.toList(StringsKt.lineSequence(`$this$lines`));
   }

   @JvmStatic
   internal fun CharSequence?.contentEqualsIgnoreCaseImpl(other: CharSequence?): Boolean {
      if (`$this$contentEqualsIgnoreCaseImpl` is java.lang.String && other is java.lang.String) {
         return StringsKt.equals(`$this$contentEqualsIgnoreCaseImpl` as java.lang.String, other as java.lang.String, true);
      } else if (`$this$contentEqualsIgnoreCaseImpl` === other) {
         return true;
      } else if (`$this$contentEqualsIgnoreCaseImpl` != null && other != null && `$this$contentEqualsIgnoreCaseImpl`.length() == other.length()) {
         var i: Int = 0;

         for (int var3 = $this$contentEqualsIgnoreCaseImpl.length(); i < var3; i++) {
            if (!CharsKt.equals(`$this$contentEqualsIgnoreCaseImpl`.charAt(i), other.charAt(i), true)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @JvmStatic
   internal fun CharSequence?.contentEqualsImpl(other: CharSequence?): Boolean {
      if (`$this$contentEqualsImpl` is java.lang.String && other is java.lang.String) {
         return `$this$contentEqualsImpl` == other;
      } else if (`$this$contentEqualsImpl` === other) {
         return true;
      } else if (`$this$contentEqualsImpl` != null && other != null && `$this$contentEqualsImpl`.length() == other.length()) {
         var i: Int = 0;

         for (int var3 = $this$contentEqualsImpl.length(); i < var3; i++) {
            if (`$this$contentEqualsImpl`.charAt(i) != other.charAt(i)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @SinceKotlin(version = "1.5")
   @JvmStatic
   public fun String.toBooleanStrict(): Boolean {
      var var10000: Boolean;
      if (`$this$toBooleanStrict` == "true") {
         var10000 = true;
      } else {
         if (!(`$this$toBooleanStrict` == "false")) {
            throw new IllegalArgumentException("The string doesn't represent a boolean value: " + `$this$toBooleanStrict`);
         }

         var10000 = false;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.5")
   @JvmStatic
   public fun String.toBooleanStrictOrNull(): Boolean? {
      return if (`$this$toBooleanStrictOrNull` == "true") true else (if (`$this$toBooleanStrictOrNull` == "false") false else null);
   }

   open fun StringsKt__StringsKt() {
   }
}
