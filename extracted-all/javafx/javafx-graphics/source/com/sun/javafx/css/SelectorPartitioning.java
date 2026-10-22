package com.sun.javafx.css;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.css.CompoundSelector;
import javafx.css.Selector;
import javafx.css.SimpleSelector;
import javafx.css.StyleClass;

public final class SelectorPartitioning {
   private final Map<SelectorPartitioning.PartitionKey, SelectorPartitioning.Partition> idMap = new HashMap<>();
   private final Map<SelectorPartitioning.PartitionKey, SelectorPartitioning.Partition> typeMap = new HashMap<>();
   private final Map<SelectorPartitioning.PartitionKey, SelectorPartitioning.Partition> styleClassMap = new HashMap<>();
   private int ordinal;
   private static final int ID_BIT = 4;
   private static final int TYPE_BIT = 2;
   private static final int STYLECLASS_BIT = 1;
   private static final SelectorPartitioning.PartitionKey WILDCARD = new SelectorPartitioning.PartitionKey<>("*");
   private static final Comparator<Selector> COMPARATOR = (var0, var1) -> var0.getOrdinal() - var1.getOrdinal();

   public void reset() {
      this.idMap.clear();
      this.typeMap.clear();
      this.styleClassMap.clear();
      this.ordinal = 0;
   }

   private static SelectorPartitioning.Partition getPartition(
      SelectorPartitioning.PartitionKey var0, Map<SelectorPartitioning.PartitionKey, SelectorPartitioning.Partition> var1
   ) {
      SelectorPartitioning.Partition var2 = (SelectorPartitioning.Partition)var1.get(var0);
      if (var2 == null) {
         var2 = new SelectorPartitioning.Partition(var0);
         var1.put(var0, var2);
      }

      return var2;
   }

   public void partition(Selector var1) {
      SimpleSelector var2 = null;
      if (var1 instanceof CompoundSelector) {
         List var3 = ((CompoundSelector)var1).getSelectors();
         int var4 = var3.size() - 1;
         var2 = (SimpleSelector)var3.get(var4);
      } else {
         var2 = (SimpleSelector)var1;
      }

      String var16 = var2.getId();
      boolean var17 = var16 != null && !var16.isEmpty();
      SelectorPartitioning.PartitionKey var5 = var17 ? new SelectorPartitioning.PartitionKey<>(var16) : null;
      String var6 = var2.getName();
      boolean var7 = var6 != null && !var6.isEmpty();
      SelectorPartitioning.PartitionKey var8 = var7 ? new SelectorPartitioning.PartitionKey<>(var6) : null;
      Set var9 = var2.getStyleClassSet();
      boolean var10 = var9 != null && var9.size() > 0;
      SelectorPartitioning.PartitionKey var11 = var10 ? new SelectorPartitioning.PartitionKey<>(var9) : null;
      int var12 = (var17 ? 4 : 0) | (var7 ? 2 : 0) | (var10 ? 1 : 0);
      SelectorPartitioning.Partition var13 = null;
      SelectorPartitioning.Slot var14 = null;
      var1.setOrdinal(this.ordinal++);
      switch (var12) {
         case 1:
         case 4:
         case 5:
         default:
            assert false;
            break;
         case 2:
         case 3:
            var13 = getPartition(var8, this.typeMap);
            if ((var12 & 1) == 1) {
               var14 = var13.partition(var11, this.styleClassMap);
               var14.addSelector(var1);
            } else {
               var13.addSelector(var1);
            }
            break;
         case 6:
         case 7:
            var13 = getPartition(var5, this.idMap);
            var14 = var13.partition(var8, this.typeMap);
            if ((var12 & 1) == 1) {
               var14 = var14.partition(var11, this.styleClassMap);
            }

            var14.addSelector(var1);
      }
   }

