package org.glassfish.hk2.internal;

import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.IndexedFilter;

public class IndexedFilterImpl implements IndexedFilter {
   private final String contract;
   private final String name;

   public IndexedFilterImpl(String contract, String name) {
      this.contract = contract;
      this.name = name;
   }

   @Override
   public boolean matches(Descriptor d) {
      return true;
   }

   @Override
   public String getAdvertisedContract() {
      return this.contract;
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public String toString() {
      return "IndexedFilterImpl(" + this.contract + "," + this.name + "," + System.identityHashCode(this) + ")";
   }
}
