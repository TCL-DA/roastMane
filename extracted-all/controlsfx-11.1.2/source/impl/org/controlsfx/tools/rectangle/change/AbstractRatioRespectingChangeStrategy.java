package impl.org.controlsfx.tools.rectangle.change;

abstract class AbstractRatioRespectingChangeStrategy extends AbstractBeginEndCheckingChangeStrategy {
   private final boolean ratioFixed;
   private final double ratio;

   protected AbstractRatioRespectingChangeStrategy(boolean ratioFixed, double ratio) {
      this.ratioFixed = ratioFixed;
      this.ratio = ratio;
   }

   protected final boolean isRatioFixed() {
      return this.ratioFixed;
   }

   protected final double getRatio() {
      if (!this.ratioFixed) {
         throw new IllegalStateException("The ratio is not fixed.");
      } else {
         return this.ratio;
      }
   }
}
