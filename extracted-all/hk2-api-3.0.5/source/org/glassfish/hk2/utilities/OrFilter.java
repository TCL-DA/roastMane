package org.glassfish.hk2.utilities;

import java.util.ArrayList;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.IndexedFilter;
import org.glassfish.hk2.utilities.general.GeneralUtilities;

public class OrFilter implements Filter {
   private final ArrayList<Filter> allFilters;

   public OrFilter(Filter... filters) {
      this.allFilters = new ArrayList<>(filters.length);

      for (Filter f : filters) {
         if (f != null) {
            this.allFilters.add(f);
         }
      }
   }

   @Override
   public boolean matches(Descriptor d) {
      for (Filter filter : this.allFilters) {
         if (filter instanceof IndexedFilter) {
            IndexedFilter iFilter = (IndexedFilter)filter;
            String name = iFilter.getName();
            if (name != null && !GeneralUtilities.safeEquals(name, d.getName())) {
               continue;
            }

            String contract = iFilter.getAdvertisedContract();
            if (contract != null && !d.getAdvertisedContracts().contains(contract)) {
               continue;
            }
         }

         if (filter.matches(d)) {
            return true;
         }
      }

      return false;
   }
}
