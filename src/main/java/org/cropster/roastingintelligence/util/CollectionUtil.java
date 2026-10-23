package org.cropster.roastingintelligence.util;

import com.cropster.util.Collect;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import org.cropster.roastingintelligence.model.Identifiable;

public final class CollectionUtil {
   private CollectionUtil() {
   }

   public static Map<String, String> toMap(String[][] array) {
      Map<String, String> map = new LinkedHashMap<>();

      for (String[] tuple : array) {
         map.put(tuple[0], tuple[1]);
      }

      return map;
   }

   public static <T extends Identifiable> Map<Long, T> toMap(Collection<T> collection) {
      Map<Long, T> map = new LinkedHashMap<>();

      for (T element : collection) {
         map.put(element.getId(), element);
      }

      return map;
   }

   public static Long getProximateKey(SortedMap<Long, ?> map, Long key, long maxDeviation) {
      return Collect.proximate(map, key, maxDeviation);
   }

   public static <T> T getProximateValue(SortedMap<Long, T> map, Long key, long maxDeviation) {
      Long proximateKey = getProximateKey(map, key, maxDeviation);
      return proximateKey != null ? map.get(proximateKey) : null;
   }

   public static Long nextKey(SortedMap<Long, ?> map, Long referenceKey) {
      SortedMap<Long, ?> tailMap = map.tailMap(referenceKey);
      return tailMap.isEmpty() ? null : tailMap.firstKey();
   }

   public static <T> T nextValue(SortedMap<Long, T> map, Long referenceKey) {
      Long nextKey = nextKey(map, referenceKey);
      return nextKey == null ? null : map.get(nextKey);
   }
}
