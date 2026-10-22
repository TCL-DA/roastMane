package kotlin.text

import java.io.Serializable
import java.util.ArrayList
import java.util.Collections
import java.util.EnumSet
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function0
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

@SourceDebugExtension(["SMAP\nRegex.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Regex.kt\nkotlin/text/Regex\n+ 2 Regex.kt\nkotlin/text/RegexKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,396:1\n22#2,3:397\n1#3:400\n*S KotlinDebug\n*F\n+ 1 Regex.kt\nkotlin/text/Regex\n*L\n102#1:397,3\n*E\n"])
class Regex @PublishedApi  internal constructor(nativePattern: Pattern) : Serializable {
   @JvmStatic
   public Regex.Companion Companion = new Regex.Companion(null);

   private final var _options: Set<RegexOption>?
   private final val nativePattern: Pattern
   public final val options: Set<RegexOption>
      public final get() {
         var var10000: java.util.Set = this._options;
         if (this._options == null) {
            var `value$iv`: Int = this.nativePattern.flags();
            var var3: EnumSet = EnumSet.allOf(RegexOption.class);
            CollectionsKt.retainAll(var3, new Regex$special$$inlined$fromInt$1(`value$iv`));
            var10000 = Collections.unmodifiableSet(var3);
            this._options = var10000;
            var10000 = var10000;
         }

         return var10000;
      }

   public final val pattern: String
      public final get() {
         var var10000: java.lang.String = this.nativePattern.pattern();
         return var10000;
      }


   init {
      this.nativePattern = nativePattern;
   }

   public constructor(pattern: String)  {
      var var10001: Pattern = Pattern.compile(pattern);
      this(var10001);
   }

   public constructor(pattern: String, option: RegexOption)  {
      var var10001: Pattern = Pattern.compile(pattern, Regex.Companion.access$ensureUnicodeCase(Companion, option.getValue()));
      this(var10001);
   }

   public constructor(pattern: String, options: Set<RegexOption>)  {
      var var10001: Pattern = Pattern.compile(pattern, Regex.Companion.access$ensureUnicodeCase(Companion, RegexKt.access$toInt(options)));
      this(var10001);
   }

   public infix fun matches(input: CharSequence): Boolean {
      return this.nativePattern.matcher(input).matches();
   }

   public fun containsMatchIn(input: CharSequence): Boolean {
      return this.nativePattern.matcher(input).find();
   }

   public fun find(input: CharSequence, startIndex: Int): MatchResult? {
      var var10000: Matcher = this.nativePattern.matcher(input);
      return RegexKt.access$findNext(var10000, startIndex, input);
   }

   public fun findAll(input: CharSequence, startIndex: Int): Sequence<MatchResult> {
      if (startIndex >= 0 && startIndex <= input.length()) {
         return SequencesKt.generateSequence((new Function0<MatchResult>(this, input, startIndex) {
            {
               super(0);
               this.this$0 = `$receiver`;
               this.$input = `$input`;
               this.$startIndex = `$startIndex`;
            }

            @Nullable
            public final MatchResult invoke() {
               return this.this$0.find(this.$input, this.$startIndex);
            }
         }) as Function0<? extends MatchResult>, <unrepresentable>.INSTANCE);
      } else {
         throw new IndexOutOfBoundsException("Start index out of bounds: " + startIndex + ", input length: " + input.length());
      }
   }

   public fun matchEntire(input: CharSequence): MatchResult? {
      var var10000: Matcher = this.nativePattern.matcher(input);
      return RegexKt.access$matchEntire(var10000, input);
   }

