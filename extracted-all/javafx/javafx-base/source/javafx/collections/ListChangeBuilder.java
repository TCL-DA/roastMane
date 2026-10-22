package javafx.collections;

import com.sun.javafx.collections.ChangeHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.Map.Entry;

final class ListChangeBuilder<E> {
   private static final int[] EMPTY_PERM = new int[0];
   private final ObservableListBase<E> list;
   private int changeLock;
   private List<ListChangeBuilder.SubChange<E>> addRemoveChanges;
   private List<ListChangeBuilder.SubChange<E>> updateChanges;
   private ListChangeBuilder.SubChange<E> permutationChange;

   private void checkAddRemoveList() {
      if (this.addRemoveChanges == null) {
         this.addRemoveChanges = new ArrayList<>();
      }
   }

   private void checkState() {
      if (this.changeLock == 0) {
         throw new IllegalStateException("beginChange was not called on this builder");
      }
   }

   private int findSubChange(int var1, List<ListChangeBuilder.SubChange<E>> var2) {
      int var3 = 0;
      int var4 = var2.size() - 1;

      while (var3 <= var4) {
         int var5 = (var3 + var4) / 2;
         ListChangeBuilder.SubChange var6 = (ListChangeBuilder.SubChange)var2.get(var5);
         if (var1 >= var6.to) {
            var3 = var5 + 1;
         } else {
            if (var1 >= var6.from) {
               return var5;
            }

            var4 = var5 - 1;
         }
      }

      return ~var3;
   }

   private void insertUpdate(int var1) {
      int var2 = this.findSubChange(var1, this.updateChanges);
      if (var2 < 0) {
         var2 = ~var2;
         ListChangeBuilder.SubChange var3;
         if (var2 > 0 && (var3 = this.updateChanges.get(var2 - 1)).to == var1) {
            var3.to = var1 + 1;
         } else if (var2 < this.updateChanges.size() && (var3 = this.updateChanges.get(var2)).from == var1 + 1) {
            var3.from = var1;
         } else {
            this.updateChanges.add(var2, new ListChangeBuilder.SubChange<>(var1, var1 + 1, null, EMPTY_PERM, true));
         }
      }
   }

   private void insertRemoved(int var1, E var2) {
      int var3 = this.findSubChange(var1, this.addRemoveChanges);
      if (var3 < 0) {
         var3 = ~var3;
         ListChangeBuilder.SubChange var4;
         if (var3 > 0 && (var4 = this.addRemoveChanges.get(var3 - 1)).to == var1) {
            var4.removed.add((E)var2);
            var3--;
         } else if (var3 < this.addRemoveChanges.size() && (var4 = this.addRemoveChanges.get(var3)).from == var1 + 1) {
            var4.from--;
            var4.to--;
            var4.removed.add(0, (E)var2);
         } else {
            ArrayList var5 = new ArrayList();
            var5.add(var2);
            this.addRemoveChanges.add(var3, new ListChangeBuilder.SubChange<>(var1, var1, var5, EMPTY_PERM, false));
         }
      } else {
         ListChangeBuilder.SubChange var7 = this.addRemoveChanges.get(var3);
         var7.to--;
         if (var7.from == var7.to && (var7.removed == null || var7.removed.isEmpty())) {
            this.addRemoveChanges.remove(var3);
         }
      }

      for (int var8 = var3 + 1; var8 < this.addRemoveChanges.size(); var8++) {
         ListChangeBuilder.SubChange var9 = this.addRemoveChanges.get(var8);
         var9.from--;
         var9.to--;
      }
   }

   private void insertAdd(int var1, int var2) {
      int var3 = this.findSubChange(var1, this.addRemoveChanges);
      int var4 = var2 - var1;
      if (var3 < 0) {
         var3 = ~var3;
         ListChangeBuilder.SubChange var5;
         if (var3 > 0 && (var5 = this.addRemoveChanges.get(var3 - 1)).to == var1) {
            var5.to = var2;
            var3--;
         } else {
            this.addRemoveChanges.add(var3, new ListChangeBuilder.SubChange<>(var1, var2, new ArrayList<>(), EMPTY_PERM, false));
         }
      } else {
         ListChangeBuilder.SubChange var7 = this.addRemoveChanges.get(var3);
         var7.to += var4;
      }

      for (int var8 = var3 + 1; var8 < this.addRemoveChanges.size(); var8++) {
         ListChangeBuilder.SubChange var6 = this.addRemoveChanges.get(var8);
         var6.from += var4;
         var6.to += var4;
      }
   }

