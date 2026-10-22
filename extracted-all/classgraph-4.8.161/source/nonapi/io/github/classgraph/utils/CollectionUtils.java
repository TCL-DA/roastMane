package nonapi.io.github.classgraph.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class CollectionUtils {
   private CollectionUtils() {
   }

   public static <T extends Comparable<? super T>> void sortIfNotEmpty(List<T> list) {
      if (list.size() > 1) {
         Collections.sort(list);
      }
   }

   public static <T> void sortIfNotEmpty(List<T> list, Comparator<? super T> comparator) {
      if (list.size() > 1) {
         Collections.sort(list, comparator);
      }
   }

   public static <T extends Comparable<T>> List<T> sortCopy(Collection<T> elts) {
      List<T> sortedCopy = new ArrayList<>(elts);
      if (sortedCopy.size() > 1) {
         Collections.sort(sortedCopy);
      }

      return sortedCopy;
   }
}