   @SinceKotlin(version = "1.7")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   public fun matchAt(input: CharSequence, index: Int): MatchResult? {
      var var3: Matcher = this.nativePattern.matcher(input).useAnchoringBounds(false).useTransparentBounds(true).region(index, input.length());
      var var10000: MatcherMatchResult;
      if (var3.lookingAt()) {
         var10000 = new MatcherMatchResult(var3, input);
      } else {
         var10000 = null;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.7")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   public fun matchesAt(input: CharSequence, index: Int): Boolean {
      return this.nativePattern.matcher(input).useAnchoringBounds(false).useTransparentBounds(true).region(index, input.length()).lookingAt();
   }

   public fun replace(input: CharSequence, replacement: String): String {
      var var10000: java.lang.String = this.nativePattern.matcher(input).replaceAll(replacement);
      return var10000;
   }

   public fun replace(input: CharSequence, transform: (MatchResult) -> CharSequence): String {
      var var10000: MatchResult = find$default(this, input, 0, 2, null);
      if (var10000 == null) {
         return input.toString();
      } else {
         var match: MatchResult = var10000;
         var lastStart: Int = 0;
         var length: Int = input.length();
         var sb: StringBuilder = new StringBuilder(length);

         do {
            sb.append(input, lastStart, match.getRange().getStart());
            sb.append(transform.invoke(match) as java.lang.CharSequence);
            lastStart = match.getRange().getEndInclusive() + 1;
            match = match.next();
         } while (lastStart < length && match != null);

         if (lastStart < length) {
            sb.append(input, lastStart, length);
         }

         var var8: java.lang.String = sb.toString();
         return var8;
      }
   }

   public fun replaceFirst(input: CharSequence, replacement: String): String {
      var var10000: java.lang.String = this.nativePattern.matcher(input).replaceFirst(replacement);
      return var10000;
   }

   public fun split(input: CharSequence, limit: Int): List<String> {
      StringsKt.requireNonNegativeLimit(limit);
      var matcher: Matcher = this.nativePattern.matcher(input);
      if (limit != 1 && matcher.find()) {
         var result: ArrayList = new ArrayList(if (limit > 0) RangesKt.coerceAtMost(limit, 10) else 10);
         var lastStart: Int = 0;
         var lastSplit: Int = limit - 1;

         do {
            result.add(input.subSequence(lastStart, matcher.start()).toString());
            lastStart = matcher.end();
         } while ((lastSplit < 0 || result.size() != lastSplit) && matcher.find());

         result.add(input.subSequence(lastStart, input.length()).toString());
         return result;
      } else {
         return CollectionsKt.listOf(input.toString());
      }
   }

   @SinceKotlin(version = "1.6")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   public fun splitToSequence(input: CharSequence, limit: Int): Sequence<String> {
      StringsKt.requireNonNegativeLimit(limit);
      return SequencesKt.sequence((new Function2<SequenceScope<? super java.lang.String>, Continuation<? super Unit>, Object>(this, input, limit, null) {
         Object L$1;
         int I$0;
         int label;

         {
            super(2, `$completion`);
            this.this$0 = `$receiver`;
            this.$input = `$input`;
            this.$limit = `$limit`;
         }

         // $VF: Irreducible bytecode was duplicated to produce valid code
         @Nullable
         @Override
         public final Object invokeSuspend(@NotNull Object $result) {
            var var6: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            var `$this$sequence`: SequenceScope;
            var matcher: Matcher;
            var splitCount: Int;
            switch (this.label) {
               case 0:
                  ResultKt.throwOnFailure(`$result`);
                  `$this$sequence` = this.L$0 as SequenceScope;
                  matcher = Regex.access$getNativePattern$p(this.this$0).matcher(this.$input);
                  if (this.$limit == 1 || !matcher.find()) {
                     var var10: java.lang.String = this.$input.toString();
                     var var13: Continuation = this;
                     this.label = 1;
                     if (`$this$sequence`.yield(var10, var13) === var6) {
                        return var6;
                     }

                     return Unit.INSTANCE;
                  }

                  splitCount = 0;
                  var var10001: java.lang.String = this.$input.subSequence(0, matcher.start()).toString();
                  var var10002: Continuation = this;
                  this.L$0 = `$this$sequence`;
                  this.L$1 = matcher;
                  this.I$0 = 0;
                  this.label = 2;
                  if (`$this$sequence`.yield(var10001, var10002) === var6) {
                     return var6;
                  }
                  break;
               case 1:
                  ResultKt.throwOnFailure(`$result`);
                  return Unit.INSTANCE;
               case 2:
                  splitCount = this.I$0;
                  matcher = this.L$1 as Matcher;
                  `$this$sequence` = this.L$0 as SequenceScope;
                  ResultKt.throwOnFailure(`$result`);
                  break;
               case 3:
                  ResultKt.throwOnFailure(`$result`);
                  return Unit.INSTANCE;
               default:
                  throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }

            var var8: java.lang.String;
            var var11: Continuation;
            do {
               var var7: Int = matcher.end();
               if (++splitCount == this.$limit - 1 || !matcher.find()) {
                  var8 = this.$input.subSequence(var7, this.$input.length()).toString();
                  var11 = this;
                  this.L$0 = null;
                  this.L$1 = null;
                  this.label = 3;
                  if (`$this$sequence`.yield(var8, var11) === var6) {
                     return var6;
                  }

                  return Unit.INSTANCE;
               }

               var8 = this.$input.subSequence(var7, matcher.start()).toString();
               var11 = this;
               this.L$0 = `$this$sequence`;
               this.L$1 = matcher;
               this.I$0 = splitCount;
               this.label = 2;
            } while ($this$sequence.yield(var8, var11) != var6);

            return var6;
         }

         @NotNull
         @Override
         public final Continuation<Unit> create(@Nullable Object value, @NotNull Continuation<?> $completion) {
            var var3: Function2 = new <anonymous constructor>(this.this$0, this.$input, this.$limit, `$completion`);
            var3.L$0 = value;
            return var3 as Continuation<Unit>;
         }

         @Nullable
         public final Object invoke(@NotNull SequenceScope<? super java.lang.String> p1, @Nullable Continuation<? super Unit> p2) {
            return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
         }
      }) as Function2<? super SequenceScope<? super java.lang.String>, ? super Continuation<? super Unit>, ? extends Object>);
   }

   public override fun toString(): String {
      var var10000: java.lang.String = this.nativePattern.toString();
      return var10000;
   }

   public fun toPattern(): Pattern {
      return this.nativePattern;
   }

   private fun writeReplace(): Any {
      var var10002: java.lang.String = this.nativePattern.pattern();
      return new Regex.Serialized(var10002, this.nativePattern.flags());
   }

   companion object Companion private constructor() {
      public fun fromLiteral(literal: String): Regex {
         return new Regex(literal, RegexOption.LITERAL);
      }

      public fun escape(literal: String): String {
         var var10000: java.lang.String = Pattern.quote(literal);
         return var10000;
      }

      public fun escapeReplacement(literal: String): String {
         var var10000: java.lang.String = Matcher.quoteReplacement(literal);
         return var10000;
      }

      private fun ensureUnicodeCase(flags: Int): Int {
         return if ((flags and 2) != 0) flags or 64 else flags;
      }
   }

   private class Serialized(pattern: String, flags: Int) : Serializable {
      @JvmStatic
      public Regex.Serialized.Companion Companion = new Regex.Serialized.Companion(null);
      @JvmStatic
      private long serialVersionUID = 0L;

      public final val flags: Int
      public final val pattern: String

      init {
         this.pattern = pattern;
         this.flags = flags;
      }

      private fun readResolve(): Any {
         var var10002: Pattern = Pattern.compile(this.pattern, this.flags);
         return new Regex(var10002);
      }

      companion object Companion private constructor() {
         private const val serialVersionUID: Long

      }
   }
}