   private int compress(List<ListChangeBuilder.SubChange<E>> var1) {
      int var2 = 0;
      ListChangeBuilder.SubChange var3 = (ListChangeBuilder.SubChange)var1.get(0);
      int var4 = 1;

      for (int var5 = var1.size(); var4 < var5; var4++) {
         ListChangeBuilder.SubChange var6 = (ListChangeBuilder.SubChange)var1.get(var4);
         if (var3.to == var6.from) {
            var3.to = var6.to;
            if (var3.removed != null || var6.removed != null) {
               if (var3.removed == null) {
                  var3.removed = new ArrayList<>();
               }

               var3.removed.addAll(var6.removed);
            }

            var1.set(var4, null);
            var2++;
         } else {
            var3 = var6;
         }
      }

      return var2;
   }

   ListChangeBuilder(ObservableListBase<E> var1) {
      this.list = var1;
   }

   public void nextRemove(int var1, E var2) {
      this.checkState();
      this.checkAddRemoveList();
      ListChangeBuilder.SubChange var3 = this.addRemoveChanges.isEmpty() ? null : this.addRemoveChanges.get(this.addRemoveChanges.size() - 1);
      if (var3 != null && var3.to == var1) {
         var3.removed.add((E)var2);
      } else if (var3 != null && var3.from == var1 + 1) {
         var3.from--;
         var3.to--;
         var3.removed.add(0, (E)var2);
      } else {
         this.insertRemoved(var1, (E)var2);
      }

      if (this.updateChanges != null && !this.updateChanges.isEmpty()) {
         int var4 = this.findSubChange(var1, this.updateChanges);
         if (var4 < 0) {
            var4 = ~var4;
         } else {
            ListChangeBuilder.SubChange var5 = this.updateChanges.get(var4);
            if (var5.from == var5.to - 1) {
               this.updateChanges.remove(var4);
            } else {
               var5.to--;
               var4++;
            }
         }

         for (int var6 = var4; var6 < this.updateChanges.size(); var6++) {
            this.updateChanges.get(var6).from--;
            this.updateChanges.get(var6).to--;
         }
      }
   }

   public void nextRemove(int var1, List<? extends E> var2) {
      this.checkState();

      for (int var3 = 0; var3 < var2.size(); var3++) {
         this.nextRemove(var1, (E)var2.get(var3));
      }
   }

   public void nextAdd(int var1, int var2) {
      this.checkState();
      this.checkAddRemoveList();
      ListChangeBuilder.SubChange var3 = this.addRemoveChanges.isEmpty() ? null : this.addRemoveChanges.get(this.addRemoveChanges.size() - 1);
      int var4 = var2 - var1;
      if (var3 != null && var3.to == var1) {
         var3.to = var2;
      } else if (var3 != null && var1 >= var3.from && var1 < var3.to) {
         var3.to += var4;
      } else {
         this.insertAdd(var1, var2);
      }

      if (this.updateChanges != null && !this.updateChanges.isEmpty()) {
         int var5 = this.findSubChange(var1, this.updateChanges);
         if (var5 < 0) {
            var5 = ~var5;
         } else {
            ListChangeBuilder.SubChange var6 = this.updateChanges.get(var5);
            this.updateChanges.add(var5 + 1, new ListChangeBuilder.SubChange<>(var2, var6.to + var2 - var1, null, EMPTY_PERM, true));
            var6.to = var1;
            var5 += 2;
         }

         for (int var8 = var5; var8 < this.updateChanges.size(); var8++) {
            this.updateChanges.get(var8).from += var4;
            this.updateChanges.get(var8).to += var4;
         }
      }
   }

