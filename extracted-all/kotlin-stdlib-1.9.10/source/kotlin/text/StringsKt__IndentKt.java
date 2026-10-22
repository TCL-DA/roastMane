package kotlin.text

import java.util.ArrayList
import kotlin.internal.IntrinsicConstEvaluation
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.SourceDebugExtension
import org.jetbrains.annotations.NotNull

@SourceDebugExtension(["SMAP\nIndent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Indent.kt\nkotlin/text/StringsKt__IndentKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 _Strings.kt\nkotlin/text/StringsKt___StringsKt\n*L\n1#1,123:1\n113#1,2:125\n115#1,4:140\n120#1,2:153\n113#1,2:162\n115#1,4:177\n120#1,2:184\n1#2:124\n1#2:150\n1#2:181\n1#2:205\n1569#3,11:127\n1864#3,2:138\n1866#3:151\n1580#3:152\n766#3:155\n857#3,2:156\n1549#3:158\n1620#3,3:159\n1569#3,11:164\n1864#3,2:175\n1866#3:182\n1580#3:183\n1569#3,11:192\n1864#3,2:203\n1866#3:206\n1580#3:207\n151#4,6:144\n151#4,6:186\n*S KotlinDebug\n*F\n+ 1 Indent.kt\nkotlin/text/StringsKt__IndentKt\n*L\n38#1:125,2\n38#1:140,4\n38#1:153,2\n78#1:162,2\n78#1:177,4\n78#1:184,2\n38#1:150\n78#1:181\n114#1:205\n38#1:127,11\n38#1:138,2\n38#1:151\n38#1:152\n74#1:155\n74#1:156,2\n75#1:158\n75#1:159,3\n78#1:164,11\n78#1:175,2\n78#1:182\n78#1:183\n114#1:192,11\n114#1:203,2\n114#1:206\n114#1:207\n39#1:144,6\n101#1:186,6\n*E\n"])
// $VF: Class flags could not be determined
internal class StringsKt__IndentKt : StringsKt__AppendableKt {
   @IntrinsicConstEvaluation
   @JvmStatic
   public fun String.trimMargin(marginPrefix: String): String {
      return StringsKt.replaceIndentByMargin(`$this$trimMargin`, "", marginPrefix);
   }

   @JvmStatic
   public fun String.replaceIndentByMargin(newIndent: String, marginPrefix: String): String {
      if (StringsKt.isBlank(marginPrefix)) {
         throw new IllegalArgumentException("marginPrefix must be non-blank string.".toString());
      } else {
         var var40: java.util.List = StringsKt.lines(`$this$replaceIndentByMargin`);
         var `resultSizeEstimate$iv`: Int = `$this$replaceIndentByMargin`.length() + newIndent.length() * var40.size();
         var `indentAddFunction$iv`: Function1 = getIndentFunction$StringsKt__IndentKt(newIndent);
         var `lastIndex$iv`: Int = CollectionsKt.getLastIndex(var40);
         var `$this$mapIndexedNotNull$iv$iv`: java.lang.Iterable = var40;
         var `destination$iv$iv$iv`: java.util.Collection = new ArrayList();
         var `index$iv$iv$iv$iv`: Int = 0;

         for (Object item$iv$iv$iv$iv : $this$mapIndexedNotNull$iv$iv) {
            var var19: Int = `index$iv$iv$iv$iv`++;
            if (var19 < 0) {
               CollectionsKt.throwIndexOverflow();
            }

            var `value$iv`: java.lang.String = `item$iv$iv$iv$iv` as java.lang.String;
            var var45: java.lang.String;
            if ((var19 == 0 || var19 == `lastIndex$iv`) && StringsKt.isBlank(`item$iv$iv$iv$iv` as java.lang.String)) {
               var45 = null;
            } else {
               label84: {
                  var `$this$indexOfFirst$iv`: java.lang.CharSequence = `value$iv`;
                  var `index$iv`: Int = 0;
                  var var31: Int = `$this$indexOfFirst$iv`.length();

                  while (true) {
                     if (`index$iv` >= var31) {
                        var43 = -1;
                        break;
                     }

                     if (!CharsKt.isWhitespace(`$this$indexOfFirst$iv`.charAt(`index$iv`))) {
                        var43 = `index$iv`;
                        break;
                     }

                     `index$iv`++;
                  }

                  if (var43 == -1) {
                     var45 = null;
                  } else if (StringsKt.startsWith$default(`value$iv`, marginPrefix, var43, false, 4, null)) {
                     var var42: Int = var43 + marginPrefix.length();
                     var45 = `value$iv`.substring(var42);
                  } else {
                     var45 = null;
                  }

                  if (var45 != null) {
                     var45 = `indentAddFunction$iv`.invoke(var45) as java.lang.String;
                     if (var45 != null) {
                        break label84;
                     }
                  }

                  var45 = `value$iv`;
               }
            }

            if (var45 != null) {
               `destination$iv$iv$iv`.add(var45);
            }
         }

         var var39: java.lang.String = (CollectionsKt.joinTo$default(
               `destination$iv$iv$iv` as java.util.List, new StringBuilder(`resultSizeEstimate$iv`), "\n", null, null, 0, null, null, 124, null
            ) as StringBuilder)
            .toString();
         return var39;
      }
   }

