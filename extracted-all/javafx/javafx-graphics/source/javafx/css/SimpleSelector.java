package javafx.css;

import com.sun.javafx.css.PseudoClassState;
import com.sun.javafx.css.StyleClassSet;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javafx.collections.ObservableList;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;

public final class SimpleSelector extends Selector {
   private final String name;
   private final StyleClassSet styleClassSet;
   private final String id;
   private final PseudoClassState pseudoClassState;
   private final boolean matchOnName;
   private final boolean matchOnId;
   private final boolean matchOnStyleClass;
   private final NodeOrientation nodeOrientation;

   public String getName() {
      return this.name;
   }

   public List<String> getStyleClasses() {
      ArrayList var1 = new ArrayList();
      Iterator var2 = this.styleClassSet.iterator();

      while (var2.hasNext()) {
         var1.add(((StyleClass)var2.next()).getStyleClassName());
      }

      return Collections.unmodifiableList(var1);
   }

   public Set<StyleClass> getStyleClassSet() {
      return this.styleClassSet;
   }

   public String getId() {
      return this.id;
   }

   Set<PseudoClass> getPseudoClassStates() {
      return this.pseudoClassState;
   }

   List<String> getPseudoclasses() {
      ArrayList var1 = new ArrayList();
      Iterator var2 = this.pseudoClassState.iterator();

      while (var2.hasNext()) {
         var1.add(((PseudoClass)var2.next()).getPseudoClassName());
      }

      if (this.nodeOrientation == NodeOrientation.RIGHT_TO_LEFT) {
         var1.add("dir(rtl)");
      } else if (this.nodeOrientation == NodeOrientation.LEFT_TO_RIGHT) {
         var1.add("dir(ltr)");
      }

      return Collections.unmodifiableList(var1);
   }

   public NodeOrientation getNodeOrientation() {
      return this.nodeOrientation;
   }

   SimpleSelector(String var1, List<String> var2, List<String> var3, String var4) {
      this.name = var1 == null ? "*" : var1;
      this.matchOnName = var1 != null && !"".equals(var1) && !"*".equals(var1);
      this.styleClassSet = new StyleClassSet();
      int var5 = var2 != null ? var2.size() : 0;

      for (int var6 = 0; var6 < var5; var6++) {
         String var7 = (String)var2.get(var6);
         if (var7 != null && !var7.isEmpty()) {
            StyleClass var8 = StyleClassSet.getStyleClass(var7);
            this.styleClassSet.add(var8);
         }
      }

      this.matchOnStyleClass = this.styleClassSet.size() > 0;
      this.pseudoClassState = new PseudoClassState();
      var5 = var3 != null ? var3.size() : 0;
      NodeOrientation var11 = NodeOrientation.INHERIT;

      for (int var12 = 0; var12 < var5; var12++) {
         String var13 = (String)var3.get(var12);
         if (var13 != null && !var13.isEmpty()) {
            if ("dir(".regionMatches(true, 0, var13, 0, 4)) {
               boolean var9 = "dir(rtl)".equalsIgnoreCase(var13);
               var11 = var9 ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT;
            } else {
               PseudoClass var14 = PseudoClassState.getPseudoClass(var13);
               this.pseudoClassState.add(var14);
            }
         }
      }

      this.nodeOrientation = var11;
      this.id = var4 == null ? "" : var4;
      this.matchOnId = var4 != null && !"".equals(var4);
   }

   @Override
   public Match createMatch() {
      int var1 = this.matchOnId ? 1 : 0;
      int var2 = this.styleClassSet.size();
      return new Match(this, this.pseudoClassState, var1, var2);
   }

