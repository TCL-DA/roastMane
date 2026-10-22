package javafx.css;

import com.sun.javafx.css.PseudoClassState;
import javafx.geometry.NodeOrientation;

public final class Match implements Comparable<Match> {
   final Selector selector;
   final PseudoClassState pseudoClasses;
   final int idCount;
   final int styleClassCount;
   final int specificity;

   Match(Selector var1, PseudoClassState var2, int var3, int var4) {
      assert var1 != null;

      this.selector = var1;
      this.idCount = var3;
      this.styleClassCount = var4;
      this.pseudoClasses = var2;
      int var5 = var2 != null ? var2.size() : 0;
      if (var1 instanceof SimpleSelector) {
         SimpleSelector var6 = (SimpleSelector)var1;
         if (var6.getNodeOrientation() != NodeOrientation.INHERIT) {
            var5++;
         }
      }

      this.specificity = var3 << 8 | var4 << 4 | var5;
   }

   public Selector getSelector() {
      return this.selector;
   }

   public PseudoClassState getPseudoClasses() {
      return this.pseudoClasses;
   }

   public int getSpecificity() {
      return this.specificity;
   }

   public int compareTo(Match var1) {
      return this.specificity - var1.specificity;
   }
}
