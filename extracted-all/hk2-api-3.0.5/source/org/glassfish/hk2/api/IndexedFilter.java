package org.glassfish.hk2.api;

public interface IndexedFilter extends Filter {
   String getAdvertisedContract();

   String getName();
}
