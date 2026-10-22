package com.sun.javafx.css;

import com.sun.javafx.collections.SetListenerHelper;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

abstract class BitSet<T> implements ObservableSet<T> {
   private static final long[] EMPTY_SET = new long[0];
   private long[] bits = EMPTY_SET;
   private SetListenerHelper<T> listenerHelper;

   protected BitSet() {
   }

   public int size() {
      int var1 = 0;
      if (this.bits.length > 0) {
         for (int var2 = 0; var2 < this.bits.length; var2++) {
            long var3 = this.bits[var2];
            if (var3 != 0L) {
               var1 += Long.bitCount(var3);
            }
         }
      }

      return var1;
   }

   public boolean isEmpty() {
      if (this.bits.length > 0) {
         for (int var1 = 0; var1 < this.bits.length; var1++) {
            long var2 = this.bits[var1];
            if (var2 != 0L) {
               return false;
            }
         }
      }

      return true;
   }

   public Iterator<T> iterator() {
      return new Iterator<T>() {
         int next = -1;
         int element = 0;
         int index = -1;

         @Override
         public boolean hasNext() {
            if (BitSet.this.bits != null && BitSet.this.bits.length != 0) {
               boolean var1 = false;

               do {
                  if (++this.next >= 64) {
                     if (++this.element >= BitSet.this.bits.length) {
                        return false;
                     }

                     this.next = 0;
                  }

                  long var2 = 1L << this.next;
                  var1 = (var2 & BitSet.this.bits[this.element]) == var2;
               } while (!var1);

               if (var1) {
                  this.index = 64 * this.element + this.next;
               }

               return var1;
            } else {
               return false;
            }
         }

         @Override
         public T next() {
            try {
               return (T)BitSet.this.getT(this.index);
            } catch (IndexOutOfBoundsException var2) {
               throw new NoSuchElementException("[" + this.element + "][" + this.next + "]");
            }
         }

         @Override
         public void remove() {
            try {
               Object var1 = BitSet.this.getT(this.index);
               BitSet.this.remove(var1);
            } catch (IndexOutOfBoundsException var2) {
               throw new NoSuchElementException("[" + this.element + "][" + this.next + "]");
            }
         }
      };
   }

   public boolean add(T var1) {
      if (var1 == null) {
         return false;
      } else {
         int var2 = this.getIndex((T)var1) / 64;
         long var3 = 1L << this.getIndex((T)var1) % 64;
         if (var2 >= this.bits.length) {
            long[] var5 = new long[var2 + 1];
            System.arraycopy(this.bits, 0, var5, 0, this.bits.length);
            this.bits = var5;
         }

         long var8 = this.bits[var2];
         this.bits[var2] = var8 | var3;
         boolean var7 = this.bits[var2] != var8;
         if (var7 && SetListenerHelper.hasListeners(this.listenerHelper)) {
            this.notifyObservers((T)var1, false);
         }

         return var7;
      }
   }

   public boolean remove(Object var1) {
      if (var1 == null) {
         return false;
      } else {
         Object var2 = this.cast(var1);
         int var3 = this.getIndex((T)var2) / 64;
         long var4 = 1L << this.getIndex((T)var2) % 64;
         if (var3 >= this.bits.length) {
            return false;
         } else {
            long var6 = this.bits[var3];
            this.bits[var3] = var6 & ~var4;
            boolean var8 = this.bits[var3] != var6;
            if (var8) {
               if (SetListenerHelper.hasListeners(this.listenerHelper)) {
                  this.notifyObservers((T)var2, true);
               }

               boolean var9 = true;

               for (int var10 = 0; var10 < this.bits.length && var9; var10++) {
                  var9 &= this.bits[var10] == 0L;
               }

               if (var9) {
                  this.bits = EMPTY_SET;
               }
            }

            return var8;
         }
      }
   }

   public boolean contains(Object var1) {
      if (var1 == null) {
         return false;
      } else {
         Object var2 = this.cast(var1);
         int var3 = this.getIndex((T)var2) / 64;
         long var4 = 1L << this.getIndex((T)var2) % 64;
         return var3 < this.bits.length && (this.bits[var3] & var4) == var4;
      }
   }

