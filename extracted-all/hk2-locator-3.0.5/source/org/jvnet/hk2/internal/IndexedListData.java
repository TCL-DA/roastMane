package org.jvnet.hk2.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ListIterator;
import org.glassfish.hk2.api.Descriptor;

public class IndexedListData {
   private final ArrayList<SystemDescriptor<?>> unsortedList = new ArrayList<>();
   private volatile boolean sorted = true;

   public Collection<SystemDescriptor<?>> getSortedList() {
      if (this.sorted) {
         return this.unsortedList;
      } else {
         synchronized (this) {
            if (this.sorted) {
               return this.unsortedList;
            } else if (this.unsortedList.size() <= 1) {
               this.sorted = true;
               return this.unsortedList;
            } else {
               Collections.sort(this.unsortedList, ServiceLocatorImpl.DESCRIPTOR_COMPARATOR);
               this.sorted = true;
               return this.unsortedList;
            }
         }
      }
   }

   public synchronized void addDescriptor(SystemDescriptor<?> descriptor) {
      this.unsortedList.add(descriptor);
      this.sorted = this.unsortedList.size() <= 1;
      descriptor.addList(this);
   }

   public synchronized void removeDescriptor(SystemDescriptor<?> descriptor) {
      ListIterator<SystemDescriptor<?>> iterator = this.unsortedList.listIterator();

      while (iterator.hasNext()) {
         SystemDescriptor<?> candidate = iterator.next();
         if (ServiceLocatorImpl.DESCRIPTOR_COMPARATOR.compare((Descriptor)descriptor, (Descriptor)candidate) == 0) {
            iterator.remove();
            break;
         }
      }

      this.sorted = this.unsortedList.size() <= 1;
      descriptor.removeList(this);
   }

   public synchronized boolean isEmpty() {
      return this.unsortedList.isEmpty();
   }

   public synchronized void unSort() {
      if (this.unsortedList.size() > 1) {
         this.sorted = false;
      }
   }

   public synchronized void clear() {
      for (SystemDescriptor<?> descriptor : this.unsortedList) {
         descriptor.removeList(this);
      }

      this.unsortedList.clear();
   }

   public synchronized int size() {
      return this.unsortedList.size();
   }
}
