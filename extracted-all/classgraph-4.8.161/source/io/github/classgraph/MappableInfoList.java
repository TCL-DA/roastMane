package io.github.classgraph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MappableInfoList<T extends HasName> extends InfoList<T> {
   private static final long serialVersionUID = 1L;

   MappableInfoList() {
   }

   MappableInfoList(int sizeHint) {
      super(sizeHint);
   }

   MappableInfoList(Collection<T> infoCollection) {
      super(infoCollection);
   }

   public Map<String, T> asMap() {
      Map<String, T> nameToInfoObject = new HashMap<>();

      for (T i : this) {
         if (i != null) {
            nameToInfoObject.put(i.getName(), i);
         }
      }

      return nameToInfoObject;
   }

   public boolean containsName(String name) {
      for (T i : this) {
         if (i != null && i.getName().equals(name)) {
            return true;
         }
      }

      return false;
   }

   public T get(String name) {
      for (T i : this) {
         if (i != null && i.getName().equals(name)) {
            return i;
         }
      }

      return null;
   }
}
