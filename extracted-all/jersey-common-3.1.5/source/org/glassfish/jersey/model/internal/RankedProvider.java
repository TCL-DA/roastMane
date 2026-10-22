package org.glassfish.jersey.model.internal;

import java.lang.reflect.Type;
import java.util.Set;
import org.glassfish.jersey.JerseyPriorities;

public class RankedProvider<T> {
   private final T provider;
   private final int rank;
   private final Set<Type> contractTypes;

   public RankedProvider(T provider) {
      this.provider = provider;
      this.rank = this.computeRank(provider, -1);
      this.contractTypes = null;
   }

   public RankedProvider(T provider, int rank) {
      this(provider, rank, null);
   }

   public RankedProvider(T provider, int rank, Set<Type> contracts) {
      this.provider = provider;
      this.rank = this.computeRank(provider, rank);
      this.contractTypes = contracts;
   }

   private int computeRank(T provider, int rank) {
      if (rank > 0) {
         return rank;
      } else {
         Class<?> clazz = provider.getClass();

         while (clazz.isSynthetic()) {
            clazz = clazz.getSuperclass();
         }

         return JerseyPriorities.getPriorityValue(clazz, 5000);
      }
   }

   public T getProvider() {
      return this.provider;
   }

   public int getRank() {
      return this.rank;
   }

   public Set<Type> getContractTypes() {
      return this.contractTypes;
   }

   @Override
   public String toString() {
      return this.provider.getClass().getName();
   }
}
