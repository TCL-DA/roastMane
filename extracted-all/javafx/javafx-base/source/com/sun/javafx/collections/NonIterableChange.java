package com.sun.javafx.collections;

import java.util.Collections;
import java.util.List;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public abstract class NonIterableChange<E> extends ListChangeListener.Change<E> {
   private final int from;
   private final int to;
   private boolean invalid = true;
   private static final int[] EMPTY_PERM = new int[0];

   protected NonIterableChange(int var1, int var2, ObservableList<E> var3) {
      super(var3);
      this.from = var1;
      this.to = var2;
   }

   @Override
   public int getFrom() {
      this.checkState();
      return this.from;
   }

   @Override
   public int getTo() {
      this.checkState();
      return this.to;
   }

   @Override
   protected int[] getPermutation() {
      this.checkState();
      return EMPTY_PERM;
   }

   @Override
   public boolean next() {
      if (this.invalid) {
         this.invalid = false;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void reset() {
      this.invalid = true;
   }

   public void checkState() {
      if (this.invalid) {
         throw new IllegalStateException("Invalid Change state: next() must be called before inspecting the Change.");
      }
   }

   @Override
   public String toString() {
      boolean var1 = this.invalid;
      this.invalid = false;
      String var2;
      if (this.wasPermutated()) {
         var2 = ChangeHelper.permChangeToString(this.getPermutation());
      } else if (this.wasUpdated()) {
         var2 = ChangeHelper.updateChangeToString(this.from, this.to);
      } else {
         var2 = ChangeHelper.addRemoveChangeToString(this.from, this.to, this.getList(), this.getRemoved());
      }

      this.invalid = var1;
      return "{ " + var2 + " }";
   }

   public static class GenericAddRemoveChange<E> extends NonIterableChange<E> {
      private final List<E> removed;

      public GenericAddRemoveChange(int var1, int var2, List<E> var3, ObservableList<E> var4) {
         super(var1, var2, var4);
         this.removed = var3;
      }

      @Override
      public List<E> getRemoved() {
         this.checkState();
         return this.removed;
      }
   }

   public static class SimpleAddChange<E> extends NonIterableChange<E> {
      public SimpleAddChange(int var1, int var2, ObservableList<E> var3) {
         super(var1, var2, var3);
      }

      @Override
      public boolean wasRemoved() {
         this.checkState();
         return false;
      }

      @Override
      public List<E> getRemoved() {
         this.checkState();
         return Collections.emptyList();
      }
   }

   public static class SimplePermutationChange<E> extends NonIterableChange<E> {
      private final int[] permutation;

      public SimplePermutationChange(int var1, int var2, int[] var3, ObservableList<E> var4) {
         super(var1, var2, var4);
         this.permutation = var3;
      }

      @Override
      public List<E> getRemoved() {
         this.checkState();
         return Collections.emptyList();
      }

      @Override
      protected int[] getPermutation() {
         this.checkState();
         return this.permutation;
      }
   }

   public static class SimpleRemovedChange<E> extends NonIterableChange<E> {
      private final List<E> removed;

      public SimpleRemovedChange(int var1, int var2, E var3, ObservableList<E> var4) {
         super(var1, var2, var4);
         this.removed = Collections.singletonList((E)var3);
      }

      @Override
      public boolean wasRemoved() {
         this.checkState();
         return true;
      }

      @Override
      public List<E> getRemoved() {
         this.checkState();
         return this.removed;
      }
   }

   public static class SimpleUpdateChange<E> extends NonIterableChange<E> {
      public SimpleUpdateChange(int var1, ObservableList<E> var2) {
         this(var1, var1 + 1, var2);
      }

      public SimpleUpdateChange(int var1, int var2, ObservableList<E> var3) {
         super(var1, var2, var3);
      }

      @Override
      public List<E> getRemoved() {
         return Collections.emptyList();
      }

      @Override
      public boolean wasUpdated() {
         return true;
      }
   }
}