   public void nextPermutation(int var1, int var2, int[] var3) {
      this.checkState();
      int var4 = var1;
      int var5 = var2;
      int[] var6 = var3;
      if (this.addRemoveChanges != null && !this.addRemoveChanges.isEmpty()) {
         int[] var7 = new int[this.list.size()];
         TreeSet var8 = new TreeSet();
         int var9 = 0;
         int var10 = 0;
         int var11 = 0;

         for (int var12 = this.addRemoveChanges.size(); var11 < var12; var11++) {
            ListChangeBuilder.SubChange var13 = this.addRemoveChanges.get(var11);

            for (int var14 = var9; var14 < var13.from; var14++) {
               var7[var14 >= var1 && var14 < var2 ? var3[var14 - var1] : var14] = var14 + var10;
            }

            for (int var49 = var13.from; var49 < var13.to; var49++) {
               var7[var49 >= var1 && var49 < var2 ? var3[var49 - var1] : var49] = -1;
            }

            var9 = var13.to;
            int var50 = var13.removed != null ? var13.removed.size() : 0;
            int var15 = var13.from + var10;

            for (int var16 = var13.from + var10 + var50; var15 < var16; var15++) {
               var8.add(var15);
            }

            var10 += var50 - (var13.to - var13.from);
         }

         for (int var36 = var9; var36 < var7.length; var36++) {
            var7[var36 >= var1 && var36 < var2 ? var3[var36 - var1] : var36] = var36 + var10;
         }

         int[] var37 = new int[this.list.size() + var10];
         int var43 = 0;

         for (int var47 = 0; var47 < var37.length; var47++) {
            if (var8.contains(var47)) {
               var37[var47] = var47;
            } else {
               while (var7[var43] == -1) {
                  var43++;
               }

               var37[var7[var43++]] = var47;
            }
         }

         var4 = 0;
         var5 = var37.length;
         var6 = var37;
      }

      if (this.permutationChange != null) {
         if (var4 == this.permutationChange.from && var5 == this.permutationChange.to) {
            for (int var18 = 0; var18 < var6.length; var18++) {
               this.permutationChange.perm[var18] = var6[this.permutationChange.perm[var18] - var4];
            }
         } else {
            int var17 = Math.max(this.permutationChange.to, var5);
            int var21 = Math.min(this.permutationChange.from, var4);
            int[] var25 = new int[var17 - var21];

            for (int var30 = var21; var30 < var17; var30++) {
               if (var30 >= this.permutationChange.from && var30 < this.permutationChange.to) {
                  int var38 = this.permutationChange.perm[var30 - this.permutationChange.from];
                  if (var38 >= var4 && var38 < var5) {
                     var25[var30 - var21] = var6[var38 - var4];
                  } else {
                     var25[var30 - var21] = var38;
                  }
               } else {
                  var25[var30 - var21] = var6[var30 - var4];
               }
            }

            this.permutationChange.from = var21;
            this.permutationChange.to = var17;
            this.permutationChange.perm = var25;
         }
      } else {
         this.permutationChange = new ListChangeBuilder.SubChange<>(var4, var5, null, var6, false);
      }

      if (this.addRemoveChanges != null && !this.addRemoveChanges.isEmpty()) {
         TreeSet var19 = new TreeSet();
         HashMap var22 = new HashMap();
         int var26 = 0;

         for (int var31 = this.addRemoveChanges.size(); var26 < var31; var26++) {
            ListChangeBuilder.SubChange var39 = this.addRemoveChanges.get(var26);

            for (int var44 = var39.from; var44 < var39.to; var44++) {
               if (var44 >= var1 && var44 < var2) {
                  var19.add(var3[var44 - var1]);
               } else {
                  var19.add(var44);
               }
            }

            if (var39.removed != null) {
               if (var39.from >= var1 && var39.from < var2) {
                  var22.put(var3[var39.from - var1], var39.removed);
               } else {
                  var22.put(var39.from, var39.removed);
               }
            }
         }

         this.addRemoveChanges.clear();
         ListChangeBuilder.SubChange var27 = null;

         for (Integer var40 : var19) {
            if (var27 != null && var27.to == var40) {
               var27.to = var40 + 1;
            } else {
               var27 = new ListChangeBuilder.SubChange(var40, var40 + 1, null, EMPTY_PERM, false);
               this.addRemoveChanges.add(var27);
            }

            List var45 = (List)var22.remove(var40);
            if (var45 != null) {
               if (var27.removed != null) {
                  var27.removed.addAll(var45);
               } else {
                  var27.removed = var45;
               }
            }
         }

         for (Entry var41 : var22.entrySet()) {
            Integer var46 = (Integer)var41.getKey();
            int var48 = this.findSubChange(var46, this.addRemoveChanges);

            assert var48 < 0;

            this.addRemoveChanges.add(~var48, new ListChangeBuilder.SubChange<>(var46, var46, (List<E>)var41.getValue(), new int[0], false));
         }
      }

      if (this.updateChanges != null && !this.updateChanges.isEmpty()) {
         TreeSet var20 = new TreeSet();
         int var23 = 0;

         for (int var28 = this.updateChanges.size(); var23 < var28; var23++) {
            ListChangeBuilder.SubChange var34 = this.updateChanges.get(var23);

            for (int var42 = var34.from; var42 < var34.to; var42++) {
               if (var42 >= var1 && var42 < var2) {
                  var20.add(var3[var42 - var1]);
               } else {
                  var20.add(var42);
               }
            }
         }

         this.updateChanges.clear();
         ListChangeBuilder.SubChange var24 = null;

         for (Integer var35 : var20) {
            if (var24 != null && var24.to == var35) {
               var24.to = var35 + 1;
            } else {
               var24 = new ListChangeBuilder.SubChange(var35, var35 + 1, null, EMPTY_PERM, true);
               this.updateChanges.add(var24);
            }
         }
      }
   }

