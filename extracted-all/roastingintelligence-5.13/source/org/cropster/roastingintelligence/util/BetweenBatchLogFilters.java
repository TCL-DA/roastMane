package org.cropster.roastingintelligence.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

public final class BetweenBatchLogFilters implements FileFilter, FilenameFilter {
   private static final BetweenBatchLogFilters FILTER = new BetweenBatchLogFilters();
   public static final FileFilter FILE_FILTER = FILTER;
   public static final FilenameFilter FILENAME_FILTER = FILTER;
   public static final String FILE_PATTERN = "betweenbatchlog-\\d{8}_\\d{6}_\\d{3}\\.json";

   private BetweenBatchLogFilters() {
   }

   @Override
   public boolean accept(File pathname) {
      return pathname.isFile() && !pathname.isHidden() && pathname.getName().matches("betweenbatchlog-\\d{8}_\\d{6}_\\d{3}\\.json");
   }

   @Override
   public boolean accept(File dir, String name) {
      return name.matches("betweenbatchlog-\\d{8}_\\d{6}_\\d{3}\\.json");
   }
}
