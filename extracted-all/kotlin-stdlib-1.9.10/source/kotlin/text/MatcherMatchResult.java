package kotlin.text

import java.util.regex.Matcher
import kotlin.internal.PlatformImplementationsKt
import kotlin.jvm.functions.Function1
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

private class MatcherMatchResult(matcher: Matcher, input: CharSequence) : MatchResult {
   public open val groupValues: List<String>
      public open get() {
         if (this.groupValues_ == null) {
            this.groupValues_ = new AbstractList<java.lang.String>(this) {
               {
                  this.this$0 = `$receiver`;
               }

               @Override
               public int getSize() {
                  return MatcherMatchResult.access$getMatchResult(this.this$0).groupCount() + 1;
               }

               @NotNull
               public java.lang.String get(int index) {
                  var var10000: java.lang.String = MatcherMatchResult.access$getMatchResult(this.this$0).group(index);
                  if (var10000 == null) {
                     var10000 = "";
                  }

                  return var10000;
               }
            };
         }

         var var10000: java.util.List = this.groupValues_;
         return var10000;
      }

   private final var groupValues_: List<String>?
   public open val groups: MatchGroupCollection
   private final val input: CharSequence
   private final val matchResult: java.util.regex.MatchResult
      private final get() {
         return this.matcher;
      }

   private final val matcher: Matcher
   public open val range: IntRange
      public open get() {
         return RegexKt.access$range(this.getMatchResult());
      }

   public open val value: String
      public open get() {
         var var10000: java.lang.String = this.getMatchResult().group();
         return var10000;
      }


   init {
      this.matcher = matcher;
      this.input = input;
      this.groups = (
         new MatchNamedGroupCollection(this) {
            {
               this.this$0 = `$receiver`;
            }

            @Override
            public int getSize() {
               return MatcherMatchResult.access$getMatchResult(this.this$0).groupCount() + 1;
            }

            @Override
            public boolean isEmpty() {
               return false;
            }

            @NotNull
            @Override
            public java.util.Iterator<MatchGroup> iterator() {
               return SequencesKt.<Integer, MatchGroup>map(
                     CollectionsKt.asSequence(CollectionsKt.getIndices(this as java.util.Collection<?>)), (new Function1<Integer, MatchGroup>(this) {
                        {
                           super(1);
                           this.this$0 = `$receiver`;
                        }

                        @Nullable
                        public final MatchGroup invoke(int it) {
                           return this.this$0.get(it);
                        }
                     }) as Function1<? super Integer, ? extends MatchGroup>
                  )
                  .iterator();
            }

            @Nullable
            @Override
            public MatchGroup get(int index) {
               var range: IntRange = RegexKt.access$range(MatcherMatchResult.access$getMatchResult(this.this$0), index);
               var var10000: MatchGroup;
               if (range.getStart() >= 0) {
                  var var10002: java.lang.String = MatcherMatchResult.access$getMatchResult(this.this$0).group(index);
                  var10000 = new MatchGroup(var10002, range);
               } else {
                  var10000 = null;
               }

               return var10000;
            }

            @Nullable
            @Override
            public MatchGroup get(@NotNull java.lang.String name) {
               return PlatformImplementationsKt.IMPLEMENTATIONS.getMatchResultNamedGroup(MatcherMatchResult.access$getMatchResult(this.this$0), name);
            }
         }
      ) as MatchGroupCollection;
   }

   public override fun next(): MatchResult? {
      var nextIndex: Int = this.getMatchResult().end() + (if (this.getMatchResult().end() == this.getMatchResult().start()) 1 else 0);
      var var2: MatchResult;
      if (nextIndex <= this.input.length()) {
         var var10000: Matcher = this.matcher.pattern().matcher(this.input);
         var2 = RegexKt.access$findNext(var10000, nextIndex, this.input);
      } else {
         var2 = null;
      }

      return var2;
   }

   override fun getDestructured(): MatchResult.Destructured {
      return MatchResult.DefaultImpls.getDestructured(this);
   }
}
