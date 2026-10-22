package io.github.classgraph;

import java.util.Collection;

public class ModuleInfoList extends MappableInfoList<ModuleInfo> {
   private static final long serialVersionUID = 1L;

   ModuleInfoList() {
   }

   ModuleInfoList(int sizeHint) {
      super(sizeHint);
   }

   ModuleInfoList(Collection<ModuleInfo> moduleInfoCollection) {
      super(moduleInfoCollection);
   }

   public ModuleInfoList filter(ModuleInfoList.ModuleInfoFilter filter) {
      ModuleInfoList moduleInfoFiltered = new ModuleInfoList();

      for (ModuleInfo resource : this) {
         if (filter.accept(resource)) {
            moduleInfoFiltered.add(resource);
         }
      }

      return moduleInfoFiltered;
   }

   @FunctionalInterface
   public interface ModuleInfoFilter {
      boolean accept(ModuleInfo var1);
   }
}
