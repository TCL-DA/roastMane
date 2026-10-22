package io.github.classgraph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import nonapi.io.github.classgraph.utils.LogNode;

public class MethodInfoList extends InfoList<MethodInfo> {
   private static final long serialVersionUID = 1L;
   static final MethodInfoList EMPTY_LIST = new MethodInfoList();

   public static MethodInfoList emptyList() {
      return EMPTY_LIST;
   }

   public MethodInfoList() {
   }

   public MethodInfoList(int sizeHint) {
      super(sizeHint);
   }

   public MethodInfoList(Collection<MethodInfo> methodInfoCollection) {
      super(methodInfoCollection);
   }

   protected void findReferencedClassInfo(Map<String, ClassInfo> classNameToClassInfo, Set<ClassInfo> refdClassInfo, LogNode log) {
      for (MethodInfo mi : this) {
         mi.findReferencedClassInfo(classNameToClassInfo, refdClassInfo, log);
      }
   }

   public Map<String, MethodInfoList> asMap() {
      Map<String, MethodInfoList> methodNameToMethodInfoList = new HashMap<>();

      for (MethodInfo methodInfo : this) {
         String name = methodInfo.getName();
         MethodInfoList methodInfoList = methodNameToMethodInfoList.get(name);
         if (methodInfoList == null) {
            methodInfoList = new MethodInfoList(1);
            methodNameToMethodInfoList.put(name, methodInfoList);
         }

         methodInfoList.add(methodInfo);
      }

      return methodNameToMethodInfoList;
   }

   public boolean containsName(String methodName) {
      for (MethodInfo mi : this) {
         if (mi.getName().equals(methodName)) {
            return true;
         }
      }

      return false;
   }

   public MethodInfoList get(String methodName) {
      boolean hasMethodWithName = false;

      for (MethodInfo mi : this) {
         if (mi.getName().equals(methodName)) {
            hasMethodWithName = true;
            break;
         }
      }

      if (!hasMethodWithName) {
         return EMPTY_LIST;
      } else {
         MethodInfoList matchingMethods = new MethodInfoList(2);

         for (MethodInfo mix : this) {
            if (mix.getName().equals(methodName)) {
               matchingMethods.add(mix);
            }
         }

         return matchingMethods;
      }
   }

   public MethodInfo getSingleMethod(String methodName) {
      int numMethodsWithName = 0;
      MethodInfo lastFoundMethod = null;

      for (MethodInfo mi : this) {
         if (mi.getName().equals(methodName)) {
            numMethodsWithName++;
            lastFoundMethod = mi;
         }
      }

      if (numMethodsWithName == 0) {
         return null;
      } else if (numMethodsWithName == 1) {
         return lastFoundMethod;
      } else {
         throw new IllegalArgumentException(
            "There are multiple methods named \"" + methodName + "\" in class " + ((MethodInfo)this.iterator().next()).getClassInfo().getName()
         );
      }
   }

   public MethodInfoList filter(MethodInfoList.MethodInfoFilter filter) {
      MethodInfoList methodInfoFiltered = new MethodInfoList();

      for (MethodInfo resource : this) {
         if (filter.accept(resource)) {
            methodInfoFiltered.add(resource);
         }
      }

      return methodInfoFiltered;
   }

   static {
      EMPTY_LIST.makeUnmodifiable();
   }

   @FunctionalInterface
   public interface MethodInfoFilter {
      boolean accept(MethodInfo var1);
   }
}
