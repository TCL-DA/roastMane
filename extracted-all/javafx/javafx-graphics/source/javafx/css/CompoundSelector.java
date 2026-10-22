package javafx.css;

import com.sun.javafx.css.Combinator;
import com.sun.javafx.css.PseudoClassState;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javafx.collections.ObservableSet;

public final class CompoundSelector extends Selector {
   private final List<SimpleSelector> selectors;
   private final List<Combinator> relationships;
   private int hash = -1;

   public List<SimpleSelector> getSelectors() {
      return this.selectors;
   }

   CompoundSelector(List<SimpleSelector> var1, List<Combinator> var2) {
      this.selectors = var1 != null ? Collections.unmodifiableList(var1) : Collections.EMPTY_LIST;
      this.relationships = var2 != null ? Collections.unmodifiableList(var2) : Collections.EMPTY_LIST;
   }

   private CompoundSelector() {
      this(null, null);
   }

   @Override
   public Match createMatch() {
      PseudoClassState var1 = new PseudoClassState();
      int var2 = 0;
      int var3 = 0;
      int var4 = 0;

      for (int var5 = this.selectors.size(); var4 < var5; var4++) {
         Selector var6 = this.selectors.get(var4);
         Match var7 = var6.createMatch();
         var1.addAll(var7.pseudoClasses);
         var2 += var7.idCount;
         var3 += var7.styleClassCount;
      }

      return new Match(this, var1, var2, var3);
   }

   @Override
   public boolean applies(Styleable var1) {
      return this.applies(var1, this.selectors.size() - 1, null, 0);
   }

   @Override
   public boolean applies(Styleable var1, Set<PseudoClass>[] var2, int var3) {
      assert var2 == null || var3 < var2.length;

      if (var2 != null && var2.length <= var3) {
         return false;
      } else {
         PseudoClassState[] var4 = var2 != null ? new PseudoClassState[var2.length] : null;
         boolean var5 = this.applies(var1, this.selectors.size() - 1, var4, var3);
         if (var5 && var4 != null) {
            for (int var6 = 0; var6 < var2.length; var6++) {
               Set var7 = var2[var6];
               PseudoClassState var8 = var4[var6];
               if (var7 != null) {
                  var7.addAll(var8);
               } else {
                  var2[var6] = var8;
               }
            }
         }

         return var5;
      }
   }

   private boolean applies(Styleable var1, int var2, Set<PseudoClass>[] var3, int var4) {
      if (var2 < 0) {
         return false;
      } else if (!this.selectors.get(var2).applies(var1, var3, var4)) {
         return false;
      } else if (var2 == 0) {
         return true;
      } else {
         Combinator var5 = this.relationships.get(var2 - 1);
         if (var5 == Combinator.CHILD) {
            Styleable var9 = var1.getStyleableParent();
            return var9 == null ? false : this.applies(var9, var2 - 1, var3, ++var4);
         } else {
            for (Styleable var6 = var1.getStyleableParent(); var6 != null; var6 = var6.getStyleableParent()) {
               boolean var7 = this.applies(var6, var2 - 1, var3, ++var4);
               if (var7) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   @Override
   public boolean stateMatches(Styleable var1, Set<PseudoClass> var2) {
      return this.stateMatches(var1, var2, this.selectors.size() - 1);
   }

   private boolean stateMatches(Styleable var1, Set<PseudoClass> var2, int var3) {
      if (var3 < 0) {
         return false;
      } else if (!this.selectors.get(var3).stateMatches(var1, var2)) {
         return false;
      } else if (var3 == 0) {
         return true;
      } else {
         Combinator var4 = this.relationships.get(var3 - 1);
         if (var4 == Combinator.CHILD) {
            Styleable var5 = var1.getStyleableParent();
            if (var5 == null) {
               return false;
            }

            if (this.selectors.get(var3 - 1).applies(var5)) {
               ObservableSet var6 = var5.getPseudoClassStates();
               return this.stateMatches(var5, var6, var3 - 1);
            }
         } else {
            for (Styleable var7 = var1.getStyleableParent(); var7 != null; var7 = var7.getStyleableParent()) {
               if (this.selectors.get(var3 - 1).applies(var7)) {
                  ObservableSet var8 = var7.getPseudoClassStates();
                  return this.stateMatches(var7, var8, var3 - 1);
               }
            }
         }

         return false;
      }
   }

   @Override
   public int hashCode() {
      if (this.hash == -1) {
         int var1 = 0;

         for (int var2 = this.selectors.size(); var1 < var2; var1++) {
            this.hash = 31 * (this.hash + this.selectors.get(var1).hashCode());
         }

         var1 = 0;

         for (int var4 = this.relationships.size(); var1 < var4; var1++) {
            this.hash = 31 * (this.hash + this.relationships.get(var1).hashCode());
         }
      }

      return this.hash;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         CompoundSelector var2 = (CompoundSelector)var1;
         if (var2.selectors.size() != this.selectors.size()) {
            return false;
         } else {
            int var3 = 0;

            for (int var4 = this.selectors.size(); var3 < var4; var3++) {
               if (!var2.selectors.get(var3).equals(this.selectors.get(var3))) {
                  return false;
               }
            }

            if (var2.relationships.size() != this.relationships.size()) {
               return false;
            } else {
               var3 = 0;

               for (int var6 = this.relationships.size(); var3 < var6; var3++) {
                  if (!var2.relationships.get(var3).equals(this.relationships.get(var3))) {
                     return false;
                  }
               }

               return true;
            }
         }
      }
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append(this.selectors.get(0));

      for (int var2 = 1; var2 < this.selectors.size(); var2++) {
         var1.append(this.relationships.get(var2 - 1));
         var1.append(this.selectors.get(var2));
      }

      return var1.toString();
   }

   @Override
   protected final void writeBinary(DataOutputStream var1, StyleConverter.StringStore var2) throws IOException {
      super.writeBinary(var1, var2);
      var1.writeShort(this.selectors.size());

      for (int var3 = 0; var3 < this.selectors.size(); var3++) {
         this.selectors.get(var3).writeBinary(var1, var2);
      }

      var1.writeShort(this.relationships.size());

      for (int var4 = 0; var4 < this.relationships.size(); var4++) {
         var1.writeByte(this.relationships.get(var4).ordinal());
      }
   }

   static CompoundSelector readBinary(int var0, DataInputStream var1, String[] var2) throws IOException {
      short var3 = var1.readShort();
      ArrayList var4 = new ArrayList();

      for (int var5 = 0; var5 < var3; var5++) {
         var4.add((SimpleSelector)Selector.readBinary(var0, var1, var2));
      }

      short var9 = var1.readShort();
      ArrayList var6 = new ArrayList();

      for (int var7 = 0; var7 < var9; var7++) {
         byte var8 = var1.readByte();
         if (var8 == Combinator.CHILD.ordinal()) {
            var6.add(Combinator.CHILD);
         } else if (var8 == Combinator.DESCENDANT.ordinal()) {
            var6.add(Combinator.DESCENDANT);
         } else {
            assert false : "error deserializing CompoundSelector: Combinator = " + var8;

            var6.add(Combinator.DESCENDANT);
         }
      }

      return new CompoundSelector(var4, var6);
   }
}
