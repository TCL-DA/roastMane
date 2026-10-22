package javafx.css;

import com.sun.javafx.css.Combinator;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public abstract class Selector {
   private Rule rule;
   private int ordinal = -1;
   private static final int TYPE_SIMPLE = 1;
   private static final int TYPE_COMPOUND = 2;

   Selector() {
   }

   static Selector getUniversalSelector() {
      return Selector.UniversalSelector.INSTANCE;
   }

   void setRule(Rule var1) {
      this.rule = var1;
   }

   public Rule getRule() {
      return this.rule;
   }

   public void setOrdinal(int var1) {
      this.ordinal = var1;
   }

   public int getOrdinal() {
      return this.ordinal;
   }

   public abstract Match createMatch();

   public abstract boolean applies(Styleable var1);

   public abstract boolean applies(Styleable var1, Set<PseudoClass>[] var2, int var3);

   public abstract boolean stateMatches(Styleable var1, Set<PseudoClass> var2);

   protected void writeBinary(DataOutputStream var1, StyleConverter.StringStore var2) throws IOException {
      if (this instanceof SimpleSelector) {
         var1.writeByte(1);
      } else {
         var1.writeByte(2);
      }
   }

   static Selector readBinary(int var0, DataInputStream var1, String[] var2) throws IOException {
      byte var3 = var1.readByte();
      return (Selector)(var3 == 1 ? SimpleSelector.readBinary(var0, var1, var2) : CompoundSelector.readBinary(var0, var1, var2));
   }

   public static Selector createSelector(String var0) {
      if (var0 != null && var0.length() != 0) {
         ArrayList var1 = new ArrayList();
         ArrayList var2 = new ArrayList();
         ArrayList var3 = new ArrayList();
         int var4 = 0;
         int var5 = -1;
         char var6 = 0;

         for (int var7 = 0; var7 < var0.length(); var7++) {
            char var8 = var0.charAt(var7);
            if (var8 == ' ') {
               if (var6 == 0) {
                  var6 = var8;
                  var5 = var7;
               }
            } else if (var8 == '>') {
               if (var6 == 0) {
                  var5 = var7;
               }

               var6 = var8;
            } else if (var6 != 0) {
               var3.add(var0.substring(var4, var5));
               var4 = var7;
               var2.add(var6 == ' ' ? Combinator.DESCENDANT : Combinator.CHILD);
               var6 = 0;
            }
         }

         var3.add(var0.substring(var4));

         for (int var16 = 0; var16 < var3.size(); var16++) {
            String var17 = (String)var3.get(var16);
            if (var17 != null && !var17.equals("")) {
               String[] var9 = var17.split(":");
               ArrayList var10 = new ArrayList();

               for (int var11 = 1; var11 < var9.length; var11++) {
                  if (var9[var11] != null && !var9[var11].equals("")) {
                     var10.add(var9[var11].trim());
                  }
               }

               String var18 = var9[0].trim();
               String[] var12 = var18.split("\\.");
               ArrayList var13 = new ArrayList();

               for (int var14 = 1; var14 < var12.length; var14++) {
                  if (var12[var14] != null && !var12[var14].equals("")) {
                     var13.add(var12[var14].trim());
                  }
               }

               String var19 = null;
               String var15 = null;
               if (!var12[0].equals("")) {
                  if (var12[0].charAt(0) == '#') {
                     var15 = var12[0].substring(1).trim();
                  } else {
                     var19 = var12[0].trim();
                  }
               }

               var1.add(new SimpleSelector(var19, var13, var10, var15));
            }
         }

         return (Selector)(var1.size() == 1 ? (Selector)var1.get(0) : new CompoundSelector(var1, var2));
      } else {
         return null;
      }
   }

   private static class UniversalSelector {
      private static final Selector INSTANCE = new SimpleSelector("*", null, null, null);
   }
}