   @Override
   public boolean applies(Styleable var1) {
      if (this.nodeOrientation != NodeOrientation.INHERIT && var1 instanceof Node) {
         Node var2 = (Node)var1;
         NodeOrientation var3 = var2.getNodeOrientation();
         if (var3 == NodeOrientation.INHERIT ? var2.getEffectiveNodeOrientation() != this.nodeOrientation : var3 != this.nodeOrientation) {
            return false;
         }
      }

      if (this.matchOnId) {
         String var8 = var1.getId();
         boolean var11 = this.id.equals(var8);
         if (!var11) {
            return false;
         }
      }

      if (this.matchOnName) {
         String var9 = var1.getTypeSelector();
         boolean var12 = this.name.equals(var9);
         if (!var12) {
            return false;
         }
      }

      if (this.matchOnStyleClass) {
         StyleClassSet var10 = new StyleClassSet();
         ObservableList var13 = var1.getStyleClass();
         int var4 = 0;

         for (int var5 = var13.size(); var4 < var5; var4++) {
            String var6 = (String)var13.get(var4);
            if (var6 != null && !var6.isEmpty()) {
               StyleClass var7 = StyleClassSet.getStyleClass(var6);
               var10.add(var7);
            }
         }

         boolean var14 = this.matchStyleClasses(var10);
         if (!var14) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean applies(Styleable var1, Set<PseudoClass>[] var2, int var3) {
      boolean var4 = this.applies(var1);
      if (var4 && var2 != null && var3 < var2.length) {
         if (var2[var3] == null) {
            var2[var3] = new PseudoClassState();
         }

         var2[var3].addAll(this.pseudoClassState);
      }

      return var4;
   }

   @Override
   public boolean stateMatches(Styleable var1, Set<PseudoClass> var2) {
      return var2 != null ? var2.containsAll(this.pseudoClassState) : false;
   }

   private boolean matchStyleClasses(StyleClassSet var1) {
      return var1.containsAll(this.styleClassSet);
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         SimpleSelector var2 = (SimpleSelector)var1;
         if (this.name == null ? var2.name == null : this.name.equals(var2.name)) {
            if (this.id == null ? var2.id == null : this.id.equals(var2.id)) {
               return !this.styleClassSet.equals(var2.styleClassSet) ? false : this.pseudoClassState.equals(var2.pseudoClassState);
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   @Override
   public int hashCode() {
      int var1 = 7;
      var1 = 31 * (var1 + this.name.hashCode());
      var1 = 31 * (var1 + this.styleClassSet.hashCode());
      var1 = 31 * (var1 + this.styleClassSet.hashCode());
      var1 = this.id != null ? 31 * (var1 + this.id.hashCode()) : 0;
      return 31 * (var1 + this.pseudoClassState.hashCode());
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      if (this.name != null && !this.name.isEmpty()) {
         var1.append(this.name);
      } else {
         var1.append("*");
      }

      for (StyleClass var3 : this.styleClassSet) {
         var1.append('.').append(var3.getStyleClassName());
      }

      if (this.id != null && !this.id.isEmpty()) {
         var1.append('#');
         var1.append(this.id);
      }

      for (PseudoClass var4 : this.pseudoClassState) {
         var1.append(':').append(var4.getPseudoClassName());
      }

      return var1.toString();
   }

   @Override
   protected final void writeBinary(DataOutputStream var1, StyleConverter.StringStore var2) throws IOException {
      super.writeBinary(var1, var2);
      var1.writeShort(var2.addString(this.name));
      var1.writeShort(this.styleClassSet.size());

      for (StyleClass var4 : this.styleClassSet) {
         var1.writeShort(var2.addString(var4.getStyleClassName()));
      }

      var1.writeShort(var2.addString(this.id));
      int var7 = this.pseudoClassState.size()
         + (this.nodeOrientation != NodeOrientation.RIGHT_TO_LEFT && this.nodeOrientation != NodeOrientation.LEFT_TO_RIGHT ? 0 : 1);
      var1.writeShort(var7);

      for (PseudoClass var6 : this.pseudoClassState) {
         var1.writeShort(var2.addString(var6.getPseudoClassName()));
      }

      if (this.nodeOrientation == NodeOrientation.RIGHT_TO_LEFT) {
         var1.writeShort(var2.addString("dir(rtl)"));
      } else if (this.nodeOrientation == NodeOrientation.LEFT_TO_RIGHT) {
         var1.writeShort(var2.addString("dir(ltr)"));
      }
   }

   static SimpleSelector readBinary(int var0, DataInputStream var1, String[] var2) throws IOException {
      String var3 = var2[var1.readShort()];
      short var4 = var1.readShort();
      ArrayList var5 = new ArrayList();

      for (int var6 = 0; var6 < var4; var6++) {
         var5.add(var2[var1.readShort()]);
      }

      String var10 = var2[var1.readShort()];
      short var7 = var1.readShort();
      ArrayList var8 = new ArrayList();

      for (int var9 = 0; var9 < var7; var9++) {
         var8.add(var2[var1.readShort()]);
      }

      return new SimpleSelector(var3, var5, var8, var10);
   }
}
