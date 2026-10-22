package org.jfree.chart.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CloneUtils {
   public static List<?> cloneList(List<?> source) {
      Args.nullNotPermitted(source, "source");
      List result = new ArrayList();

      for (Object obj : source) {
         if (obj != null) {
            try {
               result.add(ObjectUtils.clone(obj));
            } catch (CloneNotSupportedException var5) {
               throw new RuntimeException(var5);
            }
         } else {
            result.add(null);
         }
      }

      return result;
   }

   public static Map cloneMapValues(Map source) {
      Args.nullNotPermitted(source, "source");
      Map result = new HashMap();

      for (Object key : source.keySet()) {
         Object value = source.get(key);
         if (value != null) {
            try {
               result.put(key, ObjectUtils.clone(value));
            } catch (CloneNotSupportedException var6) {
               throw new RuntimeException(var6);
            }
         } else {
            result.put(key, null);
         }
      }

      return result;
   }
}
