package com.sun.javafx.css;

import java.util.Set;
import javafx.css.Declaration;
import javafx.css.Match;
import javafx.css.ParsedValue;
import javafx.css.PseudoClass;
import javafx.css.Rule;
import javafx.css.Selector;
import javafx.css.Style;
import javafx.css.StyleOrigin;

public class CascadingStyle implements Comparable<CascadingStyle> {
   private final Style style;
   private final Set<PseudoClass> pseudoClasses;
   private final int specificity;
   private final int ordinal;
   private final boolean skinProp;

   public Style getStyle() {
      return this.style;
   }

   public CascadingStyle(Style var1, Set<PseudoClass> var2, int var3, int var4) {
      this.style = var1;
      this.pseudoClasses = var2;
      this.specificity = var3;
      this.ordinal = var4;
      this.skinProp = "-fx-skin".equals(var1.getDeclaration().getProperty());
   }

   public CascadingStyle(Declaration var1, Match var2, int var3) {
      this(new Style(var2.getSelector(), var1), var2.getPseudoClasses(), var2.getSpecificity(), var3);
   }

   public String getProperty() {
      return this.style.getDeclaration().getProperty();
   }

   public Selector getSelector() {
      return this.style.getSelector();
   }

   public Rule getRule() {
      return this.style.getDeclaration().getRule();
   }

   public StyleOrigin getOrigin() {
      return this.getRule().getOrigin();
   }

   public ParsedValue getParsedValue() {
      return this.style.getDeclaration().getParsedValue();
   }

   @Override
   public String toString() {
      return this.getProperty();
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         CascadingStyle var2 = (CascadingStyle)var1;
         String var3 = this.getProperty();
         String var4 = var2.getProperty();
         if (var3 == null ? var4 == null : var3.equals(var4)) {
            return this.pseudoClasses == null ? var2.pseudoClasses == null : this.pseudoClasses.containsAll(var2.pseudoClasses);
         } else {
            return false;
         }
      }
   }

   @Override
   public int hashCode() {
      int var1 = 7;
      String var2 = this.getProperty();
      var1 = 47 * var1 + (var2 != null ? var2.hashCode() : 0);
      return 47 * var1 + (this.pseudoClasses != null ? this.pseudoClasses.hashCode() : 0);
   }

   public int compareTo(CascadingStyle var1) {
      Declaration var2 = this.style.getDeclaration();
      boolean var3 = var2 != null ? var2.isImportant() : false;
      Rule var4 = var2 != null ? var2.getRule() : null;
      StyleOrigin var5 = var4 != null ? var4.getOrigin() : null;
      Declaration var6 = var1.style.getDeclaration();
      boolean var7 = var6 != null ? var6.isImportant() : false;
      Rule var8 = var6 != null ? var6.getRule() : null;
      StyleOrigin var9 = var8 != null ? var8.getOrigin() : null;
      int var10 = 0;
      if (this.skinProp && !var1.skinProp) {
         var10 = 1;
      } else if (var3 != var7) {
         var10 = var3 ? -1 : 1;
      } else if (var5 != var9) {
         if (var5 == null) {
            var10 = -1;
         } else if (var9 == null) {
            var10 = 1;
         } else {
            var10 = var9.compareTo(var5);
         }
      } else {
         var10 = var1.specificity - this.specificity;
      }

      if (var10 == 0) {
         var10 = var1.ordinal - this.ordinal;
      }

      return var10;
   }
}
