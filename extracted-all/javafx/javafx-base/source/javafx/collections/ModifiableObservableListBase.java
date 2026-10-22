package javafx.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public abstract class ModifiableObservableListBase<E> extends ObservableListBase<E> {
   @Override
   public boolean setAll(Collection<? extends E> var1) {
      if (this.isEmpty() && var1.isEmpty()) {
         return false;
      } else {
         this.beginChange();

         boolean var2;
         try {
            this.clear();
            this.addAll(var1);
            var2 = true;
         } finally {
            this.endChange();
         }

         return var2;
      }
   }

   @Override
   public boolean addAll(Collection<? extends E> var1) {
      this.beginChange();

      boolean var3;
      try {
         boolean var2 = super.addAll(var1);
         var3 = var2;
      } finally {
         this.endChange();
      }

      return var3;
   }

   @Override
   public boolean addAll(int var1, Collection<? extends E> var2) {
      this.beginChange();

      boolean var4;
      try {
         boolean var3 = super.addAll(var1, var2);
         var4 = var3;
      } finally {
         this.endChange();
      }

      return var4;
   }

   @Override
   protected void removeRange(int var1, int var2) {
      this.beginChange();

      try {
         super.removeRange(var1, var2);
      } finally {
         this.endChange();
      }
   }

   @Override
   public boolean removeAll(Collection<?> var1) {
      this.beginChange();

      boolean var3;
      try {
         boolean var2 = super.removeAll(var1);
         var3 = var2;
      } finally {
         this.endChange();
      }

      return var3;
   }

   @Override
   public boolean retainAll(Collection<?> var1) {
      this.beginChange();

      boolean var3;
      try {
         boolean var2 = super.retainAll(var1);
         var3 = var2;
      } finally {
         this.endChange();
      }

      return var3;
   }

   @Override
   public void add(int var1, E var2) {
      this.doAdd(var1, (E)var2);
      this.beginChange();
      this.nextAdd(var1, var1 + 1);
      this.modCount++;
      this.endChange();
   }

   @Override
   public E set(int var1, E var2) {
      Object var3 = this.doSet(var1, (E)var2);
      this.beginChange();
      this.nextSet(var1, (E)var3);
      this.endChange();
      return (E)var3;
   }

   @Override
   public boolean remove(Object var1) {
      int var2 = this.indexOf(var1);
      if (var2 != -1) {
         this.remove(var2);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public E remove(int var1) {
      Object var2 = this.doRemove(var1);
      this.beginChange();
      this.nextRemove(var1, (E)var2);
      this.modCount++;
      this.endChange();
      return (E)var2;
   }

   @Override
   public List<E> subList(int var1, int var2) {
      return new ModifiableObservableListBase.SubObservableList(super.subList(var1, var2));
   }

   @Override
   public abstract E get(int var1);

   @Override
   public abstract int size();

   protected abstract void doAdd(int var1, E var2);

   protected abstract E doSet(int var1, E var2);

   protected abstract E doRemove(int var1);

   private class SubObservableList implements List<E> {
      private List<E> sublist;

      public SubObservableList(List<E> var2) {
         this.sublist = var2;
      }

      @Override
      public int size() {
         return this.sublist.size();
      }

      @Override
      public boolean isEmpty() {
         return this.sublist.isEmpty();
      }

      @Override
      public boolean contains(Object var1) {
         return this.sublist.contains(var1);
      }

      @Override
      public Iterator<E> iterator() {
         return this.sublist.iterator();
      }

      @Override
      public Object[] toArray() {
         return this.sublist.toArray();
      }

      @Override
      public <T> T[] toArray(T[] var1) {
         return (T[])this.sublist.toArray(var1);
      }

      @Override
      public boolean add(E var1) {
         return this.sublist.add((E)var1);
      }

      @Override
      public boolean remove(Object var1) {
         return this.sublist.remove(var1);
      }

      @Override
      public boolean containsAll(Collection<?> var1) {
         return this.sublist.containsAll(var1);
      }

      @Override
      public boolean addAll(Collection<? extends E> var1) {
         ModifiableObservableListBase.this.beginChange();

         boolean var3;
         try {
            boolean var2 = this.sublist.addAll(var1);
            var3 = var2;
         } finally {
            ModifiableObservableListBase.this.endChange();
         }

         return var3;
      }

      @Override
      public boolean addAll(int var1, Collection<? extends E> var2) {
         ModifiableObservableListBase.this.beginChange();

         boolean var4;
         try {
            boolean var3 = this.sublist.addAll(var1, var2);
            var4 = var3;
         } finally {
            ModifiableObservableListBase.this.endChange();
         }

         return var4;
      }

      @Override
      public boolean removeAll(Collection<?> var1) {
         ModifiableObservableListBase.this.beginChange();

         boolean var3;
         try {
            boolean var2 = this.sublist.removeAll(var1);
            var3 = var2;
         } finally {
            ModifiableObservableListBase.this.endChange();
         }

         return var3;
      }

      @Override
      public boolean retainAll(Collection<?> var1) {
         ModifiableObservableListBase.this.beginChange();

         boolean var3;
         try {
            boolean var2 = this.sublist.retainAll(var1);
            var3 = var2;
         } finally {
            ModifiableObservableListBase.this.endChange();
         }

         return var3;
      }

      @Override
      public void clear() {
         ModifiableObservableListBase.this.beginChange();

         try {
            this.sublist.clear();
         } finally {
            ModifiableObservableListBase.this.endChange();
         }
      }

      @Override
      public E get(int var1) {
         return this.sublist.get(var1);
      }

      @Override
      public E set(int var1, E var2) {
         return this.sublist.set(var1, (E)var2);
      }

      @Override
      public void add(int var1, E var2) {
         this.sublist.add(var1, (E)var2);
      }

      @Override
      public E remove(int var1) {
         return this.sublist.remove(var1);
      }

      @Override
      public int indexOf(Object var1) {
         return this.sublist.indexOf(var1);
      }

      @Override
      public int lastIndexOf(Object var1) {
         return this.sublist.lastIndexOf(var1);
      }

      @Override
      public ListIterator<E> listIterator() {
         return this.sublist.listIterator();
      }

      @Override
      public ListIterator<E> listIterator(int var1) {
         return this.sublist.listIterator(var1);
      }

      @Override
      public List<E> subList(int var1, int var2) {
         return ModifiableObservableListBase.this.new SubObservableList(this.sublist.subList(var1, var2));
      }

      @Override
      public boolean equals(Object var1) {
         return this.sublist.equals(var1);
      }

      @Override
      public int hashCode() {
         return this.sublist.hashCode();
      }

      @Override
      public String toString() {
         return this.sublist.toString();
      }
   }
}
