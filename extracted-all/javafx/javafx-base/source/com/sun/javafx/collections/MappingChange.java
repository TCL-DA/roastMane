package com.sun.javafx.collections;

import java.util.AbstractList;
import java.util.List;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public final class MappingChange<E, F> extends ListChangeListener.Change<F> {
   private final MappingChange.Map<E, F> map;
   private final ListChangeListener.Change<? extends E> original;
   private List<F> removed;
   public static final MappingChange.Map NOOP_MAP = new MappingChange.Map() {
      @Override
      public Object map(Object var1) {
         return var1;
      }
   };

   public MappingChange(ListChangeListener.Change<? extends E> var1, MappingChange.Map<E, F> var2, ObservableList<F> var3) {
      super(var3);
      this.original = var1;
      this.map = var2;
   }

   @Override
   public boolean next() {
      return this.original.next();
   }

   @Override
   public void reset() {
      this.original.reset();
   }

   @Override
   public int getFrom() {
      return this.original.getFrom();
   }

   @Override
   public int getTo() {
      return this.original.getTo();
   }

   @Override
   public List<F> getRemoved() {
      if (this.removed == null) {
         this.removed = new AbstractList<F>() {
            @Override
            public F get(int var1) {
               return MappingChange.this.map.map((E)MappingChange.this.original.getRemoved().get(var1));
            }

            @Override
            public int size() {
               return MappingChange.this.original.getRemovedSize();
            }
         };
      }

      return this.removed;
   }

   @Override
   protected int[] getPermutation() {
      return new int[0];
   }

   @Override
   public boolean wasPermutated() {
      return this.original.wasPermutated();
   }

   @Override
   public boolean wasUpdated() {
      return this.original.wasUpdated();
   }

   @Override
   public int getPermutation(int var1) {
      return this.original.getPermutation(var1);
   }

   @Override
   public String toString() {
      int var1 = 0;

      while (this.next()) {
         var1++;
      }

      int var2 = 0;
      this.reset();

      while (this.next()) {
         var2++;
      }

      this.reset();
      StringBuilder var3 = new StringBuilder();
      var3.append("{ ");
      int var4 = 0;

      while (this.next()) {
         if (this.wasPermutated()) {
            var3.append(ChangeHelper.permChangeToString(this.getPermutation()));
         } else if (this.wasUpdated()) {
            var3.append(ChangeHelper.updateChangeToString(this.getFrom(), this.getTo()));
         } else {
            var3.append(ChangeHelper.addRemoveChangeToString(this.getFrom(), this.getTo(), this.getList(), this.getRemoved()));
         }

         if (var4 != var2) {
            var3.append(", ");
         }
      }

      var3.append(" }");
      this.reset();
      var4 = var2 - var1;

      while (var4-- > 0) {
         this.next();
      }

      return var3.toString();
   }

   public interface Map<E, F> {
      F map(E var1);
   }
}
