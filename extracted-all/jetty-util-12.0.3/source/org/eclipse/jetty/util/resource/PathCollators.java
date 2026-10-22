package org.eclipse.jetty.util.resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class PathCollators {
   private static Comparator<? super Path> BY_NAME_ASCENDING = new Comparator<Path>() {
      private final Collator collator = Collator.getInstance(Locale.ENGLISH);

      public int compare(Path o1, Path o2) {
         return this.collator.compare(o1.getFileName().toString(), o2.getFileName().toString());
      }
   };
   private static Comparator<? super Path> BY_NAME_DESCENDING = Collections.reverseOrder(BY_NAME_ASCENDING);
   private static Comparator<? super Path> BY_LAST_MODIFIED_ASCENDING = Comparator.comparing(PathCollators::getLastModifiedTime);
   private static Comparator<? super Path> BY_LAST_MODIFIED_DESCENDING = Collections.reverseOrder(BY_LAST_MODIFIED_ASCENDING);
   private static Comparator<? super Path> BY_SIZE_ASCENDING = Comparator.comparingLong(PathCollators::size);
   private static Comparator<? super Path> BY_SIZE_DESCENDING = Collections.reverseOrder(BY_SIZE_ASCENDING);

   public static Comparator<? super Path> byLastModified(boolean sortOrderAscending) {
      return sortOrderAscending ? BY_LAST_MODIFIED_ASCENDING : BY_LAST_MODIFIED_DESCENDING;
   }

   public static Comparator<? super Path> byName(boolean sortOrderAscending) {
      return sortOrderAscending ? BY_NAME_ASCENDING : BY_NAME_DESCENDING;
   }

   public static Comparator<? super Path> bySize(boolean sortOrderAscending) {
      return sortOrderAscending ? BY_SIZE_ASCENDING : BY_SIZE_DESCENDING;
   }

   private static FileTime getLastModifiedTime(Path path) {
      try {
         return Files.getLastModifiedTime(path);
      } catch (IOException var2) {
         return FileTime.fromMillis(0L);
      }
   }

   private static long size(Path path) {
      try {
         return Files.size(path);
      } catch (IOException var2) {
         return 0L;
      }
   }
}
