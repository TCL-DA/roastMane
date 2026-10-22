package impl.org.controlsfx.collections;

import java.util.Arrays;
import java.util.List;

class ChangeHelper {
   static String addRemoveChangeToString(int from, int to, List<?> list, List<?> removed) {
      StringBuilder b = new StringBuilder();
      if (removed.isEmpty()) {
         b.append(list.subList(from, to));
         b.append(" addition at ").append(from);
      } else {
         b.append(removed);
         if (from == to) {
            b.append(" removal at ").append(from);
         } else {
            b.append(" replaced by ");
            b.append(list.subList(from, to));
            b.append(" at ").append(from);
         }
      }

      return b.toString();
   }

   static String permChangeToString(int[] permutation) {
      return "permutation by " + Arrays.toString(permutation);
   }

   static String updateChangeToString(int from, int to) {
      return "update at range [" + from + ", " + to + ")";
   }
}
