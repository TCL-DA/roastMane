package com.nimbusds.jose;

import java.util.Collection;
import java.util.LinkedHashSet;
import net.jcip.annotations.Immutable;

@Immutable
class AlgorithmFamily<T extends Algorithm> extends LinkedHashSet<T> {
   private static final long serialVersionUID = 1L;

   public AlgorithmFamily(T... algs) {
      for (T alg : algs) {
         super.add(alg);
      }
   }

   public boolean add(T alg) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean addAll(Collection<? extends T> algs) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean remove(Object o) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean removeAll(Collection<?> c) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainAll(Collection<?> c) {
      throw new UnsupportedOperationException();
   }
}