   public List<Selector> match(String var1, String var2, Set<StyleClass> var3) {
      boolean var4 = var1 != null && !var1.isEmpty();
      SelectorPartitioning.PartitionKey var5 = var4 ? new SelectorPartitioning.PartitionKey<>(var1) : null;
      boolean var6 = var2 != null && !var2.isEmpty();
      SelectorPartitioning.PartitionKey var7 = var6 ? new SelectorPartitioning.PartitionKey<>(var2) : null;
      boolean var8 = var3 != null && var3.size() > 0;
      SelectorPartitioning.PartitionKey var9 = var8 ? new SelectorPartitioning.PartitionKey<>(var3) : null;
      int var10 = (var4 ? 4 : 0) | (var6 ? 2 : 0) | (var8 ? 1 : 0);
      Object var11 = null;
      Object var12 = null;
      ArrayList var13 = new ArrayList();

      while (var10 != 0) {
         switch (var10) {
            case 1:
               var10--;
               break;
            case 2:
            case 3:
               SelectorPartitioning.PartitionKey var22 = var7;

               do {
                  var11 = this.typeMap.get(var22);
                  if (var11 != null) {
                     if (((SelectorPartitioning.Partition)var11).selectors != null) {
                        var13.addAll(((SelectorPartitioning.Partition)var11).selectors);
                     }

                     if ((var10 & 1) == 1) {
                        Set var23 = (Set)var9.key;

                        for (SelectorPartitioning.Slot var25 : ((SelectorPartitioning.Partition)var11).slots.values()) {
                           if (var25.selectors != null && !var25.selectors.isEmpty()) {
                              Set var26 = (Set)var25.partition.key.key;
                              if (var23.containsAll(var26)) {
                                 var13.addAll(var25.selectors);
                              }
                           }
                        }
                     }
                  }

                  var22 = !WILDCARD.equals(var22) ? WILDCARD : null;
               } while (var22 != null);

               var10 -= 2;
               break;
            case 4:
            case 5:
               var10 -= 4;
               break;
            case 6:
            case 7:
               var11 = this.idMap.get(var5);
               if (var11 != null) {
                  if (((SelectorPartitioning.Partition)var11).selectors != null) {
                     var13.addAll(((SelectorPartitioning.Partition)var11).selectors);
                  }

                  SelectorPartitioning.PartitionKey var14 = var7;

                  do {
                     var12 = ((SelectorPartitioning.Partition)var11).slots.get(var14);
                     if (var12 != null) {
                        if (((SelectorPartitioning.Slot)var12).selectors != null) {
                           var13.addAll(((SelectorPartitioning.Slot)var12).selectors);
                        }

                        if ((var10 & 1) == 1) {
                           Set var15 = (Set)var9.key;

                           for (SelectorPartitioning.Slot var17 : ((SelectorPartitioning.Slot)var12).referents.values()) {
                              if (var17.selectors != null && !var17.selectors.isEmpty()) {
                                 Set var18 = (Set)var17.partition.key.key;
                                 if (var15.containsAll(var18)) {
                                    var13.addAll(var17.selectors);
                                 }
                              }
                           }
                        }
                     }

                     var14 = !WILDCARD.equals(var14) ? WILDCARD : null;
                  } while (var14 != null);
               }

               var10 -= 4;
               break;
            default:
               assert false;
         }
      }

      Collections.sort(var13, COMPARATOR);
      return var13;
   }

   private static final class Partition {
      private final SelectorPartitioning.PartitionKey key;
      private final Map<SelectorPartitioning.PartitionKey, SelectorPartitioning.Slot> slots;
      private List<Selector> selectors;

      private Partition(SelectorPartitioning.PartitionKey var1) {
         this.key = var1;
         this.slots = new HashMap<>();
      }

      private void addSelector(Selector var1) {
         if (this.selectors == null) {
            this.selectors = new ArrayList<>();
         }

         this.selectors.add(var1);
      }

      private SelectorPartitioning.Slot partition(
         SelectorPartitioning.PartitionKey var1, Map<SelectorPartitioning.PartitionKey, SelectorPartitioning.Partition> var2
      ) {
         SelectorPartitioning.Slot var3 = this.slots.get(var1);
         if (var3 == null) {
            SelectorPartitioning.Partition var4 = SelectorPartitioning.getPartition(var1, var2);
            var3 = new SelectorPartitioning.Slot(var4);
            this.slots.put(var1, var3);
         }

         return var3;
      }
   }

   private static final class PartitionKey<K> {
      private final K key;

      private PartitionKey(K var1) {
         this.key = (K)var1;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == null) {
            return false;
         } else if (this.getClass() != var1.getClass()) {
            return false;
         } else {
            SelectorPartitioning.PartitionKey var2 = (SelectorPartitioning.PartitionKey)var1;
            return this.key == var2.key || this.key != null && this.key.equals(var2.key);
         }
      }

      @Override
      public int hashCode() {
         byte var1 = 7;
         return 71 * var1 + (this.key != null ? this.key.hashCode() : 0);
      }
   }

   private static final class Slot {
      private final SelectorPartitioning.Partition partition;
      private final Map<SelectorPartitioning.PartitionKey, SelectorPartitioning.Slot> referents;
      private List<Selector> selectors;

      private Slot(SelectorPartitioning.Partition var1) {
         this.partition = var1;
         this.referents = new HashMap<>();
      }

      private void addSelector(Selector var1) {
         if (this.selectors == null) {
            this.selectors = new ArrayList<>();
         }

         this.selectors.add(var1);
      }

      private SelectorPartitioning.Slot partition(
         SelectorPartitioning.PartitionKey var1, Map<SelectorPartitioning.PartitionKey, SelectorPartitioning.Partition> var2
      ) {
         SelectorPartitioning.Slot var3 = this.referents.get(var1);
         if (var3 == null) {
            SelectorPartitioning.Partition var4 = SelectorPartitioning.getPartition(var1, var2);
            var3 = new SelectorPartitioning.Slot(var4);
            this.referents.put(var1, var3);
         }

         return var3;
      }
   }
}
