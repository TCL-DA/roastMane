package org.apache.commons.io.filefilter;

import java.io.File;
import java.io.Serializable;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AndFileFilter extends AbstractFileFilter implements ConditionalFileFilter, Serializable {
   private static final long serialVersionUID = 7215974688563965257L;
   private final List<IOFileFilter> fileFilters;

   public AndFileFilter() {
      this(0);
   }

   private AndFileFilter(ArrayList<IOFileFilter> initialList) {
      this.fileFilters = Objects.requireNonNull(initialList, "initialList");
   }

   private AndFileFilter(int initialCapacity) {
      this(new ArrayList<>(initialCapacity));
   }

   public AndFileFilter(IOFileFilter filter1, IOFileFilter filter2) {
      this(2);
      this.addFileFilter(filter1);
      this.addFileFilter(filter2);
   }

   public AndFileFilter(IOFileFilter... fileFilters) {
      this(Objects.requireNonNull(fileFilters, "fileFilters").length);
      this.addFileFilter(fileFilters);
   }

   public AndFileFilter(List<IOFileFilter> fileFilters) {
      this(new ArrayList<>(Objects.requireNonNull(fileFilters, "fileFilters")));
   }

   @Override
   public boolean accept(File file) {
      if (this.isEmpty()) {
         return false;
      } else {
         for (IOFileFilter fileFilter : this.fileFilters) {
            if (!fileFilter.accept(file)) {
               return false;
            }
         }

         return true;
      }
   }

   @Override
   public boolean accept(File file, String name) {
      if (this.isEmpty()) {
         return false;
      } else {
         for (IOFileFilter fileFilter : this.fileFilters) {
            if (!fileFilter.accept(file, name)) {
               return false;
            }
         }

         return true;
      }
   }

   @Override
   public FileVisitResult accept(Path file, BasicFileAttributes attributes) {
      if (this.isEmpty()) {
         return FileVisitResult.TERMINATE;
      } else {
         for (IOFileFilter fileFilter : this.fileFilters) {
            if (fileFilter.accept(file, attributes) != FileVisitResult.CONTINUE) {
               return FileVisitResult.TERMINATE;
            }
         }

         return FileVisitResult.CONTINUE;
      }
   }

   @Override
   public void addFileFilter(IOFileFilter fileFilter) {
      this.fileFilters.add(Objects.requireNonNull(fileFilter, "fileFilter"));
   }

   public void addFileFilter(IOFileFilter... fileFilters) {
      for (IOFileFilter fileFilter : Objects.requireNonNull(fileFilters, "fileFilters")) {
         this.addFileFilter(fileFilter);
      }
   }

   @Override
   public List<IOFileFilter> getFileFilters() {
      return Collections.unmodifiableList(this.fileFilters);
   }

   private boolean isEmpty() {
      return this.fileFilters.isEmpty();
   }

   @Override
   public boolean removeFileFilter(IOFileFilter ioFileFilter) {
      return this.fileFilters.remove(ioFileFilter);
   }

   @Override
   public void setFileFilters(List<IOFileFilter> fileFilters) {
      this.fileFilters.clear();
      this.fileFilters.addAll(fileFilters);
   }

   @Override
   public String toString() {
      StringBuilder buffer = new StringBuilder();
      buffer.append(super.toString());
      buffer.append("(");

      for (int i = 0; i < this.fileFilters.size(); i++) {
         if (i > 0) {
            buffer.append(",");
         }

         buffer.append(this.fileFilters.get(i));
      }

      buffer.append(")");
      return buffer.toString();
   }
}
