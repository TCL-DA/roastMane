package com.cropster.roastingintelligence.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.cropster.roastingintelligence.model.Channel;

public class ChannelComparator implements Comparator<Channel> {
   private List<String> channelOrder;

   public ChannelComparator(String[] channelOrder) {
      this.channelOrder = Arrays.asList(channelOrder);
   }

   public int compare(Channel o1, Channel o2) {
      int i1 = this.channelOrder.indexOf(o1.getName());
      int i2 = this.channelOrder.indexOf(o2.getName());
      if (i1 >= 0 && i2 >= 0) {
         return i1 - i2;
      } else if (i1 < 0 && i2 < 0) {
         return 0;
      } else {
         return i1 < 0 ? 1 : -1;
      }
   }
}
