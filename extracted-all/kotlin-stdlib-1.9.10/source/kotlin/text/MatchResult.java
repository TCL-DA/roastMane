package kotlin.text

import kotlin.internal.InlineOnly

interface MatchResult {
   public open val destructured: kotlin.text.MatchResult.Destructured
      public open get() {
      }

   public val groupValues: List<String>
   public val groups: MatchGroupCollection
   public val range: IntRange
   public val value: String

   public abstract fun next(): MatchResult? {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      fun getDestructured(`$this`: MatchResult): MatchResult.Destructured {
         return new MatchResult.Destructured(`$this`);
      }
   }

   class Destructured internal constructor(match: MatchResult) {
      public final val match: MatchResult

      init {
         this.match = match;
      }

      @InlineOnly
      public inline operator fun component1(): String {
         return this.getMatch().getGroupValues().get(1);
      }

      @InlineOnly
      public inline operator fun component2(): String {
         return this.getMatch().getGroupValues().get(2);
      }

      @InlineOnly
      public inline operator fun component3(): String {
         return this.getMatch().getGroupValues().get(3);
      }

      @InlineOnly
      public inline operator fun component4(): String {
         return this.getMatch().getGroupValues().get(4);
      }

      @InlineOnly
      public inline operator fun component5(): String {
         return this.getMatch().getGroupValues().get(5);
      }

      @InlineOnly
      public inline operator fun component6(): String {
         return this.getMatch().getGroupValues().get(6);
      }

      @InlineOnly
      public inline operator fun component7(): String {
         return this.getMatch().getGroupValues().get(7);
      }

      @InlineOnly
      public inline operator fun component8(): String {
         return this.getMatch().getGroupValues().get(8);
      }

      @InlineOnly
      public inline operator fun component9(): String {
         return this.getMatch().getGroupValues().get(9);
      }

      @InlineOnly
      public inline operator fun component10(): String {
         return this.getMatch().getGroupValues().get(10);
      }

      public fun toList(): List<String> {
         return this.match.getGroupValues().subList(1, this.match.getGroupValues().size());
      }
   }
}