   public void nextReplace(int var1, int var2, List<? extends E> var3) {
      this.nextRemove(var1, var3);
      this.nextAdd(var1, var2);
   }

   public void nextSet(int var1, E var2) {
      this.nextRemove(var1, (E)var2);
      this.nextAdd(var1, var1 + 1);
   }

   public void nextUpdate(int var1) {
      this.checkState();
      if (this.updateChanges == null) {
         this.updateChanges = new ArrayList<>();
      }

      ListChangeBuilder.SubChange var2 = this.updateChanges.isEmpty() ? null : this.updateChanges.get(this.updateChanges.size() - 1);
      if (var2 != null && var2.to == var1) {
         var2.to = var1 + 1;
      } else {
         this.insertUpdate(var1);
      }
   }

   private void commit() {
      boolean var1 = this.addRemoveChanges != null && !this.addRemoveChanges.isEmpty();
      boolean var2 = this.updateChanges != null && !this.updateChanges.isEmpty();
      if (this.changeLock == 0 && (var1 || var2 || this.permutationChange != null)) {
         int var3 = (this.updateChanges != null ? this.updateChanges.size() : 0)
            + (this.addRemoveChanges != null ? this.addRemoveChanges.size() : 0)
            + (this.permutationChange != null ? 1 : 0);
         if (var3 == 1) {
            if (var1) {
               this.list.fireChange(new ListChangeBuilder.SingleChange<>(finalizeSubChange(this.addRemoveChanges.get(0)), this.list));
               this.addRemoveChanges.clear();
            } else if (var2) {
               this.list.fireChange(new ListChangeBuilder.SingleChange<>(finalizeSubChange(this.updateChanges.get(0)), this.list));
               this.updateChanges.clear();
            } else {
               this.list.fireChange(new ListChangeBuilder.SingleChange<>(finalizeSubChange(this.permutationChange), this.list));
               this.permutationChange = null;
            }
         } else {
            if (var2) {
               int var4 = this.compress(this.updateChanges);
               var3 -= var4;
            }

            if (var1) {
               int var9 = this.compress(this.addRemoveChanges);
               var3 -= var9;
            }

            ListChangeBuilder.SubChange[] var10 = new ListChangeBuilder.SubChange[var3];
            int var5 = 0;
            if (this.permutationChange != null) {
               var10[var5++] = this.permutationChange;
            }

            if (var1) {
               int var6 = this.addRemoveChanges.size();

               for (int var7 = 0; var7 < var6; var7++) {
                  ListChangeBuilder.SubChange var8 = this.addRemoveChanges.get(var7);
                  if (var8 != null) {
                     var10[var5++] = var8;
                  }
               }
            }

            if (var2) {
               int var11 = this.updateChanges.size();

               for (int var12 = 0; var12 < var11; var12++) {
                  ListChangeBuilder.SubChange var13 = this.updateChanges.get(var12);
                  if (var13 != null) {
                     var10[var5++] = var13;
                  }
               }
            }

            this.list.fireChange(new ListChangeBuilder.IterableChange<>(finalizeSubChangeArray(var10), this.list));
            if (this.addRemoveChanges != null) {
               this.addRemoveChanges.clear();
            }

            if (this.updateChanges != null) {
               this.updateChanges.clear();
            }

            this.permutationChange = null;
         }
      }
   }

   public void beginChange() {
      this.changeLock++;
   }

   public void endChange() {
      if (this.changeLock <= 0) {
         throw new IllegalStateException("Called endChange before beginChange");
      } else {
         this.changeLock--;
         this.commit();
      }
   }

   private static <E> ListChangeBuilder.SubChange<E>[] finalizeSubChangeArray(ListChangeBuilder.SubChange<E>[] var0) {
      for (ListChangeBuilder.SubChange var4 : var0) {
         finalizeSubChange(var4);
      }

      return var0;
   }