   public boolean containsAll(Collection<?> var1) {
      if (var1 != null && this.getClass() == var1.getClass()) {
         BitSet var2 = (BitSet)var1;
         if (this.bits.length == 0 && var2.bits.length == 0) {
            return true;
         } else if (this.bits.length < var2.bits.length) {
            return false;
         } else {
            int var3 = 0;

            for (int var4 = var2.bits.length; var3 < var4; var3++) {
               if ((this.bits[var3] & var2.bits[var3]) != var2.bits[var3]) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public boolean addAll(Collection<? extends T> var1) {
      if (var1 != null && this.getClass() == var1.getClass()) {
         boolean var2 = false;
         BitSet var3 = (BitSet)var1;
         long[] var4 = this.bits;
         long[] var5 = var3.bits;
         int var6 = var4.length;
         int var7 = var5.length;
         int var8 = var6 < var7 ? var7 : var6;
         long[] var9 = var8 > 0 ? new long[var8] : EMPTY_SET;

         for (int var10 = 0; var10 < var8; var10++) {
            if (var10 < var4.length && var10 < var5.length) {
               var9[var10] = var4[var10] | var5[var10];
               var2 |= var9[var10] != var4[var10];
            } else if (var10 < var4.length) {
               var9[var10] = var4[var10];
               var2 |= false;
            } else {
               var9[var10] = var5[var10];
               var2 = true;
            }
         }

         if (var2) {
            if (SetListenerHelper.hasListeners(this.listenerHelper)) {
               for (int var17 = 0; var17 < var8; var17++) {
                  long var11 = 0L;
                  if (var17 < var4.length && var17 < var5.length) {
                     var11 = ~var4[var17] & var5[var17];
                  } else {
                     if (var17 < var4.length) {
                        continue;
                     }

                     var11 = var5[var17];
                  }

                  for (int var13 = 0; var13 < 64; var13++) {
                     long var14 = 1L << var13;
                     if ((var14 & var11) == var14) {
                        Object var16 = this.getT(var17 * 64 + var13);
                        this.notifyObservers((T)var16, false);
                     }
                  }
               }
            }

            this.bits = var9;
         }

         return var2;
      } else {
         return false;
      }
   }

   public boolean retainAll(Collection<?> var1) {
      if (var1 != null && this.getClass() == var1.getClass()) {
         boolean var2 = false;
         BitSet var3 = (BitSet)var1;
         long[] var4 = this.bits;
         long[] var5 = var3.bits;
         int var6 = var4.length;
         int var7 = var5.length;
         int var8 = var6 < var7 ? var6 : var7;
         long[] var9 = var8 > 0 ? new long[var8] : EMPTY_SET;
         var2 |= var4.length > var8;
         boolean var10 = true;

         for (int var11 = 0; var11 < var8; var11++) {
            var9[var11] = var4[var11] & var5[var11];
            var2 |= var9[var11] != var4[var11];
            var10 &= var9[var11] == 0L;
         }

         if (var2) {
            if (SetListenerHelper.hasListeners(this.listenerHelper)) {
               for (int var19 = 0; var19 < var4.length; var19++) {
                  long var12 = 0L;
                  if (var19 < var5.length) {
                     var12 = var4[var19] & ~var5[var19];
                  } else {
                     var12 = var4[var19];
                  }

                  for (int var14 = 0; var14 < 64; var14++) {
                     long var15 = 1L << var14;
                     if ((var15 & var12) == var15) {
                        Object var17 = this.getT(var19 * 64 + var14);
                        this.notifyObservers((T)var17, true);
                     }
                  }
               }
            }

            this.bits = !var10 ? var9 : EMPTY_SET;
         }

         return var2;
      } else {
         this.clear();
         return true;
      }
   }

   public boolean removeAll(Collection<?> var1) {
      if (var1 != null && this.getClass() == var1.getClass()) {
         boolean var2 = false;
         BitSet var3 = (BitSet)var1;
         long[] var4 = this.bits;
         long[] var5 = var3.bits;
         int var6 = var4.length;
         int var7 = var5.length;
         int var8 = var6 < var7 ? var6 : var7;
         long[] var9 = var8 > 0 ? new long[var8] : EMPTY_SET;
         boolean var10 = true;

         for (int var11 = 0; var11 < var8; var11++) {
            var9[var11] = var4[var11] & ~var5[var11];
            var2 |= var9[var11] != var4[var11];
            var10 &= var9[var11] == 0L;
         }

         if (var2) {
            if (SetListenerHelper.hasListeners(this.listenerHelper)) {
               for (int var18 = 0; var18 < var8; var18++) {
                  long var12 = var4[var18] & var5[var18];

                  for (int var14 = 0; var14 < 64; var14++) {
                     long var15 = 1L << var14;
                     if ((var15 & var12) == var15) {
                        Object var17 = this.getT(var18 * 64 + var14);
                        this.notifyObservers((T)var17, true);
                     }
                  }
               }
            }

            this.bits = !var10 ? var9 : EMPTY_SET;
         }

         return var2;
      } else {
         return false;
      }
   }

   public void clear() {
      for (int var1 = 0; var1 < this.bits.length; var1++) {
         long var2 = this.bits[var1];

         for (int var4 = 0; var4 < 64; var4++) {
            long var5 = 1L << var4;
            if ((var5 & var2) == var5) {
               Object var7 = this.getT(var1 * 64 + var4);
               this.notifyObservers((T)var7, true);
            }
         }
      }

      this.bits = EMPTY_SET;
   }

   @Override
   public int hashCode() {
      int var1 = 7;
      if (this.bits.length > 0) {
         for (int var2 = 0; var2 < this.bits.length; var2++) {
            long var3 = this.bits[var2];
            var1 = 71 * var1 + (int)(var3 ^ var3 >>> 32);
         }
      }

      return var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         BitSet var2 = (BitSet)var1;
         int var3 = this.bits != null ? this.bits.length : 0;
         int var4 = var2.bits != null ? var2.bits.length : 0;
         if (var3 != var4) {
            return false;
         } else {
            for (int var5 = 0; var5 < var3; var5++) {
               long var6 = this.bits[var5];
               long var8 = var2.bits[var5];
               if (var6 != var8) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   protected abstract T getT(int var1);

   protected abstract int getIndex(T var1);

   protected abstract T cast(Object var1);

   protected long[] getBits() {
      return this.bits;
   }

   public void addListener(SetChangeListener<? super T> var1) {
      if (var1 != null) {
         this.listenerHelper = SetListenerHelper.addListener(this.listenerHelper, var1);
      }
   }

   public void removeListener(SetChangeListener<? super T> var1) {
      if (var1 != null) {
         SetListenerHelper.removeListener(this.listenerHelper, var1);
      }
   }

   public void addListener(InvalidationListener var1) {
      if (var1 != null) {
         this.listenerHelper = SetListenerHelper.addListener(this.listenerHelper, var1);
      }
   }

   public void removeListener(InvalidationListener var1) {
      if (var1 != null) {
         SetListenerHelper.removeListener(this.listenerHelper, var1);
      }
   }

   private void notifyObservers(T var1, boolean var2) {
      if (var1 != null && SetListenerHelper.hasListeners(this.listenerHelper)) {
         BitSet.Change var3 = new BitSet.Change(var1, var2);
         SetListenerHelper.fireValueChangedEvent(this.listenerHelper, var3);
      }
   }

   private class Change extends javafx.collections.SetChangeListener.Change<T> {
      private static final boolean ELEMENT_ADDED = false;
      private static final boolean ELEMENT_REMOVED = true;
      private final T element;
      private final boolean removed;

      public Change(T var2, boolean var3) {
         super(FXCollections.unmodifiableObservableSet(BitSet.this));
         this.element = (T)var2;
         this.removed = var3;
      }

      public boolean wasAdded() {
         return !this.removed;
      }

      public boolean wasRemoved() {
         return this.removed;
      }

      public T getElementAdded() {
         return this.removed ? null : this.element;
      }

      public T getElementRemoved() {
         return this.removed ? this.element : null;
      }
   }
}
