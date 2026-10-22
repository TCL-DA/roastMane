package javafx.collections.transformation;

import com.sun.javafx.collections.NonIterableChange;
import com.sun.javafx.collections.SortHelper;
import com.sun.javafx.collections.SourceAdapterChange;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javafx.beans.NamedArg;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public final class SortedList<E> extends TransformationList<E, E> {
   private Comparator<SortedList.Element<E>> elementComparator;
   private SortedList.Element<E>[] sorted;
   private int[] perm;
   private int size;
   private final SortHelper helper = new SortHelper();
   private final SortedList.Element<E> tempElement = new SortedList.Element<>(null, -1);
   private ObjectProperty<Comparator<? super E>> comparator;

   public SortedList(@NamedArg("source") ObservableList<? extends E> var1, @NamedArg("comparator") Comparator<? super E> var2) {
      super(var1);
      this.sorted = new SortedList.Element[var1.size() * 3 / 2 + 1];
      this.perm = new int[this.sorted.length];
      this.size = var1.size();

      for (int var3 = 0; var3 < this.size; this.perm[var3] = var3++) {
         this.sorted[var3] = new SortedList.Element<>((E)var1.get(var3), var3);
      }

      if (var2 != null) {
         this.setComparator(var2);
      }
   }

   public SortedList(@NamedArg("source") ObservableList<? extends E> var1) {
      this(var1, (Comparator<? super E>)null);
   }

   @Override
   protected void sourceChanged(ListChangeListener.Change<? extends E> var1) {
      if (this.elementComparator != null) {
         this.beginChange();

         while (var1.next()) {
            if (var1.wasPermutated()) {
               this.updatePermutationIndexes(var1);
            } else if (var1.wasUpdated()) {
               this.update(var1);
            } else {
               this.addRemove(var1);
            }
         }

         this.endChange();
      } else {
         this.updateUnsorted(var1);
         this.fireChange(new SourceAdapterChange<>(this, var1));
      }
   }

   public final ObjectProperty<Comparator<? super E>> comparatorProperty() {
      if (this.comparator == null) {
         this.comparator = new ObjectPropertyBase<Comparator<? super E>>() {
            @Override
            protected void invalidated() {
               Comparator var1 = this.get();
               SortedList.this.elementComparator = var1 != null ? new SortedList.ElementComparator<>(var1) : null;
               SortedList.this.doSortWithPermutationChange();
            }

            @Override
            public Object getBean() {
               return SortedList.this;
            }

            @Override
            public String getName() {
               return "comparator";
            }
         };
      }

      return this.comparator;
   }

   public final Comparator<? super E> getComparator() {
      return this.comparator == null ? null : this.comparator.get();
   }

   public final void setComparator(Comparator<? super E> var1) {
      this.comparatorProperty().set(var1);
   }

   @Override
   public E get(int var1) {
      if (var1 >= this.size) {
         throw new IndexOutOfBoundsException();
      } else {
         return this.sorted[var1].e;
      }
   }

   @Override
   public int size() {
      return this.size;
   }

   private void doSortWithPermutationChange() {
      if (this.elementComparator != null) {
         int[] var1 = this.helper.sort(this.sorted, 0, this.size, this.elementComparator);
         int var2 = 0;

         while (var2 < this.size) {
            this.perm[this.sorted[var2].index] = var2++;
         }

         this.fireChange(new NonIterableChange.SimplePermutationChange<>(0, this.size, var1, this));
      } else {
         int[] var8 = new int[this.size];
         int[] var9 = new int[this.size];

         for (int var3 = 0; var3 < this.size; var3++) {
            var8[var3] = var9[var3] = var3;
         }

         boolean var10 = false;
         int var4 = 0;

         while (var4 < this.size) {
            int var5 = this.sorted[var4].index;
            if (var5 == var4) {
               var4++;
            } else {
               SortedList.Element var6 = this.sorted[var5];
               this.sorted[var5] = this.sorted[var4];
               this.sorted[var4] = var6;
               this.perm[var4] = var4;
               this.perm[var5] = var5;
               var8[var9[var4]] = var5;
               var8[var9[var5]] = var4;
               int var7 = var9[var4];
               var9[var4] = var9[var5];
               var9[var5] = var7;
               var10 = true;
            }
         }

         if (var10) {
            this.fireChange(new NonIterableChange.SimplePermutationChange<>(0, this.size, var8, this));
         }
      }
   }

   @Override
   public int getSourceIndex(int var1) {
      return this.sorted[var1].index;
   }

   @Override
   public int getViewIndex(int var1) {
      return this.perm[var1];
   }

   private void updatePermutationIndexes(ListChangeListener.Change<? extends E> var1) {
      int var2 = 0;

      while (var2 < this.size) {
         int var3 = var1.getPermutation(this.sorted[var2].index);
         this.sorted[var2].index = var3;
         this.perm[var3] = var2++;
      }
   }

   private void updateUnsorted(ListChangeListener.Change<? extends E> var1) {
      while (var1.next()) {
         if (var1.wasPermutated()) {
            SortedList.Element[] var2 = new SortedList.Element[this.sorted.length];

            for (int var3 = 0; var3 < this.size; var3++) {
               if (var3 >= var1.getFrom() && var3 < var1.getTo()) {
                  int var4 = var1.getPermutation(var3);
                  var2[var4] = this.sorted[var3];
                  var2[var4].index = var4;
                  this.perm[var3] = var3;
               } else {
                  var2[var3] = this.sorted[var3];
               }
            }

            this.sorted = var2;
         }

         if (var1.wasRemoved()) {
            int var5 = var1.getFrom() + var1.getRemovedSize();
            System.arraycopy(this.sorted, var5, this.sorted, var1.getFrom(), this.size - var5);
            System.arraycopy(this.perm, var5, this.perm, var1.getFrom(), this.size - var5);
            this.size = this.size - var1.getRemovedSize();
            this.updateIndices(var5, var5, -var1.getRemovedSize());
         }

         if (var1.wasAdded()) {
            this.ensureSize(this.size + var1.getAddedSize());
            this.updateIndices(var1.getFrom(), var1.getFrom(), var1.getAddedSize());
            System.arraycopy(this.sorted, var1.getFrom(), this.sorted, var1.getTo(), this.size - var1.getFrom());
            System.arraycopy(this.perm, var1.getFrom(), this.perm, var1.getTo(), this.size - var1.getFrom());
            this.size = this.size + var1.getAddedSize();

            for (int var6 = var1.getFrom(); var6 < var1.getTo(); this.perm[var6] = var6++) {
               this.sorted[var6] = new SortedList.Element<>((E)var1.getList().get(var6), var6);
            }
         }
      }
   }

   private void ensureSize(int var1) {
      if (this.sorted.length < var1) {
         SortedList.Element[] var2 = new SortedList.Element[var1 * 3 / 2 + 1];
         System.arraycopy(this.sorted, 0, var2, 0, this.size);
         this.sorted = var2;
         int[] var3 = new int[var1 * 3 / 2 + 1];
         System.arraycopy(this.perm, 0, var3, 0, this.size);
         this.perm = var3;
      }
   }

   private void updateIndices(int var1, int var2, int var3) {
      for (int var4 = 0; var4 < this.size; var4++) {
         if (this.sorted[var4].index >= var1) {
            this.sorted[var4].index += var3;
         }

         if (this.perm[var4] >= var2) {
            this.perm[var4] = this.perm[var4] + var3;
         }
      }
   }

   private int findPosition(E var1) {
      if (this.sorted.length == 0) {
         return 0;
      } else {
         this.tempElement.e = (E)var1;
         return Arrays.binarySearch(this.sorted, 0, this.size, this.tempElement, this.elementComparator);
      }
   }

   private void insertToMapping(E var1, int var2) {
      int var3 = this.findPosition((E)var1);
      if (var3 < 0) {
         var3 = ~var3;
      }

      this.ensureSize(this.size + 1);
      this.updateIndices(var2, var3, 1);
      System.arraycopy(this.sorted, var3, this.sorted, var3 + 1, this.size - var3);
      this.sorted[var3] = new SortedList.Element<>((E)var1, var2);
      System.arraycopy(this.perm, var2, this.perm, var2 + 1, this.size - var2);
      this.perm[var2] = var3;
      this.size++;
      this.nextAdd(var3, var3 + 1);
   }

   private void setAllToMapping(List<? extends E> var1, int var2) {
      this.ensureSize(var2);
      this.size = var2;

      for (int var3 = 0; var3 < var2; var3++) {
         this.sorted[var3] = new SortedList.Element<>((E)var1.get(var3), var3);
      }

      int[] var4 = this.helper.sort(this.sorted, 0, this.size, this.elementComparator);
      System.arraycopy(var4, 0, this.perm, 0, this.size);
      this.nextAdd(0, this.size);
   }

   private void removeFromMapping(int var1, E var2) {
      int var3 = this.perm[var1];
      System.arraycopy(this.sorted, var3 + 1, this.sorted, var3, this.size - var3 - 1);
      System.arraycopy(this.perm, var1 + 1, this.perm, var1, this.size - var1 - 1);
      this.size--;
      this.sorted[this.size] = null;
      this.updateIndices(var1 + 1, var3, -1);
      this.nextRemove(var3, (E)var2);
   }

   private void removeAllFromMapping() {
      ArrayList var1 = new ArrayList<>(this);

      for (int var2 = 0; var2 < this.size; var2++) {
         this.sorted[var2] = null;
      }

      this.size = 0;
      this.nextRemove(0, var1);
   }

   private void update(ListChangeListener.Change<? extends E> var1) {
      int[] var2 = this.helper.sort(this.sorted, 0, this.size, this.elementComparator);
      int var3 = 0;

      while (var3 < this.size) {
         this.perm[this.sorted[var3].index] = var3++;
      }

      this.nextPermutation(0, this.size, var2);
      var3 = var1.getFrom();

      for (int var4 = var1.getTo(); var3 < var4; var3++) {
         this.nextUpdate(this.perm[var3]);
      }
   }

   private void addRemove(ListChangeListener.Change<? extends E> var1) {
      if (var1.getFrom() == 0 && var1.getRemovedSize() == this.size) {
         this.removeAllFromMapping();
      } else {
         int var2 = 0;

         for (int var3 = var1.getRemovedSize(); var2 < var3; var2++) {
            this.removeFromMapping(var1.getFrom(), (E)var1.getRemoved().get(var2));
         }
      }

      if (this.size == 0) {
         this.setAllToMapping(var1.getList(), var1.getTo());
      } else {
         int var4 = var1.getFrom();

         for (int var5 = var1.getTo(); var4 < var5; var4++) {
            this.insertToMapping((E)var1.getList().get(var4), var4);
         }
      }
   }

   private static class Element<E> {
      private E e;
      private int index;

      public Element(E var1, int var2) {
         this.e = (E)var1;
         this.index = var2;
      }
   }

   private static class ElementComparator<E> implements Comparator<SortedList.Element<E>> {
      private final Comparator<? super E> comparator;

      public ElementComparator(Comparator<? super E> var1) {
         this.comparator = var1;
      }

      public int compare(SortedList.Element<E> var1, SortedList.Element<E> var2) {
         return this.comparator.compare(var1.e, var2.e);
      }
   }
}