   @IntrinsicConstEvaluation
   @JvmStatic
   public fun String.trimIndent(): String {
      return StringsKt.replaceIndent(`$this$trimIndent`, "");
   }

   @JvmStatic
   public fun String.replaceIndent(newIndent: String): String {
      var lines: java.util.List = StringsKt.lines(`$this$replaceIndent`);
      var `resultSizeEstimate$iv`: java.lang.Iterable = lines;
      var `lastIndex$iv`: java.util.Collection = new ArrayList();

      for (Object element$iv$iv : resultSizeEstimate$iv) {
         if (!StringsKt.isBlank(`$this$mapIndexedNotNullTo$iv$iv$iv` as java.lang.String)) {
            `lastIndex$iv`.add(`$this$mapIndexedNotNullTo$iv$iv$iv`);
         }
      }

      `resultSizeEstimate$iv` = `lastIndex$iv` as java.util.List;
      `lastIndex$iv` = new ArrayList(CollectionsKt.collectionSizeOrDefault(`lastIndex$iv` as java.util.List, 10));

      for (Object item$iv$iv : resultSizeEstimate$iv) {
         `lastIndex$iv`.add(indentWidth$StringsKt__IndentKt(var44 as java.lang.String));
      }

      var var10000: Int = CollectionsKt.minOrNull(`lastIndex$iv`);
      var minCommonIndent: Int = (int)(var10000 ?: 0);
      var var35: Int = `$this$replaceIndent`.length() + newIndent.length() * lines.size();
      var var37: Function1 = getIndentFunction$StringsKt__IndentKt(newIndent);
      var var39: Int = CollectionsKt.getLastIndex(lines);
      var var41: java.lang.Iterable = lines;
      var var46: java.util.Collection = new ArrayList();
      var `index$iv$iv$iv$iv`: Int = 0;

      for (Object item$iv$iv$iv$iv : $this$mapIndexedNotNull$iv$iv) {
         var var19: Int = `index$iv$iv$iv$iv`++;
         if (var19 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         var `value$iv`: java.lang.String = `item$iv$iv$iv$iv` as java.lang.String;
         var var51: java.lang.String;
         if ((var19 == 0 || var19 == var39) && StringsKt.isBlank(`item$iv$iv$iv$iv` as java.lang.String)) {
            var51 = null;
         } else {
            label78: {
               var var50: java.lang.String = StringsKt.drop(`value$iv`, minCommonIndent);
               if (var50 != null) {
                  var51 = var37.invoke(var50) as java.lang.String;
                  if (var51 != null) {
                     break label78;
                  }
               }

               var51 = `value$iv`;
            }
         }

         if (var51 != null) {
            var46.add(var51);
         }
      }

      var var32: java.lang.String = (CollectionsKt.joinTo$default(var46 as java.util.List, new StringBuilder(var35), "\n", null, null, 0, null, null, 124, null) as StringBuilder)
         .toString();
      return var32;
   }

   @JvmStatic
   public fun String.prependIndent(indent: String): String {
      return SequencesKt.joinToString$default(
         SequencesKt.map(StringsKt.lineSequence(`$this$prependIndent`), (new Function1<java.lang.String, java.lang.String>(indent) {
            {
               super(1);
               this.$indent = `$indent`;
            }

            @NotNull
            public final java.lang.String invoke(@NotNull java.lang.String it) {
               return if (StringsKt.isBlank(it)) (if (it.length() < this.$indent.length()) this.$indent else it) else this.$indent + it;
            }
         }) as Function1), "\n", null, null, 0, null, null, 62, null
      );
   }

   @JvmStatic
   private fun String.indentWidth(): Int {
      var `$this$indexOfFirst$iv`: java.lang.CharSequence = `$this$indentWidth`;
      var var3: Int = 0;
      var var4: Int = `$this$indexOfFirst$iv`.length();

      var var10000: Int;
      while (true) {
         if (var3 >= var4) {
            var10000 = -1;
            break;
         }

         if (!CharsKt.isWhitespace(`$this$indexOfFirst$iv`.charAt(var3))) {
            var10000 = var3;
            break;
         }

         var3++;
      }

      return if (var10000 == -1) `$this$indentWidth`.length() else var10000;
   }

   @JvmStatic
   private fun getIndentFunction(indent: String): (String) -> String {
      return (Function1<java.lang.String, java.lang.String>)(if (indent.length() == 0)
         <unrepresentable>.INSTANCE
         else
         (new Function1<java.lang.String, java.lang.String>(indent) {
            {
               super(1);
               this.$indent = `$indent`;
            }

            @NotNull
            public final java.lang.String invoke(@NotNull java.lang.String line) {
               return this.$indent + line;
            }
         }) as Function1);
   }

   @JvmStatic
   private inline fun List<String>.reindent(resultSizeEstimate: Int, indentAddFunction: (String) -> String, indentCutFunction: (String) -> String?): String {
      var lastIndex: Int = CollectionsKt.getLastIndex(`$this$reindent`);
      var `$this$mapIndexedNotNull$iv`: java.lang.Iterable = `$this$reindent`;
      var `destination$iv$iv`: java.util.Collection = new ArrayList();
      var `index$iv$iv$iv`: Int = 0;

      for (Object item$iv$iv$iv : $this$mapIndexedNotNull$iv) {
         var var17: Int = `index$iv$iv$iv`++;
         if (var17 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         var value: java.lang.String = `item$iv$iv$iv` as java.lang.String;
         var var29: java.lang.String;
         if ((var17 == 0 || var17 == lastIndex) && StringsKt.isBlank(`item$iv$iv$iv` as java.lang.String)) {
            var29 = null;
         } else {
            label44: {
               var29 = indentCutFunction.invoke(value) as java.lang.String;
               if (var29 != null) {
                  var29 = indentAddFunction.invoke(var29) as java.lang.String;
                  if (var29 != null) {
                     break label44;
                  }
               }

               var29 = value;
            }
         }

         if (var29 != null) {
            `destination$iv$iv`.add(var29);
         }
      }

      var var6: java.lang.String = (CollectionsKt.joinTo$default(
            `destination$iv$iv` as java.util.List, new StringBuilder(resultSizeEstimate), "\n", null, null, 0, null, null, 124, null
         ) as StringBuilder)
         .toString();
      return var6;
   }

   open fun StringsKt__IndentKt() {
   }
}
