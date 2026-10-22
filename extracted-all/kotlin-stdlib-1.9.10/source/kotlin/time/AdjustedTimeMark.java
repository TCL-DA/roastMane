package kotlin.time

private class AdjustedTimeMark(mark: TimeMark, adjustment: Duration) : AdjustedTimeMark(mark, adjustment), TimeMark {
   private long adjustment;

   public final val adjustment: Duration
   public final val mark: TimeMark

   fun AdjustedTimeMark(mark: TimeMark, adjustment: Long) {
      this.mark = mark;
      this.adjustment = adjustment;
   }

   public override fun elapsedNow(): Duration {
      return Duration.minus-LRDsOJo(this.mark.elapsedNow-UwyO8pc(), this.adjustment);
   }

   public override operator fun plus(duration: Duration): TimeMark {
      return new AdjustedTimeMark(this.mark, Duration.plus-LRDsOJo(this.adjustment, duration), null);
   }

   override fun `minus-LRDsOJo`(duration: Long): TimeMark {
      return TimeMark.DefaultImpls.minus-LRDsOJo(this, duration);
   }

   override fun hasPassedNow(): Boolean {
      return TimeMark.DefaultImpls.hasPassedNow(this);
   }

   override fun hasNotPassedNow(): Boolean {
      return TimeMark.DefaultImpls.hasNotPassedNow(this);
   }
}
