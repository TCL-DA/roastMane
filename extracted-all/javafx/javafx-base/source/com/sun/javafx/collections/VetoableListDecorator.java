package com.sun.javafx.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public abstract class VetoableListDecorator<E> implements ObservableList<E> {
   private final ObservableList<E> list;
   private int modCount;
   private ListListenerHelper<E> helper;

   protected abstract void onProposedChange(List<E> var1, int... var2);

   public VetoableListDecorator(ObservableList<E> var1) {
      this.list = var1;
      this.list.addListener(var1x -> ListListenerHelper.fireValueChangedEvent(this.helper, new SourceAdapterChange<>(this, var1x)));
   }

   @Override
   public void addListener(ListChangeListener<? super E> var1) {
      this.helper = ListListenerHelper.addListener(this.helper, var1);
   }

   @Override
   public void removeListener(ListChangeListener<? super E> var1) {
      this.helper = ListListenerHelper.removeListener(this.helper, var1);
   }

   @Override
   public void addListener(InvalidationListener var1) {
      this.helper = ListListenerHelper.addListener(this.helper, var1);
   }

   @Override
   public void removeListener(InvalidationListener var1) {
      this.helper = ListListenerHelper.removeListener(this.helper, var1);
   }

   @Override
   public boolean addAll(E... var1) {
      return this.addAll(Arrays.asList((E[])var1));
   }

   @Override
   public boolean setAll(E... var1) {
      return this.setAll(Arrays.asList((E[])var1));
   }

   @Override
   public boolean setAll(Collection<? extends E> var1) {
      this.onProposedChange(Collections.unmodifiableList(new ArrayList<>(var1)), 0, this.size());

      try {
         this.modCount++;
         return this.list.setAll(var1);
      } catch (Exception var3) {
         this.modCount--;
         throw var3;
      }
   }

   private void removeFromList(List<E> var1, int var2, Collection<?> var3, boolean var4) {
      int[] var5 = new int[2];
      byte var6 = -1;

      for (int var7 = 0; var7 < var1.size(); var7++) {
         Object var8 = var1.get(var7);
         if (var3.contains(var8) ^ var4) {
            if (var6 == -1) {
               var5[var6 + 1] = var2 + var7;
               var5[var6 + 2] = var2 + var7 + 1;
               var6 += 2;
            } else if (var5[var6 - 1] == var2 + var7) {
               var5[var6 - 1] = var2 + var7 + 1;
            } else {
               int[] var9 = new int[var5.length + 2];
               System.arraycopy(var5, 0, var9, 0, var5.length);
               var5 = var9;
               var9[var6 + 1] = var2 + var7;
               var9[var6 + 2] = var2 + var7 + 1;
               var6 += 2;
            }
         }
      }

      if (var6 != -1) {
         this.onProposedChange(Collections.emptyList(), var5);
      }
   }

   @Override
   public boolean removeAll(E... var1) {
      return this.removeAll(Arrays.asList(var1));
   }

   @Override
   public boolean retainAll(E... var1) {
      return this.retainAll(Arrays.asList(var1));
   }

   @Override
   public void remove(int var1, int var2) {
      this.onProposedChange(Collections.emptyList(), var1, var2);

      try {
         this.modCount++;
         this.list.remove(var1, var2);
      } catch (Exception var4) {
         this.modCount--;
      }
   }

   @Override
   public int size() {
      return this.list.size();
   }

   @Override
   public boolean isEmpty() {
      return this.list.isEmpty();
   }

   @Override
   public boolean contains(Object var1) {
      return this.list.contains(var1);
   }

   @Override
   public Iterator<E> iterator() {
      return new VetoableListDecorator.VetoableIteratorDecorator(new VetoableListDecorator.ModCountAccessorImpl(), this.list.iterator(), 0);
   }

   @Override
   public Object[] toArray() {
      return this.list.toArray();
   }

   @Override
   public <T> T[] toArray(T[] var1) {
      return (T[])this.list.toArray(var1);
   }

   @Override
   public boolean add(E var1) {
      this.onProposedChange(Collections.singletonList((E)var1), this.size(), this.size());

      try {
         this.modCount++;
         this.list.add((E)var1);
         return true;
      } catch (Exception var3) {
         this.modCount--;
         throw var3;
      }
   }

   @Override
   public boolean remove(Object var1) {
      int var2 = this.list.indexOf(var1);
      if (var2 != -1) {
         this.remove(var2);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean containsAll(Collection<?> var1) {
      return this.list.containsAll(var1);
   }

   @Override
   public boolean addAll(Collection<? extends E> var1) {
      this.onProposedChange(Collections.unmodifiableList(new ArrayList<>(var1)), this.size(), this.size());

      try {
         this.modCount++;
         boolean var2 = this.list.addAll(var1);
         if (!var2) {
            this.modCount--;
         }

         return var2;
      } catch (Exception var3) {
         this.modCount--;
         throw var3;
      }
   }

   @Override
   public boolean addAll(int var1, Collection<? extends E> var2) {
      this.onProposedChange(Collections.unmodifiableList(new ArrayList<>(var2)), var1, var1);

      try {
         this.modCount++;
         boolean var3 = this.list.addAll(var1, var2);
         if (!var3) {
            this.modCount--;
         }

         return var3;
      } catch (Exception var4) {
         this.modCount--;
         throw var4;
      }
   }

   @Override
   public boolean removeAll(Collection<?> var1) {
      this.removeFromList(this, 0, var1, false);

      try {
         this.modCount++;
         boolean var2 = this.list.removeAll(var1);
         if (!var2) {
            this.modCount--;
         }

         return var2;
      } catch (Exception var3) {
         this.modCount--;
         throw var3;
      }
   }

   @Override
   public boolean retainAll(Collection<?> var1) {
      this.removeFromList(this, 0, var1, true);

      try {
         this.modCount++;
         boolean var2 = this.list.retainAll(var1);
         if (!var2) {
            this.modCount--;
         }

         return var2;
      } catch (Exception var3) {
         this.modCount--;
         throw var3;
      }
   }

   @Override
   public void clear() {
      this.onProposedChange(Collections.emptyList(), 0, this.size());

      try {
         this.modCount++;
         this.list.clear();
      } catch (Exception var2) {
         this.modCount--;
         throw var2;
      }
   }

   @Override
   public E get(int var1) {
      return this.list.get(var1);
   }

   @Override
   public E set(int var1, E var2) {
      this.onProposedChange(Collections.singletonList((E)var2), var1, var1 + 1);
      return this.list.set(var1, (E)var2);
   }

   @Override
   public void add(int var1, E var2) {
      this.onProposedChange(Collections.singletonList((E)var2), var1, var1);

      try {
         this.modCount++;
         this.list.add(var1, (E)var2);
      } catch (Exception var4) {
         this.modCount--;
         throw var4;
      }
   }

   @Override
   public E remove(int var1) {
      this.onProposedChange(Collections.emptyList(), var1, var1 + 1);

      try {
         this.modCount++;
         return this.list.remove(var1);
      } catch (Exception var3) {
         this.modCount--;
         throw var3;
      }
   }

   @Override
   public int indexOf(Object var1) {
      return this.list.indexOf(var1);
   }

   @Override
   public int lastIndexOf(Object var1) {
      return this.list.lastIndexOf(var1);
   }

   @Override
   public ListIterator<E> listIterator() {
      return new VetoableListDecorator.VetoableListIteratorDecorator(new VetoableListDecorator.ModCountAccessorImpl(), this.list.listIterator(), 0);
   }

   @Override
   public ListIterator<E> listIterator(int var1) {
      return new VetoableListDecorator.VetoableListIteratorDecorator(new VetoableListDecorator.ModCountAccessorImpl(), this.list.listIterator(var1), var1);
   }

   @Override
   public List<E> subList(int var1, int var2) {
      return new VetoableListDecorator.VetoableSubListDecorator(new VetoableListDecorator.ModCountAccessorImpl(), this.list.subList(var1, var2), var1);
   }

   @Override
   public String toString() {
      return this.list.toString();
   }

   @Override
   public boolean equals(Object var1) {
      return this.list.equals(var1);
   }

   @Override
   public int hashCode() {
      return this.list.hashCode();
   }

   private interface ModCountAccessor {
      int get();

      int incrementAndGet();

      int decrementAndGet();
   }

   private class ModCountAccessorImpl implements VetoableListDecorator.ModCountAccessor {
      public ModCountAccessorImpl() {
      }

      @Override
      public int get() {
         return VetoableListDecorator.this.modCount;
      }

      @Override
      public int incrementAndGet() {
         return ++VetoableListDecorator.this.modCount;
      }

      @Override
      public int decrementAndGet() {
         return --VetoableListDecorator.this.modCount;
      }
   }

   private class VetoableIteratorDecorator implements Iterator<E> {
      private final Iterator<E> it;
      private final VetoableListDecorator.ModCountAccessor modCountAccessor;
      private int modCount;
      protected final int offset;
      protected int cursor;
      protected int lastReturned;

      public VetoableIteratorDecorator(VetoableListDecorator.ModCountAccessor var2, Iterator<E> var3, int var4) {
         this.modCountAccessor = var2;
         this.modCount = var2.get();
         this.it = var3;
         this.offset = var4;
      }

      @Override
      public boolean hasNext() {
         this.checkForComodification();
         return this.it.hasNext();
      }

      @Override
      public E next() {
         this.checkForComodification();
         Object var1 = this.it.next();
         this.lastReturned = this.cursor++;
         return (E)var1;
      }

      @Override
      public void remove() {
         this.checkForComodification();
         if (this.lastReturned == -1) {
            throw new IllegalStateException();
         } else {
            VetoableListDecorator.this.onProposedChange(Collections.emptyList(), this.offset + this.lastReturned, this.offset + this.lastReturned + 1);

            try {
               this.incrementModCount();
               this.it.remove();
            } catch (Exception var2) {
               this.decrementModCount();
               throw var2;
            }

            this.lastReturned = -1;
            this.cursor--;
         }
      }

      protected void checkForComodification() {
         if (this.modCount != this.modCountAccessor.get()) {
            throw new ConcurrentModificationException();
         }
      }

      protected void incrementModCount() {
         this.modCount = this.modCountAccessor.incrementAndGet();
      }

      protected void decrementModCount() {
         this.modCount = this.modCountAccessor.decrementAndGet();
      }
   }

   private class VetoableListIteratorDecorator extends VetoableListDecorator<E>.VetoableIteratorDecorator implements ListIterator<E> {
      private final ListIterator<E> lit;

      public VetoableListIteratorDecorator(VetoableListDecorator.ModCountAccessor var2, ListIterator<E> var3, int var4) {
         super(var2, var3, var4);
         this.lit = var3;
      }

      @Override
      public boolean hasPrevious() {
         this.checkForComodification();
         return this.lit.hasPrevious();
      }

      @Override
      public E previous() {
         this.checkForComodification();
         Object var1 = this.lit.previous();
         this.lastReturned = --this.cursor;
         return (E)var1;
      }

      @Override
      public int nextIndex() {
         this.checkForComodification();
         return this.lit.nextIndex();
      }

      @Override
      public int previousIndex() {
         this.checkForComodification();
         return this.lit.previousIndex();
      }

      @Override
      public void set(E var1) {
         this.checkForComodification();
         if (this.lastReturned == -1) {
            throw new IllegalStateException();
         } else {
            VetoableListDecorator.this.onProposedChange(
               Collections.singletonList((E)var1), this.offset + this.lastReturned, this.offset + this.lastReturned + 1
            );
            this.lit.set((E)var1);
         }
      }

      @Override
      public void add(E var1) {
         this.checkForComodification();
         VetoableListDecorator.this.onProposedChange(Collections.singletonList((E)var1), this.offset + this.cursor, this.offset + this.cursor);

         try {
            this.incrementModCount();
            this.lit.add((E)var1);
         } catch (Exception var3) {
            this.decrementModCount();
            throw var3;
         }

         this.cursor++;
      }
   }

   private class VetoableSubListDecorator implements List<E> {
      private final List<E> subList;
      private final int offset;
      private final VetoableListDecorator.ModCountAccessor modCountAccessor;
      private int modCount;

      public VetoableSubListDecorator(VetoableListDecorator.ModCountAccessor var2, List<E> var3, int var4) {
         this.modCountAccessor = var2;
         this.modCount = var2.get();
         this.subList = var3;
         this.offset = var4;
      }

      @Override
      public int size() {
         this.checkForComodification();
         return this.subList.size();
      }

      @Override
      public boolean isEmpty() {
         this.checkForComodification();
         return this.subList.isEmpty();
      }

      @Override
      public boolean contains(Object var1) {
         this.checkForComodification();
         return this.subList.contains(var1);
      }

      @Override
      public Iterator<E> iterator() {
         this.checkForComodification();
         return VetoableListDecorator.this.new VetoableIteratorDecorator(
            new VetoableListDecorator.VetoableSubListDecorator.ModCountAccessorImplSub(), this.subList.iterator(), this.offset
         );
      }

      @Override
      public Object[] toArray() {
         this.checkForComodification();
         return this.subList.toArray();
      }

      @Override
      public <T> T[] toArray(T[] var1) {
         this.checkForComodification();
         return (T[])this.subList.toArray(var1);
      }

      @Override
      public boolean add(E var1) {
         this.checkForComodification();
         VetoableListDecorator.this.onProposedChange(Collections.singletonList((E)var1), this.offset + this.size(), this.offset + this.size());

         try {
            this.incrementModCount();
            this.subList.add((E)var1);
            return true;
         } catch (Exception var3) {
            this.decrementModCount();
            throw var3;
         }
      }

      @Override
      public boolean remove(Object var1) {
         this.checkForComodification();
         int var2 = this.indexOf(var1);
         if (var2 != -1) {
            this.remove(var2);
            return true;
         } else {
            return false;
         }
      }

      @Override
      public boolean containsAll(Collection<?> var1) {
         this.checkForComodification();
         return this.subList.containsAll(var1);
      }

      @Override
      public boolean addAll(Collection<? extends E> var1) {
         this.checkForComodification();
         VetoableListDecorator.this.onProposedChange(Collections.unmodifiableList(new ArrayList<>(var1)), this.offset + this.size(), this.offset + this.size());

         try {
            this.incrementModCount();
            boolean var2 = this.subList.addAll(var1);
            if (!var2) {
               this.decrementModCount();
            }

            return var2;
         } catch (Exception var3) {
            this.decrementModCount();
            throw var3;
         }
      }

      @Override
      public boolean addAll(int var1, Collection<? extends E> var2) {
         this.checkForComodification();
         VetoableListDecorator.this.onProposedChange(Collections.unmodifiableList(new ArrayList<>(var2)), this.offset + var1, this.offset + var1);

         try {
            this.incrementModCount();
            boolean var3 = this.subList.addAll(var1, var2);
            if (!var3) {
               this.decrementModCount();
            }

            return var3;
         } catch (Exception var4) {
            this.decrementModCount();
            throw var4;
         }
      }

      @Override
      public boolean removeAll(Collection<?> var1) {
         this.checkForComodification();
         VetoableListDecorator.this.removeFromList(this, this.offset, var1, false);

         try {
            this.incrementModCount();
            boolean var2 = this.subList.removeAll(var1);
            if (!var2) {
               this.decrementModCount();
            }

            return var2;
         } catch (Exception var3) {
            this.decrementModCount();
            throw var3;
         }
      }

      @Override
      public boolean retainAll(Collection<?> var1) {
         this.checkForComodification();
         VetoableListDecorator.this.removeFromList(this, this.offset, var1, true);

         try {
            this.incrementModCount();
            boolean var2 = this.subList.retainAll(var1);
            if (!var2) {
               this.decrementModCount();
            }

            return var2;
         } catch (Exception var3) {
            this.decrementModCount();
            throw var3;
         }
      }

      @Override
      public void clear() {
         this.checkForComodification();
         VetoableListDecorator.this.onProposedChange(Collections.emptyList(), this.offset, this.offset + this.size());

         try {
            this.incrementModCount();
            this.subList.clear();
         } catch (Exception var2) {
            this.decrementModCount();
            throw var2;
         }
      }

      @Override
      public E get(int var1) {
         this.checkForComodification();
         return this.subList.get(var1);
      }

      @Override
      public E set(int var1, E var2) {
         this.checkForComodification();
         VetoableListDecorator.this.onProposedChange(Collections.singletonList((E)var2), this.offset + var1, this.offset + var1 + 1);
         return this.subList.set(var1, (E)var2);
      }

      @Override
      public void add(int var1, E var2) {
         this.checkForComodification();
         VetoableListDecorator.this.onProposedChange(Collections.singletonList((E)var2), this.offset + var1, this.offset + var1);

         try {
            this.incrementModCount();
            this.subList.add(var1, (E)var2);
         } catch (Exception var4) {
            this.decrementModCount();
            throw var4;
         }
      }

      @Override
      public E remove(int var1) {
         this.checkForComodification();
         VetoableListDecorator.this.onProposedChange(Collections.emptyList(), this.offset + var1, this.offset + var1 + 1);

         try {
            this.incrementModCount();
            return this.subList.remove(var1);
         } catch (Exception var3) {
            this.decrementModCount();
            throw var3;
         }
      }

      @Override
      public int indexOf(Object var1) {
         this.checkForComodification();
         return this.subList.indexOf(var1);
      }

      @Override
      public int lastIndexOf(Object var1) {
         this.checkForComodification();
         return this.subList.lastIndexOf(var1);
      }

      @Override
      public ListIterator<E> listIterator() {
         this.checkForComodification();
         return VetoableListDecorator.this.new VetoableListIteratorDecorator(
            new VetoableListDecorator.VetoableSubListDecorator.ModCountAccessorImplSub(), this.subList.listIterator(), this.offset
         );
      }

      @Override
      public ListIterator<E> listIterator(int var1) {
         this.checkForComodification();
         return VetoableListDecorator.this.new VetoableListIteratorDecorator(
            new VetoableListDecorator.VetoableSubListDecorator.ModCountAccessorImplSub(), this.subList.listIterator(var1), this.offset + var1
         );
      }

      @Override
      public List<E> subList(int var1, int var2) {
         this.checkForComodification();
         return VetoableListDecorator.this.new VetoableSubListDecorator(
            new VetoableListDecorator.VetoableSubListDecorator.ModCountAccessorImplSub(), this.subList.subList(var1, var2), this.offset + var1
         );
      }

      @Override
      public String toString() {
         this.checkForComodification();
         return this.subList.toString();
      }

      @Override
      public boolean equals(Object var1) {
         this.checkForComodification();
         return this.subList.equals(var1);
      }

      @Override
      public int hashCode() {
         this.checkForComodification();
         return this.subList.hashCode();
      }

      private void checkForComodification() {
         if (this.modCount != this.modCountAccessor.get()) {
            throw new ConcurrentModificationException();
         }
      }

      private void incrementModCount() {
         this.modCount = this.modCountAccessor.incrementAndGet();
      }

      private void decrementModCount() {
         this.modCount = this.modCountAccessor.decrementAndGet();
      }

      private class ModCountAccessorImplSub implements VetoableListDecorator.ModCountAccessor {
         @Override
         public int get() {
            return VetoableSubListDecorator.this.modCount;
         }

         @Override
         public int incrementAndGet() {
            return VetoableSubListDecorator.this.modCount = VetoableSubListDecorator.this.modCountAccessor.incrementAndGet();
         }

         @Override
         public int decrementAndGet() {
            return VetoableSubListDecorator.this.modCount = VetoableSubListDecorator.this.modCountAccessor.decrementAndGet();
         }
      }
   }
}
