package io.github.classgraph;

import java.util.Collection;

public class PackageInfoList extends MappableInfoList<PackageInfo> {
   private static final long serialVersionUID = 1L;
   static final PackageInfoList EMPTY_LIST = new PackageInfoList() {
      private static final long serialVersionUID = 1L;

      public boolean add(PackageInfo e) {
         throw new IllegalArgumentException("List is immutable");
      }

      public void add(int index, PackageInfo element) {
         throw new IllegalArgumentException("List is immutable");
      }

      @Override
      public boolean remove(Object o) {
         throw new IllegalArgumentException("List is immutable");
      }

      public PackageInfo remove(int index) {
         throw new IllegalArgumentException("List is immutable");
      }

      @Override
      public boolean addAll(Collection<? extends PackageInfo> c) {
         throw new IllegalArgumentException("List is immutable");
      }

      @Override
      public boolean addAll(int index, Collection<? extends PackageInfo> c) {
         throw new IllegalArgumentException("List is immutable");
      }

      @Override
      public boolean removeAll(Collection<?> c) {
         throw new IllegalArgumentException("List is immutable");
      }

      @Override
      public boolean retainAll(Collection<?> c) {
         throw new IllegalArgumentException("List is immutable");
      }

      @Override
      public void clear() {
         throw new IllegalArgumentException("List is immutable");
      }

      public PackageInfo set(int index, PackageInfo element) {
         throw new IllegalArgumentException("List is immutable");
      }
   };

   PackageInfoList() {
   }

   PackageInfoList(int sizeHint) {
      super(sizeHint);
   }

   PackageInfoList(Collection<PackageInfo> packageInfoCollection) {
      super(packageInfoCollection);
   }

   public PackageInfoList filter(PackageInfoList.PackageInfoFilter filter) {
      PackageInfoList packageInfoFiltered = new PackageInfoList();

      for (PackageInfo resource : this) {
         if (filter.accept(resource)) {
            packageInfoFiltered.add(resource);
         }
      }

      return packageInfoFiltered;
   }

   @FunctionalInterface
   public interface PackageInfoFilter {
      boolean accept(PackageInfo var1);
   }
}
