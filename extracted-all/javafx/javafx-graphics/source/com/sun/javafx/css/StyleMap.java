package com.sun.javafx.css;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.css.Declaration;
import javafx.css.Match;
import javafx.css.Rule;
import javafx.css.Selector;

public final class StyleMap {
   public static final StyleMap EMPTY_MAP = new StyleMap(-1, Collections.emptyList());
   private static final Comparator<CascadingStyle> cascadingStyleComparator = (var0, var1) -> {
      String var2 = var0.getProperty();
      String var3 = var1.getProperty();
      int var4 = var2.compareTo(var3);
      return var4 != 0 ? var4 : var0.compareTo(var1);
   };
   private final int id;
   private List<Selector> selectors;
   private Map<String, List<CascadingStyle>> cascadingStyles;

   public StyleMap(int var1, List<Selector> var2) {
      this.id = var1;
      this.selectors = var2;
   }

   public int getId() {
      return this.id;
   }

   public boolean isEmpty() {
      if (this.selectors != null) {
         return this.selectors.isEmpty();
      } else {
         return this.cascadingStyles != null ? this.cascadingStyles.isEmpty() : true;
      }
   }

   public Map<String, List<CascadingStyle>> getCascadingStyles() {
      if (this.cascadingStyles == null) {
         if (this.selectors == null || this.selectors.isEmpty()) {
            this.cascadingStyles = Collections.emptyMap();
            return this.cascadingStyles;
         }

         ArrayList var1 = new ArrayList();
         int var2 = 0;
         int var3 = 0;

         for (int var4 = this.selectors.size(); var3 < var4; var3++) {
            Selector var5 = this.selectors.get(var3);
            Match var6 = var5.createMatch();
            Rule var7 = var5.getRule();
            int var8 = 0;

            for (int var9 = var7.getDeclarations().size(); var8 < var9; var8++) {
               Declaration var10 = (Declaration)var7.getDeclarations().get(var8);
               CascadingStyle var11 = new CascadingStyle(var10, var6, var2++);
               var1.add(var11);
            }
         }

         if (var1.isEmpty()) {
            this.cascadingStyles = Collections.emptyMap();
            return this.cascadingStyles;
         }

         Collections.sort(var1, cascadingStyleComparator);
         var3 = var1.size();
         this.cascadingStyles = new HashMap<>(var3);
         CascadingStyle var13 = (CascadingStyle)var1.get(0);
         String var15 = var13.getProperty();
         int var16 = 0;

         while (var16 < var3) {
            List var17 = this.cascadingStyles.get(var15);
            if (var17 == null) {
               int var18 = var16;
               String var19 = var15;

               while (++var18 < var3) {
                  var13 = (CascadingStyle)var1.get(var18);
                  var15 = var13.getProperty();
                  if (!var15.equals(var19)) {
                     break;
                  }
               }

               this.cascadingStyles.put(var19, var1.subList(var16, var18));
               var16 = var18;
            } else {
               assert false;
            }
         }

         this.selectors.clear();
         this.selectors = null;
      }

      return this.cascadingStyles;
   }
}
