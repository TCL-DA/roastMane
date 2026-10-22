package org.jvnet.hk2.internal;

import java.io.Serializable;
import java.util.Comparator;
import org.glassfish.hk2.api.Descriptor;

public class DescriptorComparator implements Comparator<Descriptor>, Serializable {
   private static final long serialVersionUID = 4454509124508404602L;

   public int compare(Descriptor o1, Descriptor o2) {
      int o1Ranking = o1.getRanking();
      int o2Ranking = o2.getRanking();
      if (o1Ranking < o2Ranking) {
         return 1;
      } else if (o1Ranking > o2Ranking) {
         return -1;
      } else {
         long o1LocatorId = o1.getLocatorId();
         long o2LocatorId = o2.getLocatorId();
         if (o1LocatorId < o2LocatorId) {
            return 1;
         } else if (o1LocatorId > o2LocatorId) {
            return -1;
         } else {
            long o1ServiceId = o1.getServiceId();
            long o2ServiceId = o2.getServiceId();
            if (o1ServiceId > o2ServiceId) {
               return 1;
            } else {
               return o1ServiceId < o2ServiceId ? -1 : 0;
            }
         }
      }
   }
}
