package javafx.collections.transformation;

import com.sun.javafx.collections.NonIterableChange;
import com.sun.javafx.collections.SortHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;
import java.util.function.Predicate;
import javafx.beans.NamedArg;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public final class FilteredList<E> extends TransformationList<E, E> {
   private int[] filtered;
   private int size;
   private SortHelper helper;
   private static final Predicate ALWAYS_TRUE = var0 -> true;
   private ObjectProperty<Predicate<? super E>> predicate;

   public FilteredList(@NamedArg("source") ObservableList<E> var1, @NamedArg("predicate") Predicate<? super E> var2) {
      super(var1);
      this.filtered = new int[var1.size() * 3 / 2 + 1];
      if (var2 != null) {
         this.setPredicate(var2);
      } else {
         this.size = 0;

         while (this.size < var1.size()) {
            this.filtered[this.size] = this.size++;
         }
      }
   }

   public FilteredList(@NamedArg("source") ObservableList<E> var1) {
      this(var1, null);
   }

   public final ObjectProperty<Predicate<? super E>> predicateProperty() {
      if (this.predicate == null) {
         this.predicate = new ObjectPropertyBase<Predicate<? super E>>() {
            @Override
            protected void invalidated() {
               FilteredList.this.refilter();
            }

            @Override
            public Object getBean() {
               return FilteredList.this;
            }

            @Override
            public String getName() {
               return "predicate";
            }
         };
      }

      return this.predicate;
   }

   public final Predicate<? super E> getPredicate() {
      return this.predicate == null ? null : this.predicate.get();
   }

   public final void setPredicate(Predicate<? super E> var1) {
      this.predicateProperty().set(var1);
   }

   private Predicate<? super E> getPredicateImpl() {
      return this.getPredicate() != null ? this.getPredicate() : ALWAYS_TRUE;
   }

   @Override
   protected void sourceChanged(ListChangeListener.Change<? extends E> var1) {
      this.beginChange();

      while (var1.next()) {
         if (var1.wasPermutated()) {
            this.permutate(var1);
         } else if (var1.wasUpdated()) {
            this.update(var1);
         } else {
            this.addRemove(var1);
         }
      }

      this.endChange();
   }

   @Override
   public int size() {
      return this.size;
   }

   @Override
   public E get(int var1) {
      if (var1 >= this.size) {
         throw new IndexOutOfBoundsException();
      } else {
         return (E)this.getSource().get(this.filtered[var1]);
      }
   }

   @Override
   public int getSourceIndex(int var1) {
      if (var1 >= this.size) {
         throw new IndexOutOfBoundsException();
      } else {
         return this.filtered[var1];
      }
   }

   @Override
   public int getViewIndex(int var1) {
      return Arrays.binarySearch(this.filtered, 0, this.size, var1);
   }

   private SortHelper getSortHelper() {
      if (this.helper == null) {
         this.helper = new SortHelper();
      }

      return this.helper;
   }

   private int findPosition(int var1) {
      if (this.filtered.length == 0) {
         return 0;
      } else if (var1 == 0) {
         return 0;
      } else {
         int var2 = Arrays.binarySearch(this.filtered, 0, this.size, var1);
         if (var2 < 0) {
            var2 = ~var2;
         }

         return var2;
      }
   }

   private void ensureSize(int var1) {
      if (this.filtered.length < var1) {
         int[] var2 = new int[var1 * 3 / 2 + 1];
         System.arraycopy(this.filtered, 0, var2, 0, this.size);
         this.filtered = var2;
      }
   }

   private void updateIndexes(int var1, int var2) {
      for (int var3 = var1; var3 < this.size; var3++) {
         this.filtered[var3] = this.filtered[var3] + var2;
      }
   }

   private void permutate(ListChangeListener.Change<? extends E> var1) {
      int var2 = this.findPosition(var1.getFrom());
      int var3 = this.findPosition(var1.getTo());
      if (var3 > var2) {
         for (int var4 = var2; var4 < var3; var4++) {
            this.filtered[var4] = var1.getPermutation(this.filtered[var4]);
         }

         int[] var5 = this.getSortHelper().sort(this.filtered, var2, var3);
         this.nextPermutation(var2, var3, var5);
      }
   }

   private void addRemove(ListChangeListener.Change<? extends E> var1) {
      Predicate var2 = this.getPredicateImpl();
      this.ensureSize(this.getSource().size());
      int var3 = this.findPosition(var1.getFrom());
      int var4 = this.findPosition(var1.getFrom() + var1.getRemovedSize());

      for (int var5 = var3; var5 < var4; var5++) {
         this.nextRemove(var3, (E)var1.getRemoved().get(this.filtered[var5] - var1.getFrom()));
      }

      this.updateIndexes(var4, var1.getAddedSize() - var1.getRemovedSize());
      int var8 = var3;
      int var6 = var1.getFrom();
      ListIterator var7 = this.getSource().listIterator(var6);

      while (var8 < var4 && var7.nextIndex() < var1.getTo()) {
         if (var2.test(var7.next())) {
            this.filtered[var8] = var7.previousIndex();
            this.nextAdd(var8, var8 + 1);
            var8++;
         }
      }

      if (var8 < var4) {
         System.arraycopy(this.filtered, var4, this.filtered, var8, this.size - var4);
         this.size -= var4 - var8;
      } else {
         for (; var7.nextIndex() < var1.getTo(); var6++) {
            if (var2.test(var7.next())) {
               System.arraycopy(this.filtered, var8, this.filtered, var8 + 1, this.size - var8);
               this.filtered[var8] = var7.previousIndex();
               this.nextAdd(var8, var8 + 1);
               var8++;
               this.size++;
            }
         }
      }
   }

   private void update(ListChangeListener.Change<? extends E> var1) {
      Predicate var2 = this.getPredicateImpl();
      this.ensureSize(this.getSource().size());
      int var3 = var1.getFrom();
      int var4 = var1.getTo();
      int var5 = this.findPosition(var3);
      int var6 = this.findPosition(var4);
      ListIterator var7 = this.getSource().listIterator(var3);

      for (int var8 = var5; var8 < var6 || var3 < var4; var3++) {
         Object var9 = var7.next();
         if (var8 < this.size && this.filtered[var8] == var3) {
            if (!var2.test(var9)) {
               this.nextRemove(var8, (E)var9);
               System.arraycopy(this.filtered, var8 + 1, this.filtered, var8, this.size - var8 - 1);
               this.size--;
               var6--;
            } else {
               this.nextUpdate(var8);
               var8++;
            }
         } else if (var2.test(var9)) {
            this.nextAdd(var8, var8 + 1);
            System.arraycopy(this.filtered, var8, this.filtered, var8 + 1, this.size - var8);
            this.filtered[var8] = var3;
            this.size++;
            var8++;
            var6++;
         }
      }
   }

   private void refilter() {
      this.ensureSize(this.getSource().size());
      ArrayList var1 = null;
      if (this.hasListeners()) {
         var1 = new ArrayList<>(this);
      }

      this.size = 0;
      int var2 = 0;
      Predicate var3 = this.getPredicateImpl();

      for (Object var5 : this.getSource()) {
         if (var3.test(var5)) {
            this.filtered[this.size++] = var2;
         }

         var2++;
      }

      if (this.hasListeners()) {
         this.fireChange(new NonIterableChange.GenericAddRemoveChange<>(0, this.size, var1, this));
      }
   }
}