   private static <E> ListChangeBuilder.SubChange<E> finalizeSubChange(ListChangeBuilder.SubChange<E> var0) {
      if (var0.perm == null) {
         var0.perm = EMPTY_PERM;
      }

      if (var0.removed == null) {
         var0.removed = Collections.emptyList();
      } else {
         var0.removed = Collections.unmodifiableList(var0.removed);
      }

      return var0;
   }

   private static class IterableChange<E> extends ListChangeListener.Change<E> {
      private ListChangeBuilder.SubChange[] changes;
      private int cursor = -1;

      private IterableChange(ListChangeBuilder.SubChange[] var1, ObservableList<E> var2) {
         super(var2);
         this.changes = var1;
      }

      @Override
      public boolean next() {
         if (this.cursor + 1 < this.changes.length) {
            this.cursor++;
            return true;
         } else {
            return false;
         }
      }

      @Override
      public void reset() {
         this.cursor = -1;
      }

      @Override
      public int getFrom() {
         this.checkState();
         return this.changes[this.cursor].from;
      }

      @Override
      public int getTo() {
         this.checkState();
         return this.changes[this.cursor].to;
      }

      @Override
      public List<E> getRemoved() {
         this.checkState();
         return this.changes[this.cursor].removed;
      }

      @Override
      protected int[] getPermutation() {
         this.checkState();
         return this.changes[this.cursor].perm;
      }

      @Override
      public boolean wasUpdated() {
         this.checkState();
         return this.changes[this.cursor].updated;
      }

      private void checkState() {
         if (this.cursor == -1) {
            throw new IllegalStateException("Invalid Change state: next() must be called before inspecting the Change.");
         }
      }

      @Override
      public String toString() {
         int var1 = 0;
         StringBuilder var2 = new StringBuilder();
         var2.append("{ ");

         for (; var1 < this.changes.length; var1++) {
            if (this.changes[var1].perm.length != 0) {
               var2.append(ChangeHelper.permChangeToString(this.changes[var1].perm));
            } else if (this.changes[var1].updated) {
               var2.append(ChangeHelper.updateChangeToString(this.changes[var1].from, this.changes[var1].to));
            } else {
               var2.append(ChangeHelper.addRemoveChangeToString(this.changes[var1].from, this.changes[var1].to, this.getList(), this.changes[var1].removed));
            }

            if (var1 != this.changes.length - 1) {
               var2.append(", ");
            }
         }

         var2.append(" }");
         return var2.toString();
      }
   }

   private static class SingleChange<E> extends ListChangeListener.Change<E> {
      private final ListChangeBuilder.SubChange<E> change;
      private boolean onChange;

      public SingleChange(ListChangeBuilder.SubChange<E> var1, ObservableListBase<E> var2) {
         super(var2);
         this.change = var1;
      }

      @Override
      public boolean next() {
         if (this.onChange) {
            return false;
         } else {
            this.onChange = true;
            return true;
         }
      }

      @Override
      public void reset() {
         this.onChange = false;
      }

      @Override
      public int getFrom() {
         this.checkState();
         return this.change.from;
      }

      @Override
      public int getTo() {
         this.checkState();
         return this.change.to;
      }

      @Override
      public List<E> getRemoved() {
         this.checkState();
         return this.change.removed;
      }

      @Override
      protected int[] getPermutation() {
         this.checkState();
         return this.change.perm;
      }

      @Override
      public boolean wasUpdated() {
         this.checkState();
         return this.change.updated;
      }

      private void checkState() {
         if (!this.onChange) {
            throw new IllegalStateException("Invalid Change state: next() must be called before inspecting the Change.");
         }
      }

      @Override
      public String toString() {
         String var1;
         if (this.change.perm.length != 0) {
            var1 = ChangeHelper.permChangeToString(this.change.perm);
         } else if (this.change.updated) {
            var1 = ChangeHelper.updateChangeToString(this.change.from, this.change.to);
         } else {
            var1 = ChangeHelper.addRemoveChangeToString(this.change.from, this.change.to, this.getList(), this.change.removed);
         }

         return "{ " + var1 + " }";
      }
   }

   private static class SubChange<E> {
      int from;
      int to;
      List<E> removed;
      int[] perm;
      boolean updated;

      public SubChange(int var1, int var2, List<E> var3, int[] var4, boolean var5) {
         this.from = var1;
         this.to = var2;
         this.removed = var3;
         this.perm = var4;
         this.updated = var5;
      }
   }
}
