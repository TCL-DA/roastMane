package org.eclipse.jetty.util.resource;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;

public class ResourceCollators {
   private static final Comparator<? super Resource> BY_NAME_ASCENDING = new Comparator<Resource>() {
      private final Collator collator = Collator.getInstance(Locale.ENGLISH);

      public int compare(Resource o1, Resource o2) {
         return this.collator.compare(Objects.requireNonNullElse(o1.getName(), ""), Objects.requireNonNullElse(o2.getName(), ""));
      }
   };
   private static final Comparator<? super Resource> BY_FILENAME_ASCENDING = new Comparator<Resource>() {
      private final Collator collator = Collator.getInstance(Locale.ENGLISH);

      public int compare(Resource o1, Resource o2) {
         return this.collator.compare(o1.getFileName(), o2.getFileName());
      }
   };
   private static final Comparator<? super Resource> BY_NAME_DESCENDING = Collections.reverseOrder(BY_NAME_ASCENDING);
   private static final Comparator<? super Resource> BY_FILENAME_DESCENDING = Collections.reverseOrder(BY_FILENAME_ASCENDING);
   private static final Comparator<? super Resource> BY_LAST_MODIFIED_ASCENDING = Comparator.comparing(Resource::lastModified);
   private static final Comparator<? super Resource> BY_LAST_MODIFIED_DESCENDING = Collections.reverseOrder(BY_LAST_MODIFIED_ASCENDING);
   private static final Comparator<? super Resource> BY_SIZE_ASCENDING = Comparator.comparingLong(Resource::length);
   private static final Comparator<? super Resource> BY_SIZE_DESCENDING = Collections.reverseOrder(BY_SIZE_ASCENDING);

   public static Comparator<? super Resource> byLastModified(boolean sortOrderAscending) {
      return sortOrderAscending ? BY_LAST_MODIFIED_ASCENDING : BY_LAST_MODIFIED_DESCENDING;
   }

   public static Comparator<? super Resource> byName(boolean sortOrderAscending) {
      return sortOrderAscending ? BY_NAME_ASCENDING : BY_NAME_DESCENDING;
   }

   public static Comparator<? super Resource> byFileName(boolean sortOrderAscending) {
      return sortOrderAscending ? BY_FILENAME_ASCENDING : BY_FILENAME_DESCENDING;
   }

   public static Comparator<? super Resource> bySize(boolean sortOrderAscending) {
      return sortOrderAscending ? BY_SIZE_ASCENDING : BY_SIZE_DESCENDING;
   }
}
